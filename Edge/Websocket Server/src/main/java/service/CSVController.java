package service;

import service.TrSetObj;
import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/*Class and method to read incoming csv messages*/
@Controller
public class CSVController {

    //String folder = "/home/vaggelis/Documents/Server/Received/"; /*baggelhs*/
    String folder = "/C:\\Project data\\Edge\\Received\\"; /*stamaths*/

    @MessageMapping("/CSVc")
    public void csvfile(CSV trset) throws Exception {
        System.out.println("Received file: " + trset.getFilename());
        TrSetObj trSetob = TrSetObj.getInstance();
        System.out.println(trset.filename);
        if (trset.filename.equals("Training_Set.csv")){
            trSetob.csvObjTOtrSetObj(trset);
            trSetob.printTrSet();
        }
        trset.filefy(folder); /*Saving file*/
    }
}
