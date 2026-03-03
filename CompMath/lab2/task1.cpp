#include <bits/stdc++.h>
#include "matplotlibcpp.h"

using namespace std;
namespace plt = matplotlibcpp;

void plotFunction(double a, double b, const std::function<double(double)>& f, const std::string& func_name = "") {
    const int points = 1000;                 
    vector<double> x(points), y(points);
    double step = (b - a) / (points - 1);

    for (int i = 0; i < points; i++) {
        x[i] = a + i * step;
        y[i] = f(x[i]);
    }

    plt::plot(x, y);
    plt::title(func_name.empty() ? "График функции" : func_name);
    plt::xlabel("x");
    plt::ylabel("f(x)");
    plt::grid(true);
    plt::show();
}

struct Equation {
    function<double(double)> f;
    function<double(double)> df;
    string view;
};

function<double(double)> current_f;
function<double(double)> current_df;
double lambda_param = 0;


double f1(double x) {
    return -1.38 * pow(x, 3) - 5.42 * pow(x, 2) + 2.57 * x + 10.95;
}
double df1(double x) {
    return -4.14 * pow(x, 2) - 10.84 * x + 2.57;
}

double f2(double x) {
    return pow(x, 2) + sin(x) - 1;
}
double df2(double x) {
    return 2 * x + cos(x);
}

double f3(double x) {
    return exp(x) - 3 * x;
}
double df3(double x) {
    return exp(x) - 3;
}

double f(double x) {
    return current_f(x);
}
double df(double x) {
    return current_df(x);
}
double phi(double x) {
    return x + lambda_param * f(x);
}

void secantMethod(double a, double b, double epsilon, ostream& out) {
    double x_prev = a;
    double x_curr = b; 

    int step = 1;
    double x_next;
    double diff = b - a; 

    out << "--- Метод секущих ---" << "\n";
    out << setw(5) << "Step" 
              << setw(12) << "x_(k-1)" 
              << setw(12) << "x_k" 
              << setw(12) << "x_(k+1)" 
              << setw(12) << "f(x_(k+1))" 
              << setw(12) << "|diff|" << "\n";

    while (abs(diff) > epsilon) {        
        double f_curr = f(x_curr);
        double f_prev = f(x_prev);
        
        if (abs(f_curr - f_prev) < 1e-9) {
            out << "Деление на 0 => метод сошелся" << "\n";
            break;
        }

        x_next = x_curr - (x_curr - x_prev) / (f_curr - f_prev) * f_curr;
        
        diff = x_next - x_curr;
        out << setw(5) << step 
                  << setw(12) << fixed << setprecision(3) << x_prev 
                  << setw(12) << x_curr 
                  << setw(12) << x_next 
                  << setw(12) << f(x_next) 
                  << setw(12) << abs(diff) << "\n";

        x_prev = x_curr;
        x_curr = x_next;
        step++;
        
        if (step > 1000) {
            out << "Превышено число итераций\n";
            break;
        }
    }
    
    out << "Найденный корень: " << x_curr << "\n";
    out << "Значение функции: " << f(x_curr) << "\n";
    out << "Число итераций: " << step - 1 << "\n\n";
}

void bisectionMethod(double a, double b, double eps, ostream& out) {
    out << "--- Метод половинного деления (бисекции) ---\n";
    out << "Шаг |     a     |     b     |     x     |   f(a)    |   f(b)    |   f(x)    |  |b-a|  \n";
    out << "------------------------------------------------------------------------------\n";
    out << fixed << setprecision(6);

    double fa = f(a);
    double fb = f(b);

    int step = 0;
    double x;
    double fx;

    while ((b - a) > eps) {
        x = (a + b) / 2.0;
        fx = f(x);

        out << setw(3) << ++step << "  | "
            << setw(9) << a << " | "
            << setw(9) << b << " | "
            << setw(9) << x << " | "
            << setw(9) << fa << " | "
            << setw(9) << fb << " | "
            << setw(9) << fx << " | "
            << setw(9) << (b - a) << "\n";

        if (abs(fx) < eps) { \
            break;
        }

        if (fa * fx < 0) {
            b = x;
            fb = fx;
        } else {
            a = x;
            fa = fx;
        }

        if (step > 1000) {
            out << "Превышено число итераций.\n";
            break;
        }
    }

    x = (a + b) / 2.0;
    out << "------------------------------------------------------------------------------\n";
    out << "Найденный корень: " << x << "\n";
    out << "Значение функции: " << f(x) << "\n";
    out << "Число итераций: " << step << "\n\n";
}

