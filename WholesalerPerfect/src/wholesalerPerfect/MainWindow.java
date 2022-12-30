package wholesalerPerfect;

import conn.BackupDB;
import conn.RestoreDB;
import conn.dBConnection;
import dto.UserProfile;
import java.awt.Dimension;
import java.awt.Toolkit;
import dto.Enterprise;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import query.Query;
import utilities.MyCustomFilterDBBackup;
import utilities.clsTimer;

public class MainWindow extends javax.swing.JFrame {

    private UserProfile up=new UserProfile();
    private String loginTime="";
    private Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    private Enterprise e;
    private Query q=new Query();
    private boolean enterpriseSetupFlag;
    public boolean openReservationPanelFlag;

    /** Creates new form MainWindow */
    public MainWindow(UserProfile up, String loginTime) 
    {
        super("WHOLESALER PERFECT V-1.0");
        initComponents();
        this.up=up;
        this.loginTime=loginTime;

        jMenu6.setMnemonic('F');// File
        jMenu2.setMnemonic('Y');// System Setup
        jMenu1.setMnemonic('B');// Business Setup
        jMenu13.setMnemonic('P');// Purchase
        jMenu17.setMnemonic('S');// Sale
        jMenu10.setMnemonic('N');// Print

        this.setIconImage(new ImageIcon(getClass().getResource("/images/WholeSaler.jpg")).getImage());
        this.setSize(screen);
        new clsTimer(jLabel3, 1);

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                String ObjButtons[] = {"Yes","Cancel"};
                int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure to Leave from Application","Bye...",
                        JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null,ObjButtons,ObjButtons[1]);
                if(PromptResult==0)
                {
                    // Mandatory Database Backup
                    JFileChooser fc= new JFileChooser();
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int returnVal = fc.showSaveDialog(MainWindow.this);
                    String path="";
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        path = fc.getSelectedFile().getAbsolutePath();
                        BackupDB.copyFile(path);
                    }
                    
