#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include "collection.h"
#include "commands.h"

char* readl() {
    int size = 8;
    char* buffer = malloc(size * sizeof(char));
    if (buffer == NULL) {
        free(buffer);
        return NULL;
    }
    int pos = 0;
    char c;
    while (1) {
        c = getchar();
        if (c == '\n' || c == EOF) { // зависит от системы
            buffer[pos] = '\0';
            return buffer;
        } else {
            buffer[pos++] = c;
        }
        if (pos >= size) {
            char* new_buffer = malloc(2 * size * sizeof(char));
            size *= 2;
            if (new_buffer == NULL) {
                free(buffer);
                return NULL;
            }
            for (int i = 0; i <= pos; i++) {
                new_buffer[i] = buffer[i];
            }
            free(buffer);
            buffer = new_buffer;
        }
    }
}

void inf() {
    printf("%s", "Menu:\n1 View all contacts\n2 Search\n3 New contact\n4 Exit\n");
}

bool validate_input(char* string) {
    if (string[0] == '\0') {
        return false;
    }
    return (string[0] == '1' || string[0] == '2' || string[0] == '3' || string[0] == '4');
}

int main() {
    printf("%s", "Enter the number of action and press [Enter]. Then follow instructions.\n");
    printf("%s", "If you will input more than one symbol, only the first of them will be scanned\n");
    bool flag = true;
    char* input;
    linked_list* list = create_list();
    while (1) {
        if (flag) {
            inf();
        }
        input = readl();  
        if (!validate_input(input)) {
            printf("%s", "There is no correct input, please try again\n");
            flag = false;
        } else {
            flag = true;
            if (input[0] == '4') {
                destroy_list(list);
                break;
            } else if (input[0] == '1') {
                view(list);
            } else if (input[0] == '3') {
                add_node(list);
            } else {
                search(list);
            }
        }
    }

    return 0;
}    