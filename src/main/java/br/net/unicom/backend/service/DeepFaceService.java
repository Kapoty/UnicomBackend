package br.net.unicom.backend.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class DeepFaceService {

    @Autowired
    WebClient deepFaceClient;

    public String find(String img_path, String db_path) {
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("img_path", img_path);
        bodyMap.put("db_path", db_path);
        bodyMap.put("distance_metric", "euclidean");
        return deepFaceClient
                .post()
                .uri("/find")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bodyMap))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block(Duration.ofSeconds(5));
    }

}
