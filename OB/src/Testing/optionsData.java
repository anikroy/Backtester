/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.swing.JPanel;

import org.jfree.data.xy.OHLCDataItem;
import org.jquantlib.exercise.EuropeanExercise;
import org.jquantlib.instruments.VanillaOption;


/**
 *
 * @author ds
 */

public class optionsData extends Portfolio {         //0   1     2     3     4        5
        //[AAPLM1715100][0][date,open, high, low, adjClose, volume];
	final static String fileEqt = "C:/Users/Admin/Desktop/Backtester/cta_20131001_equity_quotes_taq/taq_20131001_equity_quotes.txt",
						fileOpt = "C:/Users/Admin/Desktop/Backtester/opra_20130301_bar05/opra_20130301_bar05.txt";

	public static LinkedHashMap<String, LinkedHashMap<java.util.Date, Double>> spotData = new LinkedHashMap();
    public static LinkedHashMap<String, ArrayList<String[]>> OptionsData = new LinkedHashMap(),
    														 EquityData = new LinkedHashMap();
    
    private static HashMap dateRef = new HashMap();
    private static BufferedReader OptData, EqtData;
    
    protected static boolean init = false;
    
    final static int lines    = 79,
    				 numOfPts = 79;
    
    private static int countO  = 0,
    				   countU  = 0,
    				   oldLenO = 0,
    				   oldLenE = 0;
    //bid,ask,strike,maturity
        
	/**
	 * @param args
	 */
        
    public static void setDates(){
        dateRef.put("A", "01");
        dateRef.put("M", "01");
        dateRef.put("B", "02");
        dateRef.put("N", "02");
        dateRef.put("C", "03");
        dateRef.put("O", "03");
        dateRef.put("D", "04");
        dateRef.put("P", "04");
        dateRef.put("E", "05");
        dateRef.put("Q", "05");
        dateRef.put("F", "06");
        dateRef.put("R", "06");
        dateRef.put("G", "07");
        dateRef.put("S", "07");
        dateRef.put("H", "08");
        dateRef.put("T", "08");
        dateRef.put("I", "09");
        dateRef.put("U", "09");
        dateRef.put("J", "10");
        dateRef.put("V", "10");
        dateRef.put("K", "11");
        dateRef.put("W", "11");
        dateRef.put("L", "12");
        dateRef.put("X", "12");
    }
    
    public static void runBackTest(String OptTicker, String EqtTicker) throws IOException, ParseException{
    	setDates();
    	OptData = new BufferedReader(new FileReader(fileOpt));
    	EqtData = new BufferedReader(new FileReader(fileEqt));
    	Backtester.charts = new Draw();
    	
    	while(oldLenO<numOfPts && oldLenE<numOfPts){
	    	fillOptData(OptTicker); fillUnderData(EqtTicker);
	    	if(OptionsData.size()>0 && EquityData.size()>0){
	    		if(OptionsData.get(OptTicker).size()>oldLenO){
	    			
		    		oldLenO++; draw();
		    	}else if(EquityData.get(EqtTicker).size()>oldLenE){
		    		
		    		oldLenE++; draw();
		    	}
	    	}
    	}	
    	
    	OptData.close(); EqtData.close();
	    System.out.println("The data processing is done");
	    	
    }
    
    private static void fillOptData(String ticker) throws IOException, ParseException{
        String[] values = OptData.readLine().split(",");
        String symbol = values[1]+values[4]+values[5]+values[6]+values[7];
        if(ticker.equals(symbol)){
            countO ++;
            
            String date = (values[0]+ " " + values[8]);
               
            String open     = values[25],
                   high    = values[26],
                   low      = values[27],
                   close    = values[28],
                   volume   = values[30],
                   adjClose = values[28];
            
            String[] gData = {date,open, high, low, adjClose, volume};

            if(OptionsData.containsKey(symbol)){
                ArrayList<String[]> temp = OptionsData.get(symbol);
                temp.add(gData);
                OptionsData.put(symbol,temp);}
            else{
                ArrayList<String[]> temp = new ArrayList();
                temp.add(gData);
                OptionsData.put(symbol, temp);
            }
            System.out.println(OptionsData.size() +" "+ EquityData.size() +" "+ spotData.size());
            if(OptionsData.size()>=1 && EquityData.size()>=1 && spotData.size()>=1){
            	double strike = Double.parseDouble(values[7]);
            	SimpleDateFormat df2= new SimpleDateFormat("MM/dd/yy",Locale.ENGLISH);
                java.util.Date maturity = df2.parse(dateRef.get(values[4])+ "/" +values[5]+"/"+values[6]);
                
                //String type,String underline,double strike, java.uti.date Date
                Instrument currentOption = new Instrument("option", values[4].charAt(0), values[1], strike, Double.parseDouble(values[28]), maturity, df2.parse(date));
                PricingData.put(symbol, currentOption);
                VanillaOption Contract = Portfolio.Contract(symbol);
                System.out.println("value of the option is: " + Contract.NPV());
                System.out.println("the option was trading at: " + close);
                System.out.println("the Delta is: " + Contract.delta());
                System.out.println("the Gamma is: " + Contract.gamma());
                System.out.println("the Vega is: " + Contract.vega());
            }
        }
    }
    
    private static void fillUnderData(String ticker) throws IOException, ParseException{
        String[] values  = EqtData.readLine().split(","); 
        if(values[0].equals(ticker.split("-")[0])){
        	countU ++;
        	
            String symbol = values[0], 
            	   date = (values[1]+ " " + values[2]),
            	   open = values[3],
                   high = values[3],
                   low = values[3],
                   close = values[3],
                   volume = Integer.toString(Integer.parseInt(values[5])), // + Integer.parseInt(values[6])),
                   adjClose = values[3];
            
            
            LinkedHashMap<java.util.Date, Double> newPrices = new LinkedHashMap();
            double price = (Double.parseDouble(values[3]) + Double.parseDouble(values[4]))/2.0;
            SimpleDateFormat df2= new SimpleDateFormat("MM/dd/yy",Locale.ENGLISH);
            Date _date = df2.parse(values[1].substring(0, 4)+"/" +
	 				   values[1].substring(4, 6)+"/" +
	 				   values[1].substring(6, 8)+" " +values[2]);
            
            String[] gData = {date, open, high, low, adjClose, volume};
        
	        if(EquityData.containsKey(symbol)){
	            ArrayList<String[]> temp = EquityData.get(symbol);
	            temp.add(gData);
	            
	            EquityData.remove(symbol);
	            EquityData.put(symbol,temp);
	            
	            newPrices = spotData.get(symbol);
	            newPrices.put(_date,price);
	            
	            spotData.remove(symbol);
                spotData.put("AAPL",newPrices);
	        }
	        else{
	            ArrayList<String[]> temp = new ArrayList();
	            temp.add(gData); EquityData.put(symbol, temp); 
	            newPrices.put(_date,price); spotData.put("AAPL",newPrices);
	        
            }
        }
    }
    
    private static void draw() throws ParseException{
	    if((countO+1)%lines != 0){
	    	if(Backtester.chart==null && OptionsData.size()>0 && EquityData.size()>0 && !init) {
	    		Draw.Price_Chart("AAPLD0513430.00", "AAPL");
	        	Backtester.logs = (JPanel) Backtester.log.Trade_Log();
	    		Backtester.initMainFrame();
	    		init=true;
	    	}
	    	else if(init){
            	Draw.Price_Chart("AAPLD0513430.00", "AAPL");
            	Backtester.mainFrame.repaint();
	    	}
		} 
    }
}