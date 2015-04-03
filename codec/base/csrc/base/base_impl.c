#ifndef _DOVE_CODEC_BASE_BASE_H_
#define _DOVE_CODEC_BASE_BASE_H_
#endif // _DOVE_CODEC_BASE_BASE_H_

#include <jni.h>

#include "dove_codec_base_Base.h"

#define STANDARD 0
#define FILESAFE 1

char[] base64_Standard = new char[]{
    'A' , 'B' , 'C' , 'D' , 'E' , 'F' , 'G' , 'H' , 'I' ,
    'J' , 'K' , 'L' , 'M' , 'N' , 'O' , 'P' , 'Q' , 'R' ,
    'S' , 'T' , 'U' , 'V' , 'W' , 'X' , 'Y' , 'Z' , 'a' ,
    'b' , 'c' , 'd' , 'e' , 'f' , 'g' , 'h' , 'i' , 'j' ,
    'k' , 'l' , 'm' , 'n' , 'o' , 'p' , 'q' , 'r' , 's' ,
    't' , 'u' , 'v' , 'w' , 'x' , 'y' , 'z' , '0' , '1' ,
    '2' , '3' , '4' , '5' , '6' , '7' , '8' , '9' , '+' ,
    '/'
};

char base64Padding_Standard = '=';

char[] base64_Filesafe = new char[]{
    'A' , 'B' , 'C' , 'D' , 'E' , 'F' , 'G' , 'H' , 'I' ,
    'J' , 'K' , 'L' , 'M' , 'N' , 'O' , 'P' , 'Q' , 'R' ,
    'S' , 'T' , 'U' , 'V' , 'W' , 'X' , 'Y' , 'Z' , 'a' ,
    'b' , 'c' , 'd' , 'e' , 'f' , 'g' , 'h' , 'i' , 'j' ,
    'k' , 'l' , 'm' , 'n' , 'o' , 'p' , 'q' , 'r' , 's' ,
    't' , 'u' , 'v' , 'w' , 'x' , 'y' , 'z' , '0' , '1' ,
    '2' , '3' , '4' , '5' , '6' , '7' , '8' , '9' , '-' ,
    '_'
}

char base64Padding_Filesafe = '=';

char[] base32_Standard = new char[]{
    'A' , 'B' , 'C' , 'D' , 'E' , 'F' , 'G' , 'H' , 'I' ,
    'J' , 'K' , 'L' , 'M' , 'N' , 'O' , 'P' , 'Q' , 'R' ,
    'S' , 'T' , 'U' , 'V' , 'W' , 'X' , 'Y' , 'Z' , '2' ,
    '3' , '4' , '5' , '6' , '7'
}

char base32Padding_Standard = '=';

JNIEXPORT jobject JNICALL Java_dove_codec_base_Base_base64Encode
  (JNIEnv * env, jobject base , jobject in)
{

}

/*
 * Class:     dove_codec_base_Base
 * Method:    base64Decode
 * Signature: (Ljava/io/InputStream;)Ljava/io/OutputStream;
 */
JNIEXPORT jobject JNICALL Java_dove_codec_base_Base_base64Decode
  (JNIEnv * env, jobject base, jobject in)
{
    jobject result =
}

/*
 * Class:     dove_codec_base_Base
 * Method:    base32Encode
 * Signature: (Ljava/io/InputStream;)Ljava/io/OutputStream;
 */
JNIEXPORT jobject JNICALL Java_dove_codec_base_Base_base32Encode
  (JNIEnv * env, jobject base , jobject in)
{

}

/*
 * Class:     dove_codec_base_Base
 * Method:    base32Decode
 * Signature: (Ljava/io/InputStream;)Ljava/io/OutputStream;
 */
JNIEXPORT jobject JNICALL Java_dove_codec_base_Base_base32Decode
  (JNIEnv * env, jobject base, jobject in)
{

}

/*
 * Class:     dove_codec_base_Base
 * Method:    base16Encode
 * Signature: (Ljava/io/InputStream;)Ljava/io/OutputStream;
 */
JNIEXPORT jobject JNICALL Java_dove_codec_base_Base_base16Encode
  (JNIEnv * env, jobject base, jobject in)
{

}

/*
 * Class:     dove_codec_base_Base
 * Method:    base16Decode
 * Signature: (Ljava/io/InputStream;)Ljava/io/OutputStream;
 */
JNIEXPORT jobject JNICALL Java_dove_codec_base_Base_base16Decode
  (JNIEnv *, jobject, jobject)
{

}
