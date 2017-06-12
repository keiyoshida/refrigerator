package com.example.refrigerator;

import java.time.LocalDate;

public class Goods {

    private final int id;
    private final String name;
    private final String limit;
    private final LocalDate today;
    private final LocalDate limitDay;
    private final int state;
    
    public Goods(int id, String name, String limit, LocalDate limitDay, int state){
        
        this.id = id;
        this.name = name;
        this.limit = limit;
        this.limitDay = limitDay;
        this.state = state;
        this.today = LocalDate.now();
    }
    
    public int getId(){
        return id;
    }
    
    public String getName(){
        return name;
    }
    
    public String getLimit(){
        return limit;
    }
    
    public LocalDate getToday(){
        return today;
    }
    
    public LocalDate getLimitDay(){
        return limitDay;
    }
    public int getState(){
        return state;
    }
}
