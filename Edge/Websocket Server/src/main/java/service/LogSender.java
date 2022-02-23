package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/*Class and Method to send files to clients*/
@Component
public class LogSender {
    @Autowired
    private SimpMessagingTemplate broker;

    @Autowired
    public LogSender(final SimpMessagingTemplate broker) {
        this.broker = broker;
    }


    @Scheduled(fixedRate = 3000)
    public void run() {
        Buffer buf = Buffer.getInstance();
        //synarthsh poy na elegxei to buffer twn logs empty()
        if(!buf.logsEmpty()){
            LogObj log;
            //synarthsh gia adeiasma enos element toy buffer an not empty
            log = buf.logpull();
            System.out.println("Send log with criticality Level: " + log.getCritlvlS() + " and mac: " + log.getMac()+ " and gps: "+ log.getGps_signal());
            broker.convertAndSend("/topic/Logs", log);
        }
    }
}
