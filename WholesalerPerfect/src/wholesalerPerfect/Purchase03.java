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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import utilities.Add0Padding2;
import utilities.DateConverter;
import utilities.MyNumberFormat;
import utilities.Settings;

public class Purchase03 extends javax.swing.JInternalFrame implements AWTEventListener {
    
    private JDesktopPane jDesktopPane1;
    private UserProfile up;
    private Settings settings=new Settings();
    private DecimalFormat format = new DecimalFormat("0.#");
    private Query q=new Query();
    private DecimalFormat format2afterDecimal = new DecimalFormat("#.##");
    
    private boolean isFromOtherWindow;
    private String ssidArray[];
    private String compnmArray[];
    private String compidArray[];
    private String currentCompid;
    private String itemmidArray[];
    private String newStockist;
    private double tradeDiscPer;
    private double replacementDiscPer;
    private String currentItemmid;
    private String newItemDetails;
    private String selectedItemDetails;
    private String currentItemdid;
    private ArrayList<PurchaseSubV2> psAl = new ArrayList<PurchaseSubV2>();
    private ArrayList<PurchaseGSTV2> pgstAl = null;
    
    public Purchase03(JDesktopPane jDesktopPane1, UserProfile up, boolean isFromOtherWindow) {
        super("Purchase",false,true,false,true);
        initComponents();
        this.jDesktopPane1 = jDesktopPane1;
        this.up = up;
        this.isFromOtherWindow = isFromOtherWindow;
        Dimension d=getSize();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) dim.getWidth() - (int)d.getWidth())/2,((int) dim.getHeight() - (int)d.getHeight())/2-43);
        this.setResizable(false);
        this.setFrameIcon(new ImageIcon(getClass().getResource("/images/purchase01.png")));
        
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
        
        populateCombo1();
        
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
        ((JLabel)jComboBox2.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        jTable1.setComponentPopupMenu(jPopupMenu1);
        
        SwingUtilities.invokeLater
        (() -> {
            jTextField1.requestFocusInWindow();
        });
    }
    
    @Override
    public void eventDispatched(AWTEvent event) {
        if(event instanceof KeyEvent)
        {
            KeyEvent key = (KeyEvent)event;
            if(key.getID()==KeyEvent.KEY_PRESSED) {
                if(event.getSource().equals(jDateChooser1.getDateEditor())&&key.getKeyCode()==10) {
                    jComboBox1.requestFocusInWindow();
                } 
            }
        }
    }
    
    private void populateCombo1() { // Super Stockist
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in SuperStockistMaster: 9
        /* ssid, compid, ssnm, addr1, addr2, gstno, contact, email, isactive */
        // Number of columns in CompanyMaster: 4
        /* compid, compnm, compabbr, isactive */
        String query="select ssid, ssnm, compnm, x.compid as compid from (select ssid, compid, ssnm from "
                + "SuperStockistMaster where isactive=1) x, (select compid, compnm from CompanyMaster"
                + " where isactive=1) y where x.compid=y.compid order by ssnm asc";
        System.out.println(query);
        try {
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
                ssidArray=new String[total];
                compnmArray=new String[total];
                compidArray=new String[total];
                jComboBox1.addItem("-- Select --");
                int i=0;
                while(rs.next()) {
                    ssidArray[i]=rs.getString("ssid");
                    jComboBox1.addItem(rs.getString("ssnm"));
                    compnmArray[i]=rs.getString("compnm");
                    compidArray[i]=rs.getString("compid");
                    i++;
                }
            }
        }
        catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Purchase03 ex: "+ex.getMessage(),
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
    
    private void populateCombo2() { // Item Details
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in ItemMaster: 5
        /* itemmid, compid, itemnm, hsn, isactive */
        String query="select itemmid, itemnm from ItemMaster where isactive=1 "
                + "and compid="+currentCompid+" order by itemnm";
        System.out.println(query);
        try {
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
            jComboBox2.removeAllItems();
            if(total != 0) {
                itemmidArray=new String[total];
                jComboBox2.addItem("-- Select --");
                int i=0;
                while(rs.next()) {
                    itemmidArray[i++]=rs.getString("itemmid");
                    jComboBox2.addItem(rs.getString("itemnm"));
                }
            }
        }
        catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Purchase02 ex: "+ex.getMessage(),
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
    
    private String getOpeningStockInvoiceNo() {
        int total=0;
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        String financialcode="";
        String query="select financialcode from FinancialYear where isactive=1";
        System.out.println(query);
        try {
            Statement smt=conn.createStatement();
            ResultSet rs=smt.executeQuery(query);
            if(rs.next()) {
                financialcode=rs.getString("financialcode");
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Purchase03 ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        }
        finally {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException ex){}
        }
        
        // Table PurchaseMasterV2, no. of columns - 18
        /*
        pmid, ssid, compid, invno, invdt, netqty, netgross, netitemdisc, tradediscper, nettradediscamt, 
        replacementdiscper, netreplacementdiscamt, dispscheme, roundoff, netpayableamt, 
        isopening, amtpaid, isactive
        */
        query="select IFNULL(max(invno),'') as x from PurchaseMasterV2 where invno like '"
                    + "OPNSTK/__/"+financialcode+"'";
        System.out.println(query);
        conn = db.setConnection();
        try {
            Statement smt=conn.createStatement();
            ResultSet rs=smt.executeQuery(query);
            if(rs.next()) {
                String lastTotalID=rs.getString("x");
                if(lastTotalID.length()!=0) {
                    // Pattern: OPNSTK/01/17-18
                    String lastID=lastTotalID.substring(lastTotalID.indexOf("/")+1,lastTotalID.lastIndexOf("/"));
                    total=Integer.parseInt(lastID);
                }
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Purchase03 ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        }
        finally {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException ex){}
        }
        total++;
        return "OPNSTK/"+Add0Padding2.add0Padding(total)+"/"+financialcode;
    }
    
    private void addAlterStockist() {
        Thread t = new Thread(() -> {
            try {
                setVisible(false);
                final SuperStockistMaster ref=new SuperStockistMaster(jDesktopPane1, true, up);
                ref.addInternalFrameListener(new InternalFrameAdapter() {
                    @Override
                    public void internalFrameDeactivated(InternalFrameEvent e) {
                        newStockist=ref.getNewStockist();
                    }
                    @Override
                    public void internalFrameClosed(InternalFrameEvent e) {
                        Purchase03.this.setVisible(true);
                        if(newStockist!=null) {
                            populateCombo1();
                            jComboBox1.setSelectedItem(newStockist);
                            try {
                                if(((String)jComboBox1.getSelectedItem()).equals("-- Select --")) {
                                    jLabel4.setText("N/A");
                                } else {
                                    jLabel4.setText(compnmArray[jComboBox1.getSelectedIndex()-1]);
                                }
                            }
                            catch(NullPointerException ex){}
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
    
    private void billingStart() {
        if ( jTextField1.getText().trim().length() == 0 ) {
            if ( jCheckBox1.isSelected() ) {
                JOptionPane.showMessageDialog(null,"Software error!! Contact with software vendor.",
                    "Software Error Found",JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                JOptionPane.showMessageDialog(null,"Invoice No. is mandatory!!!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
                jTextField1.requestFocusInWindow();
                return;
            }
        }
        String invno = jTextField1.getText().trim().toUpperCase();
        if (currentCompid == null) {
            JOptionPane.showMessageDialog(null,"Select Super Stockist!!!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox1.requestFocusInWindow();
        }
        try {
            currentCompid = compidArray[jComboBox1.getSelectedIndex()-1];
        } catch (NullPointerException ex) {
            return;
        }
        // Testing for duplicate invoice for the same company
        // Table PurchaseMasterV2, no. of columns - 18
        /*
        pmid, ssid, compid, invno, invdt, netqty, netgross, netitemdisc, tradediscper, nettradediscamt, 
        replacementdiscper, netreplacementdiscamt, dispscheme, roundoff, netpayableamt, 
        isopening, amtpaid, isactive
        */
        String query="select pmid from PurchaseMasterV2 where compid="+currentCompid+" and invno='"+invno+"'";
        System.out.println(query);
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        int total=0;
        try {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(query);
            //Move to the last record
            rs.afterLast();
            //Get the current record position
            if(rs.previous())
                total = rs.getRow();
            //Move back to the first record;
            rs.beforeFirst();
            if(total != 0) {
                JOptionPane.showMessageDialog(null,"Duplicate Invoice No. for the same company !!!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
                jTextField1.selectAll();
                jTextField1.requestFocusInWindow();
                return;
            }
        }
        catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Purchase03 ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException ex){}
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
        if ( jComboBox1.getSelectedIndex() == 0 ) {
            JOptionPane.showMessageDialog(null,"Super Stockist mandatory!!!",
                "Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox1.requestFocusInWindow();
            return;
        }
        tradeDiscPer = Double.parseDouble(jTextField2.getText());
        replacementDiscPer = Double.parseDouble(jTextField3.getText());
        
        jCheckBox1.setEnabled(false);
        jTextField1.setEnabled(false);
        jDateChooser1.setEnabled(false);
        jComboBox1.setEnabled(false);
        jTextField2.setEnabled(false);
        jTextField3.setEnabled(false);
        jButton1.setEnabled(false);
        jComboBox2.requestFocusInWindow();
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
                        Purchase03.this.setVisible(true);
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
                                JOptionPane.showMessageDialog(null,"Purchase03 ex: "+ex.getMessage(),
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
                            
                            jComboBox2.setSelectedItem(newItemnm);
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
                        Purchase03.this.setVisible(true);
                        if(selectedItemDetails!=null) {
                            //     0        1       2       3        4          5          6          7          8
                            // itemdid+"~"+hsn+"~"+mrp+"~"+gst+"~"+pexgst+"~"+pingst+"~"+sexgst+"~"+singst+"~"+onhand
                            String s[] = selectedItemDetails.split("~");
                            currentItemdid = s[0];
                            jLabel9.setText(s[2]); //mrp
                            jLabel11.setText(s[1]); //hsn
                            jLabel13.setText(s[3]); //gst
                            jLabel16.setText(s[4]); //purchase ex-gst - Now reffered as RATE
                            
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
            double exgst = Double.parseDouble(jLabel16.getText().trim().replaceAll(",", ""));
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
            jLabel20.setText(MyNumberFormat.rupeeFormat(amount));
        } catch (Exception ex) {}
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
            jComboBox2.requestFocusInWindow();
            return;
        }
        String mrp = jLabel9.getText().trim().replaceAll(",", "");
        String gst = jLabel13.getText().trim().replaceAll(",", "");
        String qty = jTextField4.getText();
        if(Integer.parseInt(qty) == 0)
        {
            JOptionPane.showMessageDialog(null,"Quantity is mandatory !!!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField4.requestFocusInWindow();
            return;
        }
        String rate = jLabel16.getText().trim().replaceAll(",", "");
        String discper = jTextField5.getText();
        String discamt = jTextField6.getText();
        String amount = jLabel20.getText().trim().replaceAll(",", "");
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
        jComboBox2.setSelectedIndex(0);
        jComboBox2.requestFocusInWindow();
    }
    
    private void itemDetailsFormFlush() {
        currentItemdid = null;
        newItemDetails = null;
        selectedItemDetails = null;
        
        jLabel9.setText("0");
        jLabel11.setText("N/A");
        jLabel13.setText("0");
        jTextField4.setText("0");
        jLabel16.setText("0");
        jTextField5.setText("0");
        jTextField6.setText("0");
        jLabel20.setText("0");
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
                JOptionPane.showMessageDialog(null,"Purchase03 ex: "+ex.getMessage(),
                        "Error Found",JOptionPane.ERROR_MESSAGE);
                return;
            }
            // No. of columns in table - 
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
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(300);
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
        
        jLabel23.setText(format.format(netqty));
        jLabel25.setText(MyNumberFormat.rupeeFormat(netdiscount));
        jLabel27.setText(MyNumberFormat.rupeeFormat(netamount));
        
        computation02();
    }
    
    private  void computation02() {
        try {
            double netamount = Double.parseDouble(jLabel27.getText().trim().replaceAll(",", ""));
            // Trade Discount
            double tradediscper = Double.parseDouble(jTextField2.getText());
            double nettradediscamt = netamount * (tradediscper / 100.0);
            jLabel31.setText(MyNumberFormat.rupeeFormat(nettradediscamt));
            double amountAfterTradeDisc = netamount - nettradediscamt;
            // Replacement Discount After Trade Discount
            double replacementdiscper = Double.parseDouble(jTextField3.getText());
            double netreplacementdiscamt = amountAfterTradeDisc * (replacementdiscper / 100.0);
            jLabel33.setText(MyNumberFormat.rupeeFormat(netreplacementdiscamt));
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
            jLabel39.setText(MyNumberFormat.rupeeFormat(amountAfterGST));
            double roundoff = Double.parseDouble(jTextField8.getText());
            double amountAfterRoundoff = amountAfterGST + roundoff;
            double displayscheme = Double.parseDouble(jTextField9.getText());
            double total = amountAfterRoundoff - displayscheme;
            jLabel37.setText(MyNumberFormat.rupeeFormat(total));
        } catch (Exception ex) { 
            // ex.printStackTrace();
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
    
    private void insertToDatabase() {
        if (psAl.isEmpty() || pgstAl.isEmpty()) {
            JOptionPane.showMessageDialog(null,"Incomplete Data!!!", 
                    "Incomplete Data",JOptionPane.ERROR_MESSAGE);
            jComboBox2.requestFocusInWindow();
            return;
        }
        if(((String)jComboBox1.getSelectedItem()).equals("-- Select --")) {
            JOptionPane.showMessageDialog(null,"Select The Super-Stockist!", 
                    "Incomplete Data",JOptionPane.ERROR_MESSAGE);
            jComboBox1.requestFocusInWindow();
            return;
        }
        String ssid="";
        try {
            ssid=ssidArray[jComboBox1.getSelectedIndex()-1];
        }
        catch(ArrayIndexOutOfBoundsException e) {
            return;
        }
        String invno=jTextField1.getText().trim().toUpperCase();
        if(invno.length()==0) {
            JOptionPane.showMessageDialog(null,"Enter the Invoice No.!", 
                    "Incomplete Data",JOptionPane.ERROR_MESSAGE);
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
        String netqty = jLabel23.getText().trim().replaceAll(",", "");
        String netitemdisc = jLabel25.getText().trim().replaceAll(",", "");
        String netgross = jLabel27.getText().trim().replaceAll(",", "");
        String tradediscper = jTextField2.getText();
        String nettradediscamt = jLabel31.getText().trim().replaceAll(",", "");
        String replacementdiscper = jTextField3.getText();
        String netreplacementdiscamt = jLabel33.getText().trim().replaceAll(",", "");
        String netgstamt = format2afterDecimal.format(pgstAl.stream()
                .mapToDouble(f -> Double.parseDouble(f.getGstamt())).sum());
        String amtaftergst = jLabel39.getText().trim().replaceAll(",", "");
        String roundoff = jTextField8.getText();
        String dispscheme = jTextField9.getText();
        String netpayableamt = jLabel37.getText().trim().replaceAll(",", "");
        String isopening = (jCheckBox1.isSelected()?"1":"0");
        String amtpaid = netpayableamt;
        String isactive = "1";
        
        // Table PurchaseMasterV2, no. of columns - 20
        /*
        pmid, ssid, compid, invno, invdt, netqty, netitemdisc, netgross, tradediscper, nettradediscamt, 
        replacementdiscper, netreplacementdiscamt, netgstamt, amtaftergst, roundoff, dispscheme, 
        netpayableamt, isopening, amtpaid, isactive
        */
        PurchaseMasterV2 pm = new PurchaseMasterV2();
        pm.setPmid(""); // At Insert
        pm.setSsid(ssid);
        pm.setCompid(currentCompid);
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
                // Number of columns in ItemDetails 10
                /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
                String sql1 = "select onhand from ItemDetails where itemdid=?";
                psmt1 = conn.prepareStatement(sql1);
                
                String sql2 = "update ItemDetails set onhand=onhand+? where itemdid=?";
                psmt2 = conn.prepareStatement(sql2);
                // Number of columns in ItemLedger: 9
                /* ilid, itemdid, tablenm, pknm, pkval, actiondt, type, prevqty, qty */
                String sql3 = "insert into ItemLedger (ilid, itemdid, tablenm, pknm, pkval, actiondt, "
                        + "type, prevqty, qty) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                psmt3 = conn.prepareStatement(sql3);
                
                HashMap<String, Integer> hashMap = new HashMap<>();
                
                for(PurchaseSubV2 ref : pm.getPsAl()) {
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
                            if (hashMap.containsKey(ref.getItemdid())) {
                                flag = false;
                                pervqty = hashMap.get(ref.getItemdid());
                                hashMap.put(ref.getItemdid(), hashMap.get(ref.getItemdid()) + Integer.parseInt(ref.getQty()));
                            }
                        }
                        if (flag) {
                            hashMap.put(ref.getItemdid(), pervqty + Integer.parseInt(ref.getQty()));
                        }
                    }
                    
                    psmt2.setInt(1, Integer.parseInt(ref.getQty()));
                    psmt2.setInt(2, Integer.parseInt(ref.getItemdid()));
                    psmt2.addBatch();
                    
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
            catch(SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"Purchase03 ex: "+ex.getMessage(),
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
        }
    }
    
    private void formFlush() {
        currentCompid = null;
        newStockist = null;
        tradeDiscPer = 0.0;
        replacementDiscPer = 0.0;
        psAl = new ArrayList<PurchaseSubV2>();
        pgstAl = null;
        
        jCheckBox1.setSelected(false);
        jCheckBox1.setEnabled(true);
        jTextField1.setEnabled(true);
        jTextField1.setText("");
        jDateChooser1.setEnabled(true);
        jDateChooser1.setDate(new Date());
        jComboBox1.setEnabled(true);
        jComboBox1.setSelectedIndex(0);
        jLabel4.setText("N/A");
        jTextField2.setEnabled(true);
        jTextField2.setText("0");
        jTextField3.setEnabled(true);
        jTextField3.setText("0");
        jButton1.setEnabled(true);
        
        clearTable(jTable1);
        itemDetailsFormFlush();
        
        jTextField7.setText("");
        jLabel23.setText("0");
        jLabel25.setText("0");
        jLabel27.setText("0");
        
        jLabel31.setText("0");
        jLabel33.setText("0");
        clearTable(jTable2);
        jLabel39.setText("0");
        jTextField8.setText("0");
        jTextField9.setText("0");
        jLabel37.setText("0");
        
        jTextField1.requestFocusInWindow();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel21 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel34 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();

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

        jCheckBox1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jCheckBox1.setText("IS OPENING STOCK");
        jCheckBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBox1ItemStateChanged(evt);
            }
        });
        jCheckBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCheckBox1KeyPressed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("INV. NO.");

        jTextField1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("INV. DATE");

        jDateChooser1.setDateFormatString("dd/MM/yyyy");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("SUPER STOCKIST");

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

        jLabel4.setBackground(new java.awt.Color(255, 255, 204));
        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("N/A");
        jLabel4.setOpaque(true);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("TRADE DISC. %");

        jTextField2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
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

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setText("REPLACEMENT DISC. %");

        jTextField3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
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
        });

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton1.setText("START");
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

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 51, 255), 2, true), "ITEM PURCHASED", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setText("SELECT ITEM+<F5>");

        jComboBox2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox2ItemStateChanged(evt);
            }
        });
        jComboBox2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox2KeyPressed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setText("MRP");

        jLabel9.setBackground(new java.awt.Color(255, 255, 204));
        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("0");
        jLabel9.setOpaque(true);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("HSN");

        jLabel11.setBackground(new java.awt.Color(255, 255, 204));
        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("N/A");
        jLabel11.setOpaque(true);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setText("GST %");

        jLabel13.setBackground(new java.awt.Color(255, 255, 204));
        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("0");
        jLabel13.setOpaque(true);

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel14.setText("QTY.");

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

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel15.setText("RATE");

        jLabel16.setBackground(new java.awt.Color(255, 255, 204));
        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("0");
        jLabel16.setOpaque(true);

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

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel17.setText("DISC. %");

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

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel18.setText("DISC. AMOUNT");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel19.setText("AMOUNT");

        jLabel20.setBackground(new java.awt.Color(255, 255, 204));
        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("0");
        jLabel20.setOpaque(true);

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton2.setText("ADD TO LIST");
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

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/SEARCH.PNG"))); // NOI18N
        jLabel21.setText("ITEM SEARCH");

        jTextField7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField7KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField7KeyReleased(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel22.setText("TOTAL NO. OF ITEMS");

        jLabel23.setBackground(new java.awt.Color(255, 255, 204));
        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("0");
        jLabel23.setOpaque(true);

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel24.setText("NET DISC. AMT.");

        jLabel25.setBackground(new java.awt.Color(255, 255, 204));
        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("0");
        jLabel25.setOpaque(true);

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel26.setText("NET AMT.");

        jLabel27.setBackground(new java.awt.Color(255, 255, 204));
        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("0");
        jLabel27.setOpaque(true);

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
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton2))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 94, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24)
                    .addComponent(jLabel25)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel30.setText("TRADE DISC. AMT.");

        jLabel31.setBackground(new java.awt.Color(255, 255, 204));
        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("0");
        jLabel31.setOpaque(true);

        jLabel32.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel32.setText("REPLACEMENT DISC. AMT.");

        jLabel33.setBackground(new java.awt.Color(255, 255, 204));
        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText("0");
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
        jLabel34.setText("ROUND OFF");

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

        jLabel35.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel35.setText("DISPLAY SCHEME");

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

        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel36.setText("TOTAL");

        jLabel37.setBackground(new java.awt.Color(255, 255, 204));
        jLabel37.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setText("0");
        jLabel37.setOpaque(true);

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton3.setText("SAVE");
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

        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel38.setText("PAYABLE AMOUNT");

        jLabel39.setBackground(new java.awt.Color(255, 255, 204));
        jLabel39.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("0");
        jLabel39.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jCheckBox1)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel38)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel34)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel36)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jCheckBox1)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(jLabel31)
                    .addComponent(jLabel32)
                    .addComponent(jLabel33))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel38)
                    .addComponent(jLabel39)
                    .addComponent(jLabel34)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36)
                    .addComponent(jLabel37)
                    .addComponent(jButton3))
                .addGap(27, 27, 27))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox1ItemStateChanged
        if ( jCheckBox1.isSelected() ) {
            jTextField1.setText(getOpeningStockInvoiceNo());
            jTextField1.setEnabled(false);
        } else {
            jTextField1.setText("");
            jTextField1.setEnabled(true);
        }
    }//GEN-LAST:event_jCheckBox1ItemStateChanged

    private void jCheckBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCheckBox1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if ( jCheckBox1.isSelected() ) {
                jDateChooser1.requestFocusInWindow();
            } else {
                jTextField1.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jCheckBox1KeyPressed

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jDateChooser1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        try
        {
            if(((String)jComboBox1.getSelectedItem()).equals("-- Select --"))
            {
                jLabel4.setText("N/A");
                currentCompid = null;
                jComboBox2.removeAllItems();
            }
            else
            {
                jLabel4.setText(compnmArray[jComboBox1.getSelectedIndex()-1]);
                currentCompid=compidArray[jComboBox1.getSelectedIndex()-1];
                populateCombo2();
            }
        }
        catch(NullPointerException ex){}
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jComboBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTextField2.requestFocusInWindow();
        }
        if(evt.getKeyCode() == KeyEvent.VK_F2) {
            addAlterStockist();
        }
    }//GEN-LAST:event_jComboBox1KeyPressed

    private void jTextField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField2FocusGained
        String s=jTextField2.getText().trim();
        if(Double.parseDouble(s) == 0) {
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
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTextField3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField2KeyPressed

    private void jTextField3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField3FocusGained
        String s=jTextField3.getText().trim();
        if(Double.parseDouble(s) == 0) {
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
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jButton1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField3KeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        billingStart();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            billingStart();
        }
    }//GEN-LAST:event_jButton1KeyPressed

    private void jComboBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox2ItemStateChanged
        jLabel9.setText("0"); // MRP
        jLabel11.setText("N/A"); // HSN
        jLabel13.setText("0"); // GST
        jLabel16.setText("0"); // RATE
        currentItemmid = null;
    }//GEN-LAST:event_jComboBox2ItemStateChanged

    private void jComboBox2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox2KeyPressed
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
            if ( jComboBox2.getSelectedIndex() == 0 ) {
                JOptionPane.showMessageDialog(null,"Select Proper Item", "Error Found",JOptionPane.ERROR_MESSAGE);
                jComboBox2.requestFocusInWindow();
                return;
            }
            currentItemmid = itemmidArray[jComboBox2.getSelectedIndex()-1];
            getItemDetails();
        }
    }//GEN-LAST:event_jComboBox2KeyPressed

    private void jTextField5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTextField6.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField5KeyPressed

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

    private void jTextField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyReleased
        calculation01();
    }//GEN-LAST:event_jTextField2KeyReleased

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
            jButton2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField6KeyPressed

    private void jTextField6KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyReleased
        calculation01();
    }//GEN-LAST:event_jTextField6KeyReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        addToList();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            addToList();
        }
    }//GEN-LAST:event_jButton2KeyPressed

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
            jButton3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField9KeyPressed

    private void jTextField9KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField9KeyReleased
        computation02();
    }//GEN-LAST:event_jTextField9KeyReleased

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

    private void jTextField5KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyReleased
        calculation01();
    }//GEN-LAST:event_jTextField5KeyReleased

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        insertToDatabase();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            insertToDatabase();
        }
    }//GEN-LAST:event_jButton3KeyPressed

    private void jTextField7KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTextField7.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField7KeyPressed

    private void jTextField7KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyReleased
        if (!psAl.isEmpty()) {
            String searchString = jTextField6.getText().trim().toUpperCase();
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
                    JOptionPane.showMessageDialog(null,"Purchase03 ex: "+ex.getMessage(),
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
                    jComboBox2.requestFocusInWindow();
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
                            jComboBox2.setSelectedItem(rs.getString("itemnm").replace("\\'", "'"));
                            jLabel11.setText(rs.getString("hsn"));
                        }
                    }
                    catch(SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null,"Purchase03 ex: "+ex,
                                "Error Found",JOptionPane.ERROR_MESSAGE);
                    }
                    finally {
                        try {
                            if (conn!=null) conn.close();
                        }
                        catch(SQLException ex){}
                    }
                    jLabel9.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getMrp())));
                    jLabel13.setText(format2afterDecimal.format(Double.parseDouble(ps.getGst())));
                    jTextField4.setText(ps.getQty());
                    jLabel16.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getRate())));
                    jTextField5.setText(format2afterDecimal.format(Double.parseDouble(ps.getDiscper())));
                    jTextField6.setText(format2afterDecimal.format(Double.parseDouble(ps.getDiscamt())));
                    jLabel20.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getAmount())));
                    calculation01();
                    psAl.remove(row);
                    Fetch();
                    computation02();
                    jComboBox2.requestFocusInWindow();
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

    private void formInternalFrameIconified(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameIconified
        moveToFront();
    }//GEN-LAST:event_formInternalFrameIconified


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
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
