/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Useful;

import conn.dBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Jayanta B. Sen
 */
public class CustomerToCustid {
    
    private static ArrayList<String> csAl = new ArrayList<String>();
    
    public static void main(String args[])
    {
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in CustomerSale: 3
        /* csid, salemid, custid */
        String query="select salemid, custid from CustomerSale";
        System.out.println(query);
        try{
            Statement smt=conn.createStatement();
            ResultSet rs=smt.executeQuery(query);
            while ( rs.next() )
            {
                String x = rs.getString("salemid")+"~"+rs.getString("custid");
                csAl.add(x);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException ex){}
        }
        
        PreparedStatement pstm1 = null;
        // Number of columns in SaleMaster: 31
        /* salemid, billno, billdt, billtime, orderdt, custid, 
        spid, totgrosswt, totnetwt, lessadvwt, metaladvdt, timeofadv, actualwt, 
        ratepergm, totvalue, totmakingcrg, totothercrg, totamount01, grossbillamt, 
        vatper, vatamt, totamount02, lessadvamt, roundoff, netamt, amtpaid, amtdue, 
        isactive, remarks, diamonddiscper, diamonddiscamt */
        String sql1 = "update SaleMaster set custid=? where salemid=?";
        try 
        {
            db=new dBConnection();
            conn=db.setConnection();

            pstm1 = conn.prepareStatement(sql1);
            for(String s : csAl)
            {
                String x[] = s.split("~");
                // x[0]=>salemid, x[1]=>custid
                pstm1.setInt(1, Integer.parseInt(x[1]));
                pstm1.setInt(2, Integer.parseInt(x[0]));
                pstm1.addBatch();
            }
            pstm1.executeBatch();
            
            conn.setAutoCommit(false);
            
            conn.commit();
        } 
        catch (SQLException ex) 
        {
            ex.printStackTrace();
            try {
                conn.rollback();
                return;
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        }
        finally 
        {
            if (pstm1 != null) 
            {
                try {
                    pstm1.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) 
            {
                db=null;
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
}
