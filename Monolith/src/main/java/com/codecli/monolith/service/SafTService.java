package com.codecli.monolith.service;

import com.codecli.monolith.components.SafTGenerator;
import org.springframework.stereotype.Service;

@Service
public class SafTService {
    private final SafTGenerator generator;

    public SafTService(SafTGenerator generator) {
        this.generator = generator;
    }

    public boolean generateSafT() {
        try {
            generator.createSafT();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
