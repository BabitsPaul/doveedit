package dove.desktop.sphere;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by Babits on 16/04/2015.
 */
public class SphereFile {
    private File file;

    private BufferedImage img;

    private double latitude;

    //position equator
    private double longitude;

    public SphereFile(File file, double longitude, double latitude) {
        this.file = file;

        this.latitude = latitude;
        this.longitude = longitude;

        Icon icon = FileSystemView.getFileSystemView().getSystemIcon(file);
        img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        icon.paintIcon(null, img.getGraphics(), 0, 0);
    }

    public BufferedImage getIcon() {
        return img;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitue() {
        return longitude;
    }

    public void setLongitue(double longitue) {
        this.longitude = longitue;
    }

    public File getFile() {
        return file;
    }
}
