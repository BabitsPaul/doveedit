package dove.cmd.ui.model;

public class ClipObject
        extends CommandLineElement {
    public static final int CLIPPING_ENABLED = 0;

    public static final int CLIPPING_DISABLED = 1;

    private boolean isEnabled;

    private int offSetX;

    private int offSetY;

    private int clipWidth;

    private int clipHeight;

    private int width;

    private int height;

    public ClipObject(int width, int height) {
        isEnabled = false;

        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return (isEnabled ? clipWidth : width);
    }

    public int getHeight() {
        return (isEnabled ? clipHeight : height);
    }

    public int convertToRelativeX(int absolutex) {
        return (isEnabled ? absolutex - offSetX : absolutex);
    }

    public int convertToRelativeY(int absolutey) {
        return (isEnabled ? absolutey - offSetY : absolutey);
    }

    public int convertToAbsoluteX(int relativeX) {
        return (isEnabled ? relativeX + offSetX : relativeX);
    }

    public int convertToAbsoluteY(int relativeY) {
        return (isEnabled ? relativeY + offSetY : relativeY);
    }

    public boolean inBoundsX(int absolutex) {
        if (isEnabled) {
            return absolutex >= offSetX && absolutex < offSetX + clipWidth;
        }
        else {
            return absolutex >= 0 && absolutex < width;
        }
    }

    public boolean inBoundsY(int absolutey) {
        if (isEnabled)
            return absolutey >= offSetY && absolutey < offSetY + clipHeight;
        else
            return absolutey >= 0 && absolutey < height;
    }

    public int getOffSetX() {
        return offSetX;
    }

    public int getOffSetY() {
        return offSetY;
    }

    public int getClipWidth() {
        return clipWidth;
    }

    public int getClipHeight() {
        return clipHeight;
    }

    public void enableClipping(int x, int y, int width, int height) {
        offSetY = y;
        offSetX = x;
        this.clipWidth = width;
        this.clipHeight = height;

        isEnabled = true;

        fireCommandLineEvent(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.CLIPPING, CLIPPING_ENABLED));
    }

    public void reverseClipping() {
        isEnabled = false;

        fireCommandLineEvent(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.CLIPPING, CLIPPING_DISABLED));
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
