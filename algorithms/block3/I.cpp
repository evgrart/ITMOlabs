#include <bits/stdc++.h>

using namespace std;

int main() {
  ios_base::sync_with_stdio(false);
  cin.tie(nullptr);

  int n, k, p;
  cin >> n >> k >> p;

  vector<int> a(p);
  vector<int> b(n + 1);
  vector<int> m(p);

  for (int i = 0; i < p; ++i) {
    cin >> m[i];
  }

  for (int i = 0; i <= n; ++i) {
    b[i] = 5800001;
  }

  for (int i = p - 1; i >= 0; --i) {
    int c = m[i];
    a[i] = b[c];
    b[c] = i;
  }

  set<pair<int, int>> s;
  vector<bool> flags(n + 1);

  for (int i = 0; i <= n; ++i) {
    flags[i] = true;
  }

  int count = 0;
  for (int i = 0; i < p; ++i) {
    if ((int)s.size() < k && flags[m[i]]) {
      s.insert(make_pair(-a[i], m[i]));
      flags[m[i]] = false;
      count++;
    } else if (!flags[m[i]]) {
      s.erase({-i, m[i]});
      s.insert({-a[i], m[i]});
    } else if ((int)s.size() == k && flags[m[i]]) {
      count++;
      flags[s.begin()->second] = true;
      flags[m[i]] = false;
      s.erase(s.begin());
      s.insert(make_pair(-a[i], m[i]));
    }
  }

  cout << count;
  return 0;
}

