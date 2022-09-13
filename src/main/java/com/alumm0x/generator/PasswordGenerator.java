package com.alumm0x.generator;

import burp.IIntruderPayloadGenerator;
import com.alumm0x.ui.PwOptions;
import com.alumm0x.util.CommonStore;
import com.alumm0x.util.PayloadBuildler;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 根据密码复杂度的要求，用户可以选择复杂度，以生成更匹配的弱口令字典
 * 密码复杂度的粒度分类如下
 * - 大写字母（顺序/逆序/重复，逆序可以取反就可以了）
 * - 小写字母（顺序/逆序/重复）
 * - 数字（顺序/逆序/重复）
 * - 特殊字符（分隔）
 * - 长度范围
 * 特殊场景
 * - 键位字典（键盘的弱口令）
 * - 年月日字典（日期的弱口令，归类到数字）
 * - 指定特征（比如admin，组成admin123等，内置常见默认密码数据）注意:组合时必会生成在第一位的密码，如123qwe，admin123qwe
 * - 首字母大写（直接处理最后保留的payload，新增首字母大写的数据）
 * 密码字典生成器的生成设计预期及思路
 * 1.每个复杂度粒度生成基础数据集（长度限制: 1~密码的MAX长度）,可以保存生成的数据集，以复用
 * 2.围绕键位生成基础数据集（长度限制: 1~密码的MAX长度）,可以保存生成的数据集，以复用
 * 3.生成当前年月日开始，往前计算xx年的年/年月/月日/年月日的基础数据集,可以保存生成的数据集，以复用
 * 4.根据所勾选的密码复杂度组合密码，[保留策略一]最后留下长度符合要求的payloads（复杂度会浮动，比如要求是大写字母/小写字母/数字/特殊字符的组合中的2种及以上组合且长度大于6的密码，复杂度最小2，最大4）
 * 5.组合的顺序问题，一般是随机组合的就可以了，但是"指定特征"比较特殊，需要以此开头进行组合
 * 6.可以将生成的payload导出，这样可以使用到别的工具中
 * 7.[保留策略二]payload的组合复杂度1～n，1也就本身，如123456，在后面才是多种组合，所以前面基础数据集的最大长度限制跟随密码长度限制，说明:如果最小长度也跟随密码长度，那组合后不太可能满足密码长度要求，如admin1
 * 8.最终的payload数据集会有两个门禁检查
 * 8.1 满足密码长度限制的[保留策略一]
 * 8.2 满足密码复杂度要求[保留策略二]
 *
 * UI设计
 * Info
 * <img>我独自升级</img>
 * pwOptions
 * 密码长度要求: MIN~MAX
 * 复杂度要求
 *  ☑️ 大写字母
 *  ☑️ 小写字母
 *  ☑️ 字母（不区分大小写,不能与上面的大写/小写共存）
 *  ☑️ 纯数字
 *  ☑️ 特殊字符
 * 密码复杂度: MIN~MAX
 * 基础数据集选择(根据选择的数据集生成密码,别选太多,不然有你等的),MinLen: xxx
 *  ☑️ 大写字母（字母/大写）-0
 *  ☑️ 小写字母（字母/小写）-1
 *  ☑️ 纯数字（数字）-2
 *  ☑️ 重复字母或数字（大写字母3_0/小写字母3_1/数据3_2)
 *  ☑️ 特殊字符（特殊字符）-4
 *  ☑️ 键位字典（数字/小写字母/特殊字符）-5
 *  ☑️ 时间字典（数字）-6
 *  ☑️ 首字母大写（字母）-7
 *  ☑️ 常见用户特征（字母，如admin/root/guest/tomcat等）-8
 * ｜----------------｜
 * ｜test            ｜|Remove|
 * ｜________________｜|Clear|
 * |Add| ________
 *  ☑️ 自定义组合（根据基础数据集后面的数据,以逗号分隔）
 *  |----------------|
 *  | 7,4,3_2        | |Set|
 *  |________________|
 *
 * Payloads
 * |Generate|  |Save|  Total: xxx
 * |------------------|
 * |__________________|
 * |__________________|
 * |__________________|
 * |__________________|
 * |__________________|
 * |__________________|
 */
public class PasswordGenerator implements IIntruderPayloadGenerator {

    private int payloadIndex = 0;

    /**
     * 判断是否还有payload，如果没有了就结束爆破任务
     * @return 是否还有payload
     */
    @Override
    public boolean hasMorePayloads() {
        return payloadIndex < CommonStore.PW_DATA.size();
    }

