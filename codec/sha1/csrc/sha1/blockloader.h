#ifndef BLOCKLOADER_H
#define BLOCKLOADER_H

class BlockLoader
{
    public:
        BlockLoader(const char* file);
        ~BlockLoader();

        char* nextBlock();
    private:
        static const int CHUNKSIZE = 4096;
        static const int BLOCKSIZE = 64;

        const char* file;

        long fPos;
        long fSize;

        char* chunk;
        bool lastChunk;
        int chunkSize;

        char* blockA;
        char* blockB;
        bool blockAInUse;
        char* getNextBlock();

        void nextChunk();
        char* createPadding();
};

#endif // BLOCKLOADER_H
