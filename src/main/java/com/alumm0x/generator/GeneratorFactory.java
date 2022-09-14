package com.alumm0x.generator;

import burp.IIntruderAttack;
import burp.IIntruderPayloadGenerator;
import burp.IIntruderPayloadGeneratorFactory;
import com.alumm0x.util.CommonStore;
import com.alumm0x.util.SourceLoader;

import java.util.ArrayList;

public class GeneratorFactory implements IIntruderPayloadGeneratorFactory {
    String type = "";

    public GeneratorFactory(String type){
        this.type = type;
    }
    @Override
    public String getGeneratorName() {
        if (type.equalsIgnoreCase("api")){
            CommonStore.ALL_DATA = SourceLoader.loadSources("/api/all.oh");
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
