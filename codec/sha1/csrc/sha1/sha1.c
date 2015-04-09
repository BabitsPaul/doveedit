#include "dove_codec_sha1_SHA1.h"

#include <iostream>
#include <fstream>
#include <stdint.h>
using namespace std;

//constants for calculation
#define K0_19 0x5A827999
#define K20_39 0x6ED9EBA1
#define K40_59 0x8F1BBCDC
#define K60_79 0xCA62C1D6

#define BUFFERSIZE 4096		//size of the buffer
#define BLOCKSIZE 64		//size of a block that is used in sha1
#define HASHSIZE 20			//size of the resulting hashcode (bytes)

//the buffers for storing the hashcode
//and the currently used block
char* block;
char* buffer;
unsigned char* sha1;

//the resolved name of the file to open
const char* file;

long fPos;
int bufPos;
int bufSize;

uint32_t (*fptr[4])(uint32_t , uint32_t , uint32_t);

void setup();
void readNextBlock();
void cleanUp();
void generateSHA1();

uint32_t f(int num , uint32_t a , uint32_t b , uint32_t c);

uint32_t f0(uint32_t a , uint32_t b , uint32_t c);

uint32_t f1(uint32_t a , uint32_t b , uint32_t c);

uint32_t f2(uint32_t a , uint32_t b , uint32_t c);

uint32_t f3(uint32_t a , uint32_t b , uint32_t c);

JNIEXPORT jbyteArray JNICALL Java_dove_codec_sha1_SHA1_sha1
  (JNIEnv * env , jobject sha1 , jstring src)
{
    file = env->GetStringUTFChars(src , 0);
    setup();



    cleanUp();
    env->ReleaseStringUTFChars(src , file);

    return NULL;
}

void setup()
{
    fptr[0] = f0;
    fptr[1] = f1;
    fptr[2] = f2;
    fptr[3] = f3;

	sha1 = new unsigned char[HASHSIZE];
	block = new char[BLOCKSIZE];
	buffer = new char[BUFFERSIZE];

	fPos = 0l;
	bufPos = 0;

	ifstream is;
	is.open(file , ios::in | ios::binary);
	is.read(buffer , BUFFERSIZE);

	fPos = is.tellg();
	is.close();
	bufSize = (fPos % BUFFERSIZE);
}

void readNextBlock()
{

}

void cleanUp()
{

}

void generateSHA1()
{

}

uint32_t f(int num , uint32_t a , uint32_t b , uint32_t c)
{
    return 0;
}

uint32_t f0(uint32_t a , uint32_t b , uint32_t c)
{
    return 0;
}

uint32_t f1(uint32_t a , uint32_t b , uint32_t c)
{
    return 0;
}

uint32_t f2(uint32_t a , uint32_t b , uint32_t c)
{
    return 0;
}

uint32_t f3(uint32_t a , uint32_t b , uint32_t c)
{
    return 0;
}
