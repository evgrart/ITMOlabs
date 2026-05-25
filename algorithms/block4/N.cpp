#include <bits/stdc++.h>

using namespace std;

int c = 0;

void dfs(int i, vector<vector<int>>& v, vector<int>& mark) {
  mark[i] = 1;
  for (int j : v[i]) {
    if (mark[j] == -1) {
      dfs(j, v, mark);
    } else if (mark[j] == 1) {
      c++;
    }
  }
  mark[i] = 2;
}

int main() {
  ios_base::sync_with_stdio(false);
  cin.tie(nullptr);

  int n;
  cin >> n;
  vector<vector<int>> v(n + 1);
  int x;

  for (int i = 1; i <= n; i++) {
    cin >> x;
    v[x].push_back(i);
  }

  vector<int> mark(n + 1, -1);

  for (int i = 1; i <= n; i++) {
    if (mark[i] == -1) {
      dfs(i, v, mark);
    }
  }

  cout << c;
  return 0;
}
