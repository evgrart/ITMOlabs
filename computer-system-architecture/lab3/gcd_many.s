.data
.org 0x88
input_addr: .word 0x80
output_addr: .word 0x84

.text
.org 0x100
_start:
    lui sp, %hi(0x1000)
    addi sp, sp, %lo(0x1000)
    jal ra, proc_solve
    halt

proc_solve:
    addi sp, sp, -16
    sw ra, 12(sp)
    sw s1, 8(sp)
    sw s2, 4(sp)
    sw s3, 0(sp)
    lui s2, %hi(input_addr)
    addi s2, s2, %lo(input_addr)
    lw s2, 0(s2)
    mv a0, s2
    jal ra, proc_read_word
    mv s1, a0
    ble s1, zero, proc_solve_invalid_input
    mv a0, s2
    jal ra, proc_read_word
    jal ra, proc_abs
    mv s3, a0
    addi s1, s1, -1
proc_solve_loop:
    beqz s1, proc_solve_write_result
    mv a0, s2
    jal ra, proc_read_word
    mv a1, a0
    mv a0, s3
    jal ra, proc_gcd_recursive
    mv s3, a0
    addi s1, s1, -1
    j proc_solve_loop
proc_solve_invalid_input:
    addi a0, zero, -1
    j proc_solve_output
proc_solve_write_result:
    mv a0, s3
proc_solve_output:
    jal ra, proc_write_result
    lw s3, 0(sp)
    lw s2, 4(sp)
    lw s1, 8(sp)
    lw ra, 12(sp)
    addi sp, sp, 16
    jr ra

proc_read_word:
    lw a0, 0(a0)
    jr ra

proc_write_result:
    lui t0, %hi(output_addr)
    addi t0, t0, %lo(output_addr)
    lw t0, 0(t0)
    sw a0, 0(t0)
    jr ra

proc_gcd_recursive:
    addi sp, sp, -16
    sw ra, 12(sp)
    beqz a1, proc_gcd_recursive_base
    rem t0, a0, a1
    mv a0, a1
    mv a1, t0
    jal ra, proc_gcd_recursive
    j proc_gcd_recursive_epilogue
proc_gcd_recursive_base:
    jal ra, proc_abs
proc_gcd_recursive_epilogue:
    lw ra, 12(sp)
    addi sp, sp, 16
    jr ra

proc_abs:
    bgt a0, zero, proc_abs_return
    sub a0, zero, a0
proc_abs_return:
    jr ra
