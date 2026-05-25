#include <bits/stdc++.h>

using namespace std;

int dr[] = {-1, 1, 0, 0};
int dc[] = {0, 0, -1, 1};
char dir_chars[] = {'N', 'S', 'W', 'E'};

int main() {
  ios_base::sync_with_stdio(false);
  cin.tie(nullptr);

  int n, m;
  cin >> n >> m;

  int sr, sc, er, ec;
  cin >> sr >> sc >> er >> ec;

  --sr;
  --sc;
  --er;
  --ec;

  vector<string> grid(n);
  for (int i = 0; i < n; ++i) {
    cin >> grid[i];
  }

  vector<vector<int>> dist(n, vector<int>(m, 1e9));
  vector<vector<char>> parent(n, vector<char>(m, 0));

  using State = pair<int, pair<int, int>>;
  priority_queue<State, vector<State>, greater<State>> pq;

  dist[sr][sc] = 0;
  pq.push(make_pair(0, make_pair(sr, sc)));

  while (!pq.empty()) {
    auto top = pq.top();
    pq.pop();

    int d = top.first;
    int r = top.second.first;
    int c = top.second.second;

    if (d > dist[r][c]) {
      continue;
    }

    if (r == er && c == ec) {
      break;
    }

    for (int i = 0; i < 4; ++i) {
      int nr = r + dr[i];
      int nc = c + dc[i];

      if (nr >= 0 && nr < n && nc >= 0 && nc < m && grid[nr][nc] != '#') {
        int cost = (grid[nr][nc] == 'W') ? 2 : 1;
        if (dist[r][c] + cost < dist[nr][nc]) {
          dist[nr][nc] = dist[r][c] + cost;
          parent[nr][nc] = dir_chars[i];
          pq.push(make_pair(dist[nr][nc], make_pair(nr, nc)));
        }
      }
    }
  }

  if (dist[er][ec] == 1e9) {
    cout << -1 << "\n";
  } else {
    cout << dist[er][ec] << "\n";

    string path = "";
    int r = er;
    int c = ec;
    while (r != sr || c != sc) {
      char p = parent[r][c];
      path += p;
      if (p == 'N') {
        r++;
      } else if (p == 'S') {
        r--;
      } else if (p == 'W') {
        c++;
      } else if (p == 'E') {
        c--;
      }
    }

    reverse(path.begin(), path.end());
    cout << path << "\n";
  }

  return 0;
}
