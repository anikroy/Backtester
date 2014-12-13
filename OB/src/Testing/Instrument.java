/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing;
import org.jquantlib.instruments.Option;
import org.jquantlib.time.Date;
    
/**
 *
 * @author ds
 */
public class Instrument {
    public String underline; 
    private String type;
    private double strike;
    private double lastPrice;
    private org.jquantlib.time.Date maturity;
    private java.util.Date lastPriced;
    private char monthCode; 
    
    public Instrument(String type1,char monthCode1, String spotName,double strike1,double sLastPrice,java.util.Date mDate,java.util.Date lPriced){
        type = type1;
        strike = strike1; 
        underline = spotName;
        maturity = new org.jquantlib.time.Date(mDate);
        lastPriced = lPriced;
        lastPrice = sLastPrice;
        monthCode = monthCode1;
        /**
     *
     * @param type1
     * @param spotName
     * @param lPrice
     * @param lPriced
     */
    }
    
    /**
     *
     * @param type1
     * @param spotName
     * @param lPrice
     * @param lPriced
     */
    public Instrument(String type1,String spotName,double lPrice,java.util.Date lPriced){
        type = type1;
        underline = spotName;
        lastPrice = lPrice;
        lastPriced = lPriced; 
    }
    //String type,String underline,double strike, java.uti.date Date
    public boolean isOption(){
        return type.equals("option");
    }
    
    public String getSpotName(){
        return underline;
    }
    
    public Option.Type getType(){
        if(monthCode<'M'){
            return Option.Type.Call;
        }
        else{return Option.Type.Put;}
    }
    
    public org.jquantlib.time.Date getMaturity(){
        return maturity;
    }
    
    public double getStrike(){
        return strike; 
    }
    
    public java.util.Date lastPriced(){
        return lastPriced;
    }
    
    public double getLastPrice(){
        return lastPrice;
    }
}
