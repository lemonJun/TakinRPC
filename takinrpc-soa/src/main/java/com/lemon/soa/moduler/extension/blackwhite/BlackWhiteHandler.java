package com.lemon.soa.moduler.extension.blackwhite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lemon.soa.Conf;
import com.lemon.soa.common.URL;
import com.lemon.soa.common.utils.StringUtils;

/**
 * URL解析为黑白名单配置
 * <br>
 * [KEY字段]=>[黑/白名单类型:WHITE/BLACK]|[子开关:true/false]|[黑白名单值:VALUE]
 * <br>
 * ip=>WHITE|true|10.24.1.10,ip=>WHITE|true|10.24.1.10
 */
public class BlackWhiteHandler {

    /**
     * 根据URL解析黑白名单配置
     * 
     * @param url
     * @return
     */
    public static Map<String, BlackWhiteEntity> getBlackWhiteEntityMap(URL url) {
        Map<String, BlackWhiteEntity> bwMap = new HashMap<String, BlackWhiteEntity>();
        String blacklistData = url.getModulerParameter(Conf.BLACKWHITE, BlackWhiteConf.BLACKWHITELIST_KEY, "");

        if (StringUtils.isNotBlank(blacklistData)) {
            //根据“,”分割所有参数
            String[] blacklistArray = blacklistData.split(Conf.PARAMS_SEQ);
            if (blacklistArray.length > 0) {
                for (String blacklist : blacklistArray) {//遍历所有参数
                    try {
                        if (StringUtils.isNotBlank(blacklist)) {
                            //根据“=>”分割参数KEY、VALUE
                            String[] array = blacklist.split(Conf.KV_SEQ);
                            if (array.length == 2) {
                                //根据“|”分割多个参数值
                                String[] vals = array[1].split("\\" + Conf.VALS_SEQ);
                                if (vals.length == 3) {
                                    String key = array[0];//一类黑白名单数据
                                    BlackWhiteEntity entity = bwMap.get(key);
                                    if (entity == null) {
                                        entity = new BlackWhiteEntity();
                                    }

                                    boolean blackWhiteEnabled = Boolean.valueOf(vals[1]);
                                    if (BlackWhiteType.BLACK.getVal().equals(vals[0])) {//黑名单收集
                                        List<String> blackData = new ArrayList<String>();
                                        if (blackWhiteEnabled) {//开关打开
                                            entity.setBlackEnabled(blackWhiteEnabled);
                                            if (!entity.getOnlineBlackData().isEmpty()) {
                                                blackData.addAll(entity.getOnlineBlackData());//添加已添加过的在线黑名单		
                                            }
                                            blackData.add(vals[2]);
                                            entity.setOnlineBlackData(blackData);
                                        } else {//开关关闭
                                            if (!entity.getOfflineBlackData().isEmpty()) {
                                                blackData.addAll(entity.getOfflineBlackData());//添加已添加过的离线黑名单		
                                            }
                                            blackData.add(vals[2]);
                                            entity.setOfflineBlackData(blackData);
                                        }
                                    } else if (BlackWhiteType.WHITE.getVal().equals(vals[0])) {//白名单收集
                                        List<String> whiteData = new ArrayList<String>();
                                        if (blackWhiteEnabled) {//开关打开
                                            entity.setWhiteEnabled(blackWhiteEnabled);
                                            if (!entity.getOnlineWhiteData().isEmpty()) {
                                                whiteData.addAll(entity.getOnlineWhiteData());//添加已添加过的在线白名单		
                                            }
                                            whiteData.add(vals[2]);
                                            entity.setOnlineWhiteData(whiteData);
                                        } else {//开关关闭
                                            if (!entity.getOfflineWhiteData().isEmpty()) {
                                                whiteData.addAll(entity.getOfflineWhiteData());//添加已添加过的离线白名单
                                            }
                                            whiteData.add(vals[2]);
                                            entity.setOfflineWhiteData(whiteData);
                                        }
                                    }

                                    bwMap.put(key, entity);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return bwMap;
    }

}
