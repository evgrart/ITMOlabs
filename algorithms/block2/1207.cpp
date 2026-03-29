#include <bits/stdc++.h>

using namespace std;

struct Point {
  int x, y, idx;
};

bool operator == (const Point& a, const Point& b) {
  return (a.x == b.x) && (a.y == b.y) && (a.idx == b.idx);
}

int main() {
  ios_base::sync_with_stdio(false);
  cin.tie(nullptr);
  int n;
  cin >> n;
  Point p[n];
  Point p0 = {0, 10000000, 0};
  for (int i = 0; i < n; i++) {
    cin >> p[i].x >> p[i].y;
    p[i].idx = i + 1;
    if (p[i].y < p0.y) {
      p0 = p[i];
    } else if (p[i].y == p0.y && p[i].x < p0.x) {
      p0 = p[i];
    }
  }
  swap(p[0], p[p0.idx - 1]);
  sort(p + 1, p + n, [&](const Point& a, const Point& b) {
    return atan2(a.x - p0.x, a.y - p0.y) < atan2(b.x - p0.x, b.y - p0.y);
  });
    
  cout << p0.idx << " " << p[n / 2].idx;
  return 0;
}
