package ru.bpmcons.preview.powerpoint;

import lombok.SneakyThrows;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.springframework.stereotype.Service;
import ru.bpmcons.preview.base.PreviewGenerator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
public class PowerPointPreviewGenerator implements PreviewGenerator {
    @Override
    public boolean isApplicableTo(String fileExt) {
        return fileExt.startsWith("pp") || fileExt.startsWith("po");
    }

    @Override
    @SneakyThrows
    public ByteArrayOutputStream generate(InputStream inputStream) {
        XMLSlideShow ppt = new XMLSlideShow(inputStream);
        Dimension pgsize = ppt.getPageSize();

        BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = img.createGraphics();
        graphics.setPaint(Color.white);
        graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));
        ppt.getSlides().get(0).draw(graphics);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "jpeg", out);
        return out;
    }
}
