#include <bits/stdc++.h>
// LU-decomposition 
using namespace std;

vector<vector<long double>> l;
vector<vector<long double>> u;
vector<long double> y;
vector<long double> res;

struct Data {
    int n;
    vector<vector<long double>> matrix;
    vector<long double> vector_b;
};

void decomp(int n, vector<vector<long double>>& A, int step) {
    if (step == n) {
        return;
    }
    long double x = A[step][step];
    if (abs(x) < 1e-9) {
        return;
    }
    for (int i = step + 1; i < n; i++) {
        l[i][step] = A[i][step] / x;
    }
    for (int i = step; i < n; i++) {
        u[step][i] = A[step][i];
    }
    for (int i = step + 1; i < n; i++) {
        for (int j = step + 1; j < n; j++) {
            A[i][j] -= A[i][step] * A[step][j] / x;
        }
    }
    decomp(n, A, step + 1);
}

void subst(int n, vector<vector<long double>>& A, vector<long double>& b) {
    for (int i = 0; i < n; i++) {
        double s = b[i];
        if (abs(A[i][i]) < 1e-9) {
            return;
        } 
        for (int j = 0; j < i; j++) {
            s -= A[i][j] * y[j];
        }
        s /= A[i][i];
        y[i] = s;
    }
}

void back_subst(int n, vector<vector<long double>>& A, vector<long double>& b) {
    for (int i = n - 1; i >= 0; i--) {
        double s = b[i];
        if (abs(A[i][i]) < 1e-9) {
            return;
        } 
        for (int j = n - 1; j > i; j--) {
            s -= A[i][j] * res[j];
        }
        s /= A[i][i];
        res[i] = s;
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
    int n = data.n;
    l.resize(n, vector<long double>(n));
    u.resize(n, vector<long double>(n));
    y.resize(n);
    res.resize(n);
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            l[i][j] = 0;
            u[i][j] = 0;
        }
        l[i][i] = 1;
    }
    decomp(n, data.matrix, 0);
    subst(n, l, data.vector_b);
    back_subst(n, u, y);
    for (auto c : res) {
        outputFile << c << " ";
    }
    return 0;
}