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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {

    @Autowired
    private JdbcTemplate jdbc;
    
    @Autowired
    private RefrigeratorDao refrigeratorDao;

    // @Value("${app.name}")
    // private String appName;
    
    @GetMapping("/test")
    public String test(){
        System.out.println(refrigeratorDao.findById(2));

        return "";
    }

    /**
     * (/top)にアクセスしたときにトップ画面を表示する。
     * @param model
     * @return
     */
    @GetMapping("/top")
    public String top(Model model) {

        String qry = "SELECT id, name, limitDay FROM refrigerator";
        getPrintList(qry, model);
        return "top";
    }

    @GetMapping("/input")
    public String input(Model model) {
        return "input";
    }

    @GetMapping("/search")
    public String search(Model model) {
        String qry = "SELECT id, name, limitDay FROM refrigerator";
        getPrintList(qry, model);
        return "search";
    }

    @PostMapping("/returnTop")
    public String returnTop(){
        return "redirect:/top";
    }

    @PostMapping("/in")
    public String moveInput() {
        return "redirect:/input";
    }

    @PostMapping("/out")
    public String moveOut(int[] selectGoods, RedirectAttributes attr) {

        String qry = "SELECT id, name, limitDay FROM refrigerator";
        
        if(selectGoods != null){
            for(int i = 0; i < selectGoods.length; i++) {
                jdbc.update("DELETE FROM refrigerator WHERE id = ?", selectGoods[i]);
            }
        } else{
            attr.addFlashAttribute("alert", "取り出す項目を選択してください。");
        }

        postPrintList(qry, attr);
        return "redirect:/top";
    }

    @PostMapping("/searchForm")
    public String searchForm(RedirectAttributes attr) {
        String qry = "SELECT id, name, limitDay FROM refrigerator";
        postPrintList(qry, attr);
        return "redirect:/search";
    }

    @PostMapping("/selectSearch")
    public String selectSearch(int[] selectGoods, RedirectAttributes attr) {
        StringBuffer url = new StringBuffer("https://cookpad.com/search/");
        StringBuffer urlText;
        String qry = "SELECT id, name, limitDay FROM refrigerator";
        String name = "";
        postPrintList(qry, attr);
        if (selectGoods != null) {
            name = (jdbc.queryForList("SELECT name FROM refrigerator WHERE id = ?", selectGoods[0])).get(0).get("name")
                    .toString();
            url.append(name);
            urlText = new StringBuffer(name);
            for (int i = 1; i < selectGoods.length; i++) {
                name = (jdbc.queryForList("SELECT name FROM refrigerator WHERE id = ?", selectGoods[i])).get(0)
                        .get("name").toString();
                url.append("%20");
                url.append(name);
                urlText.append(",");
                urlText.append(name);
            }
            attr.addFlashAttribute("urlText", urlText + "を使ったレシピ");
        }
        attr.addFlashAttribute("url", url);
        return "redirect:/search";
    }

    @PostMapping("/type")
    public String selectType(String type, RedirectAttributes attr) {

        if (type.equals("other")) {
            attr.addFlashAttribute("limit", "");
            attr.addFlashAttribute("select6", true);
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
            attr.addFlashAttribute(types, true);
            attr.addFlashAttribute("limit", date);
        }
        return "redirect:/input";
    }

    @PostMapping("/addGoods")
    public String addGoods(AddGoodsForm form, RedirectAttributes attr) {

        String name = form.getName();
        String date = form.getDate();
        String qry = "SELECT id, name, limitDay FROM refrigerator";
        if (name.equals("") != false || date.equals("") != false) {
            attr.addFlashAttribute("goodsName", name);
            attr.addFlashAttribute("alert", "未入力項目があります。");
            return "redirect:/input";
        } else if (name.indexOf("'") != -1 || name.indexOf('"') != -1 || name.indexOf("=") != -1
                || name.indexOf("”") != -1 || name.indexOf("’") != -1) {
            attr.addFlashAttribute("alert", "登録出来ない文字が含まれています。");
            return "redirect:/input";
        } else {
            jdbc.update("INSERT INTO refrigerator (name, limitDay) VALUES (?, to_date(?, 'yyyy/MM/dd'))", name, date);
            postPrintList(qry, attr);
            return "redirect:/top";
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

    public void getPrintList(String qry, Model model) {
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

    public void postPrintList(String qry, RedirectAttributes attr) {
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

        attr.addFlashAttribute("goodslist", goods);
    }
}
