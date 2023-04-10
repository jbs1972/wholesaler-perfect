package Useful;

import conn.dBConnection;
import java.sql.*;
import java.io.*;
import javax.swing.JOptionPane;

public class queryFire
{
    public static void main(String[] args) throws IOException
    {
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
//        String query = "delete from LoginDetails";
//        String query = "delete from ItemMaster";
//        String query = "delete from ItemDetails";
//        String query = "delete from Retailer";
//        String query = "delete from PurchaseMasterV2";
//        String query = "delete from PurchaseSubV2";
//        String query = "delete from PurchaseGSTV2";
//        String query = "delete from PurchasePaymentRegister";
//        String query = "delete from ItemLedger";
//        String query = "delete from SalePaymentRegister";
//        String query = "delete from SaleMasterV2";
        String query = "delete from SaleSubV2";
        System.out.println(query);
        try {
            Statement smt=conn.createStatement();
            smt.executeUpdate(query);
//            ResultSet rs = smt.executeQuery(query);
//            if (rs.next()) {
//                String result = rs.getString("x");
//                System.out.println("Max Count: "+result);
//            }
        }
        catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"queryFire ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException ex){}
        }
    }
}