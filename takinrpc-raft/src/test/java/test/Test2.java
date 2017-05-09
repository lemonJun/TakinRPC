package test;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.log4j.PropertyConfigurator;
import org.robotninjas.barge.RaftService;
import org.robotninjas.barge.Replica;
import org.robotninjas.barge.StateMachine;

import com.google.common.collect.Lists;

public class Test2 implements StateMachine {

    @Override
    public void applyOperation(@Nonnull ByteBuffer entry) {
        System.out.println(entry.getLong());
    }

    public static void main(String... args) throws Exception {
        PropertyConfigurator.configure("D:/log4j.properties");

        try {
            List<Replica> members = Lists.newArrayList();
            members.add(Replica.fromString("localhost:10000"));
            members.add(Replica.fromString("localhost:10002"));
            File logDir = new File("D:/raft1");
            logDir.mkdir();

            // configure the service
            RaftService raft = RaftService.newBuilder().local(Replica.fromString("localhost:10001")).members(members).logDir(logDir).timeout(300).build(new Test2());

            // start this replica
            raft.startAsync().awaitRunning();

            // let's commit some things
            //            for (int i = 0; i < 10; i++) {
            //                raft.commit(new byte[] { 'O', '_', 'o' });
            //            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
