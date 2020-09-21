package main.controller;

import main.api.responses.ErrorResponse;
import main.api.responses.ImageResponse;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${blog.upload.path}")
    private String uploadPath;

    @PostMapping(path = "")
    @PreAuthorize("hasAuthority('user:write')")
    public Object uploadImageOnServer(@RequestParam("image") MultipartFile image)
    {
        ImageResponse imageResponse = checkUploadingPhoto(image);

        if(imageResponse != null)
        {
            return new ResponseEntity<>(imageResponse, HttpStatus.BAD_REQUEST);
        }

        HashMap<String, String> paths = createPathForPhoto();
        String resultFilename = image.getOriginalFilename();

            try
            {
                if(image.getSize() < 5_242_880)
                {
                    image.transferTo(new File(System.getProperty("user.dir") + File.separator +
                            paths.get("uploadPath") + File.separator + resultFilename));
                    return paths.get("pathForResponse") + "/" + resultFilename;
                }
                else
                {
                    imageResponse = new ImageResponse();
                    imageResponse.setResult(false);

                    HashMap<String, String> errors = new HashMap<>(1);
                    errors.put("image", "Размер загружаемого файла больше 5 мб!");

                    ErrorResponse errorResponse = new ErrorResponse();
                    errorResponse.setErrors(errors);

                    imageResponse.setErrors(errorResponse);
                    return new ResponseEntity<>(imageResponse, HttpStatus.BAD_REQUEST);
                }
            }
            catch (IOException exception)
            {
                imageResponse = new ImageResponse();
                imageResponse.setResult(false);

                HashMap<String, String> errors = new HashMap<>(1);
                errors.put("image", "Произошла ошибка при загрузке фото на сервер");

                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.setErrors(errors);

                imageResponse.setErrors(errorResponse);
                return new ResponseEntity<>(imageResponse, HttpStatus.BAD_REQUEST);
            }
    }

    protected ImageResponse checkUploadingPhoto(MultipartFile image)
    {
        if (image == null ||
                image.getOriginalFilename() == null ||
                image.getOriginalFilename().isEmpty())
        {
            ImageResponse imageResponse = new ImageResponse();
            imageResponse.setResult(false);

            HashMap<String, String> errors = new HashMap<>(1);
            errors.put("image", "Файл загружен с ошибкой");

            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrors(errors);

            imageResponse.setErrors(errorResponse);
            return imageResponse;
        }
        else
        {
            return null;
        }
    }

    protected HashMap<String, String> createPathForPhoto()
    {
        HashMap<String, String> paths = new HashMap<>();
        String imagesPath = uploadPath + File.separator + "images";
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
            newUploadPath = uploadPath + File.separator + alphaNumericString;
            pathForResponse = pathForResponse + "/" + alphaNumericString;
            File uploadDir = new File(newUploadPath);

            if(!uploadDir.exists())
            {
                uploadDir.mkdir();
            }

            uploadPath = newUploadPath;
        }
        paths.put("uploadPath", uploadPath);
        paths.put("pathForResponse", pathForResponse);
        return paths;
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
