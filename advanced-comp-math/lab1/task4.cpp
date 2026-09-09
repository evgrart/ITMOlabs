#include <bits/stdc++.h>

using namespace std;
// m \times n и m >= n
vector<vector<long double>> q; 
vector<vector<long double>> r;


struct Data {
    int n;
    int m;
    vector<vector<long double>> matrix;
    vector<long double> vector_b;
};

void qrdecomp(Data& A) {
    for (int i = 0; i < A.n; i++) {
        vector<long double> p;
        long double norm = 0;
        for (int j = 0; j < A.m; j++) {
            p.push_back(A.matrix[j][i]);
        }
        for (int k = 0; k < i; k++) {
            long double prod = 0;
            for (int x = 0; x < A.m; x++) {
                prod += A.matrix[x][i] * q[k][x];
            }
            r[k][i] = prod;
            for (int j = 0; j < A.m; j++) {
                p[j] -= prod * q[k][j];
            }
        }

        for (int j = 0; j < A.m; j++) {
            norm += p[j] * p[j];
        }
        norm = sqrt(norm);
        r[i][i] = norm;
        for (int j = 0; j < A.m; j++) {
            p[j] /= norm;
        }
        q[i] = p;
    }
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    ifstream inputFile("input.txt");
    ofstream outputFile("output.txt");

    int n, m;
    inputFile >> m >> n;
    Data data;
    data.n = n;
    data.m = m;
    data.matrix.resize(m, vector<long double>(n));
    q.resize(n);
    r.resize(n, vector<long double>(n, 0));

    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            inputFile >> data.matrix[i][j];
        }
    }
    qrdecomp(data);

    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            outputFile << q[j][i] << " ";
        }
        outputFile << "\n";
    }
    outputFile << "\n";
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            outputFile << r[i][j] << " ";
        }
        outputFile << "\n";
    }
    outputFile << "\n";
    int rank = 0;
    for (int i = 0; i < n; i++) {
        bool flag = false;
        for (int j = i; j < n; j++) {
            if (abs(r[i][j]) > 1e-9) {
                flag = true;
                break;
            }
        }
        if (flag) {
            rank++;
        }
    }
    outputFile << rank;
    return 0;
}    

/*
3 2
1 2
3 4
5 6
*/