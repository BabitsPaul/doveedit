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
    char* currentBlock;
    if(blockAInUse)
        currentBlock = blockA;
    else
        currentBlock = blockB;

    if(currentBlock - chunk > CHUNKSIZE)
        if(lastChunk){

        }else{
            nextChunk();

            return getNextBlock();
        }
    else{

    }
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
        lastChunk = true;
}

char* BlockLoader::createPadding()
{
    return NULL;
}

char* BlockLoader::getNextBlock()
{

}
