package service;

//import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


@SpringBootApplication
public class EdgeServer implements MqttCallback {



    private Terminals terminals = new Terminals();
    public static void main(String[] args) {


        Thread WsThread = new Thread(new WebsockThread(args));
        WsThread.start();


        String topicsuba = "A";
        String topicsubb = "C";
        int qos = 2;
        String broker = "tcp://localhost:1883";
        String clientId = "JavaAsyncSample";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttAsyncClient sampleClient = new MqttAsyncClient(broker, clientId, persistence);//////////////
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            sampleClient.setCallback(new EdgeServer());
            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");///////////////////////
            Thread.sleep(1000);
            sampleClient.subscribe(topicsuba, qos);
            System.out.println("Subscribed");
            sampleClient.subscribe(topicsubb, qos);
            System.out.println("Subscribed");

        } catch (Exception me) {
            if (me instanceof MqttException) {
                System.out.println("reason " + ((MqttException) me).getReasonCode());
            }
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
        Thread Androidsender = new Thread(new MQTTsend());
        Androidsender.start();
    }

    public void connectionLost(Throwable arg0) {
        System.err.println("connection lost");

    }

    public void deliveryComplete(IMqttDeliveryToken arg0) {
        System.err.println("delivery complete");
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        int term_index;

        String msg = message.toString();
        //System.out.println("received " + msg);
        String[] parts = msg.split("/");
        String part1 = parts[0];
        String part2 = parts[1];
        String part3 = parts[2];

        term_index = terminals.Terminal_index(part3);


        if (part1.equals("lon")) {
            //System.out.print("longitude received");
            terminals.getTerminals().get(term_index).setLon(part2);
        }
        else if(part1.equals("lat")) {
            //System.out.print("latitude received");
            terminals.getTerminals().get(term_index).setLat(part2);
        }
        else if (part1.equals("xac")) {
            //System.out.println("acceleration from x axis received");
            terminals.getTerminals().get(term_index).setXac(part2);
        }
        else if (part1.equals("yac")) {
            //System.out.println("acceleration from y axis received");
            terminals.getTerminals().get(term_index).setYac(part2);
        }
        else if (part1.equals("zac")) {
            //System.out.println("acceleration from z axis received");
            terminals.getTerminals().get(term_index).setZac(part2);
        }
        else if (part1.equals("top")) {
            System.out.println("topic received: " + part2);
            terminals.getTerminals().get(term_index).setTopic(part2);
        }
        else if (part1.equals("csv")) {
            System.out.println(" ");
            String part4 = parts[3];
            terminals.getTerminals().get(term_index).addCsv(part2, part4);
        }
        else System.out.println("Corrupted message");
    }



}
