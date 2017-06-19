package com.example.refrigerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {

    @Autowired
    private RefrigeratorDao refrigeratorDao;

    // @Value("${app.name}")
    // private String appName;

    /**
     * (/top)にアクセスしたときにトップ画面を表示する。
     * @param model
     * @return
     */
    @GetMapping("/top")
    public String top(Model model) {
        getPrintList(refrigeratorDao.findAll(), model);
        return "top";
    }

    @GetMapping("/input")
    public String input(Model model) {
        return "input";
    }

    @GetMapping("/search")
    public String search(Model model) {
        getPrintList(refrigeratorDao.findAll(), model);
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
        
        if(selectGoods != null){
            for(int i = 0; i < selectGoods.length; i++) {
                refrigeratorDao.deleteById(selectGoods[i]);
            }
        } else{
            attr.addFlashAttribute("alert", "取り出す項目を選択してください。");
        }

        return "redirect:/top";
    }

    @PostMapping("/searchForm")
    public String searchForm(RedirectAttributes attr) {
        return "redirect:/search";
    }

    @PostMapping("/selectSearch")
    public String selectSearch(int[] selectGoods, RedirectAttributes attr) {
        StringBuffer url = new StringBuffer("https://cookpad.com/search/");
        StringBuffer urlText;
        String name = "";
        //postPrintList(refrigeratorDao.findAll(), attr);
        if (selectGoods != null) {
            name = refrigeratorDao.findById(selectGoods[0]).get(0).getName();
            url.append(name);
            urlText = new StringBuffer(name);
            for (int i = 1; i < selectGoods.length; i++) {
                name = refrigeratorDao.findById(selectGoods[i]).get(0).getName();
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
        if (name.equals("") != false || date.equals("") != false) {
            attr.addFlashAttribute("goodsName", name);
            attr.addFlashAttribute("alert", "未入力項目があります。");
            return "redirect:/input";
        } else if (name.indexOf("'") != -1 || name.indexOf('"') != -1 || name.indexOf("=") != -1
                || name.indexOf("”") != -1 || name.indexOf("’") != -1) {
            attr.addFlashAttribute("alert", "登録出来ない文字が含まれています。");
            return "redirect:/input";
        } else {
            refrigeratorDao.insertGoods(name, date);
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

    public void getPrintList(List<Refrigerator> list, Model model) {
        List<Goods> goods = new ArrayList<Goods>();
        for (int i = 0; i < list.size(); i++) {
            int id = list.get(i).getId();
            String name = list.get(i).getName();
            String limit = list.get(i).getLimitDay().toString();
            LocalDate limitDay = list.get(i).getLimitDay();
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

    /*public void postPrintList(List<Refrigerator> list, RedirectAttributes attr) {
        List<Goods> goods = new ArrayList<Goods>();
        for (int i = 0; i < list.size(); i++) {
            int id = list.get(i).getId();
            String name = list.get(i).getName();
            String limit = list.get(i).getLimitDay().toString();
            LocalDate limitDay = list.get(i).getLimitDay();
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
    }*/
}
