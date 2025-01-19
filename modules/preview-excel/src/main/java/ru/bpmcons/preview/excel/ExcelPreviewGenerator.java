package ru.bpmcons.preview.excel;

import com.spire.xls.CellRange;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;
import org.springframework.stereotype.Service;
import ru.bpmcons.preview.base.PreviewGenerator;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
public class ExcelPreviewGenerator implements PreviewGenerator {
    @Override
    public boolean isApplicableTo(String fileExt) {
        return fileExt.startsWith("xl");
    }

    @Override
    public ByteArrayOutputStream generate(InputStream inputStream) {
        Workbook workbook = new Workbook();
        workbook.loadFromStream(inputStream);

        Worksheet worksheet = workbook.getWorksheets().get(0);

        CellRange endCell = worksheet.getAllocatedRange().getEndCell();
        int column = endCell.getColumn();
        int row = endCell.getRow();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        worksheet.saveToImage(out, 0, 0, row, column, 3);
        return out;
    }
}
