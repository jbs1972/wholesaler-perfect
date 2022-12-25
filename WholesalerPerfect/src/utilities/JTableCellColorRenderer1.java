/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Jayanta Bishnu Sen
 */
public class JTableCellColorRenderer1 extends DefaultTableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent(JTable table,Object value, boolean isSelected,boolean hasFocus, int row, int column)
    {
        JLabel label = new JLabel();

        if (value!=null) 
        {
            label.setHorizontalAlignment(JLabel.CENTER);
            if(value.toString().equals("Open"))
            {
                label.setBackground(new Color(150,255,150));// GREEN
                label.setOpaque(true);
                label.setText("Open");
            }
            else
            {
                if(value.toString().equals("Blocked"))
                {
                    label.setBackground(new Color(255,150,150));// RED
                    label.setOpaque(true);
                    label.setText("Blocked");
                }
            }
        }
        return label;
    }
}
