package service;

import java.io.IOException;
import java.util.*;

public class Terminal {

    private double xac;
    private double yac;
    private double zac;
    private String mac;
    private double lon;
    private double lat;
    private List<CSV> csv;
    private CSV tempCSV;
    private List<FeatureVector> ftVector;
    private FeatureVector tempFtV;
    private boolean init; /*it is true when the terminal has coords and a csv*/
    private int consecutive_closed;
    private String topic;
    private boolean dangerlvl2;/*true an xtypa o synagermos moy apo allon*/
    private boolean alarm2;/*true an xtypa o synagermos alloy logw emena*/


    public Terminal() {
        xac = 0.0;
        yac = 0.0;
        zac = 0.0;
        lon = 0.0;
        lat = 0.0;
        topic = null;
        init = false;
        consecutive_closed = 0;
        //tempCSV = new CSV();
        csv = new LinkedList<CSV>();
        //tempFtV = new FeatureVector();
        ftVector = new LinkedList<FeatureVector>();
        dangerlvl2 = false;
        alarm2 = false;
    }

    /*SETERS*/

    public void setMac(String mac_addr) {
        this.mac = mac_addr;
    }

    public void setXac(String x_acc) {
        this.xac = Double.parseDouble(x_acc);
    }

    public void setYac(String y_acc) {
        this.yac = Double.parseDouble(y_acc);
    }

    public void setZac(String z_acc) {
        this.zac = Double.parseDouble(z_acc);
    }

