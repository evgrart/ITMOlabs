import csv
import json
import os
import resource
import shlex
import subprocess
import sys
import time
from datetime import datetime
from pathlib import Path

ROOT = Path(__file__).resolve().parent
GRAPH = ROOT / "graph-rand.bin"
CORE = 2
ITER = 1

if len(sys.argv) != 3:
    raise SystemExit("Запуск: python3 collect.py N results/имя-серии")

N = int(sys.argv[1])
if N < 2:
    raise SystemExit("Нужно минимум два повторения")

OUT = Path(sys.argv[2]).resolve()
OUT.mkdir(parents=True, exist_ok=False)

configs = [
    ("rw_cache", "graph_traverse", False),
    ("rw_nocache", "graph_traverse", True),
    ("mmap_cache", "graph_traverse_mmap", False),
    ("mmap_nocache", "graph_traverse_mmap", True),
]

metadata = {
    "N_per_config": N,
    "core": CORE,
    "iterations": ITER,
    "graph": str(GRAPH),
    "graph_bytes": GRAPH.stat().st_size,
    "kernel": os.uname().release,
    "governor": Path(
        f"/sys/devices/system/cpu/cpu{CORE}/cpufreq/scaling_governor"
    ).read_text().strip(),
    "cache_policy": "fsync, then full buffered read before every run",
    "order": "cyclic rotation of four configurations",
    "discarded_warmups": 0,
    "outliers": "keep all observations",
}
(OUT / "metadata.json").write_text(
    json.dumps(metadata, indent=2), encoding="utf-8"
)

fields = [
    "timestamp", "run", "position", "config", "core", "iterations",
    "graph", "graph_bytes", "command", "wall_s", "user_s", "sys_s",
    "vol_ctx", "invol_ctx", "minflt", "majflt",
    "inputs", "outputs", "exit_code",
]
usage_fields = [
    "ru_utime", "ru_stime", "ru_nvcsw", "ru_nivcsw",
    "ru_minflt", "ru_majflt", "ru_inblock", "ru_oublock",
]

with (OUT / "measurements.csv").open("w", newline="") as table:
    writer = csv.writer(table)
    writer.writerow(fields)
    table.flush()

    for r in range(N):
        shift = r % len(configs)
        order = configs[shift:] + configs[:shift]

        for position, (label, executable, no_cache) in enumerate(order, 1):
            # Подготовка файла выполняется ДО начала измерения.
            with GRAPH.open("r+b") as graph:
                os.fsync(graph.fileno())
                while graph.read(1024 * 1024):
                    pass

            command = [
                "taskset", "-c", str(CORE),
                str(ROOT / "out" / executable), "--write",
            ]
            if no_cache:
                command.append("--no-cache")
            command += [str(ITER), str(GRAPH)]

            print(f"{r + 1}/{N}: {label} — запуск", flush=True)
            timestamp = datetime.now().astimezone().isoformat()

            with (OUT / f"{r + 1:02d}-{label}.log").open("w") as log:
                before = resource.getrusage(resource.RUSAGE_CHILDREN)
                start = time.perf_counter_ns()
                result = subprocess.run(
                    command, stdout=log, stderr=subprocess.STDOUT
                )
                elapsed = (time.perf_counter_ns() - start) / 1e9
                after = resource.getrusage(resource.RUSAGE_CHILDREN)

            usage = [
                getattr(after, key) - getattr(before, key)
                for key in usage_fields
            ]
            writer.writerow([
                timestamp, r + 1, position, label, CORE, ITER,
                str(GRAPH), GRAPH.stat().st_size,
                shlex.join(command), f"{elapsed:.9f}",
                *usage, result.returncode,
            ])
            table.flush()
            print(f"  время: {elapsed:.6f} с", flush=True)

            if result.returncode:
                raise SystemExit(f"Ошибка запуска: смотри журнал {label}")

print(f"Готово: {OUT / 'measurements.csv'}")
