package service;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class MQTTsend implements Runnable {

    private int qos = 2;
    private String broker = "tcp://localhost:1883";
    private String clientId = "EDGE";
    MemoryPersistence persistence = new MemoryPersistence();
    private String content,order;
    private String topic;
    private MqttAsyncClient sampleClient;


    public MQTTsend() {
    }

    @Override
    public void run() {
        int never = 1;
        while(never == 1){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Buffer buf = Buffer.getInstance();
            if(buf.bufferEmpty()==false){
                String[] data = buf.bufferpull();
                this.setdata(data[0],data[1]);

                try {
                    sampleClient = new MqttAsyncClient(broker, clientId, persistence);
                    MqttConnectOptions connOpts = new MqttConnectOptions();
                    connOpts.setCleanSession(true);
                    sampleClient.setCallback(new EdgeServer());
                    System.out.println("Connecting to broker: " + broker);
                    sampleClient.connect(connOpts);
                    System.out.println("Connected to Terminal");
                    Thread.sleep(1000);
                    System.out.println("Publishing message: " + order);
                    MqttMessage message = new MqttMessage(content.getBytes());
                    message.setQos(qos);
                    sampleClient.publish(this.topic, message);
                    //System.out.println("Message published");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (MqttPersistenceException e) {
                    e.printStackTrace();
                } catch (MqttSecurityException e) {
                    e.printStackTrace();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setdata(String con, String top){
        clientId = String.join(top);
        this.topic = top;
        if (con == "Execute Eyes Closed Single Danger Level 1"){
            order = con;
            content = "A";}
        else if (con == "Execute Eyes Closed Double Danger Level 2"){
            order = con;
            content = "B";}
        else if (con == "Execute Eyes Open No More Danger"){
            order = con;
            content = "C";}
        else {
            System.out.println("Unknown Order: " + con);
        }
    }
}