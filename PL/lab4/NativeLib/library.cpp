#include "library.h"
#include <complex>
#include <cmath>
#include <algorithm>

using Complex = std::complex<double>;
const int MAX_ITER = 50;

int GetColor(int iterations, int max_iter) {
    if (iterations == max_iter) return 0xFF000000; 
    int t = (iterations * 255) / max_iter;
    int r = t;
    int g = (t * 5) % 255;
    int b = 255 - t;
    return (255 << 24) | (r << 16) | (g << 8) | b;
}

extern "C" void CalculateNovaFractal(int* buffer, int width, int height, double zoom, double centerX, double centerY) {
    double aspectRatio = (double)width / height;

    #pragma omp parallel for
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            double re = centerX + (x - width / 2.0) / (0.5 * zoom * width) * aspectRatio;
            double im = centerY + (y - height / 2.0) / (0.5 * zoom * height);

            Complex c(re, im);
            Complex z = 1.0;
            
            int iter = 0;
            for (; iter < MAX_ITER; iter++) {
                if (std::abs(z) < 1e-6) break;
                
                Complex z2 = z * z;
                Complex z3 = z2 * z;
                Complex delta = (z3 - 1.0) / (3.0 * z2);
                z = z - delta + c;

                if (std::abs(z) > 10.0) break;
            }
            buffer[y * width + x] = GetColor(iter, MAX_ITER);
        }
    }
}