#!/usr/bin/env python3
"""Read saved observations, validate them, compute statistics, export figures."""
import argparse
import csv
import hashlib
import json
import math
import re
import struct
from pathlib import Path

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import numpy as np
from scipy import stats

HERE = Path(__file__).resolve().parent
LAB = HERE
CONFIGS = ("rw_cache", "rw_nocache", "mmap_cache", "mmap_nocache")
LABELS = ("read/write: Cache", "read/write: --no-cache",
          "mmap: Cache", "mmap: --no-cache")
COLORS = ("#2563eb", "#d97706")


def read_series(directory):
    metadata = json.loads((directory / "metadata.json").read_text())
    with (directory / "measurements.csv").open(newline="") as stream:
        rows = list(csv.DictReader(stream))
    expected = metadata["N_per_config"]
    if len(rows) != expected * 4:
        raise ValueError("Unexpected number of observations")
    groups = {}
    for cfg in CONFIGS:
        g = sorted((r for r in rows if r["config"] == cfg),
                   key=lambda r: int(r["run"]))
        if [int(r["run"]) for r in g] != list(range(1, expected + 1)):
            raise ValueError(f"Missing or repeated run in {cfg}")
        for r in g:
            if int(r["exit_code"]) != 0 or float(r["wall_s"]) <= 0:
                raise ValueError(f"Failed or invalid observation: {cfg}")
            for field, value in (("core", metadata["core"]),
                                 ("iterations", metadata["iterations"]),
                                 ("graph_bytes", metadata["graph_bytes"])):
                if int(r[field]) != value:
                    raise ValueError(f"Inconsistent metadata: {field}")
            log = directory / f"{int(r['run']):02d}-{cfg}.log"
            if "OK (436905 nodes processed)" not in log.read_text():
                raise ValueError(f"Traversal not confirmed: {log}")
        groups[cfg] = g
    for run in range(1, expected + 1):
        positions = sorted(int(r["position"]) for r in rows
                           if int(r["run"]) == run)
        if positions != [1, 2, 3, 4]:
            raise ValueError("Invalid interleaving")
    return groups, metadata


def summarize(values):
    x = np.asarray(values, dtype=float)
    n = len(x)
    mean, sd = float(x.mean()), float(x.std(ddof=1))
    half = float(stats.t.ppf(.975, n - 1) * sd / np.sqrt(n))
    target = .1 * mean
    approximate = max(2, math.ceil((1.96 * sd / target) ** 2))
    refined = approximate
    while stats.t.ppf(.975, refined - 1) * sd / math.sqrt(refined) > target:
        refined += 1
    return {"N": n, "mean_s": mean, "sd_s": sd,
            "median_s": float(np.median(x)), "min_s": float(x.min()),
            "max_s": float(x.max()), "t_ci95_s": [mean-half, mean+half],
            "relative_halfwidth_pct": 100*half/mean,
            "N_10pct_initial": approximate, "N_10pct_t_refined": refined}


def verify_graph(path):
    data = path.read_bytes()
    magic, version, n, size, fanout, root, flags = struct.unpack_from("<8sIQIIQI", data)
    if magic != b"GCACHEG1" or size != 24 or fanout != 1 or len(data) != 40+n*size:
        raise ValueError("Unexpected graph format")
    seen = bytearray(n)
    current = root
    reached = forward = backward = short = 0
    minimum = math.ceil(4096 * 2 / size)
    digest = hashlib.sha256(data[:40])
    for i in range(n):
        digest.update(data[40+i*size+8:40+(i+1)*size])
    while True:
        if not 0 <= current < n or seen[current]:
            raise ValueError("Graph has invalid index or cycle")
        seen[current] = 1
        reached += 1
        degree = struct.unpack_from("<I", data, 40+current*size+8)[0]
        if degree == 0:
            break
        if degree != 1:
            raise ValueError("Graph is not a chain")
        child = struct.unpack_from("<Q", data, 40+current*size+16)[0]
        forward += child > current
        backward += child < current
        short += abs(child-current) < minimum
        current = child
    if reached != n:
        raise ValueError("Graph traversal does not visit all vertices")
    return {"bytes": len(data), "nodes": n, "record_bytes": size, "root": root,
            "reached": reached, "forward": forward, "backward": backward,
            "short_steps": short, "short_percent": 100*short/(n-1),
            "min_step_nodes": minimum, "structure_sha256": digest.hexdigest()}


