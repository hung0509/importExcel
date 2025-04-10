package com.example.demo.service.strategy;

import com.example.demo.factory.Example1;
import com.example.demo.repository.Example1Repository;
import com.example.demo.repository.InterfaceTableRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExcelProcessorFactory {
    InterfaceTableRepository interfaceTableRepository;
    Example1Repository example1Repository;
    private static final Logger log = LoggerFactory.getLogger(ExcelProcessorFactory.class);

    @Autowired
    public ExcelProcessorFactory(InterfaceTableRepository interfaceTableRepository, Example1Repository example1Repository) {
        this.interfaceTableRepository = interfaceTableRepository;
        this.example1Repository = example1Repository;
    }


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
                if (col > 0) {
                    columnName.append(", ");
                }

                String column = cell.getStringCellValue().toLowerCase().replaceAll("\\s+", "_");
                if(column.contains("id") && !column.startsWith("d_") && !column.equalsIgnoreCase("ID")){
                    columnName.append("d_").append(column);
                }else{
                    columnName.append(column);
                }
                col++;
            }

            log.info("Columns: {" + columnName.toString() + "}");
            log.info("col: {" + col + "}");

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
                        if (cell != null) {
                            if (cell.getCellType() == CellType.NUMERIC) {
                                value = String.valueOf(cell.getNumericCellValue()); // Chuyển giá trị số thành chuỗi
                            } else {
                                value = "'" + cell.getStringCellValue() + "'"; // Lấy giá trị chuỗi
                            }

                            if (i == col - 1) {
                                values.append("," +  value + "),");
                            } else if( i == 0) {
                                values.append("(" + value);
                            }else {
                                values.append("," + value );
                            }
                        }
                    }
                }
            }
            values.deleteCharAt(values.length() - 1);
            log.info("Values: {" + values.toString() + "}");

            // Lưu danh sách dữ liệu vào cơ sở dữ liệu
            interfaceTableRepository.saveRow(columnName.toString(), values.toString(), objectType);
        }
    }

    public String convertCamelToCustomFormat(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1 $2") // Thêm hai khoảng trắng giữa các từ
                .substring(0, 1).toUpperCase()         // Viết hoa ký tự đầu
                + camelCase.replaceAll("([a-z])([A-Z])", "$1 $2").substring(1); // Giữ phần còn lại của chuỗi
    }

    private String[] extractColumns(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        // Tạo mảng String để lưu tên các trường
        String[] fieldNames = new String[fields.length];

        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = convertCamelToCustomFormat(fields[i].getName()); // Lấy tên của từng trường
        }

        return fieldNames;
    }

    public void writeToExcel() throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("ExcelData");

        // Header row
        CellStyle lockedCellStyle = workbook.createCellStyle();
        lockedCellStyle.setLocked(true);

        Row headerRow = sheet.createRow(0);
        String[] header = extractColumns(Example1.class);

        for(int i = 0; i < header.length; i++){
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(header[i]); // Set header text
            cell.setCellStyle(lockedCellStyle);
        }

        // Data rows
        List<Example1> dataList = example1Repository.findAll();


        int rowIndex = 1; // Start from the second row (index 1)
        for (Example1 data : dataList) {
            log.info("data: " + data.toString());
            Row row = sheet.createRow(rowIndex++);
            for (int i = 0; i < header.length; i++) {
                // Use reflection to get field values dynamically
                Field[] fields = data.getClass().getDeclaredFields();

                Field field = fields[i];
                field.setAccessible(true);
                try {
                    Object value = field.get(data);
                    log.info("value: " + value);
                    row.createCell(i).setCellValue(value != null ? value.toString() : ""); // Handle null values
                } catch ( Exception e) {
                    // Log or handle any errors (e.g., missing getter)
                    row.createCell(i).setCellValue(""); // Default to empty cell
                    e.printStackTrace();
                }

            }
        }

        // Write to file
        try (FileOutputStream fileOut = new FileOutputStream("D://Data//Clother//output.xlsx")) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

}
