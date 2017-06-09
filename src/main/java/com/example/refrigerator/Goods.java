package com.example.refrigerator;

import java.util.Calendar;
import java.util.Date;

public class Goods {

    private final int id;
    private final String name;
    private final String limit;
    private final Date today;
    private final Date limitDay;
    
    public Goods(int id, String name, String limit, Date limitDay){
        
        Calendar calendar = Calendar.getInstance();
        this.id = id;
        this.name = name;
        this.limit = limit;
        this.limitDay = limitDay;
        this.today = (calendar.getTime());
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
    
    public Date getToday(){
        return today;
    }
    
    public Date getLimitDay(){
        return limitDay;
    }
}
