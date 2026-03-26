package com.urbanissue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class WebMain {

    public static void main(String[] args) {
        SpringApplication.run(WebMain.class, args);
    }

    @GetMapping("/")
    public String home() {
        return "<html><head><title>CivicTrack</title></head>" +
               "<body><h1>CivicTrack - Urban Issue Reporting</h1>" +
               "<p>Web version is running!</p>" +
               "<p><a href='/login'>Go to Login</a></p>" +
               "</body></html>";
    }

    @GetMapping("/login")
    public String login() {
        return "<html><head><title>CivicTrack Login</title></head>" +
               "<body><h1>CivicTrack Login</h1>" +
               "<p>This is a simple web interface for CivicTrack.</p>" +
               "<p>For full functionality, use the JavaFX desktop version.</p>" +
               "</body></html>";
    }
}