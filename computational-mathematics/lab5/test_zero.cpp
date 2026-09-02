#include <iostream>
#include <cmath>
using namespace std;

double ode_variant_2(double x, double y) {
    return x + y * y;
}

int main() {
    double x = 0.0, y = 0.0, h = 0.1;
    for (int i = 0; i < 3; i++) {
        double y_temp = y + h * ode_variant_2(x, y);
        y = y + (h / 2.0) * (ode_variant_2(x, y) + ode_variant_2(x + h, y_temp));
        x += h;
        cout << "x=" << x << ", y=" << y << "\n";
    }
    return 0;
}
