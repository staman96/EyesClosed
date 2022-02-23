package service;

import java.io.*;
import java.util.*;

public class TrSetObj{

    private static volatile TrSetObj instance = new TrSetObj();
    private String[] experiment;/*Experiment name*/
    private double[][] trsetArray; /*Feature Vector values*/
    private String fileloc = "/home/vaggelis/Documents/Server/Received/Training_Set.csv";
    private int rowcount;
    private boolean ready;/*ready allazei otan exei etoimastei o trsetArray gia epejergasia sto knn??*/

    private TrSetObj(){
        /*Ayth h class xrhsimopoiei singleton me eager initialization*/
    }

    public static TrSetObj getInstance(){
        return instance;
    }

    public void csvObjTOtrSetObj(CSV obj){
        /*Init*/
        List<String> strlines = obj.getLines();
        this.rowcount = obj.getRowcount();
        trsetArray = new double[this.rowcount][14];
        experiment = new String[this.rowcount];
        String SplitBy = ",";

        System.out.println("Converting Training_Set CSV object to trainset object.");

        for (int row = 1; row < this.rowcount; row++) {

			String[] value = strlines.get(row).split(SplitBy);
            this.experiment[row-1] = value[0];
            for (int column=1; column<15;column++){
                this.trsetArray[row-1][column-1] = Double.parseDouble(value[column]);
            }
		}
    }

    public void trSetObj(){

        /*Checking if the file has arrived*/
        boolean check;
        while ((check = new File(fileloc).exists()) != true);


        System.out.println("Making the training set into an object.");
        this.trSetToArrays(fileloc);

    }

    /*Method that fill arrays with training set's data*/
    public void trSetToArrays(String fname){
        BufferedReader br = null;
        String line = "";
        String SplitBy = ",";
        int row,column;


        try {
            br = new BufferedReader(new FileReader(fname));
            br.readLine(); /*skip 1st line*/ 
            row = 0;
            while ((line = br.readLine()) != null) {

                /* use comma as separator*/
                String[] value = line.split(SplitBy);
                this.experiment[row] = value[0];
                for (column=1; column<15;column++){
                    this.trsetArray[row][column-1] = Double.parseDouble(value[column]);
                }
                row++;
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

    /*method to print training set object*/
    public void printTrSet(){
        for (int row=0; row<36; row++){
            System.out.print(this.experiment[row]);
            for (int col=0; col<14; col++){
                System.out.printf("," + this.trsetArray[row][col]);
            }
            System.out.println();
        }
    }

    /*getters*/
    public String[] getExperiment() {
        return experiment;
    }

    public double[][] getTrsetArray() {
        return trsetArray;
    }

    public int getRowcount() { return rowcount; }

    public String getFileloc() {
        return fileloc;
    }
}
