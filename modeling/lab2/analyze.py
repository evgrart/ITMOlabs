"""Reproducible CTMC calculation for lab 2, variant 11/12/09."""
from __future__ import annotations

import csv
import itertools
import json
from pathlib import Path

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.patches import FancyArrowPatch, Circle, Rectangle
import numpy as np
from scipy.linalg import expm

ROOT = Path(__file__).resolve().parent
OUT = ROOT / "results"
OUT.mkdir(exist_ok=True)
LAMBDA, B, CV, Q = 0.4, 10.0, 2.0, 0.1
PHASE_B = B * np.array([
    1 + np.sqrt((CV**2 - 1) * (1 - Q) / (2 * Q)),
    1 - np.sqrt((CV**2 - 1) * Q / (2 * (1 - Q))),
])
PHASE_MU = 1 / PHASE_B
PROBS = np.array([Q, 1 - Q])
plt.rcParams.update({"font.family": "DejaVu Sans", "font.size": 10,
                     "axes.spines.top": False, "axes.spines.right": False,
                     "savefig.bbox": "tight"})


def stationary(g):
    a = g.T.copy()
    a[-1] = 1
    rhs = np.zeros(len(g))
    rhs[-1] = 1
    return np.linalg.solve(a, rhs)


def local_model(kind, capacity, arrival):
    """Local state (number including service, phase); -1 means idle."""
    states = [(0, -1)] + [(n, phase) for n in range(1, capacity + 2)
                          for phase in range(2 if kind == "H" else 1)]
    idx = {state: i for i, state in enumerate(states)}
    g = np.zeros((len(states), len(states)))
    for i, (n, phase) in enumerate(states):
        if n == 0:
            for ph, prob in enumerate(PROBS if kind == "H" else [1]):
                g[i, idx[(1, ph)]] += arrival * prob
        else:
            mu = PHASE_MU[phase] if kind == "H" else 1 / B
            if n < capacity + 1:
                g[i, idx[(n + 1, phase)]] += arrival
            if n == 1:
                g[i, 0] += mu
            else:
                # The queued customer's phase is chosen when service starts.
                for ph, prob in enumerate(PROBS if kind == "H" else [1]):
                    g[i, idx[(n - 1, ph)]] += mu * prob
        g[i, i] = -g[i].sum()
    return states, g


