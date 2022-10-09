package com.alumm0x.ui;

import com.alumm0x.collect.LogEntry;
import com.alumm0x.collect.ReqMessageCollector;
import com.alumm0x.util.CommonStore;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;

public class CollectorOptions {

    public static Component getOptions(){
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

        return splitPane;
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