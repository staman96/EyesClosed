package service;

public class LogObj {

    private String mac;
    private String gps_signal;
    private int critlvlI;
    private String  critlvlS;


    public LogObj(){
        mac = null;
        gps_signal = null;
        critlvlI = -1;

    }

    public void dataTolog(String term_id, String gps,int CL){
        this.mac = term_id;
        this.gps_signal = gps;
        this.critlvlI = CL;
        this.critlvlS = Integer.toString(CL);
    }

    /*GETTERS*/
    public String getMac() {
        return mac;
    }

    public String getCritlvlS() {
        return critlvlS;
    }

    public int getCritlvlI() {
        return critlvlI;
    }

    public String getGps_signal() {
        return gps_signal;
    }
}
