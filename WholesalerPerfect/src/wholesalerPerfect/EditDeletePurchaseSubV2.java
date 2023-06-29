package wholesalerPerfect;

import com.toedter.calendar.JTextFieldDateEditor;
import conn.dBConnection;
import dto.PurchaseGSTV2;
import dto.PurchaseMasterV2;
import dto.PurchaseSubV2;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

public class EditDeletePurchaseSubV2 extends javax.swing.JInternalFrame implements AWTEventListener {

    private JDesktopPane jDesktopPane1;
    private UserProfile up;
    private boolean soldFlag;
    private Settings settings=new Settings();
    private DecimalFormat format = new DecimalFormat("0.#");
    private Query q=new Query();
    private DecimalFormat format2afterDecimal = new DecimalFormat("#.##");
    
    private boolean isFromOtherWindow;
    private PurchaseMasterV2 oldPm;
    private PurchaseMasterV2 pm;
    private String itemmidArray[];
    private double tradeDiscPer;
    private double replacementDiscPer;
    private ArrayList<PurchaseSubV2> psAl;
    private ArrayList<PurchaseGSTV2> pgstAl;
    private String currentItemmid;
    private String newItemDetails;
    private String selectedItemDetails;
    private String currentItemdid;
    private String currentCompid;
    
    public EditDeletePurchaseSubV2(JDesktopPane jDesktopPane1, UserProfile up, 
            PurchaseMasterV2 pm, boolean isFromOtherWindow, boolean soldFlag) {
        super("Edit/Delete Purchase",false,true,false,true);
        initComponents();
        this.jDesktopPane1 = jDesktopPane1;
        this.up = up;
        this.oldPm = q.getPurchaseMasterV2(pm.getPmid());
        this.isFromOtherWindow = isFromOtherWindow;
        this.soldFlag = soldFlag;
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
        map.put(stroke,"test");
        
        jDateChooser1.setDate(new Date());
        jDateChooser1.getToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
        jDateChooser1.getDateEditor().getUiComponent().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                ((JTextFieldDateEditor)evt.getSource()).selectAll();
            }
        });
        
        settings.numvalidatorFloat(jTextField2);
        settings.numvalidatorFloat(jTextField3);
        settings.numvalidatorInt(jTextField4);
        settings.numvalidatorFloat(jTextField5);
        settings.numvalidatorFloat(jTextField6);
        settings.numvalidatorFloatWithSign(jTextField8);
        settings.numvalidatorFloat(jTextField9);
        
        ((DefaultTableCellRenderer)jTable1.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        ((DefaultTableCellRenderer)jTable2.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        ((JLabel)jComboBox1.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        jTable1.setComponentPopupMenu(jPopupMenu1);
        
        populateCombo2();
        populateData();
        
        // The following buttons are disabled when item is sold from the Purchase Bill
        if(soldFlag) {
            jButton1.setEnabled(false); // Add To List Button
            jButton2.setEnabled(false); // Update Button
            jButton3.setEnabled(false); // Delete Button
        }
        
        SwingUtilities.invokeLater
        (() -> {
            jTextField1.requestFocusInWindow();
        });
    }
    
    @Override
    public void eventDispatched(AWTEvent event) {
        if(event instanceof KeyEvent) {
            KeyEvent key = (KeyEvent)event;
            if(key.getID()==KeyEvent.KEY_PRESSED) {
                if(event.getSource().equals(jDateChooser1.getDateEditor())&&key.getKeyCode()==10) {
                    jComboBox1.requestFocusInWindow();
                } 
            }
        }
    }
    
    private void populateData() {
        jLabel1.setText(Integer.parseInt(oldPm.getIsopening())==1?"OPENING":"REGULAR");
        jTextField1.setText(oldPm.getInvno());
        String iDate=oldPm.getInvdt();  
        Date date=null;
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(iDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"EditDeletePurchaseSubV2 ex: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        jDateChooser1.setDate(date);
        // Number of columns in SuperStockistMaster: 9
        /* ssid, compid, ssnm, addr1, addr2, gstno, contact, email, isactive */
        // Number of columns in CompanyMaster: 4
        /* compid, compnm, compabbr, isactive */
        String query1 = "SELECT ssnm, compnm FROM SuperStockistMaster ssm, CompanyMaster cm "
                + "WHERE ssm.compid=cm.compid AND ssm.isactive=1 AND cm.isactive=1 AND "
                + "ssm.ssid="+oldPm.getSsid();
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        int total = 0;
        try {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(query1);
            //Move to the last record
            rs.afterLast();
            //Get the current record position
            if(rs.previous())
                total = rs.getRow();
            //Move back to the first record;
            rs.beforeFirst();
            if(total != 0) {
                if(rs.next()) {
                    jLabel5.setText(rs.getString("ssnm"));
                    jLabel6.setText(rs.getString("compnm"));
                }
            }
        }
        catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"EditDeletePurchaseSubV2 ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e) {}
        }
        jTextField2.setText(format2afterDecimal.format(Double.parseDouble(oldPm.getTradediscper())));
        tradeDiscPer = Double.parseDouble(oldPm.getTradediscper());
        jTextField3.setText(format2afterDecimal.format(Double.parseDouble(oldPm.getReplacementdiscper())));
        replacementDiscPer = Double.parseDouble(oldPm.getReplacementdiscper());
        psAl = q.getAllPurchaseSubV2(oldPm.getPmid());
        jTextField8.setText(format2afterDecimal.format(Double.parseDouble(oldPm.getRoundoff())));
        jTextField9.setText(format2afterDecimal.format(Double.parseDouble(oldPm.getDispscheme())));
        currentCompid = oldPm.getCompid();
        populateCombo2();
        
        Fetch();
        
        jTextField1.requestFocusInWindow();
    }
    
    private void populateCombo2() { // Item Details
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in ItemMaster: 5
        /* itemmid, compid, itemnm, hsn, isactive */
        String query="select itemmid, itemnm from ItemMaster where isactive=1 "
                + "and compid="+currentCompid+" order by itemnm";
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
            if(total != 0) {
                itemmidArray=new String[total];
                jComboBox1.addItem("-- Select --");
                int i=0;
                while(rs.next()) {
                    itemmidArray[i++]=rs.getString("itemmid");
                    jComboBox1.addItem(rs.getString("itemnm"));
                }
            }
        }
        catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"EditDeletePurchaseSubV2 ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e) {}
        }
    }
    
    private void clearTable(JTable table) {
        for(int i=table.getRowCount()-1; i>=0; i--)
        {
            ((DefaultTableModel)table.getModel()).removeRow(i);
        }
    }
    
    private void Fetch() {
        int netqty = 0;
        double netdiscount = 0.0;
        double netamount = 0.0;
        
        int slno1=0;
        clearTable(jTable1);
        
        // Table PurchaseSubV2, no. of columns - 12
        /*
        psid, pmid, itemdid, mrp, gst, qty, rate, discper, discamt, amount, qtysold, retqty
        */
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        for (PurchaseSubV2 ps :  psAl) {
            Vector<String> row = new Vector<String>();
            row.addElement(++slno1+"");
            // No. of columns in table - 11
            /*
            SLN., PARTICULARS, MRP, HSN, GST %, QTY., UOM, RATE, DISC. %, DISC. AMT., AMOUNT
            */
            // Number of columns in ItemMaster: 5
            /* itemmid, compid, itemnm, hsn, isactive */
            // Number of columns in ItemDetails: 10
            /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
            String query="select itemnm, hsn, gst from ItemMaster, ItemDetails where "
                    + "ItemMaster.itemmid=ItemDetails.itemmid and ItemMaster.isactive=1 "
                    + "and ItemDetails.isactive=1 and itemdid="+ps.getItemdid();
            System.out.println(query);
            String hsn = "";
            String gst = "";
            try {
                Statement smt=conn.createStatement();
                ResultSet rs=smt.executeQuery(query);
                if (rs.next()) {
                    row.addElement(rs.getString("itemnm"));
                    hsn = rs.getString("hsn");
                    gst = rs.getString("gst");
                }
            }
            catch(SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"EditDeletePurchaseSubV2 ex: "+ex.getMessage(),
                        "Error Found",JOptionPane.ERROR_MESSAGE);
                return;
            }
            // No. of columns in table - 11
            /*
            SLN., PARTICULARS, MRP, HSN, GST %, QTY., UOM, RATE, DISC. %, DISC. AMT., AMOUNT
            */
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getMrp())));
            row.addElement(hsn);
            row.addElement(format2afterDecimal.format(Double.parseDouble(gst)));
            row.addElement(ps.getQty());
            row.addElement("PCS");
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getRate())));
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getDiscper())));
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getDiscamt())));
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getAmount())));
            
            netqty += Integer.parseInt(ps.getQty());
            netdiscount += Double.parseDouble(ps.getDiscamt());
            netamount += Double.parseDouble(ps.getAmount());
            
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
        // No. of columns in table - 11
        /*
        SLN., PARTICULARS, MRP, HSN, GST %, QTY., UOM, RATE, DISC. %, DISC. AMT., AMOUNT
        */
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN.
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(1).setMinWidth(0);// PARTICULARS
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(280);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);// MRP
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(110);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// HSN
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);// GST %
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);// QTY.
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(6).setMinWidth(0);// UOM
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(7).setMinWidth(0);// RATE
        jTable1.getColumnModel().getColumn(7).setPreferredWidth(110);
        jTable1.getColumnModel().getColumn(8).setMinWidth(0);// DISC. %
        jTable1.getColumnModel().getColumn(8).setPreferredWidth(110);
        jTable1.getColumnModel().getColumn(9).setMinWidth(0);// DISC. AMT.
        jTable1.getColumnModel().getColumn(9).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(10).setMinWidth(0);// AMOUNT
        jTable1.getColumnModel().getColumn(10).setPreferredWidth(110);
                
        // No. of columns in table - 11
        /*
        SLN., PARTICULARS, MRP, HSN, GST %, QTY., UOM, RATE, DISC. %, DISC. AMT., AMOUNT
        */
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable1.getColumn("SLN.").setCellRenderer( centerRenderer );
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        jTable1.getColumn("MRP").setCellRenderer( rightRenderer );
        jTable1.getColumn("GST %").setCellRenderer( rightRenderer );
        jTable1.getColumn("QTY.").setCellRenderer( rightRenderer );
        jTable1.getColumn("RATE").setCellRenderer( rightRenderer );
        jTable1.getColumn("DISC. %").setCellRenderer( rightRenderer );
        jTable1.getColumn("DISC. AMT.").setCellRenderer( rightRenderer );
        jTable1.getColumn("AMOUNT").setCellRenderer( rightRenderer );
        
        jLabel25.setText(format.format(netqty));
        jLabel27.setText(MyNumberFormat.rupeeFormat(netdiscount));
        jLabel28.setText(MyNumberFormat.rupeeFormat(netamount));
        
        computation02();
    }
    
    private  void computation02() {
        try {
            double netamount = Double.parseDouble(jLabel28.getText().trim().replaceAll(",", ""));
            // Trade Discount
            double tradediscper = Double.parseDouble(jTextField2.getText());
            double nettradediscamt = netamount * (tradediscper / 100.0);
            jLabel31.setText(MyNumberFormat.rupeeFormat(nettradediscamt));
            double amountAfterTradeDisc = netamount - nettradediscamt;
            // Replacement Discount After Trade Discount
            double replacementdiscper = Double.parseDouble(jTextField3.getText());
            double netreplacementdiscamt = amountAfterTradeDisc * (replacementdiscper / 100.0);
            jLabel32.setText(MyNumberFormat.rupeeFormat(netreplacementdiscamt));
            double amountAfterReplacementDisc = amountAfterTradeDisc - netreplacementdiscamt;
            // Group by GST calculation over gst%
            double[] uniqueGSTs = psAl.stream()
                .mapToDouble(f -> Double.parseDouble(f.getGst()))
                .distinct()
                .toArray();
            // Table PurchaseGSTV2, no. of columns - 5
            /*
            pgstid, pmid, gstper, taxableamt, gstamt
            */
            // Making ArrayList blank
            pgstAl = new ArrayList<PurchaseGSTV2>();
            double netgstamt = 0.0;
            for(double gstVal : uniqueGSTs) {
                double gstWiseNetAmt = psAl.stream()
                        .filter(f -> Double.parseDouble(f.getGst()) == gstVal)
                        .mapToDouble(f -> Double.parseDouble(f.getAmount()))
                        .sum();
                double gstWiseAmountAfterTradeDisc = gstWiseNetAmt - (gstWiseNetAmt * (tradediscper / 100.0));
                double gstWiseNetReplacementDiscAmt = gstWiseAmountAfterTradeDisc - 
                        (gstWiseAmountAfterTradeDisc * (replacementdiscper / 100.0));
                double gstWiseGstAmt = gstWiseNetReplacementDiscAmt * (gstVal / 100.0);
                netgstamt += gstWiseGstAmt;
                PurchaseGSTV2 pgst = new PurchaseGSTV2();
                pgst.setPgstid(""); // At Insert
                pgst.setPmid(""); // At Insert
                pgst.setGstper(format2afterDecimal.format(gstVal));
                pgst.setTaxableamt(format2afterDecimal.format(gstWiseNetReplacementDiscAmt));
                pgst.setGstamt(format2afterDecimal.format(gstWiseGstAmt));
                pgstAl.add(pgst);
            }
            FetchGSTData();
            double amountAfterGST = amountAfterReplacementDisc + netgstamt;
            jLabel35.setText(MyNumberFormat.rupeeFormat(amountAfterGST));
            double roundoff = Double.parseDouble(jTextField8.getText());
            double amountAfterRoundoff = amountAfterGST + roundoff;
            double displayscheme = Double.parseDouble(jTextField9.getText());
            double total = amountAfterRoundoff - displayscheme;
            jLabel38.setText(MyNumberFormat.rupeeFormat(total));
        } catch (Exception ex) { 
            ex.printStackTrace();
        }
    }
    
    private void FetchGSTData() {
        int slno1=0;
        clearTable(jTable2);
        
        // Table PurchaseGSTV2, no. of columns - 5
        /*
        pgstid, pmid, gstper, taxableamt, gstamt
        */
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        for (PurchaseGSTV2 pgst :  pgstAl) {
            Vector<String> row = new Vector<String>();
            // No. of columns in table - 5
            /*
            SLN., TYPE, %, TAXABLE AMT., GST AMT.
            */
            // Table PurchaseGSTV2, no. of columns - 5
            /*
            pgstid, pmid, gstper, taxableamt, gstamt
            */
            // For CGST
            row.addElement(++slno1+"");
            row.addElement("CGST");
            row.addElement(format2afterDecimal.format(Double.parseDouble(pgst.getGstper()) / 2.0));
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(pgst.getTaxableamt())));
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(pgst.getGstamt()) / 2.0));
            ((DefaultTableModel)jTable2.getModel()).addRow(row);
            // For SGST
            row = new Vector<String>();
            row.addElement(++slno1+"");
            row.addElement("SGST");
            row.addElement(format2afterDecimal.format(Double.parseDouble(pgst.getGstper()) / 2.0));
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(pgst.getTaxableamt())));
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(pgst.getGstamt()) / 2.0));
            ((DefaultTableModel)jTable2.getModel()).addRow(row);
        }
        try {
            if (conn!=null) conn.close();
        }
        catch(SQLException e){}

        jTable2.setDragEnabled(false);
        // Disable auto resizing
        jTable2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableHeader header = jTable2.getTableHeader();
        header.setBackground(Color.cyan);
        //Start resize the table column
        // No. of columns in table - 5
        /*
        SLN., TYPE, GST %, TAXABLE AMT., GST AMT.
        */
        jTable2.getColumnModel().getColumn(0).setMinWidth(0);// SLN.
        jTable2.getColumnModel().getColumn(0).setPreferredWidth(150);
        jTable2.getColumnModel().getColumn(1).setMinWidth(0);// TYPE
        jTable2.getColumnModel().getColumn(1).setPreferredWidth(260);
        jTable2.getColumnModel().getColumn(2).setMinWidth(0);// %
        jTable2.getColumnModel().getColumn(2).setPreferredWidth(260);
        jTable2.getColumnModel().getColumn(3).setMinWidth(0);// TAXABLE AMT.
        jTable2.getColumnModel().getColumn(3).setPreferredWidth(260);
        jTable2.getColumnModel().getColumn(4).setMinWidth(0);// GST AMT.
        jTable2.getColumnModel().getColumn(4).setPreferredWidth(260);
                
        // No. of columns in table - 5
        /*
        SLN., TYPE, GST %, TAXABLE AMT., GST AMT.
        */
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable2.getColumn("SLN.").setCellRenderer( centerRenderer );
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        jTable2.getColumn("GST %").setCellRenderer( rightRenderer );
        jTable2.getColumn("TAXABLE AMT.").setCellRenderer( rightRenderer );
        jTable2.getColumn("GST AMT.").setCellRenderer( rightRenderer );
    }
    
    private void addAlterItemDetails() {
        Thread t = new Thread(() -> {
            try {
                setVisible(false);
                final ItemDetails ref=new ItemDetails(jDesktopPane1, true, up);
                ref.addInternalFrameListener(new InternalFrameAdapter() {
                    @Override
                    public void internalFrameDeactivated(InternalFrameEvent e) {
                        newItemDetails=ref.getNewItemDetails();
                    }
                    @Override
                    public void internalFrameClosed(InternalFrameEvent e) {
                        EditDeletePurchaseSubV2.this.setVisible(true);
                        if(newItemDetails!=null) {
                            String newItemnm = "";
                            // itemdid+"~"+itemmid+"~"+mrp+"~"+gst+"~"+pexgst+"~"+pingst+"~"+sexgst+"~"+singst
                            // Number of columns in ItemMaster: 5
                            /* itemmid, compid, itemnm, hsn, isactive */
                            String s[] = newItemDetails.split("~");
                            dBConnection db=new dBConnection();
                            Connection conn=db.setConnection();
                            String query="select itemnm from ItemMaster where isactive=1 and itemmid="+s[1];
                            System.out.println(query);
                            try {
                                Statement smt=conn.createStatement();
                                ResultSet rs=smt.executeQuery(query);
                                if(rs.next()) {
                                    newItemnm = rs.getString("itemnm");
                                }
                            }
                            catch(SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(null,"EditDeletePurchaseSubV2 ex: "+ex.getMessage(),
                                        "SQL Error Found",JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            finally {
                                try {
                                    if (conn!=null) conn.close();
                                }
                                catch(SQLException ex) {}
                            }
                            populateCombo2();
                            
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
        });
        t.start();
        try {
            t.join();
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    private void getItemDetails() {
        Thread t = new Thread(() -> {
            try
            {
                setVisible(false);
                final ItemDetails4Purchase ref=new ItemDetails4Purchase(currentItemmid);
                ref.addInternalFrameListener(new InternalFrameAdapter() {
                    @Override
                    public void internalFrameDeactivated(InternalFrameEvent e) {
                        selectedItemDetails=ref.getSelectedItemDetails();
                    }
                    @Override
                    public void internalFrameClosed(InternalFrameEvent e) {
                        EditDeletePurchaseSubV2.this.setVisible(true);
                        if(selectedItemDetails!=null) {
                            //     0        1       2       3        4          5          6          7          8
                            // itemdid+"~"+hsn+"~"+mrp+"~"+gst+"~"+pexgst+"~"+pingst+"~"+sexgst+"~"+singst+"~"+onhand
                            String s[] = selectedItemDetails.split("~");
                            currentItemdid = s[0];
                            jLabel11.setText(s[2]); //mrp
                            jLabel12.setText(s[1]); //hsn
                            jLabel14.setText(s[3]); //gst
                            jLabel18.setText(s[4]); //purchase ex-gst - Now reffered as RATE
                            
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
        });
        t.start();
        try {
            t.join();
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    private void calculation01() {
        try {
            double exgst = Double.parseDouble(jLabel18.getText().trim().replaceAll(",", ""));
            int qty = Integer.parseInt(jTextField4.getText());
            double gross = exgst * qty;
            double discountper = Double.parseDouble(jTextField5.getText());
            double discountAmt = 0.0;
            if (discountper > 0.0) {
                discountAmt = gross * (discountper / 100.0);
                jTextField6.setText(format2afterDecimal.format(discountAmt));
            } else {
                discountAmt = Double.parseDouble(jTextField6.getText());
            }
            double amount = gross - discountAmt;
            jLabel22.setText(MyNumberFormat.rupeeFormat(amount));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void addToList() {
        // Table PurchaseSubV2, no. of columns - 13
        /*
        psid, pmid, itemdid, mrp, gst, qty, rate, discper, discamt, amount, qtysold, retqty
        */
        String itemdid = currentItemdid;
        if(itemdid == null) {
            JOptionPane.showMessageDialog(null,"Select the Item Details (F5 from item selection) !!!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox1.requestFocusInWindow();
            return;
        }
        String mrp = jLabel11.getText().trim().replaceAll(",", "");
        String gst = jLabel14.getText().trim().replaceAll(",", "");
        String qty = jTextField4.getText();
        if(Integer.parseInt(qty) == 0)
        {
            JOptionPane.showMessageDialog(null,"Quantity is mandatory !!!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField4.requestFocusInWindow();
            return;
        }
        String rate = jLabel18.getText().trim().replaceAll(",", "");
        String discper = jTextField5.getText();
        String discamt = jTextField6.getText();
        String amount = jLabel22.getText().trim().replaceAll(",", "");
        String qtysold = "0";
        String retqty = "0";
        
        PurchaseSubV2 ps = new PurchaseSubV2();
        // Table PurchaseSubV2, no. of columns - 13
        /*
        psid, pmid, itemdid, mrp, gst, qty, rate, discper, discamt, amount, qtysold, retqty
        */
        ps.setPsid(""); // At Insert
        ps.setPmid(""); // At Insert
        ps.setItemdid(itemdid);
        ps.setMrp(mrp);
        ps.setGst(gst);
        ps.setQty(qty);
        ps.setRate(rate);
        ps.setDiscper(discper);
        ps.setDiscamt(discamt);
        ps.setAmount(amount);
        ps.setQtysold(qtysold);
        ps.setRetqty(retqty);
        psAl.add(ps);
        
        Fetch();

        itemDetailsFormFlush();
        jComboBox1.setSelectedIndex(0);
        jComboBox1.requestFocusInWindow();
    }
    
    private void itemDetailsFormFlush() {
        currentItemdid = null;
        newItemDetails = null;
        selectedItemDetails = null;
        
        jLabel11.setText("0");
        jLabel12.setText("N/A");
        jLabel14.setText("0");
        jTextField4.setText("0");
        jLabel18.setText("0");
        jTextField5.setText("0");
        jTextField6.setText("0");
        jLabel22.setText("0");
    }
    
    private void updateToDatabase() {
        if(psAl.stream()
                .filter(x -> Integer.parseInt(x.getRetqty())>0)
                .count()>0) {
            JOptionPane.showMessageDialog(null,"Some items are already sold, hence unable to update!!!",
                    "Unable to update",JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (psAl.isEmpty()) {
            JOptionPane.showMessageDialog(null,"Incomplete Data !!!","Incomplete Data",JOptionPane.ERROR_MESSAGE);
            jComboBox1.requestFocusInWindow();
            return;
        }
        // Supre Stockist ID (ssid) can not be altered
        String invno=jTextField1.getText().trim().toUpperCase();
        if(invno.equals("N/A")) {
            JOptionPane.showMessageDialog(null,"Enter the Invoice No.!","Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField1.requestFocusInWindow();
            return;
        }
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
        Date invDt =jDateChooser1.getDate();
        String invdt=null;
        try {
            invdt=sdf.format(invDt);
        }
        catch(NullPointerException ex) {
            JOptionPane.showMessageDialog(null,"Invalid Purchase Date.","Invalid Date",JOptionPane.ERROR_MESSAGE);
            jDateChooser1.requestFocusInWindow();
            return;
        }
        
        // Table PurchaseMasterV2, no. of columns - 20
        /*
        pmid, ssid, compid, invno, invdt, netqty, netitemdisc, netgross, tradediscper, nettradediscamt, 
        replacementdiscper, netreplacementdiscamt, netgstamt, amtaftergst, roundoff, dispscheme, 
        netpayableamt, isopening, amtpaid, isactive
        */
        String netqty = jLabel25.getText().trim().replaceAll(",", "");
        String netitemdisc = jLabel27.getText().trim().replaceAll(",", "");
        String netgross = jLabel28.getText().trim().replaceAll(",", "");
        String tradediscper = jTextField2.getText();
        String nettradediscamt = jLabel31.getText().trim().replaceAll(",", "");
        String replacementdiscper = jTextField3.getText();
        String netreplacementdiscamt = jLabel32.getText().trim().replaceAll(",", "");
        String netgstamt = format2afterDecimal.format(pgstAl.stream()
                .mapToDouble(f -> Double.parseDouble(f.getGstamt())).sum());
        String amtaftergst = jLabel35.getText().trim().replaceAll(",", "");
        String roundoff = jTextField8.getText();
        String dispscheme = jTextField9.getText();
        String netpayableamt = jLabel38.getText().trim().replaceAll(",", "");
        String isopening = oldPm.getIsopening();
        String amtpaid = netpayableamt;
        String isactive = "1";

        // Table PurchaseMasterV2, no. of columns - 20
        /*
        pmid, ssid, compid, invno, invdt, netqty, netitemdisc, netgross, tradediscper, nettradediscamt, 
        replacementdiscper, netreplacementdiscamt, netgstamt, amtaftergst, roundoff, dispscheme, 
        netpayableamt, isopening, amtpaid, isactive
        */
        pm = new PurchaseMasterV2();
        pm.setPmid(""); // At Insert
        pm.setSsid(this.oldPm.getSsid());
        pm.setCompid(this.oldPm.getCompid());
        pm.setInvno(invno);
        pm.setInvdt(invdt);
        pm.setNetqty(netqty);
        pm.setNetitemdisc(netitemdisc);
        pm.setNetgross(netgross);
        pm.setTradediscper(tradediscper);
        pm.setNettradediscamt(nettradediscamt);
        pm.setReplacementdiscper(replacementdiscper);
        pm.setNetreplacementdiscamt(netreplacementdiscamt);
        pm.setNetgstamt(netgstamt);
        pm.setAmtaftergst(amtaftergst);
        pm.setRoundoff(roundoff);
        pm.setDispscheme(dispscheme);
        pm.setNetpayableamt(netpayableamt);
        pm.setIsopening(isopening);
        pm.setAmtpaid(amtpaid);
        pm.setIsactive(isactive);
        pm.setPsAl(psAl);
        pm.setPgstAl(pgstAl);

        if(q.deleteFromPurchaseMasterV2(oldPm)) {
            int pmid = q.insertToPurchaseMasterV2(pm);
            if (pmid > 0) {
                pm = q.getPurchaseMasterV2(pmid+"");

                dBConnection db=new dBConnection();
                Connection conn=db.setConnection();

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
                try {
                    conn.setAutoCommit(false);
                    
                    int pervqty = 0;
                    // Number of columns in ItemDetails: 10
                    /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
                    String sql1 = "select onhand from ItemDetails where itemdid=?";
                    psmt1 = conn.prepareStatement(sql1);
                    // Number of columns in PurchaseSub: 16
                    /* psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, 
                    taxableamt, gstamt, qtysold, retqty, tradediscamt, replacementdiscamt */
                    // Number of columns in ItemDetails: 10
                    /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
                    String sql2 = "update ItemDetails set onhand=onhand+? where itemdid=?";
                    psmt2 = conn.prepareStatement(sql2);
                    // Number of columns in ItemLedger: 9
                    /* ilid, itemdid, tablenm, pknm, pkval, actiondt, type, prevqty, qty */
                    String sql3 = "insert into ItemLedger (ilid, itemdid, tablenm, pknm, pkval, actiondt, "
                            + "type, prevqty, qty) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    psmt3 = conn.prepareStatement(sql3);
                    
                    HashMap<String, Integer> hashMap = new HashMap<>();
                    HashMap<Integer, Integer> itemDetailsHashMap = new HashMap<>();

                    for( PurchaseSubV2 ref : pm.getPsAl() ) {
                        psmt1.setInt(1, Integer.parseInt(ref.getItemdid()));
                        ResultSet rs = psmt1.executeQuery();
                        if (rs.next()) {
                            pervqty = Integer.parseInt(rs.getString("onhand"));
                        }

                        // Creating the HashMap to identify items with same itemdid
                        if (hashMap.isEmpty()) {
                            hashMap.put(ref.getItemdid(), pervqty + Integer.parseInt(ref.getQty()));
                        } else {
                            boolean flag = true;
                            for (String key : hashMap.keySet()) {
                                if (key.equals(ref.getItemdid())) {
                                    flag = false;
                                    pervqty = hashMap.get(key);
                                    hashMap.put(key, hashMap.get(key) + Integer.parseInt(ref.getQty()));
                                }
                            }
                            if (flag) {
                                hashMap.put(ref.getItemdid(), pervqty + Integer.parseInt(ref.getQty()));
                            }
                        }
                        
                        // Creating HashMap for ItemDetails
                        if (itemDetailsHashMap.isEmpty()) {
                            itemDetailsHashMap.put(Integer.parseInt(ref.getItemdid()), 
                                    Integer.parseInt(ref.getQty()));
                        } else {
                            boolean flag = true;
                            for (Integer key : itemDetailsHashMap.keySet()) {
                                if (key == Integer.parseInt(ref.getItemdid())) {
                                    flag = false;
                                    itemDetailsHashMap.put(key, itemDetailsHashMap.get(key) + 
                                        Integer.parseInt(ref.getQty()));
                                }
                            }
                            if (flag) {
                                itemDetailsHashMap.put(Integer.parseInt(ref.getItemdid()), 
                                    Integer.parseInt(ref.getQty()));
                            }
                        }
                        
//                        psmt2.setInt(1, pervqty + Integer.parseInt(ref.getQty()));
//                        psmt2.setInt(2, Integer.parseInt(ref.getItemdid()));
//                        psmt2.addBatch();
//                        psmt2.executeUpdate();

                        psmt3.setInt(1, ++ilid);
                        psmt3.setInt(2, Integer.parseInt(ref.getItemdid()));
                        psmt3.setString(3, "PurchaseSubV2");
                        psmt3.setString(4, "psid");
                        psmt3.setString(5, ref.getPsid());
                        psmt3.setDate(6, java.sql.Date.valueOf(DateConverter.dateConverter1(invdt)));
                        psmt3.setString(7, "ADD");
                        psmt3.setInt(8, pervqty);
                        psmt3.setInt(9, Integer.parseInt(ref.getQty()));
                        psmt3.addBatch();
                    }
                    psmt3.executeBatch();
                    
                    for (Map.Entry<Integer,Integer> entry : itemDetailsHashMap.entrySet()) {
                        System.out.println("Purchase-----------> Key = " + entry.getKey() + ", Value = " + entry.getValue());
                        psmt2.setInt(1, entry.getValue());
                        psmt2.setInt(2, entry.getKey());
                        psmt2.addBatch();
                    }
                    psmt2.executeBatch();

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
                catch(SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,"EditDeletePurchaseSubV2 ex: "+ex.getMessage(),
                            "Error Found",JOptionPane.ERROR_MESSAGE);
                    try {
                        conn.rollback();
                    } catch (SQLException ex1) {
                        ex1.printStackTrace();
                    }
                }
                finally {
                    if (psmt1 != null) {
                        try {
                            psmt1.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (psmt2 != null) {
                        try {
                            psmt2.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (psmt3 != null) {
                        try {
                            psmt3.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (psmt4 != null) {
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
                setVisible(false);
                dispose();
            }
        } else {
            JOptionPane.showMessageDialog(null,"Unable to Edit Purchase",
                    "Edit Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void formFlush() {
        currentCompid = null;
        tradeDiscPer = 0.0;
        replacementDiscPer = 0.0;
        psAl = null;
        pgstAl = null;
        
        jLabel1.setText("N/A");
        jTextField1.setText("");
        jDateChooser1.setDate(new Date());
        jLabel5.setText("N/A");
        jLabel6.setText("N/A");
        jTextField2.setText("0");
        jTextField3.setText("0");
        
        clearTable(jTable1);
        itemDetailsFormFlush();
        
        jTextField7.setText("");
        jLabel25.setText("0");
        jLabel27.setText("0");
        jLabel28.setText("0");
        
        jLabel31.setText("0");
        jLabel32.setText("0");
        clearTable(jTable2);
        jLabel35.setText("0");
        jTextField8.setText("0");
        jTextField9.setText("0");
        jLabel38.setText("0");
        
        jTextField1.requestFocusInWindow();
    }
    
    private void deleteFromDatabase() {
        String ObjButtons[] = {"Yes","Cancel"};
        int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure to Delete the Purchase details!",
            "Delete Record",JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null,ObjButtons,ObjButtons[1]);
        if(PromptResult==0)
        {
            if(q.deleteFromPurchaseMasterV2(oldPm)) {
                JOptionPane.showMessageDialog(null,"Purchase Details Successfully Deleted",
                            "Purchase Deleted",JOptionPane.INFORMATION_MESSAGE);
                setVisible(false);
                dispose();
            } else {
                JOptionPane.showMessageDialog(null,"Unable to Delete Purchase Details",
                            "Error Found",JOptionPane.ERROR_MESSAGE);
            }
        }
        else
        JOptionPane.showMessageDialog(null,"Action Discarded","Discard Information",JOptionPane.INFORMATION_MESSAGE);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel23 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        jMenuItem1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jMenuItem1.setText("### DELETE SELECTED ITEM ###");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem1);

        jMenuItem2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
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

        jLabel1.setBackground(new java.awt.Color(255, 255, 204));
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("N/A");
        jLabel1.setOpaque(true);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("INV. NO.");

        jTextField1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField1.setText("N/A");
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField1FocusGained(evt);
            }
        });
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("INV. DATE");

        jDateChooser1.setDateFormatString("dd/MM/yyyy");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("SUPER STOCKIST");

        jLabel5.setBackground(new java.awt.Color(255, 255, 204));
        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("N/A");
        jLabel5.setOpaque(true);

        jLabel6.setBackground(new java.awt.Color(255, 255, 204));
        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("N/A");
        jLabel6.setOpaque(true);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setText("TRADE DISC. %");

        jTextField2.setEditable(false);
        jTextField2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField2.setText("0");
        jTextField2.setFocusable(false);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setText("REPLACEMENT DISC. %");

        jTextField3.setEditable(false);
        jTextField3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField3.setText("0");
        jTextField3.setFocusable(false);

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "ITEM PURCHASED", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(0, 0, 102))); // NOI18N

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setText("SELECT ITEM+<F5>");

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

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("MRP");

        jLabel11.setBackground(new java.awt.Color(255, 255, 204));
        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("0");
        jLabel11.setOpaque(true);

        jLabel12.setBackground(new java.awt.Color(255, 255, 204));
        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("N/A");
        jLabel12.setOpaque(true);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel13.setText("HSN");

        jLabel14.setBackground(new java.awt.Color(255, 255, 204));
        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("N/A");
        jLabel14.setOpaque(true);

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel15.setText("GST %");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel16.setText("QTY.");

        jTextField4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
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

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel17.setText("RATE");

        jLabel18.setBackground(new java.awt.Color(255, 255, 204));
        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("N/A");
        jLabel18.setOpaque(true);

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel19.setText("DISC. %");

        jTextField5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField5.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField5.setText("0");
        jTextField5.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField5FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField5FocusLost(evt);
            }
        });
        jTextField5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField5KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField5KeyReleased(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel20.setText("DISC. AMOUNT");

        jTextField6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
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

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel21.setText("AMOUNT");

        jLabel22.setBackground(new java.awt.Color(255, 255, 204));
        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("0");
        jLabel22.setOpaque(true);

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton1.setText("ADD TO LIST");
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
                "SLN.", "PARTICULARS", "MRP", "HSN", "GST %", "QTY.", "UOM", "RATE", "DISC. %", "DISC. AMT.", "AMOUNT"
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

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/SEARCH.PNG"))); // NOI18N
        jLabel23.setText("ITEM SEARCH");

        jTextField7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField7KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField7KeyReleased(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel24.setText("TOTAL NO. OF ITEMS");

        jLabel25.setBackground(new java.awt.Color(255, 255, 204));
        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("0");
        jLabel25.setOpaque(true);

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel26.setText("NET DISC. AMT.");

        jLabel27.setBackground(new java.awt.Color(255, 255, 204));
        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("0");
        jLabel27.setOpaque(true);

        jLabel28.setBackground(new java.awt.Color(255, 255, 204));
        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("0");
        jLabel28.setOpaque(true);

        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel29.setText("NET AMT.");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel29)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel13)
                    .addComponent(jLabel12)
                    .addComponent(jLabel15)
                    .addComponent(jLabel14)
                    .addComponent(jLabel16)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jButton1)
                    .addComponent(jLabel19)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel23)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(jLabel25)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27)
                    .addComponent(jLabel29)
                    .addComponent(jLabel28))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel30.setText("TRADE DISC. AMT.");
        jLabel30.setOpaque(true);

        jLabel31.setBackground(new java.awt.Color(255, 255, 204));
        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("0");
        jLabel31.setOpaque(true);

        jLabel32.setBackground(new java.awt.Color(255, 255, 204));
        jLabel32.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText("0");
        jLabel32.setOpaque(true);

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel33.setText("REPLACEMENT DISC. AMT.");
        jLabel33.setOpaque(true);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 51, 255), 2, true), "GST DETAILS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SLN.", "TYPE", "GST %", "TAXABLE AMT.", "GST AMT."
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel34.setText("PAYMENT AMOUNT");

        jLabel35.setBackground(new java.awt.Color(255, 255, 204));
        jLabel35.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText("0");
        jLabel35.setOpaque(true);

        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel36.setText("ROUND OFF");

        jTextField8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField8.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField8.setText("0");
        jTextField8.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField8FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField8FocusLost(evt);
            }
        });
        jTextField8.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField8KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField8KeyReleased(evt);
            }
        });

        jTextField9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField9.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField9.setText("0");
        jTextField9.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField9FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField9FocusLost(evt);
            }
        });
        jTextField9.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField9KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField9KeyReleased(evt);
            }
        });

        jLabel37.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel37.setText("DISPLAY SCHEME");

        jLabel38.setBackground(new java.awt.Color(255, 255, 204));
        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("0");
        jLabel38.setOpaque(true);

        jLabel39.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel39.setText("TOTAL");

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/MODIFY.PNG"))); // NOI18N
        jButton2.setText("UPDATE");
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

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        jButton3.setText("DELETE");
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
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel33)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(27, 27, 27)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(jLabel31)
                    .addComponent(jLabel33)
                    .addComponent(jLabel32))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel39)
                        .addComponent(jLabel38)
                        .addComponent(jButton2)
                        .addComponent(jButton3))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel37)
                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel34)
                        .addComponent(jLabel35)
                        .addComponent(jLabel36)
                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jDateChooser1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        jLabel11.setText("0"); // MRP
        jLabel12.setText("N/A"); // HSN
        jLabel14.setText("0"); // GST
        jLabel18.setText("0"); // RATE
        currentItemmid = null;
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jComboBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTextField4.requestFocusInWindow();
        }
        if (evt.getKeyCode() == KeyEvent.VK_F2) {
            addAlterItemDetails();
        }
        if (evt.getKeyCode() == KeyEvent.VK_F3)
        {
            jTextField8.requestFocusInWindow();
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

    private void jTextField4FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField4FocusGained
        String s=jTextField4.getText().trim();
        if(Integer.parseInt(s) == 0) {
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
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTextField5.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField4KeyPressed

    private void jTextField4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyReleased
        calculation01();
    }//GEN-LAST:event_jTextField4KeyReleased

    private void jTextField5FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField5FocusGained
        String s=jTextField5.getText().trim();
        if(Double.parseDouble(s) == 0) {
            jTextField5.setText("");
        } else {
            jTextField5.selectAll();
        }
    }//GEN-LAST:event_jTextField5FocusGained

    private void jTextField5FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField5FocusLost
        String s=jTextField5.getText().trim();
        if(s.length()==0) {
            jTextField5.setText("0");
        }
    }//GEN-LAST:event_jTextField5FocusLost

    private void jTextField5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTextField6.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField5KeyPressed

    private void jTextField5KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyReleased
        calculation01();
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
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jButton1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField6KeyPressed

    private void jTextField6KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyReleased
        calculation01();
    }//GEN-LAST:event_jTextField6KeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        addToList();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            addToList();
        }
    }//GEN-LAST:event_jButton1KeyPressed

    private void jTextField8FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField8FocusGained
        String s=jTextField8.getText().trim();
        if(Double.parseDouble(s) == 0) {
            jTextField8.setText("");
        } else {
            jTextField8.selectAll();
        }
    }//GEN-LAST:event_jTextField8FocusGained

    private void jTextField8FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField8FocusLost
        String s=jTextField8.getText().trim();
        if(s.length()==0) {
            jTextField8.setText("0");
        }
    }//GEN-LAST:event_jTextField8FocusLost

    private void jTextField8KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField8KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTextField9.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField8KeyPressed

    private void jTextField8KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField8KeyReleased
        computation02();
    }//GEN-LAST:event_jTextField8KeyReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        updateToDatabase();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            updateToDatabase();
        }
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            jButton3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jButton2KeyPressed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        deleteFromDatabase();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            deleteFromDatabase();
        }
        if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            jButton2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jButton3KeyPressed

    private void jTextField7KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTextField8.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField7KeyPressed

    private void jTextField7KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyReleased
        if (!psAl.isEmpty()) {
            String searchString = jTextField7.getText().trim().toUpperCase();
            dBConnection db=new dBConnection();
            Connection conn=db.setConnection();
            ArrayList<String> rows = new ArrayList<String>();
            int rowNo = 0;
            for (PurchaseSubV2 ps: psAl) {
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
                    JOptionPane.showMessageDialog(null,"EditDeletePurchaseSubV2 ex: "+ex.getMessage(),
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
    }//GEN-LAST:event_jTextField7KeyReleased

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // DELETE SELECTED ITEM
        if(evt.getSource() == jMenuItem1) {
            if(jTable1.getSelectedRow()!=-1 && jTable1.getSelectedColumn()!=-1) {
                String ObjButtons[] = {"Yes","Cancel"};
                int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure to Delete the Item!",
                        "Delete Record",JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,null,ObjButtons,ObjButtons[1]);
                if(PromptResult==0){
                    int row = jTable1.getSelectedRow();
                    psAl.remove(row);
                    Fetch();
                    jComboBox1.requestFocusInWindow();
                } else {
                    JOptionPane.showMessageDialog(null,"Action Discarded","Discard Information",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null,"Select an Item and then proceed!!!",
                        "Error Found",JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // EDIT SELECTED ITEM
        if(evt.getSource() == jMenuItem2) {
            if(jTable1.getSelectedRow()!=-1 && jTable1.getSelectedColumn()!=-1) {
                String ObjButtons[] = {"Yes","Cancel"};
                int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure to Edit the Item!",
                    "Edit Record",JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,null,ObjButtons,ObjButtons[1]);
                if(PromptResult==0) {
                    int row = jTable1.getSelectedRow();
                    PurchaseSubV2 ps = psAl.get(row);
                    currentItemdid = ps.getItemdid();
                    // Table PurchaseSubV2, no. of columns - 12
                    /*
                    psid, pmid, itemdid, mrp, gst, qty, rate, discper, discamt, amount, qtysold, retqty
                    */
                    // Number of columns in ItemMaster: 5
                    /* itemmid, compid, itemnm, hsn, isactive */
                    // Number of columns in ItemDetails: 10
                    /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
                    String query="select itemnm, hsn from ItemMaster, ItemDetails"
                            + " where ItemMaster.itemmid=ItemDetails.itemmid and itemdid="+currentItemdid;
                    System.out.println(query);
                    dBConnection db=new dBConnection();
                    Connection conn=db.setConnection();
                    try {
                        Statement stm=conn.createStatement();
                        ResultSet rs=stm.executeQuery(query);
                        if(rs.next()) {
                            jComboBox1.setSelectedItem(rs.getString("itemnm").replace("\\'", "'"));
                            jLabel12.setText(rs.getString("hsn"));
                        }
                    }
                    catch(SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null,"EditDeletePurchaseSubV2 ex: "+ex,
                                "Error Found",JOptionPane.ERROR_MESSAGE);
                    }
                    finally {
                        try {
                            if (conn!=null) conn.close();
                        }
                        catch(SQLException ex){}
                    }
                    jLabel11.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getMrp())));
                    jLabel14.setText(format2afterDecimal.format(Double.parseDouble(ps.getGst())));
                    jTextField4.setText(ps.getQty());
                    jLabel18.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getRate())));
                    jTextField5.setText(format2afterDecimal.format(Double.parseDouble(ps.getDiscper())));
                    jTextField6.setText(format2afterDecimal.format(Double.parseDouble(ps.getDiscamt())));
                    jLabel22.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getAmount())));
                    calculation01();
                    psAl.remove(row);
                    Fetch();
                    computation02();
                    jComboBox1.requestFocusInWindow();
                } else {
                    JOptionPane.showMessageDialog(null,"Action Discarded","Discard Information",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null,"Select an Item and then proceed!!!",
                        "Error Found",JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jTextField1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusGained
        String s=jTextField1.getText().trim();
        if(s.length() != 0) {
            jTextField1.selectAll();
        }
    }//GEN-LAST:event_jTextField1FocusGained

    private void jTextField9FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField9FocusGained
        String s=jTextField9.getText().trim();
        if(Double.parseDouble(s) == 0) {
            jTextField9.setText("");
        } else {
            jTextField9.selectAll();
        }
    }//GEN-LAST:event_jTextField9FocusGained

    private void jTextField9FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField9FocusLost
        String s=jTextField9.getText().trim();
        if(s.length()==0) {
            jTextField9.setText("0");
        }
    }//GEN-LAST:event_jTextField9FocusLost

    private void jTextField9KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField9KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jButton2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField9KeyPressed

    private void jTextField9KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField9KeyReleased
        computation02();
    }//GEN-LAST:event_jTextField9KeyReleased

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
    private javax.swing.JLabel jLabel3;
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
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    // End of variables declaration//GEN-END:variables
}