    /**
     * 获取下一个payload
     * @param baseValue 指原始请求中的原始值，非我们生成的payload，可以忽略
     * @return payload_byte
     */
    @Override
    public byte[] getNextPayload(byte[] baseValue) {
        byte[] pl = CommonStore.PW_DATA.get(payloadIndex).getBytes(StandardCharsets.UTF_8);
        payloadIndex++;
        return pl;
    }

    /**
     * 重置生成器，主要场景：同时对多个位置进行爆破，那么同一个生成器就需要重置到初始状态，也就是返回第一个payload
     * 也可以把数据集的加载初始化及处理放在这里
     */
    @Override
    public void reset() {
        payloadIndex = 0;
    }

    // 根据配置生成密码，追加到已有数据中，即分类持续添加
    public static void generate(){
        initConf();
        // 根据配置生成密码
        try {
            // 将结果添加到已有的数据中
            CommonStore.PW_DATA.addAll(makePaylaod());
        }catch (Exception e){
            CommonStore.callbacks.printError(e.getMessage());
        }
    }

    // 重新生成，从0开始
    public static void reGenerate(){
        initConf();
        // 根据配置生成密码
        try {
            CommonStore.PW_DATA = makePaylaod();
        }catch (Exception e){
            CommonStore.callbacks.printError(e.getMessage());
        }
    }

    private static void initConf(){
        // 配置设置
        CommonStore.MINLE_PW = Integer.parseInt(PwOptions.minlen_pw.getText());
        CommonStore.MAXLEN_PW = Integer.parseInt(PwOptions.maxlen_pw.getText());
        CommonStore.minRelex = Integer.parseInt(PwOptions.min_relex.getText());
        CommonStore.maxRelex = Integer.parseInt(PwOptions.max_relex.getText());
        CommonStore.MIN_LEN = Integer.parseInt(PwOptions.min_baseData.getText());
        CommonStore.CUSTOMIZE_CONFIG = PwOptions.customize_v.getText();
    }

