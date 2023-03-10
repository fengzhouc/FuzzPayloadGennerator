package com.alumm0x.ui;

import com.alumm0x.collect.LogEntry;
import com.alumm0x.collect.ReqMessageCollector;
import com.alumm0x.util.CommonStore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class CollectorOptions {

    public static Component getOptions(){
        // 整个UI
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));

        // 设置的UI
        JPanel panel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        // 设置：过滤的UI
        JCheckBox collect = new JCheckBox("On-Off");
        collect.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JCheckBox jcb = (JCheckBox) e.getItem();// 将得到的事件强制转化为JCheckBox类
                if (jcb.isSelected()) {// 判断是否被选择
                    CommonStore.ON_OFF = true;
                    // 开关即可以更新配置
                    ReqMessageCollector.preInit();
                }else {
                    CommonStore.ON_OFF = false;
                }
            }
        });
        panel.add(collect);

        //分割界面
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT); //上下分割

        //上面板，结果面板
        ReqMessageCollector httpListener = (ReqMessageCollector) CommonStore.callbacks.getHttpListeners().stream().filter(ls -> ls instanceof ReqMessageCollector).findFirst().get();
        Table logTable = new Table(httpListener);
        JScrollPane scrollPane = new JScrollPane(logTable); //滚动条
        splitPane.setLeftComponent(scrollPane);

        //下面板，请求响应的面板
        JTabbedPane tabs = new JTabbedPane();
        CommonStore.requestViewer = CommonStore.callbacks.createMessageEditor(httpListener, false);
        CommonStore.responseViewer = CommonStore.callbacks.createMessageEditor(httpListener, false);
        tabs.addTab("Request", CommonStore.requestViewer.getComponent());
        tabs.addTab("Response", CommonStore.responseViewer.getComponent());
        splitPane.setRightComponent(tabs);

        // 组装完整UI
        contentPane.add(panel, BorderLayout.NORTH);
        contentPane.add(splitPane, BorderLayout.CENTER);

        return contentPane;
    }
}

/*
 * 下面是Table的一些方法，主要是结果面板的数据展示，可定制，修改如下数据即可
 * */
class Table extends JTable {
    public Table(TableModel tableModel) {
        super(tableModel);
    }

    @Override
    public void changeSelection(int row, int col, boolean toggle, boolean extend)
    {
        LogEntry logEntry = CommonStore.log.get(row);
        CommonStore.requestViewer.setMessage(logEntry.requestResponse.getRequest(), true);
        CommonStore.responseViewer.setMessage(logEntry.requestResponse.getResponse(), false);
        CommonStore.currentlyDisplayedItem = logEntry.requestResponse;

        super.changeSelection(row, col, toggle, extend);
    }
}