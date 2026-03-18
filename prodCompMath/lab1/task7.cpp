#include <bits/stdc++.h>

using namespace std;

struct Data {
    int n;
    vector<vector<long double>> matrix;
    vector<long double> vector_b;
};

void gauss(int n, vector<vector<long double>>& A_orig, vector<long double>& b_orig, ostream& out) {
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
            return; 
        }
        if (maxRow != k) {
            swap_count++;
        }
        swap(A[k], A[maxRow]);
        swap(b[k], b[maxRow]);

        for (int i = k + 1; i < n; i++) { 
            long double f = A[i][k] / A[k][k];
            A[i][k] = 0; 
            for (int j = k + 1; j < n; j++) {
                A[i][j] -= f * A[k][j];
            }
            b[i] -= f * b[k];
        }
    }

    long double det = 1.0;
    for (int i = 0; i < n; i++) {
        det *= A[i][i];
    }

    if (abs(det) < 1e-9) {
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

    cout << setprecision(6);
    for (int i = 0; i < n; i++) {
        out << x[i] << " ";
    }
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

    gauss(data.n, data.matrix, data.vector_b, outputFile);

    return 0;
}