package com.example.refrigerator;

import java.time.LocalDate;

import lombok.Data;

@Data
public class GraphTable {

    private String name;            // 商品名
    private LocalDate limitDay;     // 消費期限
    
    public GraphTable(String name, LocalDate limitDay){
        this.name = name;
        this.limitDay = limitDay;
    }
}
