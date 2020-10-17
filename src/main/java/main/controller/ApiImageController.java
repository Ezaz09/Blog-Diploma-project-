package main.controller;

import main.services.ImageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/image")
public class ApiImageController {

    private ImageService imageService;

    public ApiImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(path = "", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAuthority('user:write')")
    public Object uploadImageOnServer(@RequestParam("image") MultipartFile image) {
        return imageService.uploadImageOnServer(image, "images");
    }


}
