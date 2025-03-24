package com.example.demo.service;

import com.example.demo.model.ExcelData;
import com.example.demo.repository.ExcelDataRepository;
import org.apache.logging.slf4j.SLF4JLogger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class ApachePOI {
    @Autowired
    ExcelDataRepository excelDataRepository;

    //Với cấu hình hiện tại, bạn có thể xử lý thoải mái các file Excel có dung lượng từ 10-20 MB nếu không thực hiện
    // tinh chỉnh bộ nhớ thêm. Đối với các file lớn hơn, bạn nên điều chỉnh kích thước heap của JVM hoặc tối ưu hóa
    // mã nguồn bằng các kỹ thuật xử lý file theo dòng.
    public void importExcelFile(MultipartFile file) throws Exception {
        List<ExcelData> dataList = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên

            // Đọc hàng tiêu đề (header row) để ánh xạ tên cột với chỉ số
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnMap = new HashMap<>();
            Iterator<Cell> headerIterator = headerRow.cellIterator();
            while (headerIterator.hasNext()) {
                Cell cell = headerIterator.next();
                columnMap.put(cell.getStringCellValue(), cell.getColumnIndex());
            }

            // Duyệt qua các dòng dữ liệu, bắt đầu từ dòng thứ 2 (bỏ qua tiêu đề)
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    // Sử dụng tên cột từ ánh xạ để truy xuất dữ liệu
                    String name = row.getCell(columnMap.get("Name")).getStringCellValue(); // Cột "name"
                    int age = (int) row.getCell(columnMap.get("Age")).getNumericCellValue(); // Cột "age"

                    ExcelData data = new ExcelData();
                    data.setName(name);
                    data.setAge(age);
                    dataList.add(data);
                }
            }
        }

        // Lưu danh sách dữ liệu vào cơ sở dữ liệu
        excelDataRepository.saveAll(dataList);
    }


    public void writeToExcel() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("ExcelData");

        // Header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Age");

        // Data rows
        List<ExcelData> dataList = excelDataRepository.findAll();
        int rowIndex = 1; // Start from the second row (index 1)
        for (ExcelData data : dataList) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(data.getId()); // ID column
            row.createCell(1).setCellValue(data.getName()); // Name column
            row.createCell(2).setCellValue(data.getAge()); // Age column
        }

        // Write to file
        try (FileOutputStream fileOut = new FileOutputStream("D://Data//Clother//output.xlsx")) {
            workbook.write(fileOut);
        }
        workbook.close();
    }


}
