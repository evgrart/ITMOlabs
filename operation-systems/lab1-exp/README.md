# Лабораторная работа 1: эксперимент

Вариант: **Write, Cache/NoCache, 10M, Rand**.

## Файлы

- `report.tex`, `preambula_labs.tex`, `analysis/figures/` — отчёт для pdfLaTeX.
- `src/`, `graphgen.py` — исходники программ и генератор из репозитория курса.
- `generate.sh`, `collect.py`, `analyze.py` — генерация, сбор и обработка.
- `results/` — пилотные, основные и диагностические измерения.
- `analysis/summary.csv`, `analysis/summary.json` — сводки результатов.

## Воспроизведение

Команды выполняются из `operation-systems/lab1-exp` на Linux:

```bash
mkdir -p out
gcc -o out/graph_traverse src/graph_traverse.c
gcc -o out/graph_traverse_mmap src/graph_traverse_mmap.c
bash generate.sh
```

Генерация создаёт графы размером 10 МиБ с seed 427. Измеряется
`graph-rand.bin`; бинарные файлы не включены в Git. Существующие графы
скрипт не перезаписывает.

Для нового сбора выберите доступный CPU в `collect.py` (исходно CPU 2).
Перед исходными измерениями использовался governor `performance`.
Каталоги новых результатов должны отсутствовать:

```bash
python3 collect.py 6 results/pilot-new
python3 collect.py 30 results/main-new
```

Обработка сохранённой основной серии и воспроизведение рисунков:

```bash
python3 -m pip install -r requirements.txt
python3 analyze.py
```

Анализ проверяет граф и журналы, считает среднее, стандартное отклонение,
медиану и 95%-й интервал Стьюдента. Сохранённые CSV и журналы не изменяются.
Абсолютные пути в них относятся к исходному расположению эксперимента.
Текст отчёта описывает исходные измерения и не обновляется автоматически.

Для Overleaf загрузите `report.tex`, `preambula_labs.tex` и папку
`analysis/figures/`; основной файл — `report.tex`, компилятор — pdfLaTeX.
