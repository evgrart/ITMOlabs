#include <bits/stdc++.h>

using namespace std;

struct Node {
  int pref, suff, mid, s, l, r;
};

const int MAX = 2000000;
Node tree[MAX];
int cnt = 2;

int new_node(int len) {
  int id = cnt++;
  tree[id] = {len, len, len, -1, 0, 0};
  return id;
}

void push(int x, long long lx, long long rx) {
  if (tree[x].s == -1)
    return;

  long long mx = lx + (rx - lx) / 2;

  if (!tree[x].l)
    tree[x].l = new_node(mx - lx);
  if (!tree[x].r)
    tree[x].r = new_node(rx - mx);

  int l = tree[x].l;
  int r = tree[x].r;

  tree[l].s = tree[x].s;
  tree[r].s = tree[x].s;

  if (tree[x].s == 1) {
    tree[l].pref = tree[l].suff = tree[l].mid = 0;
    tree[r].pref = tree[r].suff = tree[r].mid = 0;
  } else {
    tree[l].pref = tree[l].suff = tree[l].mid = mx - lx;
    tree[r].pref = tree[r].suff = tree[r].mid = rx - mx;
  }

  tree[x].s = -1;
}

void pull(int x, long long lx, long long rx) {
  long long mx = lx + (rx - lx) / 2;

  int l = tree[x].l ? tree[x].l : 0;
  int r = tree[x].r ? tree[x].r : 0;

  int l_pref = l ? tree[l].pref : (mx - lx);
  int l_suff = l ? tree[l].suff : (mx - lx);
  int l_mid = l ? tree[l].mid : (mx - lx);

  int r_pref = r ? tree[r].pref : (rx - mx);
  int r_suff = r ? tree[r].suff : (rx - mx);
  int r_mid = r ? tree[r].mid : (rx - mx);

  tree[x].pref = l_pref;
  if (l_pref == mx - lx)
    tree[x].pref += r_pref;

  tree[x].suff = r_suff;
  if (r_suff == rx - mx)
    tree[x].suff += l_suff;

  tree[x].mid = max({l_mid, r_mid, l_suff + r_pref});
}

void op(int x, long long lx, long long rx, long long l, long long r, int p) {
  if (lx >= r || rx <= l)
    return;

  if (lx >= l && rx <= r) {
    tree[x].s = p;
    if (p == 1)
      tree[x].pref = tree[x].suff = tree[x].mid = 0;
    else
      tree[x].pref = tree[x].suff = tree[x].mid = rx - lx;
    return;
  }

  push(x, lx, rx);

  long long mx = lx + (rx - lx) / 2;

  if (!tree[x].l)
    tree[x].l = new_node(mx - lx);
  if (!tree[x].r)
    tree[x].r = new_node(rx - mx);

  op(tree[x].l, lx, mx, l, r, p);
  op(tree[x].r, mx, rx, l, r, p);

  pull(x, lx, rx);
}

int find(int x, long long lx, long long rx, int k) {
  if (tree[x].mid < k)
    return -1;

  if (rx - lx == k)
    return lx;

  push(x, lx, rx);

  long long mx = lx + (rx - lx) / 2;

  int l = tree[x].l ? tree[x].l : 0;
  int r = tree[x].r ? tree[x].r : 0;

  int l_mid = l ? tree[l].mid : (mx - lx);
  int l_suff = l ? tree[l].suff : (mx - lx);
  int r_pref = r ? tree[r].pref : (rx - mx);

  if (l_mid >= k) {
    if (!tree[x].l)
      tree[x].l = new_node(mx - lx);
    return find(tree[x].l, lx, mx, k);
  }

  if (l_suff + r_pref >= k)
    return mx - l_suff;

  if (!tree[x].r)
    tree[x].r = new_node(rx - mx);

  return find(tree[x].r, mx, rx, k);
}

struct Req {
  int start, len;
};

int main() {
  ios_base::sync_with_stdio(false);
  cin.tie(nullptr);

  int n, m;
  cin >> n >> m;

  tree[1].pref = tree[1].suff = tree[1].mid = n;
  tree[1].s = -1;

  vector<Req> history(m + 1);

  int y;
  for (int i = 1; i <= m; i++) {
    cin >> y;

    if (y > 0) {
      int z = find(1, 1, (long long)n + 1, y);

      if (z == -1) {
        cout << "-1\n";
        history[i] = {-1, 0};
      } else {
        cout << z << "\n";
        history[i] = {z, y};
        op(1, 1, (long long)n + 1, z, (long long)z + y, 1);
      }
    } else {
      int idx = -y;

      if (idx <= m && history[idx].start != -1) {
        op(1,
           1,
           (long long)n + 1,
           history[idx].start,
           (long long)history[idx].start + history[idx].len,
           0);
        history[idx] = {-1, 0};
      }

      history[i] = {-1, 0};
    }
  }

  return 0;
}
