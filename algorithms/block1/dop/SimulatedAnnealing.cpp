#include <bits/stdc++.h>

using namespace std;

const long double pi = acos(-1.0);

long double func(long double A, vector<long double>& x) {
	long double res = 0;
	for (int i = 0; i < (int) x.size(); i++) {
		res += A + x[i] * x[i] - A * cos(2 * pi * x[i]);
	}
	return res;
}

int main() {
	ios_base::sync_with_stdio(false);
	cin.tie(nullptr);
	random_device rd;  
   	mt19937_64 gen(rd());
   	uniform_real_distribution<long double> dist(0.0, 1.0);

	int n;
	long double A_const;
	cin >> n;
	cin >> A_const;
	vector<long double> x_curr(n, 10);
	vector<long double> x(n);
	vector<long double> temp(n, 100);
	long double A = -(A_const + 101);
	long double B = (A_const + 101);
	long double c = 0.9997;
	long double norm = 1;
	long double E_curr = func(A_const, x_curr);
	long double E;
	while (norm > 1e-14) {
		norm = 0;
		for (int i = 0; i < n; i++) {
			long double alpha = dist(gen);
			int flag = 1;
			if (alpha < 0.5) {
				flag = -1;
			} 
			long double z = flag * temp[i] * (pow((1.0 + 1.0 / temp[i]), abs(2 * alpha - 1)) - 1);
			x[i] = x_curr[i] + z * (B - A);
			if (x[i] < A) {
				x[i] = A;
			}
        		if (x[i] > B) {
				x[i] = B;
			}
			temp[i] = c * temp[i];
		}
		E = func(A_const, x);
		long double delta = E - E_curr;
		if (delta < 1e-10) {
			x_curr = x;
			E_curr = E;
		} else {
			long double p = exp(-delta / norm);
			long double chance = dist(gen);
            
            		if (chance < p) {
                		x_curr = x;
                		E_curr = E;
            		}
		}
		for (int i = 0; i < n; i++) {
			norm += temp[i] * temp[i];
		}
		norm = sqrt(norm);
	}
	
	for (long double i : x_curr) {
		cout << i << " ";
	}

	return 0;
}
