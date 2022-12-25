/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Jayanta B. Sen
 */
public class TableAllign {

    static public void setTableAlignment(JTable table, int from){
        // table header alignment
//        JTableHeader header = table.getTableHeader();
//        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer();
//        header.setDefaultRenderer(renderer);
//        renderer.setHorizontalAlignment(JLabel.CENTER);

        // table content alignment
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.RIGHT );
        int rowNumber = table.getColumnCount();
        for(int i = from; i < rowNumber; i++){
            table.getColumnModel().getColumn(i).setCellRenderer( centerRenderer );
        }
    }

}
