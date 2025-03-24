package com.example.demo.controller;

import com.example.demo.service.ImportExportService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ExcelController {
    ImportExportService importExportService;

    @PostMapping("/upload")
    public void uploadExcel(@RequestParam("file") MultipartFile file,
                            @RequestParam("typeFile") String typeFile,
                            @RequestParam("objectType") String objectType) throws Exception {
        importExportService.importData(file, objectType, typeFile);
    }
}
