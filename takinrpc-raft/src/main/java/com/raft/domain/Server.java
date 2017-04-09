package com.raft.domain;

/**
 * Created by Administrator on 2016/10/28.
 */
public class Server {
    private String addr;
    private int port;

    public Server(String addr, int port) {
        this.addr = addr;
        this.port = port;
    }

    public String getAddr() {

        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
