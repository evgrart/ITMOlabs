#include <bits/stdc++.h>

using namespace std;

pair<string, string> split(string s) {
  string s1 = "";
  string s2 = "";
  int j = 0;
  for (int i = 0; i < (int)s.size(); i++) {
    if (s[i] == '=') {
      j = i;
      break;
    }
    s1 += s[i];
  }
  for (int i = j + 1; i < (int)s.size(); i++) {
    s2 += s[i];
  }
  return make_pair(s1, s2);
}

int main() {
  ios_base::sync_with_stdio(false);
  cin.tie(nullptr);

  unordered_map<string, int> mp;
  stack<pair<string, int>> st;
  string s;
  pair<string, int> mark = make_pair("mark000", 0);
  pair<string, string> p;
  int k = 0;

  while (cin >> s) {
    if (s == "{") {
      st.push(mark);
      k++;
    } else if (s == "}") {
      while (st.top() != mark) {
        mp[st.top().first] = st.top().second;
        st.pop();
      }
      st.pop();
      k--;
    } else {
      p = split(s);
      if (k > 0) {
        st.push(make_pair(p.first, mp[p.first]));
      }
      if (p.second[0] >= '0' && p.second[0] <= '9') {
        mp[p.first] = stoi(p.second);
      } else if (p.second[0] == '-') {
        mp[p.first] = stoi(p.second);
      } else {
        mp[p.first] = mp[p.second];
        cout << mp[p.first] << "\n";
      }
    }
  }

  return 0;
}
