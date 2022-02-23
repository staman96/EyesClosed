package client;

import java.util.*;
import java.io.*; 

public class Training{ 

    private String[] CSVnames;
    private String folder;
    private String outputfileDest;
    private ArrayList<String>[] tempcsv;
    private int filesinFol;
    

    public Training(){
        filesinFol = 0;
        folder = "D:\\programming\\project\\Training_Set\\"; /*baggelhs*/
        outputfileDest = "D:\\programming\\project\\backhaul\\send\\Training_Set.csv"; /*baggelhs*/
        //folder = "C:\\Project data\\Backhaul\\Training_Set\\";    /*stamaths*/
        //outputfileDest = "C:\\Project data\\Backhaul\\Sent\\Training_Set.csv"; /*stamaths*/
        tempcsv = new ArrayList[14];
        for (int i=0; i<14; i++) tempcsv[i] = new ArrayList<>();
    }

    public String training(){
        System.out.println("Proccesing files to make training set.");
        this.filenamesfill();

        /*opening Training_Set.csv to write to, and to put channel headers*/
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(outputfileDest));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
			writer.write("Experiment name,AF3,F7,F3,FC5,T7,P7,O1,O2,P8,T8,FC6,F4,F8,AF4\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        /*Analyzing the csv files to produce the training set file*/
        for (int filecount = 0; filecount<this.filesinFol; filecount++){

            csvToArray(folder + CSVnames[filecount]); /*putting csv file to array os string lists for each channel*/
            double[] featureVector = new double[14];
            for (int channel=0; channel<14;channel++){
                                                        /*putting string's list channel to double array */
                int listsize = tempcsv[channel].size(); /* initializig variables for each channel/loop*/
                double[] dataVector = new double[listsize];
                double entropy;
                
                for (int stod = 0; stod < listsize; stod++){
                    dataVector[stod] = Double.parseDouble(this.tempcsv[channel].get(stod)); /*making string channel list double*/
                }
                entropy = Entropy.calculateEntropy(dataVector);/*calculating entropy for each channel*/
                featureVector[channel] = entropy; /*filling each feature vector*/
            }

                            /* writing each feature vector to file */
            try {
                fillTR_SET(CSVnames[filecount], featureVector,writer);
            } catch (IOException e) {
                e.printStackTrace();
            } 

            for (int i=0; i < 14;i++) tempcsv[i].clear(); /*clearing arraylist for next file to use*/
        }
        try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        System.out.println("Training_Set.csv completed.");
        return outputfileDest; //return file path to send
    }
    

    public void filenamesfill(){
        /*create a file that is really a directory*/
        File Directory = new File(folder);

        /*getting a listing of all files in the directory*/
        String[] filesInDir = Directory.list();

        /*Counting the files in the folder and initializing the names array*/
        this.filesinFol = Directory.list().length;
        this.CSVnames = new String[this.filesinFol];


        /*saving file names on array*/
        for ( int i=0; i<this.filesinFol; i++ ){
            CSVnames[i] = filesInDir[i];
        }
    }

    /*method to fill the Array of string lists with the values of the csv file*/
    public void csvToArray(String fname){
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            br = new BufferedReader(new FileReader(fname));
            br.readLine(); /*skip 1st line*/ 
            while ((line = br.readLine()) != null) {

                /* use comma as separator*/
                String[] value = line.split(cvsSplitBy);
                for (int i=0; i<14;i++){
                    this.tempcsv[i].add(value[i]);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /*Method to fill the training set csv line by line with feature vectors*/
    public void fillTR_SET(String name, double[] featureVector,BufferedWriter writer) throws IOException {

       
        /*Deciding if eyes are opened or close in file name*/
        name = this.openedclosed(name);
        try {
			writer.append(name);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for(int i =0; i<14; i++){ 
            try {
				writer.append("," + (Double.toString(featureVector[i])));/*making double string again*/
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        try {/*print new line at the end of the feaure vector*/
            writer.append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /*method to extract if its eyes opened or closed experiment*/
    public String openedclosed(String name){

        /*Searching for the word Opened in file name to return 
        eyesopened if true or Eyesclosed if false */
        String find = "Opened";
        int nameLength = name.length();
        int openedLength = find.length();
        /*Checks the whole string if there is a match in each case*/
        for (int i = 0; 
             i <= (nameLength - openedLength);
             i++) {
           if (name.regionMatches(i, find, 0, openedLength)) {
              return "Eyes Opened";
           }
        }
        return "Eyes Closed";
    }
}