package com.alumm0x.collect;

import burp.*;
import com.alumm0x.generator.GeneratorFactory;
import com.alumm0x.util.CommonStore;

import javax.swing.table.AbstractTableModel;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReqMessageCollector extends AbstractTableModel implements IHttpListener, IMessageEditorController {

    public PrintWriter stdout;

    /**
     * 最开始的初始化，一些前置工作的执行，比如
     * 1.创建文件目录
     */
    public static void preInit(){
        // 创建文件目录
        CommonStore.PARENT_PATH = GeneratorFactory.creatFilePath(ReqMessageCollector.class.getName()).getPath();
    }

    // 请求类型黑名单，不采集起信息
    private boolean checkPruffix(String header) {
        String kv = header.split(":")[1].trim();
        for (String pruffix : CommonStore.WHITE_PRUFFIX) {
            if (kv.startsWith(pruffix.trim())){
                return false;
            }
        }
        return true;
    }

    @Override
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
        if (!messageIsRequest && CommonStore.ON_OFF) {
            int row = CommonStore.log.size();
            if (toolFlag == 4 || toolFlag == 8 || toolFlag == 16) {//proxy/spider/scanner
                IResponseInfo responseInfo = CommonStore.helpers.analyzeResponse(messageInfo.getResponse());
                List<String> respheaders = responseInfo.getHeaders();
                for (String s : respheaders) {
                    if (s.toLowerCase(Locale.ROOT).startsWith("Content-Type".toLowerCase(Locale.ROOT))) {
                        if (checkPruffix(s)) {
                            break;
                        } else {
                            return;
                        }
                    }
                } // 如果没有centent-type就默认采集
                IRequestInfo requestInfo = CommonStore.helpers.analyzeRequest(messageInfo);
                URL url = requestInfo.getUrl();
                String host = url.getHost();
                //子域名的数据数组，eg：a.b.c.d.com，a/b/c/a.b.c
                String[] domains = handleHost(host);

                //目录的数据数组
                String path = url.getPath();
                String[] dirs = handlePath(path);

                //文件的数据数组
                //将完整的url加进去，包含查询参数
                String[] files = new String[]{url.getFile()};;

                //参数名的数据数组
                String[] param_list = handleParam(requestInfo);

                //将结果写入文件
                write(domains, dirs, param_list, files);
                //设置面板数据
                logAdd(messageInfo, domains, dirs, param_list, files);
            }
            fireTableRowsInserted(row, row);
        }
    }

    /**
     * 处理host
     * @param host 待处理的host
     * @return 返回子域名数据
     */
    private String[] handleHost(String host){
        String[] domains;
        if (!isIP(host)){
            String[] ha = host.split("\\.");
            //剔除二级域名后的每个节点数据
            String[] han = Arrays.copyOfRange(ha,0, ha.length-2);
            List<String> arrDm = new ArrayList<>(Arrays.asList(han));
            //添加完整子域名串,但是在有多个节点数据的时候
            if (han.length > 1) {
                String ats = arrayToStr(han, ".");
                arrDm.add(ats.substring(0, ats.length() - 1));
            }
            //子域名的数据数组，eg：a.b.c.d.com，a/b/c/a.b.c
            domains = arrDm.toArray(new String[0]);
        }else {
            domains = new String[]{};
        }
        return domains;
    }

    /**
     * 处理参数
     * @param requestInfo 待收集的请求
     * @return 返回收集的参数名
     */
    private String[] handleParam(IRequestInfo requestInfo){
        java.util.List<IParameter> params = requestInfo.getParameters();
        java.util.List<String> pas = new ArrayList<>();
        for (IParameter p :
                params) {
            pas.add(p.getName());
        }
        //参数名的数据数组
        return pas.toArray(new String[0]);
    }

    /**
     * 收集文件路径，不包含查询参数
     * @param path 待处理的url，url.getPath()
     * @return 返回path信息
     */
    private String[] handlePath(String path){
        String[] dirs = path.split("/");
        java.util.List<String> up = Arrays.asList(dirs);
        //up是数组转换的，不能add/remove，所以需要重新new一个ArrayList
        java.util.List<String> arrList = new ArrayList<>(up);
        return arrList.toArray(new String[0]);
    }

    // 添加面板展示数据
    // 已经在列表的不添加
    protected void logAdd(IHttpRequestResponse requestResponse, String[] domains, String[] dirs, String[] param_list, String[] files) {
        boolean inside = false;
        int lastRow = CommonStore.log.size();
        String host = arrayToStr(domains, ",");
        String paths = arrayToStr(dirs, ",");
        String params = arrayToStr(param_list, ",");
        String fs = arrayToStr(files, ",");

        for (LogEntry le : CommonStore.log) {
            if (le.Hosts.equalsIgnoreCase(host)
                    && le.Paths.equalsIgnoreCase(paths)
                    && le.Params.equalsIgnoreCase(params)
                    && le.Files.equals(fs)) {
                inside = true;
                break;
            }
        }
        if (!inside) {
            CommonStore.log.add(new LogEntry(lastRow, CommonStore.callbacks.saveBuffersToTempFiles(requestResponse),
                    host, paths, params, fs));
        }
    }

    /**
     * 判断是否ip，如果为ip则不要
     * @param addr 待检测数据
     * @return 返回布尔
     */
    private boolean isIP(String addr) {
        if ("".equals(addr) || addr.length() < 7 || addr.length() > 15) {
            return false;
        }
        /*
         * 判断IP格式和范围
         */
        String rexp = "^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        return mat.find();

    }

    private void write(String[] domains, String[] paths,String[] params, String[] files){
        String f = CommonStore.PARENT_PATH + "/files.txt";
        String d = CommonStore.PARENT_PATH + "/host.txt";
        String pa = CommonStore.PARENT_PATH + "/params.txt";
        String ps = CommonStore.PARENT_PATH + "/paths.txt";
        // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
        if (domains.length != 0){
            FileWriter writer = null;
            try {
                writer = new FileWriter(d, true);
                for (String s :
                        domains) {
                    if ("".equals(s)){
                        continue;
                    }
                    writer.write(s+"\r");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    assert writer != null;
                    writer.close();
                } catch (IOException ignored) {}
            }
        }
        if (paths.length != 0){
            FileWriter writer = null;
            try {
                writer = new FileWriter(ps, true);
                for (String s :
                        paths) {
                    if ("".equals(s)){
                        continue;
                    }
                    writer.write(s+"\r");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    assert writer != null;
                    writer.close();
                } catch (IOException ignored) {}
            }
        }
        if (params.length != 0){
            FileWriter writer = null;
            try {
                writer = new FileWriter(pa, true);
                for (String s :
                        params) {
                    if ("".equals(s)){
                        continue;
                    }
                    writer.write(s+"\r");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    assert writer != null;
                    writer.close();
                } catch (IOException ignored) {}
            }
        }
        if (null != files && files.length != 0){
            //TODO 分类文件类型
            //js/jsp/php/action/do/asp/aspx
            FileWriter writer = null;
            try {
                writer = new FileWriter(f, true);
                for (String s :
                        files) {
                    if ("".equals(s)){
                        continue;
                    }
                    writer.write(s+"\r");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    assert writer != null;
                    writer.close();
                } catch (IOException ignored) {}
            }
        }

    }

    private String arrayToStr(String[] arr, String splitSTR){
        if (null == arr){
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : arr) {
            stringBuilder.append(s);
            stringBuilder.append(splitSTR);
        }
        return stringBuilder.toString();
    }

    //上面板结果的数量，log是存储检测结果的
    @Override
    public int getRowCount()
    {
        return CommonStore.log.size();
    }
    //结果面板的字段数量
    @Override
    public int getColumnCount()
    {
        return 5;
    }
    //结果面板字段的值
    @Override
    public String getColumnName(int columnIndex)
    {
        switch (columnIndex)
        {
            case 0:
                return "Id";
            case 1:
                return "Host(host.txt)";
            case 2:
                return "Paths(paths.txt)";
            case 3:
                return "Params(params.txt)";
            case 4:
                return "Files(files.txt)";
            default:
                return "";
        }
    }
    //获取数据到面板展示
    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        LogEntry logEntry = CommonStore.log.get(rowIndex);

        switch (columnIndex)
        {
            case 0:
                return logEntry.id;
            case 1:
                return logEntry.Hosts;
            case 2:
                return logEntry.Paths;
            case 3:
                return logEntry.Params;
            case 4:
                return logEntry.Files;
            default:
                return "";
        }
    }

    @Override
    public IHttpService getHttpService() {
        return null;
    }

    @Override
    public byte[] getRequest() {
        return new byte[0];
    }

    @Override
    public byte[] getResponse() {
        return new byte[0];
    }
}

