package com.nemo.receiver.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nemo.receiver.payload.UploadFileResponse;
import com.nemo.receiver.service.AuthValidator;
import com.nemo.receiver.service.FileStorageService;
import com.nemo.receiver.service.LinksClient;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
public class FileController {
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private AuthValidator authValidator;
    @Autowired
    private UploadFileResponse uploadFileResponse;
    @Autowired
    private LinksClient linksClient;

    @PostMapping("/upload")
    public UploadFileResponse upload(@RequestHeader("X-NEMO-AUTH") String authToken,
                                     @RequestParam("file") MultipartFile file,
                                     @RequestParam("fileTitle") String fileTitle) {
        try {
            authValidator.validateToken(authToken);
        } catch (JWTVerificationException ex) {
            // TODO: handle auth error
            throw ex;
        }

        String fileName = fileStorageService.storeFile(file);
        uploadFileResponse.setFileName(fileName);
        uploadFileResponse.setFileType(file.getContentType());
        uploadFileResponse.setSize(file.getSize());
        uploadFileResponse.setFileTitle(fileTitle);
        linksClient.saveImageMetadata();
        return uploadFileResponse;
    }

    @GetMapping("/image/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable("fileName") String fileName) throws IOException {
        FileInputStream fs = new FileInputStream("public/photos/" + fileName);
        byte[] bytes = StreamUtils.copyToByteArray(fs);
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
    }

}