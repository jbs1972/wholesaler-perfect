/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Useful;

import conn.dBConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import query.Query;

/**
 *
 * @author Jayanta B. Sen
 */
public class CustomerMigration {
    
    private static Query q=new Query();
    private static ArrayList<String> custAl = new ArrayList<String>();
    
    public static void main(String[] args) throws IOException
    {
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in SaleMaster: 33
        /* salemid, billno, billdt, billtime, orderdt, custnm, custaddr, custcontact, 
        spid, totgrosswt, totnetwt, lessadvwt, metaladvdt, timeofadv, actualwt, 
        ratepergm, totvalue, totmakingcrg, totothercrg, totamount01, grossbillamt, 
        vatper, vatamt, totamount02, lessadvamt, roundoff, netamt, amtpaid, amtdue, 
        isactive, remarks, diamonddiscper, diamonddiscamt */
        String query="select salemid, custnm, custaddr, custcontact from SaleMaster";
        System.out.println(query);
        try{
            Statement smt=conn.createStatement();
            ResultSet rs=smt.executeQuery(query);
            while ( rs.next() )
            {
                String x = rs.getString("salemid")+"~"+rs.getString("custnm")+"~"
                        + rs.getString("custaddr")+"~"+rs.getString("custcontact");
                custAl.add(x);
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
        PreparedStatement pstm2 = null;
        int custid=q.getMaxId("CustomerMaster", "custid");
        int csid=q.getMaxId("CustomerSale", "csid");
        // Number of columns in CustomerMaster: 6
        /* custid, custnm, custaddr, custcontact, isactive, remarks */
        String sql1 = "insert into CustomerMaster (custid, custnm, custaddr, custcontact,"
                + " isactive, remarks) values (?, ?, ?, ?, 1, 'N/A')";
        // Number of columns in CustomerSale: 3
        /* csid, salemid, custid */
        String sql2 = "insert into CustomerSale (csid, salemid, custid) values (?, ?, ?)";
        try 
        {
            db=new dBConnection();
            conn=db.setConnection();

            pstm1 = conn.prepareStatement(sql1);
            pstm2 = conn.prepareStatement(sql2);
            for(String s : custAl)
            {
                String x[] = s.split("~");
                // Number of columns in CustomerMaster: 6
                /* custid, custnm, custaddr, custcontact, isactive, remarks */
                pstm1.setInt(1, ++custid);
                pstm1.setString(2, x[1]);
                pstm1.setString(3, x[2]);
                pstm1.setString(4, x[3]);
                pstm1.addBatch();
                
                // Number of columns in CustomerSale: 3
                /* csid, salemid, custid */
                pstm2.setInt(1, ++csid);
                pstm2.setInt(2, Integer.parseInt(x[0]));
                pstm2.setInt(3, custid);
                pstm2.addBatch();
            }
            pstm1.executeBatch();
            pstm2.executeBatch();
            
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
            if (pstm2 != null) 
            {
                try {
                    pstm2.close();
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
