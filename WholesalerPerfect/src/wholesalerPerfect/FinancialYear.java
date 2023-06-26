package wholesalerPerfect;

import conn.dBConnection;
import dto.UserProfile;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import query.Query;
import utilities.DateConverter;

public class FinancialYear extends javax.swing.JInternalFrame implements AWTEventListener {

    private UserProfile up;
    private String fyidArray[];
    private String currentFyid;
    private Query q=new Query();

    public FinancialYear(UserProfile up) {
        super("Financial Year",false,true,false,true);
        initComponents();
        this.up=up;
        Dimension d=getSize();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) dim.getWidth() - (int)d.getWidth())/2,((int) dim.getHeight() - (int)d.getHeight())/2-40);
	this.setResizable(false);
        this.setFrameIcon(new ImageIcon(getClass().getResource("/images/calendar_year.png")));
        
        this.getActionMap().put("test", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        InputMap map = this.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        map.put(stroke,"test");
        
        Fetch();

        jTabbedPane1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(!jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ALTER"))
                {
                    currentFyid=null;
                    Fetch();
                    jDateChooser3.setDate(null);
                    jDateChooser4.setDate(null);
                    jLabel9.setText("N/A");
                    jCheckBox2.setSelected(false);
                    jTextField6.setText("");
                }
            }
        });
        
        jDateChooser1.getToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
        jDateChooser2.getToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
        jDateChooser3.getToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
        jDateChooser4.getToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
        
        jTabbedPane1.setMnemonicAt(0, KeyEvent.VK_A);
        jTabbedPane1.setMnemonicAt(1, KeyEvent.VK_L);
        jTabbedPane1.setMnemonicAt(2, KeyEvent.VK_T);
        
        SwingUtilities.invokeLater
        (
            new Runnable() 
            {
                @Override
                public void run() 
                {
                    jDateChooser1.requestFocusInWindow();
                }
            }
        );
    }
    
    public void eventDispatched(AWTEvent event) {
        if(event instanceof KeyEvent){
            KeyEvent key = (KeyEvent)event;
            if(key.getID()==KeyEvent.KEY_PRESSED)
            {
                if(event.getSource().equals(jDateChooser1.getDateEditor())&&key.getKeyCode()==10)
                {
                    jDateChooser2.requestFocusInWindow();
                }
                if(event.getSource().equals(jDateChooser2.getDateEditor())&&key.getKeyCode()==10)
                {
                    jCheckBox1.requestFocusInWindow();
                }
                if(event.getSource().equals(jDateChooser3.getDateEditor())&&key.getKeyCode()==10)
                {
                    jDateChooser4.requestFocusInWindow();
                }
                if(event.getSource().equals(jDateChooser4.getDateEditor())&&key.getKeyCode()==10)
                {
                    jCheckBox2.requestFocusInWindow();
                }
            }
        }
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
        // NO OF COLUMNS: 6
        // SLN., STARTING DATE, ENDING DATE, FINANCIAL CODE, IS ACTIVE?, REMARKS
        int slno1=0;
        clearTable(jTable1);
        // Number of columns in FinancialYear: 6
        /* fyid, startingdt, endingdt, financialcode, isactive, remarks */
        String query="select fyid, startingdt, endingdt, financialcode, isactive, remarks from FinancialYear order by startingdt";
        System.out.println(query);
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        int total=0;
        try
        {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(query);
            //Move to the last record
            rs.afterLast();
            //Get the current record position
            if(rs.previous())
                total = rs.getRow();
            //Move back to the first record;
            rs.beforeFirst();
            if(total != 0)
            {
                fyidArray=new String[total];
                int i=0;
		while(rs.next())
		{
                    Vector<String> row = new Vector<String>();
                    row.addElement(++slno1+"");
                    fyidArray[i++]=rs.getString("fyid");
                    /* fyid, startingdt, endingdt, financialcode, isactive, remarks */
                    row.addElement(DateConverter.dateConverter(rs.getString("startingdt")));
                    row.addElement(DateConverter.dateConverter(rs.getString("endingdt")));
                    row.addElement(rs.getString("financialcode"));
                    String isactive=rs.getString("isactive");
                    row.addElement(isactive.equals("0")?"In-Active":"Active");
                    row.addElement(rs.getString("remarks").replace("\\'", "'"));
                    ((DefaultTableModel)jTable1.getModel()).addRow(row);
		}
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"AcademicYear ex1:"+ex.getMessage(),"Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally
        {
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
        // NO OF COLUMNS: 6
        // SLN., STARTING DATE, ENDING DATE, FINANCIAL CODE, IS ACTIVE?, REMARKS
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(40);
        jTable1.getColumnModel().getColumn(1).setMinWidth(0);// STARTING DATE
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);// ENDING DATE
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// FINANCIAL CODE
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);// IS ACTIVE?
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);// REMARKS
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(200);
    }

    private void generateAbbr(JLabel ref, String startingyear, String endingyear)
    {
        try
        {
            ref.setText(startingyear.substring(2)+"-"+endingyear.substring(2));
        }
        catch(StringIndexOutOfBoundsException ex){}
    }
    
    private void insertToDatabase()
    {
        // ADD Button
        SimpleDateFormat formatter=new SimpleDateFormat("dd/MM/yyyy");
        Date startingDt=jDateChooser1.getDate();
        String startingdt=null;
        try
        {
            startingdt=formatter.format(startingDt);
        }
        catch(NullPointerException ex)
        {
            JOptionPane.showMessageDialog(null,"Invalid Start Date !!!","Invalid Date",JOptionPane.ERROR_MESSAGE);
            jDateChooser1.setDate(null);
            return;
        }
        Date endingDt=jDateChooser2.getDate();
        String endingdt=null;
        try
        {
            endingdt=formatter.format(endingDt);
        }
        catch(NullPointerException ex)
        {
            JOptionPane.showMessageDialog(null,"Invalid Start Date !!!","Invalid Date",JOptionPane.ERROR_MESSAGE);
            jDateChooser2.setDate(null);
            return;
        }
        int istartingyear=Integer.parseInt(startingdt.substring(startingdt.lastIndexOf("/")+1));
        int iendingyear=Integer.parseInt(endingdt.substring(endingdt.lastIndexOf("/")+1));
        if(iendingyear-istartingyear!=1)
        {
            JOptionPane.showMessageDialog(null,"Invalid Year Range!","Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        String financialcode=jLabel4.getText().trim();
        if(financialcode.length()!=5)
        {
            JOptionPane.showMessageDialog(null,"Invalid Year Input!","Error Found",JOptionPane.ERROR_MESSAGE);
            jDateChooser1.requestFocusInWindow();
            return;
        }
        String isactive=jCheckBox1.isSelected()?"1":"0";
        String remarks=jTextField3.getText().trim().toUpperCase().replace("'", "\\'");
        boolean isActiveFlag=false;
        if(jCheckBox1.isSelected())
            isActiveFlag=true;

        // Duplicate Testing
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        String query="select * from FinancialYear where startingdt=#"+DateConverter.dateConverter1(startingdt)+"#";
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
            if(total != 0)
            {
                JOptionPane.showMessageDialog(null,"Duplicate Entry! Action Denied.","Error Found",JOptionPane.ERROR_MESSAGE);
                jDateChooser1.setDate(null);
                jDateChooser1.requestFocusInWindow();
                try {
                    if (conn!=null) conn.close();
                } catch(SQLException ex){}
                return;
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"FinancialYear ex?: "+ex.getMessage(),"SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException ex){}
        }
        // Whether More Than 1 Is Active
        if(isActiveFlag)
        {
            query="select * from FinancialYear where isactive=1";
            System.out.println(query);
            conn=db.setConnection();
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
                if(total != 0)
                {
                    JOptionPane.showMessageDialog(null,"More Than 1 Entry Can Not Be Active! Action Denied.","Error Found",JOptionPane.ERROR_MESSAGE);
                    jCheckBox1.requestFocusInWindow();
                    try {
                        if (conn!=null) conn.close();
                    } catch(SQLException ex){}
                    return;
                }
            }
            catch(SQLException ex)
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"FinancialYear ex?: "+ex.getMessage(),"SQL Error Found",JOptionPane.ERROR_MESSAGE);
                return;
            }
            finally
            {
                try {
                    if (conn!=null) conn.close();
                }
                catch(SQLException ex){}
            }
        }

        // Number of columns in FinancialYear: 6
        /* fyid, startingdt, endingdt, financialcode, isactive, remarks */
        int fyid=q.getMaxId("FinancialYear", "fyid");
        fyid++;
        query="insert into FinancialYear(fyid, startingdt, endingdt, financialcode, isactive, remarks) values("
                +fyid+",#"+DateConverter.dateConverter1(startingdt)+"#,#"+DateConverter.dateConverter1(endingdt)
                +"#,'"+financialcode+"',"+isactive+",'"+remarks+"')";
        System.out.println(query);
        conn=db.setConnection();
        try {
            Statement stm=conn.createStatement();
            stm.executeUpdate(query);
        } catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"FinalcialYear ex3:  "+ex.getMessage(),"SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException ex){}
        }
    
        jDateChooser1.setDate(null);
        jDateChooser2.setDate(null);
        jLabel4.setText("N/A");
        jCheckBox1.setSelected(true);
        jTextField3.setText("N/A");
        jDateChooser1.requestFocusInWindow();
        Fetch();
    }
    
    private void updateToDatabase()
    {
        // ALTER Button
        SimpleDateFormat formatter=new SimpleDateFormat("dd/MM/yyyy");
        Date startingDt=jDateChooser3.getDate();
        String startingdt=null;
        try
        {
            startingdt=formatter.format(startingDt);
        }
        catch(NullPointerException ex)
        {
            JOptionPane.showMessageDialog(null,"Invalid Start Date !!!","Invalid Date",JOptionPane.ERROR_MESSAGE);
            jDateChooser3.setDate(null);
            return;
        }
        Date endingDt=jDateChooser4.getDate();
        String endingdt=null;
        try
        {
            endingdt=formatter.format(endingDt);
        }
        catch(NullPointerException ex)
        {
            JOptionPane.showMessageDialog(null,"Invalid Start Date !!!","Invalid Date",JOptionPane.ERROR_MESSAGE);
            jDateChooser4.setDate(null);
            return;
        }
        int istartingyear=Integer.parseInt(startingdt.substring(startingdt.lastIndexOf("/")+1));
        int iendingyear=Integer.parseInt(endingdt.substring(endingdt.lastIndexOf("/")+1));
        if(iendingyear-istartingyear!=1)
        {
            JOptionPane.showMessageDialog(null,"Invalid Year Range!","Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        String financialcode=jLabel9.getText().trim();
        if(financialcode.length()!=5)
        {
            jDateChooser3.requestFocusInWindow();
            JOptionPane.showMessageDialog(null,"Invalid Year Input!","Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        String isactive=jCheckBox2.isSelected()?"1":"0";
        String remarks=jTextField6.getText().trim().toUpperCase().replace("'", "\\'");
        boolean isActiveFlag=false;
        if(jCheckBox2.isSelected())
            isActiveFlag=true;

        // Duplicate Testing
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        String query="select * from FinancialYear where startingdt=#"+DateConverter.dateConverter1(startingdt)+"# and fyid<>"+currentFyid;
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
            if(total != 0)
            {
                JOptionPane.showMessageDialog(null,"Duplicate Entry! Action Denied.","Error Found",JOptionPane.ERROR_MESSAGE);
                jDateChooser3.setDate(null);
                jDateChooser3.requestFocusInWindow();
                try {
                    if (conn!=null) conn.close();
                } catch(SQLException ex){}
                return;
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"FinancialYear ex?: "+ex.getMessage(),"SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException ex){}
        }
        // Whether More Than 1 Is Active
        if(isActiveFlag)
        {
            query="select * from FinancialYear where isactive=1 and fyid<>"+currentFyid;
            System.out.println(query);
            conn=db.setConnection();
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
                if(total != 0)
                {
                    JOptionPane.showMessageDialog(null,"More Than 1 Entry Can Not Be Active! Action Denied.","Error Found",JOptionPane.ERROR_MESSAGE);
                    jCheckBox2.requestFocusInWindow();
                    try {
                        if (conn!=null) conn.close();
                    } catch(SQLException ex){}
                    return;
                }
            }
            catch(SQLException ex)
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"FinalcialYear ex3:  "+ex.getMessage(),"SQL Error Found",JOptionPane.ERROR_MESSAGE);
                return;
            }
            finally {
                try {
                    if (conn!=null) conn.close();
                } catch(SQLException ex){}
            }
        }

        query="update FinancialYear set startingdt=#"+DateConverter.dateConverter1(startingdt)+"#,endingdt=#"
                +DateConverter.dateConverter1(endingdt)+"#,financialcode='"+financialcode+"',isactive="+isactive+",remarks='"
                +remarks+"' where fyid="+currentFyid;
        System.out.println(query);
        conn=db.setConnection();
        try {
            Statement stm=conn.createStatement();
            stm.executeUpdate(query);
        } catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"FinalcialYear ex3:  "+ex.getMessage(),"SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException ex){}
        }

        currentFyid=null;
        Fetch();
        jDateChooser3.setDate(null);
        jDateChooser4.setDate(null);
        jLabel9.setText("N/A");
        jCheckBox2.setSelected(false);
        jTextField6.setText("");
    }
    
    private void recDelete()
    {
        // DELETE
        if(jTable1.getSelectedRow()!=-1&&jTable1.getSelectedColumn()!=-1) {
            String fyid=fyidArray[jTable1.getSelectedRow()];
            String ObjButtons[] = {"Yes","Cancel"};
            int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure to Delete the Academic Year!","Delete Record",JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null,ObjButtons,ObjButtons[1]);
            if(PromptResult==0) {
                dBConnection db=new dBConnection();
                Connection conn=db.setConnection();
                String query="delete from FinancialYear where fyid="+fyid;
                System.out.println(query);
                try {
                    Statement smt=(Statement) conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
                    smt.executeUpdate(query);
                } catch(SQLException ex) {
                    JOptionPane.showMessageDialog(null,"AcademicYear ex5:  "+ex.getMessage(),"Error Found",JOptionPane.ERROR_MESSAGE);
                }
                finally {
                    try {
                        if (conn!=null) conn.close();
                    } catch(SQLException e){}
                    Fetch();
                }
            } else
                JOptionPane.showMessageDialog(null,"Action Discarded","Discard Information",JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,"Select a Record and then try to Delete","Error Found",JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jCheckBox2 = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jDateChooser3 = new com.toedter.calendar.JDateChooser();
        jDateChooser4 = new com.toedter.calendar.JDateChooser();
        jPanel3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();

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

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SLN.", "STARTING DATE", "ENDING DATE", "FINANCIAL CODE", "IS ACTIVE?", "REMARKS"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTable1MouseReleased(evt);
            }
        });
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTable1KeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("STARTING YEAR");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("ENDING YEAR");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("ABBREVIATION");

        jLabel4.setBackground(new java.awt.Color(255, 255, 204));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("N/A");
        jLabel4.setOpaque(true);

        jCheckBox1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jCheckBox1.setSelected(true);
        jCheckBox1.setText("IS ACTIVE?");
        jCheckBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCheckBox1KeyPressed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("REMARKS");

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

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton1.setText("SAVE");
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

        jDateChooser1.setDateFormatString("dd/MM/yyyy");
        jDateChooser1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateChooser1PropertyChange(evt);
            }
        });

        jDateChooser2.setDateFormatString("dd/MM/yyyy");
        jDateChooser2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateChooser2PropertyChange(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jCheckBox1)
                            .addComponent(jLabel5))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        jTabbedPane1.addTab("ADD", jPanel1);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("STARTING YEAR");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("ENDING YEAR");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("ABBREVIATION");

        jLabel9.setBackground(new java.awt.Color(255, 255, 204));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("N/A");
        jLabel9.setOpaque(true);

        jCheckBox2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jCheckBox2.setText("IS ACTIVE ?");
        jCheckBox2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCheckBox2KeyPressed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("REMARKS");

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
        });

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
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

        jDateChooser3.setDateFormatString("dd/MM/yyyy");
        jDateChooser3.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateChooser3PropertyChange(evt);
            }
        });

        jDateChooser4.setDateFormatString("dd/MM/yyyy");
        jDateChooser4.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateChooser4PropertyChange(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox2)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jDateChooser4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel10)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jDateChooser3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jDateChooser3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jDateChooser4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addContainerGap())
        );

        jTabbedPane1.addTab("ALTER", jPanel2);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 0, 0));
        jLabel11.setText("SELECT A RECORD, THEN DELETE ...");

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(122, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 199, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addContainerGap())
        );

        jTabbedPane1.addTab("DELETE", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        updateToDatabase();
      
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        recDelete();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void keyRelease()
    {
        // When a row is selected by mouuse
        if (jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ALTER"))
        {
            int row=jTable1.getSelectedRow();
            currentFyid=fyidArray[row];
            SimpleDateFormat formatter=new SimpleDateFormat("dd/MM/yyyy");
            try 
            {
                jDateChooser3.setDate(formatter.parse((String)jTable1.getModel().getValueAt(row, 1)));
                jDateChooser4.setDate(formatter.parse((String)jTable1.getModel().getValueAt(row, 2)));
            } 
            catch (ParseException ex) 
            {
                ex.printStackTrace();
                return;
            }
            jLabel9.setText((String)jTable1.getModel().getValueAt(row, 3));
            boolean checkBoxFlag=((String)jTable1.getModel().getValueAt(row, 4)).equals("Active");
            jCheckBox2.setSelected(checkBoxFlag);
            jTextField6.setText((String)jTable1.getModel().getValueAt(row, 5));
        }
    }
    private void jTextField3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField3FocusGained
        // REMARKS Focus Gained - ADD
        String s=jTextField3.getText().trim();
        if(s.equals("N/A")) {
            jTextField3.setText("");
        }
    }//GEN-LAST:event_jTextField3FocusGained

    private void jTextField3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField3FocusLost
        // REMARKS Focus Lost - ADD
        String s=jTextField3.getText().trim();
        if(s.length()==0) {
            jTextField3.setText("N/A");
        }
    }//GEN-LAST:event_jTextField3FocusLost

    private void jTextField6FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField6FocusLost
        // REMARKS Focus Lost - ALTER
        String s=jTextField6.getText().trim();
        if(s.length()==0) {
            jTextField6.setText("N/A");
        }
    }//GEN-LAST:event_jTextField6FocusLost

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // When <Enter> is Pressed on The SAVE BUTTON-ADD
        insertToDatabase();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTable1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseReleased
        // When a row is selected by MOUSE Release
        keyRelease();
    }//GEN-LAST:event_jTable1MouseReleased

    private void jCheckBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCheckBox1KeyPressed
        // When <Enter> is pressed on IS ACTIVE-ADD
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
           jTextField3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jCheckBox1KeyPressed

    private void jTextField3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyPressed
        // When <Enter> is pressed on REMARKS-ADD
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
           jButton1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField3KeyPressed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        // When <Enter> is pressed on SAVE-ADD
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
           insertToDatabase();
        }
    }//GEN-LAST:event_jButton1KeyPressed

    private void jCheckBox2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCheckBox2KeyPressed
        // When <Enter> is pressed on IS ACTIVE-ALTER
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
           jTextField6.requestFocusInWindow();
        }
    }//GEN-LAST:event_jCheckBox2KeyPressed

    private void jTextField6KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyPressed
       // When <Enter> is pressed on -ALTER
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
           jButton2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField6KeyPressed

    private void jButton2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton2KeyPressed
        // When <Enter> is pressed on UPDATE BUTTON -ALTER
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
           updateToDatabase();
        }
    }//GEN-LAST:event_jButton2KeyPressed

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        // When <Enter> is pressed while scrolling in table by key against Delete pannel
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
           if(jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("DELETE"))
           {
              jButton3.requestFocusInWindow();
              evt.consume();
           }
           if(jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ALTER"))
           {
              jDateChooser3.requestFocusInWindow();
              evt.consume();
           }
        }
    }//GEN-LAST:event_jTable1KeyPressed

    private void jTable1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyReleased
        // When a row is selected by KEY Released
        keyRelease();
    }//GEN-LAST:event_jTable1KeyReleased

    private void jButton3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton3KeyPressed
        // When <Enter> is pressed on DELETE BUTTON -ALTER
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
           recDelete();
        }
    }//GEN-LAST:event_jButton3KeyPressed

    private void jDateChooser1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDateChooser1PropertyChange
        SimpleDateFormat formatter=new SimpleDateFormat("dd/MM/yyyy");
        Date startingDt=jDateChooser1.getDate();
        String startingdt=null;
        try
        {
            startingdt=formatter.format(startingDt);
        }
        catch(NullPointerException ex)
        {
            return;
        }
        Date endingDt=jDateChooser2.getDate();
        String endingdt=null;
        try
        {
            endingdt=formatter.format(endingDt);
        }
        catch(NullPointerException ex)
        {
            return;
        }
        String startingyear=startingdt.substring(startingdt.lastIndexOf("/")+1);
        String endingyear=endingdt.substring(endingdt.lastIndexOf("/")+1);
        generateAbbr(jLabel4, startingyear, endingyear);
    }//GEN-LAST:event_jDateChooser1PropertyChange

    private void jDateChooser2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDateChooser2PropertyChange
        SimpleDateFormat formatter=new SimpleDateFormat("dd/MM/yyyy");
        Date startingDt=jDateChooser1.getDate();
        String startingdt=null;
        try
        {
            startingdt=formatter.format(startingDt);
        }
        catch(NullPointerException ex)
        {
            return;
        }
        Date endingDt=jDateChooser2.getDate();
        String endingdt=null;
        try
        {
            endingdt=formatter.format(endingDt);
        }
        catch(NullPointerException ex)
        {
            return;
        }
        String startingyear=startingdt.substring(startingdt.lastIndexOf("/")+1);
        String endingyear=endingdt.substring(endingdt.lastIndexOf("/")+1);
        generateAbbr(jLabel4, startingyear, endingyear);
    }//GEN-LAST:event_jDateChooser2PropertyChange

    private void jDateChooser3PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDateChooser3PropertyChange
        SimpleDateFormat formatter=new SimpleDateFormat("dd/MM/yyyy");
        Date startingDt=jDateChooser3.getDate();
        String startingdt=null;
        try
        {
            startingdt=formatter.format(startingDt);
        }
        catch(NullPointerException ex)
        {
            return;
        }
        Date endingDt=jDateChooser3.getDate();
        String endingdt=null;
        try
        {
            endingdt=formatter.format(endingDt);
        }
        catch(NullPointerException ex)
        {
            return;
        }
        String startingyear=startingdt.substring(startingdt.lastIndexOf("/")+1);
        String endingyear=endingdt.substring(endingdt.lastIndexOf("/")+1);
        generateAbbr(jLabel9, startingyear, endingyear);
    }//GEN-LAST:event_jDateChooser3PropertyChange

    private void jDateChooser4PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDateChooser4PropertyChange
        SimpleDateFormat formatter=new SimpleDateFormat("dd/MM/yyyy");
        Date startingDt=jDateChooser3.getDate();
        String startingdt=null;
        try
        {
            startingdt=formatter.format(startingDt);
        }
        catch(NullPointerException ex)
        {
            return;
        }
        Date endingDt=jDateChooser4.getDate();
        String endingdt=null;
        try
        {
            endingdt=formatter.format(endingDt);
        }
        catch(NullPointerException ex)
        {
            return;
        }
        String startingyear=startingdt.substring(startingdt.lastIndexOf("/")+1);
        String endingyear=endingdt.substring(endingdt.lastIndexOf("/")+1);
        generateAbbr(jLabel9, startingyear, endingyear);
    }//GEN-LAST:event_jDateChooser4PropertyChange

    private void formInternalFrameIconified(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameIconified
        moveToFront();
    }//GEN-LAST:event_formInternalFrameIconified

    private void jTextField6FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField6FocusGained
        String s=jTextField6.getText().trim();
        if(s.equals("N/A")) {
            jTextField6.setText("");
        }
    }//GEN-LAST:event_jTextField6FocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private com.toedter.calendar.JDateChooser jDateChooser3;
    private com.toedter.calendar.JDateChooser jDateChooser4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField6;
    // End of variables declaration//GEN-END:variables

}