def model(number, arrival=LAMBDA):
    spec = [("M", 2, 0.6), ("M", 2, 0.4)] if number == 1 else [
        ("H", 1, 0.6), ("M", 0, 0.1), ("M", 0, 0.3)]
    local = [local_model(kind, cap, arrival * route) for kind, cap, route in spec]
    states = list(itertools.product(*(range(len(st)) for st, _ in local)))
    index = {state: i for i, state in enumerate(states)}
    g = np.zeros((len(states), len(states)))
    # Explicitly build transitions, independently of the Kronecker construction.
    for row, state in enumerate(states):
        for device, (st, h) in enumerate(local):
            for dest in range(len(st)):
                if dest != state[device] and h[state[device], dest] > 0:
                    target = list(state)
                    target[device] = dest
                    g[row, index[tuple(target)]] += h[state[device], dest]
        g[row, row] = -g[row].sum()
    p = stationary(g)
    metrics = []
    for device, (kind, cap, route) in enumerate(spec):
        populations = np.array([local[device][0][s[device]][0] for s in states])
        phases = np.array([local[device][0][s[device]][1] for s in states])
        rates = np.array([0 if n == 0 else PHASE_MU[ph] if kind == "H"
                          else 1 / B for n, ph in zip(populations, phases)])
        accepted = populations < cap + 1
        # Direct arrival-state conditioning; not Little's law.
        wait = np.array([0 if n == 0 else
                         (PHASE_B[ph] + (n - 1) * B if kind == "H" else n * B)
                         for n, ph in zip(populations, phases)])
        acceptance = float(p @ accepted)
        metrics.append({
            "load": arrival * route * B,
            "busy": float(p @ (populations > 0)),
            "queue": float(p @ np.maximum(populations - 1, 0)),
            "number": float(p @ populations),
            "wait": float(p @ (accepted * wait) / acceptance),
            "sojourn": float(p @ (accepted * (wait + B)) / acceptance),
            "loss": float(p @ (~accepted)),
            "throughput": float(p @ rates),
        })
    total = {key: sum(m[key] for m in metrics)
             for key in ("load", "busy", "queue", "number", "throughput")}
    total["loss"] = sum(s[2] * m["loss"] for s, m in zip(spec, metrics))
    for key in ("wait", "sojourn"):
        total[key] = sum(m["throughput"] * m[key] for m in metrics) / total["throughput"]
    total["utilization"] = total["busy"] / len(spec)
    checks = {
        "stationary_residual": float(np.max(np.abs(p @ g))),
        "row_sum_residual": float(np.max(np.abs(g.sum(axis=1)))),
        "normalization_residual": float(abs(p.sum() - 1)),
        "min_probability": float(p.min()),
    }
    product = np.array([np.prod([stationary(h)[s[d]] for d, (_, h) in enumerate(local)])
                        for s in states])
    checks["product_solution_residual"] = float(np.max(abs(p - product)))
    kron = np.zeros_like(g)
    for device, (_, h) in enumerate(local):
        term = np.array([[1.0]])
        for d, (st, _) in enumerate(local):
            term = np.kron(term, h if d == device else np.eye(len(st)))
        kron += term
    checks["kronecker_residual"] = float(np.max(abs(kron - g)))
    checks["flow_residual"] = float(abs(total["throughput"] - arrival * (1 - total["loss"])))
    checks["little_queue_residual"] = abs(total["queue"] - total["throughput"] * total["wait"])
    checks["little_number_residual"] = abs(total["number"] - total["throughput"] * total["sojourn"])
    for device, m in enumerate(metrics):
        assert abs(m["throughput"] - arrival * spec[device][2] * (1 - m["loss"])) < 1e-11
        assert abs(m["busy"] - B * m["throughput"]) < 1e-11
        assert abs(m["queue"] - m["throughput"] * m["wait"]) < 1e-11
    assert p.min() >= 0
    assert max(v for k, v in checks.items() if k != "min_probability") < 1e-11
    return {"states": states, "g": g, "p": p, "spec": spec, "local": local,
            "metrics": metrics, "total": total, "checks": checks}


def fmt(x, digits=6):
    return f"{x:.{digits}f}".replace(".", "{,}")


def code(number, state):
    if number == 1:
        return f"({state[0]},{state[1]})"
    local_codes = ["0", "1a", "1b", "2a", "2b"]
    return f"({local_codes[state[0]]},{state[1]},{state[2]})"


def savefig(fig, name):
    fig.savefig(OUT / f"{name}.pdf")
    fig.savefig(OUT / f"{name}.png", dpi=160)
    plt.close(fig)


def graph_one():
    fig, ax = plt.subplots(figsize=(9, 7))
    radius = 0.27
    for n1, n2 in itertools.product(range(4), repeat=2):
        x, y = n1 * 2, n2 * 2
        ax.add_patch(Circle((x, y), radius, facecolor="#edf4fa", edgecolor="#254c70"))
        ax.text(x, y, f"$E_{{{4*n1+n2}}}$", ha="center", va="center")
        if n1 < 3:
            for off, start, end, label in [(0.09, x+radius, x+2-radius, "0,24"),
                                           (-0.09, x+2-radius, x+radius, "0,10")]:
                ax.add_patch(FancyArrowPatch((start,y+off),(end,y+off),arrowstyle="->",mutation_scale=9,color="#286599"))
                ax.text(x+1,y+(0.27 if off>0 else -0.32),label,ha="center",fontsize=8)
        if n2 < 3:
            for off, start, end, label in [(-0.09,y+radius,y+2-radius,"0,16"),
                                           (0.09,y+2-radius,y+radius,"0,10")]:
                ax.add_patch(FancyArrowPatch((x+off,start),(x+off,end),arrowstyle="->",mutation_scale=9,color="#b15b25"))
                ax.text(x+(-0.25 if off<0 else 0.26),y+1,label,ha="center",rotation=90,fontsize=8)
    ax.set(xlim=(-0.65,6.65),ylim=(-0.65,6.65),aspect="equal")
    ax.axis("off")
    savefig(fig,"01_graph_system1")


