#include "collection.h"
#include "main.h"
#include <stdio.h>
#include <stdio.h>
#include <string.h>
#include <stdbool.h>

void view(linked_list* list) {
    int count = 1;
    Node* node = list->fict->next;
    while (node->next != list->fict) {
        printf("%s%i%s", "\nRecord number ", count++, "\n");
        print_rec(node->val);
        node = node->next;
    }
    if (node == list->fict) {
        printf("%s", "Empty list\n");
    } else {
        printf("%s%i%s", "Record number ", count, "\n");
        print_rec(node->val);
    }
}

void add_node(linked_list* list) {
    char* name, *surname, *phone, *email;
    printf("%s", "Enter name: ");
    name = readl();
    printf("%s", "\nEnter surname: ");
    surname = readl();
    printf("%s", "\nEnter phone: ");
    phone = readl();
    printf("%s", "\nEnter e-mail: ");
    email = readl();
    record* rec = create_record(name, surname, phone, email);
    push_back(list, rec);
    printf("%s", "Record has been added in list\n");
}

void search(linked_list* list) {
    char* input;
    printf("%s", "Search by\n1 Name\n2 Surname\n3 Name and Surname\n4 Phone\n5 E-mail\n");
    input = readl();
    char c = input[0];
    while ((c != '1') && (c != '2') && (c != '3') && (c != '4') && (c != '5')) {
        printf("%s", "Please try again\n");
        input = readl();
        c = input[0];
    }
    int count = 1;
    char* s;
    printf("%s", "Enter: ");
    input = readl();
    if (c == '1') { // name
        Node* node = list->fict->next;
        while (node->next != list->fict) {
            node = node->next;
            s = node->val->name;
            if (strstr(s, input) != NULL) {
                printf("%s%i%s", "\nRecord number ", count++, "\n");
                print_rec(node->val);
            }
        }

        s = node->val->name;
        if (strstr(s, input) != NULL) {
            printf("%s%i%s", "\nRecord number ", count++, "\n");
            print_rec(node->val);
        }

    } else if (c == '2') { // surname
        Node* node = list->fict->next;
        while (node->next != list->fict) {
            node = node->next;
            s = node->val->surname;
            if (strstr(s, input) != NULL) {
                printf("%s%i%s", "\nRecord number ", count++, "\n");
                print_rec(node->val);
            }
        }

        s = node->val->surname;
        if (strstr(s, input) != NULL) {
            printf("%s%i%s", "\nRecord number ", count++, "\n");
            print_rec(node->val);
        }
    } else if (c == '3') {
        Node* node = list->fict->next;
        while (node->next != list->fict) {
            node = node->next;
            s = strcat(node->val->name, node->val->surname);
            if (strstr(s, input) != NULL) {
                printf("%s%i%s", "\nRecord number ", count++, "\n");
                print_rec(node->val);
            }
        }

        s = strcat(node->val->name, node->val->surname);
        if (strstr(s, input) != NULL) {
            printf("%s%i%s", "\nRecord number ", count++, "\n");
            print_rec(node->val);
        }
    } else if (c == '4') {
        Node* node = list->fict->next;
        while (node->next != list->fict) {
            node = node->next;
            s = node->val->phone;
            if (strstr(s, input) != NULL) {
                printf("%s%i%s", "\nRecord number ", count++, "\n");
                print_rec(node->val);
            }
        }

        s = node->val->phone;
        if (strstr(s, input) != NULL) {
            printf("%s%i%s", "\nRecord number ", count++, "\n");
            print_rec(node->val);
        }
    } else {
        Node* node = list->fict->next;
        while (node->next != list->fict) {
            node = node->next;
            s = node->val->email;
            if (strstr(s, input) != NULL) {
                printf("%s%i%s", "\nRecord number ", count++, "\n");
                print_rec(node->val);
            }
        }

        s = node->val->email;
        if (strstr(s, input) != NULL) {
            printf("%s%i%s", "\nRecord number ", count++, "\n");
            print_rec(node->val);
        }
    }
    if (count == 1) {
        printf("%s", "No matches\n");
    }
}