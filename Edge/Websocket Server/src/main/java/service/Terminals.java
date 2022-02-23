package service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Terminals {
    private static List<Terminal> terminals = new ArrayList<>();
    private static int failCount = 0;
    private static int succCount = 0;
    private static Double successrate = 0.0;
    //private String loc = "/home/vaggelis/Documents/Server/successrate.txt";
    private String loc = "C:\\Project data\\SuccessRate\\successrate.txt";


    public Terminals(){
    }

    public List<Terminal> getTerminals() {
        return terminals;
    }

    public int Terminal_index(String mac){
        int i;
        for (i = 0; i<this.terminals.size(); i++){
            if (mac.equals(terminals.get(i).getMac())) {
                //System.out.println("terminal index completed(old)");
                return i;
            }
        }
        Terminal ter = new Terminal();
        ter.setMac(mac);
        terminals.add(ter);
        System.out.println("incoming msg from new mac: " + mac);
        return i;
    }

    public int getfailCount() {
        return failCount;
    }

    public int getsuccCount() {
        return succCount;
    }

    public void upfailCount() { this.failCount++;  }

    public void upsuccCount() {
        this.succCount++;
    }

    public Double SuccessRate() throws IOException {
        if(failCount >0 || succCount>0){
            Double sc = Double.valueOf(succCount);
            Double fc = Double.valueOf(failCount);
            this.successrate = sc/(sc+fc);
        }
        Double sr = successrate;
        //System.out.println("Success Rate ====== " + successrate + " sr-->" + sr);

        BufferedWriter writer = new BufferedWriter(new FileWriter(loc));
        writer.write(Double.toString(sr));
        writer.close();
        return sr;
    }
}
