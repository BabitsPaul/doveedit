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

        static const int NO_PADDING_CREATED = 0;
        static const int PADDING_CREATED_1_BLOCK = 1;
        static const int PADDING_CREATED_2_BLOCKS = 2;

        const char* file;

        long fPos;
        long fSize;

        char* chunk;
        bool lastChunk;
        int chunkSize;

        char* blockA;
        char* blockB;
        bool blockAInUse;
        int paddingCreated;
        int paddingLeft;
        char* getNextBlock();

        void nextChunk();
        void createPadding();
};

#endif // BLOCKLOADER_H
