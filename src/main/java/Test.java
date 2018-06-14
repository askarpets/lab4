import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        Config config = new Config();
        NetworkConfig network = config.getNetworkConfig();
        network.setPort(5701).setPortAutoIncrement(false);

        JoinConfig join = network.getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig().setEnabled(true)
                .addMember("172.20.10.7")
                .addMember("172.20.10.6")
                .addMember("172.20.10.2");

//        QuorumConfig quorumConfig = new QuorumConfig();
//        quorumConfig.setName("myQuorum");
//        quorumConfig.setSize(2);
//        quorumConfig.setEnabled(true);
//        //quorumConfig.setType(QuorumType.WRITE);
//        config.addQuorumConfig(quorumConfig);
//
//        LockConfig lockConfig = new LockConfig();
//        lockConfig.setName("myLock");
//        lockConfig.setQuorumName("myQuorum");
//        config.addLockConfig(lockConfig);

        HazelcastInstance hazelcast1 = Hazelcast.newHazelcastInstance(config);
        ILock lock = hazelcast1.getLock("myLock");
        Thread.sleep(9000);

        System.out.println(lock.isLocked());
        lock.forceUnlock();



        while (true) {
            lock.lock();
            System.out.println("Created lock");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Unlocking...");
                lock.unlock();
            }
        }
    }
}