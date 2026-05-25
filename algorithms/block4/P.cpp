#include <bits/stdc++.h>

using namespace std;

void dfs(int v, vector<vector<int>>& e, vector<bool>& mark, int n, int x) {
  mark[v] = true;
  for (int i = 0; i < n; i++) {
    if (i != v && e[v][i] <= x && !mark[i]) {
      dfs(i, e, mark, n, x);
    }
  }
}

int main() {
  ios_base::sync_with_stdio(false);
  cin.tie(nullptr);

  int n;
  cin >> n;

  int x;
  vector<vector<int>> e(n);
  vector<vector<int>> rev(n, vector<int>(n));
  vector<bool> mark1(n, false);
  vector<bool> mark2(n, false);

  int r = 0;

  for (int i = 0; i < n; i++) {
    for (int j = 0; j < n; j++) {
      cin >> x;
      r = max(r, x);
      e[i].push_back(x);
      rev[j][i] = x;
    }
  }

  int l = -1;

  while (l + 1 < r) {
    int m = l + (r - l) / 2;
    bool flag = true;

    dfs(0, e, mark1, n, m);
    dfs(0, rev, mark2, n, m);

    for (int i = 0; i < n; i++) {
      flag &= mark1[i];
      flag &= mark2[i];
      mark1[i] = false;
      mark2[i] = false;
    }

    if (flag) {
      r = m;
    } else {
      l = m;
    }
  }

  cout << r;
  return 0;
}
