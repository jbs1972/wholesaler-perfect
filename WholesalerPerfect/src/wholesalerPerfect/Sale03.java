package wholesalerPerfect;

import com.toedter.calendar.JTextFieldDateEditor;
import conn.dBConnection;
import dto.Enterprise;
import dto.SaleMaster;
import dto.SaleSub;
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
import print.printSaleBill01;
import query.Query;
import utilities.Add0Padding6;
import utilities.DateConverter;
import utilities.MyNumberFormat;
import utilities.Settings;

public class Sale03 extends javax.swing.JInternalFrame implements AWTEventListener {

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
    private String newRetnm;
    private String itemmidArray[];
    private String hsnArray[];
    private SaleMaster sm;
    private ArrayList<SaleSub> ssAl = new ArrayList<SaleSub>();
    private String currentItemmid;
    private String currentPsidWithItemDetails;
    private String currentPsid;
    private String currentItemdid;
    private int avlqty;
    
    public Sale03(JDesktopPane jDesktopPane, UserProfile up, Enterprise e, boolean isFromOtherWindow) {
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
        
        settings.numvalidatorInt(jTextField6);
        settings.numvalidatorFloat(jTextField7);
        settings.numvalidatorFloat(jTextField9);
        settings.numvalidatorFloatWithSign(jTextField10);
        
        jTable1.setComponentPopupMenu(jPopupMenu1);
        
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
        jDateChooser2.getDateEditor().getUiComponent().addFocusListener(new FocusAdapter() 
        {
            @Override
            public void focusGained(FocusEvent evt) 
            {
                ((JTextFieldDateEditor)evt.getSource()).selectAll();
            }
        });
        jDateChooser3.setDate(date1);
        jDateChooser3.getToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
        jDateChooser3.getDateEditor().getUiComponent().addFocusListener(new FocusAdapter() 
        {
            @Override
            public void focusGained(FocusEvent evt) 
            {
                ((JTextFieldDateEditor)evt.getSource()).selectAll();
            }
        });
        
        populateCombo1();
        populateCombo2();
        
        SwingUtilities.invokeLater
        (
            new Runnable() 
            {
                @Override
                public void run() 
                {
                    jComboBox1.requestFocusInWindow();
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
                if(event.getSource().equals(jDateChooser2.getDateEditor())&&key.getKeyCode()==10)
                {
                    jComboBox2.requestFocusInWindow();
                }
                if(event.getSource().equals(jDateChooser3.getDateEditor())&&key.getKeyCode()==10)
                {
                    jButton1.requestFocusInWindow();
                }
            }
        }
    }
    
    private void populateCombo1() // Company
    {
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in CompanyMaster: 4
        /* compid, compnm, compabbr, isactive */
        String query="select compid, compabbr from CompanyMaster where isactive=1 order by compabbr asc";
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
                compidArray=new String[total];
                jComboBox1.addItem("-- Select --");
                int i=0;
                while(rs.next())
                {
                    compidArray[i++]=rs.getString("compid");
                    jComboBox1.addItem(rs.getString("compabbr"));
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Sale03 ex: "+ex.getMessage(),
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
    
    private void populateCombo2() // Retailer
    {
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
            if(total != 0)
            {
                retidArray=new String[total];
                beatabbrArray = new String[total];
                jComboBox2.addItem("-- Select --");
                int i=0;
                while(rs.next())
                {
                    retidArray[i]=rs.getString("retid");
                    String beatabbr = rs.getString("beatabbr");
                    beatabbrArray[i] = beatabbr;
                    jComboBox2.addItem(rs.getString("retnm"));
                    i++;
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Sale03 ex: "+ex.getMessage(),
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
    
    private void addAlterRetailer() {
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    setVisible(false);
                    final RetailerMaster ref=new RetailerMaster(jDesktopPane1, true, up);
                    ref.addInternalFrameListener(new InternalFrameAdapter()
                    {
                        @Override
                        public void internalFrameDeactivated(InternalFrameEvent e)
                        {
                            newRetnm=ref.getNewRetnm();
                        }
                        @Override
                        public void internalFrameClosed(InternalFrameEvent e)
                        {
                            Sale03.this.setVisible(true);
                            if(newRetnm!=null)
                            {
                                populateCombo2();
                                jComboBox2.setSelectedItem(newRetnm);
                                try
                                {
                                    if(((String)jComboBox2.getSelectedItem()).equals("-- Select --"))
                                    {
                                        jLabel6.setText("N/A");
                                    }
                                    else
                                    {
                                        jLabel6.setText(beatabbrArray[jComboBox2.getSelectedIndex()-1]);
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
    
    private void startBilling() {
        // Validity testing
        if(((String)jComboBox1.getSelectedItem()).equals("-- Select --")) 
        {
            JOptionPane.showMessageDialog(null,"Select The Company!","Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox1.requestFocusInWindow();
            return;
        }
        if(((String)jComboBox2.getSelectedItem()).equals("-- Select --")) 
        {
            JOptionPane.showMessageDialog(null,"Select The Retailer!","Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox2.requestFocusInWindow();
            return;
        }
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
        Date saleDt =jDateChooser1.getDate();
        String saledt=null;
        try
        {
            saledt=sdf.format(saleDt);
        }
        catch(NullPointerException ex)
        {
            JOptionPane.showMessageDialog(null,"Invalid Sale Date.","Invalid Date",JOptionPane.ERROR_MESSAGE);
            jDateChooser1.requestFocusInWindow();
            return;
        }
        String ordno = jTextField1.getText().trim().toUpperCase();
        Date ordDt =jDateChooser2.getDate();
        String orddt=null;
        try
        {
            orddt=sdf.format(ordDt);
        }
        catch(NullPointerException ex)
        {
            JOptionPane.showMessageDialog(null,"Invalid Order Date.","Invalid Date",JOptionPane.ERROR_MESSAGE);
            jDateChooser2.requestFocusInWindow();
            return;
        }
        Date supplyDt =jDateChooser3.getDate();
        String supplydt=null;
        try
        {
            supplydt=sdf.format(supplyDt);
        }
        catch(NullPointerException ex)
        {
            JOptionPane.showMessageDialog(null,"Invalid Supply Date.","Invalid Date",JOptionPane.ERROR_MESSAGE);
            jDateChooser3.requestFocusInWindow();
            return;
        }
        
        // Setting enability to false
        jComboBox1.setEnabled(false);
        jDateChooser1.setEnabled(false);
        jTextField1.setEnabled(false);
        jDateChooser2.setEnabled(false);
        jComboBox2.setEnabled(false);
        jTextField2.setEnabled(false);
        jTextField3.setEnabled(false);
        jTextField4.setEnabled(false);
        jTextField5.setEnabled(false);
        jDateChooser3.setEnabled(false);
        jButton1.setEnabled(false);
        jComboBox3.requestFocusInWindow();
    }
    
    private String getNextSaleInvoiceNo()
    {
        int total=0;
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        String financialcode="";
        String query="select financialcode from FinancialYear where isactive=1";
        System.out.println(query);
        try
        {
            Statement smt=conn.createStatement();
            ResultSet rs=smt.executeQuery(query);
            if(rs.next())
            {
                financialcode=rs.getString("financialcode");
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Sale01 ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        String compabbr = null;
        try {
            compabbr = jComboBox1.getSelectedItem().toString();
        } catch(java.lang.NullPointerException ex) {
            return null;
        }
        query="select IFNULL(max(salemid),'') as x from SaleMaster where salemid like '"
                    + e.getEabbr()+compabbr+"/______/"+financialcode+"'";
        System.out.println(query);
        try
        {
            Statement smt=conn.createStatement();
            ResultSet rs=smt.executeQuery(query);
            if(rs.next())
            {
                String lastTotalID=rs.getString("x");
                if(lastTotalID.length()!=0)
                {
                    String lastID=lastTotalID.substring(lastTotalID.indexOf("/")+1,lastTotalID.lastIndexOf("/"));
                    total=Integer.parseInt(lastID);
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Sale03 ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException ex){}
        }
        total++;
        return e.getEabbr()+compabbr+"/"+Add0Padding6.add0Padding(total)+"/"+financialcode;
    }
    
    private void populateCombo3() // Item HSN
    {
        if (jComboBox1.getSelectedIndex()==0) {
            return;
        }
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in ItemMaster: 5
        /* itemmid, compid, itemnm, hsn, isactive */
        // Number of columns in ItemDetails: 10
        /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
        // Number of columns in PurchaseSub: 14
        /* psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, taxableamt, gstamt, qtysold, retqty */
        String query="select y.itemmid, hsn, itemnm from (select itemmid, itemnm, hsn from ItemMaster where isactive=1 "
                + "and compid="+compidArray[jComboBox1.getSelectedIndex()-1]+") x, (select distinct itemmid from "
                + "ItemDetails where isactive=1 and itemdid in (select distinct itemdid from PurchaseSub where "
                + "qty-(qtysold+retqty)>0)) y where x.itemmid=y.itemmid order by itemnm";
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
            jComboBox3.removeAllItems();
            if(total != 0)
            {
                itemmidArray=new String[total];
                hsnArray=new String[total];
                jComboBox3.addItem("-- Select --");
                int i=0;
                while(rs.next())
                {
                    itemmidArray[i]=rs.getString("itemmid");
                    jComboBox3.addItem(rs.getString("itemnm"));
                    hsnArray[i]=rs.getString("hsn");
                    i++;
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Sale03 ex: "+ex.getMessage(),
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
    
    private void getItemFromPurchase() {
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    setVisible(false);
                    final ItemDetails4Sale ref=new ItemDetails4Sale(currentItemmid);
                    ref.addInternalFrameListener(new InternalFrameAdapter()
                    {
                        @Override
                        public void internalFrameDeactivated(InternalFrameEvent e)
                        {
                            currentPsidWithItemDetails=ref.getSelectedPsidWithItemDetails();
                        }
                        @Override
                        public void internalFrameClosed(InternalFrameEvent e)
                        {
                            Sale03.this.setVisible(true);
                            if(currentPsidWithItemDetails!=null)
                            {
                                //     0             1                2              3             4             5                6                7                8
                                // psid+"~"+itemdid+"~"+avlqty+"~"+mrp+"~"+gst+"~"+pexgst+"~"+pingst+"~"+sexgst+"~"+singst
                                String s[] = currentPsidWithItemDetails.split("~");
                                currentPsid = s[0];
                                currentItemdid = s[1];
                                avlqty = Integer.parseInt(s[2]);
                                jLabel17.setText(s[3]);
                                jLabel21.setText(s[4]);
                                jTextField6.setText(s[2]);
                                computation01();
                                jTextField6.requestFocusInWindow();
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
    
    private void computation01() {
        try {
            double netRate = Double.parseDouble(jTextField7.getText());
            int qty = Integer.parseInt(jTextField6.getText());
            if ( qty > avlqty ) {
                JOptionPane.showMessageDialog(null,"Quantity for Sale can't be > available quantity in Purchase !!!",
                        "Error Found",JOptionPane.ERROR_MESSAGE);
                jTextField6.selectAll();
                jTextField6.requestFocusInWindow();
                return;
            }
            double gst = Double.parseDouble(jLabel21.getText().trim().replaceAll(",", ""));
            double rate = netRate / (1 + (gst/100));
            jLabel25.setText(MyNumberFormat.rupeeFormat(rate));
            double gross = rate * qty;
            jLabel27.setText(MyNumberFormat.rupeeFormat(gross));
            double individualGst = ((netRate - rate) * qty) / 2.0;
            jLabel29.setText(MyNumberFormat.rupeeFormat(individualGst));
            jLabel31.setText(MyNumberFormat.rupeeFormat(individualGst));
            double total = netRate * qty;
            jLabel33.setText(MyNumberFormat.rupeeFormat(total));
        } catch (Exception ex) {}
    }
    
    private void addToList() {
        // Number of columns in SaleSub: 19
        /* salesid, salemid, psid, itemid, qty, mrp, ingst, amt, discper, discamt, taxableamt, 
        cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
        if (currentPsid == null || currentItemdid == null) {
            JOptionPane.showMessageDialog(null,"Item should be selected from Purchase using F5 !!!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox3.requestFocusInWindow();
            return;
        }
        // Checking duplicate for same psid
        if ( ssAl.size() != 0 ) {
            for ( SaleSub ss : ssAl ) {
                if ( ss.getPsid().equals(currentPsid)) {
                    JOptionPane.showMessageDialog(null,"Duplicate Purchase-Item Selection !!!",
                            "Error Found",JOptionPane.ERROR_MESSAGE);
                    jComboBox3.requestFocusInWindow();
                    return;
                }
            }
        }
        if ( jComboBox3.getSelectedIndex() == 0 )
        {
            JOptionPane.showMessageDialog(null,"Select proper item !!!","Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox3.requestFocusInWindow();
            return;
        }
        // Already have "psid" as currentPsid & "itemid" as currentItemid
        // Number of columns in SaleSub: 21
        /* salesid, salemid, psid, itemdid, qty, mrp, gst, exgst, ingst, amt, discper, discamt, taxableamt, 
        cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
        String qty = jTextField6.getText();
        if(Integer.parseInt(qty) == 0)
        {
            JOptionPane.showMessageDialog(null,"Quantity is mandatory !!!","Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField6.requestFocusInWindow();
            return;
        }
        if (Integer.parseInt(qty) > avlqty) {
            JOptionPane.showMessageDialog(null,qty+"Pcs. item not available in selected purchase !!!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField6.requestFocusInWindow();
            return;
        }
        String mrp = jLabel17.getText().trim().replaceAll(",", "");
        String gst = jLabel21.getText().trim().replaceAll(",", "");
        String exgst = jLabel25.getText().trim().replaceAll(",", "");
        String ingst = jTextField7.getText();
        String amt = jLabel27.getText().trim().replaceAll(",", "");
        String itemdiscamt = "0";
        String taxableamt = jLabel27.getText().trim().replaceAll(",", "");
        String csgstper = format.format(Double.parseDouble(gst) / 2.0);
        String cgstper = csgstper;
        String cgstamt = jLabel29.getText().trim().replaceAll(",", "");
        String sgstper = csgstper;
        String sgstamt = jLabel31.getText().trim().replaceAll(",", "");
        String igstper = "0";
        String igstamt = "0";
        String total = jLabel33.getText().trim().replaceAll(",", "");
        String retqty = "0";
        
        SaleSub ss = new SaleSub();
        // Number of columns in SaleSub: 20
        /* salesid, salemid, psid, itemdid, qty, mrp, gst, exgst, ingst, amt, itemdiscamt, 
        taxableamt, cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
        ss.setSalesid(""); // At Insert
        ss.setSalemid(""); // At Insert
        ss.setPsid(currentPsid);
        ss.setItemdid(currentItemdid);
        ss.setQty(qty);
        ss.setMrp(mrp);
        ss.setGst(gst);
        ss.setExgst(exgst);
        ss.setIngst(ingst);
        ss.setAmt(amt);
        ss.setItemdiscamt(itemdiscamt);
        ss.setTaxableamt(taxableamt);
        ss.setCgstper(cgstper);
        ss.setCgstamt(cgstamt);
        ss.setSgstper(sgstper);
        ss.setSgstamt(sgstamt);
        ss.setIgstper(igstper);
        ss.setIgstamt(igstamt);
        ss.setTotal(total);
        ss.setRetqty(retqty);
        ssAl.add(ss);
        
        Fetch();
        
        currentPsid = null;
        currentItemmid = null;
        currentItemdid = null;
        currentPsidWithItemDetails = null;
        
        jComboBox3.setSelectedIndex(0);
        innerFornFlush();
        
        jComboBox3.requestFocusInWindow();
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
        double netcgst = 0.0;
        double netsgst = 0.0;
        double nettotal = 0.0;
        
        int slno=0;
        clearTable(jTable1);
        
        // Number of columns in SaleSub: 21
        /* salesid, salemid, psid, itemdid, qty, mrp, gst, exgst, ingst, amt, discper, discamt, taxableamt, 
        cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        for (SaleSub ss :  ssAl)
        {
            Vector<String> row = new Vector<String>();
            row.addElement(++slno+"");
            //NO. OF TABLE HEADER: 16
            /* SLN., PUR. NO., ITEM, QTY., MRP, RATE, AMOUNT, ITEM DISC. AMT., TAXABLE, 
            CGST%, CGST AMT., SGST%, SGST AMT., IGST%, IGST AMT., TOTAL */
            // Number of columns in PurchaseMaster: 20
            /* pmid, ssid, compid, invno, invdt, netqty, netgross, netitemdisc, nettaxable, tradediscper, 
            tradediscamt, replacementdiscper, replacementdiscamt, netgstamt, netpayableamt, roundoff, totamt, 
            isopening, amtpaid, isactive */
            // Number of columns in PurchaseSub: 14
            /* psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, taxableamt, gstamt, qtysold, retqty */
            String query="select invno from PurchaseMaster, PurchaseSub where PurchaseMaster.pmid="
                    + "PurchaseSub.pmid and psid="+ss.getPsid();
            System.out.println(query);
            try
            {
                Statement smt=conn.createStatement();
                ResultSet rs=smt.executeQuery(query);
                if (rs.next())
                {
                    row.addElement(rs.getString("invno"));
                }
            }
            catch(SQLException ex)
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"Sale01 ex: "+ex.getMessage(),
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
            try
            {
                Statement smt=conn.createStatement();
                ResultSet rs=smt.executeQuery(query);
                if (rs.next())
                {
                    row.addElement(rs.getString("itemnm")+"["+rs.getString("hsn")+"]");
                }
            }
            catch(SQLException ex)
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"Sale01 ex: "+ex.getMessage(),
                        "Error Found",JOptionPane.ERROR_MESSAGE);
                return;
            }
            netqty += Integer.parseInt(ss.getQty());
            row.addElement(ss.getQty());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getMrp())));
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getExgst())));
            //NO. OF TABLE HEADER: 16
            /* SLN., PUR. NO., ITEM, QTY., MRP, RATE, AMOUNT, ITEM DISC. AMT., TAXABLE, 
            CGST%, CGST AMT., SGST%, SGST AMT., IGST%, IGST AMT., TOTAL */
            // Number of columns in SaleSub: 20
            /* salesid, salemid, psid, itemdid, qty, mrp, gst, exgst, ingst, amt, itemdiscamt, 
            taxableamt, cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
            /*
                int netqty = 0;
                double netgross = 0.0;
                double netcgst = 0.0;
                double netsgst = 0.0;
                double nettotal = 0.0;
            */
            netgross += Double.parseDouble(ss.getAmt());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getAmt())));
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getItemdiscamt())));
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getTaxableamt())));
            row.addElement(format.format(Double.parseDouble(ss.getCgstper())));
            netcgst += Double.parseDouble(ss.getCgstamt());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getCgstamt())));
            row.addElement(format.format(Double.parseDouble(ss.getSgstper())));
            netsgst += Double.parseDouble(ss.getSgstamt());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getSgstamt())));
            row.addElement(format.format(Double.parseDouble(ss.getIgstper())));
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getIgstamt())));
            nettotal += Double.parseDouble(ss.getTotal());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getTotal())));
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
       //NO. OF TABLE HEADER: 16
        /* SLN., PUR. NO., ITEM, QTY., MRP, RATE, AMOUNT, ITEM DISC. AMT., TAXABLE, 
        CGST%, CGST AMT., SGST%, SGST AMT., IGST%, IGST AMT., TOTAL */
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN.
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(40);
        jTable1.getColumnModel().getColumn(1).setMinWidth(0);// PUR. NO.
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);// ITEM
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(250);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// QTY.
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);// MRP
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);// RATE
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(6).setMinWidth(0);// AMOUNT
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(7).setMinWidth(0);// ITEM DISC. AMT.
        jTable1.getColumnModel().getColumn(7).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(8).setMinWidth(0);// TAXABLE
        jTable1.getColumnModel().getColumn(8).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(9).setMinWidth(0);// CGST%
        jTable1.getColumnModel().getColumn(9).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(10).setMinWidth(0);// CGST AMT.
        jTable1.getColumnModel().getColumn(10).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(11).setMinWidth(0);// SGST%
        jTable1.getColumnModel().getColumn(11).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(12).setMinWidth(0);// SGST AMT.
        jTable1.getColumnModel().getColumn(12).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(13).setMinWidth(0);// IGST%
        jTable1.getColumnModel().getColumn(13).setPreferredWidth(20);
        jTable1.getColumnModel().getColumn(14).setMinWidth(0);// IGST AMT.
        jTable1.getColumnModel().getColumn(14).setPreferredWidth(20);
        jTable1.getColumnModel().getColumn(15).setMinWidth(0);// TOTAL
        jTable1.getColumnModel().getColumn(15).setPreferredWidth(150);
        
        //NO. OF TABLE HEADER: 16
        /* SLN., PUR. NO., ITEM, QTY., MRP, RATE, AMOUNT, ITEM DISC. AMT., TAXABLE, 
        CGST%, CGST AMT., SGST%, SGST AMT., IGST%, IGST AMT., TOTAL */
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable1.getColumn("SLN.").setCellRenderer( centerRenderer );
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        jTable1.getColumn("QTY.").setCellRenderer( rightRenderer );
        jTable1.getColumn("MRP").setCellRenderer( rightRenderer );
        jTable1.getColumn("RATE").setCellRenderer( rightRenderer );
        jTable1.getColumn("AMOUNT").setCellRenderer( rightRenderer );
        jTable1.getColumn("ITEM DISC. AMT.").setCellRenderer( rightRenderer );
        jTable1.getColumn("TAXABLE").setCellRenderer( rightRenderer );
        jTable1.getColumn("CGST%").setCellRenderer( rightRenderer );
        jTable1.getColumn("CGST AMT.").setCellRenderer( rightRenderer );
        jTable1.getColumn("SGST%").setCellRenderer( rightRenderer );
        jTable1.getColumn("SGST AMT.").setCellRenderer( rightRenderer );
        jTable1.getColumn("IGST%").setCellRenderer( rightRenderer );
        jTable1.getColumn("IGST AMT.").setCellRenderer( rightRenderer );
        jTable1.getColumn("TOTAL").setCellRenderer( rightRenderer );
        
        /*
            int netqty = 0;
            double netgross = 0.0;
            double netdiscamt = 0.0;
            double nettaxableamt = 0.0;
            double netcgst = 0.0;
            double netsgst = 0.0;
            double nettotal = 0.0;
        */
        jLabel36.setText(netqty+"");
        jLabel38.setText(MyNumberFormat.rupeeFormat(netgross));
        jLabel40.setText(MyNumberFormat.rupeeFormat(netcgst));
        jLabel42.setText(MyNumberFormat.rupeeFormat(netsgst));
        jLabel44.setText(MyNumberFormat.rupeeFormat(nettotal));
        computation02();
    }
    
    private void computation02()
    {
        try {
            double nettotal = Double.parseDouble(jLabel44.getText().replaceAll(",", "").trim());
            double cashdiscper = Double.parseDouble(jTextField9.getText());
            double cashdiscamt = nettotal * (cashdiscper / 100.0);
            jLabel47.setText(MyNumberFormat.rupeeFormat(cashdiscamt));
            double netamt01 = nettotal - cashdiscamt;
            jLabel49.setText(MyNumberFormat.rupeeFormat(netamt01));
            double roundoff = Double.parseDouble(jTextField10.getText());
            double netamt02 = netamt01 + roundoff;
            jLabel52.setText(MyNumberFormat.rupeeFormat(netamt02));
        } catch(java.lang.NumberFormatException ex) {}
    }
    
    private void innerFornFlush() {
        jLabel15.setText("N/A");
        jLabel17.setText("0");
        jLabel21.setText("0");
        jTextField6.setText("0");
        jTextField7.setText("0");
        jLabel25.setText("0");
        jLabel27.setText("0");
        jLabel29.setText("0");
        jLabel31.setText("0");
        jLabel33.setText("0");
    }
    
    private String insertToDatabase()
    {
        if (ssAl.size() == 0)
        {
            JOptionPane.showMessageDialog(null,"Incomplete Data !!!","Incomplete Data",JOptionPane.ERROR_MESSAGE);
            jComboBox3.requestFocusInWindow();
            return null;
        }
        if(((String)jComboBox1.getSelectedItem()).equals("-- Select --")) 
        {
            JOptionPane.showMessageDialog(null,"Select The Company!","Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox1.requestFocusInWindow();
            return null;
        }
        String compid="";
        try
        {
            compid=compidArray[jComboBox1.getSelectedIndex()-1];
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        String salemid=jLabel7.getText().trim();
        if(((String)jComboBox2.getSelectedItem()).equals("-- Select --")) 
        {
            JOptionPane.showMessageDialog(null,"Select The Retailer!","Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox2.requestFocusInWindow();
            return null;
        }
        String retid="";
        try
        {
            retid=retidArray[jComboBox2.getSelectedIndex()-1];
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
        Date saleDt =jDateChooser1.getDate();
        String saledt=null;
        try
        {
            saledt=sdf.format(saleDt);
        }
        catch(NullPointerException ex)
        {
            JOptionPane.showMessageDialog(null,"Invalid Sale Date.","Invalid Date",JOptionPane.ERROR_MESSAGE);
            jDateChooser1.requestFocusInWindow();
            return null;
        }
        String ordno = jTextField1.getText().trim().toUpperCase();
        Date ordDt =jDateChooser2.getDate();
        String orddt=null;
        try
        {
            orddt=sdf.format(ordDt);
        }
        catch(NullPointerException ex)
        {
            JOptionPane.showMessageDialog(null,"Invalid Order Date.","Invalid Date",JOptionPane.ERROR_MESSAGE);
            jDateChooser2.requestFocusInWindow();
            return null;
        }
        String deliverynote = jTextField2.getText().trim().toUpperCase();
        String paymentterm = jTextField3.getText().trim().toUpperCase();
        String transporter = jTextField4.getText().trim().toUpperCase();
        String vehicleno = jTextField5.getText().trim().toUpperCase();
        Date supplyDt =jDateChooser3.getDate();
        String supplydt=null;
        try
        {
            supplydt=sdf.format(supplyDt);
        }
        catch(NullPointerException ex)
        {
            JOptionPane.showMessageDialog(null,"Invalid Supply Date.","Invalid Date",JOptionPane.ERROR_MESSAGE);
            jDateChooser3.requestFocusInWindow();
            return null;
        }
        // Number of columns in SaleMaster: 27
        /* salemid, compid, retid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
        supplydt, netqty, netgross, netitemdisc, nettaxable, netcgst, netsgst, netigst, nettotal, 
        cashdiscper, cashdiscamt, netamt01, roundoff, netamt02, amtpaid, isactive, remarks */
        String netqty = jLabel36.getText().trim().replaceAll(",", "");
        String netgross = jLabel38.getText().trim().replaceAll(",", "");
        String netitemdisc = "0";
        String nettaxable = netgross;
        String netcgst = jLabel40.getText().trim().replaceAll(",", "");
        String netsgst = jLabel42.getText().trim().replaceAll(",", "");
        String netigst = "0";
        String nettotal = jLabel44.getText().trim().replaceAll(",", "");
        String cashdiscper = jTextField9.getText().trim();
        String cashdiscamt = jLabel47.getText().trim().replaceAll(",", "");
        String netamt01 = jLabel49.getText().trim().replaceAll(",", "");
        String roundoff = jTextField10.getText().trim();
        String netamt02 = jLabel52.getText().trim().replaceAll(",", "");
        String amtpaid = "0";
        String isactive = "1";
        String remarks = jTextField11.getText().trim().toUpperCase().replace("'", "\\'");
        
        // Number of columns in SaleMaster: 27
        /* salemid, compid, retid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
        supplydt, netqty, netgross, netitemdisc, nettaxable, netcgst, netsgst, netigst, nettotal, 
        cashdiscper, cashdiscamt, netamt01, roundoff, netamt02, amtpaid, isactive, remarks */
        sm = new SaleMaster();
        sm.setSalemid(salemid);
        sm.setCompid(compid);
        sm.setRetid(retid);
        sm.setSaledt(saledt);
        sm.setOrdno(ordno);
        sm.setOrddt(orddt);
        sm.setDeliverynote(deliverynote);
        sm.setPaymentterm(paymentterm);
        sm.setTransporter(transporter);
        sm.setVehicleno(vehicleno);
        sm.setSupplydt(supplydt);
        sm.setNetqty(netqty);
        sm.setNetgross(netgross);
        sm.setNetitemdisc(netitemdisc);
        sm.setNettaxable(nettaxable);
        sm.setNetcgst(netcgst);
        sm.setNetsgst(netsgst);
        sm.setNetigst(netigst);
        sm.setNettotal(nettotal);
        sm.setCashdiscper(cashdiscper);
        sm.setCashdiscamt(cashdiscamt);
        sm.setNetamt01(netamt01);
        sm.setRoundoff(roundoff);
        sm.setNetamt02(netamt02);
        sm.setAmtpaid(amtpaid);
        sm.setIsactive(isactive);
        sm.setRemarks(remarks);
        sm.setSsAl(ssAl);
        
        boolean success = q.insertToSaleMaster(sm);
        if ( success )
        {
            sm = q.getSaleMaster(salemid);
            
            dBConnection db=new dBConnection();
            Connection conn=db.setConnection();
            
            PreparedStatement psmt1 = null;
            PreparedStatement psmt2 = null;
            PreparedStatement psmt3 = null;
            PreparedStatement psmt4 = null;
            PreparedStatement psmt5 = null;
            
            // Number of columns in ItemLedger: 9
            /* ilid, itemdid, tablenm, pknm, pkval, actiondt, type, prevqty, qty */
            int ilid=q.getMaxId("ItemLedger", "ilid");
            // Number of columns in SalePaymentRegister: 8
            /* sprid, pmid, pknm, pkval, actiondt, refno, type, amount */
            int sprid=q.getMaxId("SalePaymentRegister", "sprid");
            try
            {
                conn.setAutoCommit(false);
                
                String pervqty = null;
                // Number of columns in ItemDetails: 10
                /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
                String sql1 = "select onhand from ItemDetails where itemdid=?";
                psmt1 = conn.prepareStatement(sql1);
                // Number of columns in SaleSub: 20
                /* salesid, salemid, psid, itemdid, qty, mrp, gst, exgst, ingst, amt, itemdiscamt, 
                taxableamt, cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
                // Number of columns in ItemDetails: 10
                /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
                String sql2 = "update ItemDetails set onhand=onhand-? where itemdid=?";
                psmt2 = conn.prepareStatement(sql2);
                // Number of columns in ItemLedger: 9
                /* ilid, itemdid, tablenm, pknm, pkval, actiondt, type, prevqty, qty */
                String sql3 = "insert into ItemLedger (ilid, itemdid, tablenm, pknm, pkval, actiondt, "
                        + "type, prevqty, qty) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                psmt3 = conn.prepareStatement(sql3);
                // Number of columns in PurchaseSub: 15
                /* psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, taxableamt, gstamt, qtysold, retqty */
                String sql4 = "update PurchaseSub set qtysold=qtysold+? where psid=?";
                psmt4 = conn.prepareStatement(sql4);
                for( SaleSub ref : sm.getSsAl() )
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
                    psmt3.setString(3, "SaleSub");
                    psmt3.setString(4, "salesid");
                    psmt3.setString(5, ref.getSalesid());
                    psmt3.setDate(6, java.sql.Date.valueOf(DateConverter.dateConverter1(saledt)));
                    psmt3.setString(7, "LESS");
                    psmt3.setInt(8, Integer.parseInt(pervqty));
                    psmt3.setInt(9, Integer.parseInt(ref.getQty()));
                    psmt3.addBatch();
                    
                    psmt4.setInt(1, Integer.parseInt(ref.getQty()));
                    psmt4.setInt(2, Integer.parseInt(ref.getPsid()));
                    psmt4.addBatch();
                }
                psmt2.executeBatch();
                psmt3.executeBatch();
                psmt4.executeBatch();
                
                // Number of columns in SalePaymentRegister: 7
                /* sprid, pknm, pkval, actiondt, refno, type, amount */
                String sql5 = "insert into SalePaymentRegister (sprid, pknm, pkval, actiondt,"
                        + " refno, type, amount) values (?, ?, ?, ?, ?, ?, ?)";
                psmt5 = conn.prepareStatement(sql5);
                psmt5.setInt(1, ++sprid);
                psmt5.setString(2, "salemid");
                psmt5.setString(3, salemid);
                psmt5.setDate(4, java.sql.Date.valueOf(DateConverter.dateConverter1(saledt)));
                psmt5.setString(5, salemid);
                psmt5.setInt(6, 0);
                psmt5.setDouble(7, Double.parseDouble(sm.getNetamt02()));
                psmt5.executeUpdate();
                
                conn.commit();
            }
            catch(SQLException ex)
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"Sale01 ex: "+ex.getMessage(),
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
                if (psmt5 != null) 
                {
                    try {
                        psmt5.close();
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
        return success?salemid:null;
    }
    
    private void formFlush() {
        newRetnm = null;
        currentItemmid = null;
        currentItemdid = null;
        currentPsidWithItemDetails = null;
        currentPsid = null;
        ssAl = new ArrayList<SaleSub>();
        avlqty = 0;
        sm = null;
        
        jComboBox1.setEnabled(true);
        jComboBox1.setSelectedIndex(0);
        jLabel7.setText("N/A");
        jDateChooser1.setEnabled(true);
        jDateChooser1.setDate(new Date());
        jTextField1.setEnabled(true);
        jTextField1.setText("N/A");
        jDateChooser2.setEnabled(true);
        String sDate1="01/01/2000";  
        Date date1=null;
        try {
            date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
        }
        catch (ParseException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Sale ex?: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        jDateChooser2.setDate(date1);
        jComboBox2.setEnabled(true);
        jComboBox2.setSelectedIndex(0);
        jLabel6.setText("N/A");
        jTextField2.setEnabled(true);
        jTextField2.setText("N/A");
        jTextField3.setEnabled(true);
        jTextField3.setText("N/A");
        jTextField4.setEnabled(true);
        jTextField4.setText("N/A");
        jTextField5.setEnabled(true);
        jTextField5.setText("N/A");
        jDateChooser3.setEnabled(true);
        jDateChooser3.setDate(date1);
        jButton1.setEnabled(true);
        
        clearTable(jTable1);
        jComboBox3.removeAllItems();
        innerFornFlush();
        jTextField8.setText("");
        jLabel36.setText("0");
        jLabel38.setText("0");
        jLabel40.setText("0");
        jLabel42.setText("0");
        jLabel44.setText("0");
        
        jTextField9.setText("0");
        jLabel47.setText("0");
        jLabel49.setText("0");
        jTextField10.setText("0");
        jLabel52.setText("0");
        jTextField11.setText("N/A");
        
        jComboBox1.requestFocusInWindow();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jDateChooser3 = new com.toedter.calendar.JDateChooser();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
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
        jButton3 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel34 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
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
        jTextField9 = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();

        jMenuItem1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jMenuItem1.setText("## DELETE SELECTED ITEM ##");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem1);

        jMenuItem2.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jMenuItem2.setText("## EDIT SELECTED ITEM ##");
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

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("COMPANY");

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

        jDateChooser1.setDateFormatString("dd/MM/yyyy");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("INV DT.");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("ORDER NO.");

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

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("ORDER DT.");

        jDateChooser2.setDateFormatString("dd/MM/yyyy");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("RETAILER");

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

        jLabel6.setBackground(new java.awt.Color(255, 255, 0));
        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("N/A");
        jLabel6.setOpaque(true);

        jLabel7.setBackground(new java.awt.Color(0, 255, 0));
        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("N/A");
        jLabel7.setOpaque(true);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("DELV. NOTE");

        jTextField2.setText("N/A");
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

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("PAY. NOTE");

        jTextField3.setText("N/A");
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

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("TRANSPORTER");

        jTextField4.setText("N/A");
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
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("VEHICLE NO.");

        jTextField5.setText("N/A");
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
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("SLP. DT.");

        jDateChooser3.setDateFormatString("dd/MM/yyyy");

        jButton1.setText("GO");
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

        jPanel1.setBackground(new java.awt.Color(204, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 0, 0), 2, true), "ITEM DETAILS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(0, 0, 204))); // NOI18N

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("ITEM + <F5>");

        jComboBox3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox3ItemStateChanged(evt);
            }
        });
        jComboBox3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox3KeyPressed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("HSN");

        jLabel15.setBackground(new java.awt.Color(255, 255, 0));
        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("N/A");
        jLabel15.setOpaque(true);

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel16.setText("MRP");

        jLabel17.setBackground(new java.awt.Color(255, 255, 0));
        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("0");
        jLabel17.setOpaque(true);

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel20.setText("GST %");

        jLabel21.setBackground(new java.awt.Color(255, 255, 0));
        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("0");
        jLabel21.setOpaque(true);

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel22.setText("QTY");

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

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel23.setText("NET RATE");

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

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel24.setText("RATE");

        jLabel25.setBackground(new java.awt.Color(255, 255, 0));
        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("0");
        jLabel25.setOpaque(true);

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel26.setText("GROSS / TAXABLE");

        jLabel27.setBackground(new java.awt.Color(255, 255, 0));
        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel27.setText("0");
        jLabel27.setOpaque(true);

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel28.setText("CGST");

        jLabel29.setBackground(new java.awt.Color(255, 255, 0));
        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel29.setText("0");
        jLabel29.setOpaque(true);

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel30.setText("SGST");

        jLabel31.setBackground(new java.awt.Color(255, 255, 0));
        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel31.setText("0");
        jLabel31.setOpaque(true);

        jLabel32.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel32.setText("TOTAL");

        jLabel33.setBackground(new java.awt.Color(0, 255, 0));
        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText("0");
        jLabel33.setOpaque(true);

        jButton3.setText("ADD TO LIST");
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

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "ADDED ITEMS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SLN.", "PUR. NO.", "ITEM", "QTY.", "MRP", "RATE", "AMOUNT", "ITEM DISC. AMT.", "TAXABLE", "CGST%", "CGST AMT.", "SGST%", "SGST AMT.", "IGST%", "IGST AMT.", "TOTAL"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel34.setText("SEARCH");

        jTextField8.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextField8.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField8KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField8KeyReleased(evt);
            }
        });

        jLabel35.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel35.setText("NET QTY.");

        jLabel36.setBackground(new java.awt.Color(255, 255, 0));
        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel36.setText("0");
        jLabel36.setOpaque(true);

        jLabel37.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel37.setText("NET GROSS / TAXABLE");

        jLabel38.setBackground(new java.awt.Color(255, 255, 0));
        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel38.setText("0");
        jLabel38.setOpaque(true);

        jLabel39.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel39.setText("NET CGST");

        jLabel40.setBackground(new java.awt.Color(255, 255, 0));
        jLabel40.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel40.setText("0");
        jLabel40.setOpaque(true);

        jLabel41.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel41.setText("NET SGST");

        jLabel42.setBackground(new java.awt.Color(255, 255, 0));
        jLabel42.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel42.setText("0");
        jLabel42.setOpaque(true);

        jLabel43.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel43.setText("NET TOTAL");

        jLabel44.setBackground(new java.awt.Color(0, 255, 0));
        jLabel44.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setText("0");
        jLabel44.setOpaque(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                        .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel43)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel23)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel24)
                        .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel26)
                        .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel28)
                        .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel30)
                        .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel32)
                        .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel34)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel35)
                        .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel37)
                        .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel39)
                        .addComponent(jLabel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel41)
                        .addComponent(jLabel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel43)
                        .addComponent(jLabel44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jLabel45.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel45.setText("CASH DISCOUNT %");

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

        jLabel46.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel46.setText("CASH DISCOUNT AMT.");

        jLabel47.setBackground(new java.awt.Color(255, 255, 0));
        jLabel47.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel47.setText("0");
        jLabel47.setOpaque(true);

        jLabel48.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel48.setText("NET AMT.");

        jLabel49.setBackground(new java.awt.Color(255, 255, 0));
        jLabel49.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel49.setText("0");
        jLabel49.setOpaque(true);

        jTextField10.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField10.setText("0");
        jTextField10.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField10FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField10FocusLost(evt);
            }
        });
        jTextField10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField10KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField10KeyReleased(evt);
            }
        });

        jLabel50.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel50.setText("ROUND OFF");

        jLabel51.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel51.setText("NET PAYABLE");

        jLabel52.setBackground(new java.awt.Color(255, 255, 0));
        jLabel52.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel52.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel52.setText("0");
        jLabel52.setOpaque(true);

        jLabel53.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel53.setText("REMARKS");

        jTextField11.setText("N/A");
        jTextField11.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField11FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField11FocusLost(evt);
            }
        });
        jTextField11.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField11KeyPressed(evt);
            }
        });

        jButton4.setText("SAVE & PRINT");
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

        jButton5.setText("ONLY SAVE");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jButton5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton5KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(6, 6, 6)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)
                                .addComponent(jLabel2)
                                .addGap(6, 6, 6)
                                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)
                                .addComponent(jLabel3)
                                .addGap(6, 6, 6)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)
                                .addComponent(jLabel4)
                                .addGap(6, 6, 6)
                                .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)
                                .addComponent(jLabel5)
                                .addGap(6, 6, 6)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)
                                .addComponent(jLabel8)
                                .addGap(6, 6, 6)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)
                                .addComponent(jLabel9)
                                .addGap(6, 6, 6)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)
                                .addComponent(jLabel10)
                                .addGap(6, 6, 6)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)
                                .addComponent(jLabel11)
                                .addGap(6, 6, 6)
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)
                                .addComponent(jLabel12)
                                .addGap(6, 6, 6)
                                .addComponent(jDateChooser3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)
                                .addComponent(jButton1)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel46)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel48)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel50)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel51)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 841, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))))
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jLabel8))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jLabel9))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jLabel10))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jLabel11))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jLabel12))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jDateChooser3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton1))
                .addGap(9, 9, 9)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel45)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel46)
                        .addComponent(jLabel47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel48)
                        .addComponent(jLabel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel50)
                        .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel51)
                        .addComponent(jLabel52, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel53)
                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox2ItemStateChanged
        try
        {
            if(((String)jComboBox2.getSelectedItem()).equals("-- Select --"))
            {
                jLabel6.setText("N/A");
            }
            else
            {
                jLabel6.setText(beatabbrArray[jComboBox2.getSelectedIndex()-1]);
            }
        }
        catch(NullPointerException ex){}
    }//GEN-LAST:event_jComboBox2ItemStateChanged

    private void jComboBox2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField2.requestFocusInWindow();
        }
        if(evt.getKeyCode() == KeyEvent.VK_F2)
        {
            addAlterRetailer();
        }
    }//GEN-LAST:event_jComboBox2KeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        startBilling();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            startBilling();
        }
    }//GEN-LAST:event_jButton1KeyPressed

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        if ( jComboBox1.getSelectedIndex() != 0 ) {
            jLabel7.setText(getNextSaleInvoiceNo());
            populateCombo3();
        } else {
            jLabel7.setText("N/A");
            jComboBox3.removeAllItems();
        }
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jComboBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jDateChooser1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jComboBox1KeyPressed

    private void jComboBox3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox3ItemStateChanged
        try {
            if(((String)jComboBox3.getSelectedItem()).equals("-- Select --"))
            {
                jLabel15.setText("N/A");
            }
            else
            {
                jLabel15.setText(hsnArray[jComboBox3.getSelectedIndex()-1]);
            }
        } catch (java.lang.NullPointerException ex) {}
    }//GEN-LAST:event_jComboBox3ItemStateChanged

    private void jTextField1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusGained
        String s=jTextField1.getText().trim();
        if(s.equals("N/A")) {
            jTextField1.setText("");
        }
    }//GEN-LAST:event_jTextField1FocusGained

    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusLost
        String s=jTextField1.getText().trim();
        if(s.length()==0) {
            jTextField1.setText("N/A");
        }
    }//GEN-LAST:event_jTextField1FocusLost

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jDateChooser2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jTextField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField2FocusGained
        String s=jTextField2.getText().trim();
        if(s.equals("N/A")) {
            jTextField2.setText("");
        }
    }//GEN-LAST:event_jTextField2FocusGained

    private void jTextField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField2FocusLost
        String s=jTextField2.getText().trim();
        if(s.length()==0) {
            jTextField2.setText("N/A");
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
        if(s.equals("N/A")) {
            jTextField3.setText("");
        }
    }//GEN-LAST:event_jTextField3FocusGained

    private void jTextField3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField3FocusLost
        String s=jTextField3.getText().trim();
        if(s.length()==0) {
            jTextField3.setText("N/A");
        }
    }//GEN-LAST:event_jTextField3FocusLost

    private void jTextField3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField4.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField3KeyPressed

    private void jTextField4FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField4FocusGained
        String s=jTextField4.getText().trim();
        if(s.equals("N/A")) {
            jTextField4.setText("");
        }
    }//GEN-LAST:event_jTextField4FocusGained

    private void jTextField4FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField4FocusLost
        String s=jTextField4.getText().trim();
        if(s.length()==0) {
            jTextField4.setText("N/A");
        }
    }//GEN-LAST:event_jTextField4FocusLost

    private void jTextField4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField5.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField4KeyPressed

    private void jTextField5FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField5FocusGained
        String s=jTextField5.getText().trim();
        if(s.equals("N/A")) {
            jTextField5.setText("");
        }
    }//GEN-LAST:event_jTextField5FocusGained

    private void jTextField5FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField5FocusLost
        String s=jTextField5.getText().trim();
        if(s.length()==0) {
            jTextField5.setText("N/A");
        }
    }//GEN-LAST:event_jTextField5FocusLost

    private void jTextField5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jDateChooser3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField5KeyPressed

    private void jComboBox3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField6.requestFocusInWindow();
        }
        if (evt.getKeyCode() == KeyEvent.VK_F3)
        {
            jTextField9.requestFocusInWindow();
        }
        if (evt.getKeyCode() == KeyEvent.VK_F5)
        {
            if ( jComboBox3.getSelectedIndex() == 0 ) {
                JOptionPane.showMessageDialog(null,"Select proper Item",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
                jComboBox3.requestFocusInWindow();
                return;
            }
            currentItemmid = itemmidArray[jComboBox3.getSelectedIndex()-1];
            getItemFromPurchase();
        }
    }//GEN-LAST:event_jComboBox3KeyPressed

    private void jTextField6FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField6FocusLost
        String s=jTextField6.getText().trim();
        if(s.length()==0) {
            jTextField6.setText("0");
        }
    }//GEN-LAST:event_jTextField6FocusLost

    private void jTextField6FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField6FocusGained
        String s=jTextField6.getText().trim();
        if(Integer.parseInt(s) == 0) {
            jTextField6.setText("");
        } else {
            jTextField6.selectAll();
        }
    }//GEN-LAST:event_jTextField6FocusGained

    private void jTextField6KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField7.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField6KeyPressed

    private void jTextField6KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyReleased
        computation01();
    }//GEN-LAST:event_jTextField6KeyReleased

    private void jTextField7FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField7FocusGained
        String s=jTextField7.getText().trim();
        if(Double.parseDouble(s) == 0.0) {
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
            jButton3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField7KeyPressed

    private void jTextField7KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyReleased
        computation01();
    }//GEN-LAST:event_jTextField7KeyReleased

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        addToList();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            addToList();
        }
    }//GEN-LAST:event_jButton3KeyPressed

    private void jTextField8KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField8KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField9.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField8KeyPressed

    private void jTextField8KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField8KeyReleased
        if (ssAl.size() != 0) {
            String searchString = jTextField8.getText().trim().toUpperCase();
            dBConnection db=new dBConnection();
            Connection conn=db.setConnection();
            ArrayList<String> rows = new ArrayList<String>();
            int rowNo = 0;
            for (SaleSub ss: ssAl) {
                String itemnm = null;
                // Number of columns in ItemMaster: 5
                /* itemmid, compid, itemnm, hsn, isactive */
                // Number of columns in ItemDetails: 10
                /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
                String query="select itemnm from ItemMaster, ItemDetails where ItemMaster.itemmid=ItemDetails.itemmid "
                        + "and ItemMaster.isactive=1 and ItemDetails.isactive=1 and itemdid="+ss.getItemdid();
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
                    JOptionPane.showMessageDialog(null,"Sale03 ex: "+ex.getMessage(),
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
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField10.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField9KeyPressed

    private void jTextField9KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField9KeyReleased
        computation02();
    }//GEN-LAST:event_jTextField9KeyReleased

    private void jTextField10FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField10FocusGained
        String s=jTextField10.getText().trim();
        if(Double.parseDouble(s) == 0) {
            jTextField10.setText("");
        } else {
            jTextField10.selectAll();
        }
    }//GEN-LAST:event_jTextField10FocusGained

    private void jTextField10FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField10FocusLost
        String s=jTextField10.getText().trim();
        if(s.length()==0) {
            jTextField10.setText("0");
        }
    }//GEN-LAST:event_jTextField10FocusLost

    private void jTextField10KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField10KeyPressed
       if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            computation02();
            jTextField11.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField10KeyPressed

    private void jTextField10KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField10KeyReleased
        computation02();
    }//GEN-LAST:event_jTextField10KeyReleased

    private void jTextField11FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField11FocusGained
        String s=jTextField11.getText().trim();
        if(s.equals("N/A")) {
            jTextField11.setText("");
        }
    }//GEN-LAST:event_jTextField11FocusGained

    private void jTextField11FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField11FocusLost
        String s=jTextField11.getText().trim();
        if(s.length()==0) {
            jTextField11.setText("N/A");
        }
    }//GEN-LAST:event_jTextField11FocusLost

    private void jTextField11KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField11KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jButton4.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField11KeyPressed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // SAVE & PRINT
        String salemid = insertToDatabase();
        if (salemid != null) {
            SaleMaster tempSm = q.getSaleMaster(salemid);
            new printSaleBill01(tempSm, e, q.getRetailer(tempSm.getRetid()));
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton4KeyPressed
        // SAVE & PRINT
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            String salemid = insertToDatabase();
            if (salemid != null) {
                SaleMaster tempSm = q.getSaleMaster(salemid);
                new printSaleBill01(tempSm, e, q.getRetailer(tempSm.getRetid()));
            }
        }
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT)
        {
            jButton5.requestFocusInWindow();
        }
    }//GEN-LAST:event_jButton4KeyPressed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        insertToDatabase();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton5KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            insertToDatabase();
        }
        if (evt.getKeyCode() == KeyEvent.VK_LEFT)
        {
            jButton4.requestFocusInWindow();
        }
    }//GEN-LAST:event_jButton5KeyPressed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // Delete Selected Item
        if(evt.getSource() == jMenuItem1)
        {
            if(jTable1.getSelectedRow()!=-1 && jTable1.getSelectedColumn()!=-1)
            {
                String ObjButtons[] = {"Yes","Cancel"};
                int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure to Delete this Item!",
                    "Delete Record",JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null,ObjButtons,ObjButtons[1]);
                if(PromptResult==0)
                {
                    int row = jTable1.getSelectedRow();
                    ssAl.remove(row);
                    Fetch();
                    jComboBox3.requestFocusInWindow();
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
        // Edit Selected Item
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
                    SaleSub ss = ssAl.get(row);
                    // Number of columns in SaleSub: 21
                    /* salesid, salemid, psid, itemdid, qty, mrp, gst, exgst, ingst, amt, itemdiscamt, 
                    taxableamt, cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
                    // Number of columns in ItemMaster: 5
                    /* itemmid, compid, itemnm, hsn, isactive */
                    // Number of columns in ItemDetails: 10
                    /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
                    String query="select itemnm, hsn from ItemMaster, ItemDetails"
                            + " where ItemMaster.itemmid=ItemDetails.itemmid and itemdid="+ss.getItemdid();
                    System.out.println(query);
                    dBConnection db=new dBConnection();
                    Connection conn=db.setConnection();
                    try
                    {
                        Statement stm=conn.createStatement();
                        ResultSet rs=stm.executeQuery(query);
                        if(rs.next())
                        {
                            jComboBox3.setSelectedItem(rs.getString("itemnm").replace("\\'", "'"));
                            jLabel15.setText(rs.getString("hsn"));
                        }
                    }
                    catch(SQLException ex)
                    {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null,"Sale03 ex: "+ex,
                                "Error Found",JOptionPane.ERROR_MESSAGE);
                    }
                    finally
                    {
                        try {
                            if (conn!=null) conn.close();
                        }
                        catch(SQLException ex){}
                    }
                    jLabel17.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getMrp())));
                    jLabel21.setText(format.format(Double.parseDouble(ss.getGst())));
                    jTextField6.setText(ss.getQty());
                    jTextField7.setText(format.format(Double.parseDouble(ss.getIngst())));
                    computation01();
                    ssAl.remove(row);
                    Fetch();
                    computation02();
                    jComboBox3.requestFocusInWindow();
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
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
