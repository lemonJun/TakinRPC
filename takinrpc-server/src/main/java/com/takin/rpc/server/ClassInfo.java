package com.takin.rpc.server;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

public class ClassInfo<T> {
    private Class<?> cls;
    private List<MethodInfo> methodList;
    private ClassType classType;
    private String lookUP;
    
    public static class MethodInfo {
        private Method method;
        private ParamInfo[] paramInfoAry;

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public void setParamInfoAry(ParamInfo[] paramInfoAry) {
            this.paramInfoAry = paramInfoAry;
        }

        public ParamInfo[] getParamInfoAry() {
            return paramInfoAry;
        }
    }

    public static class ParamInfo {
        private int index;
        private Class<?> cls;
        private Type type;
        private String name;
        private String mapping;

        public ParamInfo() {

        }

        public ParamInfo(int index, Class<?> cls, Type type, String name, String mapping) {
            super();
            this.index = index;
            this.cls = cls;
            this.type = type;
            this.name = name;
            this.mapping = mapping;
        }

        public int getIndex() {
            return index;
        }

        public Type getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setMapping(String mapping) {
            this.mapping = mapping;
        }

        public String getMapping() {
            return mapping;
        }

        public void setCls(Class<?> cls) {
            this.cls = cls;
        }

        public Class<?> getCls() {
            return cls;
        }
    }

    public enum ClassType {
        INTERFACE,

        CLASS
    }

    public Class<?> getCls() {
        return cls;
    }

    public List<MethodInfo> getMethodList() {
        return methodList;
    }

    public void setCls(Class<?> cls) {
        this.cls = cls;
    }

    public void setMethodList(List<MethodInfo> methodList) {
        this.methodList = methodList;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public ClassType getClassType() {
        return classType;
    }

    public void setLookUP(String lookUP) {
        this.lookUP = lookUP;
    }

    public String getLookUP() {
        return lookUP;
    }
}