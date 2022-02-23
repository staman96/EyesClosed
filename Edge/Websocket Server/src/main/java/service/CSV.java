package service;

import java.util.*;
import java.io.*;

public class CSV
{ 
	protected List<String> lines;
	protected String filename;
	/*Counters for testing for  future use */
	private static int sent = 0;
	private static int received = 0;
	private int rowcount;


	public CSV()
	{ 
		lines = new LinkedList<String>();
	}

	public void objectify(String path){/*parameter is the exact path of the file..with the name*/

		File file = new File(path);
		filename = file.getName();  /*Extracting the name of the file*/
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));  /*Initiating filereader*/
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String st;
									/*read the whole csv line by line and add each line to list*/
		try {
			while ((st = br.readLine()) != null) {
				lines.add(st);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.rowcount = lines.size();
		sent++;
	} 
	
	public void filefy(String folder) throws IOException {
		/*parameter is the path of the folder in which the file is going to be saved*/
		
		/*Initiating writer and opening file*/
		BufferedWriter writer = new BufferedWriter(new FileWriter(folder + filename ));

		/*Writing lines to file*/
		try {
			writer.write(this.lines.get(0));
		} catch (IOException e) {
			e.printStackTrace();
		}

		for(int i =1; i<lines.size(); i++){ 
            try {
				writer.append("\n" + this.lines.get(i));
			} catch (IOException e) {
				e.printStackTrace();
			}
        } 
    		/**FILES WITH THE SAME NAMES WILL BE OVEREATEN*/
     
    	try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		received++;
	}

	public void StrToCSV(String str,String name){

		this.filename = name.substring(0,10);
		System.out.println(this.filename);
		/*30 columns*/
		String test;
		String SplitBy = ",";
		String[] val = str.split(SplitBy);
		String[] tempstr = new String[14];
		for(int row=0; row<val.length-16; row+=30  ){
			for(int col = 0; col<14; col++){
				//System.out.print(val[row+col] + " ");
				tempstr[col] = val[row+col];
			}
			test = String.join(", ", tempstr);
			//System.out.println(test);
			this.lines.add(test);
		}
		//System.out.println("Str to csv completed");
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public List<String> getLines() {
		return lines;
	}

	@Override
	public String toString() {
		return "CSV.file";
	}

	public String getFilename() {
		return filename;
	}
	
	public int getRowcount() {
		return rowcount;
	}
}
