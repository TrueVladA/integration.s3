package ru.bpmcons.preview.text.generator;

import com.opencsv.CSVReader;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class CsvPreviewGenerator extends AbstractTextToImagePreviewGenerator {
    @Override
    public boolean isApplicableTo(String fileExt) {
        return fileExt.equals("csv");
    }

    @Override
    @SneakyThrows
    public ByteArrayOutputStream generate(InputStream inputStream) {
        StringBuilder str = new StringBuilder();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream))) {
            int lines = 0;
            while (lines <= getMaxLines()) {
                String[] line = csvReader.readNext();
                if (line == null) {
                    break;
                }
                for (String part : line) {
                    str.append(part).append(' ');
                }
                lines += 1;
            }
        }
        return renderText(str.toString());
    }
}
