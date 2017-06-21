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

    // top画面 printList()でデータベース内の商品情報をhtmlに渡す。
    /**
     * [/top]にアクセスしたときにトップ画面を表示する。
     * @param model
     * @return topページを返す。
     */
    @GetMapping("/top")
    public String top(Model model) {
        printList(refrigeratorDao.findAll(), model);
        return "top";
    }

    // input画面
    @GetMapping("/input")
    public String input(Model model) {
        return "input";
    }

    // search画面 printList()でデータベース内の商品情報をhtmlに渡す。
    @GetMapping("/search")
    public String search(Model model) {
        printList(refrigeratorDao.findAll(), model);
        return "search";
    }

    // graph画面 graph表示用のデータをリストで作成する(graph)。
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

    // 「取り出し」ボタン処理
    // 選択したチェックボックスのidを取得し、合致するidの商品をデータベースから削除する。
    // ・もし期限が切れている商品が選択されていた場合、overlimitdataテーブルに追加する。
    // 何も選択されていない状態でボタンが押された場合、アラート文を表示する。
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

    // 「url作成」ボタン処理
    // 選択された商品のidを取得し、合致するidの商品名を参照してurlを作成する。
    // ・作成したurlはhtmlに渡す。
    // 何も選択されていない状態でボタンが押された場合、アラート文を表示する。
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

    // input画面　商品区分選択時の処理
    // 選択された商品区分に応じて参考消費期限を算出し、htmlで表示する。
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
        attr.addFlashAttribute("target", "target");
        return "redirect:/input";
    }

    // input画面　「入力完了」ボタン処理
    // 未入力項目や使用不可能な文字が入力されていた場合、アラート文を表示する。
    // 入力に不備がない場合、データベースに商品情報を追加し、topページに遷移する。
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

    // graph画面　テーブル表示用メソッド
    // urlから取得した年と月をもとにデータベースの検索を行い、参照したデータをテーブルで表示する。
    @PostMapping("/graphTable/{date}")
    public String graphTable(@PathVariable String date, RedirectAttributes attr){
        List<OverLimitData> list = overLimitDataDao.findByDate(date.split("-")[0], date.split("-")[1]);
        attr.addFlashAttribute("graphTable", list);
        return "redirect:/graph";
    }

    // top画面　画像表示用メソッド
    // 商品情報の入ったリストを引数にとり、消費期限切れの商品があった場合には画像を変更しアラート文を表示する。
    public void printImage(List<Goods> goods, Model model) {
        boolean flag = false;
        for (int i = 0; i < goods.size(); i++) {
            if (goods.get(i).getState() == 3) {
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

    // 商品リスト表示用のメソッド　top画面とsearch画面で使用
    // 引数にデータベース内の全ての商品情報を持ったリストを取る。
    // 今日の日付との比較を行い、商品の状態を定義する。
    // 商品の状態を追加したリストgoodsを作成し、htmlに渡す。
    /**
     * refrigeratorテーブル内のデータをhtmlに渡す。
     * <p>
     * goodsリストにデータベース内の各商品情報を挿入する。
     * @param list
     * @param model
     */
    public void printList(List<Refrigerator> list, Model model) {
        List<Goods> goods = new ArrayList<Goods>();
        for (int i = 0; i < list.size(); i++) {
            int id = list.get(i).getId();
            String name = list.get(i).getName();
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
            goods.add(new Goods(id, name, limitDay, state));
        }

        printImage(goods, model);
        model.addAttribute("goodslist", goods);
    }
}
