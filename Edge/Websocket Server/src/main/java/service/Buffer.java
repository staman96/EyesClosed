package service;

import java.util.LinkedList;
import java.util.Queue;

public class Buffer {
    private Queue<String[]> buffer;
    private Queue<LogObj> logs;
    private static volatile Buffer instance = new Buffer();

    private Buffer(){
        buffer = new LinkedList<String[]>();
        logs = new LinkedList<LogObj>();
    }

    public static synchronized Buffer getInstance(){
        return instance;
    }

    public void push(String order, String topic,String mac, Double lat, Double lon){
        /*terminal push*/
        String[] tempS = new String[2];
        tempS[0] = order;
        tempS[1] = topic;
        this.buffer.add(tempS);
        System.out.println("Single Order pushed to buffer");

        /*backhaul push*/
        if (order != "Execute Eyes Open No More Danger"){
            StringBuilder gpsbuild = new StringBuilder().append(lat);
            gpsbuild.append(",");
            gpsbuild.append(lon);
            String gps = gpsbuild.toString();
            LogObj log= new LogObj();
            int critlvl = 1;
            log.dataTolog(mac,gps,critlvl);
            //System.out.println("Received log with criticality LeveL: " + log.getCritlvlS());
            this.logs.add(log);
        }
    }

    public void push(String order, String topic1,String topic2,String mac1, String mac2,Double lat1, Double lon1,Double lat2, Double lon2){
        /*terminal push*/
        String[] tempS1 = new String[2];
        String[] tempS2 = new String[2];
        tempS1[0] = order;
        tempS1[1] = topic1;
        this.buffer.add(tempS1);
        tempS2[0] = order;
        tempS2[1] = topic2;
        this.buffer.add(tempS2);
        System.out.println("Double Order pushed to buffer");

        /*backhaul push*/
        StringBuilder macbuild = new StringBuilder().append(mac1).append("_").append(mac2);
        StringBuilder gpsbuild = new StringBuilder().append(lat1).append(",").append(lon1).append("_").append(lat2).append(",").append(lon2);
        String mac = macbuild.toString();
        String gps = gpsbuild.toString();
        LogObj log= new LogObj();
        int critlvl = 2;
        log.dataTolog(mac,gps,critlvl);
        this.logs.add(log);
    }

    public boolean bufferEmpty(){
        if (this.buffer.isEmpty()) return true;
        else return false;
    }

    public boolean logsEmpty(){
        if (this.logs.isEmpty()) return true;
        else return false;
    }

    /*pull from logs*/
    public LogObj logpull(){
        LogObj log;
        log = logs.remove();
        return log;
    }

    /*pull from buffer*/
    public String[] bufferpull(){
        String[] buf = buffer.remove();
        return buf;
    }


}


