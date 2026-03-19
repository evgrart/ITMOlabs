#include <bits/stdc++.h>

using namespace std;

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    ifstream inputFile("input.txt");
    ofstream outputFile("output.txt");

    string line;
    getline(inputFile, line); 

    istringstream iss(line);
    vector<int> dims;
    int val;
    long long int n = 1;
    while (iss >> val) {
        n *= val;
        dims.push_back(val);
    }
    vector<long double> v1;
    long double x;
    for (int i = 0; i < n; i++) {
        inputFile >> x;
        v1.push_back(x);
    }
    vector<long double> v2;
    for (int i = 0; i < n; i++) {
        inputFile >> x;
        v2.push_back(x);
    }
    int cols = dims.back();            
    int rows = n / cols;

    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            outputFile << v1[i * cols + j] + v2[i * cols + j] << " ";
        }
        outputFile << "\n";
    }

    return 0;
}    