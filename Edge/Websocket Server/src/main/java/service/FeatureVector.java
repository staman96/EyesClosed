package service;

import java.io.*;
import java.util.*;

public class FeatureVector {
    private String experiment;/*Experiment name*/
    private double ftVector[];
    private ArrayList<String>[] tempcsv ;
    private int category;/*values swstes einai oi 1 kai 2. To 0 shmainei oti den exei kathgoriopoih8ei akoma*/

    public FeatureVector(){
        category = 0;
        ftVector = new double[14];
        tempcsv = new ArrayList[14];
        for (int i=0; i<14; i++) tempcsv[i] = new ArrayList<>();
    }

    public void csvToFtVector(CSV obj){
        /*first making csv obj to array of string lists for each channel*/
        this.csvToArray(obj);

        //System.out.println("calculating entropy");
        for (int channel=0; channel<14;channel++){
            /*putting string's list channel to double array */
            int listsize = tempcsv[channel].size(); /* initializing variables for each channel/loop*/
            double[] dataVector = new double[listsize];
            double entropy;


            for (int stod = 0; stod < listsize; stod++){
                //System.out.println(this.tempcsv[channel].get(stod));
                dataVector[stod] = Double.parseDouble(this.tempcsv[channel].get(stod)); /*making string channel list double*/
            }
            entropy = Entropy.calculateEntropy(dataVector);/*calculating entropy for each channel*/
            this.ftVector[channel] = entropy; /*filling each feature vector*/
        }
    }

    public void csvToArray(CSV obj) {
        String line = "";
        String cvsSplitBy = ",";

        List<String> csvobj = obj.getLines();
        /*skip 1st line!!!! PROSOXH MPOREI NA MH SYMBAINEI AYTO*/
        for (int i = 1; i < csvobj.size(); i++) {
            line = csvobj.get(i);
            //System.out.println(line);
            String[] value = line.split(cvsSplitBy);
            for (int col=0; col<14;col++){
                this.tempcsv[col].add(value[col]);
            }
        }
        //System.out.println("csvtoarray completed");
    }

    /*SETERS*/
    public void setExperiment(String experiment) {
        this.experiment = experiment;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    /*GETERS*/
    public double[] getFtVector() {
        return ftVector;
    }

    public String getExperiment() {
        return experiment;
    }

    public int getCategory() {
        return category;
    }

}
