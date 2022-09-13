package com.alumm0x.ui;

import com.alumm0x.util.CommonStore;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class MyItemListener implements ItemListener {

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
