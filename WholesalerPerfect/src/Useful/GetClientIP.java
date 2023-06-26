/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Useful;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jayanta B.Sen
 */
public class GetClientIP {
    
    public static void main(String args[])
    {
        InetAddress IP=null;
        try {
            IP = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        System.out.println("IP of my system is := "+IP.getHostAddress());
    }
    
}
