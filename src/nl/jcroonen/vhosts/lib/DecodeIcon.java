package nl.jcroonen.vhosts.lib;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import net.sf.image4j.codec.ico.ICODecoder;

import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class DecodeIcon {
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
            catch (IOException e ) {
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

    public void tryFavicons(Label label, String root) {
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

}
