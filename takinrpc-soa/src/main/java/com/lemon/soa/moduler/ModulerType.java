package com.lemon.soa.moduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum ModulerType {

    /**
     * 优雅停机
     */
    GraceStop,
    /**
     * 黑白名单
     */
    BlackWhite,
    /**
     * 管道缩放
     */
    PipeScaling,
    /**
     * 流量控制
     */
    FlowRate,
    /**
     * 服务降级
     */
    Degrade,
    /**
     * 幂等模块
     */
    Idempotent,
    /**
     * 回声探测
     */
    EchoSound,
    /**
     * 容错内核
     */
    Neure;

    Integer no;
    Class<?> clazz;
    static List<ModulerType> modulerTypes = new ArrayList<ModulerType>();

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public static List<ModulerType> getModulerTypes() {
        if (modulerTypes.isEmpty()) {
            for (ModulerType modulerType : values()) {
                modulerTypes.add(modulerType);
            }

            Collections.sort(modulerTypes, new Comparator<ModulerType>() {
                public int compare(ModulerType arg0, ModulerType arg1) {
                    return arg0.getNo().compareTo(arg1.getNo());
                }
            });
        }

        return modulerTypes;
    }

}
