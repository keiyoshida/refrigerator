package com.example.refrigerator;

import java.time.LocalDate;

public class Goods {

    private final int id;               // 商品id
    private final String name;          // 商品名
    private final LocalDate limitDay;   // 消費期限
    private final int state;            // 商品の状態 1:期限前日 2:期限当日 3:期限切れ 0:その他
    
    public Goods(int id, String name, LocalDate limitDay, int state){
        
        this.id = id;
        this.name = name;
        this.limitDay = limitDay;
        this.state = state;
    }
    
    public int getId(){
        return id;
    }
    
    public String getName(){
        return name;
    }
    
    public LocalDate getLimitDay(){
        return limitDay;
    }
    public int getState(){
        return state;
    }
}
