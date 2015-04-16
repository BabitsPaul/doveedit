package dove.desktop.io;

import dove.desktop.sphere.FileSphere;
import dove.desktop.sphere.SphereFile;
import dove.desktop.ui.EventRedirect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Babits on 16/04/2015.
 */
public class SphereLoader {
    private static final String DESKTOP_FILES = "..resources/desktop.bin";

    private EventRedirect redirect;

    public SphereLoader(EventRedirect redirect) {
        this.redirect = redirect;
    }

    public FileSphere load()
            throws IOException {
        FileSphere result = new FileSphere(redirect);

        FileInputStream fis = new FileInputStream(DESKTOP_FILES);

        byte[] bytes = new byte[8];
        fis.read(bytes);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        result.setZoom(buffer.getDouble());

        while (fis.available() > 0) {
            int pathSize = 0;
            byte[] tmp = new byte[2];
            fis.read(tmp);
            pathSize = ((int) tmp[0]) << 8 | ((int) tmp[1]);

            tmp = new byte[pathSize];
            fis.read(tmp);
            File f = new File(new String(tmp));

            tmp = new byte[8];
            fis.read(tmp);
            buffer = ByteBuffer.wrap(tmp);
            double longitude = buffer.getDouble();

            fis.read(tmp);
            buffer = ByteBuffer.wrap(tmp);
            double latitude = buffer.getDouble();

            result.add(new SphereFile(f, longitude, latitude));
        }

        fis.close();

        return result;
    }

    public void save(FileSphere sphere)
            throws IOException {
        FileOutputStream fos = new FileOutputStream(DESKTOP_FILES);

        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putDouble(sphere.getZoom());
        fos.write(buffer.array());

        for (SphereFile file : sphere)
            fos.write(getBytes(file));

        fos.flush();
        fos.close();
    }

    private byte[] getBytes(SphereFile file) {
        //double length = 8 bytes
        //format: sizebyte1 sizebyte2 pathbyte1 ... pathbyten longitudebyte1 ...
        int blockSize = file.getFile().getAbsolutePath().length() + 17;

        ByteBuffer buffer = ByteBuffer.allocate(blockSize);
        buffer.putShort(0, (short) file.getFile().getAbsolutePath().length());
        buffer.put(file.getFile().getAbsolutePath().getBytes());
        buffer.putDouble(file.getLongitue());
        buffer.putDouble(file.getLatitude());

        return buffer.array();
    }
}
