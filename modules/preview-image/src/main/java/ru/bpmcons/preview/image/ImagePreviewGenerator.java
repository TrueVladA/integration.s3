package ru.bpmcons.preview.image;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.bpmcons.preview.base.PreviewGenerator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ImagePreviewGenerator implements PreviewGenerator {
    private final Scalr scalr;

    @Override
    public boolean isApplicableTo(String fileExt) {
        return fileExt.equals("jpeg") ||
                fileExt.equals("jpg") ||
                fileExt.equals("png") ||
                fileExt.equals("bmp") ||
                fileExt.equals("tiff") ||
                fileExt.equals("gif");
    }

    @Override
    @SneakyThrows
    public ByteArrayOutputStream generate(InputStream inputStream) {
        BufferedImage image = ImageIO.read(inputStream);
        BufferedImage finalImage = scalr.resize(image, image.getWidth() / 3, image.getHeight() / 3);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(finalImage, "jpeg", out);
        return out;
    }
}
