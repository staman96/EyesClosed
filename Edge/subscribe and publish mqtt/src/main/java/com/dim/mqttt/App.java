package com.dim.mqttt;

/**
 * Hello world!
 *
 */
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class App implements MqttCallback {

    public static void main(String[] args) {
        String topicpuba = "B";
        String topicsuba = "A";
        String topicpubb = "D";
        String topicsubb = "C";
        int qos = 2;
        String broker = "tcp://localhost:1883";
        String clientId = "JavaAsyncSample";
        //String content = "A";
        //String content = "B";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttAsyncClient sampleClient = new MqttAsyncClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            sampleClient.setCallback(new App());
            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            Thread.sleep(1000);
            sampleClient.subscribe(topicsuba, qos);
            System.out.println("Subscribed");
            //System.out.println("Publishing message: " + content);
            //MqttMessage message = new MqttMessage(content.getBytes());
            //message.setQos(qos);
            //sampleClient.publish(topicpub, message);
            //System.out.println("Message published");
            sampleClient.subscribe(topicsubb, qos);
            System.out.println("Subscribed");
            //System.out.println("Publishing message: " + content);
            //MqttMessage message2 = new MqttMessage(content.getBytes());
            //message2.setQos(qos);
            //sampleClient.publish(topicpubb, message2);
            //System.out.println("Message published");
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
    }

    public void connectionLost(Throwable arg0) {
        System.err.println("connection lost");

    }

    public void deliveryComplete(IMqttDeliveryToken arg0) {
        System.err.println("delivery complete");
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        //System.out.println("topic: " + topic);
        //System.out.println("message: " + new String(message.getPayload()));
        String msg = message.toString();
        String[] parts = msg.split(" ");
        String part1 = parts[0];
        String part2 = parts[1];
        if (part1.equals("lon")){
            System.out.println("longitude received");
        }
        if (part1.equals("lat")){
            System.out.println("latitude received");
        }
        if (part1.equals("mac")){
            System.out.println("mac address received");
        }
        if (part1.equals("xac")){
            System.out.println("acceleration from x axis received");
        }
        if (part1.equals("yac")){
            System.out.println("acceleration from y axis received");
        }
        if (part1.equals("zac")){
            System.out.println("acceleration from z axis received");
        }
        if (part1.equals("csv")){
            System.out.println("csv received");
        }
        if (part1.equals("csn")){
            System.out.println("csv's name:" + part2);
        }
    }

}