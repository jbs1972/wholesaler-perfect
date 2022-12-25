/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Jayanta B. Sen
 */
public class SaleBillEditor extends AbstractCellEditor implements TableCellEditor
{
    JComponent component = new JTextField();
    public Component getTableCellEditorComponent(JTable table, Object value,boolean isSelected, int rowIndex, int vColIndex)
    {
        ((JTextField)component).setText((String) value);
        return component;
    }
    @Override
    public Object getCellEditorValue()
    {
        return ((JTextField)component).getText();
    }
}
