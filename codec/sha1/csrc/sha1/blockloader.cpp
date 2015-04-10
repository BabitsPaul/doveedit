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

    ifstream is;
    is.open(file , ios::in | ios::binary);
    is.seekg(0 , ios::end);
    fSize = is.tellg();
    is.close();
}

BlockLoader::~BlockLoader()
{
    delete[] buffer;
}

char* BlockLoader::nextBlock()
{
    bool lastBlock = false;

    if(blockPos > bufSize && fPos == fSize - 1 - BUFFERSIZE && blockPos == BUFFERSIZE - BLOCKSIZE)
        //only one block of data remaining
        lastBlock = true;

    if(!lastBlock && blockPos > bufSize)
        //current chunk is read and further chunks are available
        nextChunk();

    if(!lastBlock)
    {
        //block in the current buffer available
        //return next block
        char* tmp = buffer + blockPos;
        blockPos += BLOCKSIZE;

        return tmp;
    }else
    {
        //last block of data reached
        if(bufSize == 0)//TODO correct value
            //padding and remaining data fit in one block
        else
            //padding must be inserted in the next block
    }

    return NULL;
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

char* BlockLoader::createPadding()
{
    return NULL;
}
