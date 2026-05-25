#include <bits/stdc++.h>

using namespace std;

bool flag = true;

void dfs(int i, vector<vector<int>>& v, vector<int>& mark, int col) {
  mark[i] = col;
  for (int j : v[i]) {
    if (mark[j] == 0) {
      dfs(j, v, mark, -col);
    } else if (mark[j] == col) {
      flag = false;
      return;
    }
  }
}

int main() {
  ios_base::sync_with_stdio(false);
  cin.tie(nullptr);

  int n, m;
  cin >> n >> m;

  vector<vector<int>> v(n + 1);
  int x, y;

  for (int i = 0; i < m; i++) {
    cin >> x >> y;
    v[x].push_back(y);
    v[y].push_back(x);
  }

  vector<int> mark(n + 1, 0);

  for (int i = 1; i <= n; i++) {
    if (mark[i] == 0) {
      dfs(i, v, mark, 1);
    }
  }

  if (flag) {
    cout << "YES";
  } else {
    cout << "NO";
  }

  return 0;
}
