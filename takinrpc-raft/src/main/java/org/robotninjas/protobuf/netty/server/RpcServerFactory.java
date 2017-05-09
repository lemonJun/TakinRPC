package org.robotninjas.protobuf.netty.server;

import java.net.SocketAddress;

public interface RpcServerFactory {

    RpcServer makeServer(SocketAddress saddr);

}
