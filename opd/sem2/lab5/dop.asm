ORG 0x000
ADR: WORD $RES
COUNTER: WORD 0x0000

BEGIN: CLA

READ:   IN 0x19
   AND #0x40
    BZS READ
      IN 0x18
      CMP #0x0A
      BZS SAVE
      ST (ADR)+
      LD COUNTER
      INC
      ST COUNTER
      JUMP READ

SAVE:   CLA
      LD COUNTER
      CMP #0x00 ; если ввели 0 символов, то выход
      BZS EXIT
      JUMP START_WRITING

START_WRITING:  CLA
      IN 0xD
      AND #0x40
      BZS START_WRITING
      LD ADR
      SUB COUNTER
      ST ADR
      JUMP WRITE

WRITE:   CLA
      LD (ADR)
      ASR
      ASR
      ASR
      ASR
      PUSH
      CALL FUNC
      POP
      OUT 0xC
      LD (ADR)+
      AND #0x0F
      PUSH
      CALL FUNC
      POP
      OUT 0xC
      LD #0x9A
      OUT 0xC
      LOOP COUNTER
      JUMP WRITE
      HLT        

FUNC:   LD &1
      CMP #0x00
      BZS SET_0
      CMP #0x01
      BZS SET_1
      CMP #0x02
      BZS SET_2
      CMP #0x03
      BZS SET_3
      CMP #0x04
      BZS SET_4
      CMP #0x05
      BZS SET_5
      CMP #0x06
      BZS SET_6
      CMP #0x07
      BZS SET_7
      CMP #0x08
      BZS SET_8
      CMP #0x09
      BZS SET_9
      CMP #0x0A
      BZS SET_A
      CMP #0x0B
      BZS SET_B
      CMP #0x0C
      BZS SET_C
      CMP #0x0D
      BZS SET_D
      CMP #0x0E
      BZS SET_E
      CMP #0x0F
      BZS SET_F  
        
SET_0:    LD #0x30
        ST &1
        RET
        
SET_1:    LD #0x31
        ST &1
        RET
        
SET_2:    LD #0x32
        ST &1
        RET
        
SET_3:    LD #0x33
        ST &1
        RET
        
SET_4:    LD #0x34
        ST &1
        RET
        
SET_5:    LD #0x35
        ST &1
        RET
        
SET_6:    LD #0x36
        ST &1
        RET
        
SET_7:    LD #0x37
        ST &1
        RET
        
SET_8:    LD #0x38
        ST &1
        RET
        
SET_9:    LD #0x39
        ST &1
        RET
        
SET_A:    LD #0x41
        ST &1
        RET
        
SET_B:    LD #0x42
        ST &1
        RET
        
SET_C:    LD #0x43
        ST &1
        RET
        
SET_D:    LD #0x44
        ST &1
        RET
        
SET_E:    LD #0x45
        ST &1
        RET
        
SET_F:    LD #0x46
        ST &1
        RET
          
ORG 0x2CD
RES:  WORD 0