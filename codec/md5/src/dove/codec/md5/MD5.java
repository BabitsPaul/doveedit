package dove.codec.md5;

import dove.util.bitop.Word32;
import dove.util.concurrent.Returnable;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * represents the MD5-algorithm
 * <p>
 * provides all necassary utilitymethods to
 * create a MD5-hash from a given File/bytearray
 * <p>
 * specified by RFC-1321
 */
public class MD5 {
    private static final byte[] t = generateT();

    private static final int A = 0;
    private static final int B = 1;
    private static final int C = 2;
    private static final int D = 3;

    private byte[] x = new byte[64];

    private boolean next_block_available = true;

    private boolean stream_read = false;

    private static byte[] generateT() {
        byte[] t = new byte[64 * 4];

        for (int i = 0; i < 64; i++) {
            int temp = (int) ((int) (4294967296l) * Math.abs(Math.sin(i)));

            //register the next sinusvalue in t
            for (int j = 0; j < 4; j++)
                t[i * 4 + j] = (byte) (0xFF & temp >> ((3 - j) * 8));
        }

        return t;
    }

    public byte[] md5(Reader reader) {
        byte[] md5 = new byte[16];


        return md5;
    }

    private void nextBlock(InputStream reader)
            throws IOException {
        if (next_block_available) {
            int v = reader.read(x, 0, 64);

            if (v == 64)
                return;

            //result block is smaller than 64 -> append padding bits
            x[v] = (byte) 0x80;

            for (int i = v + 1; i < 56; i++)
                x[i] = 0x00;

            next_block_available = false;

            if (v < 56)
                stream_read = true;
        }
        else {
            for (int i = 0; i < 56; i++)
                x[i] = 0x00;

            stream_read = true;
        }

    }

    private void round(int round, byte[] a, byte[] b, byte[] c, byte[] d) {
        byte[][] abcd = new byte[][]{a, b, c, d};

        int k, s, i;

        i = round * 16 + 1;

        //the operation performed by this round
        Returnable<byte[]> r;
        switch (round) {
            case 0:
                r = () -> f(b, c, d);
                k = 0;
                s = 7;
                break;
            case 1:
                r = () -> g(b, c, d);
                k = 1;
                s = 5;
                break;
            case 2:
                r = () -> h(b, c, d);
                k = s = 0;
                break;
            case 3:
                k = s = 0;
                r = () -> i(b, c, d);
                break;
            default:
                r = null;
                k = s = i = 0;
                break;
        }

        for (int o = 0; o < 16; o++) {
            op(null, k, s, i, r);

            i += 1;
            k += 1;

            s = (((o + 1) % 4) == 0 ? 7 : s + 5);

            //rotate the content of abcd to the right
            rotateContent(abcd, 1);
     /* Round 1. */
     /* Let [abcd k s i] denote the operation
          a = b + ((a + F(b,c,d) + X[k] + T[i]) <<< s). */
     /* Do the following 16 operations.
            [ABCD  0  7  1]  [DABC  1 12  2]  [CDAB  2 17  3]  [BCDA  3 22  4]
            [ABCD  4  7  5]  [DABC  5 12  6]  [CDAB  6 17  7]  [BCDA  7 22  8]
            [ABCD  8  7  9]  [DABC  9 12 10]  [CDAB 10 17 11]  [BCDA 11 22 12]
            [ABCD 12  7 13]  [DABC 13 12 14]  [CDAB 14 17 15]  [BCDA 15 22 16]
        */
        }
    }

    private void op(byte[][] abcd, int k, int s, int i, Returnable<byte[]> op) {
        Word32 o_v = new Word32(op.run());

        Word32 x_v = new Word32(new byte[]{x[k], x[k + 1], x[k + 2], x[k + 3]});
        Word32 t_v = new Word32(new byte[]{t[i], t[i + 1], t[i + 2], t[i + 3]});

        Word32 temp = Word32.add(new Word32(abcd[A]), o_v, x_v, t_v);
        rotate(temp.getBytes(), s);
        abcd[A] = Word32.add(new Word32(abcd[B]), temp).getBytes();
    }

    private byte[] f(byte[] x, byte[] y, byte[] z) {
        byte[] f = new byte[4];

        for (int i = 0; i < 4; i++)
            f[i] = (byte) (x[i] & y[i] | ~x[i] & z[i]);

        return f;
    }

    private byte[] g(byte[] x, byte[] y, byte[] z) {
        byte[] g = new byte[4];

        for (int i = 0; i < 4; i++)
            g[i] = (byte) (x[i] & z[i] | y[i] & ~z[i]);

        return g;
    }

    private byte[] h(byte[] x, byte[] y, byte[] z) {
        byte[] h = new byte[4];

        for (int i = 0; i < 4; i++)
            h[i] = (byte) (x[i] ^ y[i] ^ z[i]);

        return h;
    }

    private byte[] i(byte[] x, byte[] y, byte[] z) {
        byte[] i = new byte[4];

        for (int j = 0; j < 4; j++)
            i[j] = (byte) (y[j] ^ (x[j] | ~z[j]));

        return i;
    }

    private void rotate(byte[] b, int shift) {
        //create a helperarray and overwrite every byte with 0
        byte[] helper = new byte[b.length];
        for (int i = 0; i < 4; i++)
            helper[i] = 0x00;

        //the number of bytes every byte will be shifted to the left
        int byte_shift = shift / 8;
        //the number of bits every bit will be shifted left
        int bit_shift = shift % 8;

        for (int i = 0; i < b.length; i++) {
            //the byte at which the last bit of b[i] will be stored
            int nbyte = (i + byte_shift) % b.length;

            //expand the value of b[i] to two bits
            //(needed due to the shift)
            short val = (short) (b[i] << bit_shift);

            //transfer val to helper[nbyte] and the next byte after nbyte
            helper[nbyte] |= val;
            helper[(nbyte + 1) % b.length] |= (val >> 8);
        }

        //copy all bytes back to b
        System.arraycopy(helper, 0, b, 0, helper.length);
    }

    private void rotateContent(Object[] b, int rotate_right) {
        if (b.length == 0 || rotate_right == 0)
            return;

        Object temp0 = b[0];

        System.arraycopy(b, 1, b, 0, b.length - 1);

        b[b.length - 1] = temp0;
    }
}