def graph_two(m):
    fig, ax = plt.subplots(figsize=(12, 8))
    coordinates = {0:(0,0),1:(2,1.4),2:(2,-1.4),3:(4,1.4),4:(4,-1.4)}
    # Four layers correspond to (n2,n3); identical H transitions in each layer.
    layers = [(0,0),(0,1),(1,1),(1,0)]
    centers = [(0,0),(7,0),(7,-5),(0,-5)]
    pos = {}
    for (n2,n3),(cx,cy) in zip(layers,centers):
        for h,(x,y) in coordinates.items():
            pos[(h,n2,n3)]=(cx+x,cy+y)
        ax.text(cx+2,cy+2.3,f"$(n_2,n_3)=({n2},{n3})$",ha="center",fontsize=12)
    colors=["#344a64","#277c40","#b56623"]
    for i,state in enumerate(m["states"]):
        start=np.array(pos[state],dtype=float)
        for j,rate in enumerate(m["g"][i]):
            if i==j or rate<=0: continue
            target=m["states"][j]
            device=next(d for d in range(3) if state[d]!=target[d])
            end=np.array(pos[target],dtype=float)
            if device==0:
                rad=0.13
                ax.add_patch(FancyArrowPatch(start,end,connectionstyle=f"arc3,rad={rad}",
                    shrinkA=14,shrinkB=14,arrowstyle="->",mutation_scale=8,linewidth=.75,color=colors[device]))
            else:
                # Curved interlayer edges show both directions for every H state.
                rad=0.08+0.012*state[0]
                ax.add_patch(FancyArrowPatch(start,end,connectionstyle=f"arc3,rad={rad}",
                    shrinkA=14,shrinkB=14,arrowstyle="->",mutation_scale=7,linewidth=.65,
                    alpha=.65,color=colors[device]))
        ax.add_patch(Circle(start,.30,facecolor="white",edgecolor="#254c70",zorder=4))
        ax.text(*start,f"$E_{{{i}}}$",ha="center",va="center",fontsize=9,zorder=5)
    ax.set(xlim=(-.8,12),ylim=(-7.2,3),aspect="equal")
    ax.axis("off")
    savefig(fig,"02_graph_system2")
    # A separate labelled factor graph makes every edge rate unambiguous.
    fig,ax=plt.subplots(figsize=(10,4.6))
    transitions=[(0,1,r"$\lambda_1q$"),(0,2,r"$\lambda_1(1-q)$"),
                 (1,0,r"$\mu_a$"),(2,0,r"$\mu_b$"),
                 (1,3,r"$\lambda_1$"),(2,4,r"$\lambda_1$"),
                 (3,1,r"$\mu_aq$"),(3,2,r"$\mu_a(1-q)$"),
                 (4,1,r"$\mu_bq$"),(4,2,r"$\mu_b(1-q)$")]
    for a,b,label in transitions:
        start=np.array(coordinates[a],float);end=np.array(coordinates[b],float)
        rad=.20 if (a,b) not in [(3,2),(4,1)] else .05
        ax.add_patch(FancyArrowPatch(start,end,connectionstyle=f"arc3,rad={rad}",
                    shrinkA=20,shrinkB=20,arrowstyle="->",mutation_scale=11,color="#344a64"))
        delta=end-start
        fraction = .32 if (a,b) in [(3,2),(4,1)] else .5
        mid=start+fraction*delta + np.array([delta[1],-delta[0]])*rad*.55
        ax.text(*mid,label,fontsize=10,ha="center",va="center",bbox={"facecolor":"white","edgecolor":"none","pad":1})
    for i,label in enumerate(["0","1a","1b","2a","2b"]):
        ax.add_patch(Circle(coordinates[i],.22,facecolor="#edf4fa",edgecolor="#254c70",zorder=3))
        ax.text(*coordinates[i],label,ha="center",va="center",zorder=4)
    ax.set(xlim=(-.6,4.8),ylim=(-2.1,2.1),aspect="equal");ax.axis("off")
    savefig(fig,"03_hyper_factor")


