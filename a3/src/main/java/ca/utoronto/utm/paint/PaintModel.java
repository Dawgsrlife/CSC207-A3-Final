package ca.utoronto.utm.paint;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javafx.scene.canvas.GraphicsContext;

public class PaintModel extends Observable implements Observer {

    public void save(PrintWriter writer) {
        for (PaintSaveFileSavable command : this.commands) {
            writer.print(command.getPaintSaveFileString());
        }
    }

    public void reset() {
        for (PaintCommand c : this.commands) {
            c.deleteObserver(this);
        }
        this.commands.clear();
        this.setChanged();
        this.notifyObservers();
    }

    public void addCommand(PaintCommand command) {
        this.commands.add(command);
        command.addObserver(this);
        this.setChanged();
        this.notifyObservers();
    }

    private ArrayList<PaintCommand> commands = new ArrayList<PaintCommand>();


    public void executeAll(GraphicsContext g) {
        for (PaintCommand c : this.commands) {
            c.execute(g);
        }
    }

    /**
     * We Observe our model components, the PaintCommands
     */
    @Override
    public void update(Observable o, Object arg) {
        this.setChanged();
        this.notifyObservers();
    }
}
