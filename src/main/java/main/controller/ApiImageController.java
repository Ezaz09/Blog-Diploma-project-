package main.controller;

import main.api.responses.ErrorResponse;
import main.api.responses.ImageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@RestController
@RequestMapping("/api/image")
public class ApiImageController {

    private String imagesPath = System.getProperty("user.dir") + "\\src\\main\\java\\main\\upload\\images";

    @PostMapping(path = "")
    @PreAuthorize("hasAuthority('user:write')")
    public Object uploadImageOnServer(@RequestParam("image") MultipartFile image)
    {
        if (!image.getOriginalFilename().isEmpty() && image != null) {
            File uploadCatalog = new File(imagesPath);
            if(!uploadCatalog.exists())
            {
                uploadCatalog.mkdirs();
            }

            String uploadPath = imagesPath;
            String pathForResponse = "/upload/images";
            String newUploadPath;
            for( int i = 0; i < 3; i++)
            {
                String alphaNumericString = getAlphaNumericString(2);
                newUploadPath = uploadPath + "\\" + alphaNumericString;
                pathForResponse = pathForResponse + "/" + alphaNumericString;
                File uploadDir = new File(newUploadPath);

                if(!uploadDir.exists())
                {
                    uploadDir.mkdir();
                }

                uploadPath = newUploadPath;
            }

            String resultFilename = image.getOriginalFilename();

            try
            {
                image.transferTo(new File(uploadPath + "\\" + resultFilename));
                return  pathForResponse + "/" + resultFilename;
            }
            catch (IOException exception){
                ImageResponse imageResponse = new ImageResponse();
                imageResponse.setResult(false);

                HashMap<String, String> errors = new HashMap<>(1);
                errors.put("image", "Размер файла превышает допустимый размер");

                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.setErrors(errors);

                imageResponse.setErrors(errorResponse);
                return new ResponseEntity<>(imageResponse, HttpStatus.BAD_REQUEST);
            }

        } else {
            ImageResponse imageResponse = new ImageResponse();
            imageResponse.setResult(false);

            HashMap<String, String> errors = new HashMap<>(1);
            errors.put("image", "Файл загружен с ошибкой");

            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrors(errors);

            imageResponse.setErrors(errorResponse);
            return new ResponseEntity<>(imageResponse, HttpStatus.BAD_REQUEST);
        }
    }


    protected String getAlphaNumericString(int n)

    {
        // выбрал символ случайный из этой строки

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

                + "0123456789"

                + "abcdefghijklmnopqrstuvxyz";

        // создаем StringBuffer размером AlphaNumericString

        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++)
        {
            // генерируем случайное число между

            // 0 переменной длины AlphaNumericString

            int index

                    = (int)(AlphaNumericString.length()

                    * Math.random());

            // добавляем символ один за другим в конец sb

            sb.append(AlphaNumericString

                    .charAt(index));

        }

        return sb.toString();

    }
}
