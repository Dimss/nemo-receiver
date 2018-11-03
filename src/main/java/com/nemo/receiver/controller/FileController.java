package com.nemo.receiver.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nemo.receiver.payload.UploadFileResponse;
import com.nemo.receiver.service.AuthValidator;
import com.nemo.receiver.service.FileStorageService;

import com.nemo.receiver.service.LinksClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartFile;

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
    public UploadFileResponse upload(@RequestHeader("X-NEMO-AUTH") String authToekn, @RequestParam("file") MultipartFile file) {
        try {
            authValidator.validateToken(authToekn);
        } catch (JWTVerificationException ex) {
            // TODO: handle auth error
            throw ex;
        }

        String fileName = fileStorageService.storeFile(file);
        uploadFileResponse.setFileName(fileName);
        uploadFileResponse.setFileType(file.getContentType());
        uploadFileResponse.setSize(file.getSize());
        linksClient.saveImageMetadata();
        return uploadFileResponse;

    }
}