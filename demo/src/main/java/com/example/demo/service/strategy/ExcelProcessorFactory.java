package com.example.demo.service.strategy;

import com.example.demo.repository.InterfaceTableRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExcelProcessorFactory {
    InterfaceTableRepository interfaceTableRepository;

    public void importExcelFile(MultipartFile file,String objectType ) throws Exception {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên

            // Đọc hàng tiêu đề (header row) để ánh xạ tên cột với chỉ số
            Row headerRow = sheet.getRow(0);
            Iterator<Cell> headerIterator = headerRow.cellIterator();

            StringBuilder columnName = new StringBuilder();
            int col = 0;
            while (headerIterator.hasNext()) {
                Cell cell = headerIterator.next();
                //create column
                String column = cell.getStringCellValue().replaceAll("\\s+", "_");
                if(column.contains("id") && !column.startsWith("d_") && !column.equalsIgnoreCase("ID")){
                    columnName.append("d_").append(column);
                }else{
                    columnName.append(column);
                }

                if (col > 0) {
                    columnName.append(", ");
                }
                columnName.append(column);
                col++;
            }

            // Duyệt qua các dòng dữ liệu, bắt đầu từ dòng thứ 2 (bỏ qua tiêu đề)
            StringBuilder values = new StringBuilder();
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    // Sử dụng tên cột từ ánh xạ để truy xuất dữ liệu
                    for (int i = 0; i < col; i++) {
                        Cell cell = row.getCell(i);
                        String value;
                        // Kiểm tra kiểu dữ liệu của ô
                        if (cell.getCellType() == CellType.NUMERIC) {
                            value = String.valueOf(cell.getNumericCellValue()); // Chuyển giá trị số thành chuỗi
                        } else {
                            value = "'" +  cell.getStringCellValue() + "'"; // Lấy giá trị chuỗi
                        }

                        if(i == col - 1) {
                            values.append("(" + value + ")");
                        }else{
                            values.append("(" + value + "),");
                        }
                    }
                }
            }

            // Lưu danh sách dữ liệu vào cơ sở dữ liệu
            interfaceTableRepository.saveRow(columnName.toString(), values.toString(), objectType);
        }
    }


//    public void writeToExcel() throws IOException {
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("ExcelData");
//
//        // Header row
//        Row headerRow = sheet.createRow(0);
//        headerRow.createCell(0).setCellValue("ID");
//        headerRow.createCell(1).setCellValue("Name");
//        headerRow.createCell(2).setCellValue("Age");
//
//        // Data rows
//        List<ExcelData> dataList = excelDataRepository.findAll();
//        int rowIndex = 1; // Start from the second row (index 1)
//        for (ExcelData data : dataList) {
//            Row row = sheet.createRow(rowIndex++);
//            row.createCell(0).setCellValue(data.getId()); // ID column
//            row.createCell(1).setCellValue(data.getName()); // Name column
//            row.createCell(2).setCellValue(data.getAge()); // Age column
//        }
//
//        // Write to file
//        try (FileOutputStream fileOut = new FileOutputStream("D://Data//Clother//output.xlsx")) {
//            workbook.write(fileOut);
//        }
//        workbook.close();
//    }

}
