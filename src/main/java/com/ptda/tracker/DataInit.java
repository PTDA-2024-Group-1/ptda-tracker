package com.ptda.tracker;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInit {

    @PostConstruct
    public void init() {
        System.out.println("Initializing data...");
    }
}