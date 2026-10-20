"""Statistical formulas and H2 generator for modeling lab 1, variant 11."""
from pathlib import Path

import numpy as np
from openpyxl import load_workbook

SIZES = (10, 20, 50, 100, 200, 300)
# Coefficients from the supplied course notes (including 1.643 for p=0.90).
CONFIDENCE = {0.90: 1.643, 0.95: 1.960, 0.99: 2.576}


def read_variant(path: Path, variant: int = 11):
    workbook = load_workbook(path, read_only=True, data_only=True)
    try:
        for sheet in workbook:
            headers = next(sheet.iter_rows(min_row=1, max_row=1))
            matches = [c for c in headers if c.value == variant]
            if not matches:
                continue
            cell = matches[0]
            values = [row[0] for row in sheet.iter_rows(
                min_row=2, max_row=301, min_col=cell.column,
                max_col=cell.column, values_only=True)]
            if any(isinstance(v, bool) or not isinstance(v, (int, float))
                   for v in values):
                raise ValueError("The variant must contain 300 numeric values")
            x = np.asarray(values, dtype=float)
            if len(x) != 300 or not np.isfinite(x).all():
                raise ValueError("Expected 300 finite measurements")
            return x, sheet.title, f"{cell.column_letter}2:{cell.column_letter}301"
    finally:
        workbook.close()
    raise ValueError(f"Variant {variant} not found")


def characteristics(x):
    n = len(x)
    mean = float(np.mean(x))
    variance = float(np.var(x, ddof=1))
    sigma = float(np.sqrt(variance))
    result = dict(mean=mean, variance=variance, sigma=sigma, cv=sigma / mean)
    for p, coefficient in CONFIDENCE.items():
        result[f"eps{round(100 * p)}"] = coefficient * sigma / np.sqrt(n)
    return result


def relative_deviation(value, reference):
    return 100 * abs(value - reference) / abs(reference) if reference != 0 else None


def autocorrelation(x, max_lag=10):
    return np.array([np.corrcoef(x[:-k], x[k:])[0, 1]
                     for k in range(1, max_lag + 1)])


def fit_h2(x):
    """Match the first two empirical RAW moments, not the corrected variance."""
    mean = float(np.mean(x))
    raw_second = float(np.mean(x * x))
    variance = raw_second - mean * mean
    cv2 = variance / mean**2
    if mean <= 0 or cv2 <= 1 or np.any(x < 0):
        raise ValueError("H2 requires nonnegative data, positive mean, CV > 1")
    q = float((1 + np.sqrt((cv2 - 1) / (cv2 + 1))) / 2)
    return dict(mean=mean, raw_second=raw_second, variance=variance,
                cv=float(np.sqrt(cv2)), q=q,
                lambda1=2 * q / mean, lambda2=2 * (1 - q) / mean)


def generate_h2(size, parameters, seed=11):
    rng = np.random.Generator(np.random.PCG64(seed))
    uniform = rng.random((size, 2))
    rate = np.where(uniform[:, 0] < parameters["q"],
                    parameters["lambda1"], parameters["lambda2"])
    # 1-U is in (0, 1]; log1p avoids loss of accuracy near zero.
    return -np.log1p(-uniform[:, 1]) / rate


def h2_pdf(x, p):
    x = np.asarray(x)
    t = np.maximum(x, 0)
    density = (p["q"] * p["lambda1"] * np.exp(-p["lambda1"] * t)
               + (1 - p["q"]) * p["lambda2"] * np.exp(-p["lambda2"] * t))
    return np.where(x >= 0, density, 0)


def h2_cdf(x, p):
    x = np.asarray(x)
    t = np.maximum(x, 0)
    return (p["q"] * -np.expm1(-p["lambda1"] * t)
            + (1 - p["q"]) * -np.expm1(-p["lambda2"] * t))
