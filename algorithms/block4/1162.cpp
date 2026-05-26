#include <bits/stdc++.h>

using namespace std;

struct Edge {
  int a, b;
  double r, c;
};

int main() {
  ios::sync_with_stdio(false);
  cin.tie(nullptr);

  int n, m, s;
  double v;
  cin >> n >> m >> s >> v;

  vector<Edge> e;

  for (int i = 0; i < m; i++) {
    int a, b;
    double rab, cab, rba, cba;
    cin >> a >> b >> rab >> cab >> rba >> cba;

    e.push_back({a, b, rab, cab});
    e.push_back({b, a, rba, cba});
  }

  vector<double> d(n + 1, 0.0);
  d[s] = v;

  for (int i = 0; i < n; i++) {
    bool ok = false;

    for (auto x : e) {
      if (d[x.a] > x.c) {
        double val = (d[x.a] - x.c) * x.r;
        if (val > d[x.b] + 1e-9) {
          d[x.b] = val;
          ok = true;
        }
      }
    }

    if (ok && i == n - 1) {
      cout << "YES\n";
      return 0;
    }
  }

  cout << "NO\n";
  return 0;
}
