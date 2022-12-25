/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Jayanta
 */
public class ImageRenderer extends DefaultTableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent(JTable table,Object value, boolean isSelected,boolean hasFocus, int row, int column)
    {
        JLabel label = new JLabel();

        if (value!=null) 
        {
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setSize(100, 100);
            //value is parameter which filled by byteOfImage
//            label.setIcon(new ImageIcon((byte[])value));
            label.setIcon(new ImageIcon (Toolkit.getDefaultToolkit().createImage((byte[])value).
                        getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH)));
        }
        return label;
    }
}
