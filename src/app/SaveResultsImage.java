package app;

import app.model.Point;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class SaveResultsImage {

    public static void saveResultsImage(
            String job,
            String fractal,
            int width,
            int height,
            double proportion,
            List<Point> points
    ) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        WritableRaster writableRaster = image.getRaster();
        int[] rgb = new int[3];
        rgb[0] = 255;
        for (Point p : points) {
            writableRaster.setPixel(p.getCoordX(), p.getCoordY(), rgb);
        }

        BufferedImage imageToSave = new BufferedImage(
                writableRaster.getWidth(),
                writableRaster.getHeight(),
                BufferedImage.TYPE_3BYTE_BGR
        );

        imageToSave.setData(writableRaster);
        try {
            if (fractal != null) {
                ImageIO.write(imageToSave, "PNG", new File("fractals/" + job + fractal + "_" + proportion + ".png"));
            } else {
                ImageIO.write(imageToSave, "PNG", new File("fractals/" + job + "_" + proportion + ".png"));
            }
            AppConfig.timestampedStandardPrint("Image rendered successfully.");
        } catch (IOException e) {
            AppConfig.timestampedErrorPrint("Failed to render image");
            e.printStackTrace();
        }
    }

}
