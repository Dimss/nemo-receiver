package com.nemo.receiver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nemo.receiver.payload.UploadFileResponse;
import com.nemo.receiver.service.FileStorageService;
import com.nemo.receiver.service.KafkaSender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private KafkaSender kafkaSender;

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/upload")
    public UploadFileResponse upload(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);
        UploadFileResponse ufr = new UploadFileResponse(fileName, file.getContentType(), file.getSize());
        try {
            String ufJson = objectMapper.writeValueAsString(ufr);
            kafkaSender.send("md-file-topic", ufJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ufr;
    }
}