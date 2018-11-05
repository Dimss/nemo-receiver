package com.nemo.receiver.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.nemo.receiver.payload.UploadFileResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class LinksClient {

    @Autowired
    private AuthValidator authValidator;

    @Value("${app.mesh.service.links}")
    private String linksAddr;

    @Autowired
    private UploadFileResponse uploadFileResponse;


    private HttpHeaders getAuthHeader() {
        DecodedJWT userIdentity = authValidator.getUserIdentity();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-NEMO-AUTH", userIdentity.getSubject());
        return httpHeaders;
    }

    public void saveImageMetadata() {
        String url = linksAddr + "/v1/links";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-NEMO-AUTH", authValidator.getUserIdentity().getToken());
        JSONObject jo = new JSONObject();
        jo.append("link", uploadFileResponse.getFileName());
        jo.append("title", uploadFileResponse.getFileTitle());
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post(url)
                    .headers(headers)
                    .body(jo)
                    .asJson();
            if (jsonResponse.getStatus() != 200) {
                throw new RuntimeException("Error during connecting to links service");
            }
        } catch (UnirestException ex) {
            throw new RuntimeException(ex);
        }
    }

}
