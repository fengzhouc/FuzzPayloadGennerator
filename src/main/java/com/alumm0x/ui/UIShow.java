package com.alumm0x.ui;

import javax.swing.*;
import java.awt.*;

public class UIShow {
    /**
     * 这里是组装各个部分的UI
     */
    public static Component getUI(){
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.addTab("Info", Info.getInfo());
        jTabbedPane.addTab("pwOptions", PwOptions.getpwOptions());
        contentPane.add(jTabbedPane);
        return contentPane;
    }
}
