import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;

import java.io.IOException;

public class DistributedLock {
    public static void main(String[] args) throws InterruptedException {
        /**
         * This part of code is used for establishing connection between nodes
         */
        Config config = new Config();

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


        ILock lock = hazelcastInstance.getLock("lock");


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
