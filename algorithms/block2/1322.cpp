#include <bits/stdc++.h>

using namespace std;

int main() {
	ios_base::sync_with_stdio(false);
	cin.tie(nullptr);

	int k, n;
	string s;
	cin >> k >> s;
	n = s.length();

	vector<int> c(256, 0);
	vector<int> count2(n, 0);

	for (int i = 0; i < n; i++) {
		count2[i] = c[s[i]];
		c[s[i]]++;
	}
	int prefs[256];
	prefs[0] = 0;
	for (int i = 0; i < 255; i++) {
		prefs[i + 1] = prefs[i] + c[i];
	}

	string res = "";
	int curr = k - 1;
	for (int i = 0; i < n; i++) {
		char ch = s[curr];
		res += ch;
		curr = prefs[ch] + count2[curr];
	}
	reverse(res.begin(), res.end());
	cout << res;
	return 0;
}
