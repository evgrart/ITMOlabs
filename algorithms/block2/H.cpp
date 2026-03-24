#include <bits/stdc++.h>

using namespace std;

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
  reverse(arr.begin(), arr.end());

  long long count = 0;
  for (int i = 0; i < n; i++) {
    if ((i + 1) % k != 0) {
      count += arr[i];
    }
  }

  cout << count;
  return 0;
}