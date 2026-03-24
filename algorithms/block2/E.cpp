#include <bits/stdc++.h>

using namespace std;

bool check(const vector<int>& arr, int k, int x) {
  int count = 1;
  int last = arr[0];
  for (size_t i = 1; i < arr.size(); i++) {
    if (arr[i] - last >= x) {
      last = arr[i];
      count++;
    }
  }
  return count >= k;
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

  sort(arr.begin(), arr.end());

  int l = 0;
  int r = arr[n - 1] - arr[0] + 1;
  int m;

  while (l + 1 < r) {
    m = l + (r - l) / 2;
    if (check(arr, k, m)) {
      l = m;
    } else {
      r = m;
    }
  }

  cout << l;
  return 0;
}
