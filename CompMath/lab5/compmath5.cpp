#include <iostream>
#include <vector>
#include <cmath>
#include <iomanip>
#include <string>
#include <limits>
#include <cstdlib>
#include <fstream>
#include <algorithm>
#include <sstream>
#include <stdexcept>

#ifdef __HAS_MATPLOTLIB__
#include "matplotlibcpp.h"
namespace plt = matplotlibcpp;
#endif

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
            int next = cin.peek();
            if (next == char_traits<char>::eof() || next == '\n' || next == ' ' || next == '\t') {
                cin.ignore(numeric_limits<streamsize>::max(), '\n');
                return value;
            }
        }
        if (cin.eof()) {
            cout << "\nВвод завершен\n";
            exit(1);
        }
        cin.clear();
        cin.ignore(numeric_limits<streamsize>::max(), '\n');
        cout << "Введено некорректное значение\n";
    }
}

vector<Point> default_variant_points() {
    return {
        {0.50, 1.5320},
        {0.55, 2.5356},
        {0.60, 3.5406},
        {0.65, 4.5462},
        {0.70, 5.5504},
        {0.75, 6.5559},
        {0.80, 7.5594}
    };
}

double function_sin(double x) {
    return sin(x);
}

double function_cos(double x) {
    return cos(x);
}

double function_exp(double x) {
    return exp(x);
}

double function_log(double x) {
    return log(x);
}

vector<Point> normalize_points(vector<Point> pts) {
    if (pts.size() < 2) {
        throw runtime_error("нужно минимум две точки");
    }

    sort(pts.begin(), pts.end(), [](const Point& a, const Point& b) {
        return a.x < b.x;
    });

    const double h = pts[1].x - pts[0].x;
    const double eps = max(1e-9, abs(h) * 1e-7);

    if (abs(h) < eps) {
        throw runtime_error("найдены совпадающие значения x");
    }

    for (size_t i = 1; i < pts.size(); i++) {
        if (abs(pts[i].x - pts[i - 1].x) < eps) {
            throw runtime_error("найдены совпадающие значения x");
        }
    }

    for (size_t i = 2; i < pts.size(); i++) {
        double current_h = pts[i].x - pts[i - 1].x;
        if (abs(current_h - h) > eps) {
            throw runtime_error("для конечных разностей x должны быть равноотстоящими");
        }
    }

    return pts;
}

vector<Point> read_points_from_keyboard() {
    int n;
    while (true) {
        n = get_input<int>("Введите количество точек: ");
        if (n >= 2) break;
        cout << "Количество точек должно быть не меньше 2\n";
    }

    vector<Point> pts;
    pts.reserve(n);
    for (int i = 0; i < n; i++) {
        double x = get_input<double>("x_" + to_string(i) + ": ");
        double y = get_input<double>("y_" + to_string(i) + ": ");
        pts.push_back({x, y});
    }

    return pts;
}

vector<Point> read_points_from_file() {
    string filename;
    cout << "Введите имя файла: ";
    cin >> filename;

    ifstream file(filename);
    if (!file.is_open()) {
        throw runtime_error("не удалось открыть файл " + filename);
    }

    vector<double> values;
    string line;
    int line_number = 0;
    while (getline(file, line)) {
        line_number++;
        size_t comment_pos = line.find('#');
        if (comment_pos != string::npos) {
            line.erase(comment_pos);
        }
        replace(line.begin(), line.end(), ',', '.');

        stringstream ss(line);
        double value;
        while (ss >> value) {
            values.push_back(value);
        }
        if (!ss.eof()) {
            throw runtime_error("некорректное число в файле на строке " + to_string(line_number));
        }
    }

    if (values.empty()) {
        throw runtime_error("файл не содержит точек");
    }

    size_t start = 0;
    size_t count = 0;
    long long possible_count = llround(values[0]);
    if (values.size() >= 3 &&
        abs(values[0] - possible_count) < 1e-9 &&
        possible_count >= 2 &&
        1 + 2 * static_cast<size_t>(possible_count) == values.size()) {
        start = 1;
        count = static_cast<size_t>(possible_count);
    } else {
        if (values.size() % 2 != 0) {
            throw runtime_error("ожидались пары x y или первая строка с количеством точек");
        }
        count = values.size() / 2;
    }

    vector<Point> pts;
    pts.reserve(count);
    for (size_t i = 0; i < count; i++) {
        pts.push_back({values[start + 2 * i], values[start + 2 * i + 1]});
    }

    return pts;
}

