#ifndef collection_h
#define collection_h

typedef struct record record;
typedef struct Node Node;
typedef struct linked_list linked_list;

struct record {
    char* name;
    char* surname;
    char* phone;
    char* email;
};

struct Node {
    Node* prev;
    Node* next;
    record* val; 
};

struct linked_list {
    Node* fict;
};

record* create_record(char* name, char* surname, char* phone, char* email);

void destroy_rec(record* rec);

linked_list* create_list();

void destroy_list(linked_list* list);

void print_rec(record *rec);

Node* create_node(Node* prev, Node* next, record* val);

void push_back(linked_list* collection, record* rec);

#endif