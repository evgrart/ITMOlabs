ORG 0x28B
ADR: WORD $RES

BEGIN: CLA

SYM1:  
  IN 0x5
  AND #0x40
  BZS SYM1
  IN 0x4
  CMP #0x0A
  BZS END1
  SWAB
  ST (ADR)

SYM2:  IN 0x5
  AND #0x40
  BZS SYM2
  IN 0x4
  CMP #0x0A
  BZS END2
  ADD (ADR)
  ST (ADR)+
  JUMP SYM1

END1:  SWAB
      JUMP SAVE

END2:  ADD (ADR)

SAVE:    ST (ADR)
      HLT 

ORG 0x5CD
RES:  WORD 0