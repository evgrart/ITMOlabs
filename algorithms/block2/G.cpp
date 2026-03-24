#include <bits/stdc++.h>

using namespace std;

int main() {
  ios_base::sync_with_stdio(false);
  cin.tie(nullptr);

  string s;
  cin >> s;

  vector<int> c(26);
  for (int i = 0; i < 26; i++) {
    cin >> c[i];
  }

  unordered_map<char, int> mp;
  for (char ch : s) {
    mp[ch]++;
  }

  vector<char> v;
  for (int i = 0; i < 26; ++i) {
    if (mp[i + 'a'] >= 2) {
      v.push_back(i + 'a');
    }
  }

  sort(v.begin(), v.end(), [&](char a, char b) { return c[a - 'a'] > c[b - 'a']; });

  string res = "";
  string tail = "";

  for (char ch : v) {
    res += ch;
    tail += ch;
    mp[ch] -= 2;
  }

  reverse(tail.begin(), tail.end());

  for (int i = 0; i < 26; ++i) {
    while (mp[i + 'a'] > 0) {
      res += static_cast<char>(i + 'a');
      mp[i + 'a']--;
    }
  }

  res += tail;

  cout << res;

  return 0;
}
