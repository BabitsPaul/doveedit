#include "blockloader.h"
#include <fstream>
using namespace std;

BlockLoader::BlockLoader(const char* file)
{
    this->file = file;

    buffer = new char[BUFFERSIZE];

    state = BLOCKS_AVAILABLE;

    blockPos = 0;
    bufSize = 0;

    fPos = 0L;
}

BlockLoader::~BlockLoader()
{
    delete[] buffer;
}

char* BlockLoader::nextBlock()
{
    if(blockPos >= bufSize)     //last block out of buffer read
        if(state & FILE_END)    //eof is reached -> padding
            createPadding();
        else                    //further chunks can be loaded from file
            nextChunk();        //load next chunk

    char* result = buffer + blockPos;

    blockPos += bufSize;

    return result;
}

void BlockLoader::nextChunk()
{
    ifstream is;
    is.open(file , ios::in | ios::binary);

    is.seekg(fPos);
    is.read(buffer , BUFFERSIZE);

    fPos = is.tellg();
    bufSize = (fPos % BUFFERSIZE);
    blockPos = 0;

    is.close();

    if(bufSize != BUFFERSIZE)
        state |= FILE_END;
}

void BlockLoader::createPadding()
{

}
