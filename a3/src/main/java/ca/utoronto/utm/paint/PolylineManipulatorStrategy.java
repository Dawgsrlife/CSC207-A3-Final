package ca.utoronto.utm.paint;

import javafx.scene.input.MouseEvent;

public class PolylineManipulatorStrategy extends SquiggleManipulatorStrategy {
    private boolean isDrawing = false;

    PolylineManipulatorStrategy(PaintModel paintModel) {
        super(paintModel);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPrimaryButtonDown()) {  // Start a new polyline if none and add a point.
            if (!isDrawing) {
                // Then start a new polyline
                PolylineCommand polylineCommand = new PolylineCommand();
                this.setSquiggleCommand(polylineCommand);  // use the setter from superclass
                this.addCommand(polylineCommand);
                isDrawing = true;
            }
            this.getSquiggleCommand().add(new Point((int) e.getX(), (int) e.getY()));  // use the getter from superclass
        } else if (e.isSecondaryButtonDown() && isDrawing) {  // end the current polyline if isDrawing
            ((PolylineCommand) this.getSquiggleCommand()).setPreviewPoint(null);
            isDrawing = false;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (isDrawing) {
            // Update the preview point:
            ((PolylineCommand) this.getSquiggleCommand()).setPreviewPoint(new Point((int) e.getX(), (int) e.getY()));
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Override for no-op
    }
}