def export(models):
    rows = [r"\begin{longtable}{rcrcr}",r"\caption{Форма 1. Стационарные вероятности состояний}\label{tab:prob}\\",
            r"\hline № & Система 1: код & $p_i^{(1)}$ & Система 2: код & $p_i^{(2)}$ \\ \hline\endfirsthead",
            r"\hline № & Система 1: код & $p_i^{(1)}$ & Система 2: код & $p_i^{(2)}$ \\ \hline\endhead"]
    for i in range(20):
        cells=[str(i)]
        for num,m in enumerate(models,1):
            cells.extend([f"${code(num,m['states'][i])}$",fmt(m["p"][i],8)] if i<len(m["states"]) else ["---","---"])
        rows.append(" & ".join(cells)+r" \\")
    rows.extend([r"\hline $\sum p_i$ & & $1$ & & $1$ \\ \hline",r"\end{longtable}"])
    (OUT/"probabilities.tex").write_text("\n".join(rows),encoding="utf-8")
    titles = {
        "load":("Нагрузка $y_i$, Эрл",r"$\lambda r_i b$"),
        "busy":("Загрузка $\rho_i$",r"$\sum_{s:n_i>0}p_s$"),
        "queue":("Длина очереди $l_i$",r"$\sum_s(n_i-1)^+p_s$"),
        "number":("Число заявок $m_i$",r"$\sum_s n_i p_s$"),
        "wait":("Ожидание $w_i$, с",r"формула~\eqref{eq:wait}"),
        "sojourn":("Пребывание $u_i$, с",r"формула~\eqref{eq:sojourn}"),
        "loss":(r"Вероятность потери $\pi_i$",r"$\sum_{s:n_i=K_i}p_s$"),
        "throughput":("Производительность $a_i$, 1/с",r"$\sum_{s:n_i>0}p_s\mu_{i,h_i}$"),
    }
    rows=[r"\begin{longtable}{p{3.6cm}cp{4.2cm}rr}",r"\caption{Форма 2. Характеристики систем}\label{tab:metrics}\\",
          r"\hline Характеристика & Прибор & Расчётная формула & Сист. 1 & Сист. 2 \\ \hline\endfirsthead",
          r"\hline Характеристика & Прибор & Расчётная формула & Сист. 1 & Сист. 2 \\ \hline\endhead"]
    for key,(title,formula) in titles.items():
        for device in range(3):
            cells=[title if device==0 else "",f"П{device+1}",formula]
            for m in models:
                cells.append(fmt(m["metrics"][device][key]) if device<len(m["metrics"]) else "---")
            rows.append(" & ".join(cells)+r" \\")
        total_formula = r"$\sum_i x_i$" if key in ("load","busy","queue","number","throughput") else (
            r"$\sum_i r_i\pi_i$" if key=="loss" else r"$\sum_i a_i x_i/\sum_i a_i$")
        rows.append(" & ".join(["","Система",total_formula]+[fmt(m["total"][key]) for m in models])+r" \\ \hline")
    rows.append(r"\end{longtable}")
    (OUT/"characteristics.tex").write_text("\n".join(rows),encoding="utf-8")
    for num,m in enumerate(models,1):
        np.savetxt(OUT/f"system{num}_generator.csv",m["g"],delimiter=";",fmt="%.12g")
        np.savetxt(OUT/f"system{num}_mark_offdiagonal.txt",m["g"]-np.diag(np.diag(m["g"])),fmt="%.12g",delimiter="\t")
        np.savetxt(OUT/f"system{num}_generator.txt",m["g"],fmt="%.12g",delimiter="\t")
        with (OUT/f"system{num}_states.csv").open("w",newline="",encoding="utf-8-sig") as f:
            writer=csv.writer(f,delimiter=";");writer.writerow(["state","code","probability"])
            writer.writerows((i,code(num,s),f"{m['p'][i]:.12g}") for i,s in enumerate(m["states"]))
        matrix=[r"\begingroup\setlength{\arraycolsep}{2.1pt}",r"\resizebox{\textwidth}{!}{$G_{"+str(num)+r"}=\begin{pmatrix}"]
        for row in m["g"]:
            matrix.append(" & ".join("0" if abs(x)<1e-14 else fmt(x,5) for x in row)+r" \\")
        matrix.extend([r"\end{pmatrix}\quad\text{с}^{-1}$}",r"\endgroup"])
        (OUT/f"matrix{num}.tex").write_text("\n".join(matrix),encoding="utf-8")
    summary={f"system{num}":{"total":m["total"],"devices":m["metrics"],"checks":m["checks"]}
             for num,m in enumerate(models,1)}
    summary["hyperexponential"]={"q":Q,"mean":B,"cv":CV,"phase_means":PHASE_B.tolist(),"phase_rates":PHASE_MU.tolist()}
    (OUT/"summary.json").write_text(json.dumps(summary,indent=2,ensure_ascii=False),encoding="utf-8")
    macros=[]
    for num,m in enumerate(models,1):
        suffix = "One" if num==1 else "Two"
        for key,name in [("throughput","Throughput"),("loss","Loss"),("busy","Busy"),
                         ("utilization","Utilization"),("queue","Queue"),("number","Number"),
                         ("wait","Wait"),("sojourn","Sojourn")]:
            macros.append(f"\\newcommand{{\\{name}{suffix}}}{{{fmt(m['total'][key])}}}")
    for name,value in [("PhaseMeanA",PHASE_B[0]),("PhaseMeanB",PHASE_B[1]),
                       ("PhaseRateA",PHASE_MU[0]),("PhaseRateB",PHASE_MU[1]),
                       ("ThroughputGain",100*(models[0]["total"]["throughput"]/models[1]["total"]["throughput"]-1)),
                       ("ThroughputDelta",models[0]["total"]["throughput"]-models[1]["total"]["throughput"]),
                       ("LossDelta",100*(models[1]["total"]["loss"]-models[0]["total"]["loss"]))]:
        macros.append(f"\\newcommand{{\\{name}}}{{{fmt(value)}}}")
    (OUT/"numbers.tex").write_text("\n".join(macros),encoding="utf-8")
    with (OUT/"characteristics.csv").open("w",newline="",encoding="utf-8-sig") as f:
        writer=csv.writer(f,delimiter=";");writer.writerow(["system","device",*titles])
        for num,m in enumerate(models,1):
            for label,metrics in [(str(i+1),v) for i,v in enumerate(m["metrics"])]+[("total",m["total"])]:
                writer.writerow([num,label,*[metrics[key] for key in titles]])


