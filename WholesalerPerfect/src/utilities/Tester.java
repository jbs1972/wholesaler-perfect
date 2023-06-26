/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import conn.dBConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import query.Query;

/**
 *
 * @author Jayanta B. Sen
 */
public class Tester {
    
    private static final Query q=new Query();
    private static BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
    
    private static void foo()
    {
        String a="", x="", y="", z="";
        try
        {
            br.readLine();
            System.out.print(" Give Database Name: ");
            a = br.readLine();
            System.out.print(" Give x: ");
            x = br.readLine();
            System.out.print(" Give y: ");
            y = br.readLine();
            System.out.print(" Give WHERE: ");
            z = br.readLine();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        String query="update "+a+" set "+x+"='"+q.passwordEncript(y)+"'"+z;
        try
        {
            Statement stm=conn.createStatement();
            stm.executeUpdate(query);
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException ex){}
        }
    }
    
    private static void bar()
    {
        String a="", x="", z="";
        try
        {
            br.readLine();
            System.out.print(" Give Database Name: ");
            a = br.readLine();
            System.out.print(" Give x: ");
            x = br.readLine();
            System.out.print(" Give WHERE: ");
            z = br.readLine();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        String query="select "+x+" from "+a+z;
        System.out.println(query);
        try
        {
            Statement stm=conn.createStatement();
            ResultSet rs=stm.executeQuery(query);
            if(rs.next())
            {
                System.out.println(q.passwordDecript(rs.getString(x)));
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException ex){}
        }
    }
    
    public static void main(String args[])
    {
        System.out.println(" 1. AAAAAAAAAA");
        System.out.println(" 2. BBBBBBBBBB");
        System.out.print(" Enter your choice: ");
        char ch=' ';
        try
        {
            ch = (char)System.in.read();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        switch(Character.toUpperCase(ch))
        {
            case '1':foo();
                break;
            case '2':bar();
                break;
            default: System.out.println(" Invalid Choice !!!");
        }
    }
    
}
