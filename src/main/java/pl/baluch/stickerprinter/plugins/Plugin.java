package pl.baluch.stickerprinter.plugins;

import javafx.application.Platform;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Set;

public abstract class Plugin {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private final String name;
    protected boolean exit = false;

    protected Plugin(String name) {
        this.name = name;
    }

    public void addChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    protected void updateProducts() {
        Platform.runLater(() -> support.firePropertyChange("products", null, null));
    }


    public String getName() {
        return this.name;
    }

    public abstract Set<Item> getItems();

    public void setExit() {
        this.exit = true;
    }
}
