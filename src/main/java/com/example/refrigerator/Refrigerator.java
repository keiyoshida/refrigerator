package com.example.refrigerator;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Refrigerator {

    private int id;
    private String name;
    private LocalDate limitDay;
}
