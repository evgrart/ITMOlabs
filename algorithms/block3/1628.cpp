#include <bits/stdc++.h>

using namespace std;

long long encode(int x, int y, int n) {
  return 1LL * x * (n + 1) + y;
}

long long countStripes(map<int, vector<int>>& lines, int cnt, int len) {
  long long ans = 0;

  if (len >= 2) {
    ans += cnt - (int)lines.size();
  }

  for (auto& it : lines) {
    vector<int>& v = it.second;
    sort(v.begin(), v.end());

    int stripes = 0;
    int last = 0;

    for (int x : v) {
      if (x - last - 1 >= 2) {
        stripes++;
      }
      last = x;
    }

    if (len - last >= 2) {
      stripes++;
    }

    ans += stripes;
  }

  return ans;
}

int main() {
  ios::sync_with_stdio(false);
  cin.tie(nullptr);

  int m, n, k;
  cin >> m >> n >> k;

  map<int, vector<int>> rows;
  map<int, vector<int>> cols;
  unordered_set<long long> black;
  unordered_set<long long> cand;

  int dx[] = {-1, 1, 0, 0};
  int dy[] = {0, 0, -1, 1};

  for (int i = 0; i < k; ++i) {
    int x, y;
    cin >> x >> y;
    rows[x].push_back(y);
    cols[y].push_back(x);
    black.insert(encode(x, y, n));
  }

  for (auto code : black) {
    int x = static_cast<int>(code / (n + 1));
    int y = static_cast<int>(code % (n + 1));

    for (int d = 0; d < 4; ++d) {
      int nx = x + dx[d];
      int ny = y + dy[d];

      if (nx < 1 || nx > m || ny < 1 || ny > n) {
        continue;
      }

      long long cur = encode(nx, ny, n);
      if (!black.count(cur)) {
        cand.insert(cur);
      }
    }
  }

  if (m == 1 && n == 1 && k == 0) {
    cand.insert(encode(1, 1, n));
  }

  long long single = 0;
  for (auto code : cand) {
    int x = static_cast<int>(code / (n + 1));
    int y = static_cast<int>(code % (n + 1));
    bool ok = true;

    for (int d = 0; d < 4; ++d) {
      int nx = x + dx[d];
      int ny = y + dy[d];

      if (nx < 1 || nx > m || ny < 1 || ny > n) {
        continue;
      }

      if (!black.count(encode(nx, ny, n))) {
        ok = false;
        break;
      }
    }

    if (ok) {
      single++;
    }
  }

  long long ans = 0;
  ans += countStripes(rows, m, n);
  ans += countStripes(cols, n, m);
  ans += single;

  cout << ans << '\n';
  return 0;
}
