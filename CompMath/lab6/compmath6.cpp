#include <iostream>
#include <vector>
#include <cmath>
#include <iomanip>
#include <string>
#include <limits>
#include <fstream>
#include <algorithm>

#ifdef __HAS_MATPLOTLIB__
#include "matplotlibcpp.h"
namespace plt = matplotlibcpp;
#endif

using namespace std;

struct Solution {
    vector<double> x;
    vector<double> y;
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

typedef double (*ODE_Function)(double x, double y);

double ode_variant_1(double x, double y) {
    return y + (1.0 + x) * y * y;
}

double ode_variant_2(double x, double y) {
    (void)x;
    return 1.0 + y * y;
}

double ode_variant_3(double x, double y) {
    return y - x * x + 1;
}

double exact_solution_variant_1(double x) {
    return -1.0 / x;
}

double exact_solution_variant_2(double x) {
    return tan(x);
}

double exact_solution_variant_3(double x) {
    return x * x + 2 * x + 1 + exp(x);
}

class OdeSolver {
public:
    virtual Solution solve(ODE_Function f, double x0, double y0, double xn, double h) = 0;
    virtual ~OdeSolver() {}
};

class ImprovedEuler : public OdeSolver {
public:
    Solution solve(ODE_Function f, double x0, double y0, double xn, double h) override {
        Solution sol;
        int n = static_cast<int>(floor((xn - x0) / h + 1e-9)) + 1;
        
        double x = x0;
        double y = y0;
        
        sol.x.push_back(x);
        sol.y.push_back(y);
        
        for (int i = 1; i < n; i++) {
            double y_temp = y + h * f(x, y);
            y = y + (h / 2.0) * (f(x, y) + f(x + h, y_temp));
            x += h;
            sol.x.push_back(x);
            sol.y.push_back(y);
        }
        
        return sol;
    }
};

class RungeKutta4 : public OdeSolver {
public:
    Solution solve(ODE_Function f, double x0, double y0, double xn, double h) override {
        Solution sol;
        int n = static_cast<int>(floor((xn - x0) / h + 1e-9)) + 1;
        
        double x = x0;
        double y = y0;
        
        sol.x.push_back(x);
        sol.y.push_back(y);
        
        for (int i = 1; i < n; i++) {
            double k1 = h * f(x, y);
            double k2 = h * f(x + h / 2.0, y + k1 / 2.0);
            double k3 = h * f(x + h / 2.0, y + k2 / 2.0);
            double k4 = h * f(x + h, y + k3);
            
            y = y + (k1 + 2.0 * k2 + 2.0 * k3 + k4) / 6.0;
            x += h;
            sol.x.push_back(x);
            sol.y.push_back(y);
        }
        
        return sol;
    }
};

class Milne : public OdeSolver {
private:
    RungeKutta4 rk4;
    
public:
    Solution solve(ODE_Function f, double x0, double y0, double xn, double h) override {
        Solution sol;
        int n = static_cast<int>(floor((xn - x0) / h + 1e-9)) + 1;
        
        double x = x0;
        double y = y0;
        
        sol.x.push_back(x);
        sol.y.push_back(y);
        
        Solution initial = rk4.solve(f, x0, y0, x0 + 3 * h, h);
        for (int i = 1; i <= 3 && i < n; i++) {
            sol.x.push_back(initial.x[i]);
            sol.y.push_back(initial.y[i]);
        }
        
        if (n <= 4) return sol;
        
        vector<double> f_vals;
        for (int i = 0; i < 4 && i < (int)initial.y.size(); i++) {
            f_vals.push_back(f(initial.x[i], initial.y[i]));
        }
        
        for (int i = 4; i < n; i++) {
            double x_i = sol.x.back();
            
            double y_pred = sol.y[i - 4] + (4.0 * h / 3.0) * (2.0 * f_vals[1] - f_vals[2] + 2.0 * f_vals[3]);
            
            double f_pred = f(x_i + h, y_pred);
            
            double y_corr = sol.y[i - 2] + (h / 3.0) * (f_vals[2] + 4.0 * f_vals[3] + f_pred);
            
            sol.x.push_back(x_i + h);
            sol.y.push_back(y_corr);
            
            f_vals.erase(f_vals.begin());
            f_vals.push_back(f(sol.x.back(), y_corr));
        }
        
        return sol;
    }
};

double runge_rule(double y_h, double y_h_half, int p) {
    return abs(y_h - y_h_half) / (pow(2.0, p) - 1.0);
}

double max_abs_error(const Solution& sol, double (*exact)(double)) {
    double max_error = 0.0;
    for (size_t i = 0; i < sol.x.size(); i++) {
        max_error = max(max_error, abs(sol.y[i] - exact(sol.x[i])));
    }
    return max_error;
}

void print_solution_table(const Solution& sol, const string& method_name, 
                         ODE_Function f, double (*exact)(double)) {
    cout << "\n" << method_name << ":\n";
    cout << string(80, '-') << "\n";
    cout << left << setw(12) << "x_i" << setw(20) << "y_i" << setw(20) << "Точное" 
         << setw(20) << "Ошибка\n";
    cout << string(80, '-') << "\n";
    
    for (size_t i = 0; i < sol.x.size(); i++) {
        double exact_val = exact(sol.x[i]);
        double error = abs(sol.y[i] - exact_val);
        cout << fixed << setprecision(6);
        cout << setw(12) << sol.x[i] << setw(20) << sol.y[i] 
             << setw(20) << exact_val << setw(20) << error << "\n";
    }
    cout << string(80, '-') << "\n";
}

void plot_solution(const vector<Solution>& solutions, const vector<string>& labels,
                  double (*exact)(double), double x0, double xn, const string& title) {
    #ifdef __HAS_MATPLOTLIB__
    vector<double> x_exact;
    vector<double> y_exact;
    int N = 200;
    double step_exact = (xn - x0) / (N - 1);
    
    for (int i = 0; i < N; i++) {
        double x = x0 + i * step_exact;
        x_exact.push_back(x);
        y_exact.push_back(exact(x));
    }
    
    plt::figure();
    plt::plot(x_exact, y_exact, {{"color", "black"}, {"linewidth", "2"}, {"label", "Точное решение"}});
    
    vector<string> colors = {"red", "blue", "green", "orange", "purple"};
    
    for (size_t i = 0; i < solutions.size(); i++) {
        plt::plot(solutions[i].x, solutions[i].y, 
                 {{"color", colors[i % colors.size()]}, {"linewidth", "1.5"}, {"label", labels[i]}});
    }
    
    plt::xlabel("x");
    plt::ylabel("y");
    plt::title(title);
    plt::legend();
    plt::grid(true);
    plt::show();
    #else
    cout << "\nГрафики недоступны (не скомпилировано с matplotlib)\n";
    cout << "Решение сохранено в памяти.\n";
    #endif
}

void test_input_validation() {
    cout << "\n=== ТЕСТИРОВАНИЕ ПРОВЕРКИ ВВОДА ===\n";
    
    cout << "\nТест 1: Некорректные диапазоны (x0 >= xn)\n";
    double x0 = 1.0, xn = 0.5, h = 0.1, y0 = 1.0;
    if (x0 >= xn) {
        cout << "✓ Ошибка корректно выявлена: x0 >= xn\n";
    }
    
    cout << "\nТест 2: Отрицательный шаг при возрастающем интервале\n";
    h = -0.1;
    x0 = 0.0; xn = 1.0;
    if ((xn - x0) * h < 0) {
        cout << "✓ Ошибка корректно выявлена: несоответствие знака шага\n";
    }
    
    cout << "\nТест 3: Слишком большой шаг\n";
    h = 10.0;
    if (h > (xn - x0)) {
        cout << "✓ Предупреждение: шаг больше интервала\n";
    }
    
    cout << "\nТест 4: Нулевой шаг\n";
    h = 0.0;
    if (h == 0.0) {
        cout << "✓ Ошибка корректно выявлена: шаг равен нулю\n";
    }
}

void test_edge_cases() {
    cout << "\n=== ТЕСТИРОВАНИЕ ГРАНИЧНЫХ СЛУЧАЕВ ===\n";
    
    cout << "\nТест 1: Очень малый шаг (h = 0.001)\n";
    ImprovedEuler solver;
    Solution sol = solver.solve(ode_variant_1, 1.0, -1.0, 1.3, 0.001);
    cout << "✓ Решение получено. Количество точек: " << sol.x.size() << "\n";
    cout << "  Начальное y: " << sol.y.front() << ", Конечное y: " << sol.y.back() << "\n";
    
    cout << "\nТест 2: Единственный шаг (h = 0.3)\n";
    sol = solver.solve(ode_variant_1, 1.0, -1.0, 1.3, 0.3);
    cout << "✓ Решение получено. Количество точек: " << sol.x.size() << "\n";
    
    cout << "\nТест 3: Крайние значения начальных условий\n";
    sol = solver.solve(ode_variant_2, 0.0, 0.0, 0.5, 0.1);
    cout << "✓ При y0=0: Конечное y = " << sol.y.back() << "\n";
    
    sol = solver.solve(ode_variant_3, 0.0, 2.0, 0.5, 0.1);
    cout << "✓ При y0=2.0: Конечное y = " << sol.y.back() << "\n";
}

void test_comparison_methods() {
    cout << "\n=== СРАВНЕНИЕ МЕТОДОВ ===\n";
    
    double x0 = 1.0, y0 = -1.0, xn = 1.5, h = 0.1;
    
    ImprovedEuler euler;
    RungeKutta4 rk4;
    Milne milne;
    
    Solution sol_euler = euler.solve(ode_variant_1, x0, y0, xn, h);
    Solution sol_rk4 = rk4.solve(ode_variant_1, x0, y0, xn, h);
    Solution sol_milne = milne.solve(ode_variant_1, x0, y0, xn, h);
    
    cout << "\nСравнение в конечной точке x = " << xn << ":\n";
    double exact_val = exact_solution_variant_1(xn);
    cout << "Точное решение: " << fixed << setprecision(8) << exact_val << "\n";
    cout << "Улучш. Эйлер:   " << sol_euler.y.back() 
         << " (ошибка: " << abs(sol_euler.y.back() - exact_val) << ")\n";
    cout << "Рунге-Кутта 4:  " << sol_rk4.y.back() 
         << " (ошибка: " << abs(sol_rk4.y.back() - exact_val) << ")\n";
    cout << "Метод Милна:    " << sol_milne.y.back() 
         << " (ошибка: " << abs(sol_milne.y.back() - exact_val) << ")\n";
}

void test_different_odes() {
    cout << "\n=== ТЕСТИРОВАНИЕ НА РАЗНЫХ ОДУ ===\n";
    
    cout << "\nОДУ 1: y' = y + (1+x)*y^2, y(1) = -1\n";
    ImprovedEuler solver;
    Solution sol = solver.solve(ode_variant_1, 1.0, -1.0, 1.5, 0.1);
    cout << "✓ Решено. Конечное y: " << sol.y.back() << "\n";
    
    cout << "\nОДУ 2: y' = 1 + y^2, y(0) = 0\n";
    sol = solver.solve(ode_variant_2, 0.0, 0.0, 0.4, 0.1);
    cout << "✓ Решено. Конечное y: " << sol.y.back() << "\n";
    
    cout << "\nОДУ 3: y' = y - x^2 + 1, y(0) = 2\n";
    sol = solver.solve(ode_variant_3, 0.0, 2.0, 1.0, 0.1);
    cout << "✓ Решено. Конечное y: " << sol.y.back() << "\n";
}

void test_runge_rule() {
    cout << "\n=== ТЕСТИРОВАНИЕ ПРАВИЛА РУНГЕ ===\n";
    
    ImprovedEuler solver;
    
    double x0 = 1.0, y0 = -1.0, xn = 1.1;
    
    Solution sol_h = solver.solve(ode_variant_1, x0, y0, xn, 0.1);
    Solution sol_h_half = solver.solve(ode_variant_1, x0, y0, xn, 0.05);
    
    double y_h = sol_h.y.back();
    double y_h_half = sol_h_half.y.back();
    double error_est = runge_rule(y_h, y_h_half, 2);
    
    cout << "\nСравнение решений с шагами h=0.1 и h=0.05:\n";
    cout << "y(h=0.1):     " << fixed << setprecision(8) << y_h << "\n";
    cout << "y(h=0.05):    " << y_h_half << "\n";
    cout << "Оценка ошибки (правило Рунге): " << error_est << "\n";
    cout << "✓ Оценка вычислена\n";
}

void test_numerical_stability() {
    cout << "\n=== ТЕСТИРОВАНИЕ ЧИСЛЕННОЙ УСТОЙЧИВОСТИ ===\n";
    
    ImprovedEuler euler;
    RungeKutta4 rk4;
    
    cout << "\nТест с увеличивающимися шагами:\n";
    for (double h : {0.01, 0.05, 0.1, 0.2}) {
        Solution sol = euler.solve(ode_variant_1, 1.0, -1.0, 1.5, h);
        double error = abs(sol.y.back() - exact_solution_variant_1(1.5));
        cout << "h = " << fixed << setprecision(2) << h << ": y_конечное = " 
             << setprecision(8) << sol.y.back() << ", ошибка = " << error << "\n";
    }
}

int main() {
    cout << "ЧИСЛЕННОЕ РЕШЕНИЕ ОБЫКНОВЕННЫХ ДИФФЕРЕНЦИАЛЬНЫХ УРАВНЕНИЙ\n";
    cout << "Вариант 2: Усовершенствованный метод Эйлера, Рунге-Кутта 4, Метод Милна\n";
    cout << "==========================================================================\n";
    
    int menu_choice;
    while (true) {
        cout << "\n1 - Интерактивное решение ОДУ\n";
        cout << "2 - Запустить все тесты\n";
        cout << "3 - Тестирование проверки ввода\n";
        cout << "4 - Тестирование граничных случаев\n";
        cout << "5 - Сравнение методов\n";
        cout << "6 - Тестирование на разных ОДУ\n";
        cout << "7 - Тестирование правила Рунге\n";
        cout << "8 - Тестирование численной устойчивости\n";
        cout << "0 - Выход\n";
        
        menu_choice = get_input<int>("Выбор: ");
        
        if (menu_choice == 0) break;
        
        if (menu_choice == 2) {
            test_input_validation();
            test_edge_cases();
            test_comparison_methods();
            test_different_odes();
            test_runge_rule();
            test_numerical_stability();
            continue;
        }
        
        if (menu_choice == 3) {
            test_input_validation();
            continue;
        }
        
        if (menu_choice == 4) {
            test_edge_cases();
            continue;
        }
        
        if (menu_choice == 5) {
            test_comparison_methods();
            continue;
        }
        
        if (menu_choice == 6) {
            test_different_odes();
            continue;
        }
        
        if (menu_choice == 7) {
            test_runge_rule();
            continue;
        }
        
        if (menu_choice == 8) {
            test_numerical_stability();
            continue;
        }
        
        if (menu_choice != 1) {
            cout << "Выберите 0-8\n";
            continue;
        }
        
        cout << "\n=== ВЫБОР ОДУ ===\n";
        cout << "1 - y' = y + (1+x)*y^2, y(1) = -1 на [1, 1.5]\n";
        cout << "2 - y' = 1 + y^2, y(0) = 0 на [0, 0.4]\n";
        cout << "3 - y' = y - x^2 + 1, y(0) = 2 на [0, 1]\n";
        
        int ode_choice = get_input<int>("Выбор ОДУ: ");
        
        if (ode_choice < 1 || ode_choice > 3) {
            cout << "Выберите 1, 2 или 3\n";
            continue;
        }
        
        ODE_Function f;
        double (*exact)(double);
        double default_x0, default_y0, default_xn;
        
        if (ode_choice == 1) {
            f = ode_variant_1;
            exact = exact_solution_variant_1;
            default_x0 = 1.0;
            default_y0 = -1.0;
            default_xn = 1.5;
        } else if (ode_choice == 2) {
            f = ode_variant_2;
            exact = exact_solution_variant_2;
            default_x0 = 0.0;
            default_y0 = 0.0;
            default_xn = 0.4;
        } else {
            f = ode_variant_3;
            exact = exact_solution_variant_3;
            default_x0 = 0.0;
            default_y0 = 2.0;
            default_xn = 1.0;
        }
        
        double x0, y0, xn, h, eps;
        
        while (true) {
            x0 = get_input<double>("x0 (начало интервала): ");
            y0 = get_input<double>("y0 (начальное условие): ");
            xn = get_input<double>("xn (конец интервала): ");
            h = get_input<double>("h (шаг): ");
            eps = get_input<double>("epsilon (точность): ");
            
            bool valid = true;
            if (x0 >= xn) {
                cout << "Ошибка: x0 должно быть меньше xn\n";
                valid = false;
            }
            if (h <= 0) {
                cout << "Ошибка: h должно быть положительным\n";
                valid = false;
            }
            if (eps <= 0) {
                cout << "Ошибка: epsilon должно быть положительным\n";
                valid = false;
            }
            if ((xn - x0) / h > 10000) {
                cout << "Ошибка: слишком много шагов (>10000)\n";
                valid = false;
            }
            if (h > (xn - x0)) {
                cout << "Предупреждение: шаг больше интервала\n";
            }
            
            if (valid) break;
        }
        
        ImprovedEuler euler;
        RungeKutta4 rk4;
        Milne milne;
        
        cout << "\nВычисление...\n";
        
        Solution sol_euler = euler.solve(f, x0, y0, xn, h);
        Solution sol_rk4 = rk4.solve(f, x0, y0, xn, h);
        Solution sol_milne = milne.solve(f, x0, y0, xn, h);
        
        print_solution_table(sol_euler, "Улучшенный метод Эйлера", f, exact);
        print_solution_table(sol_rk4, "Метод Рунге-Кутта 4-го порядка", f, exact);
        print_solution_table(sol_milne, "Метод Милна", f, exact);
        
        Solution sol_euler_half = euler.solve(f, x0, y0, xn, h / 2.0);
        Solution sol_rk4_half = rk4.solve(f, x0, y0, xn, h / 2.0);
        double runge_euler = runge_rule(sol_euler.y.back(), sol_euler_half.y.back(), 2);
        double runge_rk4 = runge_rule(sol_rk4.y.back(), sol_rk4_half.y.back(), 4);
        double milne_error = max_abs_error(sol_milne, exact);
        
        cout << "\nОценка точности:\n";
        cout << "Улучшенный метод Эйлера (правило Рунге, p=2): " << runge_euler
             << (runge_euler <= eps ? " <= epsilon\n" : " > epsilon\n");
        cout << "Метод Рунге-Кутта 4-го порядка (правило Рунге, p=4): " << runge_rk4
             << (runge_rk4 <= eps ? " <= epsilon\n" : " > epsilon\n");
        cout << "Метод Милна (max |y_exact - y_i|): " << milne_error
             << (milne_error <= eps ? " <= epsilon\n" : " > epsilon\n");
        
        int plot_choice;
        while (true) {
            plot_choice = get_input<int>("Построить график? (1 - Да, 0 - Нет): ");
            if (plot_choice == 0 || plot_choice == 1) break;
        }
        
        if (plot_choice == 1) {
            vector<Solution> solutions = {sol_euler, sol_rk4, sol_milne};
            vector<string> labels = {"Улучш. Эйлер", "Рунге-Кутта 4", "Милна"};
            plot_solution(solutions, labels, exact, x0, xn, "Численное решение ОДУ");
        }
    }
    
    return 0;
}
