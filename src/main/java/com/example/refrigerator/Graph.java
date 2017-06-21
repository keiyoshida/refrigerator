package com.example.refrigerator;

import lombok.Data;

@Data
public class Graph {

    private String label;   // 年月 (yyyy/MM) graphのラベル
    private int count;      // 指定月の期限切れ商品数
    
    public Graph(String label, int count){
        this.label = label;
        this.count = count;
    }
}
