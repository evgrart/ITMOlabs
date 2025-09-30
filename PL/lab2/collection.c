#include <stdlib.h>
#include <stdio.h>
#include "collection.h"

record* create_record(char* name, char* surname, char* phone, char* email) {
    record* rec = (record*) malloc(sizeof(record));
    if (rec == NULL) {
        return NULL;
    }
    rec->name = name;
    rec->surname = surname;
    rec->phone = phone;
    rec->email = email;
    return rec;
};

Node* create_node(Node* prev, Node* next, record* val) {
    Node* node = (Node*) malloc(sizeof(Node));
    if (node == NULL) {
        return NULL;
    }
    node->prev = prev;
    node->next = next;
    node->val = val;
    return node;
}

void destroy_rec(record* rec) {
    free(rec);
};

void destroy_node(Node* node) {
    destroy_rec(node->val);
    free(node);
}

linked_list* create_list() {
    linked_list* list = (linked_list*) malloc(sizeof(linked_list));
    if (list == NULL) {
        return NULL;
    }
    record* rec = create_record(NULL, NULL, NULL, NULL);
    list->fict = create_node(NULL, NULL, rec);
    list->fict->next = list->fict;
    list->fict->prev = list->fict;
    return list;
}

void destroy_list(linked_list* list) {
    Node* node = list->fict->next;
    while (node != list->fict) {
        Node* node2 = node->next;
        destroy_node(node);
        node = node2;
    }
    destroy_node(list->fict);
    free(list);
}

void print_rec(record *rec) {
    printf("%s\n", rec->name ? rec->name : "");
    printf("%s\n", rec->surname ? rec->surname : "");
    printf("%s\n", rec->phone ? rec->phone : "");
    printf("%s\n", rec->email ? rec->email : "");
}

void push_back(linked_list* collection, record* rec) {
    Node* node = create_node(collection->fict->prev, collection->fict, rec);
    collection->fict->prev->next = node; // теперь бывший последний элемент ссылается на новый узел
    collection->fict->prev = node;
}

