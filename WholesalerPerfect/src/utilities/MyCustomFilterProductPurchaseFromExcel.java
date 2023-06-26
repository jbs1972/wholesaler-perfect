/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.io.File;

/**
 *
 * @author Jayanta B. Sen
 */
public class MyCustomFilterProductPurchaseFromExcel extends javax.swing.filechooser.FileFilter {

    @Override
    public boolean accept(File file) {
        // Allow just directories and files with ".txt" extension...
        return file.isDirectory() || file.getAbsolutePath().endsWith(".xls");
    }

    @Override
    public String getDescription() {
        // This description will be displayed in the dialog,
        // hard-coded = ugly, should be done via I18N
        return "Excel File(*.xls)";
    }

}
