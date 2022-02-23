package service;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/*Class and method to read incoming string messages*/
@Controller
public class GreetingController {
    @MessageMapping("/hello")
    public void greeting(HelloMessage message) throws Exception {
        System.out.println("Received message: " + message.getName());
    }
}
