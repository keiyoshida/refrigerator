package com.example.refrigerator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Autowired
    private JdbcTemplate jdbc;

    // @Value("${app.name}")
    // private String appName;

    @GetMapping("/h2")
    public String h2() {

        // System.out.println(jdbc.queryForList("SELECT * FROM person"));

        // select
        // List<Map<String, Object>> list = jdbc.queryForList("SELECT * FROM
        // person");
        // System.out.println((list.get(1)).get("name"));

        // insert update delete
        // jdbc.update("INSERT INTO person VALUES (34, 'tanaka', 54)");
        return "test";
    }

    @GetMapping("/test")
    public String test(Model model) {

        printList(model);
        return "test";
    }

    @GetMapping("/input")
    public String input(Model model) {
        return "input";
    }

    @GetMapping("/in")
    public String moveInput() {
        return "input";
    }

    @GetMapping("/out")
    public String moveOut(int[] selectGoods, Model model) {
        for (int i = 0; i < selectGoods.length; i++) {
            jdbc.update("DELETE FROM refrigerator WHERE id = " + selectGoods[i] );
        }

        printList(model);
        return "test";
    }

    @GetMapping("/type")
    public String selectType(String type, Model model) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(calendar.getTime());

        if (type.equals("other")) {
            model.addAttribute("limit", "");
            model.addAttribute("select6", true);
        } else {
            String types = "";
            int addDay = 0;
            if (type.equals("meet")) {
                addDay = 3;
                types = "select1";
            } else if (type.equals("fish")) {
                addDay = 2;
                types = "select2";
            } else if (type.equals("vegetable")) {
                addDay = 7;
                types = "select3";
            } else if (type.equals("egg")) {
                addDay = 14;
                types = "select4";
            } else if (type.equals("milk")) {
                addDay = 14;
                types = "select5";
            }
            calendar.add(Calendar.DATE, addDay);
            model.addAttribute(types, true);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            model.addAttribute("limit", sdf.format(calendar.getTime()));
        }
        return "input";
    }

    @GetMapping("/addList")
    public String addList(String name, String date, Model model) {

        if (name.equals("") == false && date != null) {
            jdbc.update("INSERT INTO refrigerator (name, limitDay) VALUES ('" + name + "', to_date('" + date
                    + "', 'yyyy/MM/dd'))");
            printList(model);
            return "test";
        } else {
            model.addAttribute("goodsName", name);
            model.addAttribute("limit", date);
            return "input";
        }
    }

    public void printList(Model model) {
        List<Goods> goods = new ArrayList<Goods>();
        List<Map<String, Object>> list = jdbc.queryForList("SELECT id, name, limitDay FROM refrigerator");
        for (int i = 0; i < list.size(); i++) {
            int id = Integer.parseInt(((list.get(i)).get("id").toString()));
            String name = (list.get(i)).get("name").toString();
            String limit = ((list.get(i).get("limitDay")).toString());
            Date limitDay = java.sql.Date.valueOf(limit);
            goods.add(new Goods(id, name, limit, limitDay));
        }

        printImage(goods, model);
        model.addAttribute("goodslist", goods);
    }

    public void printImage(List<Goods> goods, Model model) {
        int count = 0;
        for (int i = 0; i < goods.size(); i++) {
            if (goods.get(i).getLimitDay().compareTo(goods.get(i).getToday()) < 0) {
                count++;
            }
        }

        if (count > 0) {
            model.addAttribute("path", "bad.png");
        } else {
            model.addAttribute("path", "normal.png");
        }
    }
}