def save_figure(fig, directory, name):
    fig.savefig(directory / f"{name}.pdf", bbox_inches="tight")
    plt.close(fig)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--input", type=Path, default=LAB / "results/main")
    parser.add_argument("--output", type=Path, default=HERE / "analysis")
    args = parser.parse_args()
    out = args.output.resolve()
    figures = out / "figures"
    figures.mkdir(parents=True, exist_ok=True)
    groups, metadata = read_series(args.input.resolve())
    values = {cfg: np.array([float(r["wall_s"]) for r in groups[cfg]])
              for cfg in CONFIGS}
    summaries = {cfg: summarize(values[cfg]) for cfg in CONFIGS}
    resource_fields = ("user_s", "sys_s", "vol_ctx", "invol_ctx", "minflt", "majflt")
    resources = {cfg: {field: float(np.mean([float(r[field]) for r in groups[cfg]]))
                      for field in resource_fields} for cfg in CONFIGS}

    means = {cfg: summaries[cfg]["mean_s"] for cfg in CONFIGS}
    ratios = {"rw_nocache_over_cache": means["rw_nocache"]/means["rw_cache"],
              "mmap_nocache_over_cache": means["mmap_nocache"]/means["mmap_cache"],
              "rw_over_mmap_cache": means["rw_cache"]/means["mmap_cache"],
              "rw_over_mmap_nocache": means["rw_nocache"]/means["mmap_nocache"]}
    result = {"metadata": metadata, "main": summaries, "resources": resources,
              "ratios": ratios,
              "graph": verify_graph(LAB / "graph-rand.bin"),
              "versions": {"numpy": np.__version__, "scipy": __import__('scipy').__version__,
                           "matplotlib": matplotlib.__version__},
              "source_csv_sha256": hashlib.sha256((args.input/"measurements.csv").read_bytes()).hexdigest()}
    pilot_dir = args.input.parent / "pilot"
    if pilot_dir.exists():
        pilot, _ = read_series(pilot_dir)
        result["pilot"] = {cfg: summarize([float(r['wall_s']) for r in pilot[cfg]])
                           for cfg in CONFIGS}
    (out / "summary.json").write_text(json.dumps(result, indent=2), encoding="utf-8")
    with (out / "summary.csv").open("w", newline="") as stream:
        writer = csv.writer(stream)
        writer.writerow(["config", "N", "mean_s", "sd_s", "median_s", "ci_low_s", "ci_high_s",
                         "halfwidth_pct", "N_target_10pct"])
        for cfg in CONFIGS:
            s = summaries[cfg]
            writer.writerow([cfg,s['N'],s['mean_s'],s['sd_s'],s['median_s'],
                             *s['t_ci95_s'],
                             s['relative_halfwidth_pct'],s['N_10pct_t_refined']])
    plt.rcParams.update({"font.family":"DejaVu Sans", "font.size":10,
                         "axes.spines.top":False, "axes.spines.right":False})
    fig, ax = plt.subplots(figsize=(9, 3.4))
    for i, cfg in enumerate(CONFIGS):
        s = summaries[cfg]
        lo, hi = s['t_ci95_s']
        ax.errorbar(s['mean_s'], i, xerr=[[s['mean_s']-lo],[hi-s['mean_s']]],
                    fmt='o', color=COLORS[i%2], capsize=5, markersize=6)
    ax.set_xscale('log'); ax.set_yticks(range(4), LABELS); ax.invert_yaxis()
    ax.set_xlabel('Полное время запуска, с (логарифмическая шкала)')
    ax.set_title('Средние времена и 95%-е t-интервалы, N = 30')
    ax.grid(axis='x', alpha=.25, which='both')
    save_figure(fig, figures, 'means')

    # Overlay distributions after explicit log10 transformation for the widely separated RW pair.
    for prefix, pair, scale in (('distribution_rw', CONFIGS[:2], 'log'),
                                ('distribution_mmap', CONFIGS[2:], 'milliseconds')):
        fig, ax = plt.subplots(figsize=(9, 3.5))
        transformed = [np.log10(values[c]) if scale=='log' else values[c]*1000 for c in pair]
        lo=min(x.min() for x in transformed); hi=max(x.max() for x in transformed)
        margin=(hi-lo)*.12
        grid=np.linspace(lo-margin,hi+margin,1200)
        for c, x, color in zip(pair, transformed, COLORS):
            ax.hist(x, bins=10, density=True, alpha=.25, color=color,
                    label=c, edgecolor=color)
            ax.plot(grid, stats.gaussian_kde(x)(grid), color=color, linewidth=1.8)
        ax.set_xlabel('log10(времени в секундах)' if scale=='log' else 'Время запуска, мс')
        ax.set_ylabel('Плотность log10(t / с)' if scale=='log' else 'Плотность, 1/мс')
        ax.set_title('Гистограммы и KDE; все 30 наблюдений сохранены')
        ax.legend(); ax.grid(alpha=.2)
        save_figure(fig, figures, prefix)
    fig, axes = plt.subplots(2,2,figsize=(9,5),sharex=True)
    for ax,cfg,color in zip(axes.flat,CONFIGS,(COLORS[0],COLORS[1],COLORS[0],COLORS[1])):
        ax.plot(np.arange(1,len(values[cfg])+1), values[cfg]/values[cfg].mean(),
                'o-',markersize=3,color=color)
        ax.axhline(1,color='gray',linestyle='--',linewidth=.8)
        ax.set_title(cfg); ax.set_ylabel('t / среднее'); ax.grid(alpha=.2)
    for ax in axes[-1]: ax.set_xlabel('Номер круга')
    fig.tight_layout(); save_figure(fig,figures,'runs')
    print(json.dumps({"main": summaries,"ratios":ratios},indent=2))
    print(f"Saved: {out}")


if __name__ == "__main__":
    main()
