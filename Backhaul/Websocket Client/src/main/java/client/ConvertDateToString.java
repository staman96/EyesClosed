package client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ConvertDateToString {

    public static String returndate(){
        Date date = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Europe/Athens"));


        String strDate = formatter.format(date);
        return strDate;
    }
}
