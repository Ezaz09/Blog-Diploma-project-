package main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ApiGeneralController {

    @GetMapping(path = "/api/init/")
    public Map<String, String> init()
    {
        HashMap<String, String> init = new HashMap<>();
        init.put("title", "DevPub");
        init.put("subtitle", "Рассказы разработчиков");
        init.put("phone", "8 800 555 35 35");
        init.put("email", "DevStories@gmail.com");
        init.put("copyright", "Донцов Владимир");
        init.put("copyrightFrom", "2020");
        return init;
    }

}
