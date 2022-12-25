/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

/**
 *
 * @author Jayanta B.Sen
 */
public class CountLines {
    
    public static int countLines(String str)
    {
        String[] lines = str.split("\r\n|\r|\n");
        return  lines.length;
    }
    
}
