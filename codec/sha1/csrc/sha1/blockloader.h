#ifndef BLOCKLOADER_H
#define BLOCKLOADER_H

class BlockLoader
{
    public:
        static const int BLOCKS_AVAILABLE = 0;
        static const int FILE_END = 1;
        static const int NO_BLOCKS_AVAILABLE = 2;

        BlockLoader(const char* file);
        ~BlockLoader();

        char* nextBlock();
    private:
        static const int BUFFERSIZE = 4096;
        static const int BLOCKSIZE = 64;

        const char* file;

        long fPos;

        char* buffer;
        int blockPos;
        int bufSize;

        int state;

        void nextChunk();
        void createPadding();
};

#endif // BLOCKLOADER_H
