#ifndef NOVAFRACTALNATIVE_LIBRARY_H
#define NOVAFRACTALNATIVE_LIBRARY_H

#define DLLEXPORT __attribute__((visibility("default")))

extern "C" {
    DLLEXPORT void CalculateNovaFractal(int* buffer, int width, int height, double zoom, double centerX, double centerY);
}

#endif