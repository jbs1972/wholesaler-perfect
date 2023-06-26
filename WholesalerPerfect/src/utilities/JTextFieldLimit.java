/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 *
 * @author Jayanta B. Sen
 */
public class JTextFieldLimit  extends PlainDocument {

    /*
     *  User Guide
     *  jTextField1.setDocument(new JTextFieldLimit(4));
     */
    
    private int limit;

    public JTextFieldLimit(int limit)
    {
        super();
        this.limit = limit;
    }

    @Override
    public void insertString( int offset, String  str, AttributeSet attr ) throws BadLocationException
    {
        if (str == null) return;

        if ((getLength() + str.length()) <= limit)
        {
            super.insertString(offset, str, attr);
        }
    }

}
