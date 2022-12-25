/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import query.Query;

/**
 *
 * @author Jayanta Bishnu Sen
 */
public class JTableRowColorRenderer extends DefaultTableCellRenderer
{
    Query q = new Query();
    int red, green, blue;
    
    public JTableRowColorRenderer(int red, int green, int blue)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    @Override
    public Component getTableCellRendererComponent( JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int col)
    {
        Component comp = super.getTableCellRendererComponent( table,  value, isSelected, 
                hasFocus, row, col);
        String s =  table.getModel().getValueAt(row, 0 ).toString();
        if(q.isNumeric(s)) 
        {
            comp.setBackground(new Color(red, green, blue));
            comp.setForeground(new Color(0, 0, 0));
        }
        else
        {
            comp.setBackground(null);
            comp.setForeground(new Color(0, 0, 255));
        }
        return( comp );
    }
}
