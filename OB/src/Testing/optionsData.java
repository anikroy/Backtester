/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing;

import static org.grep4j.core.Grep4j.grep;
import static org.grep4j.core.Grep4j.constantExpression;
import static org.grep4j.core.fluent.Dictionary.on;

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
import org.grep4j.core.model.Profile;
import org.grep4j.core.model.ProfileBuilder;
import org.grep4j.core.result.GrepResults;
import org.jfree.data.xy.OHLCDataItem;
import org.jquantlib.exercise.EuropeanExercise;
import org.jquantlib.instruments.VanillaOption;
import org.unix4j.Unix4j;


/**
 *
 * @author ds
 */

public class optionsData extends Portfolio {         //0   1     2     3     4        5
        //[AAPLM1715100][0][date,open, high, low, adjClose, volume];
        
        public static HashMap dateRef = new HashMap();
        public static BufferedReader data;
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
        public static void runBackTest(String ticker) throws IOException, ParseException{
            Portfolio p = new Portfolio();
            setDates();
            SimpleDateFormat df= new SimpleDateFormat("yyyyMMdd HH:mm:ss",Locale.ENGLISH);
            
            
            try{
                BufferedReader spotIn = new BufferedReader(new FileReader("C:\\Users\\ds\\Desktop\\dataOptions\\taq_20131001_equity_quotes.txt"));
	        spotIn.readLine();
                
               
                
                String inputLine;
                int count = 0;
                
                //String result = Unix4j.from(spotIn).grep(ticker.split("-")[0]).toStringResult();
                
                //System.out.println(result);
                
                while((inputLine=spotIn.readLine())!=null) {
                    
                    String[] values = inputLine.split(",");
                    
                    String underline = values[0];
                    
                    //LinkedHashMap<String, LinkedHashMap<java.util.Date, Double>> spotData
                    if(underline.equals(ticker.split("-")[0])){
                        
                        count= count +1;
                        
                        LinkedHashMap<java.util.Date, Double> newPrices = new LinkedHashMap();
                        double price = (Double.parseDouble(values[3]) + Double.parseDouble(values[4]))/2.0;
                        Date date = df.parse(values[1]+" " +values[2]);
                        
                        if(spotData.containsKey(underline)){
                            //AV,10/01/2013,04:18:20.560,13.06,13.1,22,22,R,P
                            newPrices = spotData.get(underline);
                            newPrices.put(date,price);
                            
                            spotData.put(underline,newPrices);}
                        else{
                            
                            newPrices.put(date,price);
                            spotData.put(underline, newPrices);
                        }
                    }
                 if(count == 20000){break;}
                }
            spotIn.close();
            } catch (Exception e) {
            e.printStackTrace();
            }
            
            
            df= new SimpleDateFormat("MM/dd/yyyy HH:mm:ss",Locale.ENGLISH);
            
            System.out.println("options data now");
            
            try	{
                BufferedReader in = new BufferedReader(new FileReader("C:\\Users\\ds\\Desktop\\dataOptions\\opra_20130301_bar05.txt"));
                in.readLine();
                String inputLine;
                int count = 0;
                
                //String result = Unix4j.from(in).grep("03/01/2013,AAN").toStringResult();
                //System.out.println(result);
                
                while((inputLine=in.readLine())!=null) {
                     
                //AACCQ18135.00
                //AANE181325.00
                //03/01/2013,AAN,EU,AAN,E,18,13,25.00
                    
                    String[] values = inputLine.split(",");
                    String symbol = values[1]+ "-" +values[4]+values[5]+values[6]+values[7];
                    if(ticker.equals(symbol)){
                        count ++;
                    
                        System.out.println(symbol);
                        String date = (values[0]+ " " + values[8]);
                        
                        java.util.Date currentDate = df.parse(date);
                        
                        String open = values[25],
                               high = values[26],
                               //low = (Double.parseDouble(values[14]) + Double.parseDouble(values[15]))/2.0,
                               low = values[27],
                               close = values[28],
                               volume = values[30],
                               adjClose = values[28];
                              
                               
                        String[] gData = {date,open, high, low, adjClose, volume};

                        if(GraphingData.containsKey(symbol)){
                            ArrayList<String[]> temp = GraphingData.get(symbol);
                            temp.add(gData);
                            GraphingData.put(symbol,temp);}
                        else{
                            ArrayList<String[]> temp = new ArrayList();
                            temp.add(gData);
                            GraphingData.put(symbol, temp);
                        }

                        double strike = Double.parseDouble(values[7]);
                        
                        SimpleDateFormat df2= new SimpleDateFormat("MM/dd/yy",Locale.ENGLISH);
                        java.util.Date maturity = df2.parse(dateRef.get(values[4])+ "/" +values[5]+"/"+values[6]);
                        
                        //String type,String underline,double strike, java.uti.date Date
                        //Instrument currentOption = new Instrument("option",values[1],strike,maturity);
                        Instrument currentOption = new Instrument("option", values[4].charAt(0), values[1], strike, Double.parseDouble(values[28]), maturity, currentDate);
                        pricingData.put(symbol, currentOption);
                       
                        VanillaOption Contract = p.Contract(symbol);
                        System.out.println("value of the option is: " + Contract.NPV());
                        System.out.println("the option was trading at: " + close);
                        System.out.println("the Delta is: " + Contract.delta());
                        System.out.println("the Gamma is: " + Contract.gamma());
                        System.out.println("the Vega is: " + Contract.vega());
                       
                        
                    }
                if(count ==79){break;}
            }
            
            System.out.println("The data processing is done");
        
     in.close();
     } catch (Exception e) {
     e.printStackTrace();
     }
            
        }

}