                    Calendar cal=Calendar.getInstance();
                    Date date = cal.getTime();
                    DateFormat dateFormatter = DateFormat.getTimeInstance();
                    String logoutTime=DateFormat.getDateInstance().format(date) + " [ " + dateFormatter.format(date) + " ]";
                    String query="update LoginDetails set logout='"+logoutTime+"' where uid="+MainWindow.this.up.getUid()+" and login='"+MainWindow.this.loginTime+"'";
                    dBConnection db=new dBConnection();
                    Connection conn=db.setConnection();
                    try
                    {
                        Statement stm=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        stm.executeUpdate(query);
                    }
                    catch(SQLException ex)
                    {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null,"MainWindow Ex1: "+ex,"Error Found",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    finally
                    {
                        try {
                            if (conn!=null) conn.close();
                        }
                        catch(SQLException ex){}
                    }
                    setVisible(false);
                    dispose();
                    System.exit(0);
                }
            }
        });

        e=q.getEnterprise();
        if(e==null)
            enterpriseSetupFlag=false;
        else
        {
            jLabel7.setText("CURRENT USER: "+up.getUnm());
            enterpriseSetupFlag=true;
        }
        if(!enterpriseSetupFlag)
        {
            JOptionPane.showMessageDialog(null,"Your Enterprise Setup Incomplete, Open System Settings->Enterprise Add to provide it.",
                    "Enterprise Setup Message",JOptionPane.WARNING_MESSAGE);
        }
        
        SwingUtilities.invokeLater(
            new Runnable() 
            {
                @Override
                public void run() 
                {
                    jLabel5.setIcon(new ImageIcon (Toolkit.getDefaultToolkit().createImage(getClass().getResource("/images/bgimg.jpg")).
                        getScaledInstance(jLabel5.getWidth(), jLabel5.getHeight(), Image.SCALE_SMOOTH)));
                }
            }
        );
    }

    private void runComponents(String sComponents)
    {
        Runtime rt = Runtime.getRuntime();
        try{rt.exec(sComponents);}
        catch(IOException evt)
        {
            JOptionPane.showMessageDialog(null,evt.getMessage(),"Error Found",JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem12 = new javax.swing.JMenuItem();
        jToolBar1 = new javax.swing.JToolBar();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        jLabel5 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu4 = new javax.swing.JMenu();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem62 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem34 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu8 = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenu9 = new javax.swing.JMenu();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenu13 = new javax.swing.JMenu();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenuItem21 = new javax.swing.JMenuItem();
        jMenu14 = new javax.swing.JMenu();
        jMenu17 = new javax.swing.JMenu();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenuItem19 = new javax.swing.JMenuItem();
        jMenu15 = new javax.swing.JMenu();
        jMenu10 = new javax.swing.JMenu();
        jMenuItem18 = new javax.swing.JMenuItem();
        jMenu16 = new javax.swing.JMenu();
        jMenu11 = new javax.swing.JMenu();
        jMenuItem25 = new javax.swing.JMenuItem();
        jMenuItem26 = new javax.swing.JMenuItem();
        jMenuItem20 = new javax.swing.JMenuItem();
        jMenuItem27 = new javax.swing.JMenuItem();
        jMenuItem23 = new javax.swing.JMenuItem();
        jMenuItem22 = new javax.swing.JMenuItem();
        jMenuItem24 = new javax.swing.JMenuItem();

        jMenuItem12.setText("jMenuItem12");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        jToolBar1.setBackground(new java.awt.Color(205, 222, 238));
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jLabel4.setText("          ");
        jToolBar1.add(jLabel4);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 102, 102));
        jLabel1.setText("WHOLESALER PERFECT V-1.0 @ 2019-20");
        jToolBar1.add(jLabel1);

        jLabel2.setText("             ");
        jToolBar1.add(jLabel2);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 204));
        jLabel3.setText("10:10");
        jToolBar1.add(jLabel3);
        jToolBar1.add(filler1);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(102, 0, 0));
        jLabel7.setText("CURRENT USER: N/A");
        jToolBar1.add(jLabel7);

        jLabel6.setText("          ");
        jToolBar1.add(jLabel6);

        jLabel5.setBackground(new java.awt.Color(204, 255, 204));
        jLabel5.setOpaque(true);

        jDesktopPane1.setLayer(jLabel5, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
        );

        jMenu4.setText("    ");
        jMenu4.setEnabled(false);
        jMenuBar1.add(jMenu4);

        jMenu6.setText("File");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Log Details.png"))); // NOI18N
        jMenuItem1.setText("Login Details");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem1);
        jMenu6.add(jSeparator2);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calc.png"))); // NOI18N
        jMenuItem2.setText("Calculator");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/notepad.png"))); // NOI18N
        jMenuItem3.setText("Notepad");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem3);
        jMenu6.add(jSeparator3);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_DOWN_MASK));
        jMenuItem4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/EXIT.PNG"))); // NOI18N
        jMenuItem4.setText("Exit");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem4);

        jMenuBar1.add(jMenu6);

        jMenu3.setText("    ");
        jMenu3.setEnabled(false);
        jMenuBar1.add(jMenu3);

        jMenu2.setText("System Setup");

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Branches.png"))); // NOI18N
        jMenuItem5.setText("Edit Enterprise Data");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/SalesRep.png"))); // NOI18N
        jMenuItem6.setText("User Profiles");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem6);

        jMenuItem62.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem62.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendar_year.png"))); // NOI18N
        jMenuItem62.setText("Financial Year");
        jMenuItem62.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem62ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem62);
        jMenu2.add(jSeparator1);

        jMenuItem34.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/db_backup.png"))); // NOI18N
        jMenuItem34.setText("Database Backup");
        jMenuItem34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem34ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem34);

        jMenuItem13.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/db_restore.png"))); // NOI18N
        jMenuItem13.setText("Database Restore");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem13);

        jMenuBar1.add(jMenu2);

        jMenu7.setText("    ");
        jMenu7.setEnabled(false);
        jMenuBar1.add(jMenu7);

        jMenu1.setText("Business Setup");

        jMenuItem8.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.ALT_DOWN_MASK));
        jMenuItem8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/paymentmode.png"))); // NOI18N
        jMenuItem8.setText("Payment Mode");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem8);

        jMenuItem7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_DOWN_MASK));
        jMenuItem7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/company.png"))); // NOI18N
        jMenuItem7.setText("Company");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem7);

        jMenu8.setText("Item");

        jMenuItem9.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/item.png"))); // NOI18N
        jMenuItem9.setText("Item Master");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem9);

        jMenuItem10.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/itemDetails.png"))); // NOI18N
        jMenuItem10.setText("Item Details");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem10);

        jMenu1.add(jMenu8);

        jMenuItem11.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.ALT_DOWN_MASK));
        jMenuItem11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/SUPPLIER.PNG"))); // NOI18N
        jMenuItem11.setText("Super Stockist");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem11);

        jMenu9.setText("Retailer");

        jMenuItem14.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/beat.png"))); // NOI18N
        jMenuItem14.setText("Beat Master");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem14);

        jMenuItem15.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/customer.png"))); // NOI18N
        jMenuItem15.setText("Retailer Master");
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem15);

        jMenu1.add(jMenu9);

        jMenuBar1.add(jMenu1);

        jMenu5.setText("    ");
        jMenu5.setEnabled(false);
        jMenuBar1.add(jMenu5);

        jMenu13.setText("Purchase");

        jMenuItem16.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/purchase01.png"))); // NOI18N
        jMenuItem16.setText("Purchase");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu13.add(jMenuItem16);

        jMenuItem21.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/MODIFY.PNG"))); // NOI18N
        jMenuItem21.setText("Edit/Delete Purchase");
        jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem21ActionPerformed(evt);
            }
        });
        jMenu13.add(jMenuItem21);

        jMenuBar1.add(jMenu13);

        jMenu14.setText("    ");
        jMenu14.setEnabled(false);
        jMenuBar1.add(jMenu14);

        jMenu17.setText("Sale");

        jMenuItem17.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/prod_sale_01.png"))); // NOI18N
        jMenuItem17.setText("Sale");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu17.add(jMenuItem17);

        jMenuItem19.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.ALT_DOWN_MASK));
        jMenuItem19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/MODIFY.PNG"))); // NOI18N
        jMenuItem19.setText("Edit/Delete Sale");
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem19ActionPerformed(evt);
            }
        });
        jMenu17.add(jMenuItem19);

        jMenuBar1.add(jMenu17);

        jMenu15.setText("    ");
        jMenu15.setEnabled(false);
        jMenuBar1.add(jMenu15);

        jMenu10.setText("Print");

        jMenuItem18.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/PRINT.PNG"))); // NOI18N
        jMenuItem18.setText("Re-print Sale Bill");
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem18);

        jMenuBar1.add(jMenu10);

        jMenu16.setText("    ");
        jMenu16.setEnabled(false);
        jMenuBar1.add(jMenu16);

        jMenu11.setText("Reports");

        jMenuItem25.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/report01.png"))); // NOI18N
        jMenuItem25.setText("Daily Dispatch Report");
        jMenuItem25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem25ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem25);

        jMenuItem26.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/report01.png"))); // NOI18N
        jMenuItem26.setText("Purchase Cost Report");
        jMenuItem26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem26ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem26);

        jMenuItem20.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/report01.png"))); // NOI18N
        jMenuItem20.setText("Item Stock Ledger");
        jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem20ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem20);

        jMenuItem27.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.ALT_DOWN_MASK));
        jMenuItem27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/report01.png"))); // NOI18N
        jMenuItem27.setText("Item Stock Report");
        jMenuItem27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem27ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem27);

        jMenuItem23.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.ALT_DOWN_MASK));
        jMenuItem23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/report01.png"))); // NOI18N
        jMenuItem23.setText("Purchase GST Report");
        jMenuItem23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem23ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem23);

        jMenuItem22.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/report01.png"))); // NOI18N
        jMenuItem22.setText("Sale GST Report");
        jMenuItem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem22ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem22);

        jMenuItem24.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.ALT_DOWN_MASK));
        jMenuItem24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/report01.png"))); // NOI18N
        jMenuItem24.setText("Partywise Product Sale Report");
        jMenuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem24ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem24);

        jMenuBar1.add(jMenu11);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 813, Short.MAX_VALUE)
            .addComponent(jDesktopPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jDesktopPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // Login Details - 
        try {
            LoginDetails ref=new LoginDetails();
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        } catch(PropertyVetoException e){}
}//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // Calculator
        runComponents("Calc.exe");
}//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // Notepad
        runComponents("Notepad.exe");
}//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // Exit Menu Item
        String ObjButtons[] = {"Yes","Cancel"};
        int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure to Leave from Application",
                "Bye...",JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null,ObjButtons,ObjButtons[1]);
        if(PromptResult==0) 
        {
            // Mandatory Database Backup
            JFileChooser fc= new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fc.showSaveDialog(MainWindow.this);
            String path="";
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                path = fc.getSelectedFile().getAbsolutePath();
                BackupDB.copyFile(path);
            }
            
            Calendar cal=Calendar.getInstance();
            Date date = cal.getTime();
            DateFormat dateFormatter = DateFormat.getTimeInstance();
            String logoutTime=DateFormat.getDateInstance().format(date) + " [ " + dateFormatter.format(date) + " ]";
            String query="update LoginDetails set logout='"+logoutTime+"' where uid='"+MainWindow.this.up.getUid()+"' and login='"+MainWindow.this.loginTime+"'";
            dBConnection db=new dBConnection();
            Connection conn=db.setConnection();
            try {
                Statement stm=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
                stm.executeUpdate(query);
            } catch(SQLException ex)
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"MainWindow Ex2: "+ex,"Error Found",JOptionPane.ERROR_MESSAGE);
                return;
            } finally {
                try {
                    if (conn!=null) conn.close();
                } catch(SQLException ex){}
            }
            this.setVisible(false);
            this.dispose();
            System.exit(0);
        }
}//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // Enterprise Edit
        try {
            EnterpriseEdit02 ref=new EnterpriseEdit02();
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        } catch(PropertyVetoException e){}
}//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // User Profile
        try {
            UserProfiles ref=new UserProfiles(up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        } catch(PropertyVetoException e){}
}//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem62ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem62ActionPerformed
        // Finalcial Year - calendar_year.png
        try {
            FinancialYear ref=new FinancialYear(up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        } catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem62ActionPerformed

    private void jMenuItem34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem34ActionPerformed
        // Database Backup
        JFileChooser fc= new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showSaveDialog(MainWindow.this);
        String path="";
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            path = fc.getSelectedFile().getAbsolutePath();
        }
        BackupDB.copyFile(path);
    }//GEN-LAST:event_jMenuItem34ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        // Database Restore - MODIFY.PNG
        JFileChooser fc= new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileFilter(new MyCustomFilterDBBackup());
        int returnVal = fc.showSaveDialog(MainWindow.this);
        String path="";
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            path = fc.getSelectedFile().getAbsolutePath();
        }
        System.out.println("Backup File Path= "+path);
        RestoreDB.copyFile(path);
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        // Company Master
        try
        {
            CompanyMaster ref=new CompanyMaster(false, up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        // Payment mode Master
        try
        {
            PaymentModeMaster ref=new PaymentModeMaster(false, up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        // Item Master
        try
        {
            ItemMaster ref=new ItemMaster(jDesktopPane1, false, up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        // Item Details
        try
        {
            ItemDetails ref=new ItemDetails(jDesktopPane1, false, up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        // Super Stockist
        try
        {
            SuperStockistMaster ref=new SuperStockistMaster(jDesktopPane1, false, up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        // Beat Master
        try
        {
            BeatMaster ref=new BeatMaster(false, up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
        // Customer Master
        try
        {
            RetailerMaster ref=new RetailerMaster(jDesktopPane1, false, up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        // Purchase
        try
        {
            // Purchase01 ref=new Purchase01(jDesktopPane1, up, false);
            // Purchase02 ref=new Purchase02(jDesktopPane1, up, false);
            Purchase03 ref=new Purchase03(jDesktopPane1, up, false);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        // Sale
        try
        {
            String enameDb = q.getEnterpriseName();
            if(!enameDb.equalsIgnoreCase("SHIB DURGA ENTERPRISE")) {
                JOptionPane.showMessageDialog(null,"You are not authorized to use this Software!!!",
                    "Authenticity Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
//            Sale01 ref=new Sale01(jDesktopPane1, up, e, false);
//            Sale02 ref=new Sale02(jDesktopPane1, up, e, false);
            SaleV3 ref=new SaleV3(jDesktopPane1, up, e, false);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed
        // Print
        try
        {
            ReprintSaleBill ref=new ReprintSaleBill(jDesktopPane1, e);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem18ActionPerformed

    private void jMenuItem19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem19ActionPerformed
        // Edit/Delete Sale Bill
        try
        {
            EditDeleteSaleBill ref=new EditDeleteSaleBill(jDesktopPane1, e, up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem19ActionPerformed

    private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem21ActionPerformed
        // Edit/Delete Purchase
        try
        {
            EditDeletePurchase ref=new EditDeletePurchase(jDesktopPane1, up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem21ActionPerformed

    private void jMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem20ActionPerformed
        // Item Stock Ledger
        try
        {
            ItemStockLedger ref=new ItemStockLedger(jDesktopPane1, up, e);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem20ActionPerformed

    private void jMenuItem22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem22ActionPerformed
        // Sale GST Report
        try
        {
            Sale_GSTReport ref=new Sale_GSTReport();
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem22ActionPerformed

    private void jMenuItem23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem23ActionPerformed
        // Purchase GST Report
        try
        {
            Purchase_GSTReport ref=new Purchase_GSTReport();
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem23ActionPerformed

    private void jMenuItem24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem24ActionPerformed
        // Partywise Product Sale Report
        try
        {
            PartywiseProductSaleReport ref=new PartywiseProductSaleReport();
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem24ActionPerformed

    private void jMenuItem25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem25ActionPerformed
        // Daily Dispatch Report
        try
        {
            DailyDispatchReport ref=new DailyDispatchReport(e);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem25ActionPerformed

    private void jMenuItem26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem26ActionPerformed
        // Purchase Cost Report
        try
        {
            PurchaseCostReport ref=new PurchaseCostReport();
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem26ActionPerformed

    private void jMenuItem27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem27ActionPerformed
        // Item Stock Report
        try
        {
            ItemStockReport ref=new ItemStockReport(jDesktopPane1, up, e);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem27ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu10;
    private javax.swing.JMenu jMenu11;
    private javax.swing.JMenu jMenu13;
    private javax.swing.JMenu jMenu14;
    private javax.swing.JMenu jMenu15;
    private javax.swing.JMenu jMenu16;
    private javax.swing.JMenu jMenu17;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenu jMenu9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem24;
    private javax.swing.JMenuItem jMenuItem25;
    private javax.swing.JMenuItem jMenuItem26;
    private javax.swing.JMenuItem jMenuItem27;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem34;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem62;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

}
