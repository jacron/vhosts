package nl.jcroonen.vhosts;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

import java.util.Dictionary;
import java.util.ResourceBundle;

public class VhostContextMenuCreator {
    private MenuItem createMenuItem(String title, String key) {
        MenuItem menuItem = new MenuItem(title);
        Label label = labelBundleText(key);
        menuItem.setGraphic(label);
        return menuItem;
    }

    private Label labelBundleText(String key) {
        Label label = new Label();
        final ResourceBundle bundle = ResourceBundle.getBundle("bundles.bundle");
        label.setText(bundle.getString(key));
        label.getStyleClass().add("fa");
        return label;
    }
    public ContextMenu createContextMenu(Dictionary<String, String> dictionary,
                                          Dictionary<String, String> metaDictionary) {
        final Action action = new Action(dictionary);
        final ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItemEdit = createMenuItem("Edit conf file", "fa.edit");
        menuItemEdit.setOnAction(ae -> action.editConfigFile());
        MenuItem menuItemEditVhost = createMenuItem("Edit vhost.config", "fa.edit");
        menuItemEditVhost.setOnAction(ae -> action.editVhostConfig());
        MenuItem menuItemError = createMenuItem("View error output", "fa.error");
        menuItemError.setOnAction(ae -> action.openErrorOutput());
        MenuItem menuItemFinder = createMenuItem("Reveal in Finder", "fa.search");
        menuItemFinder.setOnAction(ae -> action.openInFinder());
        MenuItem menuItemGithub = createMenuItem("Github repository", "fa.github");
        menuItemGithub.setOnAction(ae -> action.github());
        MenuItem menuItemIde;
        String ide = metaDictionary.get("ide");
        if (ide == null) {
            if (dictionary.get("File").contains(".wsgi")) {
                ide = "pycharm";
            } else {
                ide = "idea";
            }
        }
        menuItemIde = createMenuItem("Open in " + ide, "fa.lightbulb");
        String finalIde = ide;
        menuItemIde.setOnAction(ae -> action.openIde(finalIde));
        MenuItem menuItemRun = createMenuItem("Open in browser", "fa.link");
        menuItemRun.setOnAction(ae -> action.openServiceDefaultClient());
        contextMenu.getItems().addAll(
                menuItemEdit,
                menuItemEditVhost,
                menuItemError,
                menuItemFinder,
                menuItemGithub,
                menuItemRun,
                menuItemIde
        );
        return contextMenu;
    }
}
