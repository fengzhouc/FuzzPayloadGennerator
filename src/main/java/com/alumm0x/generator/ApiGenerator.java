package com.alumm0x.generator;

import burp.IIntruderPayloadGenerator;
import com.alumm0x.ui.ApiOptions;
import com.alumm0x.util.CommonStore;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
     * 根据配置进行初始化
     */
    public void init(){
        // 优先自定义api，
        if (CommonStore.CUSTOMIZE_API){
            // 将JTextArea的数据填充到CommonStore.API_DATA
            String[] api = ApiOptions.apiList.getText().split("\n");
            CommonStore.API_DATA = Arrays.stream(api).map(this::handlerPathParam).collect(Collectors.toList());
        } else { //如果没有开启自定义的，就按照后缀筛数据
            if (CommonStore.ALL_OFF){
                CommonStore.API_DATA = CommonStore.ALL_DATA;
            }else {
                // 遍历字典数据，筛出符合后缀要求的数据，填充到CommonStore.API_DATA
                for (String api : CommonStore.ALL_DATA) {
                    for (String suffix : CommonStore.ALLOW_SUFFIX) {
                        if (api.endsWith(suffix)) {
                            CommonStore.API_DATA.add(api);
                        } else {
                            // 无后缀的数据添加
                            if (CommonStore.NONE_OFF && !api.contains(".")){
                                CommonStore.API_DATA.add(api);
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
                return api.replace(flag, uuid.toString());
            }
        }
        // 没有path参数则返回原数据
        return api;
    }
}
