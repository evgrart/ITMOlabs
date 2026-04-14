#include <iostream>
#include <vector>
#include <cmath>
#include <iomanip>
#include <string>
#include <limits>
#include <fstream>
#include <algorithm>
#include "matplotlibcpp.h"

namespace plt = matplotlibcpp;
using namespace std;

struct Point {
    double x;
    double y;
};

struct ApproxResult {
    string name;
    vector<double> coeffs;
    double S;       // мера отклонения
    double delta;   // ско
    double R2;      // достоверность аппроксимации
    bool valid = true;
    string equation;
};


template <typename T>
T get_input(const string& prompt) {
    T value;
    while (true) {
        cout << prompt;
        if (cin >> value) {
            if (cin.peek() == '\n' || cin.peek() == ' ') {
                cin.ignore(numeric_limits<streamsize>::max(), '\n');
                return value;
            }
        }
        cin.clear();
        cin.ignore(numeric_limits<streamsize>::max(), '\n');
        cout << "Введено некорректное значение\n";
    }
}

vector<double> solve_gauss(vector<vector<double>> matrix) {
    int n = matrix.size();
    for (int i = 0; i < n; ++i) {
        int max_row = i;
        for (int k = i + 1; k < n; k++) {
            if (abs(matrix[k][i]) > abs(matrix[max_row][i])) max_row = k;
        }
        swap(matrix[i], matrix[max_row]);

        if (abs(matrix[i][i]) < 1e-12) return {}; 

        for (int k = i + 1; k < n; ++k) {
            double c = -matrix[k][i] / matrix[i][i];
            for (int j = i; j <= n; ++j) {
                if (i == j) matrix[k][j] = 0;
                else matrix[k][j] += c * matrix[i][j];
            }
        }
    }

    vector<double> x(n);
    for (int i = n - 1; i >= 0; i--) {
        x[i] = matrix[i][n] / matrix[i][i];
        for (int k = i - 1; k >= 0; k--) {
            matrix[k][n] -= matrix[k][i] * x[i];
        }
    }
    return x;
}

double calc_R2(const vector<Point>& pts, const vector<double>& phi_vals) {
    double sum_num = 0, sum_phi_sq = 0, sum_phi = 0;
    int n = pts.size();
    for (int i = 0; i < n; i++) {
        sum_num += pow(pts[i].y - phi_vals[i], 2);
        sum_phi_sq += pow(phi_vals[i], 2);
        sum_phi += phi_vals[i];
    }
    double den = sum_phi_sq - (1.0 / n) * pow(sum_phi, 2);
    if (abs(den) < 1e-12) return 0.0;
    return 1.0 - (sum_num / den);
}

double pearson_correlation(const vector<Point>& pts) {
    double sumX = 0, sumY = 0;
    for(const auto& p : pts) { sumX += p.x; sumY += p.y; }
    double meanX = sumX / pts.size();
    double meanY = sumY / pts.size();

    double num = 0, denX = 0, denY = 0;
    for(const auto& p : pts) {
        num += (p.x - meanX) * (p.y - meanY);
        denX += pow(p.x - meanX, 2);
        denY += pow(p.y - meanY, 2);
    }
    if (denX == 0 || denY == 0) return 0.0;
    return num / sqrt(denX * denY);
}


ApproxResult calc_linear(const vector<Point>& pts) {
    int n = pts.size();
    double SX = 0, SY = 0, SXX = 0, SXY = 0;
    for (const auto& p : pts) {
        SX += p.x; SY += p.y;
        SXX += p.x * p.x; SXY += p.x * p.y;
    }
    vector<vector<double>> matrix = {{SXX, SX, SXY}, {SX, (double)n, SY}};
    vector<double> coeffs = solve_gauss(matrix);
    if (coeffs.empty()) return {"Линейная", {}, 0, 0, 0, false, ""};

    double S = 0;
    vector<double> phi_vals(n);
    for (int i = 0; i < n; i++) {
        phi_vals[i] = coeffs[0] * pts[i].x + coeffs[1];
        S += pow(phi_vals[i] - pts[i].y, 2);
    }
    return {"Линейная", coeffs, S, sqrt(S / n), calc_R2(pts, phi_vals), true, "y = " + to_string(coeffs[0]) + "x + " + to_string(coeffs[1])};
}

