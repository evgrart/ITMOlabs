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

vector<vector<double>> build_diff_table(const vector<Point>& pts) {
    int n = pts.size();
    vector<vector<double>> diff(n, vector<double>(n, 0.0));
    for (int i = 0; i < n; i++) {
        diff[i][0] = pts[i].y;
    }
    for (int j = 1; j < n; j++) {
        for (int i = 0; i < n - j; i++) {
            diff[i][j] = diff[i + 1][j - 1] - diff[i][j - 1];
        }
    }
    return diff;
}

void print_diff_table(const vector<Point>& pts, const vector<vector<double>>& diff) {
    int n = pts.size();
    cout << "\nТаблица конечных разностей:\n";
    cout << setw(10) << "x" << setw(15) << "y";
    for (int j = 1; j < n; j++) {
        cout << setw(15) << "d^" + to_string(j) + "y";
    }
    cout << "\n";
    for (int i = 0; i < n; i++) {
        cout << setw(10) << fixed << setprecision(5) << pts[i].x;
        for (int j = 0; j < n - i; j++) {
            cout << setw(15) << fixed << setprecision(5) << diff[i][j];
        }
        cout << "\n";
    }
    cout << "\n";
}

double lagrange(const vector<Point>& pts, double x) {
    double res = 0;
    int n = pts.size();
    for (int i = 0; i < n; i++) {
        double term = pts[i].y;
        for (int j = 0; j < n; j++) {
            if (i != j) {
                term *= (x - pts[j].x) / (pts[i].x - pts[j].x);
            }
        }
        res += term;
    }
    return res;
}

double newton_forward(const vector<Point>& pts, const vector<vector<double>>& diff, double x) {
    int n = pts.size();
    double h = pts[1].x - pts[0].x;
    double t = (x - pts[0].x) / h;
    double res = diff[0][0];
    double term = 1;
    for (int i = 1; i < n; i++) {
        term *= (t - i + 1) / i;
        res += term * diff[0][i];
    }
    return res;
}

double newton_backward(const vector<Point>& pts, const vector<vector<double>>& diff, double x) {
    int n = pts.size();
    double h = pts[1].x - pts[0].x;
    double t = (x - pts[n - 1].x) / h;
    double res = diff[n - 1][0];
    double term = 1;
    for (int i = 1; i < n; i++) {
        term *= (t + i - 1) / i;
        res += term * diff[n - 1 - i][i];
    }
    return res;
}

double gauss_forward(const vector<Point>& pts, const vector<vector<double>>& diff, double x) {
    int n = pts.size();
    double h = pts[1].x - pts[0].x;
    int k = 0;
    double min_dist = abs(x - pts[0].x);
    for (int i = 1; i < n; i++) {
        if (abs(x - pts[i].x) < min_dist) {
            min_dist = abs(x - pts[i].x);
            k = i;
        }
    }
    if (x < pts[k].x && k > 0) k--; 

    double t = (x - pts[k].x) / h;
    double res = diff[k][0];
    double term = 1;
    int cur_idx = k;

    for (int i = 1; i < n; i++) {
        if (i % 2 == 1) {
            term *= (t - i / 2.0) / i;
        } else {
            term *= (t + i / 2.0) / i;
            cur_idx--;
        }
        if (cur_idx < 0 || cur_idx + i >= n) break;
        res += term * diff[cur_idx][i];
    }
    return res;
}

double gauss_backward(const vector<Point>& pts, const vector<vector<double>>& diff, double x) {
    int n = pts.size();
    double h = pts[1].x - pts[0].x;
    int k = 0;
    double min_dist = abs(x - pts[0].x);
    for (int i = 1; i < n; i++) {
        if (abs(x - pts[i].x) < min_dist) {
            min_dist = abs(x - pts[i].x);
            k = i;
        }
    }
    if (x > pts[k].x && k < n - 1) k++;

    double t = (x - pts[k].x) / h;
    double res = diff[k][0];
    double term = 1;
    int cur_idx = k;

    for (int i = 1; i < n; i++) {
        if (i % 2 == 1) {
            term *= (t + i / 2.0) / i;
            cur_idx--;
        } else {
            term *= (t - i / 2.0) / i;
        }
        if (cur_idx < 0 || cur_idx + i >= n) break;
        res += term * diff[cur_idx][i];
    }
    return res;
}

double stirling(const vector<Point>& pts, const vector<vector<double>>& diff, double x) {
    return (gauss_forward(pts, diff, x) + gauss_backward(pts, diff, x)) / 2.0;
}

