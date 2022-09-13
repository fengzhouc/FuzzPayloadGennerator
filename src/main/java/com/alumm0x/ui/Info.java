package com.alumm0x.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

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
        info.add(new JLabel("By: alummox"));
        info.add(new JLabel("Github: https://github.com/fengzhouc/FuzzPayloadGennerator"));

        return info;
    }
}
