package Useful;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/*
*   Jayanta B. Sen
*/

public class ColorValues extends Frame implements AdjustmentListener
{
    private Scrollbar red;
    private Scrollbar green;
    private Scrollbar blue;
    private Label lb;
    private TextField tf;
    private static ColorValues cv;

    public ColorValues()
    {
        super("Color Code Generator");
        setLayout(null);

        lb=new Label();
        lb.setBackground(Color.black);
        lb.setBounds(50,20,200,50);
        add(lb);

        tf=new TextField();
        tf.setBounds(100,75,100,20);
        add(tf);

        red = new Scrollbar(Scrollbar.HORIZONTAL,0,0,0,255);
        red.setBackground(Color.red);
        red.setBounds(50,110,200,25);
        green = new Scrollbar(Scrollbar.HORIZONTAL,0,0,0,255);
        green.setBackground(Color.green);
        green.setBounds(50,140,200,25);
        blue = new Scrollbar(Scrollbar.HORIZONTAL,0,0,0,255);
        blue.setBackground(Color.blue);
        blue.setBounds(50,170,200,25);

        red.addAdjustmentListener(this);
        green.addAdjustmentListener(this);
        blue.addAdjustmentListener(this);
        X obj=new X();
        tf.addKeyListener(obj);

        add(red);
        add(green);
        add(blue);
    }

    public void adjustmentValueChanged(AdjustmentEvent AdjEvt)
    {
        lb.setBackground(new Color(red.getValue(), green.getValue(), blue.getValue()));
        tf.setText(" "+red.getValue()+", "+green.getValue()+", "+blue.getValue());
    }
    
    class X extends KeyAdapter
    {
        public void keyPressed(KeyEvent e) 
        {
            if(e.getKeyCode() == KeyEvent.VK_ENTER)
            {
                String s=tf.getText();
                String val[]=s.split(",");
                try
                {
                    int r=Integer.parseInt(val[0].trim());
                    int g=Integer.parseInt(val[1].trim());
                    int b=Integer.parseInt(val[2].trim());
                    lb.setBackground(new Color(r,g,b));
                    red.setValue(r);
                    green.setValue(g);
                    blue.setValue(b);
                }
                catch(NumberFormatException ex)
                {
                    lb.setBackground(Color.white);
                    lb.setText("Invalid Number: "+ex.getMessage());
                }
            }
        }
    }
    
    public static void main(String args[])
    {
        cv=new ColorValues();
        cv.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                cv.setVisible(false);
                cv.dispose();
                System.exit(0);
            }
        });
        cv.setSize(300, 200);
        cv.setVisible(true);
    }
}