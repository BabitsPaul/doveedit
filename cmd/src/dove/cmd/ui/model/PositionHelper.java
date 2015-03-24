package dove.cmd.ui.model;

public class PositionHelper {
    private ClipObject clip;

    public PositionHelper(ClipObject clip) {
        this.clip = clip;
    }

    /**
     * returns the position right to the specified position
     * <p>
     * the result for (x,y) is either
     * (x + 1 , y), if the resulting position is in the available
     * screen (clipped, or the bufferbounds), or
     * (0 , y + 1), if the resulting position holds the highest x-position
     * that is still in the screen the screen has another line available
     *
     * @param position the position for which the next position to the right is created
     * @return the position right to the specified position
     */
    public Position right(Position position) {
        int x = position.x;
        int y = position.y;
        boolean isRelative = position.isRelative();

        //convert to absolute position, if the position is relative
        if (isRelative) {
            x = clip.convertToAbsoluteX(x);
            y = clip.convertToAbsoluteY(y);
        }

        //increment x-position to the next on the right
        x += 1;

        //if the x-position is out of the available screen,
        //move the position to the next line, if another line
        //is available, or return the specified position, if the
        //position cant be moved on
        if (!clip.inBoundsX(x)) {
            y += 1;
            x = clip.isEnabled() ? clip.getOffSetX() : 0;

            if (!clip.inBoundsY(y)) {
                x = position.x;
                y = position.y;
            }
        }

        //convert back to relative coordinates, if the initial position was relative
        if (isRelative) {
            x = clip.convertToRelativeX(x);
            y = clip.convertToRelativeY(y);
        }

        return new Position(x, y, isRelative);
    }

    /**
     * returns the position left to the specified position
     * <p>
     * if the specified position is on the most left position
     * of the available screen, and previous lines are remaining, that
     * are in the available screen, the resulting position is (screenWidth - 1 , y - 1)
     * else the resulting position is the specified position
     *
     * @param position the position for which the next position to the left is requested
     * @return the position left to the specified position
     */
    public Position left(Position position) {
        int x = position.x;
        int y = position.y;
        boolean isRelative = position.isRelative;

        //convert to absolute position, if the position is relative
        if (isRelative) {
            x = clip.convertToAbsoluteX(x);
            y = clip.convertToAbsoluteY(y);
        }

        //decrement x-position to the next on the left
        x -= 1;

        //if the x-position is out of the available screen,
        //move the position to the previous line, if another line
        //is available, or return the specified position, if the
        //position cant be moved on
        if (!clip.inBoundsX(x)) {
            y -= 1;
            x = (clip.isEnabled() ? clip.getOffSetX() + clip.getClipWidth() : clip.getWidth()) - 1;

            if (!clip.inBoundsY(y)) {
                x = position.x;
                y = position.y;
            }
        }

        //convert back to relative coordinates, if the initial position was relative
        if (isRelative) {
            x = clip.convertToRelativeX(x);
            y = clip.convertToRelativeY(y);
        }

        return new Position(x, y, isRelative);
    }

    /**
     * returns the position one line up of the specified position
     * <p>
     * result: (x , y - 1), if the specified position (x , y)
     * lies within the available screen,
     * or (x , y), if it doesn't
     *
     * @param position the position for which the next position one line up is requested
     * @return the position on line above the specified position
     */
    public Position up(Position position) {
        int x = position.x;
        int y = position.y;
        boolean isRelative = position.isRelative;

        //if the position is relative, translate it to absolute position
        if (isRelative)
            y = clip.convertToAbsoluteY(y);

        //go one line up
        y -= 1;

        //if the specified position is outside of the
        //available screen, return the specified position
        if (!clip.inBoundsY(y))
            y = position.y;

        //if the position is relative, translate back to relative position
        if (isRelative)
            y = clip.convertToRelativeY(y);

        return new Position(x, y, isRelative);
    }

    /**
     * returns the position one line below the specified position (x , y)
     * <p>
     * result is (x , y + 1), if this position is in the available screen,
     * or (x , y), if there are no lines available under (x , y)
     *
     * @param position the position for which the next position one line below is required
     * @return the position one line below the specified position
     */
    public Position down(Position position) {
        int x = position.getX();
        int y = position.getY();
        boolean isRelative = position.isRelative();

        //if the position is relative, translate it to absolute position
        if (isRelative)
            y = clip.convertToAbsoluteY(y);

        //go one line down
        y += 1;

        //if the specified position is outside of the
        //available screen, return the specified position
        if (!clip.inBoundsY(y))
            y = position.y;

        //if the position is relative, translate back to relative position
        if (isRelative)
            y = clip.convertToRelativeY(y);

        return new Position(x, y, isRelative);
    }

    public Position toAbsolute(Position p) {
        int x = p.x;
        int y = p.y;

        if (p.isRelative) {
            x = clip.convertToAbsoluteX(x);
            y = clip.convertToAbsoluteY(y);
        }

        return new Position(x, y, false);
    }

    public Position toRelative(Position p) {
        int x = p.x;
        int y = p.y;

        if (!p.isRelative) {
            x = clip.convertToRelativeX(x);
            y = clip.convertToRelativeY(y);
        }

        return new Position(x, y, true);
    }

    public static class Position {
        private int x;

        private int y;

        private boolean isRelative;

        public Position(int x, int y, boolean isRelative) {
            this.x = x;
            this.y = y;
            this.isRelative = isRelative;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public boolean isRelative() {
            return isRelative;
        }
    }
}