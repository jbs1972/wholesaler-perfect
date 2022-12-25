package wholesalerPerfect;

import com.toedter.calendar.JTextFieldDateEditor;
import conn.dBConnection;
import dto.Enterprise;
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import query.Query;
import utilities.DateConverter;
import utilities.MyNumberFormat;
import utilities.Settings;

public class ItemStockLedger extends javax.swing.JInternalFrame implements AWTEventListener {
    
    private JDesktopPane jDesktopPane1;
    private UserProfile up;
    private Enterprise e;
    private Settings settings=new Settings();
    private DecimalFormat format = new DecimalFormat("0.#");
    private Query q=new Query();
    private DecimalFormat format2afterDecimal = new DecimalFormat("#.##");

    public ItemStockLedger(JDesktopPane jDesktopPane1, UserProfile up, Enterprise e) {
        super("Item Stock Ledger",false,true,false,true);
        initComponents();
        this.jDesktopPane1 = jDesktopPane1;
        this.e = e;
        Dimension d=getSize();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) dim.getWidth() - (int)d.getWidth())/2,((int) dim.getHeight() - (int)d.getHeight())/2-43);
	this.setResizable(false);
        this.setFrameIcon(new ImageIcon(getClass().getResource("/images/report01.png")));
        
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
        
        Fetch();
        
        SwingUtilities.invokeLater (() -> {
            jTextField1.requestFocusInWindow();
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
    
    private void clearTable(JTable table) {
        for(int i=table.getRowCount()-1; i>=0; i--) {
            ((DefaultTableModel)table.getModel()).removeRow(i);
        }
    }

    private void Fetch() {
        String x="", a="", b="";
        String fromdt="",todt="";
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
            x=" and ItemLedger.actiondt < #"+DateConverter.dateConverter1(fromdt)+"#";
            a=" and ItemLedger.actiondt >= #"+DateConverter.dateConverter1(fromdt)+"#";
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
            b=" and ItemLedger.actiondt <= #"+DateConverter.dateConverter1(todt)+"#";
        } else {
            return;
        }
        clearTable(jTable1);
        ArrayList<String> allItemDetails = getItemDetails();
        // Getting Opening Stock for each itemdid
        int slno1=0;
        for ( String s : allItemDetails ) {
            System.out.println(s);
            String sa1[] = s.split("~");
            //     0      1     2      3      4    5     6       7       8       9
            // compnm, itemnm, hsn, itemdid, mrp, gst, pexgst, pingst, sexgst, singst
            //  0     1       2      3
            // ilid, type, prevqty, qty
            int intOpeningStock = 0;
            String openingStock = getOpeningStock(sa1[3], x);
            System.out.println(openingStock);
            if (openingStock != null) {
                String sa2[] = openingStock.split("~");
                if (sa2[1].equals("ADD")) {
                    intOpeningStock = Integer.parseInt(sa2[2]) + Integer.parseInt(sa2[3]);
                } else { // for LESS
                    intOpeningStock = Integer.parseInt(sa2[2]) - Integer.parseInt(sa2[3]);
                }
            }
            // getClosingStock(String itemdid, String fromString, String toString)
            String closingStock = getClosingStock(sa1[3], b);
            int intClosingStock = 0;
            System.out.println(closingStock);
            if (closingStock != null) {
                String sa3[] = closingStock.split("~");
                if (sa3[1].equals("ADD")) {
                    intClosingStock = Integer.parseInt(sa3[2]) + Integer.parseInt(sa3[3]);
                } else { // for LESS
                    intClosingStock = Integer.parseInt(sa3[2]) - Integer.parseInt(sa3[3]);
                }
            }
            
            Vector<String> row = new Vector<String>();
            row.addElement(++slno1+"");
            // NO. OF COLUMNS: 12
            /* SLN., COMPANY, ITEM, HSN, MRP, GST%, PEXGST, PINGST, SEXGST, SINGST, OPENING STK., CLOSING STK. */
            row.addElement(sa1[0].replace("\\'", "'")); //COMPANY
            row.addElement(sa1[1].replace("\\'", "'")); //ITEM
            row.addElement(sa1[2]); //HSN
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(sa1[4]))); //MRP
            row.addElement(format.format(Double.parseDouble(sa1[5]))); //GST%
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(sa1[6]))); //PEXGST
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(sa1[7]))); //PINGST
            row.addElement(String.valueOf(intOpeningStock));
            row.addElement(String.valueOf(intClosingStock));
            ((DefaultTableModel)jTable1.getModel()).addRow(row);
        }

        jTable1.setDragEnabled(false);
        // Disable auto resizing
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableHeader header = jTable1.getTableHeader();
        header.setBackground(Color.cyan);
        // NO. OF COLUMNS: 10
        /* SLN., COMPANY, ITEM, HSN, MRP, GST%, PEXGST, PINGST, OPENING STK., CLOSING STK. */
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
        jTable1.getColumnModel().getColumn(1).setMinWidth(0);// COMPANY
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(150);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);// ITEM
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(250);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// HSN
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);// MRP
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);// GST%
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(6).setMinWidth(0);// PEXGST
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(7).setMinWidth(0);// PINGST
        jTable1.getColumnModel().getColumn(7).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(8).setMinWidth(0);// OPENING STK.
        jTable1.getColumnModel().getColumn(8).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(9).setMinWidth(0);// CLOSING STK.
        jTable1.getColumnModel().getColumn(9).setPreferredWidth(100);
        
        // align funda
        // NO. OF COLUMNS: 10
        /* SLN., COMPANY, ITEM, HSN, MRP, GST%, PEXGST, PINGST, OPENING STK., CLOSING STK. */
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable1.getColumn("SLN.").setCellRenderer( centerRenderer );
        jTable1.getColumn("HSN").setCellRenderer( centerRenderer );
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        jTable1.getColumn("MRP").setCellRenderer( rightRenderer );
        jTable1.getColumn("GST%").setCellRenderer( rightRenderer );
        jTable1.getColumn("PEXGST").setCellRenderer( rightRenderer );
        jTable1.getColumn("PINGST").setCellRenderer( rightRenderer );
        jTable1.getColumn("OPENING STK.").setCellRenderer( rightRenderer );
        jTable1.getColumn("CLOSING STK.").setCellRenderer( rightRenderer );
    }
    
    private ArrayList<String> getItemDetails() {
        ArrayList<String> result = new ArrayList<String>();
        String a = "", b = "";
        String compnm=jTextField1.getText().trim().toUpperCase().replace("'", "\\'");
        if(compnm.length()!=0) {
            a=" and compnm like '%"+compnm+"%'";
        }
        String itemnm=jTextField2.getText().trim().toUpperCase();
        if(itemnm.length()!=0) {
            b=" and itemnm like '%"+itemnm+"%'";
        }
        // Table CompanyMaster, no. of columns - 4
        // compid, compnm, compabbr, isactive
        // Table ItemMaster, no. of columns - 5
        // itemmid, compid, itemnm, hsn, isactive
        // Table ItemDetails, no. of columns - 10
        // itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive
        String sql = "select a.compnm, b.itemnm, b.hsn, c.itemdid, c.mrp, c.gst, c.pexgst, c.pingst, c.sexgst, c.singst "
                + "from (select compid, compnm from CompanyMaster where isactive=1"+a+") a, "
                + "(select itemmid, compid, itemnm, hsn from ItemMaster where isactive=1"+b+") b, "
                + "(select itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst from ItemDetails "
                + "where isactive=1) c where a.compid = b.compid and b.itemmid = c.itemmid "
                + "order by a.compid, b.itemmid, c.itemdid";
        System.out.println(sql);
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        int total=0;
        try {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(sql);
            //Move to the last record
            rs.afterLast();
            //Get the current record position
            if(rs.previous())
                total = rs.getRow();
            //Move back to the first record;
            rs.beforeFirst();
            if(total != 0) {
                while(rs.next()) {
                    // compnm, itemnm, hsn, itemdid, mrp, gst, pexgst, pingst, sexgst, singst
                    result.add(rs.getString("compnm")+"~"+rs.getString("itemnm")+"~"+rs.getString("hsn")+"~"+rs.getString("itemdid")
                            +"~"+rs.getString("mrp")+"~"+rs.getString("gst")+"~"+rs.getString("pexgst")+"~"+rs.getString("pingst")
                            +"~"+rs.getString("sexgst")+"~"+rs.getString("singst"));
                }
            }
        }
        catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"ItemStockLedger ex: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        } finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException e){}
        }
        return result;
    }
    
    private String getOpeningStock(String itemdid, String dateString) {
        String result = null;
        // Table ItemLedger, no. of columns - 9
        // ilid, itemdid, tablenm, pknm, pkval, actiondt, type, prevqty, qty
        String sql = "select top 1 ilid, type, prevqty, qty from ItemLedger where itemdid="
                + itemdid+dateString +" order by ilid desc";
        System.out.println(sql);
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        int total=0;
        try {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(sql);
            //Move to the last record
            rs.afterLast();
            //Get the current record position
            if(rs.previous())
                total = rs.getRow();
            //Move back to the first record;
            rs.beforeFirst();
            if(total != 0) {
                if(rs.next()) {
                    // ilid, type, prevqty, qty
                    result=rs.getString("ilid")+"~"+rs.getString("type")+"~"+rs.getString("prevqty")+"~"+rs.getString("qty");
                }
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"ItemStockLedger ex: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        } finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException e){}
        }
        return result;
    }
    
    private String getClosingStock(String itemdid, String toDt) {
        String result = null;
        // Table ItemLedger, no. of columns - 9
        // ilid, itemdid, tablenm, pknm, pkval, actiondt, type, prevqty, qty
        String sql = "select top 1 ilid, type, prevqty, qty from ItemLedger where itemdid="
                + itemdid+toDt +" order by ilid desc";
        System.out.println(sql);
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        int total=0;
        try {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(sql);
            //Move to the last record
            rs.afterLast();
            //Get the current record position
            if(rs.previous())
                total = rs.getRow();
            //Move back to the first record;
            rs.beforeFirst();
            if(total != 0) {
                if(rs.next()) {
                    // ilid, type, prevqty, qty
                    result=rs.getString("ilid")+"~"+rs.getString("type")+"~"+rs.getString("prevqty")+"~"+rs.getString("qty");
                }
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"ItemStockLedger ex: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        } finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException e){}
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jButton1 = new javax.swing.JButton();
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

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("COMPANY");

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
        });

        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField2KeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("ITEM");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("FROM DATE");

        jDateChooser1.setDateFormatString("dd/MM/yyyy");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("TO DATE");

        jDateChooser2.setDateFormatString("dd/MM/yyyy");

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

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 0, 0), 1, true), "STOCK LEDGER", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(0, 0, 255))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SLN.", "COMPANY", "ITEM", "HSN", "MRP", "GST%", "PEXGST", "PINGST", "OPENING STK.", "CLOSING STK."
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Fetch();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            Fetch();
        }
    }//GEN-LAST:event_jButton1KeyPressed

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTextField2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jTextField2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jDateChooser1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField2KeyPressed

    private void formInternalFrameIconified(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameIconified
        moveToFront();
    }//GEN-LAST:event_formInternalFrameIconified


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
