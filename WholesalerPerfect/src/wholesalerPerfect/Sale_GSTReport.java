package wholesalerPerfect;

import com.toedter.calendar.JTextFieldDateEditor;
import conn.dBConnection;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import utilities.DateConverter;
import utilities.MyNumberFormat;
import utilities.Settings;

public class Sale_GSTReport extends javax.swing.JInternalFrame implements AWTEventListener {
    
    private StringBuilder dtr;
    private DecimalFormat format = new DecimalFormat("0.#");
    private Settings settings=new Settings();
    
    private String compidArray[];
    private String retidArray[];
    
    public Sale_GSTReport() {
        super("Sale-GST Report",false,true,false,true);
        initComponents();
        Dimension d=getSize();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) dim.getWidth() - (int)d.getWidth())/2,((int) dim.getHeight() - (int)d.getHeight())/2-40);
	this.setResizable(false);
        this.setFrameIcon(new ImageIcon(getClass().getResource("/images/report01.png")));
        
        this.getActionMap().put("test", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        InputMap map = this.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        map.put(stroke,"test");
        
        Calendar date = Calendar.getInstance();
        jDateChooser2.setDate(date.getTime());
        date.set(Calendar.DAY_OF_MONTH, 1);
        jDateChooser1.setDate(date.getTime());
        
        jDateChooser1.getToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
        jDateChooser2.getToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
        
        jDateChooser1.getDateEditor().getUiComponent().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                ((JTextFieldDateEditor)evt.getSource()).selectAll();
            }
        });
        jDateChooser2.getDateEditor().getUiComponent().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                ((JTextFieldDateEditor)evt.getSource()).selectAll();
            }
        });
        
        ((DefaultTableCellRenderer)jTable1.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        ((JLabel)jComboBox1.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        ((JLabel)jComboBox2.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        settings.numvalidatorFloat(jTextField1);
        
        populateCombo1();
        populateCombo2();
        Fetch();
        
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
                    jDateChooser2.requestFocusInWindow();
                }
                if(event.getSource().equals(jDateChooser2.getDateEditor())&&key.getKeyCode()==10) {
                    jButton1.requestFocusInWindow();
                }
            }
        }
    }
    
    private void populateCombo1() {// Company
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in CompanyMaster: 6
        /* compid, compnm, compabbr, compcontact, compmail, isactive */
        String query="select compid, compnm from CompanyMaster where isactive=1 order by compnm asc";
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
                jComboBox1.addItem("-- All Companies --");
                int i=0;
                while(rs.next()) {
                    compidArray[i++]=rs.getString("compid");
                    jComboBox1.addItem(rs.getString("compnm"));
                }
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Sale_GSTReport ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        } finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException e) {}
        }
    }
    
    private void populateCombo2() {// Retailer
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in Retailer: 18
        /* retid, beatid, retnm, contactperson, rstreet, rcity, rdist, rstate, rstatecode, 
        rpin, rcountry, rcontact, rmail, rgstno, rgstregntype, rpanno, raadhaarno, isactive*/
        // Number of columns in BeatMaster: 4
        /* beatid, beatnm, beatabbr, isactive */
        String query="select retid, retnm, beatabbr from Retailer, BeatMaster "
                + "where Retailer.beatid=BeatMaster.beatid and Retailer.isactive=1 "
                + "and BeatMaster.isactive=1 order by retnm asc";
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
                jComboBox2.addItem("-- All Retailers --");
                int i=0;
                while(rs.next()) {
                    retidArray[i++]=rs.getString("retid");
                    jComboBox2.addItem(rs.getString("retnm")+"-"+rs.getString("beatabbr"));
                }
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Sale_GSTReport ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        } finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException e) {}
        }
    }
    
    private void clearTable(JTable table) {
        for(int i=table.getRowCount()-1; i>=0; i--) {
            ((DefaultTableModel)table.getModel()).removeRow(i);
        }
    }
    
    private void Fetch() {
        dtr=new StringBuilder("");
        double sumtaxableamt = 0.0;
        double sumcgstamt = 0.0;
        double sumsgstamt = 0.0;
        double sumtotal = 0.0;
        
        String fromdt="",todt="";
        String a="", b="", c="", d="", e="", f="";
        if ( jComboBox1.getSelectedIndex() > 0 ) {
            a = " and comPid="+compidArray[jComboBox1.getSelectedIndex() - 1];
        }
        if ( jComboBox2.getSelectedIndex() > 0 ) {
            b = " and retid="+retidArray[jComboBox2.getSelectedIndex() - 1];
        }
        double csgstper=0.0;
        try {
            csgstper = Double.parseDouble(jTextField1.getText().trim());
        } catch (NumberFormatException ex) {}
        if ( csgstper >= 0.0 && jTextField1.getText().trim().length() > 0 ) {
            c=" having gst="+format.format(csgstper);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if(jDateChooser1.getDate()!=null) {
            Date fromDt=jDateChooser1.getDate();
            try {
                fromdt=sdf.format(fromDt);
            } catch(NullPointerException ex) {            
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"Invalid From Date.",
                        "Invalid Date",JOptionPane.ERROR_MESSAGE);
                jDateChooser1.setDate(new Date());
                jDateChooser1.requestFocusInWindow();                
                return;
            }
        }
        if(jDateChooser1.getDate()!=null) {
            d=" and saledt >= #"+DateConverter.dateConverter1(fromdt)+"#";
            dtr.append("From Date: "+fromdt);
        } else {
            return;     
        }
        if(jDateChooser2.getDate()!=null) {
            Date toDt=jDateChooser2.getDate();
            try {
                todt=sdf.format(toDt);
            } catch(NullPointerException ex) {            
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"Invalid From Date.",
                        "Invalid Date",JOptionPane.ERROR_MESSAGE);
                jDateChooser2.setDate(new Date());
                jDateChooser2.requestFocusInWindow();                
                return;
            }
        }
        if(jDateChooser2.getDate()!=null) {
            e=" and saledt <= #"+DateConverter.dateConverter1(todt)+"#";
            dtr.append("  To Date: "+todt);
        } else {
            return;
        }
        switch(jComboBox3.getSelectedItem().toString()) {
            case "All Retailers": f = "";
                break;
            case "With GST No.": f = " and rgstno <> 'N/A'";
                break;
            case "Without GST No.": f = " and rgstno = 'N/A'";
                break;
        }
                
        // No. Of Columns: 11
        /* SLN., BILL NO., BILL DATE, RETAILER, GSTIN, TAXABLE, CGST %, CGST AMT., SGST %, SGST AMT., AMOUNT */
        clearTable(jTable1);
        // Table SaleMasterV2, no. of columns - 24
        /*
        salemid, compid, saledt, ordno, orddt, retid, deliverynote, paymentterm, transporter, vehicleno, 
        supplydt, totnoofitems, netgross, netitemdiscamt, netgstamt, cashdiscper, netcashdiscamt, 
        netamt, roundoff, dispscheme, netpayableamt, amtpaid, isactive, remarks
        */
        // Table SaleSubV2, no. of columns - 17
        /*
        salesid, salemid, psid, itemdid, mrp, gst, qty, free, unitnetrate, rate, 
        gross, itemdiscper, itemdiscamt, cashdiscamt, gstamt, amount, retqty
        */
        // Number of columns in Retailer: 18
        /* retid, beatid, retnm, contactperson, rstreet, rcity, rdist, rstate, rstatecode, 
        rpin, rcountry, rcontact, rmail, rgstno, rgstregntype, rpanno, raadhaarno, isactive*/
        String query="select salemid, saledt, retnm, rgstno, tottaxableamt, cgstper, totcgstamt, sgstper, "
                + "totsgstamt, tottotal from (select salemid, saledt, retid from SaleMasterV2"
                + " where isactive=1"+a+") x, (select retid, retnm, rgstno from Retailer where isactive=1"+b+f+") y, "
                + "(select salemid, sum(gross-itemdiscamt-cashdiscamt) as tottaxableamt, (gst/2.0) as cgstper, "
                + "sum(gstamt/2.0) as totcgstamt, (gst/2.0) as sgstper, sum(gstamt/2.0) as totsgstamt, "
                + "sum(amount) as tottotal from SaleSubV2 group by salemid, gst"+c+") z where "
                + "x.retid=y.retid and x.salemid=z.salemid"+d+e+" order by salemid, saledt";
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
                Vector<String> row1 = new Vector<String>();
                // No. Of Columns: 11
                /* SLN., BILL NO., BILL DATE, RETAILER, GSTIN, TAXABLE, CGST %, CGST AMT., SGST %, SGST AMT., AMOUNT */
                row1.addElement("SLN.");
                row1.addElement("BILL NO.");
                row1.addElement("BILL DATE");
                row1.addElement("RETAILER");
                row1.addElement("GSTIN");
                row1.addElement("TAXABLE");
                row1.addElement("CGST %");
                row1.addElement("CGST AMT.");
                row1.addElement("SGST %");
                row1.addElement("SGST AMT.");
                row1.addElement("AMOUNT");
                ((DefaultTableModel)jTable1.getModel()).addRow(row1);
                
                int slno1=0;
                while(rs.next()) {
                    Vector<String> row = new Vector<String>();
                    row.addElement(++slno1+"");
                    /* salemid, saledt, retnm, rgstno, tottaxableamt, cgstper, totcgstamt, sgstper, totsgstamt, tottotal */
                    row.addElement(rs.getString("salemid"));
                    row.addElement(DateConverter.dateConverter(rs.getString("saledt")));
                    row.addElement(rs.getString("retnm").replace("\\'", "'"));
                    row.addElement(rs.getString("rgstno"));
                    double tottaxableamt = Double.parseDouble(rs.getString("tottaxableamt"));
                    sumtaxableamt += tottaxableamt;
                    row.addElement(MyNumberFormat.rupeeFormat(tottaxableamt));
                    row.addElement(format.format(Double.parseDouble(rs.getString("cgstper"))));
                    double totcgstamt = Double.parseDouble(rs.getString("totcgstamt"));
                    sumcgstamt += totcgstamt;
                    row.addElement(MyNumberFormat.rupeeFormat(totcgstamt));
                    row.addElement(format.format(Double.parseDouble(rs.getString("sgstper"))));
                    double totsgstamt = Double.parseDouble(rs.getString("totsgstamt"));
                    sumsgstamt += totsgstamt;
                    row.addElement(MyNumberFormat.rupeeFormat(totsgstamt));
                    double tottotal = Double.parseDouble(rs.getString("tottotal"));
                    sumtotal += tottotal;
                    row.addElement(MyNumberFormat.rupeeFormat(tottotal));
                    ((DefaultTableModel)jTable1.getModel()).addRow(row);
                }
                
                Vector<String> row2 = new Vector<String>();
                row2.addElement("TOTAL");
                row2.addElement("-");
                row2.addElement("-");
                row2.addElement("-");
                row2.addElement("-");
                row2.addElement(MyNumberFormat.rupeeFormat(sumtaxableamt));
                row2.addElement("-");
                row2.addElement(MyNumberFormat.rupeeFormat(sumcgstamt));
                row2.addElement("-");
                row2.addElement(MyNumberFormat.rupeeFormat(sumsgstamt));
                row2.addElement(MyNumberFormat.rupeeFormat(sumtotal));
                ((DefaultTableModel)jTable1.getModel()).addRow(row2);
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Sale_GSTReport ex: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        } finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException ex){}
        }

        jTable1.setDragEnabled(false);
        // Disable auto resizing
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableHeader header = jTable1.getTableHeader();
        header.setBackground(Color.cyan);
        // No. Of Columns: 11
        /* SLN., BILL NO., BILL DATE, RETAILER, GSTIN, TAXABLE, CGST %, CGST AMT., SGST %, SGST AMT., AMOUNT */
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN.
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
        jTable1.getColumnModel().getColumn(1).setMinWidth(0);// BILL NO.
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(135);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);// BILL DATE
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// RETAILER
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(200);
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);// GSTIN
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);// TAXABLE
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(6).setMinWidth(0);// CGST %
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(7).setMinWidth(0);// CGST AMT.
        jTable1.getColumnModel().getColumn(7).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(8).setMinWidth(0);// SGST %
        jTable1.getColumnModel().getColumn(8).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(9).setMinWidth(0);// SGST AMT.
        jTable1.getColumnModel().getColumn(9).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(10).setMinWidth(0);// AMOUNT
        jTable1.getColumnModel().getColumn(10).setPreferredWidth(120);
        
        // align funda
        // No. Of Columns: 11
        /* SLN., BILL NO., BILL DATE, RETAILER, GSTIN, TAXABLE, CGST %, CGST AMT., SGST %, SGST AMT., AMOUNT */
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable1.getColumn("SLN.").setCellRenderer( centerRenderer );
        jTable1.getColumn("BILL NO.").setCellRenderer( centerRenderer );
        jTable1.getColumn("BILL DATE").setCellRenderer( centerRenderer );
        jTable1.getColumn("GSTIN").setCellRenderer( centerRenderer );
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        jTable1.getColumn("TAXABLE").setCellRenderer( rightRenderer );
        jTable1.getColumn("CGST %").setCellRenderer( rightRenderer );
        jTable1.getColumn("CGST AMT.").setCellRenderer( rightRenderer );
        jTable1.getColumn("SGST %").setCellRenderer( rightRenderer );
        jTable1.getColumn("SGST AMT.").setCellRenderer( rightRenderer );
        jTable1.getColumn("AMOUNT").setCellRenderer( rightRenderer );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jButton1 = new javax.swing.JButton();
        jComboBox3 = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

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

        jPanel1.setBackground(new java.awt.Color(226, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "REPORT SEARCH", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("COMPANY");

        jComboBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox1KeyPressed(evt);
            }
        });

        jComboBox2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox2KeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("RETAILER");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("CGST+SGST %");

        jTextField1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
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

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("FROM DATE");

        jDateChooser1.setDateFormatString("dd/MM/yyyy");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("TO DATE");

        jDateChooser2.setDateFormatString("dd/MM/yyyy");

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton1.setText("SEARCH");
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

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Retailers", "With GST No.", "Without GST No." }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jButton1)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "SALE-GST REPORT", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SLN.", "BILL NO.", "BILL DATE", "RETAILER", "GSTIN", "TAXABLE", "CGST %", "CGST AMT.", "SGST %", "SGST AMT.", "AMOUNT"
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameIconified(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameIconified
        moveToFront();
    }//GEN-LAST:event_formInternalFrameIconified

    private void jComboBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jComboBox2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jComboBox1KeyPressed

    private void jComboBox2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTextField1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jComboBox2KeyPressed

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jDateChooser1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Fetch();
        if ( jTable1.getRowCount() != 0 ) {
            jTable1.changeSelection(0, 0, false, false);
            jTable1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            Fetch();
            if ( jTable1.getRowCount() != 0 ) {
                jTable1.changeSelection(0, 0, false, false);
                jTable1.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jButton1KeyPressed

    private void jTextField1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusGained
        jTextField1.selectAll();
    }//GEN-LAST:event_jTextField1FocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
