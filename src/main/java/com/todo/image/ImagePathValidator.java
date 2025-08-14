package com.todo.image;

import org.springframework.stereotype.Component;
import java.nio.file.Paths;

@Component
public class ImagePathValidator {
    private final String locationPattern = "classpath:img/";

    public boolean isValidPath(String fileName) {
        return !fileName.contains("..") && !fileName.contains("/") && !fileName.contains("\\");
    }
}