    private static List<String> makePaylaod(){
        List<String> all = new ArrayList<>(); // 保存最后保留的payload
        Map<String, List<String>> listss = new HashMap<>(); // 保存基础数据集及对应index
        List<List<String>> base_list = new ArrayList<>(); // 循环的基础数据集
        List<String> sorts = new ArrayList<>(); //保存数据集组合的数据
        // 复杂度跟基础数据集绑定
        if (CommonStore.UPPER_OFF && (CommonStore.CHECKCONTAINUPPERCASE_OFF || CommonStore.CHECKCONTAINCASE_OFF)){
            listss.put("0", PayloadBuildler.getUpperData());
        }
        if (CommonStore.LOWER_OFF && (CommonStore.CHECKCONTAINLOWERCASE_OFF || CommonStore.CHECKCONTAINCASE_OFF)){
            listss.put("1", PayloadBuildler.getLowerData());
        }
        if (CommonStore.NUMBER_OFF && CommonStore.CHECKCONTAINDIGIT_OFF){
            listss.put("2", PayloadBuildler.getNumberData());
        }
        if (CommonStore.DUPLICATE_OFF){ //重复字符
            if (CommonStore.CHECKCONTAINUPPERCASE_OFF) { //小写勾选
                listss.put("3_0", PayloadBuildler.makeDups(PayloadBuildler.UPPER));
            }
            if (CommonStore.CHECKCONTAINLOWERCASE_OFF) { //大写勾选
                listss.put("3_1", PayloadBuildler.makeDups(PayloadBuildler.LOWER));
            }
            if (CommonStore.CHECKCONTAINDIGIT_OFF) { //数字勾选
                listss.put("3_2", PayloadBuildler.makeDups(PayloadBuildler.DIGIT));
            }
        }
        if (CommonStore.SPECIAL_OFF && CommonStore.CHECKCONTAINSPECIALCHAR_OFF){
            listss.put("4", PayloadBuildler.getSpecialData());
        }
        if (CommonStore.KEYBOARD_OFF){
            listss.put("5", PayloadBuildler.getKeyboardData());
        }
        if (CommonStore.TIME_OFF){
            listss.put("6", PayloadBuildler.getTimeData());
        }
        if (CommonStore.FIRSTUPPER_OFF && CommonStore.CHECKCONTAINCASE_OFF){
            listss.put("7", PayloadBuildler.getFirstUpperData(PayloadBuildler.getLowerData()));
        }
        if (CommonStore.DEFAULT_OFF){
            listss.put("8", CommonStore.DEFAULT_DATA);
        }
        // 开启了自定义,就根据自定义的结构生成密码,并根据顺序构造待组合的数据集集
        if (CommonStore.CUSTOMIZE_OFF && !CommonStore.CUSTOMIZE_CONFIG.equalsIgnoreCase("")){
            String[] s = CommonStore.CUSTOMIZE_CONFIG.split(",");
            // 根据定义的顺序重新排序
            for (String index : s) {
                base_list.add(listss.get(index));
            }
            // 根据基础数据集的长度生成排序组合
            List<String> list = IntStream.range(0, base_list.size())
                    .mapToObj(Integer::toString)
                    .collect(Collectors.toList());
            sorts.addAll(Collections.singletonList(String.join(",", list)));
        }
        if (base_list.size() == 0) {
            //默认就根据复杂度及基础数据集生成密码
            base_list = new ArrayList<>(listss.values());
            // 根据基础数据集的长度生成排序组合
            List<String> list = IntStream.range(0, base_list.size())
                    .mapToObj(Integer::toString)
                    .collect(Collectors.toList());

            // 根据最小复杂度设置控制集合组合，这里虽然不能完全匹配复杂度，但是能大幅度减少产生的数据，较少内存占用
            for (int i = list.size(); i >= CommonStore.minRelex; i--) {
                // 生成不同集合的组合
                sorts.addAll(PayloadBuildler.permutationNoRepeat(list, i));
            }
        }
        // 如果存在null，则说明存在配置错误，推出
        if (base_list.contains(null)){
            //TODO 编写错误提示
            return all;
        }
        // 遍历排序组合生成payload
        for (String sort : sorts) {
            String[] s = sort.split(",");
            switch (s.length) {
                case 0:
                    break;
                case 1:
                    all.addAll(PayloadBuildler.descartes(
                            base_list.get(Integer.parseInt(s[0]))
                    ).stream().filter(PasswordGenerator::pwCheck).collect(Collectors.toList())); // java8 filter做密码要求的校验，过滤出符合要求的密码
                    break;
                case 2:
                    all.addAll(PayloadBuildler.descartes(
                            base_list.get(Integer.parseInt(s[0]))
                            , base_list.get(Integer.parseInt(s[1]))
                    ).stream().filter(PasswordGenerator::pwCheck).collect(Collectors.toList()));
                    break;
                case 3:
                    all.addAll(PayloadBuildler.descartes(
                            base_list.get(Integer.parseInt(s[0]))
                            , base_list.get(Integer.parseInt(s[1]))
                            , base_list.get(Integer.parseInt(s[2]))
                    ).stream().filter(PasswordGenerator::pwCheck).collect(Collectors.toList()));
                    break;
                case 4:
                    all.addAll(PayloadBuildler.descartes(
                            base_list.get(Integer.parseInt(s[0]))
                            , base_list.get(Integer.parseInt(s[1]))
                            , base_list.get(Integer.parseInt(s[2]))
                            , base_list.get(Integer.parseInt(s[3]))
                    ).stream().filter(PasswordGenerator::pwCheck).collect(Collectors.toList()));
                    break;
                case 5:
                    all.addAll(PayloadBuildler.descartes(
                            base_list.get(Integer.parseInt(s[0]))
                            , base_list.get(Integer.parseInt(s[1]))
                            , base_list.get(Integer.parseInt(s[2]))
                            , base_list.get(Integer.parseInt(s[3]))
                            , base_list.get(Integer.parseInt(s[4]))
                    ).stream().filter(PasswordGenerator::pwCheck).collect(Collectors.toList()));
                    break;
                case 6:
                    all.addAll(PayloadBuildler.descartes(
                            base_list.get(Integer.parseInt(s[0]))
                            , base_list.get(Integer.parseInt(s[1]))
                            , base_list.get(Integer.parseInt(s[2]))
                            , base_list.get(Integer.parseInt(s[3]))
                            , base_list.get(Integer.parseInt(s[4]))
                            , base_list.get(Integer.parseInt(s[5]))
                    ).stream().filter(PasswordGenerator::pwCheck).collect(Collectors.toList()));
                    break;
                case 7:
                    all.addAll(PayloadBuildler.descartes(
                            base_list.get(Integer.parseInt(s[0]))
                            , base_list.get(Integer.parseInt(s[1]))
                            , base_list.get(Integer.parseInt(s[2]))
                            , base_list.get(Integer.parseInt(s[3]))
                            , base_list.get(Integer.parseInt(s[4]))
                            , base_list.get(Integer.parseInt(s[5]))
                            , base_list.get(Integer.parseInt(s[6]))
                    ).stream().filter(PasswordGenerator::pwCheck).collect(Collectors.toList()));
                    break;
                case 8:
                    all.addAll(PayloadBuildler.descartes(
                            base_list.get(Integer.parseInt(s[0]))
                            , base_list.get(Integer.parseInt(s[1]))
                            , base_list.get(Integer.parseInt(s[2]))
                            , base_list.get(Integer.parseInt(s[3]))
                            , base_list.get(Integer.parseInt(s[4]))
                            , base_list.get(Integer.parseInt(s[5]))
                            , base_list.get(Integer.parseInt(s[6]))
                            , base_list.get(Integer.parseInt(s[6]))
                    ).stream().filter(PasswordGenerator::pwCheck).collect(Collectors.toList()));
                    break;
            }
        }

        return all;
    }


