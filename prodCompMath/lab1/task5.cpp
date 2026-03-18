#include <bits/stdc++.h>

using namespace std;

vector<vector<long double>> l;
vector<vector<long double>> u;
vector<long double> p;

struct Data {
    int n;
    vector<vector<long double>> matrix;
    vector<long double> vector_b;
};

void decomp(int n, vector<vector<long double>>& A, int step) {
    if (step == n) {
        return;
    }
    long double s = fabsl(A[step][step]);
    int k = step;
    for (int j = step + 1; j < n; j++) {
        if (s < fabsl(A[j][step])) {
            s = fabsl(A[j][step]);
            k = j;
        };
    }
    if (fabsl(s) < 1e-9) {
        return;
    }
    swap(A[step], A[k]);
    swap(p[step], p[k]);
    long double x = A[step][step];
    if (fabsl(x) < 1e-9) {
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
    }
    int n = data.n;
    l.resize(n, vector<long double>(n));
    u.resize(n, vector<long double>(n));
    p.resize(n);
    for (int i = 0; i < n; i++) {
        p[i] = i;
    }
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            l[i][j] = 0;
            u[i][j] = 0;
        }
        l[i][i] = 1;
    }

    decomp(n, data.matrix, 0);

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            outputFile << l[i][j] << " ";
        }
        outputFile << "\n";
    }
    outputFile << "\n"; 
    int rank = 0;
    for (int i = 0; i < n; i++) {
        bool flag = false;
        for (int j = 0; j < n; j++) {
            if (j >= i && fabsl(u[i][j]) > 1e-9) {
                flag = true;
            }
            outputFile << u[i][j] << " ";
        }
        if (flag) {
            rank++;
        }
        outputFile << "\n";
    }

    outputFile << "\n"; 

    for (auto i : p) {
        outputFile << i << " ";
    }
    
    outputFile << "\n"; 

    outputFile << rank;

    return 0;
}


/*
3
2 1 1
4 -6 0
-2 7 2
*/