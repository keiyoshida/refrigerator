package com.example.refrigerator;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RefrigeratorDao {

    @Autowired
    private JdbcTemplate jdbc;

    public List<Map<String, Object>> findAll(){
        return jdbc.queryForList("SELECT * FROM refrigerator");
    }

    public List<Map<String, Object>> findById(int id){
        return jdbc.queryForList("SELECT * FROM refrigerator WHERE id = ?", id);
    }
}
