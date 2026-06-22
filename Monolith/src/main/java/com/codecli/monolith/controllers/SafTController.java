package com.codecli.monolith.controllers;

import com.codecli.monolith.service.SafTService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("saft")
public class SafTController {

    private final SafTService safTService;

    public SafTController(SafTService safTService) {
        this.safTService = safTService;
    }

    @GetMapping
    public ResponseEntity<String> generateSafT(@RequestParam boolean shouldGenerateDocument) {
        boolean isSuccessful = safTService.generateSafT(shouldGenerateDocument);
        if (isSuccessful) {
            return ResponseEntity.ok("Saf-t Generated with success");
        } else {
            return ResponseEntity.internalServerError().body("Saf-t Generation failed");
        }
    }
}
