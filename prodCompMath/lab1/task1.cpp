#include <bits/stdc++.h>

using namespace std;

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    ifstream inputFile("input.txt");
    ofstream outputFile("output.txt");

    vector<double> arr;
    double s;
    while (inputFile >> s) {
        arr.push_back(s);
    }
    int k = (int) arr.size() / 2;
    double res = 0;
    for (int i = 0; i < k; i++) {
        res += arr[i] * arr[k + i];
    }
    outputFile << res;
    return 0;
}