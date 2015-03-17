package dove.cmd.ui.model;

public class Cursor {
    private InternalCursor cursor;

    public Cursor(InternalCursor cursor) {
        this.cursor = cursor;
    }

    public int getX() {
        return cursor.getX();
    }

    public void setX(int x) {
        cursor.setX(x);
    }

    public int getY() {
        return cursor.getY();
    }

    public void setY(int y) {
        cursor.setY(y);
    }

    public void moveCursorLeft() {
        cursor.moveCursorLeft();
    }

    public void moveCursorRight() {
        cursor.moveCursorRight();
    }

    public void moveCursorDown() {
        cursor.moveCursorDown();
    }

    public void moveCursorUp() {
        cursor.moveCursorUp();
    }
}
