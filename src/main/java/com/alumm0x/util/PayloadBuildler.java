package com.alumm0x.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PayloadBuildler {

    public static List<String> UPPER = Arrays.asList("A","B","V","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z");
    public static List<String> LOWER = Arrays.asList("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z");
    public static List<String> DIGIT = Arrays.asList("0","1","2","3","4","5","6","7","8","9");
    public static List<String> SPECIAL = Arrays.asList("~","`","!","@","#","$","%","^","&","*","(",")","-","_","=","+","[","]","{","}","\\","|",";",":","'","\"",",","<",".",">","/","?");

    //##########用于整理字典，去重合并###################
    public static void main(String[] args){
        List<String> list = SourceLoader.loadSources("/password/password.oh").stream().distinct().collect(Collectors.toList());
        writeFile("/Users/cvte/selfspace/code/FuzzPayloadGennerator/src/main/resources/password/password.oh", list);
    }
    //写文件
    private static void writeFile(String savepath, List<String> list){
        FileOutputStream fos= null;
        try {
            fos = new FileOutputStream(savepath);
            for (String textArea: list) {
                fos.write((textArea + "\n").getBytes());
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //##########用于整理字典，去重合并###################
    /**
     * java8新特性 笛卡尔积算法，求多数据集所有组合，但不排序哈，顺序全看传入数据集的顺序
     * @param lists 数据集
     * @return 所有组合的List
     */
    @SafeVarargs
    public static List<String> descartes(List<String>... lists) {
        List<String> tempList = new ArrayList<>();
        for (List<String> list : lists) {
            if (tempList.isEmpty()) {
                tempList = list;
            } else {
                // java8新特性，stream流
                tempList = tempList.stream().flatMap(item -> list.stream().map(item2 -> item + "" + item2)).collect(Collectors.toList());
            }
        }
        return tempList;
    }

    /**
     * 排列组合(字符不重复排列)
     * 内存占用：需注意结果集大小对内存的占用（list:10位，length:8，结果集:[10! / (10-8)! = 1814400]）
     * @param list 待排列组合字符集合(忽略重复字符)
     * @param length 排列组合生成长度
     * @return 指定长度的排列组合后的字符串集合
     */
    public static List<String> permutationNoRepeat(List<String> list, int length) {
        Stream<String> stream = list.stream().distinct();
        for (int n = 1; n < length; n++) {
            stream = stream.flatMap(str -> list.stream()
                    .filter(temp -> !str.contains(temp))
                    .map(temp -> str + "," + temp));
        }
        return stream.collect(Collectors.toList());
    }

    /**
     * 获取List的子集,按List顺序获取，不考虑随机组合，有序的才符合弱口令
     * 大概思路：getSubLists({1,2,3,4,5}, 3, true)
     * - |1,2|,3,4,5
     * - 1,|2,3|,4,5
     * - 1,2,|3,4|,5
     * - 1,2,3,|4,5|
     * - |5,4|,3,2,1
     * - 5,|4,3|,2,1
     * - 5,4,|3,2|,1
     * - 5,4,3,|2,1|
     *
     * @param list 源List，获取其子集
     * @param maxLen 控制子集的最大长度
     * @param needReverse 是否需要获取反序的子集
     * @return 返回子集String的List列表
     */
    public static List<String> getSubLists(List<String> list, int maxLen, boolean needReverse){
        List<String> subLists = new ArrayList<>();
        // 如果list的长度小于maxlen，则重新设置maxlen，避免越界异常
        if (list.size() < maxLen){
            maxLen = list.size();
        }
        for (int i = 1; i <= maxLen; i++){ // 控制子集的最大长度
            // 控制子集最小长度
            if (i < CommonStore.MIN_LEN){
                continue;
            }
            int first = 0;
            int end = first + i;
            // 直到end达到最后元素的位置
            while (end <= list.size()) {
                List<String> sub = list.subList(first, end);
                subLists.add(String.join("",sub)); //将子集转换成String再添加
                first++; // 往后挪一位
                end++; // 往后挪一位
            }
        }
        if (needReverse){
            Collections.reverse(list);
            // 递归获取反转后的子集
            List<String> reverseList = getSubLists(list, maxLen, false);
            // 将反转子集结果添加进去
            subLists.addAll(reverseList);
        }
        return subLists;
    }

    // ########################密码基础数据集############################
    /**
     * 根据列表的元素生成重复字符串，长度由CommonStore.maxLen控制
     * @param lists 待处理的列表
     * @return 返回生成的重复字符列表
     */
    @SafeVarargs
    public static List<String> makeDups(List<String>... lists){
        List<String> tempList = new ArrayList<>();
        for (List<String> list : lists) {
            for (String str : list) {
                for (int i = 1; i <= CommonStore.MAXLEN_PW; i++) {
                    // java8 生成重复字符串
                    tempList.add(String.join("", Collections.nCopies(i, str)));
                }
            }
        }
        return tempList;
    }

    /**
     * 大写字母数据集，纯大写，长度有CommonStore.MAXLEN_PW控制
     * @return 返回纯大写的字母列表
     */
    public static List<String> getUpperData(){
        return PayloadBuildler.getSubLists(UPPER, CommonStore.MAXLEN_PW, true);
    }

    /**
     * 小写字母数据集，纯小写，长度有CommonStore.MAXLEN_PW控制
     * @return 返回纯小写的字母列表
     */
    public static List<String> getLowerData(){
        return PayloadBuildler.getSubLists(LOWER, CommonStore.MAXLEN_PW, true);
    }

    /**
     * 数字数据集，纯数字，长度有CommonStore.MAXLEN_PW控制
     * @return 返回纯数字的组合列表
     */
    public static List<String> getNumberData(){
        return PayloadBuildler.getSubLists(DIGIT, CommonStore.MAXLEN_PW, true);
    }

    /**
     * 特殊字符数据集，特殊字符主要是做分隔使用，单一使用，无需组合
     * @return 返回特殊字符列表
     */
    public static List<String> getSpecialData(){
        return SPECIAL;
    }

    /**
     * 键位数据集，长度默认10，根据键位行最长长度
     * @return 键位的弱组合列表
     */
    public static List<String> getKeyboardData(){
        List<String> keyboard = new ArrayList<>();
        // 抽象键位
        // 数字加字母的键位
        List<String> num1 = Arrays.asList("1","2","3","4","5","6","7","8","9","0");
        List<String> tab = Arrays.asList("q","w","e","r","t","y","u","i","o","p");
        List<String> capslk = Arrays.asList("a","s","d","f","g","h","j","k","l");
        List<String> shift = Arrays.asList("z","x","c","v","b","n","m",",",".","/");
        // 九宫格的数字键
        List<String> num9_1 = Arrays.asList("7","8","9");
        List<String> num9_2 = Arrays.asList("4","5","6");
        List<String> num9_3 = Arrays.asList("1","2","3");

        // 1.每行的子集
        keyboard.addAll(PayloadBuildler.getSubLists(num1, 10, true));
        keyboard.addAll(PayloadBuildler.getSubLists(tab, 10, true));
        keyboard.addAll(PayloadBuildler.getSubLists(capslk, 10, true));
        keyboard.addAll(PayloadBuildler.getSubLists(shift, 10, true));
        // 2.多行的笛卡尔积
        keyboard.addAll(PayloadBuildler.descartes(tab,capslk,shift)); //纯字母，如qaz
        keyboard.addAll(PayloadBuildler.descartes(num1,tab,capslk,shift)); //字母加数字，如1qaz
        keyboard.addAll(PayloadBuildler.descartes(num9_1,num9_2,num9_3)); //九宫格，如741/753

        return keyboard;
    }

    /**
     * 时间数据集，根据当前时间往前推50年
     * @return
     * 1.年月组合
     * 2.月日组合
     * 3.年月日组合
     */
    public static List<String> getTimeData(){
        List<String> time = new ArrayList<>();
        // 获取当前年份
        Calendar calendar = Calendar.getInstance();
        int year_now = calendar.get(Calendar.YEAR);
        int year_lod = year_now - 50; // 往前推50年
        // java8 新特性生成顺序列表
        List<String> year = IntStream.range(year_lod, year_now + 1)
                .mapToObj(Integer::toString) // 调用Integer.toString(int)转成String
                .collect(Collectors.toList());
        // 月份
        List<String> month = Arrays.asList("01","02","03","04","05","06","07","08","09","10","11","12");
        // 日
        List<String> day = Arrays.asList("01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31");

        time.addAll(PayloadBuildler.descartes(year, month)); // 年月
        time.addAll(PayloadBuildler.descartes(month, day)); // 月日
        time.addAll(PayloadBuildler.descartes(year, month, day)); // 年月日
        return time;
    }

    /**
     * 首字母大写数据集，只是处理方法，将已有的数据处理成首字母大写
     * @param lowerList 待处理的列表
     * @return 返回首字母大写的列表
     */
    //TODO 这样设计太受限了，搞个开阔点的设计
    public static List<String> getFirstUpperData(List<String> lowerList){
        List<String> firstUpper = new ArrayList<>();
        for (String str :
                lowerList) {
            char[] cs = str.toCharArray();
            cs[0] -= 32;
            firstUpper.add(String.valueOf(cs));
        }

        return firstUpper;
    }

    /**
     * 指定特征（默认admin/root/test/user/guest）
     */
    public static List<String> getDefaultData(){
        return SourceLoader.loadSources("/password/default.oh");
    }
    // ########################密码基础数据集############################

    /**
     * 记载本地文件数据
     * @param path 本地文件完整路径
     * @return 按行处理为List返回
     */
    public static List<String> getLocalData(String path){
        List<String> list = new ArrayList<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            String str;
            while ((str = in.readLine()) != null) {
                list.add(str);
            }
        } catch (Exception e) {
            CommonStore.callbacks.printError("[PayloadBuildler.getLocalData]" + e.getMessage());
        }
        return list;
    }

    //################对数据进行处理#####################

    /**
     * 首字母大写
     * @param str 待处理的字符串
     * @return 处理后的结果
     */
    public static String firstToUpper(String str){
        char[] cs = str.toCharArray();
        // 处理非字母开头的情况，第一个字母大写
        for (int x = 0; x < cs.length; x++) {
            if (Character.isLowerCase(cs[x])) {
                cs[x] -= 32;
                break;
            }
        }
        return String.valueOf(cs);
    }

    /**
     * 添加反序，比如123 -> 123321
     * @param str 待处理的字符串
     * @return 处理后的结果
     */
    public static String addReverse(String str){
        return str + new StringBuffer(str).reverse();
    }

}
