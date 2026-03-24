#include <bits/stdc++.h>

using namespace std;

int main() {
  ios_base::sync_with_stdio(false);
  cin.tie(nullptr);

  long long a;
  long long b;
  long long c;
  long long d;
  long long k;
  cin >> a >> b >> c >> d >> k;

  long long count = a;

  for (long long i = 1; i <= k; i++) {
    long long prev_count = count;
    count = count * b - c;

    if (count <= 0) {
      cout << 0;
      return 0;
    }

    if (count > d) {
      count = d;
    }

    if (count == prev_count) {
      break;
    }
  }

  cout << count;
  return 0;
}