vector<Point> build_points_from_function() {
    int function_choice;
    while (true) {
        cout << "Выбор функции:\n";
        cout << "1 - sin(x)\n";
        cout << "2 - cos(x)\n";
        cout << "3 - exp(x)\n";
        cout << "4 - ln(x)\n";
        function_choice = get_input<int>("Ввод: ");
        if (function_choice >= 1 && function_choice <= 4) break;
        cout << "Введите 1, 2, 3 или 4\n";
    }

    double (*func)(double) = function_sin;
    if (function_choice == 2) {
        func = function_cos;
    } else if (function_choice == 3) {
        func = function_exp;
    } else if (function_choice == 4) {
        func = function_log;
    }

    double a, b;
    int n;
    while (true) {
        a = get_input<double>("Введите левую границу интервала: ");
        b = get_input<double>("Введите правую границу интервала: ");
        n = get_input<int>("Введите количество узлов: ");

        bool valid = true;
        if (a >= b) {
            cout << "Левая граница должна быть меньше правой\n";
            valid = false;
        }
        if (n < 2) {
            cout << "Количество узлов должно быть не меньше 2\n";
            valid = false;
        }
        if (function_choice == 4 && a <= 0) {
            cout << "Для ln(x) левая граница должна быть больше 0\n";
            valid = false;
        }

        if (valid) break;
    }

    vector<Point> pts;
    pts.reserve(n);
    const double h = (b - a) / (n - 1);
    for (int i = 0; i < n; i++) {
        double x = a + i * h;
        pts.push_back({x, func(x)});
    }

    return pts;
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

    for (int order = 1; order < n; order++) {
        double factor;
        if (order == 1) {
            factor = t;
        } else if (order % 2 == 0) {
            factor = t - order / 2.0;
        } else {
            factor = t + (order - 1) / 2.0;
        }
        term *= factor / order;

        int idx = k - order / 2;
        if (idx < 0 || idx + order >= n) break;
        res += term * diff[idx][order];
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

    for (int order = 1; order < n; order++) {
        double factor;
        if (order == 1) {
            factor = t;
        } else if (order % 2 == 0) {
            factor = t + order / 2.0;
        } else {
            factor = t - (order - 1) / 2.0;
        }
        term *= factor / order;

        int idx = k - (order + 1) / 2;
        if (idx < 0 || idx + order >= n) break;
        res += term * diff[idx][order];
    }
    return res;
}

double eval_best_method(const vector<Point>& pts, const vector<vector<double>>& diff, double x) {
    int n = pts.size();
    if (x <= pts[n / 2].x) return newton_forward(pts, diff, x);
    else return newton_backward(pts, diff, x);
}

bool should_use_gauss_forward(const vector<Point>& pts, double x) {
    int k = 0;
    double min_dist = abs(x - pts[0].x);
    for (size_t i = 1; i < pts.size(); i++) {
        if (abs(x - pts[i].x) < min_dist) {
            min_dist = abs(x - pts[i].x);
            k = static_cast<int>(i);
        }
    }
    return x >= pts[k].x;
}

double eval_gauss_best(const vector<Point>& pts, const vector<vector<double>>& diff, double x) {
    if (should_use_gauss_forward(pts, x)) {
        return gauss_forward(pts, diff, x);
    }
    return gauss_backward(pts, diff, x);
}

#ifdef __HAS_MATPLOTLIB__
string make_graph_filename(double x) {
    ostringstream name;
    name << "compmath5_graph_" << fixed << setprecision(3) << x << ".png";
    string filename = name.str();
    for (char& ch : filename) {
        if (ch == '.') ch = '_';
        if (ch == '-') ch = 'm';
    }
    filename.replace(filename.size() - 4, 4, ".png");
    return filename;
}
#endif

void plot_interpolation(const vector<Point>& pts, const vector<vector<double>>& diff, double target_x, double target_y) {
    #ifdef __HAS_MATPLOTLIB__
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
    vector<double> curve_x, newton_y, gauss_y;
    
    for (int i = 0; i < N; i++) {
        double x = min_x - padding + i * step;
        curve_x.push_back(x);
        newton_y.push_back(eval_best_method(pts, diff, x));
        gauss_y.push_back(eval_gauss_best(pts, diff, x));
    }

    plt::figure();
    plt::scatter(x_pts, y_pts, 30.0, {{"color", "red"}, {"label", "Узлы интерполяции"}});
    plt::plot(curve_x, newton_y, {{"color", "blue"}, {"label", "Многочлен Ньютона"}});
    plt::plot(curve_x, gauss_y, {{"color", "orange"}, {"linestyle", "--"}, {"label", "Многочлен Гаусса"}});
    plt::scatter(vector<double>{target_x}, vector<double>{target_y}, 50.0, {{"color", "green"}, {"label", "Искомая точка"}});

    plt::xlabel("X");
    plt::ylabel("Y");
    plt::title("Интерполяция функции");
    plt::legend();
    plt::grid(true);
    string filename = make_graph_filename(target_x);
    plt::save(filename, 200);
    cout << "\nГрафик сохранен в файл: " << filename << "\n";
    plt::show(); 
    plt::close();
    #else
    (void)pts;
    (void)diff;
    (void)target_x;
    (void)target_y;
    cout << "\nГрафики недоступны (не скомпилировано с matplotlib)\n";
    #endif
}

int main() {
    vector<Point> points;
    
    int input_type;
    while(true) {
        cout << "Выбор ввода данных:\n";
        cout << "1 - С клавиатуры\n";
        cout << "2 - Из файла\n";
        cout << "3 - Выбор функции\n";
        cout << "4 - Таблица варианта 2\n";
        cout << "Ввод: ";
        input_type = get_input<int>("");
        if(input_type >= 1 && input_type <= 4) break;
        cout << "Введите 1, 2, 3 или 4\n";
    }

    try {
        if (input_type == 1) {
            points = read_points_from_keyboard();
        } else if (input_type == 2) {
            points = read_points_from_file();
        } else if (input_type == 3) {
            points = build_points_from_function();
        } else {
            points = default_variant_points();
        }

        points = normalize_points(points);
    } catch (const exception& e) {
        cout << "Ошибка чтения исходных данных: " << e.what() << "\n";
        return 1;
    }
    
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

        bool use_gauss_forward = should_use_gauss_forward(points, x);
        double res_gauss = use_gauss_forward
            ? gauss_forward(points, diff_table, x)
            : gauss_backward(points, diff_table, x);
        cout << left << setw(30)
             << (use_gauss_forward ? "Многочлен Гаусса (вперед):" : "Многочлен Гаусса (назад):")
             << res_gauss << "\n";
        
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
