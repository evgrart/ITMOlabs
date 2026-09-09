    .text
    .org 0x100
_start:
    lui s2, %hi(0x01000193)      / lui s1, %hi(0x811c9dc5)      / lw t0, 128(zero) / nop
    addi s2, s2, %lo(0x01000193) / addi s1, s1, %lo(0x811c9dc5) / nop              / nop

hash_loop:
    mul t1, s1, s2               / nop                          / nop              / beqz t0, hash_end
    xor s1, t1, t0               / nop                          / lw t0, 128(zero) / j hash_loop

hash_end:
    nop                          / nop                          / sw s1, 132(zero) / halt