def comparisons(models):
    grid=np.linspace(.01,1.0,100)
    curves={num:[model(num,x)["total"] for x in grid] for num in (1,2)}
    with (OUT/"lambda_sweep.csv").open("w",newline="",encoding="utf-8-sig") as f:
        writer=csv.writer(f,delimiter=";");writer.writerow(["lambda","system","throughput","loss","wait","sojourn"])
        for idx,x in enumerate(grid):
            for num in (1,2): writer.writerow([x,num,*[curves[num][idx][k] for k in ("throughput","loss","wait","sojourn")]])
    fig,axes=plt.subplots(2,2,figsize=(10,7.4))
    for ax,(key,title) in zip(axes.flat,[("throughput","Производительность, 1/с"),("loss","Вероятность потери"),
                                      ("queue","Средняя длина очередей"),("sojourn","Время пребывания, с")]):
        for num,color in [(1,"#286599"),(2,"#c76a2c")]:
            ax.plot(grid,[v[key] for v in curves[num]],label=f"Система {num}",color=color,lw=2)
            ax.plot(LAMBDA,models[num-1]["total"][key],"o",color=color)
        ax.axvline(LAMBDA,color="gray",lw=.8,ls="--")
        ax.set(xlabel="Интенсивность входного потока λ, 1/с",ylabel=title)
        ax.grid(alpha=.25);ax.legend()
    fig.tight_layout();savefig(fig,"04_comparison")
    # Transient solution is an independent check of the stationary solution.
    fig,ax=plt.subplots(figsize=(9,4.2))
    times=np.linspace(0,1200,80)
    for num,m in enumerate(models,1):
        distances=[]
        for t in times:
            transient=expm(m["g"]*t)[0]
            distances.append(.5*np.abs(transient-m["p"]).sum())
        assert distances[-1]<1e-8
        ax.semilogy(times,np.maximum(distances,1e-16),label=f"Система {num}")
        m["checks"]["transient_distance_1200s"]=float(distances[-1])
    ax.set(xlabel="Время от пустого состояния, с",ylabel="Расстояние до стационарного режима")
    ax.grid(alpha=.25);ax.legend();fig.tight_layout();savefig(fig,"05_convergence")


