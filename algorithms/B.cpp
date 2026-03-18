#include <bits/stdc++.h>

using namespace std;

int main() {
  ios_base::sync_with_stdio(false);
  cin.tie(nullptr);

  string s;
  cin >> s;
  stack<int> st;
  vector<int> res(s.size() / 2 + 1);
  int k = 1;
  vector<int> v1;  
  vector<int> v2;
  for (int i = 0; i < (int)s.size(); i++) {
    if (islower(s[i])) {
      v1.push_back(k++);
    } else {
      v1.push_back(0);
    }
  }
  k = 1;
  for (int i = 0; i < (int)s.size(); i++) {
    if (isupper(s[i])) {
      v2.push_back(k++);
    } else {
      v2.push_back(0);
    }
  }
  for (int i = 0; i < (int)s.size(); i++) {
    if (st.empty()) {
      st.push(i);
      continue;
    }
    
    bool a1 = (isupper(s[i]) && islower(s[st.top()]));
    bool a2 = (islower(s[i]) && isupper(s[st.top()]));
    bool a3 = (tolower(s[i]) == tolower(s[st.top()]));

    if ((a1 || a2) && a3) {
      if (islower(s[i])) {
        res[v2[st.top()]] = v1[i];
      } else {
        res[v2[i]] = v1[st.top()];
      }
      st.pop();
    } else {
      st.push(i);
    }
  }
  if (!st.empty()) {
    cout << "Impossible";
  } else {
    cout << "Possible\n";
    for (int i = 1; i <= (int)s.size() / 2; i++) {
      cout << res[i] << " ";
    }
  }
  return 0;
}
