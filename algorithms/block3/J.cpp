#include <bits/stdc++.h>

using namespace std;

int main() {
  ios_base::sync_with_stdio(false);
  cin.tie(nullptr);

  int n;
  cin >> n;

  deque<int> s1;
  deque<int> s2;

  for (int i = 0; i < n; ++i) {
    char type;
    cin >> type;

    if (type == '+') {
      int id;
      cin >> id;
      s2.push_back(id);
    } else if (type == '*') {
      int id;
      cin >> id;
      s2.push_front(id);
    } else {
      cout << s1.front() << "\n";
      s1.pop_front();
    }

    if (s2.size() > s1.size()) {
      int x = s2.front();
      s2.pop_front();
      s1.push_back(x);
    }
  }

  return 0;
}