void chordMethod(double a, double b, double eps, ostream& out) {
    out << "--- Метод хорд ---\n";
    out << "Step | a      | b      | x      | f(a)   | f(b)   | f(x)   | |x_next - x_prev|\n";
    out << "-----------------------------------------------------------------------------\n";
    out << fixed << setprecision(3);
    
    int step = 1;
    double x = (a + b) / 2;
    double x_prev = a; 
    
    double diff = eps + 1.0;

    while (diff > eps) {
        if (abs(f(b) - f(a)) < 1e-9) {
             out << "Деление на 0 => метод сошелся\n";
             break;
        }

        x = a - (f(a) * (b - a)) / (f(b) - f(a));
        
        double fa = f(a);
        double fb = f(b);
        double fx = f(x);
        
        if (step == 1) diff = abs(b - a); 
        else diff = abs(x - x_prev);

        out << step << "    | " 
             << a << " | " 
             << b << " | " 
             << x << " | " 
             << fa << "  | " 
             << fb << " | " 
             << fx << " | " 
             << diff << "\n";

        if (fa * fx < 0) {
            b = x;
        } else {
            a = x;
        }

        x_prev = x;
        step++;
        
        if (step > 1000) {
            out << "Превышено число итераций\n";
            break;
        }
    }
    out << "Найденный корень: " << x << "\n";
    out << "Значение функции: " << f(x) << "\n";
    out << "Число итераций: " << step - 1 << "\n\n";
}

void newtonMethod(double a, double b, double eps, ostream& out) {
    out << "--- Метод Ньютона ---\n";
    
    double fa = f(a);
    double fb = f(b);
    double x_k;

    if (abs(fa) < abs(fb))
        x_k = a;
    else
        x_k = b;
    
    out << "Начальное приближение x0 = " << x_k << " (выбрано как конец с меньшим |f(x)|)\n";
    out << "-------------------------------------------------------------------------------\n";
    out << "№ итер |     x_k     |    f(x_k)    |    f'(x_k)   |    x_{k+1}   | |x_{k+1}-x_k|\n";
    out << "-------------------------------------------------------------------------------\n";
    out << fixed << setprecision(6);

    int step = 0;
    double x_next;
    double diff;
    do {
        double f_k = f(x_k);
        double df_k = df(x_k);
        
        if (abs(df_k) < 1e-12) {
            out << "Метод не сходится.\n";
            return;
        }
        
        x_next = x_k - f_k / df_k;
        diff = abs(x_next - x_k);
        step++;
        
        out << setw(5) << step << "   | "
            << setw(11) << x_k << " | "
            << setw(11) << f_k << " | "
            << setw(11) << df_k << " | "
            << setw(11) << x_next << " | "
            << setw(12) << diff << "\n";
        
        if (diff < eps) break;
        
        x_k = x_next;
        
        if (step > 1000) {
            out << "Превышено число итераций.\n";
            break;
        }
        
    } while (true);
    
    out << "-------------------------------------------------------------------------------\n";
    out << "Найденный корень: " << x_next << "\n";
    out << "Значение функции: " << f(x_next) << "\n";
    out << "Число итераций: " << step << "\n\n";
}


void simpleIterationMethod(double a, double b, double eps, ostream& out) {
    out << "--- Метод простой итерации ---\n";
    
    double mid = (a + b) / 2.0;
    double max_df = max({abs(df(a)), abs(df(b)), abs(df(mid))}); // ?
    double sign = (df(mid) > 0) ? 1.0 : -1.0;
    lambda_param = -sign / max_df;

    // q = max |1 + lambda * f'(x)| \approx 0
    double q_a = abs(1 + lambda_param * df(a));
    double q_b = abs(1 + lambda_param * df(b));
    double q_mid = abs(1 + lambda_param * df(mid));
    double q = max({q_a, q_b, q_mid});

    out << "Интервал: [" << a << "; " << b << "]\n";
    out << "max|f'(x)| (approx): " << max_df << "\n";
    out << "Вычисленная lambda: " << lambda_param << "\n";
    out << "Коэффициент сжатия q: " << q << "\n";

    if (q >= 1.0) {
        out << "Условие сходимости не выполнено. Метод может разойтись.\n";
    }

    double stop_coef = 1.0;
    if (q > 0.5 && q < 1.0) {
        stop_coef = (1 - q) / q;
        out << "Так как q > 0.5, используем множитель (1-q)/q = " << stop_coef << "\n";
    } else if (q <= 0.5) {
        out << "Так как q <= 0.5, множитель равен 1.\n";
    }
    
    out << "-----------------------------------------------------------------------\n";
    out << "No | x_k      | x_k+1    | f(x_k+1)    | |x_k+1 - x_k|\n";
    out << "-----------------------------------------------------------------------\n";
    out << fixed << setprecision(5); 

    double x_k = (a + b) / 2.0; 
    double x_next;
    double diff;
    int step = 0;

    do {
        step++;
        x_next = phi(x_k);
        diff = abs(x_next - x_k);

        out << step << "  | " 
             << x_k << "  | " 
             << x_next << "  | " 
             << f(x_next) << "    | " 
             << diff << "\n";

        if (diff < eps * stop_coef) {
            break;
        }
        
        x_k = x_next;

        if (step > 1000) { // пусть будет так
            out << "Превышено число итераций\n";
            break;
        }
        
        // вылетит в бесконечность при расходимости 
        if (abs(x_next) > 1e10) {
            out << "Метод расходится.\n";
            return;
        }

    } while (true);
    
    out << "-----------------------------------------------------------------------\n";
    out << "Найденный корень: " << x_next << "\n";
    out << "Значение функции: " << f(x_next) << "\n";
    out << "Число итераций: " << step << "\n\n";
}

