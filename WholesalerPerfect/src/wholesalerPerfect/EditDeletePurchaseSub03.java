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
import java.text.ParseException;
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

public class EditDeletePurchaseSub03 extends javax.swing.JInternalFrame implements AWTEventListener {

    private JDesktopPane jDesktopPane1;
    private UserProfile up;
    private Settings settings=new Settings();
    private DecimalFormat format = new DecimalFormat("0.#");
    private Query q=new Query();
    private DecimalFormat format2afterDecimal = new DecimalFormat("#.##");
    
    private PurchaseMaster pm;
    private PurchaseMaster oldPm;
    private ArrayList<PurchaseSub> psAl;
    private final boolean isFromOtherWindow;
    private String itemmidArray[];
    private String currentItemmid;
    private String newItemDetails;
    private String selectedItemDetails;
    private String currentItemdid;
    private double tradeDiscPer;
    private double replacementDiscPer;
    
    public EditDeletePurchaseSub03(JDesktopPane jDesktopPane1, UserProfile up, 
            PurchaseMaster pm, boolean isFromOtherWindow) {
        super("Edit/Delete Purchase",false,true,false,true);
        initComponents();
        this.jDesktopPane1 = jDesktopPane1;
        this.up = up;
        this.oldPm = q.getPurchaseMaster(pm.getPmid());
        this.isFromOtherWindow = isFromOtherWindow;
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
            public void focusGained(FocusEvent evt) 
            {
                ((JTextFieldDateEditor)evt.getSource()).selectAll();
            }
        });
        
        ((DefaultTableCellRenderer)jTable1.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        ((JLabel)jComboBox2.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        settings.numvalidatorFloat(jTextField2);
        settings.numvalidatorFloat(jTextField3);
        settings.numvalidatorInt(jTextField4);
        settings.numvalidatorFloat(jTextField5);
        settings.numvalidatorFloatWithSign(jTextField7);
        
        jTable1.setComponentPopupMenu(jPopupMenu1);
        
        populateCombo2();
        populateData();
        
        SwingUtilities.invokeLater (
            new Runnable() {
                @Override
                public void run() {
                    jTextField1.requestFocusInWindow();
                }
            }
        );
    }
    
    @Override
    public void eventDispatched(AWTEvent event) {
        if(event instanceof KeyEvent) {
            KeyEvent key = (KeyEvent)event;
            if(key.getID()==KeyEvent.KEY_PRESSED) {
                if(event.getSource().equals(jDateChooser1.getDateEditor())&&key.getKeyCode()==10) {
                    jTextField2.requestFocusInWindow();
                } 
            }
        }
    }
    
    private void populateData() {
        jLabel55.setText(Integer.parseInt(oldPm.getIsopening())==1?"OPENING":"REGULAR");
        jTextField1.setText(oldPm.getInvno());
        String iDate=oldPm.getInvdt();  
        Date date=null;
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(iDate);
        }
        catch (ParseException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"EditDeletePurchaseSub03 ex: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        jDateChooser1.setDate(date);
        // Number of columns in SuperStockistMaster: 9
        /* ssid, compid, ssnm, addr1, addr2, gstno, contact, email, isactive */
        // Number of columns in CompanyMaster: 4
        /* compid, compnm, compabbr, isactive */
        String query1 = "SELECT ssnm, compnm FROM SuperStockistMaster ssm, CompanyMaster cm "
                + "WHERE ssm.compid=cm.compid AND ssm.isactive=1 AND cm.isactive=1 AND ssm.ssid="+oldPm.getSsid();
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
                    jLabel54.setText(rs.getString("ssnm"));
                    jLabel4.setText(rs.getString("compnm"));
                }
            }
        }
        catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"EditDeletePurchaseSub03 ex: "+ex.getMessage(),
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
        psAl = oldPm.getPsAl();
        Fetch();
        jLabel30.setText(oldPm.getNetqty());
        jLabel32.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(oldPm.getNetgross())));
        jLabel34.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(oldPm.getNetitemdisc())));
        jLabel36.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(oldPm.getNettradediscamt())));
        jLabel38.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(oldPm.getNetreplacementdiscamt())));
        jLabel39.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(oldPm.getNettaxable())));
        jLabel42.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(oldPm.getNetgstamt())));
        jLabel44.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(oldPm.getGrosspayableamt())));
        jTextField7.setText(format2afterDecimal.format(Double.parseDouble(oldPm.getRoundoff())));
        jLabel47.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(oldPm.getNetpayableamt())));
        jTextField8.setText(format2afterDecimal.format(Double.parseDouble(oldPm.getDispscheme())));
        jLabel53.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(oldPm.getFinalamt())));
        
        jTextField1.requestFocusInWindow();
    }
    
    private void populateCombo2() { // Item Details
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in ItemMaster: 5
        /* itemmid, compid, itemnm, hsn, isactive */
        String query="select itemmid, itemnm from ItemMaster where isactive=1 "
                + "and compid="+oldPm.getCompid()+" order by itemnm";
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
            JOptionPane.showMessageDialog(null,"EditDeletePurchaseSub03 ex: "+ex.getMessage(),
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
    
    private void addAlterItemDetails() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
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
                            EditDeletePurchaseSub03.this.setVisible(true);
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
                                    JOptionPane.showMessageDialog(null,"EditDeletePurchaseSub03 ex: "+ex.getMessage(),
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
            }
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
            try {
                setVisible(false);
                final ItemDetails4Purchase ref=new ItemDetails4Purchase(currentItemmid);
                ref.addInternalFrameListener(new InternalFrameAdapter() {
                    @Override
                    public void internalFrameDeactivated(InternalFrameEvent e) {
                        selectedItemDetails=ref.getSelectedItemDetails();
//                            System.out.println(selectedItemDetails);
                    }
                    @Override
                    public void internalFrameClosed(InternalFrameEvent e) {
                        EditDeletePurchaseSub03.this.setVisible(true);
                        // System.out.println(selectedItemDetails);
                        if(selectedItemDetails!=null) {
                            //     0               1            2            3             4                 5               6                 7                8
                            // itemdid+"~"+hsn+"~"+mrp+"~"+gst+"~"+pexgst+"~"+pingst+"~"+sexgst+"~"+singst+"~"+onhand
                            String s[] = selectedItemDetails.split("~");
                            // for(String s1:s){
                            //    System.out.println(s1);
                            // }
                            currentItemdid = s[0];
                            jLabel9.setText(s[1]); //hsn
                            jLabel49.setText(s[3]); //gst
                            jLabel11.setText(s[4]); //purchase ex-gst
                            jLabel13.setText(s[5]); //purchase in-gst
                            jLabel15.setText(s[2]); //mrp
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
            double exgst = Double.parseDouble(jLabel11.getText().trim().replaceAll(",", ""));
            int qty = Integer.parseInt(jTextField4.getText());
            double gross = exgst * qty;
            jLabel18.setText(MyNumberFormat.rupeeFormat(gross));
            double discountAmt = Double.parseDouble(jTextField5.getText());
            double afterDiscount = gross - discountAmt; // In bill this is the Taxable Amount
            double tradeDiscAmt = afterDiscount * (tradeDiscPer / 100.0);
            jLabel21.setText(MyNumberFormat.rupeeFormat(tradeDiscAmt));
            double afterTradeDiscount = afterDiscount - tradeDiscAmt;
            double replacementDiscAmt = afterTradeDiscount * (replacementDiscPer / 100.0);
            jLabel23.setText(MyNumberFormat.rupeeFormat(replacementDiscAmt));
            double afterReplacementDiscount = afterTradeDiscount - replacementDiscAmt; // This is the actual Taxable Amount
            jLabel25.setText(MyNumberFormat.rupeeFormat(afterReplacementDiscount));
            double gstper = Double.parseDouble(jLabel49.getText());
            double gstamt = afterReplacementDiscount * (gstper / 100.0);
            jLabel27.setText(MyNumberFormat.rupeeFormat(gstamt));
        } catch (Exception ex) {}
    }
    
    private void addToList() {
        // Number of columns in PurchaseSub: 16
        /* psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, 
        taxableamt, gstamt, qtysold, retqty, tradediscamt, replacementdiscamt */
        String itemdid = currentItemdid;
        if(itemdid == null) {
            JOptionPane.showMessageDialog(null,"Select the Item Details (F5 from item selection) !!!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox2.requestFocusInWindow();
            return;
        }
        String gst = jLabel49.getText().trim().replaceAll(",", "");
        String exgst = jLabel11.getText().trim().replaceAll(",", "");
        String ingst = jLabel13.getText().trim().replaceAll(",", "");
        String mrp = jLabel15.getText().trim().replaceAll(",", "");
        String qty = jTextField4.getText();
        if(Integer.parseInt(qty) == 0) {
            JOptionPane.showMessageDialog(null,"Quantity is mandatory !!!","Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField4.requestFocusInWindow();
            return;
        }
        String gross = jLabel18.getText().trim().replaceAll(",", "");
        String discamt = jTextField5.getText();
        String tradediscamt = jLabel21.getText().trim().replaceAll(",", "");
        String replacementdiscamt = jLabel23.getText().trim().replaceAll(",", "");
        String taxableamt = jLabel25.getText().trim().replaceAll(",", "");
        String gstamt = jLabel27.getText().trim().replaceAll(",", "");
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
        jComboBox2.setSelectedIndex(0);
        jComboBox2.requestFocusInWindow();
    }
    
    private void clearTable(JTable table) {
        for(int i=table.getRowCount()-1; i>=0; i--) {
            ((DefaultTableModel)table.getModel()).removeRow(i);
        }
    }
    
    private void Fetch() {
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
        for (PurchaseSub ps :  psAl) {
            Vector<String> row = new Vector<String>();
            row.addElement(++slno1+"");
            // Number of columns: 10
            // SLN., PARTICULAR, MRP, HSN, GST %, QTY., UOM, RATE, DISC. AMT., GROSS
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
            try
            {
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
                JOptionPane.showMessageDialog(null,"EditDeletePurchaseSub03 ex: "+ex.getMessage(),
                        "Error Found",JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Number of columns: 10
            // SLN., PARTICULAR, MRP, HSN, GST %, QTY., UOM, RATE, DISC. AMT., GROSS
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getMrp())));
            row.addElement(hsn);
            row.addElement(format2afterDecimal.format(Double.parseDouble(gst)));
            row.addElement(ps.getQty());
            row.addElement("PCS");
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getIngst())));
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getDiscamt())));
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getGross())));
            
            netqty += Integer.parseInt(ps.getQty());
            netgross += Double.parseDouble(ps.getGross());
            netdiscount += Double.parseDouble(ps.getDiscamt());
            nettradediscamt += Double.parseDouble(ps.getTradediscamt());
            netreplacementdiscamt += Double.parseDouble(ps.getReplacementdiscamt());
            nettaxable += Double.parseDouble(ps.getTaxableamt());
            netgstamt += Double.parseDouble(ps.getGstamt());
            
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
        // Number of columns: 10
        // SLN., PARTICULAR, MRP, HSN, GST %, QTY., UOM, RATE, DISC. AMT., GROSS
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN.
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(1).setMinWidth(0);// PARTICULAR
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(250);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);// MRP
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(110);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// HSN
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);// GST %
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);// QTY.
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(110);
        jTable1.getColumnModel().getColumn(6).setMinWidth(0);// UOM
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(110);
        jTable1.getColumnModel().getColumn(7).setMinWidth(0);// RATE
        jTable1.getColumnModel().getColumn(7).setPreferredWidth(110);
        jTable1.getColumnModel().getColumn(8).setMinWidth(0);// DISC. AMT.
        jTable1.getColumnModel().getColumn(8).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(9).setMinWidth(0);// GROSS
        jTable1.getColumnModel().getColumn(9).setPreferredWidth(110);
                
        // Number of columns: 10
        // SLN., PARTICULAR, MRP, HSN, GST %, QTY., UOM, RATE, DISC. AMT., GROSS
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable1.getColumn("SLN.").setCellRenderer( centerRenderer );
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        jTable1.getColumn("MRP").setCellRenderer( rightRenderer );
        jTable1.getColumn("GST %").setCellRenderer( rightRenderer );
        jTable1.getColumn("QTY.").setCellRenderer( rightRenderer );
        jTable1.getColumn("RATE").setCellRenderer( rightRenderer );
        jTable1.getColumn("DISC. AMT.").setCellRenderer( rightRenderer );
        jTable1.getColumn("GROSS").setCellRenderer( rightRenderer );
        
        jLabel30.setText(format.format(netqty));
        jLabel32.setText(MyNumberFormat.rupeeFormat(netgross));
        jLabel34.setText(MyNumberFormat.rupeeFormat(netdiscount));
        jLabel36.setText(MyNumberFormat.rupeeFormat(nettradediscamt));
        jLabel38.setText(MyNumberFormat.rupeeFormat(netreplacementdiscamt));
        jLabel39.setText(MyNumberFormat.rupeeFormat(nettaxable));
        jLabel42.setText(MyNumberFormat.rupeeFormat(netgstamt));
        
        computation02();
    }
    
    private  void computation02() {
        try {
            double netTaxableAmt = Double.parseDouble(jLabel39.getText().trim().replaceAll(",", ""));
            double netGstAmt = Double.parseDouble(jLabel42.getText().trim().replaceAll(",", ""));
            double payableAmt = netTaxableAmt + netGstAmt;
            jLabel44.setText(MyNumberFormat.rupeeFormat(payableAmt));
            double roundoff = Double.parseDouble(jTextField7.getText());
            double totalAmount = payableAmt + roundoff;
            jLabel47.setText(MyNumberFormat.rupeeFormat(totalAmount));
            double dispscheme = Double.parseDouble(jTextField8.getText());
            double finalamt = totalAmount - dispscheme;
            jLabel53.setText(MyNumberFormat.rupeeFormat(finalamt));
        } catch (Exception ex) { }
    }
    
    private void itemDetailsFormFlush() {
        currentItemdid = null;
        newItemDetails = null;
        selectedItemDetails = null;
        
        jLabel9.setText("N/A");
        jLabel49.setText("0");
        jLabel11.setText("0");
        jLabel13.setText("0");
        jLabel15.setText("0");
        jTextField4.setText("0");
        jLabel18.setText("0");
        jTextField5.setText("0");
        jLabel21.setText("0");
        jLabel23.setText("0");
        jLabel25.setText("0");
        jLabel27.setText("0");
    }
    
    private void updateToDatabase() {
        if(psAl.stream()
                .filter(x -> Integer.parseInt(x.getRetqty())>0)
                .count()>0) {
            JOptionPane.showMessageDialog(null,"Some items are already sold, hence unable to update!!!","Unable to update",JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (psAl.isEmpty()) {
            JOptionPane.showMessageDialog(null,"Incomplete Data !!!","Incomplete Data",JOptionPane.ERROR_MESSAGE);
            jComboBox2.requestFocusInWindow();
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
        String netqty = jLabel30.getText().trim().replaceAll(",", "");
        String netgross = jLabel32.getText().trim().replaceAll(",", "");
        String netitemdisc = jLabel34.getText().trim().replaceAll(",", "");
        String tradediscper = jTextField2.getText();
        String nettradediscamt = jLabel36.getText().trim().replaceAll(",", "");
        String replacementdiscper = jTextField3.getText();
        String netreplacementdiscamt = jLabel38.getText().trim().replaceAll(",", "");
        String nettaxable =  jLabel39.getText().trim().replaceAll(",", "");
        String netgstamt = jLabel42.getText().trim().replaceAll(",", "");
        String grosspayableamt = jLabel44.getText().trim().replaceAll(",", "");
        String roundoff = jTextField7.getText();
        String netpayableamt = jLabel47.getText().trim().replaceAll(",", "");
        String dispscheme = jTextField8.getText();
        String finalamt = jLabel53.getText().trim().replaceAll(",", "");
        String amtpaid = finalamt;
        String isactive = "1";

        // Number of columns in PurchaseMaster: 22
        /* pmid, ssid, compid, invno, invdt, netqty, netgross, netitemdisc, nettaxable, tradediscper, 
        nettradediscamt, replacementdiscper, netreplacementdiscamt, netgstamt, grosspayableamt, roundoff, netpayableamt, 
        dispscheme, finalamt, isopening, amtpaid, isactive */
        pm = new PurchaseMaster();
        pm.setPmid(""); // At Insert
        pm.setSsid(this.oldPm.getSsid());
        pm.setCompid(this.oldPm.getCompid());
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
        pm.setDispscheme(dispscheme);
        pm.setFinalamt(finalamt);
        pm.setIsopening(this.oldPm.getIsopening());
        pm.setAmtpaid(amtpaid);
        pm.setIsactive(isactive);
        pm.setPsAl(psAl);

        if(q.deleteFromPurchaseMaster(oldPm)) {
            int pmid = q.insertToPurchaseMaster(pm);
            if (pmid > 0) {
                pm = q.getPurchaseMaster(pmid+"");

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
                try
                {
                    conn.setAutoCommit(false);

                    String pervqty = null;
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
                    psmt4.setDouble(7, Double.parseDouble(pm.getFinalamt()));
                    psmt4.executeUpdate();

                    conn.commit();
                }
                catch(SQLException ex)
                {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,"EditDeletePurchaseSub03 ex: "+ex.getMessage(),
                            "Error Found",JOptionPane.ERROR_MESSAGE);
                    try {
                        conn.rollback();
                    } catch (SQLException ex1) {
                        ex1.printStackTrace();
                    }
                }
                finally {
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
                setVisible(false);
                dispose();
            }
        } else {
            JOptionPane.showMessageDialog(null,"Unable to Edit Purchase",
                    "Edit Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteFromDatabase() {
        String ObjButtons[] = {"Yes","Cancel"};
        int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure to Delete the entire Purchase!",
            "Delete Record",JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null,ObjButtons,ObjButtons[1]);
        if(PromptResult==0)
        {
            if(q.deleteFromPurchaseMaster(oldPm)) {
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
    
    private void formFlush() {
        tradeDiscPer = 0.0;
        replacementDiscPer = 0.0;
        psAl = null;
        
        jLabel55.setText("N/A");
        jTextField1.setText("N/A");
        jDateChooser1.setDate(new Date());
        jLabel54.setText("N/A");
        jLabel4.setText("N/A");
        jTextField2.setText("0");
        jTextField3.setText("0");
        
        clearTable(jTable1);
        itemDetailsFormFlush();
        
        jTextField6.setText("");
        jLabel30.setText("0");
        jLabel32.setText("0");
        jLabel34.setText("0");
        jLabel36.setText("0");
        jLabel38.setText("0");
        jLabel39.setText("0");
        jLabel42.setText("0");
        
        jLabel44.setText("0");
        jTextField7.setText("0");
        jLabel47.setText("0");
        jTextField8.setText("0");
        jLabel53.setText("0");
        
        jTextField1.requestFocusInWindow();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
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
        jLabel15 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel28 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel51 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();

        jMenuItem1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jMenuItem1.setText("### DELETE SELECTED ITEM ###");
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

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("INV. NO.");

        jTextField1.setText("N/A");
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
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("INV. DATE");

        jDateChooser1.setDateFormatString("dd/MM/yyyy");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("SUPER STOCKIST");

        jLabel4.setBackground(new java.awt.Color(204, 255, 255));
        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("N/A");
        jLabel4.setOpaque(true);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("TRADE DISCOUNT %");

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
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setText("REPLACEMENT DISCOUNT %");

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

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "PURCHASED ITEMS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 13), new java.awt.Color(0, 0, 204))); // NOI18N

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
        jLabel8.setText("HSN");

        jLabel9.setBackground(new java.awt.Color(255, 255, 0));
        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("N/A");
        jLabel9.setOpaque(true);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("EX-GST");

        jLabel11.setBackground(new java.awt.Color(255, 255, 0));
        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("0");
        jLabel11.setOpaque(true);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setText("IN-GST");

        jLabel13.setBackground(new java.awt.Color(255, 255, 0));
        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("0");
        jLabel13.setOpaque(true);

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel14.setText("MRP");

        jLabel15.setBackground(new java.awt.Color(255, 255, 0));
        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("0");
        jLabel15.setOpaque(true);

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

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel16.setText("QTY.");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel17.setText("GROSS");

        jLabel18.setBackground(new java.awt.Color(255, 255, 0));
        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("0");
        jLabel18.setOpaque(true);

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("DISCOUNT AMT.");

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
        jLabel20.setText("TRADE DIS. AMT.");

        jLabel21.setBackground(new java.awt.Color(255, 255, 0));
        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("0");
        jLabel21.setOpaque(true);

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel22.setText("REPL. DIS. AMT.");

        jLabel23.setBackground(new java.awt.Color(255, 255, 0));
        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("0");
        jLabel23.setOpaque(true);

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel24.setText("TAXABLE AMT.");

        jLabel25.setBackground(new java.awt.Color(255, 255, 0));
        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("0");
        jLabel25.setOpaque(true);

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel26.setText("GST AMT.");

        jLabel27.setBackground(new java.awt.Color(255, 255, 0));
        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("0");
        jLabel27.setOpaque(true);

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton2.setText("ADD");
        jButton2.setBorder(null);
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
                "SLN.", "PARTICULAR", "MRP", "HSN", "GST %", "QTY.", "UOM", "RATE", "DISC. AMT.", "GROSS"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/SEARCH.PNG"))); // NOI18N
        jLabel28.setText("ITEM");

        jTextField6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField6KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField6KeyReleased(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel29.setText("TOTAL NO. OF ITEMS");

        jLabel30.setBackground(new java.awt.Color(255, 255, 0));
        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("0");
        jLabel30.setOpaque(true);

        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel31.setText("NET GROSS");

        jLabel32.setBackground(new java.awt.Color(255, 255, 0));
        jLabel32.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText("0");
        jLabel32.setOpaque(true);

        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel33.setText("NET ITEM DISC.");

        jLabel34.setBackground(new java.awt.Color(255, 255, 0));
        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("0");
        jLabel34.setOpaque(true);

        jLabel35.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel35.setText("NET TRADE DISCOUNT AMT.");

        jLabel36.setBackground(new java.awt.Color(255, 255, 0));
        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("0");
        jLabel36.setOpaque(true);

        jLabel37.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel37.setText("NET REPLACEMENT DISCOUNT AMT.");

        jLabel38.setBackground(new java.awt.Color(255, 255, 0));
        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("0");
        jLabel38.setOpaque(true);

        jLabel39.setBackground(new java.awt.Color(0, 0, 255));
        jLabel39.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(255, 255, 255));
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("0");
        jLabel39.setOpaque(true);

        jLabel40.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel40.setText("NET TAXABLE AMT.");

        jLabel41.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel41.setText("NET GST AMT.");

        jLabel42.setBackground(new java.awt.Color(255, 255, 0));
        jLabel42.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setText("0");
        jLabel42.setOpaque(true);

        jLabel48.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel48.setText("GST");

        jLabel49.setBackground(new java.awt.Color(255, 255, 0));
        jLabel49.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel49.setText("0");
        jLabel49.setOpaque(true);

        jLabel50.setBackground(new java.awt.Color(255, 255, 0));
        jLabel50.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel50.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel50.setText("%");
        jLabel50.setOpaque(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel37)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel40)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel41)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel29)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel31)
                                .addGap(1, 1, 1)
                                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel33)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addGap(6, 6, 6)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel48)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addGap(6, 6, 6)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16)
                                .addGap(36, 36, 36)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel48)
                    .addComponent(jLabel49)
                    .addComponent(jLabel50)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24)
                    .addComponent(jLabel25)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27)
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel28)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29)
                    .addComponent(jLabel30)
                    .addComponent(jLabel31)
                    .addComponent(jLabel32)
                    .addComponent(jLabel33)
                    .addComponent(jLabel34)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel35)
                        .addComponent(jLabel36)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel41)
                        .addComponent(jLabel42))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel37)
                        .addComponent(jLabel38)
                        .addComponent(jLabel39)
                        .addComponent(jLabel40)))
                .addGap(22, 22, 22))
        );

        jLabel43.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel43.setText("GROSS PAYABLE AMT.");

        jLabel44.setBackground(new java.awt.Color(0, 0, 255));
        jLabel44.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(255, 255, 255));
        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setText("0");
        jLabel44.setOpaque(true);

        jLabel45.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel45.setText("ROUND-OFF");

        jTextField7.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField7.setText("0");
        jTextField7.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField7FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField7FocusLost(evt);
            }
        });
        jTextField7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField7KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField7KeyReleased(evt);
            }
        });

        jLabel46.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel46.setText("NET PAYABLE AMT.");

        jLabel47.setBackground(new java.awt.Color(0, 0, 255));
        jLabel47.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel47.setForeground(new java.awt.Color(255, 255, 255));
        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel47.setText("0");
        jLabel47.setOpaque(true);

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton3.setText("UPDATE");
        jButton3.setBorder(null);
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

        jLabel51.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel51.setText("DISP. SCHEME");

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

        jLabel52.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel52.setText("FINAL AMT.");

        jLabel53.setBackground(new java.awt.Color(0, 0, 255));
        jLabel53.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel53.setForeground(new java.awt.Color(255, 255, 255));
        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel53.setText("0");
        jLabel53.setOpaque(true);

        jLabel54.setBackground(new java.awt.Color(255, 255, 0));
        jLabel54.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel54.setText("N/A");
        jLabel54.setOpaque(true);

        jLabel55.setBackground(new java.awt.Color(255, 204, 204));
        jLabel55.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel55.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel55.setText("N/A");
        jLabel55.setOpaque(true);

        jButton4.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton4.setText("DELETE");
        jButton4.setBorder(null);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jButton4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton4KeyPressed(evt);
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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel43)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel45)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel46)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel51)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel52)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel54)
                    .addComponent(jLabel55))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel53)
                    .addComponent(jLabel52)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51)
                    .addComponent(jLabel47)
                    .addComponent(jLabel46)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45)
                    .addComponent(jLabel44)
                    .addComponent(jLabel43))
                .addGap(41, 41, 41))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jDateChooser1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField1KeyPressed

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
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
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
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jComboBox2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField3KeyPressed

    private void jComboBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox2ItemStateChanged
        jLabel9.setText("N/A");
        jLabel49.setText("0");
        jLabel11.setText("0");
        jLabel13.setText("0");
        jLabel15.setText("0");
        currentItemmid = null;
    }//GEN-LAST:event_jComboBox2ItemStateChanged

    private void jComboBox2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField4.requestFocusInWindow();
        }
        if (evt.getKeyCode() == KeyEvent.VK_F2)
        {
            addAlterItemDetails();
        }
        if (evt.getKeyCode() == KeyEvent.VK_F3)
        {
            jTextField7.requestFocusInWindow();
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
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
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
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jButton2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField5KeyPressed

    private void jTextField5KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyReleased
        calculation01();
    }//GEN-LAST:event_jTextField5KeyReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        addToList();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            addToList();
        }
    }//GEN-LAST:event_jButton2KeyPressed

    private void jTextField7FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField7FocusGained
        String s=jTextField7.getText().trim();
        if(Double.parseDouble(s) == 0) {
            jTextField7.setText("");
        } else {
            jTextField7.selectAll();
        }
    }//GEN-LAST:event_jTextField7FocusGained

    private void jTextField7FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField7FocusLost
        String s=jTextField7.getText().trim();
        if(s.length()==0) {
            jTextField7.setText("0");
        }
    }//GEN-LAST:event_jTextField7FocusLost

    private void jTextField7KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField8.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField7KeyPressed

    private void jTextField7KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyReleased
        computation02();
    }//GEN-LAST:event_jTextField7KeyReleased

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        updateToDatabase();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            updateToDatabase();
        }
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            jButton4.requestFocusInWindow();
        }
    }//GEN-LAST:event_jButton3KeyPressed

    private void jTextField6KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTextField7.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField6KeyPressed

    private void jTextField6KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyReleased
        if (psAl.size() != 0) {
            String searchString = jTextField6.getText().trim().toUpperCase();
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
                    JOptionPane.showMessageDialog(null,"EditDeletePurchaseSub03 ex: "+ex.getMessage(),
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
    }//GEN-LAST:event_jTextField6KeyReleased

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
                    jComboBox2.requestFocusInWindow();
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
                            jComboBox2.setSelectedItem(rs.getString("itemnm").replace("\\'", "'"));
                            jLabel9.setText(rs.getString("hsn"));
                        }
                    }
                    catch(SQLException ex)
                    {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null,"EditDeletePurchaseSub03 ex: "+ex,
                                "Error Found",JOptionPane.ERROR_MESSAGE);
                    }
                    finally
                    {
                        try {
                            if (conn!=null) conn.close();
                        }
                        catch(SQLException ex){}
                    }
                    jLabel49.setText(format2afterDecimal.format(Double.parseDouble(ps.getGst())));
                    jLabel11.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getExgst())));
                    jLabel13.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getIngst())));
                    jLabel15.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getMrp())));
                    jTextField4.setText(ps.getQty());
                    jTextField5.setText(format2afterDecimal.format(Double.parseDouble(ps.getDiscamt())));
                    calculation01();
                    psAl.remove(row);
                    Fetch();
                    computation02();
                    jComboBox2.requestFocusInWindow();
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

    private void formInternalFrameIconified(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameIconified
        moveToFront();
    }//GEN-LAST:event_formInternalFrameIconified

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        deleteFromDatabase();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton4KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            deleteFromDatabase();
        }
        if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            jButton3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jButton4KeyPressed

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
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jButton3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField8KeyPressed

    private void jTextField8KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField8KeyReleased
        computation02();
    }//GEN-LAST:event_jTextField8KeyReleased

    private void jTextField1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusGained
        String s=jTextField1.getText().trim();
        if(s.equals("N/A")) {
            jTextField1.setText("");
        } else {
            jTextField1.selectAll();
        }
    }//GEN-LAST:event_jTextField1FocusGained

    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusLost
        String s=jTextField1.getText().trim();
        if(s.length()==0) {
            jTextField1.setText("N/A");
        }
    }//GEN-LAST:event_jTextField1FocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
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
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
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
    private javax.swing.JTextField jTextField8;
    // End of variables declaration//GEN-END:variables
}
