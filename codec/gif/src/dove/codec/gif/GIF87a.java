package dove.codec.gif;

import dove.codec.InvalidFormatException;
import dove.codec.InvalidSyntaxException;

import java.awt.*;
import java.util.ArrayList;

public class GIF87a {
    /* gif version */
    private static final String FORMAT = "GIF87a";

    /* image separator */
    private static final byte IMAGE_SEPARATOR = 0x2C;

    /* gif terminator */
    private static final byte GIF_TERMINATOR = 0x3B;

    /* gif extension block */
    private static final byte GIF_EXTENSION = 0x21;

    /* screenWidth */
    public short screenWidth = ~0b0;

    /* screenHeight */
    public short screenHeight = ~0b0;

    /* global color map follows descriptor */
    public boolean M = true;

    /* bits of color resolution ( +1) */
    public byte cr = 7;

    /* bits/pixel in image */
    public byte pixel = 7;

    /* background color */
    public byte background = 5;

    /* format bytes */
    public int formatBytes = FORMAT.length();

    /* descriptor bytes */
    public int descriptorBytes = 7;

    /* global colormap */
    public Color[] globalColorMap = colorMap();

    /* image descriptor */
    public ImageDescriptor[] descriptors;

    ///////////////////////////////////////////////////////////////
    // INPUT
    ///////////////////////////////////////////////////////////////

    private static void copyTo(byte[] to, byte[] src, int startAt) {
        for (int i = startAt, j = 0; j < (src.length); i++, j++)
            to[i] = src[j];
    }

    public void parse(byte[] bytes)
            throws InvalidFormatException,
            InvalidSyntaxException {
        int currentIndex = 0;

        currentIndex += checkFormat(bytes, currentIndex);

        currentIndex += loadDescriptor(bytes, currentIndex);

        currentIndex += loadGlobalColorMap(bytes, currentIndex);

        byte nextBlock = bytes[currentIndex];
        ArrayList<ImageDescriptor> images = new ArrayList<>();

        while (nextBlock != GIF_TERMINATOR) {
            if (nextBlock == GIF_EXTENSION)
                currentIndex += loadExtension(bytes, currentIndex);
            else if (nextBlock == IMAGE_SEPARATOR) {
                currentIndex += loadImageDescriptor(bytes, currentIndex, images);

                ImageDescriptor descriptor = images.get(images.size() - 1);

                currentIndex += loadLocalColorMap(bytes, currentIndex, descriptor);

                currentIndex += loadRaster(bytes, currentIndex, descriptor);
            }
            else
                while (bytes[currentIndex] != GIF_TERMINATOR &&
                        bytes[currentIndex] != GIF_EXTENSION &&
                        bytes[currentIndex] != IMAGE_SEPARATOR)
                    ++currentIndex;
        }
    }

    public int checkFormat(byte[] bytes, int at)
            throws InvalidFormatException {
        for (int i = 0; i < FORMAT.length(); i++)
            if (bytes[i + at] != FORMAT.charAt(i)) {
                String invalidFormat = "";
                for (int j = 0; j < FORMAT.length(); j++)
                    invalidFormat += (char) bytes[j + at];

                throw new InvalidFormatException("invalid gif-format: " + invalidFormat);
            }

        return FORMAT.length();
    }

    public int loadDescriptor(byte[] bytes, int at) {
        screenWidth = 0;
        screenWidth |= (((int) bytes[at]) << 8);
        screenWidth |= bytes[at + 1];
        at += 2;

        screenHeight = 0;
        screenHeight |= (((int) bytes[at]) << 8);
        screenHeight |= bytes[at + 1];
        at += 2;

        byte nByte = bytes[at];

        M = ((nByte & (1 << 7)) != 0);

        cr = (byte) ((nByte >>> 3) & 0b111);

        pixel = (byte) (nByte & 0b111);

        at++;

        background = bytes[at];

        return descriptorBytes;
    }

