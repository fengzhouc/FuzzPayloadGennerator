package com.alumm0x.generator;

import burp.IIntruderAttack;
import burp.IIntruderPayloadGenerator;
import burp.IIntruderPayloadGeneratorFactory;
import com.alumm0x.util.CommonStore;

import java.io.*;
import java.util.ArrayList;

public class GeneratorFactory implements IIntruderPayloadGeneratorFactory {
    String type = "";

    public GeneratorFactory(String type){
        this.type = type;
    }
    @Override
    public String getGeneratorName() {
        if (type.equalsIgnoreCase("api")){
            // 内置数据落地本地
            ApiGenerator.preInit();
            return "Fuzz-Api";
        }
        return "Fuzz-Password";
    }

    @Override
    public IIntruderPayloadGenerator createNewInstance(IIntruderAttack attack) {
        if (type.equalsIgnoreCase("api")){
            ApiGenerator apiGenerator = new ApiGenerator();
            CommonStore.API_DATA = new ArrayList<>(); //初始化为空
            // 根据配置初始化数据
            apiGenerator.init();
            return apiGenerator;
        }
        return new PasswordGenerator();
    }

    /**
     * 创建文件目录
     * @param paths
     * @return
     */
    public static File creatFilePath(String...paths){
        // 获取当前插件的目录路径
        String path = new File(CommonStore.callbacks.getExtensionFilename()).getParent();
        File directory = new File(path + "/FuzzPayloadGenneratorConfig/" + String.join("/", paths));
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }
}
