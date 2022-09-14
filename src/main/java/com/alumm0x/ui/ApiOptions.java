package com.alumm0x.ui;


import com.alumm0x.generator.ApiGenerator;
import com.alumm0x.generator.PasswordGenerator;
import com.alumm0x.util.CommonStore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 UI设计
 * apiOptions
 * 文件后缀选择
 *  ☑️ all
 *  ☑️ jsp
 *  ☑️ html
 *  ☑️ js
 *  ☑️ php
 *  ☑️ 无后缀
 *  ☑️ 自定义后缀
 *  |--------------|
 *  |              |
 *  |______________|
 *  |Add|_______|Remove||Clear|
 *
 * ☑️ 自定义api (粘贴api清单)
 * |------------------|
 * |__________________|
 * |__________________|
 * |__________________|
 * |__________________|
 * |__________________|
 * |__________________|
 * path参数标识 (用于替换为随机值, 避免400)
 * |------------------|
 * |__________________|
 * |Add|_______|Remove||Clear|
 */
public class ApiOptions {

    public static JTextField add_suffix; //添加的特征值
    public static JTextField add_pathValue; //添加的path参数标识
    public static JTextArea apiList; //粘贴的api列表
    public static List<JCheckBox> jbs = new ArrayList<>();

    public static Component getOptions(){
        JPanel apiOptions = new JPanel();
        apiOptions.setBorder(new EmptyBorder(5, 0, 50, 0)); //组件间间隙
        BoxLayout pwOptions_boxLayout = new BoxLayout(apiOptions, BoxLayout.X_AXIS);
        apiOptions.setLayout(pwOptions_boxLayout);
        //构造总设置UI
        JPanel options = new JPanel();
        options.setBorder(new EmptyBorder(0, 0, 400, 0)); //组件间间隙
        BoxLayout options_boxLayout = new BoxLayout(options, BoxLayout.Y_AXIS);
        options.setLayout(options_boxLayout);
        // 1.文件类型勾选
        JLabel suffix = new JLabel("文件后缀选择");
        makeJpanel(options, suffix);
        JCheckBox all = new JCheckBox("all");
        makeJpanel(options, all);
        JCheckBox jsp = new JCheckBox("jsp");
        makeJpanel(options, jsp);
        JCheckBox html = new JCheckBox("html");
        makeJpanel(options, html);
        JCheckBox js = new JCheckBox("js");
        makeJpanel(options, js);
        JCheckBox php = new JCheckBox("php");
        makeJpanel(options, php);
        JCheckBox asp = new JCheckBox("asp");
        makeJpanel(options, asp);
        JCheckBox aspx = new JCheckBox("aspx");
        makeJpanel(options, aspx);
        JCheckBox none = new JCheckBox("无后缀");
        makeJpanel(options, none);
        JCheckBox customize = new JCheckBox("自定义后缀");
        makeJpanel(options, customize);
        // 2.自定义后缀的操作
        JList<String> list = new JList<>();
        list.setLayoutOrientation(JList.VERTICAL);
        list.setModel(new AbstractListModel<String>() {
            public int getSize() {
                return CommonStore.CUSTOMIZE_SUFFIX.size();
            }
            public String getElementAt(int i) {
                return CommonStore.CUSTOMIZE_SUFFIX.get(i);
            }
        });
        JScrollPane default_scrollPane = new JScrollPane(list);
        default_scrollPane.setPreferredSize(new Dimension(350, 100));
        default_scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        makeJpanel(options, default_scrollPane);
        // 3.1 添加/删除/清空等的按钮
        JButton add = new JButton("Add");
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String value = ApiOptions.add_suffix.getText();
                ApiGenerator.notInsideAdd(CommonStore.CUSTOMIZE_SUFFIX, value); //无重复再添加
                // JList更新数据必须通过setModel，重新设置数据
                list.setModel(new AbstractListModel<String>() {
                    public int getSize() {
                        return CommonStore.CUSTOMIZE_SUFFIX.size();
                    }
                    public String getElementAt(int i) {
                        return CommonStore.CUSTOMIZE_SUFFIX.get(i);
                    }
                });
                ApiOptions.add_suffix.setText("");
            }
        });
        add_suffix = new JTextField(); //输入框，自定义后缀
        add_suffix.setColumns(10);
        add_suffix.setText(".json");
        JButton romove = new JButton("Remove");
        romove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectValue = list.getSelectedValue();
                CommonStore.CUSTOMIZE_SUFFIX.remove(selectValue);
                // JList更新数据必须通过setModel，重新设置数据
                list.setModel(new AbstractListModel<String>() {
                    public int getSize() {
                        return CommonStore.CUSTOMIZE_SUFFIX.size();
                    }
                    public String getElementAt(int i) {
                        return CommonStore.CUSTOMIZE_SUFFIX.get(i);
                    }
                });
                CommonStore.ALLOW_SUFFIX.remove(selectValue); // 也从允许的后缀列表中删除
            }
        });
        JButton clear = new JButton("Clear");
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CommonStore.ALLOW_SUFFIX.removeAll(CommonStore.CUSTOMIZE_SUFFIX); // 也从允许的后缀列表中删除
                CommonStore.CUSTOMIZE_SUFFIX.clear();
                // JList更新数据必须通过setModel，重新设置数据
                list.setModel(new AbstractListModel<String>() {
                    public int getSize() {
                        return CommonStore.CUSTOMIZE_SUFFIX.size();
                    }
                    public String getElementAt(int i) {
                        return CommonStore.CUSTOMIZE_SUFFIX.get(i);
                    }
                });
            }
        });
        makeJpanel(options, add, add_suffix, romove, clear);

        //自定义api的总UI
        JPanel operate = new JPanel();
        operate.setBorder(new EmptyBorder(0, 0, 100, 0)); //组件间间隙
        BoxLayout operate_boxLayout = new BoxLayout(operate, BoxLayout.Y_AXIS);
        operate.setLayout(operate_boxLayout);
        // 1.勾选自定义
        JCheckBox customize_api = new JCheckBox("自定义api (粘贴api清单)");
        makeJpanel(operate, customize_api);
        // 2.粘贴自定义api清单的JTextArea
        apiList = new JTextArea();
        JScrollPane scrollPane_api = new JScrollPane(apiList);
        scrollPane_api.setPreferredSize(new Dimension(350, 500));
        makeJpanel(operate, scrollPane_api);
        // 3.path参数标识的添加
        JLabel path_flag = new JLabel("path参数标识 (用于替换为随机值, 避免400)");
        makeJpanel(operate, path_flag);
        JList<String> path_param = new JList<>();
        path_param.setLayoutOrientation(JList.VERTICAL);
        path_param.setModel(new AbstractListModel<String>() {
            public int getSize() {
                return CommonStore.CUSTOMIZE_PATH_FLAG.size();
            }
            public String getElementAt(int i) {
                return CommonStore.CUSTOMIZE_PATH_FLAG.get(i);
            }
        });
        JScrollPane path_param_scrollPane = new JScrollPane(path_param);
        path_param_scrollPane.setPreferredSize(new Dimension(350, 100));
        path_param_scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        makeJpanel(operate, path_param_scrollPane);
        // 3.1 添加/删除/清空等的按钮
        JButton add_path = new JButton("Add");
        add_path.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String value = ApiOptions.add_pathValue.getText();
                ApiGenerator.notInsideAdd(CommonStore.CUSTOMIZE_PATH_FLAG, value); //无重复再添加
                // JList更新数据必须通过setModel，重新设置数据
                path_param.setModel(new AbstractListModel<String>() {
                    public int getSize() {
                        return CommonStore.CUSTOMIZE_PATH_FLAG.size();
                    }
                    public String getElementAt(int i) {
                        return CommonStore.CUSTOMIZE_PATH_FLAG.get(i);
                    }
                });
                ApiOptions.add_pathValue.setText("");
            }
        });
        add_pathValue = new JTextField(); //输入框，path参数标识
        add_pathValue.setColumns(10);
        add_pathValue.setText("");
        JButton romove_path = new JButton("Remove");
        romove_path.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectValue = path_param.getSelectedValue();
                CommonStore.CUSTOMIZE_PATH_FLAG.remove(selectValue);
                // JList更新数据必须通过setModel，重新设置数据
                path_param.setModel(new AbstractListModel<String>() {
                    public int getSize() {
                        return CommonStore.CUSTOMIZE_PATH_FLAG.size();
                    }
                    public String getElementAt(int i) {
                        return CommonStore.CUSTOMIZE_PATH_FLAG.get(i);
                    }
                });
            }
        });
        JButton clear_path = new JButton("Clear");
        clear_path.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CommonStore.CUSTOMIZE_PATH_FLAG.clear();
                // JList更新数据必须通过setModel，重新设置数据
                path_param.setModel(new AbstractListModel<String>() {
                    public int getSize() {
                        return CommonStore.CUSTOMIZE_PATH_FLAG.size();
                    }
                    public String getElementAt(int i) {
                        return CommonStore.CUSTOMIZE_PATH_FLAG.get(i);
                    }
                });
            }
        });
        makeJpanel(operate, add_path, add_pathValue, romove_path, clear_path);


        apiOptions.add(options);
        apiOptions.add(operate);

        return new JScrollPane(apiOptions); // 防止宽口小，导致下面的配置无法操作
    }

    public static void makeJpanel(JPanel all, Component... components)
    {
        JPanel jPanel = new JPanel();
        jPanel.setBorder(new EmptyBorder(0, 0, 0, 0)); //组件间间隙
        FlowLayout flowLayout = (FlowLayout) jPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        for (Component component : components) {
            if (component instanceof JCheckBox){
                ((JCheckBox) component).addItemListener(new ApiItemListener());
                jbs.add((JCheckBox) component);
            }
            jPanel.add(component);
        }
        all.add(jPanel);
    }
}

