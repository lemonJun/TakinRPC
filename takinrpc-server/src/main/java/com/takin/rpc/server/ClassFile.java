package com.takin.rpc.server;

/**
 * 
 * @author Service Platform Architecture Team (spat@58.com)
 * 
 * <a href="http://blog.58.com/spat/">blog</a>
 * <a href="http://www.58.com">website</a>
 * 
 */
public class ClassFile {

    private String clsName;
    private byte[] clsByte;

    public ClassFile(String clsName, byte[] clsByte) {
        this.setClsName(clsName);
        this.setClsByte(clsByte);
    }

    public String getClsName() {
        return clsName;
    }

    public void setClsName(String clsName) {
        this.clsName = clsName;
    }

    public byte[] getClsByte() {
        return clsByte;
    }

    public void setClsByte(byte[] clsByte) {
        this.clsByte = clsByte;
    }
}
