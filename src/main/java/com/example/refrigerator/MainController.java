package com.example.refrigerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {

    @Autowired
    private RefrigeratorDao refrigeratorDao;

    @Autowired
    private OverLimitDataDao overLimitDataDao;

    // @Value("${app.name}")
    // private String appName;

    /**
     * [/top]にアクセスしたときにトップ画面を表示する。
     * @param model
     * @return topページを返す。
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

    @GetMapping("/graph")
    public String graph(Model model) {
        ArrayList<Graph> graph = new ArrayList<Graph>();
        for(int i=0;i<12;i++){
            LocalDate localDate = LocalDate.now().minusMonths(11 - i);
            String year = String.valueOf(localDate.getYear());
            String month = String.valueOf(localDate.getMonthValue());
            if(month.length() == 1){
                month = "0" + month;
            }
            List<OverLimitData> list = overLimitDataDao.findByDate(year, month);
            graph.add(new Graph(year+"-"+month, list.size()));
        }
        model.addAttribute("sum", overLimitDataDao.findAll().size());
        model.addAttribute("list", graph);
        return "graph";
    }

    @PostMapping("/in")
    public String moveInput() {
        return "redirect:/input";
    }

    @PostMapping("/out")
    public String moveOut(int[] selectGoods, RedirectAttributes attr) {
        
        if(selectGoods != null){
            for(int i = 0; i < selectGoods.length; i++) {
                List<Refrigerator> goods = refrigeratorDao.findById(selectGoods[i]);
                if(goods.get(0).getLimitDay().compareTo(LocalDate.now()) < 0){
                    overLimitDataDao.insertData(goods.get(0).getName(), goods.get(0).getLimitDay().toString());
                }
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
        } else{
            attr.addFlashAttribute("alert", "レシピ検索をしたい商品を選択してください。");
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

    @PostMapping("/graphTable:{date}")
    public String graphTable(@PathVariable String date,RedirectAttributes attr){
        List<OverLimitData> list = overLimitDataDao.findByDate(date.split("-")[0], date.split("-")[1]);
        attr.addFlashAttribute("graphTable", list);
        return "redirect:graph";
    }

    public void printImage(List<Goods> goods, Model model) {
        boolean flag = false;
        for (int i = 0; i < goods.size(); i++) {
            if (goods.get(i).getLimitDay().compareTo(goods.get(i).getToday()) < 0) {
                flag = true;
            }
        }

        if (flag) {
            model.addAttribute("alertGoods", "期限切れの商品があります。");
            model.addAttribute("path", "bad.png");
        } else {
            model.addAttribute("path", "normal.png");
        }
    }

    /**
     * refrigeratorテーブル内のデータをhtmlに渡す。
     * <p>
     * goodsリストにデータベース内の各商品情報を挿入する。
     * @param list
     * @param model
     */
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
}
