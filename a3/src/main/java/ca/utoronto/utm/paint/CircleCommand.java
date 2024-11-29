package ca.utoronto.utm.paint;

import javafx.scene.canvas.GraphicsContext;

public class CircleCommand extends PaintCommand {
    private Point centre;
    private int radius;

    public CircleCommand(Point centre, int radius) {
        this.centre = centre;
        this.radius = radius;
    }

    public Point getCentre() {
        return centre;
    }

    public void setCentre(Point centre) {
        this.centre = centre;
        this.setChanged();
        this.notifyObservers();
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        this.setChanged();
        this.notifyObservers();
    }

    public void execute(GraphicsContext g) {
        int x = this.getCentre().x;
        int y = this.getCentre().y;
        int radius = this.getRadius();
        if (this.isFill()) {
            g.setFill(this.getColor());
            g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
        } else {
            g.setStroke(this.getColor());
            g.strokeOval(x - radius, y - radius, 2 * radius, 2 * radius);
        }
    }

    @Override
    public String getPaintSaveFileString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Circle\n");

        // Reuse common instructions:
        String details = super.getPaintSaveFileString();
        int startIndex = details.indexOf("color:");
        details = details.substring(startIndex);
        sb.append(details);

        sb.append("\tcenter:(").append(getCentre().x).append(",").append(getCentre().y).append(")\n");
        sb.append("\tradius:").append(radius).append("\n");
        sb.append("EndCircle\n");

        return sb.toString();
    }
}
