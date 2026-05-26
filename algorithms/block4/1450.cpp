#include <bits/stdc++.h>

using namespace std;

int main() {
  ios::sync_with_stdio(false);
  cin.tie(nullptr);

  int n, m;
  cin >> n >> m;

  vector<vector<pair<int, int>>> g(n + 1);
  vector<int> deg(n + 1, 0);

  for (int i = 0; i < m; i++) {
    int a, b, c;
    cin >> a >> b >> c;
    g[a].push_back({b, c});
    deg[b]++;
  }

  int s, f;
  cin >> s >> f;

  queue<int> q;
  for (int i = 1; i <= n; i++) {
    if (deg[i] == 0) {
      q.push(i);
    }
  }

  vector<int> order;
  while (!q.empty()) {
    int v = q.front();
    q.pop();
    order.push_back(v);

    for (auto x : g[v]) {
      int to = x.first;
      deg[to]--;
      if (deg[to] == 0) {
        q.push(to);
      }
    }
  }

  long long bad = -(long long)4e18;
  vector<long long> d(n + 1, bad);
  d[s] = 0;

  for (int v : order) {
    if (d[v] == bad) {
      continue;
    }

    for (auto x : g[v]) {
      int to = x.first;
      int cost = x.second;
      d[to] = max(d[to], d[v] + cost);
    }
  }

  if (d[f] == bad) {
    cout << "No solution\n";
  } else {
    cout << d[f] << '\n';
  }

  return 0;
}