class ApiItemListener  implements ItemListener {
    @Override
    public void itemStateChanged(ItemEvent e) {
        JCheckBox jcb = (JCheckBox) e.getItem();// 将得到的事件强制转化为JCheckBox类
        String key = jcb.getText(); //任务的名称
        if (jcb.isSelected()) { // 判断是否被选择
            // 选中则创建对象，存入检查列表
            jcb.setSelected(true);
            if (key.equalsIgnoreCase("all")){
                CommonStore.ALL_OFF = true;
            }else if (key.equalsIgnoreCase("jsp")){
                CommonStore.ALLOW_SUFFIX.add(".jsp");
            }else if (key.equalsIgnoreCase("html")){
                CommonStore.ALLOW_SUFFIX.add(".html");
            }else if (key.equalsIgnoreCase("js")){
                CommonStore.ALLOW_SUFFIX.add(".js");
            }else if (key.equalsIgnoreCase("php")){
                CommonStore.ALLOW_SUFFIX.add(".php");
            }else if (key.equalsIgnoreCase("asp")){
                CommonStore.ALLOW_SUFFIX.add(".asp");
            }else if (key.equalsIgnoreCase("aspx")){
                CommonStore.ALLOW_SUFFIX.add(".aspx");
            }else if (key.equalsIgnoreCase("无后缀")){
                CommonStore.ALLOW_SUFFIX.add(".none");
                CommonStore.NONE_OFF = true;
            }else if (key.equalsIgnoreCase("自定义后缀")){
                CommonStore.ALLOW_SUFFIX.addAll(CommonStore.CUSTOMIZE_SUFFIX.stream().filter(str -> !CommonStore.ALLOW_SUFFIX.contains(str)).collect(Collectors.toList()));
            }else if (key.equalsIgnoreCase("自定义api (粘贴api清单)")){
                CommonStore.CUSTOMIZE_API = true;
            }
        }else {
            // 去勾选
            jcb.setSelected(false);
            if (key.equalsIgnoreCase("all")){
                CommonStore.ALL_OFF = false;
            }else if (key.equalsIgnoreCase("jsp")){
                CommonStore.ALLOW_SUFFIX.remove(".jsp");
            }else if (key.equalsIgnoreCase("html")){
                CommonStore.ALLOW_SUFFIX.remove(".html");
            }else if (key.equalsIgnoreCase("js")){
                CommonStore.ALLOW_SUFFIX.remove(".js");
            }else if (key.equalsIgnoreCase("php")){
                CommonStore.ALLOW_SUFFIX.remove(".php");
            }else if (key.equalsIgnoreCase("asp")){
                CommonStore.ALLOW_SUFFIX.remove(".asp");
            }else if (key.equalsIgnoreCase("aspx")){
                CommonStore.ALLOW_SUFFIX.remove(".aspx");
            }else if (key.equalsIgnoreCase("无后缀")){
                CommonStore.ALLOW_SUFFIX.remove(".none");
                CommonStore.NONE_OFF = false;
            }else if (key.equalsIgnoreCase("自定义后缀")){
                CommonStore.ALLOW_SUFFIX.removeAll(CommonStore.CUSTOMIZE_SUFFIX);
            }else if (key.equalsIgnoreCase("自定义api (粘贴api清单)")){
                CommonStore.CUSTOMIZE_API = false;
            }
        }
    }
}