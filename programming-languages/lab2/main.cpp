#include <bits/stdc++.h>

using namespace std;

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    int r, s, a, b;
    cin >> r >> s >> a >> b;

    if (s > r) {
        swap(s, r);
    }

    long long int arr[r][s];

    for (int i = 0; i < r; i++) {
        for (int j = 0; j < s; j++) {
            cin >> arr[i][j];
        }
    }

    long long int prefs[r + 1][s + 1];

    for (int i = 0; i <= s; i++) {
        prefs[0][i] = 0;
    }

    for (int j = 0; j <= r; j++) {
        prefs[j][0] = 0;
    }

    for (int i = 1; i <= r; i++) {
        for (int j = 1; j <= s; j++) {
            prefs[i][j] = prefs[i - 1][j] + prefs[i][j - 1] - prefs[i - 1][j - 1] + arr[i - 1][j - 1];
        }
    }

    if (b > a) {
        swap(a, b);
    }
    long long int m = 100000000000;
    set<long long int> se;
    for (int i1 = 1; i1 <= r; i1++) {
        for (int i2 = 0; i2 <= i1; i2++) {
            long long int c[s + 1];
            c[0] = 0;
            se.clear();
            se.insert(0);
            for (int j = 1; j <= s; j++) {
                c[j] = prefs[i1][j] - prefs[i2][j] - prefs[i1][j-1] + prefs[i2][j-1] + c[j - 1];
                se.insert(c[j]); 
                auto k = se.lower_bound(c[j] - b);
                auto p = se.lower_bound(c[j] - b + 1);
                long long int sum1 = (k != se.end()) ? c[j] - *k : 100000000000;
                long long int sum2 = (p != se.end()) ? c[j] - *p : 100000000000;
                m = min({m, abs(a - sum1) + abs(b - sum1), abs(b - sum2) + abs(a - sum2)});
            }
        }
    } 
    cout << m;
    return 0;
}