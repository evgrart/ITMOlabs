#include <bits/stdc++.h>

using namespace std;

struct Data {
    int n;
    vector<vector<long double>> matrix;
    vector<long double> vector_b;
};

bool isInteger(string& s) {
    if (s.empty()) return false;
    for (char c : s) {
        if (!isdigit(c)) return false;
    }
    return true;
}

Data getData() {
    Data data;
    long double choice; 
    cout << "Выберите способ ввода данных:\n";
    cout << "1. Клавиатура\n";
    cout << "2. Файл\n";
    cout << "Ваш выбор: ";
    cin >> choice;

    if (choice == 1) {
        string input_n;
        while (true) {
            cout << "Введите размерность матрицы n (<= 20): ";
            cin >> input_n;
            
            if (isInteger(input_n)) {
                data.n = stoi(input_n);
                if (data.n > 0 && data.n <= 20) break;
            }
            cout << "Размерность должна быть целым числом > 0 и <= 20\n";
        }

        cout << "Введите коэффициенты матрицы построчно:\n";
        data.matrix.resize(data.n, vector<long double>(data.n));
        for (int i = 0; i < data.n; i++) {
            cout << "Строка " << i + 1 << ": ";
            for (int j = 0; j < data.n; j++) {
                while (!(cin >> data.matrix[i][j])) { 
                    cin.clear();
                    cin.ignore(numeric_limits<streamsize>::max(), '\n'); 
                    cout << "Введите числовое значение: ";
                }
            }
        }

        cout << "Введите вектор свободных членов: \n";
        data.vector_b.resize(data.n);
        for (int i = 0; i < data.n; i++) {
            while (!(cin >> data.vector_b[i])) {
                cin.clear();
                cin.ignore(numeric_limits<streamsize>::max(), '\n'); 
                cout << "Введите числовое значение: ";
            }
        }

    } else if (choice == 2) {
        string filename;
        cout << "Введите имя файла: ";
        cin >> filename;

        ifstream fin(filename); // make fin
        if (!fin.is_open()) {
            cerr << "Файл не найден\n";
            exit(1);
        }

        if (!(fin >> data.n) || data.n > 20 || data.n <= 0) {
            cerr << "Неверная размерность в файле\n";
            exit(1);
        }

        data.matrix.resize(data.n, vector<long double>(data.n));
        data.vector_b.resize(data.n);
        
        for (int i = 0; i < data.n; i++) {
            for (int j = 0; j < data.n; j++)  {
                fin >> data.matrix[i][j];
            }
        }
        for (int i = 0; i < data.n; i++) {
            fin >> data.vector_b[i];
        } 
        
    } else {
        cerr << "Неверный выбор\n";
        exit(0);
    }

    return data;
}

void gaussian(int n, vector<vector<long double>>& A_orig, vector<long double>& b_orig) {
    vector<vector<long double>> A = A_orig;
    vector<long double> b = b_orig;
    int swap_count = 0;
    
    for (int k = 0; k < n; k++) {
        int maxRow = k;
        for (int i = k + 1; i < n; i++) {
            if (abs(A[i][k]) > abs(A[maxRow][k])) {
                maxRow = i;
            }
        }

        if (abs(A[maxRow][k]) < 1e-9) {
            cout << "Определитель матрицы равен 0\nМатрица вырождена\n";
            return; 
        }
        if (maxRow != k) {
            swap_count++;
        }
        swap(A[k], A[maxRow]);
        swap(b[k], b[maxRow]);

        for (int i = k + 1; i < n; i++) { // triangular
            long double f = A[i][k] / A[k][k];
            A[i][k] = 0; 
            for (int j = k + 1; j < n; j++) {
                A[i][j] -= f * A[k][j];
            }
            b[i] -= f * b[k];
        }
    }

    cout << "\nТреугольная матрица:\n";
    cout << fixed << setprecision(4);
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            cout << setw(10) << A[i][j] << " ";
        }
        cout << " | " << setw(10) << b[i] << "\n";
    }

    long double det = 1.0;
    for (int i = 0; i < n; i++) {
        det *= A[i][i];
    }
    det *= pow(-1, swap_count);
    cout << "\nОпределитель матрицы: " << det << "\n";

    if (abs(det) < 1e-9) {
        cout << "Определитель равен 0, нет решения\n";
        return;
    }


    vector<long double> x(n);
    for (int i = n - 1; i >= 0; i--) {
        long double sum = 0.0;
        for (int j = i + 1; j < n; j++) {
            sum += A[i][j] * x[j];
        }
        x[i] = (b[i] - sum) / A[i][i];
    }

    cout << "\nВектор неизвестных x:\n";
    cout << setprecision(6);
    for (int i = 0; i < n; i++) {
        cout << "x" << i+1 << " = " << x[i] << "\n";
    }

    vector<long double> r(n);
    cout << "\nВектор невязок r (Ax - b):\n";
    cout << scientific << setprecision(4);
    for (int i = 0; i < n; i++) {
        long double ax_i = 0.0;
        for (int j = 0; j < n; j++) {
            ax_i += A_orig[i][j] * x[j];
        }
        r[i] = ax_i - b_orig[i];
        cout << "r" << i+1 << " = " << r[i] << "\n";
    }
}


int main() {
    Data data = getData();

    cout << "\nИсходная матрица:" << endl;
    cout << fixed << setprecision(2);
    for (int i = 0; i < data.n; i++) {
        for (int j = 0; j < data.n; j++) {
            cout << setw(8) << data.matrix[i][j] << " ";
        }
        cout << "| " << data.vector_b[i] << endl;
    }

    gaussian(data.n, data.matrix, data.vector_b);

    return 0;
}
