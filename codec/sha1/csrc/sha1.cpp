#include "dove_codec_sha1_SHA1.h"
#include "sha1.h"
#include "blockloader.h"

#include <iostream>
#include <fstream>
#include <stdint.h>
using namespace std;

//constants for calculation
#define K0_19 0x5A827999
#define K20_39 0x6ED9EBA1
#define K40_59 0x8F1BBCDC
#define K60_79 0xCA62C1D6

#define H0 67452301
#define H1 EFCDAB89
#define H2 98BADCFE
#define H3 10325476
#define H4 C3D2E1F0

#define HASHSIZE 20			//size of the resulting hashcode (bytes)

BlockLoader* loader = NULL;

char* block;

const char* file;

extern uint32_t (*fptr[4])(uint32_t , uint32_t , uint32_t);

JNIEXPORT jbyteArray JNICALL Java_dove_codec_sha1_SHA1_sha1
  (JNIEnv * env , jobject sha1 , jstring src)
{
    //request resources
    file = env->GetStringUTFChars(src , 0);
    setup();

    //main routine for generating the hashcode
    generateSHA1();

    //convert hashcode to java-readable data
    jbyteArray result = env->NewByteArray(HASHSIZE);
    env->SetByteArrayRegion(result , 0 , HASHSIZE , (jbyte*) sha1);

    //clean up resources
    cleanUp();
    env->ReleaseStringUTFChars(src , file);

    return result;
}

/**
* void setup()
*
* loads all required resources and allocates
* necassary memory for processing data
*/
void setup()
{
    fptr[0] = f0;
    fptr[1] = f1;
    fptr[2] = f2;
    fptr[3] = f3;

	loader = new BlockLoader(file);
}

void cleanUp()
{
    delete loader;
}

void generateSHA1()
{
    while((block = loader->nextBlock()) != null)
    {
        int32_t* wordPtr = (int32_t*) block;


    }
}

uint32_t f3(uint32_t b, uint32_t c, uint32_t d)
{
    return (b ^ c ^ d);
}

uint32_t f2(uint32_t b, uint32_t c, uint32_t d)
{
    return (b & c) | (b & d) | (c & d);
}

uint32_t f1(uint32_t b, uint32_t c, uint32_t d)
{
    return (b ^ c ^ d);
}

uint32_t f0(uint32_t b, uint32_t c, uint32_t d)
{
    return (b & c) | ((~b) & d);
}

uint32_t f(int num, uint32_t b, uint32_t c, uint32_t d)
{
    return fptr[num / 20](b , c , d);
}
