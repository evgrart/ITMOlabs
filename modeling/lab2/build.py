"""Recalculate lab 2 and compile its PDF; leave detailed logs in build/."""
from pathlib import Path
import shutil
import subprocess
import sys

ROOT = Path(__file__).resolve().parent
BUILD = ROOT / "build"
NAME = "report_variant11_12_09"


def run(command, log_name):
    completed = subprocess.run(command, cwd=ROOT, stdout=subprocess.PIPE,
                               stderr=subprocess.STDOUT, encoding="utf-8",
                               errors="replace")
    log = BUILD / log_name
    log.write_text(completed.stdout, encoding="utf-8")
    if completed.returncode:
        print(completed.stdout[-7000:])
        raise SystemExit(f"Command failed; see {log}")


def main():
    BUILD.mkdir(exist_ok=True)
    latex = shutil.which("pdflatex")
    if latex is None:
        raise SystemExit("Install MiKTeX or TeX Live and add pdflatex to PATH.")
    run([sys.executable, "-B", "-X", "utf8", "analyze.py"], "analysis.log")
    print("Calculations, figures and independent checks: OK")
    for stage in (1, 2):
        run([latex, "-interaction=nonstopmode", "-halt-on-error",
             f"-output-directory={BUILD}", f"{NAME}.tex"], f"latex{stage}.log")
    shutil.copy2(BUILD / f"{NAME}.pdf", ROOT / f"{NAME}.pdf")
    print(f"PDF: {ROOT / (NAME + '.pdf')}")


if __name__ == "__main__":
    main()