double bessel(const vector<Point>& pts, const vector<vector<double>>& diff, double x) {
    int n = pts.size();
    double h = pts[1].x - pts[0].x;
    int k = 0;
    for (int i = 0; i < n - 1; i++) {
        if (x >= pts[i].x && x <= pts[i + 1].x) {
            k = i;
            break;
        }
    }
    
    double t = (x - pts[k].x) / h;
    double res = (diff[k][0] + diff[k + 1][0]) / 2.0;
    res += (t - 0.5) * diff[k][1];
    
    double term = 1;
    int cur_idx = k;
    for (int i = 2; i < n; i++) {
        if (i % 2 == 0) {
            term *= (t - i / 2.0 + 1) * (t + i / 2.0 - 1) / (i * (i - 1.0));
            if (cur_idx - 1 < 0 || cur_idx + i - 1 >= n) break;
            res += term * (diff[cur_idx - 1][i] + diff[cur_idx][i]) / 2.0;
            cur_idx--;
        } else {
            term *= (t - 0.5) / i;
            if (cur_idx < 0 || cur_idx + i >= n) break;
            res += term * diff[cur_idx][i];
        }
    }
    return res;
}

double eval_best_method(const vector<Point>& pts, const vector<vector<double>>& diff, double x) {
    int n = pts.size();
    if (x <= pts[n / 2].x) return newton_forward(pts, diff, x);
    else return newton_backward(pts, diff, x);
}

void plot_interpolation(const vector<Point>& pts, const vector<vector<double>>& diff, double target_x, double target_y) {
    vector<double> x_pts, y_pts;
    double min_x = pts[0].x;
    double max_x = pts.back().x;
    
    for (const auto& p : pts) {
        x_pts.push_back(p.x);
        y_pts.push_back(p.y);
    }

    int N = 100;
    double padding = (max_x - min_x) * 0.1;
    double step = (max_x - min_x + 2 * padding) / (N - 1);
    vector<double> curve_x, curve_y;
    
    for (int i = 0; i < N; i++) {
        double x = min_x - padding + i * step;
        curve_x.push_back(x);
        curve_y.push_back(eval_best_method(pts, diff, x));
    }

    plt::figure();
    plt::scatter(x_pts, y_pts, 30.0, {{"color", "red"}, {"label", "Узлы интерполяции"}});
    plt::plot(curve_x, curve_y, {{"color", "blue"}, {"label", "Интерполяционный многочлен"}});
    plt::scatter(vector<double>{target_x}, vector<double>{target_y}, 50.0, {{"color", "green"}, {"label", "Искомая точка"}});

    plt::xlabel("X");
    plt::ylabel("Y");
    plt::title("Интерполяция функции");
    plt::legend();
    plt::grid(true);
    plt::show(); 
}

int main() {
    vector<Point> points;
    
    int input_type;
    while(true) {
        cout << "Выбор ввода данных:\n1 - С клавиатуры\n2 - Из файла\n3 - Выбор функции\nВвод: ";
        input_type = get_input<int>("");
        if(input_type >= 1 && input_type <= 3) break;
        cout << "Введите 1, 2 или 3\n";
    }

    // ... (код инициализации массива точек скрыт для экономии места, см. полный листинг)
    // Данный блок совпадает с отправленным кодом.
    
    auto diff_table = build_diff_table(points);
    print_diff_table(points, diff_table);

    while (true) {
        double x = get_input<double>("Введите значение аргумента X для интерполяции: ");

        cout << "\nРезультаты интерполяции:\n";
        cout << string(50, '-') << "\n";
        
        double res_lagrange = lagrange(points, x);
        cout << left << setw(30) << "Многочлен Лагранжа:" << res_lagrange << "\n";

        double res_nf = newton_forward(points, diff_table, x);
        double res_nb = newton_backward(points, diff_table, x);
        double mid_x = (points[0].x + points.back().x) / 2.0;

        if (x <= mid_x) {
            cout << left << setw(30) << "Многочлен Ньютона (вперед):" << res_nf << "\n";
        } else {
            cout << left << setw(30) << "Многочлен Ньютона (назад):" << res_nb << "\n";
        }

        double res_gf = gauss_forward(points, diff_table, x);
        double res_gb = gauss_backward(points, diff_table, x);
        
        cout << left << setw(30) << "Многочлен Гаусса (вперед):" << res_gf << "\n";
        cout << left << setw(30) << "Многочлен Гаусса (назад):" << res_gb << "\n";

        double res_stirling = stirling(points, diff_table, x);
        cout << left << setw(30) << "Схема Стирлинга:" << res_stirling << "\n";

        double res_bessel = bessel(points, diff_table, x);
        cout << left << setw(30) << "Схема Бесселя:" << res_bessel << "\n";
        
        cout << string(50, '-') << "\n";

        double final_res = eval_best_method(points, diff_table, x);
        plot_interpolation(points, diff_table, x, final_res);

        int cont;
        while (true) {
            cout << "Хотите проверить другой X? (1 - Да, 0 - Нет): ";
            cont = get_input<int>("");
            if (cont == 0 || cont == 1) break;
        }
        if (cont == 0) break;
    }

    return 0;
}