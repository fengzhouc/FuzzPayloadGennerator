package com.alumm0x.generator;

import burp.IIntruderAttack;
import burp.IIntruderPayloadGenerator;
import burp.IIntruderPayloadGeneratorFactory;
import com.alumm0x.util.CommonStore;
import com.alumm0x.util.SourceLoader;

import java.io.*;
import java.util.ArrayList;

public class GeneratorFactory implements IIntruderPayloadGeneratorFactory {
    String type = "";

    public GeneratorFactory(String type){
        this.type = type;
    }
    @Override
    public String getGeneratorName() {
        // 获取当前插件的目录路径
        String path = new File(CommonStore.callbacks.getExtensionFilename()).getParent();
        File directory = new File(path + "/FuzzPayloadGenneratorConfig/api");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (type.equalsIgnoreCase("api")){
            // 先加载本地的api字典
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(path + "/FuzzPayloadGenneratorConfig/api/all.oh"));
                String str;
                while ((str = in.readLine()) != null) {
                    CommonStore.ALL_DATA.add(str);
                }
            } catch (IOException ignored) {
                // 本地没有就加载jar里面内置的字典数据
                CommonStore.ALL_DATA = SourceLoader.loadSources("/api/all.oh");
                // 再尝试落地内置字典到本地
                BufferedWriter out = null;
                try {
                    File apiFile = new File(path + "/FuzzPayloadGenneratorConfig/api/all.oh");
                    if (apiFile.createNewFile()){
                        CommonStore.callbacks.printOutput("CreateFile: " + apiFile.getAbsolutePath());
                        CommonStore.ALL_DATA_PATH = apiFile.getAbsolutePath();
                    }
                    out = new BufferedWriter(new FileWriter(apiFile));
                    for (String data :
                            CommonStore.ALL_DATA) {
                        out.write(data);
                        out.newLine();
                    }
                } catch (IOException e) {
                    CommonStore.callbacks.printError("[GeneratorFactory.getGeneratorName]" + e.getMessage());
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
}
