package com.example.demo.service;

import com.example.demo.repository.InterfaceTableRepository;
import com.example.demo.service.strategy.ExcelProcessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImportExportService {
    private final ExcelProcessorFactory excelProcessorFactory;

    @Autowired
    public ImportExportService(ExcelProcessorFactory factory, InterfaceTableRepository repository) {
        this.excelProcessorFactory = factory;
    }

    public void importData(MultipartFile file,String objectType, String fileType) throws Exception {
        switch(fileType){
            case "excel":{
                excelProcessorFactory.importExcelFile(file, objectType);
            }
        }

    }

}
