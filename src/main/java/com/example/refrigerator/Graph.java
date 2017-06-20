package com.example.refrigerator;

import lombok.Data;

@Data
public class Graph {

    private String label;
    private int count;
    
    public Graph(String label, int count){
        this.label = label;
        this.count = count;
    }
}
