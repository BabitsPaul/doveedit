package dove.desktop.sphere;

import dove.desktop.io.SphereLoader;
import dove.desktop.ui.EventRedirect;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class FileSphere
        implements Iterable<SphereFile> {
    private HashMap<Coordinate, SphereFile> files;
    private double                          zoom;

    public FileSphere(EventRedirect redirect) {
        files = new HashMap<>();

        zoom = 1.0;
    }

    public static FileSphere createInstance(EventRedirect redirect)
            throws IOException {
        return new SphereLoader(redirect).load();
    }

    @Override
    public Iterator<SphereFile> iterator() {
        return files.values().iterator();
    }

    public void add(SphereFile file) {
        files.put(new Coordinate(file.getLongitue(), file.getLatitude()), file);
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    private class Coordinate {
        private double longitude, latitude;

        public Coordinate(double longitude, double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public int hashCode() {
            return Double.hashCode(longitude) ^ Double.hashCode(latitude);
        }

        public boolean equals(Object o) {
            return (o != null && o instanceof Coordinate &&
                    ((Coordinate) o).latitude == latitude && ((Coordinate) o).longitude == longitude);
        }
    }
}