bool validateInterval(double a, double b, ostream& out) {
    if (a >= b) {
        out << "Некорректные границы интервала (a >= b).\n";
        return false;
    }
    
    double fa = f(a);
    double fb = f(b);
    
    if (fa * fb > 0) {
        out << "На концах интервала функция имеет одинаковые знаки.\n";
        return false;
    }
    
    return true;
}

int main() {
    vector<Equation> equations = {
        {f1, df1, "-1.38x^3 - 5.42x^2 + 2.57x + 10.95"},
        {f2, df2, "x^2 + sin(x) - 1"},
        {f3, df3, "e^x - 3x"}
    };

    int inputChoice;
    cout << "Выберите способ ввода данных:\n1. Консоль\n2. Файл (input.txt)\n> ";
    while(!(cin >> inputChoice) || inputChoice < 1 || inputChoice > 2) {
        cin.clear();
        cin.ignore(numeric_limits<streamsize>::max(), '\n');
        cout << "Введите 1 или 2\n";
        cout << "> ";
    };

    istream* in = &cin;
    ifstream inFile;
    if (inputChoice == 2) {
        inFile.open("input.txt");
        if (!inFile) {
            cerr << "Не удалось открыть файл input.txt. Переход на консольный ввод.\n";
            inputChoice = 1;
        } else {
            in = &inFile;
        }
    }

    int outputChoice;
    cout << "Выберите способ вывода результата:\n1. Консоль\n2. Файл (output.txt)\n> ";
    while(!(cin >> outputChoice) || outputChoice < 1 || outputChoice > 2) {
        cin.clear();
        cin.ignore(numeric_limits<streamsize>::max(), '\n');
        cout << "Введите 1 или 2\n";
        cout << "> ";
    };

    ostream* out = &cout;
    ofstream outFile;
    if (outputChoice == 2) {
        outFile.open("output.txt");
        if (!outFile) {
            cerr << "Не удалось открыть файл output.txt. Переход на консольный вывод.\n";
            outputChoice = 1;
        } else {
            out = &outFile;
        }
    }

    if (inputChoice == 1) {
        cout << "Выберите уравнение:\n";
        for (int i = 0; i < equations.size(); ++i) {
            cout << i + 1 << ". " << equations[i].view << "\n";
        }
        cout << "> ";
    }
    
    int eqIndex;    

    while (!(*in >> eqIndex) || eqIndex < 1 || eqIndex > equations.size()) {
        *out << "Неправильно набран номер, перезвоните позже\n";
        in->clear();
        in->ignore(numeric_limits<streamsize>::max(), '\n');
        *out << "> ";
    }
    
    current_f = equations[eqIndex - 1].f;
    current_df = equations[eqIndex - 1].df;

    if (inputChoice == 1) {
        cout << "Выберите метод:\n1. Метод хорд\n2. Метод секущих\n3. Метод простой итерации.\n4. Метод бисекций.\n5. Метод Ньютона\n> ";
    }
    int methodIndex;

    while (!(*in >> methodIndex) || methodIndex < 1 || methodIndex > 5) {
        *out << "Неправильно набран номер, перезвоните позже\n";
        in->clear();
        in->ignore(numeric_limits<streamsize>::max(), '\n');
        *out << "> ";
    }

    if (inputChoice == 1) cout << "Введите границы интервала a и b: ";
    double a, b;
    while (!(*in >> a) || !(*in >> b)) {
        *out << "Ну емае\n";
        in->clear();
        in->ignore(numeric_limits<streamsize>::max(), '\n');
        *out << "> ";
    }

    if (!validateInterval(a, b, *out)) {
        return 1;
    }
    in->clear();
    in->ignore(numeric_limits<streamsize>::max(), '\n');

    if (inputChoice == 1) cout << "Введите погрешность вычисления: ";
    double eps;
    while (!(*in >> eps)) {
        *out << "Ну емае\n";
        in->clear();
        in->ignore(numeric_limits<streamsize>::max(), '\n');
        *out << "> ";
    }

    *out << "Выбрано уравнение: " << equations[eqIndex - 1].view << "\n";
    *out << "Интервал: [" << a << ", " << b << "], Погрешность: " << eps << "\n\n";


    switch (methodIndex) {
        case 1:
            chordMethod(a, b, eps, *out);
            break;
        case 2:
            secantMethod(a, b, eps, *out);
            break;
        case 3:
            simpleIterationMethod(a, b, eps, *out);
            break;
        case 4:
            bisectionMethod(a, b, eps, *out);
        case 5:
            newtonMethod(a, b, eps, *out);
        default:
            return 1;
    }

    plotFunction(a, b, current_f, equations[eqIndex - 1].view);

    if (inputChoice == 2) inFile.close();
    if (outputChoice == 2) outFile.close();

    return 0;
}