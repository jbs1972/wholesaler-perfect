package conn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.JOptionPane;

public class RestoreDB {
    public static void copyFile(String path)
    {
        try
        {
            String d=new File(".").getAbsolutePath();
            d=d.substring(0,d.lastIndexOf("."))+"Database\\WPerfect.accdb";
            String s = path;
            if(s==null || s.length()==0)
            {
                JOptionPane.showMessageDialog(null,"Invalid Destination...","Error Found",JOptionPane.ERROR_MESSAGE);
                return;
            }
            File f1 = new File(s);
            File f2 = new File(d);

            InputStream in = new FileInputStream(f1);
            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
            {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            JOptionPane.showMessageDialog(null,"Database Restoration Complete...","Restoration Successful",JOptionPane.INFORMATION_MESSAGE);
        }
        catch(FileNotFoundException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"File Reading Error...","Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        catch(IOException e)
        {
            JOptionPane.showMessageDialog(null,"Input/Output Error...","Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
}
