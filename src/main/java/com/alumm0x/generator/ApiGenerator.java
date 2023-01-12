package com.alumm0x.generator;

import burp.IIntruderPayloadGenerator;
import com.alumm0x.ui.ApiOptions;
import com.alumm0x.util.CommonStore;
import com.alumm0x.util.PayloadBuildler;
import com.alumm0x.util.SourceLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ApiGenerator implements IIntruderPayloadGenerator {
    private int payloadIndex = 0;
    @Override
    public boolean hasMorePayloads() {
        return payloadIndex < CommonStore.API_DATA.size();
    }

    @Override
    public byte[] getNextPayload(byte[] baseValue) {
        byte[] pl = CommonStore.API_DATA.get(payloadIndex).getBytes(StandardCharsets.UTF_8);
        payloadIndex++;
        return pl;
    }

    @Override
    public void reset() {
        this.payloadIndex = 0;
    }

    /**
     * 最开始的初始化，一些前置工作的执行，比如
     * 1.创建文件夹/文件
     * 2.落地内置数据到本地
     */
    public static void preInit(){
        // 创建文件夹
        File file = GeneratorFactory.creatFilePath(ApiGenerator.class.getName());
        String path = file.getPath();
        // 先加载本地的api字典
        BufferedReader in = null;
        BufferedReader whiteconfig = null;
        try {
            in = new BufferedReader(new FileReader(path + "/all.oh"));
            String str;
            while ((str = in.readLine()) != null) {
                CommonStore.ALL_DATA.add(str);
            }
            whiteconfig = new BufferedReader(new FileReader(path + "/whitepruffix.config"));
            String wstr;
            while ((wstr = whiteconfig.readLine()) != null) {
                CommonStore.WHITE_PRUFFIX.add(wstr);
            }
        } catch (IOException ignored) {
            // 本地没有就加载jar里面内置的字典数据
            CommonStore.ALL_DATA = SourceLoader.loadSources("/api/all.oh");
            CommonStore.WHITE_PRUFFIX = SourceLoader.loadSources("/api/whitepruffix.config");
            // 再尝试落地内置字典到本地
            BufferedWriter out = null;
            try {
                File apiFile = new File(path + "/all.oh");
                if (apiFile.createNewFile()){
                    CommonStore.callbacks.printOutput("CreateFile: " + apiFile.getAbsolutePath());
                }
                out = new BufferedWriter(new FileWriter(apiFile));
                for (String data :
                        CommonStore.ALL_DATA) {
                    out.write(data);
                    out.newLine();
                }

                File whiteConfig = new File(path + "/whitepruffix.config");
                if (whiteConfig.createNewFile()){
                    CommonStore.callbacks.printOutput("CreateFile: " + whiteConfig.getAbsolutePath());
                }
                out = new BufferedWriter(new FileWriter(whiteConfig));
                for (String data :
                        CommonStore.WHITE_PRUFFIX) {
                    out.write(data);
                    out.newLine();
                }
            } catch (IOException e) {
                CommonStore.callbacks.printError("[ApiGenerator.preInit]" + e.getMessage());
            }finally {
                if (out != null) {
                    try {
                        out.flush();
                        out.close();
                    } catch (IOException ignored1) {
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ignored1) {
                    }
                }
            }
        }
        CommonStore.ALL_DATA_PATH = path + "/all.oh"; //将落地的字典文件路径保存起来
        CommonStore.WHITE_PRUFFIX_PATH = path + "/whitepruffix.config"; //将落地的字典文件路径保存起来
    }

    /**
     * 根据配置进行初始化数据
     */
    public void init(){
        // 优先自定义api，
        if (CommonStore.CUSTOMIZE_API){
            // 将JTextArea的数据填充到CommonStore.API_DATA
            String[] api = ApiOptions.apiList.getText().split("\n");
            CommonStore.API_DATA = Arrays.stream(api).map(this::handlerPathParam).collect(Collectors.toList());
        } else { //如果没有开启自定义的，就按照后缀筛数据
            if (CommonStore.ALL_OFF){
                CommonStore.API_DATA = PayloadBuildler.getLocalData(CommonStore.ALL_DATA_PATH);
            }else {
                // 遍历字典数据，筛出符合后缀要求的数据，填充到CommonStore.API_DATA
                for (String api : CommonStore.ALL_DATA) {
                    for (String suffix : CommonStore.ALLOW_SUFFIX) {
                        if (api.endsWith(suffix)) {
                            notInsideAdd(CommonStore.API_DATA, api); //无重复再添加
                        } else {
                            // 无后缀的数据添加
                            if (CommonStore.NONE_OFF && !api.contains(".")){
                                notInsideAdd(CommonStore.API_DATA, api); //无重复再添加
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 处理自定义api中的path参数
     * @param api
     * @return
     */
    private String handlerPathParam(String api){
        for (String flag : CommonStore.CUSTOMIZE_PATH_FLAG) {
            if (api.contains(flag)){
                UUID uuid = UUID.randomUUID();
                return api.replaceAll(flag, uuid.toString());
            }
        }
        // 没有path参数则返回原数据
        return api;
    }

    /**
     * 检查是否存在，不存在再添加
     * @param list 待添加数据的集合
     * @param add 添加的数据
     */
    public static void notInsideAdd(List<String> list, String add){
        if (!list.contains(add)){
            list.add(add);
        }
    }
}
