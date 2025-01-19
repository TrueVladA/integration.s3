package ru.bpmcons.preview.pdf;

import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import ru.bpmcons.preview.base.PreviewGenerator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
public class PdfPreviewGenerator implements PreviewGenerator {
    @Override
    public boolean isApplicableTo(String fileExt) {
        return fileExt.equals("pdf");
    }

    @Override
    @SneakyThrows
    public ByteArrayOutputStream generate(InputStream inputStream) {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpeg", out);
            return out;
        }
    }
}
