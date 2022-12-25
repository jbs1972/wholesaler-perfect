/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.JOptionPane;

/**
 *
 * @author Jayanta B. Sen
 */
public class DocumentCopy 
{
    public static void copyFile(String path, String mynm)
    {
        if(path==null || path.length()==0)
        {
            JOptionPane.showMessageDialog(null,"Invalid Source...","Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        try
        {
            String d="C:\\Users\\Jayanta B. Sen\\Dropbox\\"+mynm;
            String s = path;
            if(d==null || d.length()==0)
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
        }
        catch(FileNotFoundException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"File Reading Error...","Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Input/Output Error...","Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    
}