    public void addCsv(String csv_str, String name) {
        tempCSV = new CSV();
        tempFtV = new FeatureVector();
        //System.out.println("kalw strtocsv");
        this.tempCSV.StrToCSV(csv_str, name); //string to csv
        //System.out.println("kalw set experiment -->>" + tempCSV.getFilename());
        this.tempFtV.setExperiment(tempCSV.getFilename());  //save the name of experiment in FtVector
        //System.out.println("kalw csv to ftvector");
        this.tempFtV.csvToFtVector(tempCSV); //make the FtVector
        //System.out.println("dhmioyrgw categorization");

        /*edw kalw to knn kai kanw set to category*/
        Categorization cat = new Categorization();

        //System.out.println("kalw set category");
        tempFtV.setCategory(cat.categorize(tempFtV.getFtVector()));
        System.out.println("CATEGORY:  " + tempFtV.getCategory());
        Terminals terms = new Terminals();
        if(tempFtV.getCategory() == 1 && tempFtV.getExperiment().contains("p")){
            terms.upsuccCount();
          System.out.println("SUCCESS");
        }
        else if (tempFtV.getCategory() == 2 && tempFtV.getExperiment().contains("c")){
            terms.upsuccCount();
            System.out.println("SUCCESS");
        }
        else{
            terms.upfailCount();
            System.out.println("FAILURE");
        }
        this.ftVector.add(tempFtV); //add FtVector to list
        this.csv.add(tempCSV);  //add csv to list
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    terms.SuccessRate();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cons_cl_check(tempFtV);
            }
        });
        t1.start();

    }

    public void setLon(String lon) {
        if (lon.contains(".")) {
            System.out.println("longitude received: " + lon);
            this.lon = Double.parseDouble(lon);
        } else {
            System.out.println("longitude received as null");
        }
    }

    public void setLat(String lat) {
        if (lat.contains(".")) {
            System.out.println("latitude received: " + lat);
            this.lat = Double.parseDouble(lat);
        } else {
            System.out.println("latitude received as null");
        }
    }

    public void addFT(FeatureVector ft) {
        this.ftVector.add(ft);
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setDangerlvl2(boolean dangerlvl2) {
        this.dangerlvl2 = dangerlvl2;
    }
    public void setAlarm(boolean alarm){
        this.alarm2 = alarm;
    }

    /*GETERS*/

    public List<CSV> getCsvList() {
        return csv;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getXac() {
        return xac;
    }

    public double getYac() {
        return yac;
    }

    public double getZac() {
        return zac;
    }

    public List<FeatureVector> getFVlist() {
        return ftVector;
    }

    public String getMac() {
        return mac;
    }

    public boolean getInit() {
        this.Initilized();
        return init;
    }

    public String getTopic() {
        return topic;
    }
    public boolean getDangerlvl2(){
        return dangerlvl2;
    }
    public boolean getAlarm(){
        return this.alarm2;
    }
    public int getConsecutive_closed(){
        return this.consecutive_closed;
    }



    /*checks if the terminal has the base elements to work properly*/
    public void Initilized() {
        init = true;
        if (topic == null) init = false;
        if (csv == null) init = false;
        if (lat == 0.0) init = false;
        if (lon == 0.0) init = false;
    }

    /*elegxei kai ananewnei ta consecutive kleista matia*/
    private void cons_cl_check(FeatureVector FT) {
        Terminals term = new Terminals();
        HaversineDistance hd = new HaversineDistance();
        double distance;
        Buffer buf = Buffer.getInstance();
        if (FT.getCategory() == 1){
            if (this.consecutive_closed < 3){
            }
            if (this.consecutive_closed >= 3){
                if(this.dangerlvl2 == false){
                    if (term.getTerminals().size() == 1) {
                        /*push to buffer*/
                        buf.push("Execute Eyes Open No More Danger",this.topic,this.mac,this.lat,this.lon);
                        /*Thread Androidsend1 = new Thread(new MQTTsend("Execute Eyes Open No More Danger", topic));
                        Androidsend1.start();*/
                    }
                    else{
                        for (int i = 0; i < term.getTerminals().size(); i++) {
                            if (term.getTerminals().get(i).getMac() != this.mac) {

                                if (term.getTerminals().get(i).getConsecutive_closed() < 3){
                                    this.alarm2= false;
                                    buf.push("Execute Eyes Open No More Danger",this.topic,this.mac,this.lat,this.lon);
                                    /*Thread Androidsend1 = new Thread(new MQTTsend("Execute Eyes Open No More Danger", topic));
                                    Androidsend1.start();*/
                                    term.getTerminals().get(i).setDangerlvl2(false);
                                    buf.push("Execute Eyes Open No More Danger",term.getTerminals().get(i).getTopic(),term.getTerminals().get(i).getMac(),term.getTerminals().get(i).getLat(),term.getTerminals().get(i).getLon());
                                    /*Thread Androidsend24 = new Thread(new MQTTsend("Execute Eyes Open No More Danger",term.getTerminals().get(i).getTopic() ));
                                    Androidsend24.start();*/
                                }
                                else{
                                    distance = hd.calculateDistance(term.getTerminals().get(i).getLat(), term.getTerminals().get(i).getLon(), this.lat, this.lon);
                                    if (distance < 2) {
                                        this.alarm2 = false;
                                        this.dangerlvl2 = true;
                                        term.getTerminals().get(i).setDangerlvl2(false);
                                        buf.push("Execute Eyes Closed Double Danger Level 2",this.topic,term.getTerminals().get(i).getTopic(),this.mac,term.getTerminals().get(i).getMac(),this.lat,this.lon,term.getTerminals().get(i).getLat(),term.getTerminals().get(i).getLon());
                                        /*Thread Androidsend1 = new Thread(new MQTTsend("Execute Eyes Closed Double Danger Level 2", topic));
                                        Androidsend1.start();
                                        Thread Androidsend2 = new Thread(new MQTTsend("Execute Eyes Closed Double Danger Level 2", term.getTerminals().get(i).getTopic()));
                                        Androidsend2.start();*/
                                    }
                                    else{
                                        this.alarm2 = false;
                                        this.dangerlvl2 = false;
                                        term.getTerminals().get(i).setDangerlvl2(false);
                                        buf.push("Execute Eyes Open No More Danger",this.topic,this.mac,this.lat,this.lon);
                                        /*Thread Androidsend1 = new Thread(new MQTTsend("Execute Eyes Open No More Danger", topic));
                                        Androidsend1.start();*/
                                        buf.push("Execute Eyes Open No More Danger",term.getTerminals().get(i).getTopic(),term.getTerminals().get(i).getMac(),term.getTerminals().get(i).getLat(),term.getTerminals().get(i).getLon());
                                        /*Thread Androidsend = new Thread(new MQTTsend("Execute Eyes Closed Single Danger Level 1", term.getTerminals().get(i).getTopic()));
                                        Androidsend.start();*/
                                    }
                                }
                            }
                        }
                    }
                }
            }
            this.consecutive_closed = 0;
        }
        if (FT.getCategory() == 2){
            this.consecutive_closed++;
            if (this.consecutive_closed == 3){
                if(this.dangerlvl2 == false){
                    if (term.getTerminals().size() == 1){
                        buf.push("Execute Eyes Closed Single Danger Level 1",this.topic,this.mac,this.lat,this.lon);
                        /*Thread Androidsend = new Thread(new MQTTsend("Execute Eyes Closed Single Danger Level 1", topic));
                        Androidsend.start();*/
                    }
                    else{
                        for (int i = 0; i < term.getTerminals().size(); i++) {
                            if (term.getTerminals().get(i).getMac() != this.mac) {
                                distance = hd.calculateDistance(term.getTerminals().get(i).getLat(), term.getTerminals().get(i).getLon(), this.lat, this.lon);
                                if (distance < 2) {
                                    this.alarm2 = true;
                                    term.getTerminals().get(i).setAlarm(true);
                                    term.getTerminals().get(i).setDangerlvl2(true);
                                    buf.push("Execute Eyes Closed Double Danger Level 2",this.topic,term.getTerminals().get(i).getTopic(),this.mac,term.getTerminals().get(i).getMac(),this.lat,this.lon,term.getTerminals().get(i).getLat(),term.getTerminals().get(i).getLon());
                                    /*Thread Androidsend1 = new Thread(new MQTTsend("Execute Eyes Closed Double Danger Level 2", topic));
                                    Androidsend1.start();
                                    Thread Androidsend2 = new Thread(new MQTTsend("Execute Eyes Closed Double Danger Level 2", term.getTerminals().get(i).getTopic()));
                                    Androidsend2.start();*/
                                }
                                else{
                                    buf.push("Execute Eyes Closed Single Danger Level 1",this.topic,this.mac,this.lat,this.lon);
                                    /*Thread Androidsend = new Thread(new MQTTsend("Execute Eyes Closed Single Danger Level 1", topic));
                                    Androidsend.start();*/
                                }
                            }
                        }
                    }
                }
            }
            if (this.consecutive_closed > 3){
                if(this.dangerlvl2 == false){
                    if (term.getTerminals().size() > 1){
                        for (int i = 0; i < term.getTerminals().size(); i++) {
                            if (term.getTerminals().get(i).getMac() != this.mac) {
                                distance = hd.calculateDistance(term.getTerminals().get(i).getLat(), term.getTerminals().get(i).getLon(), this.lat, this.lon);
                                if (distance < 2){
                                     if(term.getTerminals().get(i).getDangerlvl2()== false){
                                        term.getTerminals().get(i).setAlarm(true);
                                        term.getTerminals().get(i).setDangerlvl2(true);
                                        buf.push("Execute Eyes Closed Double Danger Level 2",this.topic,term.getTerminals().get(i).getTopic(),this.mac,term.getTerminals().get(i).getMac(),this.lat,term.getTerminals().get(i).getLat(),this.lon,term.getTerminals().get(i).getLon());
                                        /*Thread Androidsend2 = new Thread(new MQTTsend("Execute Eyes Closed Double Danger Level 2", term.getTerminals().get(i).getTopic()));
                                        Androidsend2.start();*/
                                        this.alarm2 = true;
                                        /*Thread Androidsend10 = new Thread(new MQTTsend("Execute Eyes Closed Double Danger Level 2", topic));
                                        Androidsend10.start();*/
                                    }
                                }
                                else{
                                    if(term.getTerminals().get(i).getDangerlvl2()== true){
                                        this.alarm2 = false;
                                        buf.push("Execute Eyes Closed Single Danger Level 1",this.topic,this.mac,this.lat,this.lon);
                                        /*Thread Androidsend6 = new Thread(new MQTTsend("Execute Eyes Closed Single Danger Level 1", topic));
                                        Androidsend6.start();*/
                                        if(term.getTerminals().get(i).getConsecutive_closed() < 3){
                                            buf.push("Execute Eyes Open No More Danger",term.getTerminals().get(i).getTopic(),term.getTerminals().get(i).getMac(),term.getTerminals().get(i).getLat(),term.getTerminals().get(i).getLon());
                                            /*Thread Androidsend1 = new Thread(new MQTTsend("Execute Eyes Open No More Danger", term.getTerminals().get(i).getTopic()));
                                            Androidsend1.start();*/
                                        }
                                        else{
                                            buf.push("Execute Eyes Closed Single Danger Level 1",term.getTerminals().get(i).getTopic(),term.getTerminals().get(i).getMac(),term.getTerminals().get(i).getLat(),term.getTerminals().get(i).getLon());
                                            /*Thread Androidsend = new Thread(new MQTTsend("Execute Eyes Closed Single Danger Level 1", term.getTerminals().get(i).getTopic()));
                                            Androidsend.start();*/
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
    /*private void cons_cl_check(FeatureVector FT) {
        if (FT.getCategory() == 1) {
            //prepei na mpei if an einai cons > 3 na stelenei mnm gia stop toy synagermoy
            if (consecutive_closed > 1) {
                //Buffer androidBuffer = Buffer.getInstance();
                //androidBuffer.push("Execute Eyes Open No More Danger",this.topic);
                Terminals term = new Terminals();
                for (int i = 0; i < term.getTerminals().size(); i++) { //na to koita3w gia logikh
                   if (term.getTerminals().get(i).getMac() != this.mac && term.getTerminals().get(i).getDangerlvl2() == true) {
                        term.getTerminals().get(i).setDangerlvl2(false);
                        Thread Androidsend1 = new Thread(new MQTTsend("Execute Eyes Open No More Danger", term.getTerminals().get(i).getTopic()));
                        Androidsend1.start();
                    }
                }
                if (this.dangerlvl2 == false) {
                    Thread Androidsend2 = new Thread(new MQTTsend("Execute Eyes Open No More Danger", this.topic));
                    Androidsend2.start();
                }
                //System.out.println("aman!!!");
            }
            this.consecutive_closed = 0;
        }
        if (FT.getCategory() == 2) {
            this.consecutive_closed++;
            if (this.consecutive_closed == 2 && topic != null) {
                Terminals term = new Terminals();
                for (int i = 0; i < term.getTerminals().size(); i++) {
                    if (term.getTerminals().get(i).getMac() != this.mac) {
                        HaversineDistance hd = new HaversineDistance();
                        double distance = hd.calculateDistance(term.getTerminals().get(i).getLat(), term.getTerminals().get(i).getLon(), this.lat, this.lon);
                        System.out.println("DISTANCE: " + distance);
                        if (distance < 1) {
                            term.getTerminals().get(i).setDangerlvl2(true);
                            Thread Androidsend1 = new Thread(new MQTTsend("Execute Eyes Closed Double Danger Level 2", topic));
                            Androidsend1.start();
                            Thread Androidsend2 = new Thread(new MQTTsend("Execute Eyes Closed Double Danger Level 2", term.getTerminals().get(i).getTopic()));
                            Androidsend2.start();
                        } else {
                            Thread Androidsend = new Thread(new MQTTsend("Execute Eyes Closed Single Danger Level 1", topic));
                            Androidsend.start();
                        }
                    } else if (term.getTerminals().size() == 1) {
                        Thread Androidsend = new Thread(new MQTTsend("Execute Eyes Closed Single Danger Level 1", topic));
                        Androidsend.start();
                    }
                }
            }
        }

    }*/
}


