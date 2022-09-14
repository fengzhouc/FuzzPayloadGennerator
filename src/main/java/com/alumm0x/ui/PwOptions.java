package com.alumm0x.ui;


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

public class PwOptions {
    public static JTextField minlen_pw; //最小密码长度
    public static JTextField maxlen_pw; //最大密码长度
    public static JTextField min_relex; //最小复杂度
    public static JTextField max_relex; //最大复杂度
    public static JTextField min_baseData; //基础数据的最小长度，最大长度随maxlen_pw
    public static JTextField add_v; //添加的特征值
    public static JTextField customize_v; //自定义组合
    public static List<JCheckBox> jbs = new ArrayList<>();

    public static Component getOptions(){
        JPanel pwOptions = new JPanel();
        pwOptions.setBorder(new EmptyBorder(5, 0, 50, 0)); //组件间间隙
        BoxLayout pwOptions_boxLayout = new BoxLayout(pwOptions, BoxLayout.X_AXIS);
        pwOptions.setLayout(pwOptions_boxLayout);
        //构造总设置UI
        JPanel options = new JPanel();
        options.setBorder(new EmptyBorder(0, 0, 0, 0)); //组件间间隙
        BoxLayout options_boxLayout = new BoxLayout(options, BoxLayout.Y_AXIS);
        options.setLayout(options_boxLayout);
        // 1.密码长度要求
        JLabel length = new JLabel("密码长度要求: ");
        minlen_pw = new JTextField(); //输入框，最小长度
        minlen_pw.setColumns(3);
        minlen_pw.setText(String.valueOf(CommonStore.MINLE_PW));
        JLabel split = new JLabel("~");
        maxlen_pw = new JTextField(); //输入框，最大长度
        maxlen_pw.setColumns(3);
        maxlen_pw.setText(String.valueOf(CommonStore.MAXLEN_PW));
        makeJpanel(options, length, minlen_pw, split, maxlen_pw);
        // 2.最小复杂度
        JLabel minlength = new JLabel("密码复杂度: ");
        min_relex = new JTextField(); //输入框，最小复杂度
        min_relex.setColumns(3);
        min_relex.setText(String.valueOf(CommonStore.minRelex));
        JLabel split_ = new JLabel("~");
        max_relex = new JTextField(); //输入框，最大复杂度
        max_relex.setColumns(3);
        max_relex.setText(String.valueOf(CommonStore.maxRelex));
        makeJpanel(options, minlength, min_relex, split_, max_relex);
        // 3.密码复杂度勾选
        JLabel intercept = new JLabel("密码复杂度选择");
        makeJpanel(options, intercept);
        JCheckBox Upper = new JCheckBox("大写字母");
        Upper.setSelected(true);
        makeJpanel(options, Upper);
        JCheckBox Lower = new JCheckBox("小写字母");
        Lower.setSelected(true);
        makeJpanel(options, Lower);
        JCheckBox Case = new JCheckBox("字母（不区分大小写,不能与上面的大写/小写共存）");
        makeJpanel(options, Case);
        JCheckBox Num = new JCheckBox("纯数字");
        Num.setSelected(true);
        makeJpanel(options, Num);
        JCheckBox Spec = new JCheckBox("特殊字符");
        Spec.setSelected(true);
        makeJpanel(options, Spec);

        JLabel baseData = new JLabel("基础数据集选择(根据选择的数据集生成密码,别选太多,不然有你等的), MinLen: ");
        min_baseData = new JTextField(); //输入框，基础数据集的最小长度
        min_baseData.setColumns(3);
        min_baseData.setText(String.valueOf(CommonStore.MIN_LEN));
        makeJpanel(options, baseData, min_baseData);
        JCheckBox Upper_ = new JCheckBox("大写字母（字母/大写）-0");
        makeJpanel(options, Upper_);
        JCheckBox Lower_ = new JCheckBox("小写字母（字母/小写）-1");
        makeJpanel(options, Lower_);
        JCheckBox Num_ = new JCheckBox("纯数字（数字）-2");
        Num_.setSelected(true);
        makeJpanel(options, Num_);
        JCheckBox Dup_ = new JCheckBox("重复字母或数字（大写字母3_0/小写字母3_1/数据3_2)");
        makeJpanel(options, Dup_);
        JCheckBox Spec_ = new JCheckBox("特殊字符（特殊字符）-4");
        Spec_.setSelected(true);
        makeJpanel(options, Spec_);
        JCheckBox Keyb_ = new JCheckBox("键位字典（数字/小写字母/特殊字符）-5");
        makeJpanel(options, Keyb_);
        JCheckBox Time_ = new JCheckBox("时间字典（数字）-6");
        makeJpanel(options, Time_);
        JCheckBox FirstUpper_ = new JCheckBox("首字母大写（字母）-7");
        makeJpanel(options, FirstUpper_);
        JCheckBox default_ = new JCheckBox("常见用户特征（字母，如admin/root/guest/tomcat等）-8");
        default_.setSelected(true);
        makeJpanel(options, default_);
        // 3.展示特征的表格
        JList<String> list = new JList<>();
        list.setLayoutOrientation(JList.VERTICAL);
        list.setModel(new AbstractListModel<String>() {
            public int getSize() {
                return CommonStore.DEFAULT_DATA.size();
            }
            public String getElementAt(int i) {
                return CommonStore.DEFAULT_DATA.get(i);
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
                String value = PwOptions.add_v.getText();
                CommonStore.DEFAULT_DATA.add(value);
                // JList更新数据必须通过setModel，重新设置数据
                list.setModel(new AbstractListModel<String>() {
                    public int getSize() {
                        return CommonStore.DEFAULT_DATA.size();
                    }
                    public String getElementAt(int i) {
                        return CommonStore.DEFAULT_DATA.get(i);
                    }
                });
                PwOptions.add_v.setText("");
            }
        });
        add_v = new JTextField(); //输入框，最小长度
        add_v.setColumns(10);
        add_v.setText("");
        JButton romove = new JButton("Remove");
        romove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectValue = list.getSelectedValue();
                CommonStore.DEFAULT_DATA.remove(selectValue);
                // JList更新数据必须通过setModel，重新设置数据
                list.setModel(new AbstractListModel<String>() {
                    public int getSize() {
                        return CommonStore.DEFAULT_DATA.size();
                    }
                    public String getElementAt(int i) {
                        return CommonStore.DEFAULT_DATA.get(i);
                    }
                });
            }
        });
        JButton clear = new JButton("Clear");
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CommonStore.DEFAULT_DATA.clear();
                // JList更新数据必须通过setModel，重新设置数据
                list.setModel(new AbstractListModel<String>() {
                    public int getSize() {
                        return CommonStore.DEFAULT_DATA.size();
                    }
                    public String getElementAt(int i) {
                        return CommonStore.DEFAULT_DATA.get(i);
                    }
                });
            }
        });
        makeJpanel(options, add, add_v, romove, clear);
        // 4.自定义结构的
        JCheckBox customize = new JCheckBox("自定义组合（根据基础数据集后面的数据,以逗号分隔）");
        makeJpanel(options, customize);
        customize_v = new JTextField(); //输入框，最小长度
        customize_v.setColumns(20);
        customize_v.setText("2,1,0");
        makeJpanel(options, customize_v);

        //操作payload的总UI
        JPanel operate = new JPanel();
        operate.setBorder(new EmptyBorder(0, 0, 250, 0)); //组件间间隙
        BoxLayout operate_boxLayout = new BoxLayout(operate, BoxLayout.Y_AXIS);
        operate.setLayout(operate_boxLayout);
        // 展示payload的各部分UI
        // 1.生成密码的按钮
        JButton generate = new JButton("Addgenerate");
        generate.setToolTipText("根据配置生成密码字典, 然后将结果追加到");
        JButton reGenerate = new JButton("Regenerate");
        reGenerate.setToolTipText("根据配置重新生成密码字典, 会将已有的数据清空");
        // 2.状态
        JLabel status = new JLabel("Total: " + CommonStore.PW_DATA.size());
        status.setForeground(new Color(0, 255, 0));
        makeJpanel(operate, generate, reGenerate, status);
        // 3.展示payload的表格
        JList<String> table = new JList<>();
        table.setLayoutOrientation(JList.VERTICAL);
        table.setVisibleRowCount(30); // 设置JList的默认展示多少行
        table.setModel(new AbstractListModel<String>() {
            public int getSize() {
                return CommonStore.PW_DATA.size();
            }
            public String getElementAt(int i) {
                return CommonStore.PW_DATA.get(i);
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(350, 500));
        makeJpanel(operate, scrollPane);
        // 4.保存密码的按钮及清空按钮
        JButton save = new JButton("Save");
        generate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 生成密码
                PasswordGenerator.generate();
                // 设置数量
                status.setText("Total: " + CommonStore.PW_DATA.size());
                status.setForeground(new Color(0, 255, 0));
                // JList更新数据必须通过setModel，重新设置数据
                table.setModel(new AbstractListModel<String>() {
                    public int getSize() {
                        return CommonStore.PW_DATA.size();
                    }
                    public String getElementAt(int i) {
                        return CommonStore.PW_DATA.get(i);
                    }
                });
            }

        });
        reGenerate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 生成密码
                PasswordGenerator.reGenerate();
                // 设置数量
                status.setText("Total: " + CommonStore.PW_DATA.size());
                status.setForeground(new Color(0, 255, 0));
                // JList更新数据必须通过setModel，重新设置数据
                table.setModel(new AbstractListModel<String>() {
                    public int getSize() {
                        return CommonStore.PW_DATA.size();
                    }
                    public String getElementAt(int i) {
                        return CommonStore.PW_DATA.get(i);
                    }
                });
            }
        });
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showSaveDialog(save)==JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    writeFile(file.getPath());
                }
            }
        });
        save.setToolTipText("会将生成的密码字典保存到文件");
        JButton generate_rm = new JButton("Remove");
        generate_rm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectValue = table.getSelectedValue();
                CommonStore.PW_DATA.remove(selectValue);
                // JList更新数据必须通过setModel，重新设置数据
                table.setModel(new AbstractListModel<String>() {
                    public int getSize() {
                        return CommonStore.PW_DATA.size();
                    }
                    public String getElementAt(int i) {
                        return CommonStore.PW_DATA.get(i);
                    }
                });
            }
        });
        JButton generate_clean = new JButton("Clear");
        generate_clean.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CommonStore.PW_DATA.clear();
                // JList更新数据必须通过setModel，重新设置数据
                table.setModel(new AbstractListModel<String>() {
                    public int getSize() {
                        return CommonStore.PW_DATA.size();
                    }
                    public String getElementAt(int i) {
                        return CommonStore.PW_DATA.get(i);
                    }
                });
            }
        });
        makeJpanel(operate, save, generate_rm, generate_clean);
        JLabel save_success = new JLabel(""); //保存成功后显示文件名
        makeJpanel(operate, save_success);

        pwOptions.add(options);
        pwOptions.add(operate);

        return new JScrollPane(pwOptions); // 防止宽口小，导致下面的配置无法操作
    }

    public static void makeJpanel(JPanel all, Component... components)
    {
        JPanel jPanel = new JPanel();
        jPanel.setBorder(new EmptyBorder(0, 0, 0, 0)); //组件间间隙
        FlowLayout flowLayout = (FlowLayout) jPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        for (Component component : components) {
            if (component instanceof JCheckBox){
                ((JCheckBox) component).addItemListener(new MyItemListener());
                jbs.add((JCheckBox) component);
            }
            jPanel.add(component);
        }
        all.add(jPanel);
    }

    //写文件
    private static void writeFile(String savepath){
        FileOutputStream fos= null;
        try {
            fos = new FileOutputStream(savepath);
            for (String textArea:
                 CommonStore.PW_DATA) {
                fos.write((textArea + "\n").getBytes());
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class MyItemListener implements ItemListener {

    public void itemStateChanged(ItemEvent e) {
        JCheckBox jcb = (JCheckBox) e.getItem();// 将得到的事件强制转化为JCheckBox类
        String key = jcb.getText(); //任务的名称
        if (jcb.isSelected()) {// 判断是否被选择
            // 选中则创建对象，存入检查列表
            jcb.setSelected(true);
            // 根据不同清空勾选联动
            if (key.equalsIgnoreCase("大写字母")){
                CommonStore.CHECKCONTAINUPPERCASE_OFF = true;
                for (JCheckBox jb : PwOptions.jbs) {
                    if (jb.getText().equalsIgnoreCase("大写字母（字母/大写）-0")){
                        jb.setSelected(true);
                        CommonStore.UPPER_OFF = true;
                    }
                    // 这两种不能共存
                    if (jb.getText().equalsIgnoreCase("字母（不区分大小写,不能与上面的大写/小写共存）")){
                        jb.setSelected(false);
                        CommonStore.CHECKCONTAINCASE_OFF = false;
                    }
                }
            }else if (key.equalsIgnoreCase("小写字母")){
                CommonStore.CHECKCONTAINLOWERCASE_OFF = true;
                for (JCheckBox jb : PwOptions.jbs) {
                    if (jb.getText().equalsIgnoreCase("小写字母（字母/小写）-1")){
                        jb.setSelected(true);
                        CommonStore.LOWER_OFF = true;
                    }
                    // 这两种不能共存
                    if (jb.getText().equalsIgnoreCase("字母（不区分大小写,不能与上面的大写/小写共存）")){
                        jb.setSelected(false);
                        CommonStore.CHECKCONTAINCASE_OFF = false;
                    }
                }
            }else if (key.equalsIgnoreCase("字母（不区分大小写,不能与上面的大写/小写共存）")){
                CommonStore.CHECKCONTAINCASE_OFF = true;
                for (JCheckBox jb : PwOptions.jbs) {
                    if (jb.getText().equalsIgnoreCase("大写字母（字母/大写）-0")){
                        jb.setSelected(true);
                        CommonStore.UPPER_OFF = true;
                    }
                    if (jb.getText().equalsIgnoreCase("小写字母（字母/小写）-1")){
                        jb.setSelected(true);
                        CommonStore.LOWER_OFF = true;
                    }
                    // 去勾选复杂度：大写字母/小写字母
                    if (jb.getText().equalsIgnoreCase("大写字母")){
                        jb.setSelected(false);
                        CommonStore.CHECKCONTAINUPPERCASE_OFF = false;
                    }
                    if (jb.getText().equalsIgnoreCase("小写字母")){
                        jb.setSelected(false);
                        CommonStore.CHECKCONTAINLOWERCASE_OFF = false;
                    }
                }
            } else if (key.equalsIgnoreCase("纯数字")){
                CommonStore.CHECKCONTAINDIGIT_OFF = true;
                for (JCheckBox jb : PwOptions.jbs) {
                    if (jb.getText().equalsIgnoreCase("纯数字（数字）-2")){
                        jb.setSelected(true);
                        CommonStore.NUMBER_OFF = true;
                    }
                    if (jb.getText().equalsIgnoreCase("时间字典（数字）-6")){
                        jb.setSelected(true);
                        CommonStore.TIME_OFF = true;
                    }
                }
            } else if (key.equalsIgnoreCase("特殊字符")){
                CommonStore.CHECKCONTAINSPECIALCHAR_OFF = true;
                for (JCheckBox jb : PwOptions.jbs) {
                    if (jb.getText().equalsIgnoreCase("特殊字符（特殊字符）-4")){
                        jb.setSelected(true);
                        CommonStore.SPECIAL_OFF = true;
                    }
                }
            } else if (key.equalsIgnoreCase("大写字母（字母/大写）-0")){
                CommonStore.UPPER_OFF = true;
            } else if (key.equalsIgnoreCase("小写字母（字母/小写）-1")){
                CommonStore.LOWER_OFF = true;
            } else if (key.equalsIgnoreCase("纯数字（数字）-2")){
                CommonStore.NUMBER_OFF = true;
            } else if (key.equalsIgnoreCase("重复字母或数字（大写字母3_0/小写字母3_1/数据3_2)")){
                CommonStore.DUPLICATE_OFF = true;
            } else if (key.equalsIgnoreCase("特殊字符（特殊字符）-4")){
                CommonStore.SPECIAL_OFF = true;
            } else if (key.equalsIgnoreCase("键位字典（数字/小写字母/特殊字符）-5")){
                CommonStore.KEYBOARD_OFF = true;
            } else if (key.equalsIgnoreCase("时间字典（数字）-6")){
                CommonStore.TIME_OFF = true;
            } else if (key.equalsIgnoreCase("首字母大写（字母）-7")){
                CommonStore.FIRSTUPPER_OFF = true;
            } else if (key.equalsIgnoreCase("常见用户特征（字母，如admin/root/guest/tomcat等）-8")){
                CommonStore.DEFAULT_OFF = true;
            } else if (key.equalsIgnoreCase("自定义组合（根据基础数据集后面的数据,以逗号分隔）")){
                CommonStore.CUSTOMIZE_OFF = true;
            }
        } else {
            // 去勾选
            jcb.setSelected(false);
            // 根据不同清空勾选联动
            if (key.equalsIgnoreCase("大写字母")){
                CommonStore.CHECKCONTAINUPPERCASE_OFF = false;
            }else if (key.equalsIgnoreCase("小写字母")){
                CommonStore.CHECKCONTAINLOWERCASE_OFF = false;
            }else if (key.equalsIgnoreCase("字母（不区分大小写,不能与上面的大写/小写共存）")){
                CommonStore.CHECKCONTAINCASE_OFF = false;
            } else if (key.equalsIgnoreCase("纯数字")){
                CommonStore.CHECKCONTAINDIGIT_OFF = false;
            } else if (key.equalsIgnoreCase("特殊字符")){
                CommonStore.CHECKCONTAINSPECIALCHAR_OFF = false;
            } else if (key.equalsIgnoreCase("大写字母（字母/大写）-0")){
                CommonStore.UPPER_OFF = false;
            } else if (key.equalsIgnoreCase("小写字母（字母/小写）-1")){
                CommonStore.LOWER_OFF = false;
            } else if (key.equalsIgnoreCase("纯数字（数字）-2")){
                CommonStore.NUMBER_OFF = false;
            } else if (key.equalsIgnoreCase("重复字母或数字（大写字母3_0/小写字母3_1/数据3_2)")){
                CommonStore.DUPLICATE_OFF = false;
            } else if (key.equalsIgnoreCase("特殊字符（特殊字符）-4")){
                CommonStore.SPECIAL_OFF = false;
            } else if (key.equalsIgnoreCase("键位字典（数字/小写字母/特殊字符）-5")){
                CommonStore.KEYBOARD_OFF = false;
            } else if (key.equalsIgnoreCase("时间字典（数字）-6")){
                CommonStore.TIME_OFF = false;
            } else if (key.equalsIgnoreCase("首字母大写（字母）-7")){
                CommonStore.FIRSTUPPER_OFF = false;
            } else if (key.equalsIgnoreCase("常见用户特征（字母，如admin/root/guest/tomcat等）-8")){
                CommonStore.DEFAULT_OFF = false;
            } else if (key.equalsIgnoreCase("自定义组合（根据基础数据集后面的数据,以逗号分隔）")){
                CommonStore.CUSTOMIZE_OFF = false;
            }
        }
    }
}
