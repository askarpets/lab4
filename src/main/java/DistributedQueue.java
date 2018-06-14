import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;

public class DistributedQueue {
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

        /**
         * Starting working with Distributed queue
         * ----------------------------------------
         * Putting Items on the Queue
         * One of nodes run this code with commented "Taking Items" part
         */
        System.out.println("Start processing...");

        IQueue<Integer> queueForPutting = hazelcastInstance.getQueue("queue");
        for (int k = 1; k < 100; k++) {
            queueForPutting.put(k);
            System.out.println("Producing: " + k);
            Thread.sleep(1000);
        }
        queueForPutting.put(-1);
        System.out.println("Producer Finished!");

        /**
         * Taking Items off the Queue
         * Other nodes run this code with commented "Putting Items" part
         */
//        IQueue<Integer> queueForTaking = hazelcastInstance.getQueue("queue");
//        while (true) {
//            int item = queueForTaking.take();
//            System.out.println("Consumed: " + item);
//            if (item == -1) {
//                queueForTaking.put(-1);
//                break;
//            }
//            Thread.sleep(5000);
//        }
//        System.out.println("Consumer Finished!");
    }
}
