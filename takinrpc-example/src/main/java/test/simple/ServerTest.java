package test.simple;

import com.takin.rpc.server.RPCServer;

public class ServerTest {

    public static void main(String[] args) {
        try {
            RPCServer server = new RPCServer();
            server.init(new String[] {}, false);
            server.init(new String[] {}, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
