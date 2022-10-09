package com.alumm0x.ui;

import com.alumm0x.collect.ReqMessageCollector;
import com.alumm0x.util.CommonStore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class Info {

    public static Component getInfo(){
        JPanel info = new JPanel();
        info.setBorder(new EmptyBorder(5, 5, 50, 5)); //组件间间隙
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        // 获取图片路径
        java.net.URL imgURL = Info.class.getResource("/oh.png");
        assert imgURL != null;
        JLabel img=new JLabel();
        // 构造图片对象
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(imgURL).getImage().getScaledInstance(768, 432, Image.SCALE_SMOOTH));
        img.setIcon(imageIcon);
        img.setBounds(5, 5, imageIcon.getIconWidth(),imageIcon.getIconHeight());
        info.add(img);
        info.add(new JLabel("Global Setting"));
        JCheckBox collect = new JCheckBox("CollectLog");
        collect.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JCheckBox jcb = (JCheckBox) e.getItem();// 将得到的事件强制转化为JCheckBox类
                if (jcb.isSelected()) {// 判断是否被选择
                    CommonStore.ON_OFF = true;
                    ReqMessageCollector.preInit();
                }else {
                    CommonStore.ON_OFF = false;
                }
            }
        });
        info.add(collect);

        info.add(new JLabel("######################################"));
        info.add(new JLabel("By: alumm0x"));
        info.add(new JLabel("Github: https://github.com/fengzhouc/FuzzPayloadGennerator"));

        return info;
    }
}
