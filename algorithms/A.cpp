#include <bits/stdc++.h>

using namespace std;

int main() {
  ios_base::sync_with_stdio(false);
  cin.tie(nullptr);

  int n;
  cin >> n;

  int l = 0;
  int res = 2;
  int x = 0;
  int y = 1;

  vector<int> arr(n);

  for (int i = 0; i < n; i++) {
    cin >> arr[i];
  }

  if (n == 2) {
    cout << "1 2";
    return 0;
  }

  if (n == 1) {
    cout << "1 1";
    return 0;
  }

  for (int r = 2; r < n; r++) {
    if (arr[r] == arr[r - 1] && arr[r - 1] == arr[r - 2]) {
      l = r - 1;
    }
    if (r - l + 1 > res) {
      res = r - l + 1;
      x = l;
      y = r;
    }
  }

  cout << x + 1 << " " << y + 1;
  return 0;
}
