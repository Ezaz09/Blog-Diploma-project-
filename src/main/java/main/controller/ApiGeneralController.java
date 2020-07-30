package main.controller;

import main.api.responses.ResponseInit;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ApiGeneralController {

    @GetMapping(path = "/api/init")
    public ResponseEntity<ResponseInit> init()
    {
        ResponseInit responseInit = ResponseInit.builder()
                .title("DevPub")
                .subtitle("Рассказы разработчиков")
                .phone("8 800 555 35 35")
                .email("DevStories@gmail.com")
                .copyright("Донцов Владимир")
                .copyrightFrom("2020").build();
        return new ResponseEntity<>(responseInit, HttpStatus.OK);
    }

}
