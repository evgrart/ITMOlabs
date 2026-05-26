#include <bits/stdc++.h>

using namespace std;

struct Node {
  map<string, int> children;
};

vector<Node> tree(1);

void addPath(const string& path) {
  int vertex = 0;
  string name;

  for (size_t i = 0; i <= path.size(); i++) {
    if (i == path.size() || path[i] == '\\') {
      if (!tree[vertex].children.count(name)) {
        tree[vertex].children[name] = static_cast<int>(tree.size());
        tree.push_back(Node());
      }
      vertex = tree[vertex].children[name];
      name.clear();
    } else {
      name += path[i];
    }
  }
}

void printTree(int vertex, int depth) {
  for (const auto& child : tree[vertex].children) {
    cout << string(depth, ' ') << child.first << '\n';
    printTree(child.second, depth + 1);
  }
}

int main() {
  ios::sync_with_stdio(false);
  cin.tie(nullptr);

  int n;
  cin >> n;

  string path;
  for (int i = 0; i < n; i++) {
    cin >> path;
    addPath(path);
  }

  printTree(0, 0);
  return 0;
}
