#include <bits/stdc++.h>
#include <lapacke.h> 

using namespace std;

struct Data {
    int n;
    int m;
    vector<vector<long double>> matrix;
};

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    ifstream inputFile("input.txt");
    ofstream outputFile("output.txt");

    int n, m;
    inputFile >> n >> m;
    Data data;
    data.n = n;
    data.m = m;
    data.matrix.resize(n, vector<long double>(m));

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            inputFile >> data.matrix[i][j];
        }
    }

    vector<double> A;

    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            A.push_back(data.matrix[j][i]);
        }
    }

    std::vector<double> U(n * n, 0.0);  
    std::vector<double> VT(m * m, 0.0); 
    std::vector<double> S(n, 0.0);      

    int info = LAPACKE_dgesdd(LAPACK_COL_MAJOR, 'A', n, m, A.data(), n,
                               S.data(), U.data(), n, VT.data(), m);

    if (info == 0) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                outputFile << U[j * n + i] << " "; 
            }
            outputFile << "\n";
        }
        outputFile << "\n";
        for (auto val : S) {
            outputFile << val << " ";
        }
        outputFile << "\n";
        outputFile << "\n";
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                outputFile << VT[j * m + i] << " ";
            }
            outputFile << "\n";
        }
    }
    return 0;
}

/*
2 2
1 0
0 2
*/