#include <bits/stdc++.h>

using namespace std;

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    ifstream inputFile("input.txt");
    ofstream outputFile("output.txt");

    int n, m;
    inputFile >> n >> m;
    double m1[n][m];
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            inputFile >> m1[i][j];
        }
    }
    vector<double> v;
    double p;
    while (inputFile >> p) {
        v.push_back(p);
    }
    int k = v.size() / m;
    double m2[m][k];
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < k; j++) {
            m2[i][j] = v[i * k + j];
        }
    }
    double c = 0;
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < k; j++) {
            c = 0;
            for (int p = 0; p < m; p++) {
                c += m1[i][p] * m2[p][j];
            }
            outputFile << c << " ";
        }
        outputFile << "\n";
    }
    return 0;
}