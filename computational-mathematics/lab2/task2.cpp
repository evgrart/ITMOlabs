#include <bits/stdc++.h>
#include "matplotlibcpp.h"

namespace plt = matplotlibcpp;
using namespace std;

struct System {
    string description;
    double (*f1)(double, double);
    double (*f2)(double, double);
    double (*df1dx)(double, double);
    double (*df1dy)(double, double);
    double (*df2dx)(double, double);
    double (*df2dy)(double, double);
};

double f1_sys1(double x, double y) { return tan(x * y + 0.1) - x * x; }

double f2_sys1(double x, double y) { return x * x + 2 * y * y - 1; }

double df1dx_sys1(double x, double y) { 
    double denom = cos(x * y + 0.1); 
    return y / (denom * denom) - 2 * x; 
}

double df1dy_sys1(double x, double y) { 
    double denom = cos(x * y + 0.1); 
    return x / (denom * denom); 
}

double df2dx_sys1(double x, double y) { return 2 * x; }

double df2dy_sys1(double x, double y) { return 4 * y; }

double f1_sys2(double x, double y) { return cos(x - 1) + y - 0.5; }

double f2_sys2(double x, double y) { return x - cos(y) - 3; }

double df1dx_sys2(double x, double y) { return -sin(x - 1); }

double df1dy_sys2(double x, double y) { return 1; }

double df2dx_sys2(double x, double y) { return 1; }

double df2dy_sys2(double x, double y) { return sin(y); }

void plotSystem(const System& sys, double xmin, double xmax, double ymin, double ymax) {
    const int N = 500;
    vector<double> xs(N*N), ys(N*N), f1vals(N*N), f2vals(N*N);
    int idx = 0;
    double stepx = (xmax - xmin) / (N - 1);
    double stepy = (ymax - ymin) / (N - 1);

    for (int i = 0; i < N; i++) {
        double x = xmin + i * stepx;
        for (int j = 0; j < N; ++j) {
            double y = ymin + j * stepy;
            xs[idx] = x;
            ys[idx] = y;
            f1vals[idx] = sys.f1(x, y);
            f2vals[idx] = sys.f2(x, y);
            idx++;
        }
    }

    plt::figure();
    vector<double> f1x, f1y;
    for (int k = 0; k < N*N; ++k) {
        if (abs(f1vals[k]) < 0.05) {
            f1x.push_back(xs[k]);
            f1y.push_back(ys[k]);
        }
    }
    plt::scatter(f1x, f1y, 1.0, {{"color", "red"}, {"label", "f1 = 0"}});

    vector<double> f2x, f2y;
    for (int k = 0; k < N*N; ++k) {
        if (abs(f2vals[k]) < 0.05) {
            f2x.push_back(xs[k]);
            f2y.push_back(ys[k]);
        }
    }
    plt::scatter(f2x, f2y, 1.0, {{"color", "blue"}, {"label", "f2 = 0"}});

    plt::xlabel("x");
    plt::ylabel("y");
    plt::title("Графики функций системы");
    plt::legend();
    plt::grid(true);
    plt::show();
}

void newtonSystem(const System& sys, double x0, double y0, double eps, ostream& out) {
    out << "--- Метод Ньютона для системы ---\n";

    out << "Итерация |      x      |      y      |     f1      |     f2      |     dx      |     dy    \n";
    out << "--------------------------------------------------------------------------------------\n";
    out << fixed << setprecision(6);

    double x = x0, y = y0;
    int iter = 0;
    double dx, dy;

    do {
        double f1v = sys.f1(x, y);
        double f2v = sys.f2(x, y);

        double J11 = sys.df1dx(x, y);
        double J12 = sys.df1dy(x, y);
        double J21 = sys.df2dx(x, y);
        double J22 = sys.df2dy(x, y);

        double det = J11 * J22 - J12 * J21;
        if (abs(det) < 1e-12) {
            out << "Определитель Якоби близок к нулю.\n";
            return;
        }
        dx = -(f1v * J22 - J12 * f2v) / det;
        dy = -(J11 * f2v - f1v * J21) / det;

        x += dx;
        y += dy;
        iter++;

        out << setw(5) << iter << "     | "
            << setw(10) << x << " | "
            << setw(10) << y << " | "
            << setw(10) << sys.f1(x, y) << " | "
            << setw(10) << sys.f2(x, y) << " | "
            << setw(10) << dx << " | "
            << setw(10) << dy << "\n";

        if (max(abs(dx), abs(dy)) < eps) break;
        if (iter > 1000) {
            out << "Достигнут лимит итераций.\n";
            break;
        }
    } while (true);

    out << "--------------------------------------------------------------------------------------\n";
    out << "Решение: x = " << x << ", y = " << y << "\n";
    out << "Значения функций: f1 = " << sys.f1(x, y) << ", f2 = " << sys.f2(x, y) << "\n";
    out << "Количество итераций: " << iter << "\n";
    out << "Вектор погрешностей последней итерации: dx = " << dx << ", dy = " << dy << "\n\n";

    out << "Проверка (невязка):\n";
    out << "|f1| = " << abs(sys.f1(x, y)) << ", |f2| = " << abs(sys.f2(x, y)) << "\n";
    if (abs(sys.f1(x, y)) < eps && abs(sys.f2(x, y)) < eps)
        out << "Решение удовлетворяет системе с заданной точностью.\n";
    else
        out << "Невязка превышает заданную точность\n";
}

int main() {
    vector<System> systems = {
        {
            "tg(xy + 0.1) = x^2;  x^2 + 2y^2 = 1",
            f1_sys1, f2_sys1,
            df1dx_sys1, df1dy_sys1, df2dx_sys1, df2dy_sys1
        },
        {
            "cos(x-1) + y = 0.5;  x - cos(y) = 3",
            f1_sys2, f2_sys2,
            df1dx_sys2, df1dy_sys2, df2dx_sys2, df2dy_sys2
        }
    };

    int choice;
    cout << "Выберите систему:\n";
    for (size_t i = 0; i < systems.size(); i++)
        cout << i+1 << ". " << systems[i].description << "\n";
    cout << "> ";
    while (!(cin >> choice) || choice < 1 || choice > (int)systems.size()) {
        cin.clear();
        cin.ignore(numeric_limits<streamsize>::max(), '\n');
        cout << "Некорректный ввод. Повторите:\n> ";
    }

    const System& sys = systems[choice-1];

    double x0, y0, eps;
    cout << "Введите начальное приближение x0 y0: ";
    while (!(cin >> x0 >> y0)) {
        cin.clear();
        cin.ignore(numeric_limits<streamsize>::max(), '\n');
        cout << "Ошибка. Введите два числа: ";
    }
    cout << "Введите погрешность: ";
    while (!(cin >> eps)) {
        cin.clear();
        cin.ignore(numeric_limits<streamsize>::max(), '\n');
        cout << "Ошибка. Введите число: ";
    }

    double xmin = x0 - 2.0, xmax = x0 + 2.0;
    double ymin = y0 - 2.0, ymax = y0 + 2.0;
    plotSystem(sys, xmin, xmax, ymin, ymax);

    newtonSystem(sys, x0, y0, eps, cout);

    return 0;
}