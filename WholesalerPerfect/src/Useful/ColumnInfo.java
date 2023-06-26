package Useful;

import conn.dBConnection;
import java.sql.*;
import java.io.*;
import java.util.ArrayList;

public class ColumnInfo{
    public static void main(String[] args) throws IOException
    {
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        try{
            String tableName="SalePayment";
            String query="select * from "+tableName;
            Statement smt=conn.createStatement();
            ResultSet rs=smt.executeQuery(query);
            ResultSetMetaData rsmd=rs.getMetaData();
            System.out.println("// Number of columns in "+tableName+": "+rsmd.getColumnCount());
            System.out.print("/* ");
            ArrayList<String> ref=new ArrayList<String>();
            for(int i=1; i<=rsmd.getColumnCount(); i++)
            {
                String colnm=rsmd.getColumnName(i);
                ref.add(colnm.toLowerCase());
                if(i!=rsmd.getColumnCount())
                    System.out.print(colnm.toLowerCase()+", ");
                else
                    System.out.print(colnm.toLowerCase()+" */");
            }        
            System.out.println("\n");
            for(String x:ref)
                System.out.println("private String "+x+"=\"\";");
            System.out.println();
            rs.close();
            smt.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e){}
        }
    }
}