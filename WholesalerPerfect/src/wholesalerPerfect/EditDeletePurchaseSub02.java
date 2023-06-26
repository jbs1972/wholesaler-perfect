package wholesalerPerfect;

import com.toedter.calendar.JTextFieldDateEditor;
import conn.dBConnection;
import dto.PurchaseMaster;
import dto.PurchaseSub;
import dto.UserProfile;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import query.Query;
import utilities.DateConverter;
import utilities.MyNumberFormat;
import utilities.Settings;

public class EditDeletePurchaseSub02 extends javax.swing.JInternalFrame implements AWTEventListener {
    private JDesktopPane jDesktopPane1;
    private UserProfile up;
    private Settings settings=new Settings();
    private DecimalFormat format = new DecimalFormat("0.#");
    private Query q=new Query();
    private DecimalFormat format2afterDecimal = new DecimalFormat("#.##");
    
    private PurchaseMaster pm;
    private ArrayList<PurchaseSub> psAl = new ArrayList<PurchaseSub>();
    private ArrayList<PurchaseSub> oldPsAl = new ArrayList<PurchaseSub>();
    private String currentItemmid;
    private String newItemDetails;
    private String currentCompid;
    private String itemmidArray[];
    private String selectedItemDetails;
    private String currentItemdid;
    
