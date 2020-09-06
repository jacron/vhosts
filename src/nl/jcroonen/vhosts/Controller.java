package nl.jcroonen.vhosts;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import nl.jcroonen.vhosts.lib.VHosts;
import nl.jcroonen.vhosts.model.VHost;

import java.util.Dictionary;
import java.util.ResourceBundle;

public class Controller {
    @FXML private TilePane tiles;
    @FXML private ComboBox<String> sortcombobox;
    @FXML private TextField filter;
    @FXML private Label filterClear;
    @FXML private void openVHostsDir() { Action.openVhostConfigsDir(); }
    @FXML private void refresh() { reinit(); }
    @FXML private void onFilterAction() { onFilterChange(false); }
    @FXML private void onFilterClear() {onFilterChange(true);}

    private ResourceBundle bundle;
    ObservableList<VHost> vHosts;
    private Label labelBundleText(String key) {
        Label label = new Label();
        label.setText(bundle.getString(key));
        label.getStyleClass().add("fa");
        return label;
    }

    private MenuItem createMenuItem(String title, String key) {
        MenuItem menuItem = new MenuItem(title);
        Label label = labelBundleText(key);
        menuItem.setGraphic(label);
        return menuItem;
    }

    private ContextMenu createContextMenu(Dictionary<String, String> dictionary) {
        final Action action = new Action(dictionary);
        final ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItemEdit = createMenuItem("Edit conf file", "fa.edit");
        menuItemEdit.setOnAction(ae -> action.editConfigFile());
        MenuItem menuItemError = createMenuItem("View error output", "fa.error");
        menuItemError.setOnAction(ae -> action.openErrorOutput());
        MenuItem menuItemFinder = createMenuItem("Reveal in Finder", "fa.search");
        menuItemFinder.setOnAction(ae -> action.openInFinder());
        MenuItem menuItemGithub = createMenuItem("Github repository", "fa.github");
        menuItemGithub.setOnAction(ae -> action.github());
        MenuItem menuItemIde;
        if (dictionary.get("File").contains(".wsgi")) {
            menuItemIde = createMenuItem("Open in Pycharm", "fa.lightbulb");
            menuItemIde.setOnAction(ae -> action.openIde("pycharmPath"));
        } else {
            menuItemIde = createMenuItem("Open in IntelliJ", "fa.lightbulb");
            menuItemIde.setOnAction(ae -> action.openIde("intelliJPath"));
        }
        MenuItem menuItemRun = createMenuItem("Open in browser", "fa.link");
        menuItemRun.setOnAction(ae -> action.openServiceDefaultClient());
        contextMenu.getItems().addAll(
                menuItemEdit,
                menuItemError,
                menuItemFinder,
                menuItemGithub,
                menuItemIde,
                menuItemRun
        );
        return contextMenu;
    }

    private VBox createTile(Dictionary<String, String> dictionary) {
        Label name = new Label(dictionary.get("ServerName"));
        String port = dictionary.get("VirtualHost");
        if (!port.equals("*:80")) {
            name.setText(name.getText() + port.substring(1));
        }
        VBox tile = new VBox(8);
        tile.getChildren().addAll(name);
        name.setContextMenu(createContextMenu(dictionary));
        tile.setPadding(new Insets(0, 0, 12, 0));
        tile.setPrefWidth(220);
        return tile;
    }

    private void populateTiles(ObservableList<VHost> VHosts, String filter) {
        tiles.getChildren().clear();
        for (VHost vHost : VHosts) {
            if (filter == null || vHost.getDictionary().get("ServerName").contains(filter)) {
                tiles.getChildren().add(createTile(vHost.getDictionary()));
            }
        }
    }

    private void sort(int choice) {
        try {
            vHosts = VHosts.getList();
            if (choice == 1) {
                vHosts.sort((a, b) -> {
                        String nameA = a.getDictionary().get("ServerName");
                        String nameB = b.getDictionary().get("ServerName");
                        return nameA.compareTo(nameB);
                });
            }
            populateTiles(vHosts, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateSortbox() {
        String[] choices = {"- sort -", "Title"};
        sortcombobox.getItems().clear();
        sortcombobox.getItems().addAll(choices);
        sortcombobox.getSelectionModel().selectedIndexProperty().addListener(
                (ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                    int choice = new_val.intValue();
                    sort(choice);
                });
    }

    private void onFilterChange(Boolean clear) {
//        System.out.println(filter.getText());
        String filterText;
        if (clear) {
            filter.setText("");
            filterText = "";
        } else {
            filterText = filter.getText();
        }
        if (filterText.length() == 0) {
            filterClear.setText("");
        } else {
            filterClear.setText("x");
        }
        populateTiles(vHosts, filterText);
    }

    private void reinit() {
        sortcombobox.getSelectionModel().select(1);
        sort(1);
    }

    public void init() {
        this.bundle = ResourceBundle.getBundle("bundles.bundle");
        populateSortbox();
        reinit();
        filterClear.setText("");
    }
}
