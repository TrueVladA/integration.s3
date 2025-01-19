package ru.bpmcons.preview.text.generator;

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.stream.Collectors;

@Service
public class TxtPreviewGenerator extends AbstractTextToImagePreviewGenerator {
    @Override
    public boolean isApplicableTo(String fileExt) {
        return fileExt.equals("txt");
    }

    @Override
    public ByteArrayOutputStream generate(InputStream inputStream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String text = br.lines()
                .limit(getMaxLines())
                .collect(Collectors.joining("\n"));
        return renderText(text);
    }
}
