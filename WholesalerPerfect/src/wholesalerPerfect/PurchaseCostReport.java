package wholesalerPerfect;

import conn.dBConnection;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import utilities.DateConverter;
import utilities.MyNumberFormat;
import utilities.Settings;

public class PurchaseCostReport extends javax.swing.JInternalFrame {

    private DecimalFormat format = new DecimalFormat("0.#");
    private Settings settings=new Settings();
    
    public PurchaseCostReport() {
        super("Purchase Cost Report",false,true,false,true);
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
        
        ((DefaultTableCellRenderer)jTable1.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        
        Fetch();
        
        SwingUtilities.invokeLater (() -> {
            jTextField1.requestFocusInWindow();
        });
    }
    
    private void clearTable(JTable table) {
        for(int i=table.getRowCount()-1; i>=0; i--) {
            ((DefaultTableModel)table.getModel()).removeRow(i);
        }
    }

    private void Fetch() {
        double netCost = 0.0;
        
        String a="";
        String itemnm=jTextField1.getText().trim().toUpperCase();
        if(itemnm.length()!=0) {
            a=" and itemnm like '%"+itemnm+"%'";
        }
        
        // Purchase Cost Report - No. of columns 15
        /*
        SLN., INV. NO., INV. DATE, ITEM, HSN, QTY., MRP, RATE, ITEM DISC.%, 
        TRADE DISC.%, REPCL. DISC.%, GST, UNIT COST, ON HAND, NET COST 
        */
        int slno1=0;
        clearTable(jTable1);
        // Number of columns in ItemMaster: 5
        /* itemmid, compid, itemnm, hsn, isactive */
        // Number of columns in ItemDetails: 10
        /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
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
        // Table PurchaseGSTV2, no. of columns - 5
        /*
        pgstid, pmid, gstper, taxableamt, gstamt
        */
        String query="select a.invno, a.invdt, a.tradediscper, a.replacementdiscper, d.itemnm, "
                + "d.hsn, b.gst, b.rate, b.mrp, b.qty, b.discper, b.onhand from (select pmid, "
                + "invno, invdt, tradediscper, replacementdiscper from PurchaseMasterV2 where "
                + "isactive=1) a, (select pmid, itemdid, gst, rate, mrp, qty, discper,"
                + " qty-qtysold as onhand from PurchaseSubV2 where qty-qtysold>0) b, (select itemdid, "
                + "itemmid from ItemDetails where isactive=1) c, (select itemmid, itemnm, hsn "
                + "from ItemMaster where isactive=1"+a+") d where a.pmid=b.pmid and b.itemdid=c.itemdid "
                + "and c.itemmid=d.itemmid order by d.itemnm, a.invdt, a.invno";
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
                while(rs.next()) {
                    Vector<String> row = new Vector<String>();
                    row.addElement(++slno1+"");
                    // a.invno, a.invdt, a.tradediscper, a.replacementdiscper, d.itemnm, d.hsn, b.gst, b.rate, b.mrp, b.qty, b.disper, b.onhand
                    // Purchase Cost Report - No. of columns 15
                    /*
                    SLN., INV. NO., INV. DATE, ITEM, HSN, QTY., MRP, RATE, ITEM DISC.%, 
                    TRADE DISC.%, REPCL. DISC.%, GST, UNIT COST, ON HAND, NET COST 
                    */
                    row.addElement(rs.getString("invno"));
                    row.addElement(DateConverter.dateConverter(rs.getString("invdt")));
                    row.addElement(rs.getString("itemnm"));
                    row.addElement(rs.getString("hsn"));
                    double qty = Double.parseDouble(rs.getString("qty"));
                    row.addElement(format.format(qty));
                    row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(rs.getString("mrp"))));
                    double rate = Double.parseDouble(rs.getString("rate"));
                    row.addElement(MyNumberFormat.rupeeFormat(rate));
                    double itemDiscamtPer = Double.parseDouble(rs.getString("discper"));
                    row.addElement(format.format(itemDiscamtPer)+"%");
                    double tradediscper = Double.parseDouble(rs.getString("tradediscper"));
                    row.addElement(format.format(tradediscper)+"%");
                    double replacementdiscper = Double.parseDouble(rs.getString("replacementdiscper"));
                    row.addElement(format.format(replacementdiscper)+"%");
                    double gstper = Double.parseDouble(rs.getString("gst"));
                    row.addElement(format.format(gstper)+"%");
                    double gross = rate * qty;
                    double amtAfterItemDiscount = gross - (gross * (itemDiscamtPer/100.0));
                    double amtAfterTradeDiscount = amtAfterItemDiscount - (amtAfterItemDiscount * (tradediscper / 100.0));
                    double amtAfterReplacementDiscount = amtAfterTradeDiscount - (amtAfterTradeDiscount * (replacementdiscper / 100.0));
                    double amtAfterGST = amtAfterReplacementDiscount + (amtAfterReplacementDiscount * (gstper / 100.0));
                    double cost = amtAfterGST / qty;
                    row.addElement(MyNumberFormat.rupeeFormat(cost));
                    double onhand = Double.parseDouble(rs.getString("onhand"));
                    netCost += cost * onhand;
                    row.addElement(format.format(onhand));
                    row.addElement(MyNumberFormat.rupeeFormat(cost * onhand));
                    ((DefaultTableModel)jTable1.getModel()).addRow(row);
                }
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"PurchaseCostReport ex: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        } finally {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e){}
        }

        jTable1.setDragEnabled(false);
        // Disable auto resizing
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableHeader header = jTable1.getTableHeader();
        header.setBackground(Color.cyan);
        //Start resize the table column
        // Purchase Cost Report - No. of columns 15
        /*
        SLN., INV. NO., INV. DATE, ITEM, HSN, QTY., MRP, RATE, ITEM DISC.%, 
        TRADE DISC.%, REPCL. DISC.%, GST, UNIT COST, ON HAND, NET COST 
        */
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN.
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
        jTable1.getColumnModel().getColumn(1).setMinWidth(0);// INV. NO.
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);// INV. DATE
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// ITEM
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(180);
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);// HSN
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);// QTY
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(50);
        jTable1.getColumnModel().getColumn(6).setMinWidth(0);// MRP
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(7).setMinWidth(0);// RATE
        jTable1.getColumnModel().getColumn(7).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(8).setMinWidth(0);// ITEM DISC.%
        jTable1.getColumnModel().getColumn(8).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(9).setMinWidth(0);// TRADE DISC.%
        jTable1.getColumnModel().getColumn(9).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(10).setMinWidth(0);// REPCL. DISC.%
        jTable1.getColumnModel().getColumn(10).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(11).setMinWidth(0);// GST
        jTable1.getColumnModel().getColumn(11).setPreferredWidth(50);
        jTable1.getColumnModel().getColumn(12).setMinWidth(0);// UNIT COST
        jTable1.getColumnModel().getColumn(12).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(13).setMinWidth(0);// ON HAND
        jTable1.getColumnModel().getColumn(13).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(14).setMinWidth(0);// NET COST
        jTable1.getColumnModel().getColumn(14).setPreferredWidth(70);
        
        // Purchase Cost Report - No. of columns 15
        /*
        SLN., INV. NO., INV. DATE, ITEM, HSN, QTY., MRP, RATE, ITEM DISC.%, 
        TRADE DISC.%, REPCL. DISC.%, GST, UNIT COST, ON HAND, NET COST 
        */
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable1.getColumn("SLN.").setCellRenderer( centerRenderer );
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        jTable1.getColumn("QTY.").setCellRenderer( rightRenderer );
        jTable1.getColumn("MRP").setCellRenderer( rightRenderer );
        jTable1.getColumn("RATE").setCellRenderer( rightRenderer );
        jTable1.getColumn("ITEM DISC.%").setCellRenderer( rightRenderer );
        jTable1.getColumn("TRADE DISC.%").setCellRenderer( rightRenderer );
        jTable1.getColumn("REPCL. DISC.%").setCellRenderer( rightRenderer );
        jTable1.getColumn("GST").setCellRenderer( rightRenderer );
        jTable1.getColumn("UNIT COST").setCellRenderer( rightRenderer );
        jTable1.getColumn("ON HAND").setCellRenderer( rightRenderer );
        jTable1.getColumn("NET COST").setCellRenderer( rightRenderer );
        
        jLabel5.setText(MyNumberFormat.rupeeFormat(netCost)+"  ");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

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

        jPanel1.setBackground(new java.awt.Color(153, 204, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 0, 51), 2, true), "SEARCH", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 255))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("ITEM NAME");

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(349, 349, 349)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(357, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 11, Short.MAX_VALUE))
        );

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 51), 2, true), "PURCHASE COST REPORT", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SLN.", "INV. NO.", "INV. DATE", "ITEM", "HSN", "QTY.", "MRP", "RATE", "ITEM DISC.%", "TRADE DISC.%", "REPCL. DISC.%", "GST", "UNIT COST", "ON HAND", "NET COST"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("NET COST");

        jLabel5.setBackground(new java.awt.Color(255, 255, 0));
        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("0  ");
        jLabel5.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        Fetch();
    }//GEN-LAST:event_jTextField1KeyReleased

    private void formInternalFrameIconified(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameIconified
        moveToFront();
    }//GEN-LAST:event_formInternalFrameIconified


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
