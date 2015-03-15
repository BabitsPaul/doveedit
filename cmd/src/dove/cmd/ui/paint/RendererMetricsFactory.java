package dove.cmd.ui.paint;

import java.awt.*;

public class RendererMetricsFactory {
    private int signWidth;
    private int lineHeight;
    private int lineSpace;
    private int signSpace;
    private int signMaxAscent;

    private int textSpaceLeft;
    private int textSpaceRight;
    private int textSpaceTop;
    private int textSpaceBottom;

    private boolean showCursor;
    private Color   background;

    public void setSignWidth(int signWidth) {
        this.signWidth = signWidth;
    }

    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }

    public void setLineSpace(int lineSpace) {
        this.lineSpace = lineSpace;
    }

    public void setSignSpace(int signSpace) {
        this.signSpace = signSpace;
    }

    public void setSignMaxAscent(int signMaxAscent) {
        this.signMaxAscent = signMaxAscent;
    }

    public void setTextSpaceLeft(int textSpaceLeft) {
        this.textSpaceLeft = textSpaceLeft;
    }

    public void setTextSpaceRight(int textSpaceRight) {
        this.textSpaceRight = textSpaceRight;
    }

    public void setTextSpaceTop(int textSpaceTop) {
        this.textSpaceTop = textSpaceTop;
    }

    public void setTextSpaceBottom(int textSpaceBottom) {
        this.textSpaceBottom = textSpaceBottom;
    }

    public void setShowCursor(boolean showCursor) {
        this.showCursor = showCursor;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public void useMetrics(LayerRendererMetrics metrics) {
        signWidth = metrics.signWidth;
        lineHeight = metrics.lineHeight;
        lineSpace = metrics.lineSpace;
        signSpace = metrics.signSpace;
        signMaxAscent = metrics.signMaxAscent;

        textSpaceLeft = metrics.textSpaceLeft;
        textSpaceRight = metrics.textSpaceRight;
        textSpaceTop = metrics.textSpaceTop;
        textSpaceBottom = metrics.textSpaceBottom;

        showCursor = metrics.showCursor;
        background = metrics.background;
    }

    public LayerRendererMetrics createMetrics() {
        LayerRendererMetrics metrics = new LayerRendererMetrics();

        metrics.signMaxAscent = signMaxAscent;
        metrics.signWidth = signWidth;
        metrics.signSpace = signSpace;
        metrics.lineHeight = lineHeight;
        metrics.lineSpace = lineSpace;

        metrics.textSpaceLeft = textSpaceLeft;
        metrics.textSpaceRight = textSpaceRight;
        metrics.textSpaceTop = textSpaceTop;
        metrics.textSpaceBottom = textSpaceBottom;

        metrics.showCursor = showCursor;
        metrics.background = background;

        return metrics;
    }
}