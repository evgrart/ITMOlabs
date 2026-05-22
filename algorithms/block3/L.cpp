#include <bits/stdc++.h>

using namespace std;

void build(int* t, int x, int lx, int rx, int* arr) {
  if (rx - lx == 1) {
    t[x] = arr[lx];
    return;
  }
  int mx;
  mx = (rx + lx) / 2;
  build(t, 2 * x + 1, lx, mx, arr);
  build(t, 2 * x + 2, mx, rx, arr);
  t[x] = min(t[2 * x + 1], t[2 * x + 2]);
}

int getmin(int* t, int x, int lx, int rx, int l, int r) {
  if (lx >= l && rx <= r) {
    return t[x];
  }
  if (lx >= r || rx <= l) {
    return INT32_MAX;
  }
  int mx;
  mx = (lx + rx) / 2;
  int a;
  a = getmin(t, 2 * x + 1, lx, mx, l, r);
  int b;
  b = getmin(t, 2 * x + 2, mx, rx, l, r);
  return min(a, b);
}

int main() {
  ios_base::sync_with_stdio(false);
  cin.tie(nullptr);

  int n;
  int k;
  cin >> n >> k;
  vector<int> arr(n);
  for (int i = 0; i < n; i++) {
    cin >> arr[i];
  }
  int l;
  l = 1;
  while (l < n) {
    l *= 2;
  }
  vector<int> m(l);
  for (int i = 0; i < n; i++) {
    m[i] = arr[i];
  }
  for (int i = n; i < l; i++) {
    m[i] = INT32_MAX;
  }
  vector<int> tree(2 * l);
  build(tree.data(), 0, 0, l, m.data());

  for (int i = k - 1; i < n; i++) {
    cout << getmin(tree.data(), 0, 0, l, i - k + 1, i + 1) << " ";
  }
  return 0;
}
