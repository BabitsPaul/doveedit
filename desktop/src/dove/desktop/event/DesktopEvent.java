package dove.desktop.event;

/**
 * Created by Babits on 17/04/2015.
 */
public class DesktopEvent {
    public static final int DESKTOP_CLOSING    = 0;
    public static final int VIEW_ANGLE_CHANGED = 1;
    public static final int ZOOM_CHANGED       = 2;

    private int code;

    public DesktopEvent(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
