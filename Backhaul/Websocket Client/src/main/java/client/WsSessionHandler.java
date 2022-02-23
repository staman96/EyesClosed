package client;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import java.sql.*;
import static client.ConvertDateToString.returndate;

import java.lang.reflect.Type;

public class WsSessionHandler extends StompSessionHandlerAdapter {

    String USERNAME = "dbuser";
    String PASSWORD = "dbpassword";
    String CONN = "jdbc:mysql://localhost/project";

    private StompSession ses;
    /*The path of the folder where the files will be saved in */
    String folder = "D:\\programming\\project\\backhaul\\received\\";

    @Override /*After the websocket connection is made stomp subcribes to channel topic to receive files*/
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe("/topic/Logs", this);
        session.send("/app/hello", "{\"name\":\"Edge subscribed\"}".getBytes());
        ses = session;
        System.out.println("New session initialized with id: " + session.getSessionId());
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers,  
    byte[] payload, Throwable exception) {
        exception.printStackTrace();
    }

    @Override /*Makes the payload seen as CSV object*/
    public Type getPayloadType(StompHeaders headers) {
        return LogObj.class;
    }

    @Override /*Method of getting the actual received object*/
    public void handleFrame(StompHeaders headers, Object payload) {
        if (payload == null) System.out.println("___________NULL PAYLOAD___________");
        else {
            LogObj log;
            log = (LogObj) payload;
            System.out.println("Received log with criticality Level: " + log.getCritlvlS() +" and mac: " + log.getMac()+ " and gps: "+ log.getGps_signal());
            /*kaleitai synarthsh poy 8a kanei to log diaforetika string??*/
            /*kaleitai synarthsh gia nagrafei sth bash*/
            Connection con = null;
            System.out.println("Runs");
            try {
                String tsp = returndate();
                con = DriverManager.getConnection(CONN, USERNAME, PASSWORD);
                System.out.println("Connected");
                try (Statement st = con.createStatement()) {
                    String q1 = "insert into android values('" + log.getMac() + "','" + tsp + "','" + log.getGps_signal() + "','" + log.getCritlvlS() + "')";
                    st.executeUpdate(q1);
                } catch (SQLException e) {
                    System.err.print(e);
                }
            } catch (SQLException e) {
                System.err.print(e);
            } finally {
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

   /*Method to send an object*/
    public boolean sendF(CSV payload){
        if (ses == null) return false;
        System.out.println("Sending file...");
        ses.send("/app/CSVc",payload);
        System.out.println(payload.getFilename() + " sent.");
        return true;
    }
}