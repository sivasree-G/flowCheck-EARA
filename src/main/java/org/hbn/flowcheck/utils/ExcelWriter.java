package org.hbn.flowcheck.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ExcelWriter {

    public static void writeTestResult(String filePath, String sheetName, int rowIndex, String result) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);

            // Add a "Result" column if it doesn't exist
            Row headerRow = sheet.getRow(0);
            int resultColumnIndex = headerRow.getLastCellNum(); // append at the end

            boolean resultColumnExists = false;
            for (Cell cell : headerRow) {
                if (cell.getStringCellValue().trim().equalsIgnoreCase("result")) {
                    resultColumnIndex = cell.getColumnIndex();
                    resultColumnExists = true;
                    break;
                }
            }

            if (!resultColumnExists) {
                Cell cell = headerRow.createCell(resultColumnIndex);
                cell.setCellValue("Result");
            }

            // Write result to the correct row
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                Cell resultCell = row.createCell(resultColumnIndex);
                resultCell.setCellValue(result);
            }

            // Save changes
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
