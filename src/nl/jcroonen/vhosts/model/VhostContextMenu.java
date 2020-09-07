package nl.jcroonen.vhosts.model;

import javafx.scene.control.ContextMenu;
import nl.jcroonen.vhosts.Action;

public class VhostContextMenu extends ContextMenu {
    private Action action;

    public VhostContextMenu(Action action) {
        super();
        this.action = action;
    }

}
