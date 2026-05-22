#include <iostream>
#include <vector>
#include <cmath>
#include <iomanip>
#include <cassert>

using namespace std;

struct Solution {
    vector<double> x;
    vector<double> y;
};

typedef double (*ODE_Function)(double x, double y);

double ode_variant_1(double x, double y) {
    return y + (1.0 + x) * y * y;
}

double ode_variant_2(double x, double y) {
    return x + y * y;
}

double ode_variant_3(double x, double y) {
    return y - x * x + 1;
}

class ImprovedEuler {
public:
    Solution solve(ODE_Function f, double x0, double y0, double xn, double h) {
        Solution sol;
        int n = static_cast<int>((xn - x0) / h) + 1;
        
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

class RungeKutta4 {
public:
    Solution solve(ODE_Function f, double x0, double y0, double xn, double h) {
        Solution sol;
        int n = static_cast<int>((xn - x0) / h) + 1;
        
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

class Milne {
private:
    RungeKutta4 rk4;
    
public:
    Solution solve(ODE_Function f, double x0, double y0, double xn, double h) {
        Solution sol;
        int n = static_cast<int>((xn - x0) / h) + 1;
        
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
            
            double y_pred = sol.y[i - 4] + (4.0 * h / 3.0) * (2.0 * f_vals[i - 3] - f_vals[i - 2] + 2.0 * f_vals[i - 1]);
            
            double f_pred = f(x_i + h, y_pred);
            
            double y_corr = sol.y[i - 2] + (h / 3.0) * (f_vals[i - 2] + 4.0 * f_vals[i - 1] + f_pred);
            
            sol.x.push_back(x_i + h);
            sol.y.push_back(y_corr);
            
            f_vals.erase(f_vals.begin());
            f_vals.push_back(f(sol.x.back(), y_corr));
        }
        
        return sol;
    }
};

void test_single_step() {
    cout << "Тест: решение с одним шагом\n";
    ImprovedEuler euler;
    Solution sol = euler.solve(ode_variant_1, 1.0, -1.0, 1.1, 0.1);
    assert(sol.x.size() == 2);
    assert(abs(sol.x[0] - 1.0) < 1e-10);
    assert(abs(sol.x[1] - 1.1) < 1e-10);
    assert(abs(sol.y[0] - (-1.0)) < 1e-10);
    cout << "✓ Пройден\n";
}

void test_very_small_step() {
    cout << "Тест: очень малый шаг (h=0.001)\n";
    ImprovedEuler euler;
    Solution sol = euler.solve(ode_variant_1, 1.0, -1.0, 1.05, 0.001);
    assert(sol.x.size() > 40);
    assert(abs(sol.x.back() - 1.05) < 0.01);
    cout << "✓ Пройден, количество точек: " << sol.x.size() << "\n";
}

void test_zero_initial_condition() {
    cout << "Тест: нулевое начальное условие\n";
    ImprovedEuler euler;
    Solution sol = euler.solve(ode_variant_2, 0.0, 0.0, 0.3, 0.1);
    assert(sol.y[0] == 0.0);
    assert(abs(sol.y.back()) < 0.01);
    cout << "✓ Пройден, конечное y = " << sol.y.back() << "\n";
}

void test_negative_initial_condition() {
    cout << "Тест: отрицательное начальное условие\n";
    ImprovedEuler euler;
    Solution sol = euler.solve(ode_variant_3, 0.0, -2.0, 0.5, 0.1);
    assert(sol.y[0] == -2.0);
    assert(!isnan(sol.y.back()));
    cout << "✓ Пройден, конечное значение: " << sol.y.back() << "\n";
}

void test_large_initial_condition() {
    cout << "Тест: большое начальное условие\n";
    ImprovedEuler euler;
    Solution sol = euler.solve(ode_variant_3, 0.0, 100.0, 0.5, 0.1);
    assert(sol.y[0] == 100.0);
    assert(!isnan(sol.y.back()));
    cout << "✓ Пройден\n";
}

void test_convergence() {
    cout << "Тест: сходимость при уменьшении шага\n";
    ImprovedEuler euler;
    
    Solution sol_h_01 = euler.solve(ode_variant_1, 1.0, -1.0, 1.5, 0.1);
    Solution sol_h_005 = euler.solve(ode_variant_1, 1.0, -1.0, 1.5, 0.05);
    Solution sol_h_001 = euler.solve(ode_variant_1, 1.0, -1.0, 1.5, 0.01);
    
    double error_01 = abs(sol_h_01.y.back() - sol_h_001.y.back());
    double error_005 = abs(sol_h_005.y.back() - sol_h_001.y.back());
    
    assert(error_005 < error_01);
    cout << "✓ Пройден: уменьшение ошибки при меньшем шаге подтверждено\n";
    cout << "  Ошибка (h=0.1): " << fixed << setprecision(8) << error_01 << "\n";
    cout << "  Ошибка (h=0.05): " << error_005 << "\n";
}

void test_method_comparison() {
    cout << "Тест: сравнение методов\n";
    ImprovedEuler euler;
    RungeKutta4 rk4;
    Milne milne;
    
    double x0 = 1.0, y0 = -1.0, xn = 1.5, h = 0.1;
    
    Solution sol_euler = euler.solve(ode_variant_1, x0, y0, xn, h);
    Solution sol_rk4 = rk4.solve(ode_variant_1, x0, y0, xn, h);
    Solution sol_milne = milne.solve(ode_variant_1, x0, y0, xn, h);
    
    assert(sol_euler.x.size() == sol_rk4.x.size());
    assert(sol_rk4.x.size() == sol_milne.x.size());
    
    cout << "✓ Пройден: все методы дают одинаковое количество точек\n";
    cout << "  Количество точек: " << sol_euler.x.size() << "\n";
}

void test_different_odes() {
    cout << "Тест: решение разных ОДУ\n";
    ImprovedEuler euler;
    
    Solution sol1 = euler.solve(ode_variant_1, 1.0, -1.0, 1.5, 0.1);
    Solution sol2 = euler.solve(ode_variant_2, 0.0, 0.0, 0.4, 0.1);
    Solution sol3 = euler.solve(ode_variant_3, 0.0, 2.0, 1.0, 0.1);
    
    assert(sol1.x.size() > 0);
    assert(sol2.x.size() > 0);
    assert(sol3.x.size() > 0);
    
    assert(!isnan(sol1.y.back()));
    assert(!isnan(sol2.y.back()));
    assert(!isnan(sol3.y.back()));
    
    cout << "✓ Пройден: все ОДУ решены успешно\n";
}

void test_endpoint_accuracy() {
    cout << "Тест: совпадение конечной точки\n";
    ImprovedEuler euler;
    
    double xn = 1.5;
    Solution sol = euler.solve(ode_variant_1, 1.0, -1.0, xn, 0.1);
    
    double diff = abs(sol.x.back() - xn);
    assert(diff < 0.01);
    
    cout << "✓ Пройден: конечная точка совпадает в пределах шага\n";
    cout << "  Ожидаемо: " << xn << ", получено: " << sol.x.back() << "\n";
}

void test_monotonicity() {
    cout << "Тест: возрастание x координат\n";
    ImprovedEuler euler;
    
    Solution sol = euler.solve(ode_variant_1, 1.0, -1.0, 1.5, 0.1);
    
    for (size_t i = 1; i < sol.x.size(); i++) {
        assert(sol.x[i] > sol.x[i-1]);
    }
    
    cout << "✓ Пройден: x координаты строго возрастают\n";
}

void test_extreme_step_values() {
    cout << "Тест: экстремальные значения шага\n";
    ImprovedEuler euler;
    
    cout << "  Проверка очень маленького шага (0.0001)...\n";
    Solution sol_small = euler.solve(ode_variant_1, 1.0, -1.0, 1.01, 0.0001);
    assert(sol_small.x.size() > 50);
    cout << "  ✓ Успешно\n";
    
    cout << "  Проверка шага близкого к интервалу (0.49)...\n";
    Solution sol_large = euler.solve(ode_variant_1, 1.0, -1.0, 1.5, 0.49);
    assert(sol_large.x.size() == 2);
    cout << "  ✓ Успешно\n";
}

int main() {
    cout << "=== РАСШИРЕННЫЕ ТЕСТЫ ЧИСЛЕННЫХ МЕТОДОВ ===\n\n";
    
    try {
        test_single_step();
        test_very_small_step();
        test_zero_initial_condition();
        test_negative_initial_condition();
        test_large_initial_condition();
        test_convergence();
        test_method_comparison();
        test_different_odes();
        test_endpoint_accuracy();
        test_monotonicity();
        test_extreme_step_values();
        
        cout << "\n=== ВСЕ ТЕСТЫ ПРОЙДЕНЫ ===\n";
        return 0;
    } catch (const exception& e) {
        cout << "\n✗ Тест не пройден: " << e.what() << "\n";
        return 1;
    }
}
