package com.example.refrigerator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OverLimitDataDao {

    @Autowired
    private JdbcTemplate jdbc;

    public List<OverLimitData> findAll(){
        return jdbc.query("SELECT * FROM overlimitdata", new BeanPropertyRowMapper<>(OverLimitData.class));
    }

    public void insertData(String name, String date){
        jdbc.update("INSERT INTO overlimitdata (name, limitDay) VALUES (?, to_date(?, 'yyyy/MM/dd'))", name, date);
    }

    public List<CountData> countData(String year, String month){
        String keyword = year + "-" + month + "%";
        return jdbc.query("SELECT * FROM overlimitdata WHERE limitDay LIKE ?", new BeanPropertyRowMapper<>(CountData.class), keyword);
    }
}