    public int loadGlobalColorMap(byte[] bytes, int at) {
        if (M) {
            globalColorMap = new Color[pixel];
            for (int c = 0; c < pixel; c++) {
                int bytesPos = c * 3 + at;
                int red = bytes[bytesPos];
                int green = bytes[bytesPos + 1];
                int blue = bytes[bytesPos + 2];

                globalColorMap[c] = new Color(red, green, blue);
            }
        }
        else {
            return 0;
        }

        return pixel * 3;
    }

    public int loadExtension(byte[] bytes, int at)
            throws InvalidSyntaxException {
        if (bytes[at] != GIF_EXTENSION || bytes[at + 1] != 0)
            throw new InvalidSyntaxException("no valid syntax in extension descriptor");

        return 2;
    }

    public int loadImageDescriptor(byte[] bytes, int at, ArrayList<ImageDescriptor> descriptors)
            throws InvalidSyntaxException {
        ImageDescriptor result = new ImageDescriptor();

        if (bytes[at] != IMAGE_SEPARATOR)
            throw new InvalidSyntaxException("no imageseparator found");

        result.imageLeft = (short) ((bytes[at] << 8) | bytes[at + 1]);
        at += 2;

        result.imageTop = (short) ((bytes[at] << 8) | bytes[at + 1]);
        at += 2;

        result.imageWidth = (short) ((bytes[at] << 8) | bytes[at + 1]);
        at += 2;

        result.imageTop = (short) ((bytes[at] << 8) | bytes[at + 1]);
        at += 2;

        byte nByte = bytes[at];
        at++;

        result.M = (((nByte >>> 7) & 1) != 0);
        result.I = (((nByte >>> 6) & 1) != 0);

        result.pixel = (byte) (nByte & 0b111);

        descriptors.add(result);

        return 10;
    }

    public int loadLocalColorMap(byte[] bytes, int at, ImageDescriptor descriptor) {
        if (!descriptor.M)
            return 0;

        descriptor.localColorMap = new Color[descriptor.pixel];

        for (int i = 0; i < descriptor.pixel * 3; i++) {
            int atBytes = at + descriptor.pixel * 3;

            int red = bytes[atBytes];
            int green = bytes[atBytes + 1];
            int blue = bytes[atBytes + 2];

            descriptor.localColorMap[i] = new Color(red, green, blue);
        }

        return descriptor.pixel * 3;
    }

    ///////////////////////////////////////////////////////
    // OUTPUT
    ///////////////////////////////////////////////////////

    public int loadRaster(byte[] bytes, int at, ImageDescriptor descriptor) {
        //TODO LZW-compression
        return 0;
    }

    public byte[] createBytes() {
        byte[] bytes = new byte[formatBytes + descriptorBytes];

        int currentIndex = 0;

        currentIndex += insertFormat(bytes, currentIndex);

        currentIndex += createDescriptor(bytes, currentIndex);

        if (M)
            currentIndex += createGlobalColorMap(bytes, currentIndex);

        currentIndex += createExpansion(bytes, currentIndex);

        for (ImageDescriptor d : descriptors) {
            currentIndex += createImageDescriptor(bytes, currentIndex, d);

            currentIndex += createLocalColorMap(bytes, currentIndex, d);

            currentIndex += createRaster(bytes, currentIndex, d);
        }

        bytes[currentIndex] = GIF_TERMINATOR;

        return bytes;
    }

    public int insertFormat(byte[] bytes, int at) {
        copyTo(bytes, FORMAT.getBytes(), at);

        return FORMAT.length();
    }

    public int createDescriptor(byte[] bytes, int at) {
        int currentIndex = at;

        bytes[currentIndex] = (byte) (screenWidth >>> 8);
        bytes[currentIndex + 1] = (byte) screenWidth;
        currentIndex += 2;

        bytes[currentIndex] = (byte) (screenHeight >>> 8);
        bytes[currentIndex + 1] = (byte) screenHeight;
        currentIndex += 2;

        //create resolution info
        byte nByte = 0;

        if (M)
            nByte |= (1 << 7);

        nByte |= ((cr & 0b111) << 3);
        //3 bit space
        nByte |= pixel & 0b111;

        bytes[currentIndex] = nByte;
        ++currentIndex;

        bytes[currentIndex] = background;
        ++currentIndex;

        bytes[currentIndex] = 0;
        ++currentIndex;

        return 7;
    }

