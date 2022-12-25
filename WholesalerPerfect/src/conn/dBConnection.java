package conn;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class dBConnection {

    // Instance Variable
    String DBLocation;

    public Connection setConnection()
    {
        Connection conn=null;
        String DBSource=null;

        File f=null;
        try
        {
            String s=new File(".").getAbsolutePath();
            s=s.substring(0,s.lastIndexOf(".")-1)+"/Database/DBPath.loc";
            FileInputStream fis = new FileInputStream(s);
            DataInputStream myInput = new DataInputStream(fis);
            if((DBLocation = myInput.readLine()) != null)
            {
                DBSource="jdbc:ucanaccess://"+DBLocation+";";
            }
            else
            {
                JOptionPane.showMessageDialog(null,"File Reading Error...","Error Found",JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            f=new File(DBLocation);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,"URI Exception in clsDatabase..."+e.getMessage(),"Error Found",JOptionPane.ERROR_MESSAGE);
        }

        if (f.exists( ))
        {
            DBLocation=f.getAbsolutePath();
        }
        else
        {
            JOptionPane.showMessageDialog(null,"File Doesnot Exists...","Error Found",JOptionPane.ERROR_MESSAGE);
            String str = JOptionPane.showInputDialog(null, "Enter the File Path : ", "New Database Location", JOptionPane.QUESTION_MESSAGE);
            str+="\\WPerfect.accdb";
            if(str != null)
            {
                try
                {
                    String s=new File(".").getAbsolutePath();
                    s=s.substring(0,s.lastIndexOf("."))+"/Database/DBPath.loc";
                    FileOutputStream fos = new FileOutputStream(s);
                    PrintStream myOutput = new PrintStream(fos);
                    myOutput.println(str);
                    FileInputStream fis = new FileInputStream(s);
                    DataInputStream myInput = new DataInputStream(fis);
                    if((DBLocation = myInput.readLine()) != null)
                    {
                        DBSource="jdbc:ucanaccess://"+DBLocation+";";
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null,"File Reading Error...","Error Found",JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    }
                }
                catch(Exception e)
                {
                    JOptionPane.showMessageDialog(null,"File Reading Error: "+e.getMessage(),"Error Found",JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            }
            else
                System.exit(0);
        }
        try
        {
            Driver dv=new net.ucanaccess.jdbc.UcanaccessDriver();
            DriverManager.registerDriver(dv);
            conn = DriverManager.getConnection(DBSource);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,"dBConnection ex1: "+e.getMessage(),"Error Found",JOptionPane.ERROR_MESSAGE);
        }
        return conn;
    }
}