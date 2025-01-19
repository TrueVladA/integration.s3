package ru.bpmcons.preview.text.generator;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
public class RtfPreviewGenerator extends AbstractTextToImagePreviewGenerator {
    @Override
    public boolean isApplicableTo(String fileExt) {
        return fileExt.equals("rtf");
    }

    @SneakyThrows
    @Override
    public ByteArrayOutputStream generate(InputStream inputStream) {
        RTFEditorKit rtfParser = new RTFEditorKit();
        Document document = rtfParser.createDefaultDocument();
        rtfParser.read(inputStream, document, 0);
        String text = document.getText(0, document.getLength());
        return renderText(text);
    }
}