def structures():
    fig,axes=plt.subplots(2,1,figsize=(10,6.4))
    for num,ax in enumerate(axes,1):
        routes=[.6,.4] if num==1 else [.6,.1,.3]
        cap=[2,2] if num==1 else [1,0,0]
        ys=np.linspace(1,-1,len(routes))
        ax.annotate("",xy=(1,0),xytext=(-.2,0),arrowprops={"arrowstyle":"->"})
        ax.text(-.35,.2,"λ = 0,4 1/с",fontsize=10)
        for i,(y,r,c) in enumerate(zip(ys,routes,cap),1):
            ax.plot([1,1,2.1],[0,y,y],color="#344a64")
            ax.text(1.2,y+.12,f"r{i} = {r:g}".replace(".",","),fontsize=10)
            ax.add_patch(Rectangle((2.1,y-.24),1.7,.48,facecolor="#edf4fa",edgecolor="#254c70"))
            ax.text(2.95,y,f"Очередь: {c}",ha="center",va="center")
            ax.annotate("",xy=(4.35,y),xytext=(3.8,y),arrowprops={"arrowstyle":"->"})
            ax.add_patch(Rectangle((4.35,y-.24),1.7,.48,facecolor="#f8efe6",edgecolor="#b56623"))
            kind="H (ν = 2)" if num==2 and i==1 else "M"
            ax.text(5.2,y,f"П{i}: {kind}",ha="center",va="center")
            ax.annotate("",xy=(7,y),xytext=(6.05,y),arrowprops={"arrowstyle":"->"})
        ax.set_title(f"Система {num}: b = 10 с для каждого прибора",loc="left",fontsize=12)
        ax.set(xlim=(-.4,7.1),ylim=(-1.5,1.5));ax.axis("off")
    fig.tight_layout();savefig(fig,"06_structures")


def main():
    mean=float(PROBS@PHASE_B)
    second=float(2*PROBS@(PHASE_B**2))
    assert abs(mean-B)<1e-12 and abs(np.sqrt(second-mean**2)/mean-CV)<1e-12
    models=[model(1),model(2)]
    assert [len(m["states"]) for m in models]==[16,20]
    graph_one();graph_two(models[1]);structures();comparisons(models);export(models)
    print(json.dumps({f"system{i}":m["total"] for i,m in enumerate(models,1)},indent=2))
    print("All balance, moment, product-form and transient checks passed.")


if __name__=="__main__":
    main()