    /**
     * 密码复杂度的检测代码,计算复杂度是否大于最小复杂度要求CommonStore.minRelex,且小于或等于最大复杂度，有种清空是获取单一复杂度的密码
     * @param pw 待检测的密码
     * @return 返回复杂度符合结果
     */
    private static boolean pwCheck(String pw){
        int relex = 0; //复杂度
        // 长度校验
        if (pw.length() < CommonStore.MINLE_PW || pw.length() > CommonStore.MAXLEN_PW){
            return false;
        }
        // 是否包含数字
        if (CommonStore.CHECKCONTAINDIGIT_OFF && checkContainDigit(pw)){
            relex += 1;
        }
        // 是否包含小写字母
        if (CommonStore.CHECKCONTAINLOWERCASE_OFF && checkContainLowerCase(pw)){
            relex += 1;
        }
        // 是否包含大写字母
        if (CommonStore.CHECKCONTAINUPPERCASE_OFF && checkContainUpperCase(pw)){
            relex += 1;
        }
        // 是否包含特殊字符
        if (CommonStore.CHECKCONTAINSPECIALCHAR_OFF && checkContainSpecialChar(pw)){
            relex += 1;
        }
        // 是否包含字母，不区分大小写
        if (CommonStore.CHECKCONTAINCASE_OFF && checkContainCase(pw)){
            relex += 1;
        }
        return relex >= CommonStore.minRelex && relex <= CommonStore.maxRelex;
    }

    /**
     * 检查密码中是否包含字母(不区分大小写)
     *
     * @param password 待检测的密码
     * @return 包含字母 返回true
     */
    private static boolean checkContainCase(String password) {
        char[] chPass = password.toCharArray();
        boolean flag = false;
        int char_count = 0;

        for (char pass : chPass) {
            if (Character.isLetter(pass)) {
                char_count++;
            }
        }

        if (char_count >= 1) {
            flag = true;
        }

        return flag;
    }

    /**
     * 检查密码中是否包含数字
     *
     * @param password 待检测的密码
     * @return 包含数字 返回true
     */
    private static boolean checkContainDigit(String password) {
        char[] chPass = password.toCharArray();
        boolean flag = false;
        int num_count = 0;

        for (char pass : chPass) {
            if (Character.isDigit(pass)) {
                num_count++;
            }
        }
        if (num_count >= 1) {
            flag = true;
        }
        return flag;
    }

    /**
     * 检查密码中是否包含小写字母
     *
     * @param password 待检测的密码
     * @return 包含小写字母 返回true
     */
    private static boolean checkContainLowerCase(String password) {
        boolean flag = false;
        char[] chPass = password.toCharArray();
        int char_count = 0;

        for (char pass : chPass) {
            if (Character.isLowerCase(pass)) {
                char_count++;
            }
        }

        if (char_count >= 1) {
            flag = true;
        }

        return flag;
    }

    /**
     * 检查密码中是否包含大写字母
     *
     * @param password 待检测的密码
     * @return 包含大写字母 返回true
     */
    private static boolean checkContainUpperCase(String password) {
        boolean flag = false;
        char[] chPass = password.toCharArray();
        int char_count = 0;

        for (char pass : chPass) {
            if (Character.isUpperCase(pass)) {
                char_count++;
            }
        }

        if (char_count >= 1) {
            flag = true;
        }

        return flag;
    }

    /**
     * 检查密码中是否包含特殊字符
     *
     * @param password 待检测的密码
     * @return 包含特殊字符 返回true
     */
    private static boolean checkContainSpecialChar(String password) {
        boolean flag = false;
        char[] chPass = password.toCharArray();
        int special_count = 0;

        for (char pass : chPass) {
            if (PayloadBuildler.SPECIAL.contains(String.valueOf(pass))) {
                special_count++;
            }
        }

        if (special_count >= 1) {
            flag = true;
        }

        return flag;
    }

}
