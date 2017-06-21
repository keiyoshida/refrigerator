package com.example.refrigerator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RefrigeratorDao {

    // JDBCテンプレート宣言
    @Autowired
    private JdbcTemplate jdbc;

    // データベース内の全てのデータを参照する。
    public List<Refrigerator> findAll(){
        return jdbc.query("SELECT * FROM refrigerator", new BeanPropertyRowMapper<>(Refrigerator.class));
    }

    // データベース内のデータをidをもとに参照する。
    public List<Refrigerator> findById(int id){
        return jdbc.query("SELECT * FROM refrigerator WHERE id = ?", new BeanPropertyRowMapper<>(Refrigerator.class), id);
    }

    // データベース内のデータをidをもとに削除する。
    public void deleteById(int id){
        jdbc.update("DELETE FROM refrigerator WHERE id = ?", id);
    }

    // データベースに新しい商品情報を追加する。
    public void insertGoods(String name, String date){
        jdbc.update("INSERT INTO refrigerator (name, limitDay) VALUES (?, to_date(?, 'yyyy/MM/dd'))", name, date);
    }
}