ApproxResult calc_quad(const vector<Point>& pts) {
    int n = pts.size();
    double SX = 0, SX2 = 0, SX3 = 0, SX4 = 0, SY = 0, SXY = 0, SX2Y = 0;
    for (const auto& p : pts) {
        SX += p.x; SX2 += pow(p.x, 2); SX3 += pow(p.x, 3); SX4 += pow(p.x, 4);
        SY += p.y; SXY += p.x * p.y; SX2Y += pow(p.x, 2) * p.y;
    }
    vector<vector<double>> matrix = {
        {SX4, SX3, SX2, SX2Y}, {SX3, SX2, SX, SXY}, {SX2, SX, (double)n, SY}
    };
    vector<double> coeffs = solve_gauss(matrix);
    if (coeffs.empty()) return {"Квадратичная", {}, 0, 0, 0, false, ""};

    double S = 0;
    vector<double> phi_vals(n);
    for (int i = 0; i < n; i++) {
        phi_vals[i] = coeffs[0] * pow(pts[i].x, 2) + coeffs[1] * pts[i].x + coeffs[2];
        S += pow(phi_vals[i] - pts[i].y, 2);
    }
    return {"Квадратичная", coeffs, S, sqrt(S / n), calc_R2(pts, phi_vals), true, "y = " + to_string(coeffs[0]) + "x^2 + " + to_string(coeffs[1]) + "x + " + to_string(coeffs[2])};
}

ApproxResult calc_cubic(const vector<Point>& pts) {
    int n = pts.size();
    vector<double> sx(7, 0), sxy(4, 0);
    for (const auto& p : pts) {
        for (int i = 0; i <= 6; i++) sx[i] += pow(p.x, i);
        for (int i = 0; i <= 3; i++) sxy[i] += pow(p.x, i) * p.y;
    }
    vector<vector<double>> matrix = {
        {sx[6], sx[5], sx[4], sx[3], sxy[3]}, {sx[5], sx[4], sx[3], sx[2], sxy[2]},
        {sx[4], sx[3], sx[2], sx[1], sxy[1]}, {sx[3], sx[2], sx[1], sx[0], sxy[0]}
    };
    vector<double> coeffs = solve_gauss(matrix);
    if (coeffs.empty()) return {"Кубическая", {}, 0, 0, 0, false, ""};

    double S = 0;
    vector<double> phi_vals(n);
    for (int i = 0; i < n; i++) {
        phi_vals[i] = coeffs[0]*pow(pts[i].x,3) + coeffs[1]*pow(pts[i].x,2) + coeffs[2]*pts[i].x + coeffs[3];
        S += pow(phi_vals[i] - pts[i].y, 2);
    }
    return {"Кубическая", coeffs, S, sqrt(S / n), calc_R2(pts, phi_vals), true, "y = a3*x^3 + a2*x^2 + a1*x + a0"};
}

ApproxResult calc_exp(const vector<Point>& pts) {
    vector<Point> lin_pts;
    for (const auto& p : pts) {
        if (p.y <= 0) return {"Экспоненциальная", {}, 0, 0, 0, false, "y <= 0"};
        lin_pts.push_back({p.x, log(p.y)});
    }
    ApproxResult lin = calc_linear(lin_pts);
    if (!lin.valid) return {"Экспоненциальная", {}, 0, 0, 0, false, ""};
    double a = exp(lin.coeffs[1]), b = lin.coeffs[0], S = 0;
    vector<double> phi_vals(pts.size());
    for (size_t i = 0; i < pts.size(); i++) {
        phi_vals[i] = a * exp(b * pts[i].x);
        S += pow(phi_vals[i] - pts[i].y, 2);
    }
    return {"Экспоненциальная", {a, b}, S, sqrt(S / pts.size()), calc_R2(pts, phi_vals), true, "y = " + to_string(a) + " * e^(" + to_string(b) + "x)"};
}

ApproxResult calc_log(const vector<Point>& pts) {
    vector<Point> lin_pts;
    for (const auto& p : pts) {
        if (p.x <= 0) return {"Логарифмическая", {}, 0, 0, 0, false, "x <= 0"};
        lin_pts.push_back({log(p.x), p.y});
    }
    ApproxResult lin = calc_linear(lin_pts);
    if (!lin.valid) return {"Логарифмическая", {}, 0, 0, 0, false, ""};
    double a = lin.coeffs[0], b = lin.coeffs[1], S = 0;
    vector<double> phi_vals(pts.size());
    for (size_t i = 0; i < pts.size(); i++) {
        phi_vals[i] = a * log(pts[i].x) + b;
        S += pow(phi_vals[i] - pts[i].y, 2);
    }
    return {"Логарифмическая", {a, b}, S, sqrt(S / pts.size()), calc_R2(pts, phi_vals), true, "y = " + to_string(a) + " * ln(x) + " + to_string(b)};
}

ApproxResult calc_power(const vector<Point>& pts) {
    vector<Point> lin_pts;
    for (const auto& p : pts) {
        if (p.x <= 0 || p.y <= 0) return {"Степенная", {}, 0, 0, 0, false, "x, y <= 0 "};
        lin_pts.push_back({log(p.x), log(p.y)});
    }
    ApproxResult lin = calc_linear(lin_pts);
    if (!lin.valid) return {"Степенная", {}, 0, 0, 0, false, ""};
    double a = exp(lin.coeffs[1]), b = lin.coeffs[0], S = 0;
    vector<double> phi_vals(pts.size());
    for (size_t i = 0; i < pts.size(); i++) {
        phi_vals[i] = a * pow(pts[i].x, b);
        S += pow(phi_vals[i] - pts[i].y, 2);
    }
    return {"Степенная", {a, b}, S, sqrt(S / pts.size()), calc_R2(pts, phi_vals), true, "y = " + to_string(a) + " * x^(" + to_string(b) + ")"};
}

