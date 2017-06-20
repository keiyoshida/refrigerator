package com.example.refrigerator;

import java.time.LocalDate;

import lombok.Data;

@Data
public class GraphTable {

    private String name;
    private LocalDate limitDay;
    
    public GraphTable(String name, LocalDate limitDay){
        this.name = name;
        this.limitDay = limitDay;
    }
}
