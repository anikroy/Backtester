/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing;
import org.jquantlib.time.Date;
    
/**
 *
 * @author ds
 */
public class Instrument {
    public String underline; 
    private String type;
    private double strike;
    Date maturity;
    
    //String type,String underline,double strike, java.uti.date Date
    public Instrument(String t,String u,double s,java.util.Date mDate){
        type = t;
        strike = s; 
        underline = u;
        maturity = new Date(mDate);
    }
    
    public boolean isOption(){
        return type.equals("option");
    }
}
