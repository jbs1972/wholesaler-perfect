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
public class JTableCellColorRenderer2 extends DefaultTableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent(JTable table,Object value, boolean isSelected,boolean hasFocus, int row, int column)
    {
        JLabel label = new JLabel();

        if (value!=null) 
        {
            label.setHorizontalAlignment(JLabel.CENTER);
            if(value.toString().equals("Active"))
            {
                label.setBackground(new Color(150,255,150));// GREEN
                label.setOpaque(true);
                label.setText("Active");
            }
            else
            {
                if(value.toString().equals("In-Active"))
                {
                    label.setBackground(new Color(255,150,150));// RED
                    label.setOpaque(true);
                    label.setText("In-Active");
                }
            }
        }
        return label;
    }
}
