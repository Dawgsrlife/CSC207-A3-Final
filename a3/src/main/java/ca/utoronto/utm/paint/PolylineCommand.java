package ca.utoronto.utm.paint;

import javafx.scene.canvas.GraphicsContext;

public class PolylineCommand extends SquiggleCommand {
    private Point previewPoint;  // temp point for previewing next segment

    protected void setPreviewPoint(Point point) {
        this.previewPoint = point;
        this.setChanged();
        this.notifyObservers();
    }

    @Override
    public void execute(GraphicsContext g) {
        super.execute(g);  // to draw existing Polyline points

        // Draw the preview line (if any)
        if (!this.getPoints().isEmpty() && previewPoint != null) {
            Point lastPoint = this.getPoints().getLast();
            g.setStroke(this.getColor());
            g.strokeLine(lastPoint.x, lastPoint.y, previewPoint.x, previewPoint.y);
        }
    }
}
