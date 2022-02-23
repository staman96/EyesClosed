package service;

import java.lang.Math.*;

public class Categorization {
    private double[][] trset;
    private String[] experiment_names;
    private int rows;
    private int k;
    private double distances[][];
    private double wO,wC;
    private int open_counter;

    public Categorization(){
        /*init*/
        trset = TrSetObj.getInstance().getTrsetArray();
        experiment_names = TrSetObj.getInstance().getExperiment();
        rows = TrSetObj.getInstance().getRowcount();

        k = 5; /*---------SET k -----*/

        distances = new double[rows][2];
        wO = 0.0;
        wC = 0.0;
        open_counter = 0;
        for (int i = 0;i<rows;i++){
            distances[i][1] = 0.0;
        }
    }

    public int categorize(double[] ftvector){
        /*H categorize bazei ta barh stis k kontinoteres apostaseis*/
        this.EUdist(ftvector);
        this.k_extraction();
        for (int i = 0;i<k;i++){
            if(distances[i][0] == 0){
                wO+=(1/distances[i][1]);
                open_counter++;
            }
            else wC+=(1/distances[i][1]);
        }
        if (open_counter*wO>(k-open_counter)*wC) return 1; //eyes open = 1
        else return 2; //eyes closed = 2
    }

    /*eykleidia apostash sta vector*/
    private void EUdist(double[] ftvector){
        //int counter = 0;/*gia debug*/
        //System.out.println(experiment_names.length + "rows: " + rows);
        for (int i = 0;i<experiment_names.length-1;i++){
            //System.out.println(experiment_names[i] + "  " + counter);
            if (experiment_names[i].contains("C")){
                distances[i][0] = 1;
                //System.out.println("MPHKE SE CLOSE");
            }                                           /*!!!!na tsekarw ti paizei me ta onomata!!!!*/
            else if (experiment_names[i].contains("p")){
                distances[i][0] = 0;
                //System.out.println("MPHKE SE OPEN");
            }
            else System.out.println("Unidentified name!");
            for (int j = 0;j<14;j++){
                distances[i][1] += (trset[i][j]-ftvector[j])*(trset[i][j]-ftvector[j]);
            }
            distances[i][1] = Math.sqrt(distances[i][1]);
            //System.out.println(distances[i][1]);
            //counter++;
        }
        //System.out.println("LYSAME TA EYKLEIDIA");
    }

    /*bubble sort the k lowest values to the top*/
    private void k_extraction(){
        for (int i = 0;i<k;i++){
            for (int j=rows-1;j>i+1;j--){
                if (distances[j][1]<distances[j-1][1]){
                    //swap
                    double dtemp = distances[j][1];
                    double Ytemp = distances[j][0];
                    distances[j][1] = distances[j-1][1];
                    distances[j][0] = distances[j-1][0];
                    distances[j-1][1] = dtemp;
                    distances[j-1][0] = Ytemp;
                }
            }
        }
    }

}