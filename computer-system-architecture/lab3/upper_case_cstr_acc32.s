.data
.org 0x88
letter: .word 0x000
letter_a: .word 0x060
letter_z: .word 0x07B
const_f: .word 0x05f
const_one: .word 0x001
mask: .word 0x000000ff
const_tt: .word 0x020
count_cycle: .word 0x000
count_print: .word 0x000
const_ten: .word 0x00A
const_zero: .word 0x5f5f5f00
cursor: .word 0x000
const_error: .word 0xCCCCCCCC
output: .word 0x84
input: .word 0x80

.text
_start:
    cycle:
        load_addr count_cycle
        sub const_tt
        beqz loop
        load_addr const_f
        store_ind count_cycle
        load_addr count_cycle
        add const_one
        store_addr count_cycle
        jmp cycle
        
    loop: 
        load_addr cursor
        sub const_tt
        beqz error
        load input
        load_acc
        store_addr letter
        sub const_ten
        beqz set_zero ; прыгаем если конец
        load_addr letter
        sub letter_a
        ble set_letter
        load_addr letter_z
        sub letter
        ble set_letter 
        load_addr letter
        sub const_tt
        store_addr letter
        
    upper: 
        load_addr letter
        store_ind cursor
        load_addr cursor
        add const_one
        store_addr cursor
        jmp loop
    
    error: load_addr const_error
          store_ind output
          halt
    
    print:
          load count_print
          load_acc
          and mask
          beqz stop
          store_ind output
          load_addr count_print
          add const_one
          store_addr count_print
          jmp print
    
    stop: halt

    set_letter:
         load_addr letter
         jmp upper
         
    set_zero:
        load_addr const_zero
        store_ind cursor
        jmp print
