package ca.utoronto.utm.paint;

import java.util.Observable;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class PaintCommand extends Observable implements PaintSaveFileSavable {
    private Color color;
    private boolean fill;

    PaintCommand() {
        // Pick a random color for this
        int r = (int) (Math.random() * 256);
        int g = (int) (Math.random() * 256);
        int b = (int) (Math.random() * 256);
        this.color = Color.rgb(r, g, b);

        this.fill = (1 == (int) (Math.random() * 2));
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isFill() {
        return fill;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }

    public String toString() {
        double r = this.color.getRed();
        double g = this.color.getGreen();
        double b = this.color.getBlue();

        String s = "";
        s += "\tcolor:" + (int) Math.round(r * 255) + "," + (int) Math.round(g * 255) + "," + (int) Math.round(b * 255) + "\n";
        s += "\tfilled:" + this.fill + "\n";
        return s;
    }

    public abstract void execute(GraphicsContext g);

    @Override
    public String getPaintSaveFileString() {
        return this.toString();  // return the default toString() behaviour; can be overwritten
    }
}
