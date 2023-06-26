/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Jayanta Bishnu Sen
 */
public class JTableCellColorRenderer extends DefaultTableCellRenderer
{
    int red, green, blue;
    public JTableCellColorRenderer(int red, int green, int blue)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    @Override
    public Component getTableCellRendererComponent(JTable table,Object value, boolean isSelected,boolean hasFocus, int row, int column)
    {
        JLabel label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);

        label.setBackground(new Color(red, green, blue));
        label.setOpaque(true);
        label.setText(value.toString());
            
        return label;
    }
}
