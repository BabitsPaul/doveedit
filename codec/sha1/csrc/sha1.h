#ifndef SHA1_H_INCLUDED
#define SHA1_H_INCLUDED

#include <stdint.h>

void setup();
void readNextBlock();
void cleanUp();
void generateSHA1();

void sn(uint32_t , int);

uint32_t (*fptr[4])(uint32_t , uint32_t , uint32_t);

uint32_t f0(uint32_t,uint32_t,uint32_t);
uint32_t f1(uint32_t,uint32_t,uint32_t);
uint32_t f2(uint32_t,uint32_t,uint32_t);
uint32_t f3(uint32_t,uint32_t,uint32_t);

uint32_t f(int,uint32_t,uint32_t,uint32_t);

#endif // SHA1_H_INCLUDED
