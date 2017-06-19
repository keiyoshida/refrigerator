package com.example.refrigerator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RefrigeratorDao {

    @Autowired
    private JdbcTemplate jdbc;

    public List<Refrigerator> findAll(){
        return jdbc.query("SELECT * FROM refrigerator", new BeanPropertyRowMapper<>(Refrigerator.class));
    }

    public List<Refrigerator> findById(int id){
        return jdbc.query("SELECT * FROM refrigerator WHERE id = ?", new BeanPropertyRowMapper<>(Refrigerator.class), id);
    }
    
    public void deleteById(int id){
        jdbc.update("DELETE FROM refrigerator WHERE id = ?", id);
    }
    
    public void insertGoods(String name, String date){
        jdbc.update("INSERT INTO refrigerator (name, limitDay) VALUES (?, to_date(?, 'yyyy/MM/dd'))", name, date);
    }
}
