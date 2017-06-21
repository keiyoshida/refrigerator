package com.example.refrigerator;

import java.time.LocalDate;

import lombok.Data;

@Data
public class OverLimitData {

    private int id;                 // 商品id
    private String name;            // 商品名
    private LocalDate limitDay;     // 消費期限
}
