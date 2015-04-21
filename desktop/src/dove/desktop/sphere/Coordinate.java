package dove.desktop.sphere;

/**
 * Created by Babits on 20/04/2015.
 */
class Coordinate {
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
