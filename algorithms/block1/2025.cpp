#include <bits/stdc++.h>

using namespace std;
int arr[512][512];
int k = 1;

void solve(int n, int x0, int y0, int x, int y) {
  if (n == 0) {
    return;
  }
  
  int x1 = x0 + (1 << (n - 1));
  int y1 = y0 + (1 << (n - 1));
  int cur = k++;

  if (x < x1 && y < y1) {
    arr[x1 - 1][y1] = cur;
    arr[x1][y1 - 1] = cur;
    arr[x1][y1] = cur;
    solve(n - 1, x0, y0, x, y);
    solve(n - 1, x0, y1, x1 - 1, y1);
    solve(n - 1, x1, y0, x1, y1 - 1);
    solve(n - 1, x1, y1, x1, y1);
  } else if (x < x1 && y >= y1) {
    arr[x1 - 1][y1 - 1] = cur;
    arr[x1][y1 - 1] = cur;
    arr[x1][y1] = cur;
    solve(n - 1, x0, y0, x1 - 1, y1 - 1);
    solve(n - 1, x0, y1, x, y);
    solve(n - 1, x1, y0, x1, y1 - 1);
    solve(n - 1, x1, y1, x1, y1);
  } else if (x >= x1 && y < y1) {
    arr[x1 - 1][y1 - 1] = cur;
    arr[x1 - 1][y1] = cur;
    arr[x1][y1] = cur;
    solve(n - 1, x0, y0, x1 - 1, y1 - 1);
    solve(n - 1, x0, y1, x1 - 1, y1);
    solve(n - 1, x1, y0, x, y);
    solve(n - 1, x1, y1, x1, y1);
  } else {
    arr[x1 - 1][y1 - 1] = cur;
    arr[x1 - 1][y1] = cur;
    arr[x1][y1 - 1] = cur;
    solve(n - 1, x0, y0, x1 - 1, y1 - 1);
    solve(n - 1, x0, y1, x1 - 1, y1);
    solve(n - 1, x1, y0, x1, y1 - 1);
    solve(n - 1, x1, y1, x, y);
  }
}

int main() {
  ios_base::sync_with_stdio(false);
  cin.tie(nullptr);

  int n, x, y;
  cin >> n >> x >> y;
  solve(n, 0, 0, x - 1, y - 1);
  arr[x - 1][y - 1] = 0;
  for (int i = 0; i < (1 << n); i++) {
    for (int j = 0; j < (1 << n); j++) {
      cout << arr[i][j] << " ";
    }
    cout << "\n";
  }
  return 0;
}
