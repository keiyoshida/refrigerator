package com.example.refrigerator;

import java.time.LocalDate;
import java.util.ArrayList;
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
        return "top";
    }

    @GetMapping("/top")
    public String top(Model model) {

        String qry = "SELECT id, name, limitDay FROM refrigerator";
        printList(qry, model);
        return "top";
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

        String qry = "SELECT id, name, limitDay FROM refrigerator";
        for (int i = 0; i < selectGoods.length; i++) {
            jdbc.update("DELETE FROM refrigerator WHERE id = " + selectGoods[i]);
        }

        printList(qry, model);
        return "top";
    }

    @GetMapping("/search")
    public String searchURL(Model model) {
        String url = "https://cookpad.com/search/";
        String qry = "SELECT id, name, limitDay FROM refrigerator";
        printList(qry, model);
        model.addAttribute("url", url);
        return "search";
    }

    @GetMapping("/selectSearch")
    public String selectSearch(int[] selectGoods, Model model) {
        String url = "https://cookpad.com/search/";
        String qry = "SELECT id, name, limitDay FROM refrigerator";
        String name = "";
        String urlText = "";
        printList(qry, model);
        if (selectGoods != null) {
            name = (jdbc.queryForList("SELECT name FROM refrigerator WHERE id = " + selectGoods[0])).get(0).get("name")
                    .toString();
            url = url + name;
            urlText = name;
            for (int i = 1; i < selectGoods.length; i++) {
                name = (jdbc.queryForList("SELECT name FROM refrigerator WHERE id = " + selectGoods[i])).get(0)
                        .get("name").toString();
                url = url + "%20" + name;
                urlText = urlText + "," + name;
            }
            model.addAttribute("urlText", urlText + "を使ったレシピ");
        }
        model.addAttribute("url", url);
        return "search";
    }

    @GetMapping("/type")
    public String selectType(String type, Model model) {

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
            LocalDate date = LocalDate.now().plusDays(addDay);
            model.addAttribute(types, true);
            model.addAttribute("limit", date);
        }
        return "input";
    }

    @GetMapping("/addList")
    public String addList(String name, String date, Model model) {

        String qry = "SELECT id, name, limitDay FROM refrigerator";
        if (name.equals("") != false || date.equals("") != false) {
            model.addAttribute("goodsName", name);
            model.addAttribute("alert", "未入力項目があります。");
            return "input";
        } else if (name.indexOf("'") != -1 || name.indexOf('"') != -1 || name.indexOf("=") != -1
                || name.indexOf("”") != -1 || name.indexOf("’") != -1) {
            model.addAttribute("alert", "登録出来ない文字が含まれています。");
            return "input";
        } else {
            jdbc.update("INSERT INTO refrigerator (name, limitDay) VALUES ('" + name + "', to_date('" + date
                    + "', 'yyyy/MM/dd'))");
            printList(qry, model);
            return "top";
        }
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

    public void printList(String qry, Model model) {
        List<Goods> goods = new ArrayList<Goods>();
        List<Map<String, Object>> list = jdbc.queryForList(qry);
        for (int i = 0; i < list.size(); i++) {
            int id = Integer.parseInt(((list.get(i)).get("id").toString()));
            String name = (list.get(i)).get("name").toString();
            String limit = ((list.get(i).get("limitDay")).toString());
            LocalDate limitDay = LocalDate.parse(limit);
            LocalDate today = LocalDate.now();
            int state = 0;
            if (limitDay.minusDays(1).equals(today)) {
                state = 1;
            } else if (limitDay.equals(today)) {
                state = 2;
            } else if (limitDay.compareTo(today) < 0) {
                state = 3;
            } else {
                state = 0;
            }
            goods.add(new Goods(id, name, limit, limitDay, state));
        }

        printImage(goods, model);
        model.addAttribute("goodslist", goods);
    }
}
