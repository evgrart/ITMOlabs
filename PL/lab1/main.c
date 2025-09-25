#include<stdio.h>
#include<string.h>
#include <stdlib.h> 

void coder(char* string, int n, int shift) {
    shift %= 'z' - 'a' + 1;
    shift = (shift < 0) ? shift + ('z' - 'a' + 1) : shift;
    for (char* i = string; i < string + n; i++) {
        *i = (((*i - 'a') + shift) % ('z' - 'a' + 1)) + 'a'; 
    }
}

void decoder(char* string, int n, int shift) {
    shift %= 'z' - 'a' + 1;
    shift = (shift < 0) ? shift + ('z' - 'a' + 1) : shift;
    for (char* i = string; i < string + n; i++) {
        *i = (((*i - 'a') - shift) % ('z' - 'a' + 1)) + 'a'; 
    }
}


int main(int args, char* argv[]) {
    char* begin_string = argv[2];
    int n = atoi(argv[1]);
    int shift = atoi(argv[3]);
    
    coder(begin_string, n, shift);
    for (char* i = begin_string; i < begin_string + n; i++) {
        printf("%c", *i);
    }
    printf("%s", "\n");
    decoder(begin_string, n, shift);
    for (char* i = begin_string; i < begin_string + n; i++) {
        printf("%c", *i);
    }
   
    return 0;
}