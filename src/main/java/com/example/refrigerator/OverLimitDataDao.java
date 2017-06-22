package com.example.refrigerator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OverLimitDataDao {

    // JDBCテンプレート宣言
    @Autowired
    private JdbcTemplate jdbc;

    // データベース内の全てのデータを参照する。
    public List<OverLimitData> findAll(){
        return jdbc.query("SELECT * FROM overlimitdata ORDER BY limitDay ASC", new BeanPropertyRowMapper<>(OverLimitData.class));
    }

    // データベースに新しい商品情報を追加する。
    public void insertData(String name, String date){
        jdbc.update("INSERT INTO overlimitdata (name, limitDay) VALUES (?, to_date(?, 'yyyy/MM/dd'))", name, date);
    }

    // データベース内のデータを年と月をもとに参照する。
    public List<OverLimitData> findByDate(String year, String month){
        String keyword = year + "-" + month + "%";
        return jdbc.query("SELECT * FROM overlimitdata WHERE limitDay LIKE ? ORDER BY limitDay ASC", new BeanPropertyRowMapper<>(OverLimitData.class), keyword);
    }
}
