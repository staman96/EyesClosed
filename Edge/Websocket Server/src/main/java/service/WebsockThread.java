package service;

import org.springframework.boot.SpringApplication;

public class WebsockThread implements Runnable {

        public String[] args ;

        public WebsockThread(String[] argst){
            args = argst ;
        }

        @Override
        public void run() {

            SpringApplication.run(EdgeServer.class, args);
            /*try {
                Thread.sleep(2000);
                //LogSender sendlog = new LogSender();
                //sendlog.send("GEia sas!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }

}
