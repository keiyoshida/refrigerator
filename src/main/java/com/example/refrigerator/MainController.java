package com.example.refrigerator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Autowired
    private JdbcTemplate jdbc;

//    @Value("${app.name}")
//    private String appName;

    public List<Goods> goods = new ArrayList<Goods>();

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

        try {
            goods.add(new Goods(1, "肉", "2017/6/7", new SimpleDateFormat("yyyy/MM/dd").parse("2017/6/7")));
            goods.add(new Goods(2, "魚", "2017/5/20", new SimpleDateFormat("yyyy/MM/dd").parse("2017/5/20")));
            goods.add(new Goods(3, "野菜", "2017/6/8", new SimpleDateFormat("yyyy/MM/dd").parse("2017/6/8")));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        printImage(model);
        model.addAttribute("goodslist", goods);
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
            System.out.println(selectGoods[i]);
        }
        printImage(model);
        model.addAttribute("goodslist", goods);
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
            if (type.equals("meet")) {
                calendar.add(Calendar.DATE, 3);
                model.addAttribute("select1", true);
            } else if (type.equals("fish")) {
                calendar.add(Calendar.DATE, 2);
                model.addAttribute("select2", true);
            } else if (type.equals("vegetable")) {
                calendar.add(Calendar.DATE, 7);
                model.addAttribute("select3", true);
            } else if (type.equals("egg")) {
                calendar.add(Calendar.DATE, 14);
                model.addAttribute("select4", true);
            } else if (type.equals("milk")) {
                calendar.add(Calendar.DATE, 14);
                model.addAttribute("select5", true);
            }
            model.addAttribute("limit", new SimpleDateFormat("yyyy/MM/dd").format(calendar.getTime()));
        }
        return "input";
    }

    @GetMapping("/addList")
    public String addList(String name, String date, Model model) {

        if (name.equals("") == false && date != null) {
            jdbc.update("INSERT INTO refrigerator (name, limitDay) VALUES ('" + name + "', to_date('" + date
                    + "', 'yyyy/MM/dd'))");

            return "test";
        } else {
            model.addAttribute("goodsName", name);
            model.addAttribute("limit", date);
            return "input";
        }
    }

    public void printImage(Model model) {
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
