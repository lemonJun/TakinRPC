package org.robotninjas.raft;

import com.google.common.base.Objects;
import com.google.common.net.HostAndPort;

import javax.annotation.concurrent.Immutable;

@Immutable
public class ReplicaInfo {

    private final HostAndPort hostAndPort;

    public ReplicaInfo(HostAndPort hostAndPort) {
        this.hostAndPort = hostAndPort;
    }

    public HostAndPort getHostAndPort() {
        return hostAndPort;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o instanceof ReplicaInfo) {
            ReplicaInfo other = (ReplicaInfo) o;
            return Objects.equal(getHostAndPort(), other.getHostAndPort());
        }

        return false;

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(hostAndPort);
    }

    @Override
    public String toString() {
        return hostAndPort.toString();
    }

    public static ReplicaInfo fromString(String info) {
        return new ReplicaInfo(HostAndPort.fromString(info));
    }

}
