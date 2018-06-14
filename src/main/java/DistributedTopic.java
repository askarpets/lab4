import com.hazelcast.config.*;
import com.hazelcast.core.*;

public class DistributedTopic implements MessageListener<MyString> {
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

        ITopic topic = hazelcastInstance.getTopic("pigsTopic");
        topic.addMessageListener(new DistributedTopic());
        Thread.sleep(10000);
        /**
         * Messages publishing
         */
        //MyString myString = new MyString[10];
        for (int i = 0; i < 10090; i++) {
            //myString[i] = new MyString("Message " + (i + 1));
            topic.publish(new MyString(Integer.toString(i)));
            System.out.println("Published " + (i + 1) + " message");
        }

        System.out.println("Published: " + topic.getLocalTopicStats().getPublishOperationCount());
        System.out.println("Received: " + topic.getLocalTopicStats().getReceiveOperationCount());
    }

    public void onMessage(Message<MyString> message) {
        String myEvent = message.getMessageObject().toString();
        System.out.println("Message received = " + myEvent);
    }
}
