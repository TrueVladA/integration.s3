package ru.bpmcons.preview.doc;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.documents.ImageType;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.bpmcons.preview.base.PreviewGenerator;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
public class DocPreviewGenerator implements PreviewGenerator {
    @Override
    public boolean isApplicableTo(String fileExt) {
        return fileExt.startsWith("do");
    }

    @Override
    @SneakyThrows
    public ByteArrayOutputStream generate(InputStream inputStream) {
        Document doc = new Document();
        doc.loadFromStream(inputStream, FileFormat.Auto);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(doc.saveToImages(0, ImageType.Bitmap), "jpeg", out);
        return out;
    }
}
