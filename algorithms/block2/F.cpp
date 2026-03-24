#include <bits/stdc++.h>

using namespace std;

bool comp(const string& a, const string& b) {
  return a + b > b + a;
}

int main() {
  ios_base::sync_with_stdio(false);
  cin.tie(nullptr);

  vector<string> arr;
  string s;

  while (cin >> s) {
    arr.push_back(s);
  }

  sort(arr.begin(), arr.end(), comp);

  for (size_t i = 0; i < arr.size(); i++) {
    cout << arr[i];
  }

  return 0;
}
