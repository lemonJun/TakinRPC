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
public class ContractInfo {

    private List<SessionBean> sessionBeanList;

    public ContractInfo() {

    }

    public ContractInfo(List<SessionBean> sessionBeanList) {
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
        private String interfaceName;
        private Map<String, String> instanceMap;
        private ClassInfo interfaceClass;

        public SessionBean() {

        }

        public SessionBean(String interfaceName, Map<String, String> instanceMap, ClassInfo interfaceClass) {
            super();
            this.interfaceName = interfaceName;
            this.instanceMap = instanceMap;
            this.interfaceClass = interfaceClass;
        }

        public String getInterfaceName() {
            return interfaceName;
        }

        public void setInterfaceName(String interfaceName) {
            this.interfaceName = interfaceName;
        }

        public void setInstanceMap(Map<String, String> instanceMap) {
            this.instanceMap = instanceMap;
        }

        public Map<String, String> getInstanceMap() {
            return instanceMap;
        }

        public void setInterfaceClass(ClassInfo interfaceClass) {
            this.interfaceClass = interfaceClass;
        }

        public ClassInfo getInterfaceClass() {
            return interfaceClass;
        }
    }
}