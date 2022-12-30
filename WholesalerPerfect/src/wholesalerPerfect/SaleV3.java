package wholesalerPerfect;

import com.toedter.calendar.JTextFieldDateEditor;
import conn.dBConnection;
import dto.Enterprise;
import dto.SaleMasterV2;
import dto.SaleSubV2;
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
import utilities.Add0Padding6;
import utilities.MyNumberFormat;
import utilities.Settings;

public class SaleV3 extends javax.swing.JInternalFrame implements AWTEventListener {

    private Settings settings=new Settings();
    private Query q=new Query();
    private JDesktopPane jDesktopPane1;
    private UserProfile up;
    private Enterprise e;
    private DecimalFormat format = new DecimalFormat("0.#");
    private DecimalFormat format2afterDecimal = new DecimalFormat("#.##");
    private boolean isFromOtherWindow;
    
    private String compidArray[];
    private String retidArray[];
    private String beatabbrArray[];
    private String itemmidArray[];
    private String hsnArray[];
    private String newRetnm;
    private String currentItemmid;
    private String currentPsidWithItemDetails;
    private String currentPsid;
    private String currentItemdid;
    private int avlqty;
    private ArrayList<SaleSubV2> ssAl = new ArrayList<SaleSubV2>();
    private String currentGstAmt;
    private SaleMasterV2 sm;
    