    public int createGlobalColorMap(byte[] bytes, int index) {
        int endIndex = index + globalColorMap.length * 3;

        for (int i = index, j = 0; i < endIndex; i += 3, j++) {
            bytes[i] = (byte) globalColorMap[j].getRed();
            bytes[i + 1] = (byte) globalColorMap[j].getGreen();
            bytes[i + 2] = (byte) globalColorMap[j].getBlue();
        }

        return endIndex - index;
    }

    public int createImageDescriptor(byte[] bytes, int at, ImageDescriptor descriptor) {
        bytes[at] = IMAGE_SEPARATOR;
        at++;

        bytes[at] = (byte) (descriptor.imageLeft >>> 8);
        bytes[at + 1] = (byte) descriptor.imageLeft;
        at += 2;

        bytes[at] = (byte) (descriptor.imageTop >>> 8);
        bytes[at + 1] = (byte) descriptor.imageTop;
        at += 2;

        bytes[at] = (byte) (descriptor.imageWidth >>> 8);
        bytes[at + 1] = (byte) descriptor.imageWidth;
        at += 2;

        bytes[at] = (byte) (descriptor.imageHeight >>> 8);
        bytes[at + 1] = (byte) descriptor.imageHeight;
        at += 2;

        byte nByte = 0;

        if (descriptor.M)
            nByte |= (1 << 7);

        if (descriptor.I)
            nByte |= (1 << 6);

        nByte |= (descriptor.pixel & 0b111);

        bytes[at] = nByte;

        return 10;
    }

    public int createLocalColorMap(byte[] bytes, int at, ImageDescriptor d) {
        if (d.M)
            return 0;

        for (int i = 0; i < d.pixel; i++) {
            int bytesPos = at + i * 3;

            bytes[bytesPos] = (byte) d.localColorMap[i].getRed();
            bytes[bytesPos + 1] = (byte) d.localColorMap[i].getGreen();
            bytes[bytesPos + 2] = (byte) d.localColorMap[i].getBlue();
        }

        return (d.pixel * 3);
    }

    public int createRaster(byte[] bytes, int at, ImageDescriptor d) {
        if (d.I) {
            copyTo(bytes, d.raster, at);
        }
        else {
            int imgSize = d.imageWidth * d.imageHeight;

            int bPos = 0;

            //Pass 1
            for (int i = 0; i < imgSize; i += 8, bPos++)
                bytes[bPos] = d.raster[i];

            //Pass 2
            for (int i = 4; i < imgSize; i += 8, bPos++)
                bytes[bPos] = d.raster[i];

            //Pass 3
            for (int i = 2; i < imgSize; i += 4, bPos++)
                bytes[bPos] = d.raster[i];

            //Pass 4
            for (int i = 1; i < imgSize; i += 2, bPos++)
                bytes[bPos] = d.raster[i];
        }

        return (d.imageWidth * d.imageHeight);
    }

    public int createExpansion(byte[] bytes, int at) {
        bytes[at] = GIF_EXTENSION;
        bytes[at + 1] = 0;

        //no expansions in 87a

        return 2;
    }

    private Color[] colorMap() {
        Color[] result = new Color[(int) Math.pow(2, cr & 0x111)];

        return result;
    }

    public class ImageDescriptor {
        /* image start left */
        public short imageLeft;

        /* image start top */
        public short imageTop;

        /* image width */
        public short imageWidth;

        /* image height */
        public short imageHeight;

        /* use global color map */
        public boolean M;

        /* sequential (true) or interlaced order */
        public boolean I;

        /* bits per pixel */
        public byte pixel;

        /* local colormap */
        public Color[] localColorMap;

        /* image raster */
        public byte[] raster;
    }
}
