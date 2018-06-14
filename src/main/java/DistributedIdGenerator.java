import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IdGenerator;

public class DistributedIdGenerator {
    public static void main(String[] args) throws Exception {
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

        IdGenerator idGen = hazelcastInstance.getIdGenerator("newId");
        while (true) {
            Long id = idGen.newId();
            System.out.println("Id: " + id);
            Thread.sleep(1000);
        }
    }
}
