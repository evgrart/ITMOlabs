#include <bits/stdc++.h>

using namespace std;

struct MathFunction {
    string name;
    function<double(double)> calc;
    bool has_singularity;
    double singularity_point;
    bool converges;
};

double f1(double x) { return -3 * pow(x, 3) - 5 * pow(x, 2) + 4 * x - 2; }
double f2(double x) { return 1.0 / sqrt(x); }
double f3(double x) { return 1.0 / (x - 1.0); }

vector<MathFunction> functions_db = {
    {"-3x^3 - 5x^2 + 4x - 2", f1, false, 0.0, true},
    {"1 / sqrt(x)", f2, true, 0.0, true},
    {"1 / (x - 1)", f3, true, 1.0, false}
};

double left_rectangles(function<double(double)> f, double a, double b, int n) {
    double h = (b - a) / n;
    double sum = 0;
    for (int i = 0; i < n; i++) {
        sum += f(a + i * h);
    }
    return h * sum;
}

double right_rectangles(function<double(double)> f, double a, double b, int n) {
    double h = (b - a) / n;
    double sum = 0;
    for (int i = 1; i <= n; i++) {
        sum += f(a + i * h);
    }
    return h * sum;
}

double middle_rectangles(function<double(double)> f, double a, double b, int n) {
    double h = (b - a) / n;
    double sum = 0;
    for (int i = 0; i < n; i++) {
        sum += f(a + i * h + h / 2.0);
    }
    return h * sum;
}

double trapezoidal(function<double(double)> f, double a, double b, int n) {
    double h = (b - a) / n;
    double sum = (f(a) + f(b)) / 2.0;
    for (int i = 1; i < n; i++) {
        sum += f(a + i * h);
    }
    return h * sum;
}

double simpson(function<double(double)> f, double a, double b, int n) {
    double h = (b - a) / n;
    double sum = f(a) + f(b);
    double sum_odd = 0;
    double sum_even = 0;
    for (int i = 1; i < n; i++) {
        if (i % 2 == 0) sum_even += f(a + i * h);
        else sum_odd += f(a + i * h);
    }
    return (h / 3.0) * (sum + 2 * sum_even + 4 * sum_odd);
}

struct RungeResult {
    double value;
    int n_final;
};

RungeResult integrate_with_runge(
    function<double(function<double(double)>, double, double, int)> method,
    function<double(double)> f, double a, double b, double eps, int k) 
{
    int n = 4;
    double I_prev = method(f, a, b, n);
    double I_curr;

    while (true) {
        n *= 2;
        I_curr = method(f, a, b, n);
        double error = abs(I_curr - I_prev) / (pow(2, k) - 1);
        if (error <= eps) {
            break;
        }
        I_prev = I_curr;
        if (n > 1000000) {
            break;
        }
    }
    return {I_curr, n};
}
template <typename T>
T get_input(const string& prompt) {
    T value;
    while (true) {
        cout << prompt;
        while (!(cin >> value)) {
            cin.clear();
            cin.ignore(numeric_limits<streamsize>::max(), '\n');
            cout << "Некорректный ввод. Повторите: ";
        }
        cin.clear();
        cin.ignore(numeric_limits<streamsize>::max(), '\n');
        return value;
    }
}

int main() {
    int func_choice;
    while (true) {
        cout << "Выберите функцию:\n";
        for (size_t i = 0; i < functions_db.size(); i++) {
            cout << i + 1 << ". " << functions_db[i].name << "\n";
        }
        func_choice = get_input<int>("Ввод: ");
        if (func_choice >= 1 && func_choice <= 3) break;
        cout << "Неверный выбор функции.\n";
    }
    
    MathFunction selected_func = functions_db[func_choice - 1];

    double a = get_input<double>("Введите a: ");
    double b = get_input<double>("Введите b: ");
    bool flag = false;
    if (a > b) {
        flag = true;
        swap(a, b);
    }

    double eps;
    while (true) {
        eps = get_input<double>("Введите точность eps: ");
        if (eps > 0) break;
        cout << "Точность должна быть больше 0\n";
    }

    vector<pair<double, double>> intervals; 
    
    if (selected_func.has_singularity && selected_func.singularity_point >= a && selected_func.singularity_point <= b) {
        if (!selected_func.converges || (func_choice == 2 && min(a, b) < 0)) {
            cout << "\nИнтеграл не существует\n";
            return 0;
        }
        
        double delta = 1e-5;
        double sp = selected_func.singularity_point;

        if (abs(a - sp) < 1e-9) { 
            intervals.push_back({a + delta, b});
        } 
        else if (abs(b - sp) < 1e-9) { 
            intervals.push_back({a, b - delta});
        } 
        else { 
            intervals.push_back({a, sp - delta});
            intervals.push_back({sp + delta, b});
        }
    } else {
        intervals.push_back({a, b});
    }

    int method_choice;
    while (true) {
        cout << "\nВыберите метод:\n";
        cout << "1. Левые прямоугольники\n";
        cout << "2. Правые прямоугольники\n";
        cout << "3. Средние прямоугольники\n";
        cout << "4. Трапеции\n";
        cout << "5. Симпсон\n";
        method_choice = get_input<int>("Ввод: ");
        if (method_choice >= 1 && method_choice <= 5) break;
        cout << "Неверный выбор метода.\n";
    }

    function<double(function<double(double)>, double, double, int)> numerical_method;
    int k_order = 2;

    switch (method_choice) {
        case 1: numerical_method = left_rectangles; break;
        case 2: numerical_method = right_rectangles; break;
        case 3: numerical_method = middle_rectangles; break;
        case 4: numerical_method = trapezoidal; break;
        case 5: numerical_method = simpson; k_order = 4; break;
    }

    double final_integral_value = 0;
    int max_n_used = 0;

    for (auto& interval : intervals) {
        RungeResult res = integrate_with_runge(
            numerical_method, selected_func.calc, 
            interval.first, interval.second, eps, k_order
        );
        final_integral_value += res.value;
        max_n_used = max(max_n_used, res.n_final);
    }
    if (flag) {
        final_integral_value *= -1;
    }
    cout << fixed << setprecision(6);
    cout << "\nЗначение интеграла: " << final_integral_value << "\n";
    cout << "Число разбиений (n): " << max_n_used << "\n\n";
    
    return 0;
}

