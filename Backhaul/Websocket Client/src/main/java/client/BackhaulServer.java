package client;


import org.springframework.messaging.converter.MappingJackson2MessageConverter;
//import org.springframework.messaging.simp.stomp.StompSessionHandler;
//import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.sql.*;

import java.util.Scanner;

import static client.ConvertDateToString.returndate;

public class BackhaulServer {
    public static void main(String... argv) throws SQLException {
        
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        //stompClient.setTaskScheduler(new ConcurrentTaskScheduler()); /*used for testing*/




        String url = "ws://192.168.1.8:8080/hello";
        WsSessionHandler sessionHandler = new WsSessionHandler();
        stompClient.connect(url, sessionHandler); /*requesting connection to url using Stomp over Websocket*/




        Training tr = new Training();       /*Making new training object*/
        String trloc = tr.training();       /*Calling the function to make the training set*/
        CSV training_set  = new CSV();      /*Making a new CSV object to send */
        training_set.objectify(trloc);      /*Making the training set a sendable object*/
        while (!sessionHandler.sendF(training_set)); /*Sending the training set to edge server*/


        new Scanner(System.in).nextLine(); //Don't close immediately.
    }
}
