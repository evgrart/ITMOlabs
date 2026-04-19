.data
.org 0x200 \ хз
input: .word 0x080
one: .word 0x001
count: .word 0x01F
zero: .word 0x000


.text
_start:
  @p input a!
  @
  a! \ число
  @p one
  b! \ единица
  
  lit 0 \ внизу лежит res
  begin
  lit 0x84 b!
  !b 
  halt
begin: 
  @p count
  >r
cycle: lit 1
  a
  and \ здесь бит
  a
  2/
  a!
  over
  2*
  +
  next cycle
;
