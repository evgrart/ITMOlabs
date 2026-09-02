.text
.org 0x100
_start:
    movea.l 4000, A7

    movea.l 128, A0
    move.l (A0), D1      
    
    cmp.l 0, D1
    blt error_minus_1
    beq output_zero

    movea.l 1024, A1
    jsr read_input_bytes

    movea.l 1024, A1
    movea.l 2048, A2
    jsr compress_data

    movea.l 2048, A2
    jsr write_output_words

    halt

error_minus_1:
    movea.l 132, A0
    clr.l D0
    sub.l 1, D0
    move.l D0, (A0)
    halt

output_zero:
    movea.l 132, A0
    clr.l D0
    move.l D0, (A0)
    halt

read_input_bytes:
    move.l D1, D2
    movea.l 128, A0
    
    move.l 24, D5
    move.l 16, D6
    move.l 8, D7
    
read_loop:
    cmp.l 0, D2
    ble read_done
    move.l (A0), D3
    
    move.l D3, D4
    lsr.l D5, D4
    move.b D4, (A1)+
    sub.l 1, D2
    beq read_done
    
    move.l D3, D4
    lsr.l D6, D4
    move.b D4, (A1)+
    sub.l 1, D2
    beq read_done
    
    move.l D3, D4
    lsr.l D7, D4
    move.b D4, (A1)+
    sub.l 1, D2
    beq read_done
    
    move.l D3, D4
    move.b D4, (A1)+
    sub.l 1, D2
    jmp read_loop

read_done:
    rts

compress_data:
    clr.l D2
    move.l D1, D3
    cmp.l 0, D3
    ble compress_done
    
compress_next_run:
    clr.l D4
    move.b (A1)+, D4
    and.l 255, D4
    sub.l 1, D3          
    move.l 1, D5
    
compress_count_loop:
    cmp.l 0, D3
    beq compress_write_run
    cmp.l 255, D5
    beq compress_write_run
    
    clr.l D6
    move.b (A1), D6
    and.l 255, D6
    cmp.b D4, D6
    bne compress_write_run
    
    move.b (A1)+, D6
    add.l 1, D5
    sub.l 1, D3
    jmp compress_count_loop
    
compress_write_run:
    move.b D5, (A2)+
    move.b D4, (A2)+
    
    add.l 2, D2          
    
    cmp.l 0, D3
    bgt compress_next_run
    
compress_done:
    rts

write_output_words:
    movea.l 132, A0
    move.l D2, (A0)
    
    move.l D2, D3
    move.l 24, D5
    move.l 16, D6
    move.l 8, D7
    
write_loop:
    cmp.l 0, D3
    ble write_done
    
    clr.l D4
    
    clr.l D1
    move.b (A2)+, D1
    and.l 255, D1
    lsl.l D5, D1
    or.l D1, D4
    sub.l 1, D3
    beq output_word
    
    clr.l D1
    move.b (A2)+, D1
    and.l 255, D1
    lsl.l D6, D1
    or.l D1, D4
    sub.l 1, D3
    beq output_word
    
    clr.l D1
    move.b (A2)+, D1
    and.l 255, D1
    lsl.l D7, D1
    or.l D1, D4
    sub.l 1, D3
    beq output_word
    
    clr.l D1
    move.b (A2)+, D1
    and.l 255, D1
    or.l D1, D4
    sub.l 1, D3
    
output_word:
    move.l D4, (A0)
    jmp write_loop
    
write_done:
    rts
