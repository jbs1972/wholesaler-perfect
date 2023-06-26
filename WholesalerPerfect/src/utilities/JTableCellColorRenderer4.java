/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Jayanta Bishnu Sen
 */
public class JTableCellColorRenderer4 extends DefaultTableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent(JTable table,Object value, boolean isSelected,boolean hasFocus, int row, int column)
    {
        JLabel label = new JLabel();

        if (value!=null) 
        {
            Border border = LineBorder.createBlackLineBorder();
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setBorder(border);
            if(value.toString().equals("0"))
            {
                label.setBackground(new Color(150,255,150));// green
                label.setOpaque(true);
                label.setText("NR");
            }
            else
            {
                if(value.toString().equals("1"))
                {
                    label.setBackground(new Color(255,255,150));// yellow
                    label.setOpaque(true);
                    label.setText("R");
                }
                else
                {
                    label.setBackground(new Color(255,150,150));// red
                    label.setOpaque(true);
                    label.setText("O");
                }
            }
        }
        return label;
    }
}
