#include <bits/stdc++.h>

using namespace std;

struct Data {
    int n;
    vector<vector<long double>> matrix;
    vector<long double> vector_b;
};

void jacobi(int n, vector<vector<long double>>& A, vector<long double>& b, ostream& out) {
    vector<long double> v1(n, 0);
    vector<long double> v2(n);
    long double s = 0;
    long double x;
    int count = 0;
    do {
        count++;
        s = 0;
        for (int i = 0; i < n; i++) {
            if (abs(A[i][i]) < 1e-9 || count > 1000000) {
                out << "Якоби расходится";
                return;
            }
            x = b[i];
            for (int j = 0; j < n; j++) {
                if (j == i) {
                    continue;
                }
                x -= A[i][j] * v1[j];
            }
            x /= A[i][i];
            v2[i] = x;
            s = max(s, abs(v2[i] - v1[i]));
        }
        v1 = v2;
    } while (s > 1e-5);
    long double r = 0;
    for (int i = 0; i < n; i++) {
        s = -b[i];
        for (int j = 0; j < n; j++) {
            s += A[i][j] * v2[j];
        }
        r += s * s;
    }
    for (auto c : v2) {
        out << c << " ";
    }
    out << "\n";
    out << "Итераций: " << count << "\n";
    out << "Невязка: " << r << "\n";
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    ifstream inputFile("input.txt");
    ofstream outputFile("output.txt");

    Data data;
    int k;
    inputFile >> k;
    data.n = k;
    data.matrix.resize(data.n, vector<long double>(data.n));
    data.vector_b.resize(data.n);
    for (int i = 0; i < k; i++) {
        for (int j = 0; j < k; j++) {
            inputFile >> data.matrix[i][j];
        }
        inputFile >> data.vector_b[i];
    }
    int n = data.n;
    jacobi(n, data.matrix, data.vector_b, outputFile);

    return 0;
}

/*
3
4 1 1 6
1 4 1 6
1 1 4 6

*/