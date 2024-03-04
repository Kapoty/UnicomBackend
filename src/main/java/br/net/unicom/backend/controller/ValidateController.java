package br.net.unicom.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.net.unicom.backend.payload.request.TestRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;


@RestController
@Validated
@RequestMapping(
    value = "/",
    produces = MediaType.APPLICATION_JSON_VALUE
    )

public class ValidateController {
    
    Logger logger = LoggerFactory.getLogger(ValidateController.class);

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    TestRequest test(@Valid @RequestBody TestRequest testRequest) {
        return testRequest;
    }

    @GetMapping("/id/{id}/{id2}")
    public int pathTest(@PathVariable("id") @Min(5) int id, @PathVariable("id2") @Min(5) int id2) {
        return id;
    }

    @GetMapping("/id/")
    public int paramTest(@RequestParam("id") @Min(5) int id) {
        return id;
    }

    @GetMapping("/powerbi/{n}") 
    public int powerbiTest(@PathVariable("n") int n) {
        logger.info(String.valueOf(n));
        return n;
    }

}