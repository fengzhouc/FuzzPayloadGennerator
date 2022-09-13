package com.alumm0x.util;

import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;

import java.util.ArrayList;
import java.util.List;

public class CommonStore {

    public static IBurpExtenderCallbacks callbacks;
    public static IExtensionHelpers helpers;

    // 密码options-start
    public static int MINLE_PW = 6; // 密码最小长度,默认6
    public static int MAXLEN_PW = 8; // 密码最大长度,默认8，因为12345678
    public static int minRelex = 1; //最小的密码复杂度,默认1
    public static int maxRelex = 4; //最大的密码复杂度,默认4
    // 复杂度要求
    public static boolean CHECKCONTAINUPPERCASE_OFF = true; // 复杂度：大写字母
    public static boolean CHECKCONTAINLOWERCASE_OFF = true; // 复杂度：小写字母
    public static boolean CHECKCONTAINCASE_OFF = false; // 复杂度：字母，不区分大小写
    public static boolean CHECKCONTAINDIGIT_OFF = true; // 复杂度：数字
    public static boolean CHECKCONTAINSPECIALCHAR_OFF = true; // 复杂度：特殊字符

    // 基础数据集选用
    public static int MIN_LEN = 1; // 基础数据集的最小片段长度，默认1
    public static boolean UPPER_OFF = false; // 基础数据集：大写字母
    public static boolean LOWER_OFF = false; // 基础数据集：小写字母
    public static boolean NUMBER_OFF = true; // 基础数据集：数字
    public static boolean DUPLICATE_OFF = false; // 基础数据集：重复字母或数字
    public static boolean SPECIAL_OFF = true; // 基础数据集：特殊字符
    public static boolean KEYBOARD_OFF = false; // 基础数据集：键位数据
    public static boolean TIME_OFF = false; // 基础数据集：年月日
    public static boolean FIRSTUPPER_OFF = false; // 基础数据集：首字母大写
    public static boolean DEFAULT_OFF = true; // 基础数据集：常见用户特征.可添加
    public static boolean CUSTOMIZE_OFF = false; // 自定义密码组成，比如特征+特殊字符+数字，admin@123
    public static String CUSTOMIZE_CONFIG = ""; // 自定义密码组成，比如特征+特殊字符+数字，admin@123

    // 密码options-end
    //将生成的字典存放在这里
    public static List<String> PW_DATA = new ArrayList<>();
    public static List<String> DEFAULT_DATA = PayloadBuildler.getDefaultData();



}
