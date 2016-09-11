#include <stdio.h>

int main() {
	long list1[] = {1, 2, 3, 2};
	int _AD_size = 4;

	int _AD_i = 0;
	long _AD_result = 1;
	for(_AD_i = 0; _AD_i < _AD_size; _AD_i++) {
		_AD_result *= list1[_AD_i];
	}

	printf("%d\n", _AD_result);
	return 0;
}