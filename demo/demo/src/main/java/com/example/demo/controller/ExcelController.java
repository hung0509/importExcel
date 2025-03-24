package com.example.demo.controller;

import com.example.demo.model.ExcelData;
import com.example.demo.service.ApachePOI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ExcelController {

    @Autowired
    private ApachePOI excelService;

    @PostMapping("/upload")
    public void uploadExcel(@RequestParam("file") MultipartFile file) throws Exception {
        excelService.importExcelFile(file);
    }

    @PostMapping("/write")
    public String writeExcel() throws IOException {

        // Dữ liệu mẫu

        // Ghi dữ liệu vào tệp Excel
         excelService.writeToExcel();

        return "File written to: ";
    }
}