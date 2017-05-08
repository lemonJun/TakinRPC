package org.robotninjas.raft;

import com.google.common.collect.ImmutableList;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.List;

import static com.google.common.io.Files.createTempDir;

public class Raft {

    private static SocketAddress getAddress(ReplicaInfo info) throws UnknownHostException {
        InetAddress addr = InetAddress.getByName(info.getHostAndPort().getHostText());
        int port = info.getHostAndPort().getPort();
        SocketAddress saddr = new InetSocketAddress(addr, port);
        return saddr;
    }

    public static void main(String[] args) throws UnknownHostException, InterruptedException {

        int index = 9;

        ImmutableList.Builder builder = ImmutableList.builder();
        ReplicaInfo info = new ReplicaInfo(HostAndPort.fromParts("localhost", 10000 + index));
        if (index != 1) {
            builder.add(new ReplicaInfo(HostAndPort.fromParts("localhost", 10001)));
        }
        if (index != 2) {
            builder.add(new ReplicaInfo(HostAndPort.fromParts("localhost", 10002)));
        }
        if (index != 3) {
            builder.add(new ReplicaInfo(HostAndPort.fromParts("localhost", 10003)));
        }
        List<ReplicaInfo> servers = builder.build();

        Injector injector = Guice.createInjector(new RaftModule(info, servers, 1000, createTempDir()));
        final LogService raft = injector.getInstance(LogService.class);
        raft.doStart();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                raft.doStop();
            }
        });

        for (;;) {
            Thread.sleep(500);
            ListenableFuture<Boolean> result = raft.commitOperation(new byte[] { 'O', '_', 'o' });
            try {
                result.get();
            } catch (Exception e) {

            }
        }

    }
}
