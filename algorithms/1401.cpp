#include <bits/stdc++.h>

using namespace std;

int main() {
	ios_base::sync_with_stdio(false);
	cin.tie(nullptr);

	int t;
	cin >> t;
	while (t--) {
		long long int n, k;
		cin >> n >> k;
		int m = n / k;
		int r = n % k;
		long long int res = n * (n - 1) / 2 - (k - r) * m * (m - 1) / 2 - r * m * (m + 1) / 2;
		cout << res << "\n";
	}

	return 0;
}