    public EditDeletePurchaseSub02(JDesktopPane jDesktopPane1, UserProfile up, PurchaseMaster pm) {
        super("Edit/Delete Purchase",false,true,false,true);
        initComponents();
        this.jDesktopPane1 = jDesktopPane1;
        this.up = up;
        this.pm = pm;
        this.oldPsAl = pm.getPsAl();
        Dimension d=getSize();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) dim.getWidth() - (int)d.getWidth())/2,((int) dim.getHeight() - (int)d.getHeight())/2-43);
        this.setResizable(false);
        this.setFrameIcon(new ImageIcon(getClass().getResource("/images/MODIFY.PNG")));
        
        this.getActionMap().put("test", new AbstractAction(){     //ESCAPE
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Escape Pressed");
                setVisible(false);
                dispose();
            }
        });
        InputMap map = this.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        
        jDateChooser1.setDate(new Date());
        jDateChooser1.getToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
        jDateChooser1.getDateEditor().getUiComponent().addFocusListener(new FocusAdapter() 
        {
            @Override
            public void focusGained(FocusEvent evt) 
            {
                ((JTextFieldDateEditor)evt.getSource()).selectAll();
            }
        });
        
        ((DefaultTableCellRenderer)jTable1.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        ((JLabel)jComboBox1.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        settings.numvalidatorFloat(jTextField1);
        settings.numvalidatorFloat(jTextField2);
        settings.numvalidatorInt(jTextField3);
        settings.numvalidatorFloat(jTextField4);
        settings.numvalidatorFloatWithSign(jTextField6);
        
        jTable1.setComponentPopupMenu(jPopupMenu1);
        
        SwingUtilities.invokeLater
        (
            new Runnable() 
            {
                @Override
                public void run() 
                {
                    jTextField7.requestFocusInWindow();
                }
            }
        );
    }
    
    @Override
    public void eventDispatched(AWTEvent event) 
    {
        if(event instanceof KeyEvent)
        {
            KeyEvent key = (KeyEvent)event;
            if(key.getID()==KeyEvent.KEY_PRESSED)
            {
                if(event.getSource().equals(jDateChooser1.getDateEditor())&&key.getKeyCode()==10)
                {
                    jTextField1.requestFocusInWindow();
                } 
            }
        }
    }
    
    private void calculationForTradeNReplcDisc() {
        if(psAl.isEmpty()) {
            return;
        }
        double tradeDiscPer = Double.parseDouble(jTextField1.getText());
        double replacementDiscPer = Double.parseDouble(jTextField2.getText());
        if(tradeDiscPer == Double.parseDouble(pm.getTradediscper()) && replacementDiscPer == Double.parseDouble(pm.getReplacementdiscper())) {
            return;
        }
        // Updating individual item
        for(PurchaseSub ps: psAl) {
            // Number of columns in PurchaseSub: 16
            /* psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, 
            taxableamt, gstamt, qtysold, retqty, tradediscamt, replacementdiscamt */
            double gross = Double.parseDouble(ps.getGross());
            double discountAmt = Double.parseDouble(ps.getDiscamt());
            double afterDiscount = gross - discountAmt; // In bill this is the Taxable Amount
            double tradeDiscAmt = afterDiscount * (tradeDiscPer / 100.0);
            double afterTradeDiscount = afterDiscount - tradeDiscAmt;
            double replacementDiscAmt = afterTradeDiscount * (replacementDiscPer / 100.0);
            double afterReplacementDiscount = afterTradeDiscount - replacementDiscAmt; // This is the actual Taxable Amount
            double gstper = Double.parseDouble(ps.getGst());
            double gstamt = afterReplacementDiscount * (gstper / 100.0);
            
            ps.setTradediscamt(format2afterDecimal.format(tradeDiscAmt));
            ps.setReplacementdiscamt(format2afterDecimal.format(replacementDiscAmt));
            ps.setTaxableamt(format2afterDecimal.format(afterReplacementDiscount));
            ps.setGstamt(format2afterDecimal.format(gstamt));
        }
        // Populating grid and updating master record
        Fetch();
    }
    
    private void addAlterItemDetails()
    {
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    setVisible(false);
                    final ItemDetails ref=new ItemDetails(jDesktopPane1, true, up);
                    ref.addInternalFrameListener(new InternalFrameAdapter()
                    {
                        @Override
                        public void internalFrameDeactivated(InternalFrameEvent e)
                        {
                            newItemDetails=ref.getNewItemDetails();
                        }
                        @Override
                        public void internalFrameClosed(InternalFrameEvent e)
                        {
                            EditDeletePurchaseSub02.this.setVisible(true);
                            if(newItemDetails!=null)
                            {
                                String newItemnm = "";
                                // itemdid+"~"+itemmid+"~"+mrp+"~"+gst+"~"+pexgst+"~"+pingst+"~"+sexgst+"~"+singst
                                // Number of columns in ItemMaster: 5
                                /* itemmid, compid, itemnm, hsn, isactive */
                                String s[] = newItemDetails.split("~");
                                dBConnection db=new dBConnection();
                                Connection conn=db.setConnection();
                                String query="select itemnm from ItemMaster where isactive=1 and itemmid="+s[1];
                                System.out.println(query);
                                try
                                {
                                    Statement smt=conn.createStatement();
                                    ResultSet rs=smt.executeQuery(query);
                                    if(rs.next())
                                    {
                                        newItemnm = rs.getString("itemnm");
                                    }
                                }
                                catch(SQLException ex)
                                {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(null,"EditDeletePurchaseSub02 ex: "+ex.getMessage(),
                                            "SQL Error Found",JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                                finally
                                {
                                    try {
                                        if (conn!=null) conn.close();
                                    }
                                    catch(SQLException ex) {}
                                }
                                populateCombo1();
                                
                                jComboBox1.setSelectedItem(newItemnm);
                            }
                        }
                    });
                    ref.setVisible(true);
                    jDesktopPane1.add(ref);
                    ref.show();
                    ref.setIcon(false);
                    ref.setSelected(true);
                }
                catch(PropertyVetoException e){}
            }
        });
        t.start();
        try
        {
            t.join();
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
    }
    
    private void populateCombo1() // Item Details
    {
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in ItemMaster: 5
        /* itemmid, compid, itemnm, hsn, isactive */
        String query="select itemmid, itemnm from ItemMaster where isactive=1 and compid="+currentCompid+" order by itemnm";
        System.out.println(query);
        try
        {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(query);
            int total = 0;
            //Move to the last record
            rs.afterLast();
            //Get the current record position
            if(rs.previous())
                total = rs.getRow();
            //Move back to the first record;
            rs.beforeFirst();
            jComboBox1.removeAllItems();
            if(total != 0)
            {
                itemmidArray=new String[total];
                jComboBox1.addItem("-- Select --");
                int i=0;
                while(rs.next())
                {
                    itemmidArray[i++]=rs.getString("itemmid");
                    jComboBox1.addItem(rs.getString("itemnm"));
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"EditDeletePurchaseSub02 ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e) {}
        }
    }
    
    private void getItemDetails() {
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    setVisible(false);
                    final ItemDetails4Purchase ref=new ItemDetails4Purchase(currentItemmid);
                    ref.addInternalFrameListener(new InternalFrameAdapter()
                    {
                        @Override
                        public void internalFrameDeactivated(InternalFrameEvent e)
                        {
                            selectedItemDetails=ref.getSelectedItemDetails();
//                            System.out.println(selectedItemDetails);
                        }
                        @Override
                        public void internalFrameClosed(InternalFrameEvent e)
                        {
                            EditDeletePurchaseSub02.this.setVisible(true);
//                            System.out.println(selectedItemDetails);
                            if(selectedItemDetails!=null)
                            {
                                //     0               1            2            3             4                 5               6                 7                8
                                // itemdid+"~"+hsn+"~"+mrp+"~"+gst+"~"+pexgst+"~"+pingst+"~"+sexgst+"~"+singst+"~"+onhand
                                String s[] = selectedItemDetails.split("~");
                                currentItemdid = s[0];
                                jLabel12.setText(s[1]); //hsn
                                jLabel14.setText(s[3]); //gst
                                jLabel17.setText(s[4]); //purchase ex-gst
                                jLabel19.setText(s[5]); //purchase in-gst
                                jLabel21.setText(s[2]); //mrp
                                calculation01();
                            }
                        }
                    });
                    ref.setVisible(true);
                    jDesktopPane1.add(ref);
                    ref.show();
                    ref.setIcon(false);
                    ref.setSelected(true);
                }
                catch(PropertyVetoException e){}
            }
        });
        t.start();
        try
        {
            t.join();
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
    }
    
    private void calculation01() {
        try {
            double exgst = Double.parseDouble(jLabel17.getText().trim().replaceAll(",", ""));
            int qty = Integer.parseInt(jTextField3.getText());
            double gross = exgst * qty;
            jLabel24.setText(MyNumberFormat.rupeeFormat(gross));
            double discountAmt = Double.parseDouble(jTextField4.getText());
            double afterDiscount = gross - discountAmt; // In bill this is the Taxable Amount
            double tradeDiscPer = Double.parseDouble(jTextField1.getText());
            double tradeDiscAmt = afterDiscount * (tradeDiscPer / 100.0);
            jLabel27.setText(MyNumberFormat.rupeeFormat(tradeDiscAmt));
            double afterTradeDiscount = afterDiscount - tradeDiscAmt;
            double replacementDiscPer = Double.parseDouble(jTextField2.getText());
            double replacementDiscAmt = afterTradeDiscount * (replacementDiscPer / 100.0);
            jLabel29.setText(MyNumberFormat.rupeeFormat(replacementDiscAmt));
            double afterReplacementDiscount = afterTradeDiscount - replacementDiscAmt; // This is the actual Taxable Amount
            jLabel31.setText(MyNumberFormat.rupeeFormat(afterReplacementDiscount));
            double gstper = Double.parseDouble(jLabel14.getText());
            double gstamt = afterReplacementDiscount * (gstper / 100.0);
            jLabel33.setText(MyNumberFormat.rupeeFormat(gstamt));
        } catch (Exception ex) {}
    }
    
    private void addToList() {
        // Number of columns in PurchaseSub: 16
        /* psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, 
        taxableamt, gstamt, qtysold, retqty, tradediscamt, replacementdiscamt */
        String itemdid = currentItemdid;
        if(itemdid == null)
        {
            JOptionPane.showMessageDialog(null,"Select the Item Details (F5 from item selection) !!!","Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox1.requestFocusInWindow();
            return;
        }
        String gst = jLabel14.getText().trim().replaceAll(",", "");
        String exgst = jLabel17.getText().trim().replaceAll(",", "");
        String ingst = jLabel19.getText().trim().replaceAll(",", "");
        String mrp = jLabel21.getText().trim().replaceAll(",", "");
        String qty = jTextField3.getText();
        if(Integer.parseInt(qty) == 0)
        {
            JOptionPane.showMessageDialog(null,"Quantity is mandatory !!!","Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField3.requestFocusInWindow();
            return;
        }
        String gross = jLabel24.getText().trim().replaceAll(",", "");
        String discamt = jTextField4.getText();
        String tradediscamt = jLabel27.getText().trim().replaceAll(",", "");
        String replacementdiscamt = jLabel29.getText().trim().replaceAll(",", "");
        String taxableamt = jLabel31.getText().trim().replaceAll(",", "");
        String gstamt = jLabel33.getText().trim().replaceAll(",", "");
        String qtysold = "0";
        String retqty = "0";
        
        
        PurchaseSub ps = new PurchaseSub();
        // Number of columns in PurchaseSub: 16
        /* psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, 
        taxableamt, gstamt, qtysold, retqty, tradediscamt, replacementdiscamt */
        ps.setPsid(""); // At Insert
        ps.setPmid(""); // At Insert
        ps.setItemdid(itemdid);
        ps.setGst(gst);
        ps.setExgst(exgst);
        ps.setIngst(ingst);
        ps.setMrp(mrp);
        ps.setQty(qty);
        ps.setGross(gross);
        ps.setDiscamt(discamt);
        ps.setTaxableamt(taxableamt);
        ps.setGstamt(gstamt);
        ps.setQtysold(qtysold);
        ps.setRetqty(retqty);
        ps.setTradediscamt(tradediscamt);
        ps.setReplacementdiscamt(replacementdiscamt);
        psAl.add(ps);
        
        Fetch();

        itemDetailsFormFlush();
        jComboBox1.setSelectedIndex(0);
        jComboBox1.requestFocusInWindow();
    }
    
    private void clearTable(JTable table)
    {
        for(int i=table.getRowCount()-1; i>=0; i--)
        {
            ((DefaultTableModel)table.getModel()).removeRow(i);
        }
    }
    
    private void Fetch()
    {
        int netqty = 0;
        double netgross = 0.0;
        double netdiscount = 0.0;
        double nettradediscamt = 0.0;
        double netreplacementdiscamt = 0.0;
        double nettaxable = 0.0;
        double netgstamt = 0.0;
        int slno1=0;
        clearTable(jTable1);
        
        // Number of columns in PurchaseSub: 16
        /* psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, 
        taxableamt, gstamt, qtysold, retqty, tradediscamt, replacementdiscamt */
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        for (PurchaseSub ps :  psAl)
        {
            Vector<String> row = new Vector<String>();
            row.addElement(++slno1+"");
            // No. Of Columns: 11
            // SLN., ITEM, QTY., MRP, EX-GST, GROSS, DISC. AMT., TD. AMT., RD. AMT., TAXABLE, GST AMT.
            // Number of columns in ItemMaster: 5
            /* itemmid, compid, itemnm, hsn, isactive */
            // Number of columns in ItemDetails: 10
            /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
            String query="select itemnm from ItemMaster, ItemDetails where ItemMaster.itemmid=ItemDetails.itemmid "
                    + "and ItemMaster.isactive=1 and ItemDetails.isactive=1 and itemdid="+ps.getItemdid();
            System.out.println(query);
            try
            {
                Statement smt=conn.createStatement();
                ResultSet rs=smt.executeQuery(query);
                if (rs.next())
                {
                    row.addElement(rs.getString("itemnm"));
                }
            }
            catch(SQLException ex)
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"EditDeletePurchaseSub02 ex: "+ex.getMessage(),
                        "Error Found",JOptionPane.ERROR_MESSAGE);
                return;
            }
            netqty += Integer.parseInt(ps.getQty());
            row.addElement(ps.getQty());
            // No. Of Columns: 9
            // SLN., ITEM, QTY., MRP, EX-GST, GROSS, DISC. AMT., TAXABLE, GST AMT.
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getMrp())));
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getExgst())));
            netgross += Double.parseDouble(ps.getGross());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getGross())));
            netdiscount += Double.parseDouble(ps.getDiscamt());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getTradediscamt())));
            nettradediscamt += Double.parseDouble(ps.getTradediscamt());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getReplacementdiscamt())));
            netreplacementdiscamt += Double.parseDouble(ps.getReplacementdiscamt());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getDiscamt())));
            nettaxable += Double.parseDouble(ps.getTaxableamt());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getTaxableamt())));
            netgstamt += Double.parseDouble(ps.getGstamt());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getGstamt())));
            ((DefaultTableModel)jTable1.getModel()).addRow(row);
        }
        try {
            if (conn!=null) conn.close();
        }
        catch(SQLException e){}

        jTable1.setDragEnabled(false);
        // Disable auto resizing
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableHeader header = jTable1.getTableHeader();
        header.setBackground(Color.cyan);
        //Start resize the table column
        // No. Of Columns: 11
        // SLN., ITEM, QTY., MRP, EX-GST, GROSS, DISC. AMT., TD. AMT., RD. AMT., TAXABLE, GST AMT.
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN.
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(1).setMinWidth(0);// ITEM
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(350);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);// QTY.
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// MRP
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(110);
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);// EX-GST
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(110);
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);// GROSS
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(110);
        jTable1.getColumnModel().getColumn(6).setMinWidth(0);// DISC. AMT.
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(7).setMinWidth(0);// TD. AMT.
        jTable1.getColumnModel().getColumn(7).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(8).setMinWidth(0);// RD. AMT.
        jTable1.getColumnModel().getColumn(8).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(9).setMinWidth(0);// TAXABLE
        jTable1.getColumnModel().getColumn(9).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(10).setMinWidth(0);// GST AMT.
        jTable1.getColumnModel().getColumn(10).setPreferredWidth(120);
        
        // No. Of Columns: 11
        // SLN., ITEM, QTY., MRP, EX-GST, GROSS, DISC. AMT., TD. AMT., RD. AMT., TAXABLE, GST AMT.
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable1.getColumn("SLN.").setCellRenderer( centerRenderer );
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        jTable1.getColumn("QTY.").setCellRenderer( rightRenderer );
        jTable1.getColumn("MRP").setCellRenderer( rightRenderer );
        jTable1.getColumn("EX-GST").setCellRenderer( rightRenderer );
        jTable1.getColumn("GROSS").setCellRenderer( rightRenderer );
        jTable1.getColumn("DISC. AMT.").setCellRenderer( rightRenderer );
        jTable1.getColumn("TD. AMT.").setCellRenderer( rightRenderer );
        jTable1.getColumn("RD. AMT.").setCellRenderer( rightRenderer );
        jTable1.getColumn("TAXABLE").setCellRenderer( rightRenderer );
        jTable1.getColumn("GST AMT.").setCellRenderer( rightRenderer );
        
        jLabel36.setText(format.format(netqty));
        jLabel38.setText(MyNumberFormat.rupeeFormat(netgross));
        jLabel40.setText(MyNumberFormat.rupeeFormat(netdiscount));
        jLabel42.setText(MyNumberFormat.rupeeFormat(nettradediscamt));
        jLabel44.setText(MyNumberFormat.rupeeFormat(netreplacementdiscamt));
        jLabel46.setText(MyNumberFormat.rupeeFormat(nettaxable));
        
        jLabel48.setText(MyNumberFormat.rupeeFormat(netgstamt));
        
        computation02();
    }
    
    private  void computation02() {
        try {
            double netTaxableAmt = Double.parseDouble(jLabel46.getText().trim().replaceAll(",", ""));
            double netGstAmt = Double.parseDouble(jLabel48.getText().trim().replaceAll(",", ""));
            double payableAmt = netTaxableAmt + netGstAmt;
            jLabel50.setText(MyNumberFormat.rupeeFormat(payableAmt));
            double roundoff = Double.parseDouble(jTextField6.getText());
            double totalAmount = payableAmt + roundoff;
            jLabel53.setText(MyNumberFormat.rupeeFormat(totalAmount));
        } catch (Exception ex) { }
    }
    
    private void itemDetailsFormFlush() {
        currentItemdid = null;
        newItemDetails = null;
        selectedItemDetails = null;
        
        jLabel12.setText("N/A");
        jLabel14.setText("0");
        jLabel17.setText("0");
        jLabel19.setText("0");
        jLabel21.setText("0");
        jTextField3.setText("0");
        jLabel24.setText("0");
        jTextField4.setText("0");
        jLabel27.setText("0");
        jLabel29.setText("0");
        jLabel31.setText("0");
        jLabel33.setText("0");
    }
    
    private void insertToDatabase() {
        if (psAl.size() == 0)
        {
            JOptionPane.showMessageDialog(null,"Incomplete Data !!!","Incomplete Data",JOptionPane.ERROR_MESSAGE);
            jComboBox1.requestFocusInWindow();
            return;
        }
        // compid as currentCompid
        String oldPmid = pm.getPmid();
        String compid=pm.getCompid();
        String ssid=pm.getSsid();
        String invno=pm.getInvno();
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
        Date invDt =jDateChooser1.getDate();
        String invdt=null;
        try
        {
            invdt=sdf.format(invDt);
        }
        catch(NullPointerException ex)
        {
            JOptionPane.showMessageDialog(null,"Invalid Purchase Date.","Invalid Date",JOptionPane.ERROR_MESSAGE);
            jDateChooser1.requestFocusInWindow();
            return;
        }
        String netqty = jLabel36.getText().trim().replaceAll(",", "");
        String netgross = jLabel38.getText().trim().replaceAll(",", "");
        String netitemdisc = jLabel40.getText().trim().replaceAll(",", "");
        String tradediscper = jTextField1.getText();
        String nettradediscamt = jLabel42.getText().trim().replaceAll(",", "");
        String replacementdiscper = jTextField2.getText();
        String netreplacementdiscamt = jLabel44.getText().trim().replaceAll(",", "");
        String nettaxable =  jLabel46.getText().trim().replaceAll(",", "");
        String netgstamt = jLabel48.getText().trim().replaceAll(",", "");
        String grosspayableamt = jLabel50.getText().trim().replaceAll(",", "");
        String roundoff = jTextField6.getText();
        String netpayableamt = jLabel53.getText().trim().replaceAll(",", "");
        String isopening = pm.getIsopening();
        String amtpaid = pm.getAmtpaid();
        String isactive = pm.getIsactive();
        
        // Number of columns in PurchaseMaster: 20
        /* pmid, ssid, compid, invno, invdt, netqty, netgross, netitemdisc, nettaxable, tradediscper, 
        nettradediscamt, replacementdiscper, netreplacementdiscamt, netgstamt, grosspayableamt, 
        roundoff, netpayableamt, isopening, amtpaid, isactive */
        PurchaseMaster pm = new PurchaseMaster();
        pm.setPmid(oldPmid); 
        pm.setSsid(ssid);
        pm.setCompid(compid);
        pm.setInvno(invno);
        pm.setInvdt(invdt);
        pm.setNetqty(netqty);
        pm.setNetgross(netgross);
        pm.setNetitemdisc(netitemdisc);
        pm.setNettaxable(nettaxable);
        pm.setTradediscper(tradediscper);
        pm.setNettradediscamt(nettradediscamt);
        pm.setReplacementdiscper(replacementdiscper);
        pm.setNetreplacementdiscamt(netreplacementdiscamt);
        pm.setNetgstamt(netgstamt);
        pm.setGrosspayableamt(grosspayableamt);
        pm.setRoundoff(roundoff);
        pm.setNetpayableamt(netpayableamt);
        pm.setIsopening(isopening);
        pm.setAmtpaid(amtpaid);
        pm.setIsactive(isactive);
        pm.setPsAl(psAl);
        
        int pmid = q.updatePurchaseMaster(pm, oldPsAl);
        if (pmid > 0)
        {
            pm = q.getPurchaseMaster(pmid+"");
            
            dBConnection db=new dBConnection();
            Connection conn=db.setConnection();
            
            PreparedStatement psmt01 = null;
            PreparedStatement psmt02 = null;
            PreparedStatement psmt03 = null;
            
            PreparedStatement psmt1 = null;
            PreparedStatement psmt2 = null;
            PreparedStatement psmt3 = null;
            PreparedStatement psmt4 = null;
            
            // Number of columns in ItemLedger: 9
            /* ilid, itemdid, tablenm, pknm, pkval, actiondt, type, prevqty, qty */
            int ilid=q.getMaxId("ItemLedger", "ilid");
            // Number of columns in PurchasePaymentRegister: 8
            /* pprid, pmid, pknm, pkval, actiondt, refno, type, amount */
            int pprid=q.getMaxId("PurchasePaymentRegister", "pprid");
            try
            {
                conn.setAutoCommit(false);
                
                // Number of columns in ItemDetails: 10
                /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
                String sql01 = "update ItemDetails set onhand=onhand-? where itemdid=?";
                psmt01 = conn.prepareStatement(sql01);
                // Number of columns in ItemLedger: 9
                /* ilid, itemdid, tablenm, pknm, pkval, actiondt, type, prevqty, qty */
                String sql02 = "delete from  ItemLedger where tablenm='PurchaseSub' and pknm='psid' and pkval=?";
                psmt02 = conn.prepareStatement(sql02);
                // Number of columns in PurchasePaymentRegister: 7
                /* pprid, pknm, pkval, actiondt, refno, type, amount */
                String sql03 = "delete from PurchasePaymentRegister where pknm='pmid' and pkval=? and refno=?";
                psmt03 = conn.prepareStatement(sql03);
                for( PurchaseSub ref : oldPsAl )
                {
                    psmt01.setInt(1, Integer.parseInt(ref.getQty()));
                    psmt01.setInt(2, Integer.parseInt(ref.getItemdid()));
                    psmt01.addBatch();
                    
                    psmt02.setString(1, ref.getPsid());
                    psmt02.addBatch();
                }
                psmt01.executeBatch();
                psmt02.executeBatch();
                
                psmt03.setString(1, this.pm.getPmid());
                psmt03.setString(2, this.pm.getInvno());
                psmt03.executeUpdate();
                
                String pervqty = null;
                // Number of columns in ItemDetails: 10
                /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
                String sql1 = "select onhand from ItemDetails where itemdid=?";
                psmt1 = conn.prepareStatement(sql1);
                // Number of columns in PurchaseSub: 14
                /* psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, taxableamt, gstamt, qtysold, retqty */
                // Number of columns in ItemDetails: 10
                /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
                String sql2 = "update ItemDetails set onhand=onhand+? where itemdid=?";
                psmt2 = conn.prepareStatement(sql2);
                // Number of columns in ItemLedger: 9
                /* ilid, itemdid, tablenm, pknm, pkval, actiondt, type, prevqty, qty */
                String sql3 = "insert into ItemLedger (ilid, itemdid, tablenm, pknm, pkval, actiondt, "
                        + "type, prevqty, qty) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                psmt3 = conn.prepareStatement(sql3);
                for( PurchaseSub ref : pm.getPsAl() )
                {
                    psmt1.setInt(1, Integer.parseInt(ref.getItemdid()));
                    ResultSet rs = psmt1.executeQuery();
                    if (rs.next()) {
                        pervqty = rs.getString("onhand");
                    }
                    
                    psmt2.setInt(1, Integer.parseInt(ref.getQty()));
                    psmt2.setInt(2, Integer.parseInt(ref.getItemdid()));
                    psmt2.addBatch();
                    
                    psmt3.setInt(1, ++ilid);
                    psmt3.setInt(2, Integer.parseInt(ref.getItemdid()));
                    psmt3.setString(3, "PurchaseSub");
                    psmt3.setString(4, "psid");
                    psmt3.setString(5, ref.getPsid());
                    psmt3.setDate(6, java.sql.Date.valueOf(DateConverter.dateConverter1(invdt)));
                    psmt3.setString(7, "ADD");
                    psmt3.setInt(8, Integer.parseInt(pervqty));
                    psmt3.setInt(9, Integer.parseInt(ref.getQty()));
                    psmt3.addBatch();
                }
                psmt2.executeBatch();
                psmt3.executeBatch();
                
                // Number of columns in PurchasePaymentRegister: 7
                /* pprid, pknm, pkval, actiondt, refno, type, amount */
                String sql4 = "insert into PurchasePaymentRegister (pprid, pknm, pkval, actiondt,"
                        + " refno, type, amount) values (?, ?, ?, ?, ?, ?, ?)";
                psmt4 = conn.prepareStatement(sql4);
                psmt4.setInt(1, ++pprid);
                psmt4.setString(2, "pmid");
                psmt4.setString(3, pmid+"");
                psmt4.setDate(4, java.sql.Date.valueOf(DateConverter.dateConverter1(invdt)));
                psmt4.setString(5, invno);
                psmt4.setInt(6, 0);
                psmt4.setDouble(7, Double.parseDouble(pm.getNetpayableamt()));
                psmt4.executeUpdate();
                
                conn.commit();
            }
            catch(SQLException ex)
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"EditDeletePurchaseSub02 ex: "+ex.getMessage(),
                        "Error Found",JOptionPane.ERROR_MESSAGE);
                try {
                    conn.rollback();
                } catch (SQLException ex1) {
                    ex1.printStackTrace();
                }
            }
            finally {
                if (psmt01 != null)  {
                    try {
                        psmt01.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                if (psmt02 != null) {
                    try {
                        psmt02.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                if (psmt03 != null) {
                    try {
                        psmt03.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                
                if (psmt1 != null) 
                {
                    try {
                        psmt1.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                if (psmt2 != null) 
                {
                    try {
                        psmt2.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                if (psmt3 != null) 
                {
                    try {
                        psmt3.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                if (psmt4 != null) 
                {
                    try {
                        psmt4.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                try {
                    if (conn!=null) conn.close();
                }
                catch(SQLException e){}
            }
             formFlush();  
        }
    }
    
    private void formFlush() {
        currentCompid = null;
        psAl = new ArrayList<PurchaseSub>();
        
        jTextField7.setText("");
        jDateChooser1.setDate(new Date());
        jLabel8.setText("N/A");
        jTextField1.setText("0");
        jTextField2.setText("0");
        
        clearTable(jTable1);
        itemDetailsFormFlush();
        
        jTextField5.setText("");
        jLabel36.setText("0");
        jLabel38.setText("0");
        jLabel40.setText("0");
        jLabel42.setText("0");
        jLabel44.setText("0");
        jLabel46.setText("0");
        jLabel48.setText("0");
        
        jLabel50.setText("0");
        jTextField6.setText("0");
        jLabel53.setText("0");
        
        jTextField7.requestFocusInWindow();
    }
    
    private void deletePurchase() {
        String ObjButtons[] = {"Yes","Cancel"};
        int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure to Delete Purchase-Bill!","Delete Sale Bill",
                JOptionPane.DEFAULT_OPTION,JOptionPane.QUESTION_MESSAGE,null,ObjButtons,ObjButtons[1]);
        if(PromptResult!=0) {
            return;
        }
        
        String pmid = this.pm.getPmid();
        
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();

        PreparedStatement psmt01 = null;
        PreparedStatement psmt02 = null;
        PreparedStatement psmt03 = null;
        PreparedStatement psmt04 = null;
        PreparedStatement psmt05 = null;
        PreparedStatement psmt06 = null;
        try
        {
            conn.setAutoCommit(false);
            
            // Number of columns in ItemDetails: 10
            /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
            String sql01 = "update ItemDetails set onhand=onhand-? where itemdid=?";
            psmt01 = conn.prepareStatement(sql01);
            // Number of columns in ItemLedger: 9
            /* ilid, itemdid, tablenm, pknm, pkval, actiondt, type, prevqty, qty */
            String sql02 = "delete from  ItemLedger where tablenm='PurchaseSub' and pknm='psid' and pkval=?";
            psmt02 = conn.prepareStatement(sql02);
            for( PurchaseSub ref : oldPsAl )
            {
                psmt01.setInt(1, Integer.parseInt(ref.getQty()));
                psmt01.setInt(2, Integer.parseInt(ref.getItemdid()));
                psmt01.addBatch();

                psmt02.setString(1, ref.getPsid());
                psmt02.addBatch();
            }
            psmt01.executeBatch();
            psmt02.executeBatch();
            
            // Number of columns in PurchasePaymentRegister: 7
            /* pprid, pknm, pkval, actiondt, refno, type, amount */
            String sql03 = "delete from PurchasePaymentRegister where pknm='pmid' and pkval=? and refno=?";
            psmt03 = conn.prepareStatement(sql03);
            psmt03.setString(1, pmid);
            psmt03.setString(2, this.pm.getInvno());
            psmt03.executeUpdate();
            
            // Number of columns in PurchaseMaster: 20
            /* pmid, ssid, compid, invno, invdt, netqty, netgross, netitemdisc, nettaxable, tradediscper, 
            nettradediscamt, replacementdiscper, netreplacementdiscamt, netgstamt, grosspayableamt, roundoff, netpayableamt, 
            isopening, amtpaid, isactive */
            String sql05 = "delete from PurchaseMaster where pmid=?";
            psmt05 = conn.prepareStatement(sql05);
            psmt05.setString(1, pmid);
            psmt05.executeUpdate();
            
            // Number of columns in PurchaseSub: 16
            /* psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, 
            taxableamt, gstamt, qtysold, retqty, tradediscamt, replacementdiscamt */
            String sql06 = "delete from PurchaseSub where pmid=?";
            psmt06 = conn.prepareStatement(sql06);
            psmt06.setString(1, pmid);
            psmt06.executeUpdate();
            
            conn.commit();
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"EditDeletePurchaseSub02 ex: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            try {
                conn.rollback();
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        }
        finally {
            if (psmt01 != null)  {
                try {
                    psmt01.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (psmt02 != null) {
                try {
                    psmt02.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (psmt03 != null) {
                try {
                    psmt03.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (psmt04 != null) {
                try {
                    psmt04.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (psmt05 != null) {
                try {
                    psmt05.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (psmt06 != null) {
                try {
                    psmt06.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e){}
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel34 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jTextField7 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();

        jMenuItem1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jMenuItem1.setText("### DELETED SELECTED ITEM ###");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem1);

        jMenuItem2.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jMenuItem2.setText("### EDIT SELECTED ITEM ###");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem2);

        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameIconified(evt);
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jLabel1.setBackground(new java.awt.Color(204, 204, 255));
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("N/A");
        jLabel1.setOpaque(true);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel2.setText("INV. NO.");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel4.setText("INV. DATE");

        jDateChooser1.setDateFormatString("dd/MM/yyyy");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel5.setText("SUPER STOCKIST");

        jLabel6.setBackground(new java.awt.Color(255, 255, 0));
        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("N/A");
        jLabel6.setOpaque(true);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel7.setText("TRADE DISCOUNT %");

        jLabel8.setBackground(new java.awt.Color(204, 255, 204));
        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("N/A");
        jLabel8.setOpaque(true);

        jTextField1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField1.setText("0");
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField1FocusLost(evt);
            }
        });
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel9.setText("REPLACEMENT DISCOUNT %");

        jTextField2.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField2.setText("0");
        jTextField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField2FocusLost(evt);
            }
        });
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField2KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField2KeyReleased(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 204, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 0), 2, true), "ITEMS PURCHASED", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 13), new java.awt.Color(0, 0, 153))); // NOI18N

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel10.setText("ITEM+<Enter>");

        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });
        jComboBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox1KeyPressed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel11.setText("HSN");

        jLabel12.setBackground(new java.awt.Color(255, 255, 0));
        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("N/A");
        jLabel12.setOpaque(true);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel13.setText("GST");

        jLabel14.setBackground(new java.awt.Color(255, 255, 0));
        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("0");
        jLabel14.setOpaque(true);

        jLabel15.setBackground(new java.awt.Color(255, 255, 0));
        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("%");
        jLabel15.setOpaque(true);

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel16.setText("EX-GST");

        jLabel17.setBackground(new java.awt.Color(255, 255, 0));
        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("0");
        jLabel17.setOpaque(true);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel18.setText("IN-GST");

        jLabel19.setBackground(new java.awt.Color(255, 255, 0));
        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("0");
        jLabel19.setOpaque(true);

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel20.setText("MRP");

        jLabel21.setBackground(new java.awt.Color(255, 255, 0));
        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("0");
        jLabel21.setOpaque(true);

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel22.setText("QTY.");

        jTextField3.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField3.setText("0");
        jTextField3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField3FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField3FocusLost(evt);
            }
        });
        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField3KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField3KeyReleased(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel23.setText("GROSS");

        jLabel24.setBackground(new java.awt.Color(255, 255, 0));
        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("0");
        jLabel24.setOpaque(true);

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel25.setText("DISCOUNT AMT.");

        jTextField4.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField4.setText("0");
        jTextField4.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField4FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField4FocusLost(evt);
            }
        });
        jTextField4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField4KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField4KeyReleased(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel26.setText("TRADE DISC. AMT.");

        jLabel27.setBackground(new java.awt.Color(255, 255, 0));
        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("0");
        jLabel27.setOpaque(true);

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel28.setText("REPL. DISC. AMT.");

        jLabel29.setBackground(new java.awt.Color(255, 255, 0));
        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("0");
        jLabel29.setOpaque(true);

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel30.setText("TAXABLE AMT.");

        jLabel31.setBackground(new java.awt.Color(255, 255, 0));
        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("0");
        jLabel31.setOpaque(true);

        jLabel32.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel32.setText("GST AMT.");

        jLabel33.setBackground(new java.awt.Color(255, 255, 0));
        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText("0");
        jLabel33.setOpaque(true);

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton1.setText("ADD");
        jButton1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, java.awt.Color.lightGray, java.awt.Color.black, null));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jButton1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton1KeyPressed(evt);
            }
        });

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "ADDED ITEMS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SLN.", "ITEM", "QTY.", "MRP", "EX-GST", "GROSS", "DISC. AMT.", "TD. AMT.", "RD. AMT.", "TAXABLE", "GST AMT."
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/SEARCH.PNG"))); // NOI18N
        jLabel34.setText("ITEM");

        jTextField5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField5KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField5KeyReleased(evt);
            }
        });

        jLabel35.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel35.setText("TOT. NO. OF ITEMS");

        jLabel36.setBackground(new java.awt.Color(255, 255, 0));
        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("0");
        jLabel36.setOpaque(true);

        jLabel37.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel37.setText("NET GROSS");

        jLabel38.setBackground(new java.awt.Color(255, 255, 0));
        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("0");
        jLabel38.setOpaque(true);

        jLabel39.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel39.setText("NET ITEM DISC.");

        jLabel40.setBackground(new java.awt.Color(255, 255, 0));
        jLabel40.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setText("0");
        jLabel40.setOpaque(true);

        jLabel41.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel41.setText("NET TRADE DISC. AMT.");

        jLabel42.setBackground(new java.awt.Color(255, 255, 0));
        jLabel42.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setText("0");
        jLabel42.setOpaque(true);

        jLabel43.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel43.setText("NET REPLACEMENT DISC. AMT.");

        jLabel44.setBackground(new java.awt.Color(255, 255, 0));
        jLabel44.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setText("0");
        jLabel44.setOpaque(true);

        jLabel45.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel45.setText("NET TAXABLE AMT.");

        jLabel46.setBackground(new java.awt.Color(0, 51, 204));
        jLabel46.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(255, 255, 255));
        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel46.setText("0");
        jLabel46.setOpaque(true);

        jLabel47.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel47.setText("NET GST AMT.");

        jLabel48.setBackground(new java.awt.Color(255, 255, 0));
        jLabel48.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel48.setText("0");
        jLabel48.setOpaque(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField3))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel43)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel45)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel47)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel34)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel37)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel39)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel41)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel10)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24)
                    .addComponent(jLabel25)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27)
                    .addComponent(jLabel28)
                    .addComponent(jLabel29)
                    .addComponent(jLabel30)
                    .addComponent(jLabel31)
                    .addComponent(jLabel32)
                    .addComponent(jLabel33)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35)
                    .addComponent(jLabel36)
                    .addComponent(jLabel37)
                    .addComponent(jLabel38)
                    .addComponent(jLabel39)
                    .addComponent(jLabel40)
                    .addComponent(jLabel41)
                    .addComponent(jLabel42))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel47)
                        .addComponent(jLabel48))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel43)
                        .addComponent(jLabel44)
                        .addComponent(jLabel45)
                        .addComponent(jLabel46)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel49.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel49.setText("GROSS PAYABLE AMT.");

        jLabel50.setBackground(new java.awt.Color(0, 0, 204));
        jLabel50.setForeground(new java.awt.Color(255, 255, 255));
        jLabel50.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel50.setText("0");
        jLabel50.setOpaque(true);

        jLabel51.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel51.setText("ROUND-OFF");

        jTextField6.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField6.setText("0");
        jTextField6.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField6FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField6FocusLost(evt);
            }
        });
        jTextField6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField6KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField6KeyReleased(evt);
            }
        });

        jLabel52.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel52.setText("NET PAYABLE AMT.");

        jLabel53.setBackground(new java.awt.Color(0, 0, 204));
        jLabel53.setForeground(new java.awt.Color(255, 255, 255));
        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel53.setText("0");
        jLabel53.setOpaque(true);

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton2.setText("SAVE");
        jButton2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, java.awt.Color.lightGray, java.awt.Color.black, null));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jButton2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton2KeyPressed(evt);
            }
        });

        jTextField7.setText("N/A");
        jTextField7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField7KeyPressed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton3.setForeground(new java.awt.Color(153, 0, 0));
        jButton3.setText("DELETE");
        jButton3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, java.awt.Color.lightGray, java.awt.Color.black, null));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jButton3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton3KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel49)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel51)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel52)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel53, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel8)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel52)
                        .addComponent(jLabel53)
                        .addComponent(jButton2)
                        .addComponent(jButton3))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel49)
                        .addComponent(jLabel50)
                        .addComponent(jLabel51)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField7KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField7.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField7KeyPressed

    private void jTextField1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusGained
        String s=jTextField1.getText().trim();
        if(Double.parseDouble(s) == 0.0) {
            jTextField1.setText("");
        } else {
            jTextField1.selectAll();
        }
    }//GEN-LAST:event_jTextField1FocusGained

    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusLost
        String s=jTextField1.getText().trim();
        if(s.length()==0) {
            jTextField1.setText("0");
        }
    }//GEN-LAST:event_jTextField1FocusLost

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        calculationForTradeNReplcDisc();
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jTextField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField2FocusGained
        String s=jTextField2.getText().trim();
        if(Double.parseDouble(s) == 0.0) {
            jTextField2.setText("");
        } else {
            jTextField2.selectAll();
        }
    }//GEN-LAST:event_jTextField2FocusGained

    private void jTextField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField2FocusLost
        String s=jTextField2.getText().trim();
        if(s.length()==0) {
            jTextField2.setText("0");
        }
    }//GEN-LAST:event_jTextField2FocusLost

    private void jTextField2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jComboBox1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField2KeyPressed

    private void jTextField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyReleased
        calculationForTradeNReplcDisc();
    }//GEN-LAST:event_jTextField2KeyReleased

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        jLabel12.setText("N/A");
        jLabel14.setText("0");
        jLabel17.setText("0");
        jLabel19.setText("0");
        jLabel21.setText("0");
        currentItemmid = null;
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jComboBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField2.requestFocusInWindow();
        }
        if (evt.getKeyCode() == KeyEvent.VK_F2)
        {
            addAlterItemDetails();
        }
        if (evt.getKeyCode() == KeyEvent.VK_F3)
        {
            jTextField4.requestFocusInWindow();
        }
        if (evt.getKeyCode() == KeyEvent.VK_F5)
        {
            if ( jComboBox1.getSelectedIndex() == 0 ) {
                JOptionPane.showMessageDialog(null,"Select Proper Item", "Error Found",JOptionPane.ERROR_MESSAGE);
                jComboBox1.requestFocusInWindow();
                return;
            }
            currentItemmid = itemmidArray[jComboBox1.getSelectedIndex()-1];
            getItemDetails();
        }
    }//GEN-LAST:event_jComboBox1KeyPressed

    private void jTextField3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField3FocusGained
        String s=jTextField3.getText().trim();
        if(Integer.parseInt(s) == 0) {
            jTextField3.setText("");
        } else {
            jTextField3.selectAll();
        }
    }//GEN-LAST:event_jTextField3FocusGained

    private void jTextField3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField3FocusLost
        String s=jTextField3.getText().trim();
        if(s.length()==0) {
            jTextField3.setText("0");
        }
    }//GEN-LAST:event_jTextField3FocusLost

    private void jTextField3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField4.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField3KeyPressed

    private void jTextField3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyReleased
        calculation01();
    }//GEN-LAST:event_jTextField3KeyReleased

    private void jTextField4FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField4FocusGained
        String s=jTextField4.getText().trim();
        if(Double.parseDouble(s) == 0) {
            jTextField4.setText("");
        } else {
            jTextField4.selectAll();
        }
    }//GEN-LAST:event_jTextField4FocusGained

    private void jTextField4FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField4FocusLost
        String s=jTextField4.getText().trim();
        if(s.length()==0) {
            jTextField4.setText("0");
        }
    }//GEN-LAST:event_jTextField4FocusLost

    private void jTextField4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jButton1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField4KeyPressed

    private void jTextField4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyReleased
        calculation01();
    }//GEN-LAST:event_jTextField4KeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        addToList();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            addToList();
        }
    }//GEN-LAST:event_jButton1KeyPressed

    private void jTextField5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField6.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField5KeyPressed

    private void jTextField5KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyReleased
        if (!psAl.isEmpty()) {
            String searchString = jTextField5.getText().trim().toUpperCase();
            dBConnection db=new dBConnection();
            Connection conn=db.setConnection();
            ArrayList<String> rows = new ArrayList<String>();
            int rowNo = 0;
            for (PurchaseSub ps: psAl) {
                String itemnm = null;
                // Number of columns in ItemMaster: 5
                /* itemmid, compid, itemnm, hsn, isactive */
                // Number of columns in ItemDetails: 10
                /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
                String query="select itemnm from ItemMaster, ItemDetails where ItemMaster.itemmid=ItemDetails.itemmid "
                        + "and ItemMaster.isactive=1 and ItemDetails.isactive=1 and itemdid="+ps.getItemdid();
                System.out.println(query);
                try
                {
                    Statement smt=conn.createStatement();
                    ResultSet rs=smt.executeQuery(query);
                    if (rs.next())
                    {
                        itemnm = rs.getString("itemnm");
                    }
                    if (itemnm.indexOf(searchString) >= 0) {
                        rows.add(rowNo+"");
                    }
                }
                catch(SQLException ex)
                {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,"EditDeletePurchaseSub02 ex: "+ex.getMessage(),
                            "Error Found",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                rowNo++;
            }
            if (searchString.length() == 0) {
                jTable1.getSelectionModel().clearSelection();
            } else {
                if (rows.size() > 0) {
                    jTable1.getSelectionModel().clearSelection();
                    for (String s:rows) {
                        jTable1.getSelectionModel().addSelectionInterval(Integer.parseInt(s), Integer.parseInt(s));
                    }
                } else {
                    jTable1.getSelectionModel().clearSelection();
                }
            }
        }
    }//GEN-LAST:event_jTextField5KeyReleased

    private void jTextField6FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField6FocusGained
        String s=jTextField6.getText().trim();
        if(Double.parseDouble(s) == 0) {
            jTextField6.setText("");
        } else {
            jTextField6.selectAll();
        }
    }//GEN-LAST:event_jTextField6FocusGained

    private void jTextField6FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField6FocusLost
        String s=jTextField6.getText().trim();
        if(s.length()==0) {
            jTextField6.setText("0");
        }
    }//GEN-LAST:event_jTextField6FocusLost

    private void jTextField6KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jButton2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField6KeyPressed

    private void jTextField6KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyReleased
        computation02();
    }//GEN-LAST:event_jTextField6KeyReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        insertToDatabase();
        setVisible(false);
        dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            insertToDatabase();
            setVisible(false);
            dispose();
        }
    }//GEN-LAST:event_jButton2KeyPressed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // DELETE SELECTED ITEM
        if(evt.getSource() == jMenuItem1)
        {
            if(jTable1.getSelectedRow()!=-1 && jTable1.getSelectedColumn()!=-1)
            {
                String ObjButtons[] = {"Yes","Cancel"};
                int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure to Delete the Item!",
                        "Delete Record",JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null,ObjButtons,ObjButtons[1]);
                if(PromptResult==0)
                {
                    int row = jTable1.getSelectedRow();
                    psAl.remove(row);
                    Fetch();
                    jComboBox1.requestFocusInWindow();
                }
                else
                    JOptionPane.showMessageDialog(null,"Action Discarded","Discard Information",JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
                JOptionPane.showMessageDialog(null,"Select an Item and then proceed!!!","Error Found",JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // EDIT SELECTED ITEM
        if(evt.getSource() == jMenuItem2)
        {
            if(jTable1.getSelectedRow()!=-1 && jTable1.getSelectedColumn()!=-1)
            {
                String ObjButtons[] = {"Yes","Cancel"};
                int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure to Edit the Item!",
                    "Edit Record",JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null,ObjButtons,ObjButtons[1]);
                if(PromptResult==0)
                {
                    int row = jTable1.getSelectedRow();
                    PurchaseSub ps = psAl.get(row);
                    currentItemdid = ps.getItemdid();
                    // Number of columns in PurchaseSub: 16
                    /* psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, 
                    taxableamt, gstamt, qtysold, retqty, tradediscamt, replacementdiscamt */
                    // Number of columns in ItemMaster: 5
                    /* itemmid, compid, itemnm, hsn, isactive */
                    // Number of columns in ItemDetails: 10
                    /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
                    String query="select itemnm, hsn from ItemMaster, ItemDetails"
                            + " where ItemMaster.itemmid=ItemDetails.itemmid and itemdid="+currentItemdid;
                    System.out.println(query);
                    dBConnection db=new dBConnection();
                    Connection conn=db.setConnection();
                    try
                    {
                        Statement stm=conn.createStatement();
                        ResultSet rs=stm.executeQuery(query);
                        if(rs.next())
                        {
                            jComboBox1.setSelectedItem(rs.getString("itemnm").replace("\\'", "'"));
                            jLabel12.setText(rs.getString("hsn"));
                        }
                    }
                    catch(SQLException ex)
                    {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null,"EditDeletePurchaseSub ex: "+ex,
                                "Error Found",JOptionPane.ERROR_MESSAGE);
                    }
                    finally
                    {
                        try {
                            if (conn!=null) conn.close();
                        }
                        catch(SQLException ex){}
                    }
                    jLabel14.setText(format.format(Double.parseDouble(ps.getGst())));
                    jLabel17.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getExgst())));
                    jLabel19.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getIngst())));
                    jLabel21.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getMrp())));
                    jTextField3.setText(ps.getQty());
                    jTextField4.setText(format.format(Double.parseDouble(ps.getDiscamt())));
                    calculation01();
                    psAl.remove(row);
                    Fetch();
                    computation02();
                    jComboBox1.requestFocusInWindow();
                }
                else
                JOptionPane.showMessageDialog(null,"Action Discarded","Discard Information",JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
                JOptionPane.showMessageDialog(null,"Select an Item and then proceed!!!","Error Found",JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // DELETE PURCHASE
        deletePurchase();
        setVisible(false);
        dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton3KeyPressed
        // DELETE PURCHASE
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            deletePurchase();
            setVisible(false);
            dispose();
        }
    }//GEN-LAST:event_jButton3KeyPressed

    private void formInternalFrameIconified(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameIconified
        moveToFront();
    }//GEN-LAST:event_formInternalFrameIconified


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBox1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    // End of variables declaration//GEN-END:variables
}
