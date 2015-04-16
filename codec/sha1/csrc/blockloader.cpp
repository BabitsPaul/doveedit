#include "blockloader.h"
#include <fstream>
using namespace std;

BlockLoader::BlockLoader(const char* file)
{
    this->file = file;

    chunk = new char[CHUNKSIZE];

    blockAInUse = true;
    chunkSize = 0;

    paddingCreated = NO_PADDING_CREATED;

    fPos = 0L;

    ifstream is;
    is.open(file , ios::in | ios::binary);
    is.seekg(0 , ios::end);
    fSize = is.tellg();
    is.close();
}

BlockLoader::~BlockLoader()
{
    delete[] chunk;
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
            if(!paddingCreated)
                createPadding();

            --paddingLeft;

            return (paddingLeft < 0 ? NULL : getNextBlock());
        }else{
            //load next chunk and get block from new chunk
            nextChunk();

            return getNextBlock();
        }
    else{
        //blocks in the currently loaded buffer avaiable
        return getNextBlock();
    }
}

void BlockLoader::nextChunk()
{
    ifstream is;
    is.open(file , ios::in | ios::binary);

    is.seekg(fPos);
    is.read(chunk , CHUNKSIZE);

    fPos = is.tellg();
    chunkSize = (fPos % CHUNKSIZE);

    is.close();

    blockA = chunk;
    blockAInUse = true;

    if(chunkSize != CHUNKSIZE)
        lastChunk = true;
}

void BlockLoader::createPadding()
{
    blockAInUse = true;

    if(chunkSize % BLOCKSIZE + 9 > BLOCKSIZE)
    {
        //the padding isn't fitting into one block
        blockA = chunk + (chunkSize / BLOCKSIZE);//last data in the chunk
        blockB = chunk;//use the first block in the chunk for the rest of the padding

        //start with the first byte that doesn't contain data
        char* paddingA = chunk + chunkSize + 1;
        *paddingA = 1;
        //fill the rest of blockA with 0
        for(; paddingA < blockA + BLOCKSIZE ; paddingA++)
            *paddingA = 0;

        char* paddingB = blockB;
        //fill the start of blockB with 0
        for(; paddingB < blockB + BLOCKSIZE - 8 ; paddingB++)
            *paddingB = 0;
        //fill the end of blockB with the fileSize
        char* fileS = (char*) &fSize;
        for(; paddingB < blockB + BLOCKSIZE ; paddingB++)
            *paddingB = *fileS++;

        paddingLeft = 2;
        paddingCreated = PADDING_CREATED_2_BLOCKS;
    }
    else
    {
        //padding fits into one block
        blockA = chunk + (chunkSize / BLOCKSIZE);//last data in the chunk
        blockB = NULL;

        //start with the first byte that doesn't contain data
        char* paddingA = chunk + chunkSize + 1;
        *paddingA = 1;

        //fill the rest of the block with 0, except for the sizebytes
        for(; paddingA < blockA + BLOCKSIZE - 8 ; paddingA)
            *paddingA = 0;
        //fill the end of the block with the sizebytes
        char* fileS = (char*) &fSize;
        for(; paddingA < blockA + BLOCKSIZE ; paddingA++)
            *paddingA = *fileS++;

        paddingLeft = 1;
        paddingCreated = PADDING_CREATED_1_BLOCK;
    }
}

char* BlockLoader::getNextBlock()
{
    if(!paddingCreated){
        //no padding was created -> move blocks in the chunk
        char* last = (blockAInUse ? blockA : blockB);
        last += BLOCKSIZE;

        if(blockAInUse)
            blockB = last;
        else
            blockA = last;
    }

    //use next block
    blockAInUse = !blockAInUse;

    //return next block to use
    return (blockAInUse ? blockA : blockB);
}
