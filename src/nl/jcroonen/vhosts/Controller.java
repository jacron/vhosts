package nl.jcroonen.vhosts;

import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import net.sf.image4j.codec.ico.ICODecoder;
import nl.jcroonen.vhosts.lib.FS;
import nl.jcroonen.vhosts.lib.VHosts;
import nl.jcroonen.vhosts.model.VHost;

import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import java.util.List;
import java.util.ResourceBundle;

public class Controller {
    @FXML private TilePane tiles;
    @FXML private TextField filter;
    @FXML private Label filterClear;
    @FXML private void openVHostsDir() { Action.openVhostConfigsDir(); }
    @FXML private void refresh() { reinit(); }
    @FXML private void onFilterAction() { onFilterChange(false); }
    @FXML private void onFilterClear() { onFilterChange(true); }
    @FXML private void onHosts() { hosts(); }

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

    private ContextMenu createContextMenu(Dictionary<String, String> dictionary,
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

    private String getRoot(Dictionary<String, String> dictionary) {
        if (dictionary.get("DocumentRoor") != null) {
            return dictionary.get("DocumentRoot");
        } else {
            return dictionary.get("Directory");
        }
    }

    private Label createMetaLabel(Dictionary<String, String> dictionary) {
        String description = dictionary.get("description");
        Label label = new Label(description);
        label.setPrefWidth(230);
        label.setWrapText(true);
        return label;
    }

    private ImageView addFavicon(String q) {
        File file = new File(q);
        if (file.exists()) {
            List<BufferedImage> image = null;
            try {
                image = ICODecoder.read(file);
            }
            catch (EOFException e) {
                System.out.println(q);
//                e.printStackTrace();
                System.out.println(e.getMessage());
            }
            catch (IOException  e ) {
                System.out.println(q);
                System.out.println(e.getMessage());
//                e.printStackTrace();
            }
            if (image != null) {
                BufferedImage bigImage = biggestImage(image);
                return new ImageView(SwingFXUtils.toFXImage(bigImage, null));
            }
        }
        return null;
    }

    private BufferedImage biggestImage(List<BufferedImage> image) {
        double maxX = 0;
        BufferedImage biggest = null;
        for (BufferedImage b : image) {
            if (b.getRaster().getWidth() > maxX) {
                biggest = b;
                maxX = b.getRaster().getWidth();
            }
        }
        return biggest;
    }

    private void tryFavicons(Label label, String root) {
        ImageView imageView;
        String[] subdirs = {"public/", "app/", "src/", "public_html/", ""};
        for (String subdir : subdirs) {
            imageView = addFavicon(String.format("%s/%sfavicon.ico", root, subdir));
            if (imageView != null) {
                imageView.setFitWidth(32);
                imageView.setFitHeight(32);
                label.setGraphic(imageView);
                break;
            }
        }
    }

    private Label createNameLabel(Dictionary<String, String> dictionary,
                                  Dictionary<String, String> metaDictionary) {
        Label label = new Label(dictionary.get("ServerName"));
        label.getStyleClass().add("tile-title");
        String port = dictionary.get("VirtualHost");
        if (!port.equals("*:80")) {
            label.setText(label.getText() + port.substring(1));
        }
        if (FS.exists(getRoot(dictionary))) {
            label.getStyleClass().add("active");
            tryFavicons(label, getRoot(dictionary));
//            addFavicon(label, getRoot(dictionary));
        }
        label.setContextMenu(createContextMenu(dictionary, metaDictionary));
        return label;
    }

    private VBox createTile(Dictionary<String, String> dictionary) {
        VBox tile = new VBox(8);
        Dictionary<String, String> metaDictionary = FS.metaVhost(getRoot(dictionary));
//        if (FS.exists(getRoot(dictionary))) {
//            ImageView imageView = addFavicon(getRoot(dictionary) + "/public/favicon.ico");
//            if (imageView != null) {
//                tile.getChildren().add(imageView);
//            }
//        }
        tile.getChildren().addAll(
                createNameLabel(dictionary, metaDictionary),
                createMetaLabel(metaDictionary));
        tile.setPadding(new Insets(0, 0, 12, 0));
        tile.setPrefWidth(240);
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

    private void sort() {
        try {
            vHosts = VHosts.getList();
            vHosts.sort((a, b) -> {
                    String nameA = a.getDictionary().get("ServerName");
                    String nameB = b.getDictionary().get("ServerName");
                    return nameA.compareTo(nameB);
            });
            populateTiles(vHosts, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hosts() {
        VHosts.openHosts();
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
        sort();
    }

    public void init() {
        this.bundle = ResourceBundle.getBundle("bundles.bundle");
        reinit();
        filterClear.setText("");
    }
}
