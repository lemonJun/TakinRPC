package org.robotninjas.raft;

import static com.google.common.base.Throwables.propagate;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.robotninjas.raft.annotations.LocalReplica;
import org.robotninjas.raft.context.NoLeaderException;
import org.robotninjas.raft.context.RaftContext;
import org.robotninjas.raft.rpc.RaftProto;
import org.robotninjas.raft.rpc.RpcServerAdapter;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.AbstractService;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.google.protobuf.ByteString;
import com.takin.rpc.server.RPCServer;

public class LogService extends AbstractService {

    private final RaftContext context;
    private final ReplicaInfo info;

    private RPCServer server = new RPCServer();

    @Inject
    LogService(@LocalReplica ReplicaInfo info, RaftContext context) {
        this.context = context;
        this.info = info;

    }

    private SocketAddress getAddress(ReplicaInfo info) throws UnknownHostException {
        HostAndPort hostAndPort = info.getHostAndPort();
        InetAddress addr = InetAddress.getByName(hostAndPort.getHostText());
        SocketAddress saddr = new InetSocketAddress(addr, hostAndPort.getPort());
        return saddr;
    }

    @Override
    protected void doStart() {
        try {
            RpcServerAdapter adapter = new RpcServerAdapter(context);
            server.init(new String[] {}, false);

            //            rpcServer.registerService(RaftService.newReflectiveService(adapter));
            //            rpcServer.serve(getAddress(info));

            server.start();
            notifyStarted();
        } catch (Throwable t) {
            notifyFailed(t);
            throw propagate(t);
        }
    }

    @Override
    protected void doStop() {
        server.shutdown();
        notifyStopped();
    }

    public ListenableFuture<Boolean> commitOperation(final byte[] op) {

        Retryer<ListenableFuture<Boolean>> retryer = RetryerBuilder.<ListenableFuture<Boolean>> newBuilder()//
                        .retryIfExceptionOfType(NoLeaderException.class).retryIfExceptionOfType(Exception.class)//
                        .retryIfExceptionOfType(RaftException.class).withWaitStrategy(WaitStrategies.exponentialWait(2, 1, TimeUnit.MINUTES))//
                        .withStopStrategy(StopStrategies.stopAfterAttempt(20)).build();

        try {

            final RaftProto.CommitOperation request = RaftProto.CommitOperation.newBuilder().setOp(ByteString.copyFrom(op)).build();

            return retryer.call(new Callable<ListenableFuture<Boolean>>() {
                @Override
                public ListenableFuture<Boolean> call() throws Exception {
                    return context.commitOperation(request);
                }
            });

        } catch (Exception e) {
            return Futures.immediateFailedFuture(e);
        }

    }

    public void addLogListener(LogListener listener) {

    }

    public void removeLogListener(LogListener listener) {

    }

}
