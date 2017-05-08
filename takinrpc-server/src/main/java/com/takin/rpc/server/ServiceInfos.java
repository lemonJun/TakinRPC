package com.takin.rpc.server;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Service Platform Architecture Team (spat@58.com)
 * 
 * <a href="http://blog.58.com/spat/">blog</a>
 * <a href="http://www.58.com">website</a>
 * 
 */
public class ServiceInfos {

    private List<SessionBean> sessionBeanList;

    public ServiceInfos() {

    }

    public ServiceInfos(List<SessionBean> sessionBeanList) {
        super();
        this.sessionBeanList = sessionBeanList;
    }

    public void setSessionBeanList(List<SessionBean> sessionBeanList) {
        this.sessionBeanList = sessionBeanList;
    }

    public List<SessionBean> getSessionBeanList() {
        return sessionBeanList;
    }

    public static class SessionBean {
        private String defineName;
        private ClassInfo defineClass;
        private ClassInfo implClass;
        private String lookup;
        private boolean defaultimpl = false;

        public SessionBean() {

        }

        public SessionBean(String defineName, Map<String, String> instanceMap, ClassInfo defineClass) {
            super();
            this.defineName = defineName;
            this.defineClass = defineClass;
        }

        public String getDefineName() {
            return defineName;
        }

        public void setDefineName(String defineName) {
            this.defineName = defineName;
        }

        public ClassInfo getDefineClass() {
            return defineClass;
        }

        public void setDefineClass(ClassInfo defineClass) {
            this.defineClass = defineClass;
        }

        public ClassInfo getImplClass() {
            return implClass;
        }

        public void setImplClass(ClassInfo implClass) {
            this.implClass = implClass;
        }

        public String getLookup() {
            return lookup;
        }

        public void setLookup(String lookup) {
            this.lookup = lookup;
        }

        public boolean isDefaultimpl() {
            return defaultimpl;
        }

        public void setDefaultimpl(boolean defaultimpl) {
            this.defaultimpl = defaultimpl;
        }

    }
}