double evaluate_approx(const ApproxResult& res, double x) {
    if (res.name == "Линейная") return res.coeffs[0] * x + res.coeffs[1];
    if (res.name == "Квадратичная") return res.coeffs[0] * x * x + res.coeffs[1] * x + res.coeffs[2];
    if (res.name == "Кубическая") return res.coeffs[0] * pow(x, 3) + res.coeffs[1] * pow(x, 2) + res.coeffs[2] * x + res.coeffs[3];
    if (res.name == "Экспоненциальная") return res.coeffs[0] * exp(res.coeffs[1] * x);
    if (res.name == "Логарифмическая") return res.coeffs[0] * log(x) + res.coeffs[1];
    if (res.name == "Степенная") return res.coeffs[0] * pow(x, res.coeffs[1]);
    return 0;
}

void plot_best_approximation(const vector<Point>& pts, const ApproxResult& best) {

    vector<double> x_pts, y_pts;
    double min_x = pts[0].x, max_x = pts[0].x;
    
    for (const auto& p : pts) {
        x_pts.push_back(p.x);
        y_pts.push_back(p.y);
        if (p.x < min_x) min_x = p.x;
        if (p.x > max_x) max_x = p.x;
    }

    int N = 100;
    double step = (max_x - min_x) / (N - 1);
    vector<double> curve_x, curve_y;
    for (int i = 0; i < N; i++) {
        double x = min_x + i * step;
        if (x <= 0 && (best.name == "Логарифмическая" || best.name == "Степенная")) x = 1e-6; 
        curve_x.push_back(x);
        curve_y.push_back(evaluate_approx(best, x));
    }

    plt::figure();
    
    plt::scatter(x_pts, y_pts, 30.0, {{"color", "red"}, {"label", "Data points"}});
    plt::plot(curve_x, curve_y, {{"color", "blue"}, {"label", "Best approximation"}});

    plt::xlabel("X");
    plt::ylabel("Y");
    plt::title("МНК");
    plt::legend();
    plt::grid(true);
    
    plt::show(); 
}


int main() {
    vector<Point> points;
    
    int input_type;
    while(true) {
        cout << "Ввод данных:\n1 - С клавиатуры\n2 - Из файла\nВвод: ";
        input_type = get_input<int>("");
        if(input_type == 1 || input_type == 2) break;
        cout << "Введите 1 или 2\n";
    }

    if (input_type == 1) {
        int n;
        while(true) {
            n = get_input<int>("Введите количество точек (от 8 до 12): ");
            if (n >= 8 && n <= 12) break;
            cout << "Количество точек от 8 до 12\n";
        }
        for (int i = 0; i < n; i++) {
            cout << "Точка " << i + 1 << ":\n";
            double x = get_input<double>("  x = ");
            double y = get_input<double>("  y = ");
            points.push_back({x, y});
        }
    } else {
        string filename;
        cout << "Введите имя файла: ";
        cin >> filename;
        ifstream fin(filename);
        if (!fin.is_open()) {
            cout << "Ошибка открытия файла\n";
            return 1;
        }
        double x, y;
        while (fin >> x >> y) points.push_back({x, y});
        fin.close();
        if (points.size() < 8) {
            cout << "Недостаточно точек в файле\n";
            return 1;
        }
        cout << "Считано " << points.size() << " точек\n";
    }

    vector<ApproxResult> results;
    results.push_back(calc_linear(points));
    results.push_back(calc_quad(points));
    results.push_back(calc_cubic(points));
    results.push_back(calc_exp(points));
    results.push_back(calc_log(points));
    results.push_back(calc_power(points));

    cout << "\n" << string(95, '-') << "\n";
    cout << left << setw(18) << "Вид функции" 
         << setw(15) << "Мера S" 
         << setw(15) << "Откл. delta" 
         << setw(12) << "R^2" 
         << "Уравнение" << "\n";
    cout << string(95, '-') << "\n";

    ApproxResult* best = nullptr;
    
    for (auto& res : results) {
        if (!res.valid) {
            cout << left << setw(18) << res.name << res.equation << "\n";
            continue;
        }
        cout << left << setw(18) << res.name 
             << setw(15) << fixed << setprecision(5) << res.S 
             << setw(15) << res.delta 
             << setw(12) << res.R2 
             << res.equation << "\n";
             
        if (best == nullptr || res.delta < best->delta) best = &res;
    }
    cout << string(95, '-') << "\n";

    double r = pearson_correlation(points);
    cout << "\nКоэффициент Пирсона r = " << r << "\n";

    if (best) {
        cout << "\nНаилучшая аппроксимация: " << best->name << "\n";
        cout << "Уравнение: " << best->equation << "\n";
        
        plot_best_approximation(points, *best);
    }

    return 0;
}