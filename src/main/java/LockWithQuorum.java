import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.quorum.Quorum;
import com.hazelcast.quorum.QuorumService;
import com.hazelcast.quorum.QuorumType;

import java.io.IOException;

public class LockWithQuorum {
    public static void main(String[] args) throws InterruptedException {
        /**
         * This part of code is used for establishing connection between nodes
         */
        Config config = new Config();
//        Config config = new InMemoryXmlConfig("hazelcast.xml");

        NetworkConfig network = config.getNetworkConfig();
        network.setPort(5701).setPortAutoIncrement(false);

        JoinConfig join = network.getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig().setEnabled(true)
                .addMember("172.20.10.7")
                .addMember("172.20.10.6")
                .addMember("172.20.10.2");

        /**
         * This creates node (example of hazelcast)
         */
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);

        QuorumConfig quorumConfig = new QuorumConfig();
        quorumConfig.setName("myQuorum");
        quorumConfig.setSize(2);
        quorumConfig.setEnabled(true);
        //quorumConfig.setType(QuorumType.WRITE);
        config.addQuorumConfig(quorumConfig);

        LockConfig lockConfig = new LockConfig();
        lockConfig.setName("mylock").setQuorumName("myQuorum");
        config.addLockConfig(lockConfig);

        ILock lock = hazelcastInstance.getLock("mylock");

        QuorumService quorumService = hazelcastInstance.getQuorumService();
        Quorum quorum = quorumService.getQuorum("myQuorum");
        System.out.println(quorum.isPresent());


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
            Thread.sleep(5000);
        }
    }
}