    public SaleV3(JDesktopPane jDesktopPane, UserProfile up, Enterprise e, boolean isFromOtherWindow) {
        super("Sale",false,true,false,true);
        initComponents(); 
        this.jDesktopPane1 = jDesktopPane;
        this.up = up;
        this.e = e;
        this.isFromOtherWindow = isFromOtherWindow;
        Dimension d=getSize();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) dim.getWidth() - (int)d.getWidth())/2,((int) dim.getHeight() - (int)d.getHeight())/2-40);
        this.setResizable(false);
        this.setFrameIcon(new ImageIcon(getClass().getResource("/images/prod_sale_01.png")));
        
        this.getActionMap().put("test", new AbstractAction(){  //ESCAPE
            @Override
            public void actionPerformed(ActionEvent e) {                
                setVisible(false);
                dispose();
            }
        });
        InputMap map = this.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        map.put(stroke,"test");
        
        ((DefaultTableCellRenderer)jTable1.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        ((JLabel)jComboBox1.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        ((JLabel)jComboBox2.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        ((JLabel)jComboBox3.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        settings.numvalidatorFloat(jTextField6);
        settings.numvalidatorInt(jTextField7);
        settings.numvalidatorInt(jTextField8);
        settings.numvalidatorFloat(jTextField9);
        settings.numvalidatorFloatWithSign(jTextField11);
        settings.numvalidatorFloat(jTextField12);
        
        jDateChooser1.setDate(new Date());
        jDateChooser1.getToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
        jDateChooser1.getDateEditor().getUiComponent().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                ((JTextFieldDateEditor)evt.getSource()).selectAll();
            }
        });
        String sDate1="01/01/2000";  
        Date date1=null;
        try {
            date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
        }
        catch (ParseException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Purchase ex?: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        jDateChooser2.setDate(date1);
        jDateChooser2.getToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
        jDateChooser2.getDateEditor().getUiComponent().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                ((JTextFieldDateEditor)evt.getSource()).selectAll();
            }
        });
        jDateChooser3.setDate(date1);
        jDateChooser3.getToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
        jDateChooser3.getDateEditor().getUiComponent().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                ((JTextFieldDateEditor)evt.getSource()).selectAll();
            }
        });
        
        populateCombo1();
        populateCombo2();
        
        jTable1.setComponentPopupMenu(jPopupMenu1);
        
        SwingUtilities.invokeLater (() -> {
            jComboBox1.requestFocusInWindow();
        });
    }
    
    @Override
    public void eventDispatched(AWTEvent event) {
        if(event instanceof KeyEvent) {
            KeyEvent key = (KeyEvent)event;
            if(key.getID()==KeyEvent.KEY_PRESSED) {
                if(event.getSource().equals(jDateChooser1.getDateEditor())&&key.getKeyCode()==10) {
                    jTextField1.requestFocusInWindow();
                }
                if(event.getSource().equals(jDateChooser2.getDateEditor())&&key.getKeyCode()==10) {
                    jComboBox2.requestFocusInWindow();
                }
                if(event.getSource().equals(jDateChooser3.getDateEditor())&&key.getKeyCode()==10) {
                    jTextField6.requestFocusInWindow();
                }
            }
        }
    }
    
    private void populateCombo1() { // Company
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in CompanyMaster: 4
        /* compid, compnm, compabbr, isactive */
        String query="select compid, compabbr from CompanyMaster where isactive=1 order by compabbr asc";
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
                compidArray=new String[total];
                jComboBox1.addItem("-- Select --");
                int i=0;
                while(rs.next()) {
                    compidArray[i++]=rs.getString("compid");
                    jComboBox1.addItem(rs.getString("compabbr"));
                }
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"SaleV3 ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        } finally {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e) {}
        }
    }
    
    private void populateCombo2() { // Retailer
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in Retailer: 18
        /* retid, beatid, retnm, contactperson, rstreet, rcity, rdist, rstate, rstatecode, 
        rpin, rcountry, rcontact, rmail, rgstno, rgstregntype, rpanno, raadhaarno, isactive*/
        // Number of columns in BeatMaster: 4
        /* beatid, beatnm, beatabbr, isactive */
        String query="select retid, retnm, beatabbr from (select retid, beatid, retnm from "
                + "Retailer where isactive=1) x, (select beatid, beatabbr from BeatMaster"
                + " where isactive=1) y where x.beatid=y.beatid order by retnm asc";
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
                retidArray=new String[total];
                beatabbrArray = new String[total];
                jComboBox2.addItem("-- Select --");
                int i=0;
                while(rs.next()) {
                    retidArray[i]=rs.getString("retid");
                    String beatabbr = rs.getString("beatabbr");
                    beatabbrArray[i] = beatabbr;
                    jComboBox2.addItem(rs.getString("retnm"));
                    i++;
                }
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"SaleV3 ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        } finally {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e) {}
        }
    }
    
    private String getNextSaleInvoiceNo() {
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
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"SaleV3 ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        String compabbr = null;
        try {
            compabbr = jComboBox1.getSelectedItem().toString();
        } catch(java.lang.NullPointerException ex) {
            return null;
        }
        query="select IFNULL(max(salemid),'') as x from SaleMasterV2 where salemid like '"
                    + e.getEabbr()+compabbr+"/______/"+financialcode+"'";
        System.out.println(query);
        try {
            Statement smt=conn.createStatement();
            ResultSet rs=smt.executeQuery(query);
            if(rs.next()) {
                String lastTotalID=rs.getString("x");
                if(lastTotalID.length()!=0) {
                    String lastID=lastTotalID.substring(lastTotalID.indexOf("/")+1,lastTotalID.lastIndexOf("/"));
                    total=Integer.parseInt(lastID);
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"SaleV3 ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        } finally {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException ex){}
        }
        total++;
        return e.getEabbr()+compabbr+"/"+Add0Padding6.add0Padding(total)+"/"+financialcode;
    }
    
    private void populateCombo3() { // Item HSN
        if (jComboBox3.getSelectedIndex()==0) {
            return;
        }
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in ItemMaster: 5
        /* itemmid, compid, itemnm, hsn, isactive */
        // Number of columns in ItemDetails: 10
        /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
        // Table PurchaseSubV2, no. of columns - 12
        /*
        psid, pmid, itemdid, mrp, gst, qty, rate, discper, discamt, amount, qtysold, retqty
        */
        String query="select y.itemmid, hsn, itemnm from (select itemmid, itemnm, hsn from ItemMaster where isactive=1 "
                + "and compid="+compidArray[jComboBox1.getSelectedIndex()-1]+") x, (select distinct itemmid from "
                + "ItemDetails where isactive=1 and itemdid in (select distinct itemdid from PurchaseSubV2 where "
                + "qty-(qtysold+retqty)>0)) y where x.itemmid=y.itemmid order by itemnm";
        System.out.println(query);
        try {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(query);
            int total = 0;
            //Move to the last record
            rs.afterLast();
            //Get the current record position
            if(rs.previous()) {
                total = rs.getRow();
            }
            //Move back to the first record;
            rs.beforeFirst();
            jComboBox3.removeAllItems();
            if(total != 0) {
                itemmidArray=new String[total];
                hsnArray=new String[total];
                jComboBox3.addItem("-- Select --");
                int i=0;
                while(rs.next()) {
                    itemmidArray[i]=rs.getString("itemmid");
                    jComboBox3.addItem(rs.getString("itemnm"));
                    hsnArray[i]=rs.getString("hsn");
                    i++;
                }
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"SaleV3 ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        } finally {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e) {}
        }
    }
    
    private void addAlterRetailer() {
        Thread t = new Thread(() -> {
            try {
                setVisible(false);
                final RetailerMaster ref=new RetailerMaster(jDesktopPane1, true, up);
                ref.addInternalFrameListener(new InternalFrameAdapter() {
                    @Override
                    public void internalFrameDeactivated(InternalFrameEvent e1) {
                        newRetnm=ref.getNewRetnm();
                    }

                    @Override
                    public void internalFrameClosed(InternalFrameEvent e2) {
                        SaleV3.this.setVisible(true);
                        if(newRetnm!=null) {
                            populateCombo2();
                            jComboBox2.setSelectedItem(newRetnm);
                            try {
                                if(((String)jComboBox2.getSelectedItem()).equals("-- Select --")) {
                                    jLabel6.setText("N/A");
                                } else {
                                    jLabel6.setText(beatabbrArray[jComboBox2.getSelectedIndex()-1]);
                                }
                            } catch(NullPointerException ex){}
                        }
                    }
                });
                ref.setVisible(true);
                jDesktopPane1.add(ref);
                ref.show();
                ref.setIcon(false);
                ref.setSelected(true);
            } catch (PropertyVetoException e3) {
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    private void getItemFromPurchase() {
        Thread t = new Thread(() -> {
            try {
                setVisible(false);
                final ItemDetails4Sale ref=new ItemDetails4Sale(currentItemmid);
                ref.addInternalFrameListener(new InternalFrameAdapter() {
                    @Override
                    public void internalFrameDeactivated(InternalFrameEvent e1) {
                        currentPsidWithItemDetails=ref.getSelectedPsidWithItemDetails();
                    }

                    @Override
                    public void internalFrameClosed(InternalFrameEvent e2) {
                        SaleV3.this.setVisible(true);
                        if(currentPsidWithItemDetails!=null) {
                            //   0         1           2        3       4        5           6          7         8
                            // psid+"~"+itemdid+"~"+avlqty+"~"+mrp+"~"+gst+"~"+pexgst+"~"+pingst+"~"+sexgst+"~"+singst
                            String s[] = currentPsidWithItemDetails.split("~");
                            currentPsid = s[0];
                            currentItemdid = s[1];
                            avlqty = Integer.parseInt(s[2]);
                            jLabel19.setText(s[3]); // MRP
                            jLabel21.setText(s[4]); // GST
                            jTextField7.setText(s[2]); // QTY.
                            computation01();
                            jTextField7.requestFocusInWindow();
                        }
                    }
                });
                ref.setVisible(true);
                jDesktopPane1.add(ref);
                ref.show();
                ref.setIcon(false);
                ref.setSelected(true);
            } catch (PropertyVetoException e3) {
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    private void computation01() {
        try {
            int qty = Integer.parseInt(jTextField7.getText());
            int free = Integer.parseInt(jTextField8.getText());
            if ( (qty+free) > avlqty ) {
                JOptionPane.showMessageDialog(null,"Quantity for Sale can't be > available quantity in Purchase !!!",
                        "Error Found",JOptionPane.ERROR_MESSAGE);
                jTextField7.selectAll();
                jTextField7.requestFocusInWindow();
                return;
            }
            double unitNetRate = Double.parseDouble(jTextField9.getText()); //*
            double cashDiscper = Double.parseDouble(jTextField6.getText());
            double calculatedCashDiscper = (1-(cashDiscper / 100.0)); //*
            double gstper = Double.parseDouble(jLabel21.getText().trim().replaceAll(",", ""));
            double calculatedGstper = (1 + (gstper / 100.0)); //*
            // Actual Rate Calculation
            double rate = unitNetRate / (calculatedCashDiscper * calculatedGstper);
            jLabel26.setText(MyNumberFormat.rupeeFormat(rate));
            double gross = rate * qty;
            jLabel28.setText(MyNumberFormat.rupeeFormat(gross));
            double cashDiscamt = gross * (cashDiscper / 100.0);
            jLabel30.setText(MyNumberFormat.rupeeFormat(cashDiscamt));
            double amtAfterCashDisc = gross - cashDiscamt;
            double gstamt = amtAfterCashDisc * (gstper / 100.0);
            currentGstAmt = format.format(gstamt);
            jLabel32.setText(MyNumberFormat.rupeeFormat(gstamt / 2.0));
            jLabel34.setText(MyNumberFormat.rupeeFormat(gstamt / 2.0));
            double amount = amtAfterCashDisc + gstamt;
            jLabel36.setText(MyNumberFormat.rupeeFormat(amount));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void addToList() {
        // Table PurchaseSubV2, no. of columns - 12
        /*
        psid, pmid, itemdid, mrp, gst, qty, rate, discper, discamt, amount, qtysold, retqty
        */
        if (currentPsid == null || currentItemdid == null) {
            JOptionPane.showMessageDialog(null,"Item should be selected from Purchase using F5 !!!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox3.requestFocusInWindow();
            return;
        }
        // Checking duplicate for same psid
        if ( !ssAl.isEmpty() ) {
            for ( SaleSubV2 ss : ssAl ) {
                if ( ss.getPsid().equals(currentPsid)) {
                    JOptionPane.showMessageDialog(null,"Duplicate Purchase-Item Selection !!!",
                            "Error Found",JOptionPane.ERROR_MESSAGE);
                    currentPsid = null;
                    currentItemmid = null;
                    currentItemdid = null;
                    currentPsidWithItemDetails = null;
                    currentGstAmt = null;

                    jComboBox3.setSelectedIndex(0);
                    innerFormFlush();

                    jComboBox3.requestFocusInWindow();
                    return;
                }
            }
        }
        if ( jComboBox3.getSelectedIndex() == 0 )
        {
            JOptionPane.showMessageDialog(null,"Select proper item !!!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox3.requestFocusInWindow();
            return;
        }
        // Already have "psid" as currentPsid & "itemid" as currentItemid
        // Table SaleSubV2, no. of columns - 16
        /*
        salesid, salemid, psid, itemdid, mrp, gst, qty, free, rate, gross, itemdiscper, 
        itemdiscamt, cashdiscamt, gstamt, amount, retqty
        */
        String qty = jTextField7.getText();
        if(Integer.parseInt(qty) == 0)
        {
            JOptionPane.showMessageDialog(null,"Quantity is mandatory !!!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField7.requestFocusInWindow();
            return;
        }
        String free = jTextField8.getText();
        if (Integer.parseInt(qty)+Integer.parseInt(free) > avlqty) {
            JOptionPane.showMessageDialog(null,(qty+free)+" Pcs. item not available in selected purchase !!!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField7.requestFocusInWindow();
            return;
        }
        // Table SaleSubV2, no. of columns - 16
        /*
        salesid, salemid, psid, itemdid, mrp, gst, qty, free, rate, gross, itemdiscper, 
        itemdiscamt, cashdiscamt, gstamt, amount, retqty
        */
        String mrp = jLabel19.getText().trim().replaceAll(",", "");
        String gst = jLabel21.getText().trim().replaceAll(",", "");
        String rate = jLabel26.getText().trim().replaceAll(",", "");
        String gross = jLabel28.getText().trim().replaceAll(",", "");
        String itemdiscper = "0";
        String itemdiscamt = "0";
        String cashdiscamt = jLabel30.getText().trim().replaceAll(",", "");
        // currentGstAmt should be treated as gstamt
        String amount = jLabel36.getText().trim().replaceAll(",", "");
        String retqty = "0";
        
        SaleSubV2 ss = new SaleSubV2();
        // Table SaleSubV2, no. of columns - 16
        /*
        salesid, salemid, psid, itemdid, mrp, gst, qty, free, rate, gross, itemdiscper, 
        itemdiscamt, cashdiscamt, gstamt, amount, retqty
        */
        ss.setSalesid(""); // At Insert
        ss.setSalemid(""); // At Insert
        ss.setPsid(currentPsid);
        ss.setItemdid(currentItemdid);
        ss.setMrp(mrp);
        ss.setGst(gst);
        ss.setQty(qty);
        ss.setFree(free);
        ss.setRate(rate);
        ss.setGross(gross);
        ss.setItemdiscper(itemdiscper);
        ss.setItemdiscamt(itemdiscamt);
        ss.setCashdiscamt(cashdiscamt);
        ss.setGstamt(currentGstAmt);
        ss.setAmount(amount);
        ss.setRetqty(retqty);
        ssAl.add(ss);
        
        Fetch();
        
        currentPsid = null;
        currentItemmid = null;
        currentItemdid = null;
        currentPsidWithItemDetails = null;
        currentGstAmt = null;
        
        jComboBox3.setSelectedIndex(0);
        innerFormFlush();
        
        jComboBox3.requestFocusInWindow();
    }
    
    private void innerFormFlush() {
        jLabel17.setText("N/A");
        jLabel19.setText("0");
        jLabel21.setText("0");
        jTextField7.setText("0");
        jTextField8.setText("0");
        jTextField9.setText("0");
        jLabel26.setText("0");
        jLabel28.setText("0");
        jLabel30.setText("0");
        jLabel32.setText("0");
        jLabel34.setText("0");
        jLabel36.setText("0");
    }
    
    private void clearTable(JTable table) {
        for(int i=table.getRowCount()-1; i>=0; i--) {
            ((DefaultTableModel)table.getModel()).removeRow(i);
        }
    }
    
    private void Fetch() {
        int netqty = 0;
        double netgross = 0.0;
        double netgstamt = 0.0;
        double netcashdiscamt = 0.0;
        double netamount = 0.0;
        
        int slno=0;
        clearTable(jTable1);
        
        // Table SaleSubV2, no. of columns - 16
        /*
        salesid, salemid, psid, itemdid, mrp, gst, qty, free, rate, gross, itemdiscper, 
        itemdiscamt, cashdiscamt, gstamt, amount, retqty
        */
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        for (SaleSubV2 ss :  ssAl) {
            Vector<String> row = new Vector<String>();
            row.addElement(++slno+"");
            
            // Item Sale Table - 13
            /*
            SLN., PUR. INV., ITEM, MRP, GST%, QTY., FREE, 
            RATE, GROSS, CD AMT., CGST AMT., SGST AMT., AMOUNT
            */
            // Table PurchaseMasterV2, no. of columns - 20
            /*
            pmid, ssid, compid, invno, invdt, netqty, netitemdisc, netgross, tradediscper, nettradediscamt, 
            replacementdiscper, netreplacementdiscamt, netgstamt, amtaftergst, roundoff, dispscheme, 
            netpayableamt, isopening, amtpaid, isactive
            */
            // Table PurchaseSubV2, no. of columns - 12
            /*
            psid, pmid, itemdid, mrp, gst, qty, rate, discper, discamt, amount, qtysold, retqty
            */
            String query="select invno from PurchaseMasterV2, PurchaseSubV2 where PurchaseMasterV2.pmid="
                    + "PurchaseSubV2.pmid and PurchaseSubV2.psid="+ss.getPsid();
            System.out.println(query);
            try {
                Statement smt=conn.createStatement();
                ResultSet rs=smt.executeQuery(query);
                if (rs.next()) {
                    row.addElement(rs.getString("invno"));
                }
            } catch(SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"SaleV2 ex: "+ex.getMessage(),
                        "Error Found",JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Number of columns in ItemMaster: 5
            /* itemmid, compid, itemnm, hsn, isactive */
            // Number of columns in ItemDetails: 10
            /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
            query="select itemnm, hsn from ItemMaster, ItemDetails where ItemMaster.itemmid="
                    + "ItemDetails.itemmid and itemdid="+ss.getItemdid();
            System.out.println(query);
            try {
                Statement smt=conn.createStatement();
                ResultSet rs=smt.executeQuery(query);
                if (rs.next()) {
                    row.addElement(rs.getString("itemnm")+"["+rs.getString("hsn")+"]");
                }
            } catch(SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"SaleV2 ex: "+ex.getMessage(),
                        "Error Found",JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Item Sale Table - 13
            /*
            SLN., PUR. INV., ITEM, MRP, GST%, QTY., FREE, 
            RATE, GROSS, CD AMT., CGST AMT., SGST AMT., AMOUNT
            */
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getMrp())));
            row.addElement(format.format(Double.parseDouble(ss.getGst())));
            netqty += Integer.parseInt(ss.getQty()) + Integer.parseInt(ss.getFree());
            row.addElement(ss.getQty());
            row.addElement(ss.getFree());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getRate())));
            netgross += Double.parseDouble(ss.getGross());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getGross())));
            netcashdiscamt += Double.parseDouble(ss.getCashdiscamt());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getCashdiscamt())));
            double gstamt = Double.parseDouble(ss.getGstamt());
            netgstamt += gstamt;
            row.addElement(MyNumberFormat.rupeeFormat(gstamt / 2.0));
            row.addElement(MyNumberFormat.rupeeFormat(gstamt / 2.0));
            netamount += Double.parseDouble(ss.getAmount());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getAmount())));
            
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
        // Item Sale Table - 13
        /*
        SLN., PUR. INV., ITEM, MRP, GST%, QTY., FREE, 
        RATE, GROSS, CD AMT., CGST AMT., SGST AMT., AMOUNT
        */
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN.
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(40);
        jTable1.getColumnModel().getColumn(1).setMinWidth(0);// PUR. INV.
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);// ITEM
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(250);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// MRP
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);// GST%
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);// QTY.
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(6).setMinWidth(0);// FREE
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(7).setMinWidth(0);// RATE
        jTable1.getColumnModel().getColumn(7).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(8).setMinWidth(0);// GROSS
        jTable1.getColumnModel().getColumn(8).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(9).setMinWidth(0);// CD AMT.
        jTable1.getColumnModel().getColumn(9).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(10).setMinWidth(0);// CGST AMT.
        jTable1.getColumnModel().getColumn(10).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(11).setMinWidth(0);// SGST AMT.
        jTable1.getColumnModel().getColumn(11).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(12).setMinWidth(0);// AMOUNT
        jTable1.getColumnModel().getColumn(12).setPreferredWidth(120);
        
        // Item Sale Table - 13
        /*
        SLN., PUR. INV., ITEM, MRP, GST%, QTY., FREE, 
        RATE, GROSS, CD AMT., CGST AMT., SGST AMT., AMOUNT
        */
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable1.getColumn("SLN.").setCellRenderer( centerRenderer );
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        jTable1.getColumn("MRP").setCellRenderer( rightRenderer );
        jTable1.getColumn("GST%").setCellRenderer( rightRenderer );
        jTable1.getColumn("QTY.").setCellRenderer( rightRenderer );
        jTable1.getColumn("FREE").setCellRenderer( rightRenderer );
        jTable1.getColumn("RATE").setCellRenderer( rightRenderer );
        jTable1.getColumn("GROSS").setCellRenderer( rightRenderer );
        jTable1.getColumn("CD AMT.").setCellRenderer( rightRenderer );
        jTable1.getColumn("CGST AMT.").setCellRenderer( rightRenderer );
        jTable1.getColumn("SGST AMT.").setCellRenderer( rightRenderer );
        jTable1.getColumn("AMOUNT").setCellRenderer( rightRenderer );
        
        /*
        int netqty = 0;
        double netgross = 0.0;
        double netgstamt = 0.0;
        double netcashdiscamt = 0.0;
        double netamount = 0.0;
        */
        jLabel30.setText(netqty+"");
        jLabel41.setText(MyNumberFormat.rupeeFormat(netgross));
        jLabel45.setText(MyNumberFormat.rupeeFormat(netcashdiscamt));
        jLabel47.setText(MyNumberFormat.rupeeFormat(netgstamt));
        jLabel49.setText(MyNumberFormat.rupeeFormat(netamount));
        computation02();
    }
    
    private void computation02() {
        try {
            double netamount = Double.parseDouble(jLabel49.getText().replaceAll(",", "").trim());
            double roundoff = Double.parseDouble(jTextField11.getText());
            double amountAfterRoundoff = netamount + roundoff;
            double displayscheme = Double.parseDouble(jTextField12.getText());
            double netpayableamt = amountAfterRoundoff - displayscheme;
            jLabel53.setText(MyNumberFormat.rupeeFormat(netpayableamt));
        } catch(java.lang.NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jDateChooser3 = new com.toedter.calendar.JDateChooser();
        jLabel14 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel37 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jLabel51 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        jMenuItem1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jMenuItem1.setText("EDIT SELECTED ITEM");
        jPopupMenu1.add(jMenuItem1);

        jMenuItem2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jMenuItem2.setText("DELETE SELECTED ITEM");
        jPopupMenu1.add(jMenuItem2);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("COMPANY");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("INV. DT.");

        jDateChooser1.setDateFormatString("dd/MM/yyyy");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("ORD. NO.");

        jDateChooser2.setDateFormatString("dd/MM/yyyy");

        jTextField1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("ORD. DT.");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("RETAILER");

        jLabel6.setBackground(new java.awt.Color(255, 255, 153));
        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("N/A");
        jLabel6.setOpaque(true);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("INV. NO.");

        jLabel8.setBackground(new java.awt.Color(255, 255, 153));
        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("N/A");
        jLabel8.setOpaque(true);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setText("DELIVERY NOTE");

        jTextField2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField2.setText("N/A");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("PAYMENT TERM");

        jTextField3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField3.setText("N/A");

        jTextField4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField4.setText("N/A");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel11.setText("TRANSPORTER");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setText("VEHICLE NO.");

        jTextField5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField5.setText("N/A");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel13.setText("SUPPLY DT.");

        jDateChooser3.setDateFormatString("dd/MM/yyyy");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel14.setText("CASH DISC %");

        jTextField6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField6.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField6.setText("0");

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton1.setText("START");

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 102), 2, true), "ITEM DETAILS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(0, 0, 204))); // NOI18N

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel15.setText("ITEM + <F5>");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel16.setText("HSN");

        jLabel17.setBackground(new java.awt.Color(255, 255, 153));
        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("N/A");
        jLabel17.setOpaque(true);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel18.setText("MRP");

        jLabel19.setBackground(new java.awt.Color(255, 255, 153));
        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("0");
        jLabel19.setOpaque(true);

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel20.setText("GST %");

        jLabel21.setBackground(new java.awt.Color(255, 255, 153));
        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("0");
        jLabel21.setOpaque(true);

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel22.setText("QTY. + FREE");

        jTextField7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField7.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField7.setText("0");

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel23.setText("+");

        jTextField8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField8.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField8.setText("0");

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel24.setText("UNIT NET RATE");

        jTextField9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField9.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField9.setText("0");

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel25.setText("RATE");

        jLabel26.setBackground(new java.awt.Color(255, 255, 153));
        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("0");
        jLabel26.setOpaque(true);

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel27.setText("GROSS");

        jLabel28.setBackground(new java.awt.Color(255, 255, 153));
        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("0");
        jLabel28.setOpaque(true);

        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel29.setText("CD AMT.");

        jLabel30.setBackground(new java.awt.Color(255, 255, 153));
        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("0");
        jLabel30.setOpaque(true);

        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel31.setText("GST AMT.");

        jLabel32.setBackground(new java.awt.Color(255, 255, 153));
        jLabel32.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText("0");
        jLabel32.setOpaque(true);

        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel33.setText("+");

        jLabel34.setBackground(new java.awt.Color(255, 255, 153));
        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("0");
        jLabel34.setOpaque(true);

        jLabel35.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel35.setText("NET AMT.");

        jLabel36.setBackground(new java.awt.Color(255, 255, 153));
        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("0");
        jLabel36.setOpaque(true);

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton2.setText("ADD");

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "ADDED ITEMS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SLN.", "PUR. INV.", "ITEM", "MRP", "GST%", "QTY.", "FREE", "RATE", "GROSS", "CD AMT.", "CGST AMT.", "SGST AMT.", "AMOUNT"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel37.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/SEARCH.PNG"))); // NOI18N
        jLabel37.setText("ITEM SEARCH");

        jTextField10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel38.setText("ITEM TOT.");

        jLabel39.setBackground(new java.awt.Color(255, 255, 153));
        jLabel39.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("0");
        jLabel39.setOpaque(true);

        jLabel40.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel40.setText("NET GROSS");

        jLabel41.setBackground(new java.awt.Color(255, 255, 153));
        jLabel41.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel41.setText("0");
        jLabel41.setOpaque(true);

        jLabel44.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel44.setText("NET CD AMT.");

        jLabel45.setBackground(new java.awt.Color(255, 255, 153));
        jLabel45.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel45.setText("0");
        jLabel45.setOpaque(true);

        jLabel46.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel46.setText("NET GST AMT.");

        jLabel47.setBackground(new java.awt.Color(255, 255, 153));
        jLabel47.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel47.setText("0");
        jLabel47.setOpaque(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel38)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel40)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel44)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel46)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel29)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel33)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel15)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel24)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27)
                    .addComponent(jLabel28)
                    .addComponent(jLabel29)
                    .addComponent(jLabel30)
                    .addComponent(jLabel31)
                    .addComponent(jLabel32)
                    .addComponent(jLabel33)
                    .addComponent(jLabel34)
                    .addComponent(jLabel35)
                    .addComponent(jLabel36)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37)
                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38)
                    .addComponent(jLabel39)
                    .addComponent(jLabel40)
                    .addComponent(jLabel41)
                    .addComponent(jLabel44)
                    .addComponent(jLabel45)
                    .addComponent(jLabel46)
                    .addComponent(jLabel47))
                .addContainerGap())
        );

        jLabel48.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel48.setText("NET AMT.");

        jLabel49.setBackground(new java.awt.Color(255, 255, 153));
        jLabel49.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel49.setText("0");
        jLabel49.setOpaque(true);

        jLabel50.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel50.setText("ROUND OFF");

        jTextField11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField11.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField11.setText("0");

        jLabel51.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel51.setText("DISP. SCHEME");

        jTextField12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField12.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField12.setText("0");

        jLabel52.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel52.setText("TOTAL");

        jLabel53.setBackground(new java.awt.Color(255, 255, 153));
        jLabel53.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel53.setText("0");
        jLabel53.setOpaque(true);

        jLabel54.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel54.setText("REMARKS");

        jTextField13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField13.setText("N/A");

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/SAVE.PNG"))); // NOI18N
        jButton3.setText("SAVE ONLY");

        jButton4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/PRINT.PNG"))); // NOI18N
        jButton4.setText("PRINT AND SAVE");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser3, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel48)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel50)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel51)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel52)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel54)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField13))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel13)
                        .addComponent(jDateChooser3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(jLabel49)
                    .addComponent(jLabel50)
                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51)
                    .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel52)
                    .addComponent(jLabel53)
                    .addComponent(jLabel54)
                    .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private com.toedter.calendar.JDateChooser jDateChooser3;
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
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
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
