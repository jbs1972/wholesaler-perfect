package query;

import conn.dBConnection;
import dto.Enterprise;
import dto.PurchaseGSTV2;
import dto.PurchaseMaster;
import dto.PurchaseMasterV2;
import dto.PurchaseSub;
import dto.PurchaseSubV2;
import dto.Retailer;
import dto.SaleMaster;
import dto.SaleMasterV2;
import dto.SaleSub;
import dto.SaleSubV2;
import java.awt.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;
import utilities.DateConverter;

public class Query {
    
    public String passwordEncript(String pwd)
    {
        StringBuffer result=new StringBuffer("");
        for(int i=0;i<pwd.length();i++)
        {
            char ch=pwd.charAt(i);
            int ich=(int)ch;
            int alteredIch=ich+(i%2==0?(i+1):(i+1)*(-1));
            char alteredCh=(char)alteredIch;
            result.append(alteredCh);
        }
        return result.toString();
    }
    
    public String passwordDecript(String pwd)
    {
        StringBuffer result=new StringBuffer("");
        for(int i=0;i<pwd.length();i++)
        {
            char ch=pwd.charAt(i);
            int ich=(int)ch;
            int alteredIch=ich+(i%2==0?(i+1)*(-1):(i+1));
            char alteredCh=(char)alteredIch;
            result.append(alteredCh);
        }
        return result.toString();
    }

    public int getMaxId(String tableNm,String fieldNm)
    {
        int total=0;
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        String query="select max("+fieldNm+") as x from "+tableNm;
        System.out.println(query);
        try
        {
            Statement smt=conn.createStatement();
            ResultSet rs=smt.executeQuery(query);
            if(rs.next())
            {
                String stotal = rs.getString("x");
                if ( stotal != null )
                {
                    total = Integer.parseInt(stotal);
                }
            }
        }
        catch(NumberFormatException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query:getNextId ex1: "+ex.getMessage(),"Error Found",JOptionPane.ERROR_MESSAGE);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query:getNextId ex2: "+ex.getMessage(),"Error Found",JOptionPane.ERROR_MESSAGE);
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException ex){}
        }
        return total;
    }

    public Enterprise getEnterprise()
    {
        // Number of columns in Enterprise: 16
        /* ename, estreet, ecity, edist, estate, estatecode, epin, ecountry, 
        eabbr, econtact, email, efax, egstno, egstregntype epanno, eaadhaarno */
        Enterprise result=null;
        String query="select ename, estreet, ecity, edist, estate, estatecode, "
                + "epin, ecountry, eabbr, econtact, email, efax, egstno, "
                + "egstregntype epanno, eaadhaarno from Enterprise";
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        try
        {
            Statement stm=conn.createStatement();
            ResultSet rs=stm.executeQuery(query);
            if(rs.next())
            {
                result=new Enterprise();
                result.setEname(rs.getString("ename"));
                result.setEstreet(rs.getString("estreet"));
                result.setEcity(rs.getString("ecity"));
                result.setEdist(rs.getString("edist"));
                result.setEstate(rs.getString("estate"));
                result.setEstatecode(rs.getString("estatecode"));
                result.setEpin(rs.getString("epin"));
                result.setEcountry(rs.getString("ecountry"));
                result.setEabbr(rs.getString("eabbr"));
                result.setEcontact(rs.getString("econtact"));
                result.setEmail(rs.getString("email"));
                result.setEfax(rs.getString("efax"));
                result.setEgstno(rs.getString("egstno"));
                result.setEgstregntype(rs.getString("egstregntype"));
                result.setEpanno(rs.getString("epanno"));
                result.setEaadhaarno(rs.getString("eaadhaarno"));
            }
        }
        catch(SQLException ex)// ex1
        {
            JOptionPane.showMessageDialog(null,"Query:getEnterprise ex1: "+ex.getMessage(),"Error Found",JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException ex){}
        }
        return result;
    }
    
    public Date parseDate(String date, String format)
    {
        Date dt=null;
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            dt=formatter.parse(date);
        }
        catch(ParseException ex) {}
        return dt;
    }
    
    public Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
    
    public boolean isNumeric(String str)
    {  
        try  
        {  
            double d = Double.parseDouble(str);  
        }  
        catch(NumberFormatException nfe)  
        {  
            return false;  
        }  
        return true;  
    }
    
    public int insertToPurchaseMaster(PurchaseMaster pm)
    {
        dBConnection db=null;
        Connection conn = null;
        
        PreparedStatement preparedStatementInsert1 = null;
        PreparedStatement preparedStatementInsert2 = null;
        
        int pmid = getMaxId("PurchaseMaster", "pmid");
        int psid = getMaxId("PurchaseSub", "psid");

        // Number of columns in PurchaseMaster: 22
        /* pmid, ssid, compid, invno, invdt, netqty, netgross, netitemdisc, nettaxable, tradediscper, 
        nettradediscamt, replacementdiscper, netreplacementdiscamt, netgstamt, grosspayableamt, roundoff, netpayableamt, 
        dispscheme, finalamt, isopening, amtpaid, isactive */
        String insertTableSQL1 = "insert into PurchaseMaster (pmid, ssid, compid, invno, invdt, netqty, "
                + "netgross, netitemdisc, nettaxable, tradediscper, nettradediscamt, replacementdiscper, "
                + "netreplacementdiscamt, netgstamt, grosspayableamt, roundoff, netpayableamt, dispscheme, "
                + "finalamt, isopening, amtpaid, isactive) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        // Number of columns in PurchaseSub: 16
        /* psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, 
        taxableamt, gstamt, qtysold, retqty, tradediscamt, replacementdiscamt */
        String insertTableSQL2 = "insert into PurchaseSub ( psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, "
                + "gross, discamt, taxableamt, gstamt, qtysold, retqty, tradediscamt, replacementdiscamt) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try 
        {
            db=new dBConnection();
            conn=db.setConnection();

            conn.setAutoCommit(false);
            
            // Number of columns in PurchaseMaster: 22
            /* pmid, ssid, compid, invno, invdt, netqty, netgross, netitemdisc, nettaxable, tradediscper, 
            nettradediscamt, replacementdiscper, netreplacementdiscamt, netgstamt, grosspayableamt, roundoff, netpayableamt, 
            dispscheme, finalamt, isopening, amtpaid, isactive */
            preparedStatementInsert1 = conn.prepareStatement(insertTableSQL1);
            preparedStatementInsert1.setInt(1, ++pmid);
            preparedStatementInsert1.setInt(2, Integer.parseInt(pm.getSsid()));
            preparedStatementInsert1.setInt(3, Integer.parseInt(pm.getCompid()));
            preparedStatementInsert1.setString(4, pm.getInvno());
            preparedStatementInsert1.setDate(5, java.sql.Date.valueOf(DateConverter.dateConverter1(pm.getInvdt())));
            preparedStatementInsert1.setInt(6, Integer.parseInt(pm.getNetqty()));
            preparedStatementInsert1.setDouble(7, Double.parseDouble(pm.getNetgross()));
            preparedStatementInsert1.setDouble(8, Double.parseDouble(pm.getNetitemdisc()));
            preparedStatementInsert1.setDouble(9, Double.parseDouble(pm.getNettaxable()));
            preparedStatementInsert1.setDouble(10, Double.parseDouble(pm.getTradediscper()));
            preparedStatementInsert1.setDouble(11, Double.parseDouble(pm.getNettradediscamt()));
            preparedStatementInsert1.setDouble(12, Double.parseDouble(pm.getReplacementdiscper()));
            preparedStatementInsert1.setDouble(13, Double.parseDouble(pm.getNetreplacementdiscamt()));
            preparedStatementInsert1.setDouble(14, Double.parseDouble(pm.getNetgstamt()));
            preparedStatementInsert1.setDouble(15, Double.parseDouble(pm.getGrosspayableamt()));
            preparedStatementInsert1.setDouble(16, Double.parseDouble(pm.getRoundoff()));
            preparedStatementInsert1.setDouble(17, Double.parseDouble(pm.getNetpayableamt()));
            preparedStatementInsert1.setDouble(18, Double.parseDouble(pm.getDispscheme()));
            preparedStatementInsert1.setDouble(19, Double.parseDouble(pm.getFinalamt()));
            preparedStatementInsert1.setInt(20, Integer.parseInt(pm.getIsopening()));
            preparedStatementInsert1.setDouble(21, Double.parseDouble(pm.getAmtpaid()));
            preparedStatementInsert1.setInt(22, Integer.parseInt(pm.getIsactive()));
            preparedStatementInsert1.executeUpdate();
            
            preparedStatementInsert2 = conn.prepareStatement(insertTableSQL2);
            for(PurchaseSub ps : pm.getPsAl())
            {
                // Number of columns in PurchaseSub: 16
                /* psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, 
                taxableamt, gstamt, qtysold, retqty, tradediscamt, replacementdiscamt */
                preparedStatementInsert2.setInt(1, ++psid);
                preparedStatementInsert2.setInt(2, pmid);
                preparedStatementInsert2.setInt(3, Integer.parseInt(ps.getItemdid()));
                preparedStatementInsert2.setDouble(4, Double.parseDouble(ps.getGst()));
                preparedStatementInsert2.setDouble(5, Double.parseDouble(ps.getExgst()));
                preparedStatementInsert2.setDouble(6, Double.parseDouble(ps.getIngst()));
                preparedStatementInsert2.setDouble(7, Double.parseDouble(ps.getMrp()));
                preparedStatementInsert2.setInt(8, Integer.parseInt(ps.getQty()));
                preparedStatementInsert2.setDouble(9, Double.parseDouble(ps.getGross()));
                preparedStatementInsert2.setDouble(10, Double.parseDouble(ps.getDiscamt()));
                preparedStatementInsert2.setDouble(11, Double.parseDouble(ps.getTaxableamt()));
                preparedStatementInsert2.setDouble(12, Double.parseDouble(ps.getGstamt()));
                preparedStatementInsert2.setInt(13, Integer.parseInt(ps.getQtysold()));
                preparedStatementInsert2.setInt(14, Integer.parseInt(ps.getRetqty()));
                preparedStatementInsert2.setDouble(15, Double.parseDouble(ps.getTradediscamt()));
                preparedStatementInsert2.setDouble(16, Double.parseDouble(ps.getReplacementdiscamt()));
                preparedStatementInsert2.addBatch(); 
            }
            preparedStatementInsert2.executeBatch();

            conn.commit();
        } 
        catch (SQLException ex) 
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: insertToPurchaseMaster ex: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            try {
                conn.rollback();
                return 0;
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        } 
        finally 
        {
            if (preparedStatementInsert1 != null) 
            {
                try {
                    preparedStatementInsert1.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (preparedStatementInsert2 != null) 
            {
                try {
                    preparedStatementInsert2.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) 
            {
                db=null;
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return pmid;
    }
    
    public int insertToPurchaseMasterV2(PurchaseMasterV2 pm) {
        dBConnection db=null;
        Connection conn = null;
        
        PreparedStatement preparedStatementInsert1 = null;
        PreparedStatement preparedStatementInsert2 = null;
        PreparedStatement preparedStatementInsert3 = null;
        
        int pmid = getMaxId("PurchaseMasterV2", "pmid");
        int psid = getMaxId("PurchaseSubV2", "psid");
        int pgstid = getMaxId("PurchaseGSTV2", "pgstid");
        System.out.println("==================>pgstid:: "+pgstid);

        // Table PurchaseMasterV2, no. of columns - 20
        /*
        pmid, ssid, compid, invno, invdt, netqty, netitemdisc, netgross, tradediscper, nettradediscamt, 
        replacementdiscper, netreplacementdiscamt, netgstamt, amtaftergst, roundoff, dispscheme, 
        netpayableamt, isopening, amtpaid, isactive
        */
        String insertTableSQL1 = "insert into PurchaseMasterV2 (pmid, ssid, compid, invno, invdt, netqty, "
                + "netitemdisc, netgross, tradediscper, nettradediscamt, replacementdiscper, "
                + "netreplacementdiscamt, netgstamt, amtaftergst, roundoff, dispscheme,  netpayableamt, "
                + "isopening, amtpaid, isactive) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                + "?, ?, ?, ?, ?, ?, ?, ?)";
        
        // Table PurchaseSubV2, no. of columns - 12
        /*
        psid, pmid, itemdid, mrp, gst, qty, rate, discper, discamt, amount, qtysold, retqty
        */
        String insertTableSQL2 = "insert into PurchaseSubV2 (psid, pmid, itemdid, mrp, gst, qty, rate, "
                + "discper, discamt, amount, qtysold, retqty)  values "
                + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        // Table PurchaseGSTV2, no. of columns - 5
        /*
        pgstid, pmid, gstper, taxableamt, gstamt
        */
        String insertTableSQL3 = "insert into PurchaseGSTV2 (pgstid, pmid, gstper, taxableamt, gstamt) "
                + "values (?, ?, ?, ?, ?)";

        try {
            db=new dBConnection();
            conn=db.setConnection();

            conn.setAutoCommit(false);
            
            // Table PurchaseMasterV2, no. of columns - 20
            /*
            pmid, ssid, compid, invno, invdt, netqty, netitemdisc, netgross, tradediscper, nettradediscamt, 
            replacementdiscper, netreplacementdiscamt, netgstamt, amtaftergst, roundoff, dispscheme, 
            netpayableamt, isopening, amtpaid, isactive
            */
            preparedStatementInsert1 = conn.prepareStatement(insertTableSQL1);
            preparedStatementInsert1.setInt(1, ++pmid);
            preparedStatementInsert1.setInt(2, Integer.parseInt(pm.getSsid()));
            preparedStatementInsert1.setInt(3, Integer.parseInt(pm.getCompid()));
            preparedStatementInsert1.setString(4, pm.getInvno());
            preparedStatementInsert1.setDate(5, java.sql.Date.valueOf(DateConverter.dateConverter1(pm.getInvdt())));
            preparedStatementInsert1.setInt(6, Integer.parseInt(pm.getNetqty()));
            preparedStatementInsert1.setDouble(7, Double.parseDouble(pm.getNetitemdisc()));
            preparedStatementInsert1.setDouble(8, Double.parseDouble(pm.getNetgross()));
            preparedStatementInsert1.setDouble(9, Double.parseDouble(pm.getTradediscper()));
            preparedStatementInsert1.setDouble(10, Double.parseDouble(pm.getNettradediscamt()));
            preparedStatementInsert1.setDouble(11, Double.parseDouble(pm.getReplacementdiscper()));
            preparedStatementInsert1.setDouble(12, Double.parseDouble(pm.getNetreplacementdiscamt()));
            preparedStatementInsert1.setDouble(13, Double.parseDouble(pm.getNetgstamt()));
            preparedStatementInsert1.setDouble(14, Double.parseDouble(pm.getAmtaftergst()));
            preparedStatementInsert1.setDouble(15, Double.parseDouble(pm.getRoundoff()));
            preparedStatementInsert1.setDouble(16, Double.parseDouble(pm.getDispscheme()));
            preparedStatementInsert1.setDouble(17, Double.parseDouble(pm.getNetpayableamt()));
            preparedStatementInsert1.setInt(18, Integer.parseInt(pm.getIsopening()));
            preparedStatementInsert1.setDouble(19, Double.parseDouble(pm.getAmtpaid()));
            preparedStatementInsert1.setInt(20, Integer.parseInt(pm.getIsactive()));
            preparedStatementInsert1.executeUpdate();
            
            preparedStatementInsert2 = conn.prepareStatement(insertTableSQL2);
            for(PurchaseSubV2 ps : pm.getPsAl()) {
                // Table PurchaseSubV2, no. of columns - 12
                /*
                psid, pmid, itemdid, mrp, gst, qty, rate, discper, discamt, amount, qtysold, retqty
                */
                preparedStatementInsert2.setInt(1, ++psid);
                preparedStatementInsert2.setInt(2, pmid);
                preparedStatementInsert2.setInt(3, Integer.parseInt(ps.getItemdid()));
                preparedStatementInsert2.setDouble(4, Double.parseDouble(ps.getMrp()));
                preparedStatementInsert2.setDouble(5, Double.parseDouble(ps.getGst()));
                preparedStatementInsert2.setInt(6, Integer.parseInt(ps.getQty()));
                preparedStatementInsert2.setDouble(7, Double.parseDouble(ps.getRate()));
                preparedStatementInsert2.setDouble(8, Double.parseDouble(ps.getDiscper()));
                preparedStatementInsert2.setDouble(9, Double.parseDouble(ps.getDiscamt()));
                preparedStatementInsert2.setDouble(10, Double.parseDouble(ps.getAmount()));
                preparedStatementInsert2.setInt(11, Integer.parseInt(ps.getQtysold()));
                preparedStatementInsert2.setInt(12, Integer.parseInt(ps.getRetqty()));
                preparedStatementInsert2.addBatch(); 
            }
            preparedStatementInsert2.executeBatch();
            
            preparedStatementInsert3 = conn.prepareStatement(insertTableSQL3);
            for(PurchaseGSTV2 pgst : pm.getPgstAl()) {
                // Table PurchaseGSTV2, no. of columns - 5
                /*
                pgstid, pmid, gstper, taxableamt, gstamt
                */
                preparedStatementInsert3.setInt(1, ++pgstid);
                preparedStatementInsert3.setInt(2, pmid);
                preparedStatementInsert3.setDouble(3, Double.parseDouble(pgst.getGstper()));
                preparedStatementInsert3.setDouble(4, Double.parseDouble(pgst.getTaxableamt()));
                preparedStatementInsert3.setDouble(5, Double.parseDouble(pgst.getGstamt()));
                preparedStatementInsert3.addBatch(); 
            }
            preparedStatementInsert3.executeBatch();

            conn.commit();
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: insertToPurchaseMasterV2 ex: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            try {
                conn.rollback();
                return 0;
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        } 
        finally {
            if (preparedStatementInsert1 != null) {
                try {
                    preparedStatementInsert1.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (preparedStatementInsert2 != null) {
                try {
                    preparedStatementInsert2.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (preparedStatementInsert3 != null) {
                try {
                    preparedStatementInsert3.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                db=null;
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return pmid;
    }
    
    public boolean deleteFromPurchaseMaster(PurchaseMaster pm) {
        dBConnection db = new dBConnection();;
        Connection conn = db.setConnection();
        
        PreparedStatement psmt1 = null;
        PreparedStatement psmt2 = null;
        PreparedStatement psmt3 = null;
        PreparedStatement psmt4 = null;
        PreparedStatement psmt5 = null;

        // Number of columns in PurchaseMaster: 22
        /* pmid, ssid, compid, invno, invdt, netqty, netgross, netitemdisc, nettaxable, tradediscper, 
        nettradediscamt, replacementdiscper, netreplacementdiscamt, netgstamt, grosspayableamt, roundoff, netpayableamt, 
        dispscheme, finalamt, isopening, amtpaid, isactive */
        // Number of columns in PurchaseSub: 16
        /* psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, 
        taxableamt, gstamt, qtysold, retqty, tradediscamt, replacementdiscamt */
        String deleteSQL1 = "DELETE FROM PurchaseSub WHERE pmid=?";
        String deleteSQL2 = "DELETE FROM PurchaseMaster WHERE pmid=?";
        // Number of columns in ItemDetails: 10
        /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
        String updaetSQL1 = "UPDATE ItemDetails SET onhand=onhand-? WHERE itemdid=?";
        // Number of columns in ItemLedger: 9
        /* ilid, itemdid, tablenm, pknm, pkval, actiondt, type, prevqty, qty */
        String deleteSQL3 = "DELETE FROM ItemLedger WHERE itemdid=? AND tablenm='PurchaseSub'"
                + " AND pknm='psid' AND pkval=? AND actiondt=? AND type='ADD'";        
        // Number of columns in PurchasePaymentRegister: 7
        /* pprid, pknm, pkval, actiondt, refno, type, amount */
        String deleteSQL4 = "DELETE FROM PurchasePaymentRegister WHERE pknm='pmid' AND pkval=?"
                + " AND actiondt=? AND type=0";
        try {
            conn.setAutoCommit(false);
            
            psmt1 = conn.prepareStatement(updaetSQL1);
            psmt2 = conn.prepareStatement(deleteSQL3);
            psmt3 = conn.prepareStatement(deleteSQL1);
            psmt4 = conn.prepareStatement(deleteSQL2);
            psmt5 = conn.prepareStatement(deleteSQL4);
            for(PurchaseSub ref : pm.getPsAl()) {
                psmt1.setInt(1, Integer.parseInt(ref.getQty()));
                psmt1.setInt(2, Integer.parseInt(ref.getItemdid()));
                psmt1.addBatch();
                
                psmt2.setInt(1, Integer.parseInt(ref.getItemdid()));
                System.out.println("========itemdid===========>"+ref.getItemdid());
                psmt2.setString(2, ref.getPsid());
                System.out.println("========psid===========>"+ref.getPsid());
                psmt2.setDate(3, java.sql.Date.valueOf(DateConverter.dateConverter1(pm.getInvdt())));
                System.out.println("========invdt===========>"+pm.getInvdt());
                System.out.println("========modified invdt===========>"+DateConverter.dateConverter1(pm.getInvdt()));
                psmt2.addBatch();
            }
            psmt1.executeBatch();
            psmt2.executeBatch();
            
            psmt3.setInt(1, Integer.parseInt(pm.getPmid()));
            psmt3.executeUpdate();
            psmt4.setInt(1, Integer.parseInt(pm.getPmid()));
            psmt4.executeUpdate();
            psmt5.setString(1, pm.getPmid());
            psmt5.setDate(2, java.sql.Date.valueOf(DateConverter.dateConverter1(pm.getInvdt())));
            psmt5.executeUpdate();
            
            conn.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: deleteFromPurchaseMaster ex: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            try {
                conn.rollback();
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
            return false;
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
            if (psmt5 != null) {
                try {
                    psmt5.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                db=null;
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return true;
    }
    
    public boolean deleteFromPurchaseMasterV2(PurchaseMasterV2 pm) {
        dBConnection db = new dBConnection();
        Connection conn = db.setConnection();
        
        PreparedStatement psmt1 = null;
        PreparedStatement psmt2 = null;
        PreparedStatement psmt3 = null;
        PreparedStatement psmt4 = null;
        PreparedStatement psmt5 = null;
        PreparedStatement psmt6 = null;

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
        String deleteSQL1 = "DELETE FROM PurchaseSubV2 WHERE pmid=?";
        String deleteSQL2 = "DELETE FROM PurchaseMasterV2 WHERE pmid=?";
        // Number of columns in ItemDetails: 10
        /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
        String updaetSQL1 = "UPDATE ItemDetails SET onhand=onhand-? WHERE itemdid=?";
        // Number of columns in ItemLedger: 9
        /* ilid, itemdid, tablenm, pknm, pkval, actiondt, type, prevqty, qty */
        String deleteSQL3 = "DELETE FROM ItemLedger WHERE itemdid=? AND tablenm='PurchaseSub'"
                + " AND pknm='psid' AND pkval=? AND actiondt=? AND type='LESS'";        
        // Number of columns in PurchasePaymentRegister: 7
        /* pprid, pknm, pkval, actiondt, refno, type, amount */
        String deleteSQL4 = "DELETE FROM PurchasePaymentRegister WHERE pknm='pmid' AND pkval=?"
                + " AND actiondt=? AND type=0";
        // Table PurchaseGSTV2, no. of columns - 5
        /*
        pgstid, pmid, gstper, taxableamt, gstamt
        */
        String deleteSQL5 = "DELETE FROM PurchaseGSTV2 WHERE pmid=?";
        try {
            conn.setAutoCommit(false);
            
            // psmt6 = conn.prepareStatement(deleteSQL5);
            psmt1 = conn.prepareStatement(updaetSQL1);
            psmt2 = conn.prepareStatement(deleteSQL3);
            psmt3 = conn.prepareStatement(deleteSQL1);
            psmt4 = conn.prepareStatement(deleteSQL2);
            psmt5 = conn.prepareStatement(deleteSQL4);
            psmt6 = conn.prepareStatement(deleteSQL5);
            
            for(PurchaseSubV2 ref : pm.getPsAl()) {
                psmt1.setInt(1, Integer.parseInt(ref.getQty()));
                psmt1.setInt(2, Integer.parseInt(ref.getItemdid()));
                psmt1.addBatch();
                
                psmt2.setInt(1, Integer.parseInt(ref.getItemdid()));
                System.out.println("========itemdid===========>"+ref.getItemdid());
                psmt2.setString(2, ref.getPsid());
                System.out.println("========psid===========>"+ref.getPsid());
                psmt2.setDate(3, java.sql.Date.valueOf(DateConverter.dateConverter1(pm.getInvdt())));
                System.out.println("========invdt===========>"+pm.getInvdt());
                System.out.println("========modified invdt===========>"+DateConverter.dateConverter1(pm.getInvdt()));
                psmt2.addBatch();
            }
            psmt1.executeBatch();
            psmt2.executeBatch();
            
            psmt3.setInt(1, Integer.parseInt(pm.getPmid()));
            psmt3.executeUpdate();
            psmt4.setInt(1, Integer.parseInt(pm.getPmid()));
            psmt4.executeUpdate();
            psmt5.setString(1, pm.getPmid());
            psmt5.setDate(2, java.sql.Date.valueOf(DateConverter.dateConverter1(pm.getInvdt())));
            psmt5.executeUpdate();
            psmt6.setInt(1, Integer.parseInt(pm.getPmid()));
            psmt6.executeUpdate();
            
            conn.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: deleteFromPurchaseMasterV2 ex: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            try {
                conn.rollback();
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
            return false;
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
            if (psmt5 != null) {
                try {
                    psmt5.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (psmt6 != null) {
                try {
                    psmt6.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                db=null;
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return true;
    }
    
    public boolean deleteFromSaleMasterV2(SaleMasterV2 sm) {
        dBConnection db = new dBConnection();
        Connection conn = db.setConnection();
        
        PreparedStatement psmt1 = null;
        PreparedStatement psmt2 = null;
        PreparedStatement psmt3 = null;
        PreparedStatement psmt4 = null;
        PreparedStatement psmt5 = null;
        PreparedStatement psmt6 = null;

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
        String deleteSQL1 = "DELETE FROM SaleSubV2 WHERE salemid=?";
        String deleteSQL2 = "DELETE FROM SaleMasterV2 WHERE salemid=?";
        // Number of columns in ItemDetails: 10
        /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
        String updaetSQL1 = "UPDATE ItemDetails SET onhand=onhand+? WHERE itemdid=?";
        // Number of columns in ItemLedger: 9
        /* ilid, itemdid, tablenm, pknm, pkval, actiondt, type, prevqty, qty */
        String deleteSQL3 = "DELETE FROM ItemLedger WHERE itemdid=? AND tablenm='SaleSubV2'"
                + " AND pknm='salesid' AND pkval=? AND actiondt=? AND type='ADD'";        
        // Number of columns in SalePaymentRegister: 7
        /* sprid, pknm, pkval, actiondt, refno, type, amount */
        String deleteSQL4 = "DELETE FROM SalePaymentRegister WHERE pknm='salemid' AND pkval=?"
                + " AND actiondt=? AND type=0";
        // Table PurchaseSubV2, no. of columns - 12
        /*
        psid, pmid, itemdid, mrp, gst, qty, rate, discper, discamt, amount, qtysold, retqty
        */
        String updaetSQL2 = "update PurchaseSubV2 set qtysold=qtysold-? where psid=?";
        try {
            conn.setAutoCommit(false);
            
            // psmt6 = conn.prepareStatement(deleteSQL5);
            psmt1 = conn.prepareStatement(updaetSQL1);
            psmt2 = conn.prepareStatement(deleteSQL3);
            psmt3 = conn.prepareStatement(deleteSQL1);
            psmt4 = conn.prepareStatement(deleteSQL2);
            psmt5 = conn.prepareStatement(deleteSQL4);
            psmt6 = conn.prepareStatement(updaetSQL2);
            
            for(SaleSubV2 ref : sm.getSsAl()) {
                psmt1.setInt(1, Integer.parseInt(ref.getQty()));
                psmt1.setInt(2, Integer.parseInt(ref.getItemdid()));
                psmt1.addBatch();
                
                psmt2.setInt(1, Integer.parseInt(ref.getItemdid()));
                System.out.println("========itemdid===========>"+ref.getItemdid());
                psmt2.setString(2, ref.getPsid());
                System.out.println("========psid===========>"+ref.getSalesid());
                psmt2.setDate(3, java.sql.Date.valueOf(DateConverter.dateConverter1(sm.getSaledt())));
                System.out.println("========invdt===========>"+sm.getSaledt());
                System.out.println("========modified invdt===========>"+DateConverter.dateConverter1(sm.getSaledt()));
                psmt2.addBatch();
                
                psmt6.setInt(1, Integer.parseInt(ref.getQty()));
                psmt6.setInt(2, Integer.parseInt(ref.getItemdid()));
                psmt6.addBatch();
            }
            psmt1.executeBatch();
            psmt2.executeBatch();
            psmt6.executeBatch();
            
            psmt3.setString(1, sm.getSalemid());
            psmt3.executeUpdate();
            psmt4.setString(1, sm.getSalemid());
            psmt4.executeUpdate();
            psmt5.setString(1, sm.getSalemid());
            psmt5.setDate(2, java.sql.Date.valueOf(DateConverter.dateConverter1(sm.getSaledt())));
            psmt5.executeUpdate();
            
            conn.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: deleteFromSaleMasterV2 ex: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            try {
                conn.rollback();
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
            return false;
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
            if (psmt5 != null) {
                try {
                    psmt5.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (psmt6 != null) {
                try {
                    psmt6.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                db=null;
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return true;
    }
    
    public PurchaseMaster getPurchaseMaster( String pmid )
    {
        PurchaseMaster pm=new PurchaseMaster();
        // Number of columns in PurchaseMaster: 22
        /* pmid, ssid, compid, invno, invdt, netqty, netgross, netitemdisc, nettaxable, tradediscper, 
        nettradediscamt, replacementdiscper, netreplacementdiscamt, netgstamt, grosspayableamt, roundoff, netpayableamt, 
        dispscheme, finalamt, isopening, amtpaid, isactive */
        String query="select pmid, ssid, compid, invno, invdt, netqty, netgross, netitemdisc, nettaxable, "
                + "tradediscper, nettradediscamt, replacementdiscper, netreplacementdiscamt, netgstamt, "
                + "grosspayableamt, roundoff, netpayableamt, dispscheme, finalamt, "
                + "isopening, amtpaid, isactive from PurchaseMaster where pmid="+pmid; 
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        try
        {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(query);
            while(rs.next())
            {
                // Number of columns in PurchaseMaster: 22
                /* pmid, ssid, compid, invno, invdt, netqty, netgross, netitemdisc, nettaxable, tradediscper, 
                nettradediscamt, replacementdiscper, netreplacementdiscamt, netgstamt, grosspayableamt, roundoff, netpayableamt, 
                dispscheme, finalamt, isopening, amtpaid, isactive */
                pm.setPmid(pmid);
                pm.setSsid(rs.getString("ssid"));
                pm.setCompid(rs.getString("compid"));
                pm.setInvno(rs.getString("invno"));
                pm.setInvdt(DateConverter.dateConverter(rs.getString("invdt")));
                pm.setNetqty(rs.getString("netqty"));
                pm.setNetgross(rs.getString("netgross"));
                pm.setNetitemdisc(rs.getString("netitemdisc"));
                pm.setNettaxable(rs.getString("nettaxable"));
                pm.setTradediscper(rs.getString("tradediscper"));
                pm.setNettradediscamt(rs.getString("nettradediscamt"));
                pm.setReplacementdiscper(rs.getString("replacementdiscper"));
                pm.setNetreplacementdiscamt(rs.getString("netreplacementdiscamt"));
                pm.setNetgstamt(rs.getString("netgstamt"));
                pm.setGrosspayableamt(rs.getString("grosspayableamt"));
                pm.setRoundoff(rs.getString("roundoff"));
                pm.setNetpayableamt(rs.getString("netpayableamt"));
                pm.setDispscheme(rs.getString("dispscheme"));
                pm.setFinalamt(rs.getString("finalamt"));
                pm.setIsopening(rs.getString("isopening"));
                pm.setAmtpaid(rs.getString("amtpaid"));
                pm.setIsactive(rs.getString("isactive"));
                
                // Getting Formula Phase
                ArrayList<PurchaseSub> psAl=new ArrayList<PurchaseSub>();
                // Number of columns in PurchaseSub: 16
                /* psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, 
                taxableamt, gstamt, qtysold, retqty, tradediscamt, replacementdiscamt */
                query="select psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, taxableamt, "
                        + "gstamt, qtysold, retqty, tradediscamt, replacementdiscamt from PurchaseSub where pmid="+pmid; 
                try
                {
                    Statement smt1=conn.createStatement();
                    ResultSet rs1=smt1.executeQuery(query);
                    while(rs1.next())
                    {    
                        PurchaseSub ps = new PurchaseSub();
                        // Number of columns in PurchaseSub: 16
                        /* psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, 
                        taxableamt, gstamt, qtysold, retqty, tradediscamt, replacementdiscamt */
                        ps.setPsid(rs1.getString("psid"));
                        ps.setPmid(rs1.getString("pmid"));
                        ps.setItemdid(rs1.getString("itemdid"));
                        ps.setGst(rs1.getString("gst"));
                        ps.setExgst(rs1.getString("exgst"));
                        ps.setIngst(rs1.getString("ingst"));
                        ps.setMrp(rs1.getString("mrp"));
                        ps.setQty(rs1.getString("qty"));
                        ps.setGross(rs1.getString("gross"));
                        ps.setDiscamt(rs1.getString("discamt"));
                        ps.setTaxableamt(rs1.getString("taxableamt"));
                        ps.setGstamt(rs1.getString("gstamt"));
                        ps.setQtysold(rs1.getString("qtysold"));
                        ps.setRetqty(rs1.getString("retqty"));
                        ps.setTradediscamt(rs1.getString("tradediscamt"));
                        ps.setReplacementdiscamt(rs1.getString("replacementdiscamt"));

                        psAl.add(ps);
                    }
                }
                catch(SQLException ex)
                {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,"Query: getPurchaseMaster ex: "+ex.getMessage(),
                            "SQL Error Found",JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                
                pm.setPsAl(psAl);
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: getPurchaseMaster ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e){}
        }
        return pm;
    }
    
    public PurchaseSub getPurchaseSub( String psid )
    {
        PurchaseSub ps = new PurchaseSub();
        
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        
        try {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            // Number of columns in PurchaseSub: 16
            /* psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, 
            taxableamt, gstamt, qtysold, retqty, tradediscamt, replacementdiscamt */
            String query="select psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, taxableamt, "
                    + "gstamt, qtysold, retqty, tradediscamt, replacementdiscamt from PurchaseSub where psid="+psid; 
            ResultSet rs=smt.executeQuery(query);
            
            if (rs.next()) {
                ps.setPsid(rs.getString("psid"));
                ps.setPmid(rs.getString("pmid"));
                ps.setItemdid(rs.getString("itemdid"));
                ps.setGst(rs.getString("gst"));
                ps.setExgst(rs.getString("exgst"));
                ps.setIngst(rs.getString("ingst"));
                ps.setMrp(rs.getString("mrp"));
                ps.setQty(rs.getString("qty"));
                ps.setGross(rs.getString("gross"));
                ps.setDiscamt(rs.getString("discamt"));
                ps.setTaxableamt(rs.getString("taxableamt"));
                ps.setGstamt(rs.getString("gstamt"));
                ps.setQtysold(rs.getString("qtysold"));
                ps.setRetqty(rs.getString("retqty"));
                ps.setTradediscamt(rs.getString("tradediscamt"));
                ps.setReplacementdiscamt(rs.getString("replacementdiscamt"));
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: getPurchaseSub ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e) {
                return null;
            }
        }
        return ps;
    }
    
    public PurchaseMasterV2 getPurchaseMasterV2( String pmid ) {
        PurchaseMasterV2 pm=new PurchaseMasterV2();
        // Table PurchaseMasterV2, no. of columns - 20
        /*
        pmid, ssid, compid, invno, invdt, netqty, netitemdisc, netgross, tradediscper, nettradediscamt, 
        replacementdiscper, netreplacementdiscamt, netgstamt, amtaftergst, roundoff, dispscheme, 
        netpayableamt, isopening, amtpaid, isactive
        */
        String query="select pmid, ssid, compid, invno, invdt, netqty, netitemdisc, netgross, "
                + "tradediscper, nettradediscamt, replacementdiscper, netreplacementdiscamt, "
                + "netgstamt, amtaftergst, roundoff, dispscheme, netpayableamt, isopening, "
                + "amtpaid, isactive from PurchaseMasterV2 where pmid="+pmid; 
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        try {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(query);
            while(rs.next()) {
                // Table PurchaseMasterV2, no. of columns - 20
                /*
                pmid, ssid, compid, invno, invdt, netqty, netitemdisc, netgross, tradediscper, nettradediscamt, 
                replacementdiscper, netreplacementdiscamt, netgstamt, amtaftergst, roundoff, dispscheme, 
                netpayableamt, isopening, amtpaid, isactive
                */
                pm.setPmid(pmid);
                pm.setSsid(rs.getString("ssid"));
                pm.setCompid(rs.getString("compid"));
                pm.setInvno(rs.getString("invno"));
                pm.setInvdt(DateConverter.dateConverter(rs.getString("invdt")));
                pm.setNetqty(rs.getString("netqty"));
                pm.setNetitemdisc(rs.getString("netitemdisc"));
                pm.setNetgross(rs.getString("netgross"));
                pm.setTradediscper(rs.getString("tradediscper"));
                pm.setNettradediscamt(rs.getString("nettradediscamt"));
                pm.setReplacementdiscper(rs.getString("replacementdiscper"));
                pm.setNetreplacementdiscamt(rs.getString("netreplacementdiscamt"));
                pm.setNetgstamt(rs.getString("netgstamt"));
                pm.setAmtaftergst(rs.getString("amtaftergst"));
                pm.setRoundoff(rs.getString("roundoff"));
                pm.setDispscheme(rs.getString("dispscheme"));
                pm.setNetpayableamt(rs.getString("netpayableamt"));
                pm.setIsopening(rs.getString("isopening"));
                pm.setAmtpaid(rs.getString("amtpaid"));
                pm.setIsactive(rs.getString("isactive"));
                
                ArrayList<PurchaseSubV2> psAl=new ArrayList<PurchaseSubV2>();
                // Table PurchaseSubV2, no. of columns - 12
                /*
                psid, pmid, itemdid, mrp, gst, qty, rate, discper, discamt, amount, qtysold, retqty
                */
                query="select psid, pmid, itemdid, mrp, gst, qty, rate, discper, discamt, amount, "
                        + "qtysold, retqty from PurchaseSubV2 where pmid="+pmid; 
                try
                {
                    Statement smt1=conn.createStatement();
                    ResultSet rs1=smt1.executeQuery(query);
                    while(rs1.next())
                    {    
                        PurchaseSubV2 ps = new PurchaseSubV2();
                        // Table PurchaseSubV2, no. of columns - 12
                        /*
                        psid, pmid, itemdid, mrp, gst, qty, rate, discper, discamt, amount, qtysold, retqty
                        */
                        ps.setPsid(rs1.getString("psid"));
                        ps.setPmid(rs1.getString("pmid"));
                        ps.setItemdid(rs1.getString("itemdid"));
                        ps.setMrp(rs1.getString("mrp"));
                        ps.setGst(rs1.getString("gst"));
                        ps.setQty(rs1.getString("qty"));
                        ps.setRate(rs1.getString("rate"));
                        ps.setDiscper(rs1.getString("discper"));
                        ps.setDiscamt(rs1.getString("discamt"));
                        ps.setAmount(rs1.getString("amount"));
                        ps.setQtysold(rs1.getString("qtysold"));
                        ps.setRetqty(rs1.getString("retqty"));

                        psAl.add(ps);
                    }
                }
                catch(SQLException ex)
                {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,"Query: getPurchaseMaster ex: "+ex.getMessage(),
                            "SQL Error Found",JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                
                pm.setPsAl(psAl);
                
                ArrayList<PurchaseGSTV2> pgstAl=new ArrayList<PurchaseGSTV2>();
                // Table PurchaseGSTV2, no. of columns - 5
                /*
                pgstid, pmid, gstper, taxableamt, gstamt
                */
                query="select pgstid, pmid, gstper, taxableamt, gstamt from PurchaseGSTV2 where pmid="+pmid; 
                try
                {
                    Statement smt1=conn.createStatement();
                    ResultSet rs1=smt1.executeQuery(query);
                    while(rs1.next())
                    {    
                        PurchaseGSTV2 pgst = new PurchaseGSTV2();
                        // Table PurchaseGSTV2, no. of columns - 5
                        /*
                        pgstid, pmid, gstper, taxableamt, gstamt
                        */
                        pgst.setPgstid(rs1.getString("pgstid"));
                        pgst.setPmid(rs1.getString("pmid"));
                        pgst.setGstper(rs1.getString("gstper"));
                        pgst.setTaxableamt(rs1.getString("taxableamt"));
                        pgst.setGstamt(rs1.getString("gstamt"));

                        pgstAl.add(pgst);
                    }
                }
                catch(SQLException ex)
                {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,"Query: getPurchaseMasterV2 ex: "+ex.getMessage(),
                            "SQL Error Found",JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                
                pm.setPgstAl(pgstAl);
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: getPurchaseMasterV2 ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e){}
        }
        return pm;
    }
    
    public boolean insertToSaleMaster(SaleMaster sm)
    {
        dBConnection db=null;
        Connection conn = null;
        
        PreparedStatement preparedStatementInsert1 = null;
        PreparedStatement preparedStatementInsert2 = null;
        
        int salesid = getMaxId("SaleSub", "salesid");

        // Number of columns in SaleMaster: 27
        /* salemid, compid, retid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
        supplydt, netqty, netgross, netitemdisc, nettaxable, netcgst, netsgst, netigst, nettotal, 
        cashdiscper, cashdiscamt, netamt01, roundoff, netamt02, amtpaid, isactive, remarks */
        String insertTableSQL1 = "insert into SaleMaster (salemid, compid, retid, saledt, ordno, orddt, "
                + "deliverynote, paymentterm, transporter, vehicleno, supplydt, netqty, netgross, netitemdisc, "
                + "nettaxable, netcgst, netsgst, netigst, nettotal, cashdiscper, cashdiscamt, netamt01, roundoff, "
                + "netamt02, amtpaid, isactive, remarks) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        // Number of columns in SaleSub: 20
        /* salesid, salemid, psid, itemdid, qty, mrp, gst, exgst, ingst, amt, itemdiscamt, 
        taxableamt, cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
        String insertTableSQL2 = "insert into SaleSub (salesid, salemid, psid, itemdid, qty, mrp, gst, "
                + "exgst, ingst, amt, itemdiscamt, taxableamt, cgstper, cgstamt, sgstper, sgstamt, igstper, "
                + "igstamt, total, retqty) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try 
        {
            db=new dBConnection();
            conn=db.setConnection();

            conn.setAutoCommit(false);
            
            // Number of columns in SaleMaster: 27
            /* salemid, compid, retid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
            supplydt, netqty, netgross, netitemdisc, nettaxable, netcgst, netsgst, netigst, nettotal, 
            cashdiscper, cashdiscamt, netamt01, roundoff, netamt02, amtpaid, isactive, remarks */
            preparedStatementInsert1 = conn.prepareStatement(insertTableSQL1);
            preparedStatementInsert1.setString(1, sm.getSalemid());
            preparedStatementInsert1.setInt(2, Integer.parseInt(sm.getCompid()));
            preparedStatementInsert1.setInt(3, Integer.parseInt(sm.getRetid()));
            preparedStatementInsert1.setDate(4, java.sql.Date.valueOf(DateConverter.dateConverter1(sm.getSaledt())));
            preparedStatementInsert1.setString(5, sm.getOrdno());
            preparedStatementInsert1.setDate(6, java.sql.Date.valueOf(DateConverter.dateConverter1(sm.getOrddt())));
            preparedStatementInsert1.setString(7, sm.getDeliverynote());
            preparedStatementInsert1.setString(8, sm.getPaymentterm());
            preparedStatementInsert1.setString(9, sm.getTransporter());
            preparedStatementInsert1.setString(10, sm.getVehicleno());
            preparedStatementInsert1.setDate(11, java.sql.Date.valueOf(DateConverter.dateConverter1(sm.getSupplydt())));
            preparedStatementInsert1.setInt(12, Integer.parseInt(sm.getNetqty()));
            preparedStatementInsert1.setDouble(13, Double.parseDouble(sm.getNetgross()));
            preparedStatementInsert1.setDouble(14, Double.parseDouble(sm.getNetitemdisc()));
            preparedStatementInsert1.setDouble(15, Double.parseDouble(sm.getNettaxable()));
            preparedStatementInsert1.setDouble(16, Double.parseDouble(sm.getNetcgst()));
            preparedStatementInsert1.setDouble(17, Double.parseDouble(sm.getNetsgst()));
            preparedStatementInsert1.setDouble(18, Double.parseDouble(sm.getNetigst()));
            preparedStatementInsert1.setDouble(19, Double.parseDouble(sm.getNettotal()));
            preparedStatementInsert1.setDouble(20, Double.parseDouble(sm.getCashdiscper()));
            preparedStatementInsert1.setDouble(21, Double.parseDouble(sm.getCashdiscamt()));
            preparedStatementInsert1.setDouble(22, Double.parseDouble(sm.getNetamt01()));
            preparedStatementInsert1.setDouble(23, Double.parseDouble(sm.getRoundoff()));
            preparedStatementInsert1.setDouble(24, Double.parseDouble(sm.getNetamt02()));
            preparedStatementInsert1.setDouble(25, Double.parseDouble(sm.getAmtpaid()));
            preparedStatementInsert1.setInt(26, Integer.parseInt(sm.getIsactive()));
            preparedStatementInsert1.setString(27, sm.getRemarks());
            preparedStatementInsert1.executeUpdate();
            
            preparedStatementInsert2 = conn.prepareStatement(insertTableSQL2);
            for(SaleSub ss : sm.getSsAl())
            {
                // Number of columns in SaleSub: 20
                /* salesid, salemid, psid, itemdid, qty, mrp, gst, exgst, ingst, amt, itemdiscamt, 
                taxableamt, cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
                preparedStatementInsert2.setInt(1, ++salesid);
                preparedStatementInsert2.setString(2, sm.getSalemid());
                preparedStatementInsert2.setInt(3, Integer.parseInt(ss.getPsid()));
                preparedStatementInsert2.setInt(4, Integer.parseInt(ss.getItemdid()));
                preparedStatementInsert2.setInt(5, Integer.parseInt(ss.getQty()));
                preparedStatementInsert2.setDouble(6, Double.parseDouble(ss.getMrp()));
                preparedStatementInsert2.setDouble(7, Double.parseDouble(ss.getGst()));
                preparedStatementInsert2.setDouble(8, Double.parseDouble(ss.getExgst()));
                preparedStatementInsert2.setDouble(9, Double.parseDouble(ss.getIngst()));
                preparedStatementInsert2.setDouble(10, Double.parseDouble(ss.getAmt()));
                preparedStatementInsert2.setDouble(11, Double.parseDouble(ss.getItemdiscamt()));
                preparedStatementInsert2.setDouble(12, Double.parseDouble(ss.getTaxableamt()));
                preparedStatementInsert2.setDouble(13, Double.parseDouble(ss.getCgstper()));
                preparedStatementInsert2.setDouble(14, Double.parseDouble(ss.getCgstamt()));
                preparedStatementInsert2.setDouble(15, Double.parseDouble(ss.getSgstper()));
                preparedStatementInsert2.setDouble(16, Double.parseDouble(ss.getSgstamt()));
                preparedStatementInsert2.setDouble(17, Double.parseDouble(ss.getIgstper()));
                preparedStatementInsert2.setDouble(18, Double.parseDouble(ss.getIgstamt()));
                preparedStatementInsert2.setDouble(19, Double.parseDouble(ss.getTotal()));
                preparedStatementInsert2.setInt(20, Integer.parseInt(ss.getRetqty()));
                preparedStatementInsert2.addBatch(); 
            }
            preparedStatementInsert2.executeBatch();

            conn.commit();
        } 
        catch (SQLException ex) 
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: insertToSaleMaster ex: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            try {
                conn.rollback();
                return false;
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        } 
        finally 
        {
            if (preparedStatementInsert1 != null) 
            {
                try {
                    preparedStatementInsert1.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (preparedStatementInsert2 != null) 
            {
                try {
                    preparedStatementInsert2.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) 
            {
                db=null;
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return true;
    }
    
    public SaleMaster getSaleMaster( String salemid )
    {
        SaleMaster sm=new SaleMaster();
        // Number of columns in SaleMaster: 27
        /* salemid, compid, retid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
        supplydt, netqty, netgross, netitemdisc, nettaxable, netcgst, netsgst, netigst, nettotal, 
        cashdiscper, cashdiscamt, netamt01, roundoff, netamt02, amtpaid, isactive, remarks */
        String query="select salemid, compid, retid, saledt, ordno, orddt, deliverynote, paymentterm, "
                + "transporter, vehicleno,  supplydt, netqty, netgross, netitemdisc, nettaxable, netcgst, "
                + "netsgst, netigst, nettotal, cashdiscper, cashdiscamt, netamt01, roundoff, netamt02, "
                + "amtpaid, isactive, remarks from SaleMaster where salemid='"+salemid+"'"; 
        System.out.println(query);
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        try
        {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(query);
            while(rs.next())
            {
                // Number of columns in SaleMaster: 27
                /* salemid, compid, retid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
                supplydt, netqty, netgross, netitemdisc, nettaxable, netcgst, netsgst, netigst, nettotal, 
                cashdiscper, cashdiscamt, netamt01, roundoff, netamt02, amtpaid, isactive, remarks */
                sm.setSalemid(salemid);
                sm.setCompid(rs.getString("compid"));
                sm.setRetid(rs.getString("retid"));
                sm.setSaledt(DateConverter.dateConverter(rs.getString("saledt")));
                sm.setOrdno(rs.getString("ordno"));
                sm.setOrddt(DateConverter.dateConverter(rs.getString("orddt")));
                sm.setDeliverynote(rs.getString("deliverynote"));
                sm.setPaymentterm(rs.getString("paymentterm"));
                sm.setTransporter(rs.getString("transporter"));
                sm.setVehicleno(rs.getString("vehicleno"));
                sm.setSupplydt(DateConverter.dateConverter(rs.getString("supplydt")));
                sm.setNetqty(rs.getString("netqty"));
                sm.setNetgross(rs.getString("netgross"));
                sm.setNetitemdisc(rs.getString("netitemdisc"));
                sm.setNettaxable(rs.getString("nettaxable"));
                sm.setNetcgst(rs.getString("netcgst"));
                sm.setNetsgst(rs.getString("netsgst"));
                sm.setNetigst(rs.getString("netigst"));
                sm.setNettotal(rs.getString("nettotal"));
                sm.setCashdiscper(rs.getString("cashdiscper"));
                sm.setCashdiscamt(rs.getString("cashdiscamt"));
                sm.setNetamt01(rs.getString("netamt01"));
                sm.setRoundoff(rs.getString("roundoff"));
                sm.setNetamt02(rs.getString("netamt02"));
                sm.setAmtpaid(rs.getString("amtpaid"));
                sm.setIsactive(rs.getString("isactive"));
                sm.setRemarks(rs.getString("remarks"));
                
                ArrayList<SaleSub> ssAl=new ArrayList<SaleSub>();
                // Number of columns in SaleSub: 20
                /* salesid, salemid, psid, itemdid, qty, mrp, gst, exgst, ingst, amt, itemdiscamt, 
                taxableamt, cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
                query="select salesid, salemid, psid, itemdid, qty, mrp, gst, exgst, ingst, amt, itemdiscamt, "
                        + "taxableamt, cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty from "
                        + "SaleSub where salemid='"+salemid+"'"; 
                System.out.println(query);
                try
                {
                    Statement smt1=conn.createStatement();
                    ResultSet rs1=smt1.executeQuery(query);
                    while(rs1.next())
                    {    
                        SaleSub ss = new SaleSub();
                        // Number of columns in SaleSub: 20
                        /* salesid, salemid, psid, itemdid, qty, mrp, gst, exgst, ingst, amt, itemdiscamt, 
                        taxableamt, cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
                        ss.setSalesid(rs1.getString("salesid"));
                        ss.setSalemid(rs1.getString("salemid"));
                        ss.setPsid(rs1.getString("psid"));
                        ss.setItemdid(rs1.getString("itemdid"));
                        ss.setQty(rs1.getString("qty"));
                        ss.setMrp(rs1.getString("mrp"));
                        ss.setGst(rs1.getString("gst"));
                        ss.setExgst(rs1.getString("exgst"));
                        ss.setIngst(rs1.getString("ingst"));
                        ss.setAmt(rs1.getString("amt"));
                        ss.setItemdiscamt(rs1.getString("itemdiscamt"));
                        ss.setTaxableamt(rs1.getString("taxableamt"));
                        ss.setCgstper(rs1.getString("cgstper"));
                        ss.setCgstamt(rs1.getString("cgstamt"));
                        ss.setSgstper(rs1.getString("sgstper"));
                        ss.setSgstamt(rs1.getString("sgstamt"));
                        ss.setIgstper(rs1.getString("igstper"));
                        ss.setIgstamt(rs1.getString("igstamt"));
                        ss.setTotal(rs1.getString("total"));
                        ss.setRetqty(rs1.getString("retqty"));
                        ssAl.add(ss);
                    }
                }
                catch(SQLException ex)
                {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,"Query: getSaleMaster ex: "+ex.getMessage(),
                            "SQL Error Found",JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                
                sm.setSsAl(ssAl);
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: getSaleMaster ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e){}
        }
        return sm;
    }
    
    public boolean insertToSaleMasterV2(SaleMasterV2 sm) {
        dBConnection db=null;
        Connection conn = null;
        
        PreparedStatement preparedStatementInsert1 = null;
        PreparedStatement preparedStatementInsert2 = null;
        
        int salesid = getMaxId("SaleSubV2", "salesid");

        // Table SaleMasterV2, no. of columns - 24
        /*
        salemid, compid, saledt, ordno, orddt, retid, deliverynote, paymentterm, transporter, vehicleno, 
        supplydt, totnoofitems, netgross, netitemdiscamt, netgstamt, cashdiscper, netcashdiscamt, 
        netamt, roundoff, dispscheme, netpayableamt, amtpaid, isactive, remarks
        */
        String insertTableSQL1 = "insert into SaleMasterV2 (salemid, compid, saledt, ordno, "
                + "orddt, retid, deliverynote, paymentterm, transporter, vehicleno, "
                + "supplydt, totnoofitems, netgross, netitemdiscamt, netgstamt, cashdiscper, "
                + "netcashdiscamt, netamt, roundoff, dispscheme, netpayableamt, amtpaid, isactive, "
                + "remarks) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                + "?, ?, ?, ?, ?, ?, ?, ?)";
        
        // Table SaleSubV2, no. of columns - 17
        /*
        salesid, salemid, psid, itemdid, mrp, gst, qty, free, unitnetrate, rate, 
        gross, itemdiscper, itemdiscamt, cashdiscamt, gstamt, amount, retqty
        */
        String insertTableSQL2 = "insert into SaleSubV2 (salesid, salemid, psid, itemdid, mrp, "
                + "gst, qty, free, unitnetrate, rate, gross, itemdiscper, itemdiscamt, cashdiscamt, gstamt, "
                + "amount, retqty) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            db=new dBConnection();
            conn=db.setConnection();

            conn.setAutoCommit(false);
            
            // Table SaleMasterV2, no. of columns - 24
            /*
            salemid, compid, saledt, ordno, orddt, retid, deliverynote, paymentterm, transporter, vehicleno, 
            supplydt, totnoofitems, netgross, netitemdiscamt, netgstamt, cashdiscper, netcashdiscamt, 
            netamt, roundoff, dispscheme, netpayableamt, amtpaid, isactive, remarks
            */
            preparedStatementInsert1 = conn.prepareStatement(insertTableSQL1);
            preparedStatementInsert1.setString(1, sm.getSalemid());
            preparedStatementInsert1.setInt(2, Integer.parseInt(sm.getCompid()));
            preparedStatementInsert1.setDate(3, java.sql.Date.valueOf(DateConverter.dateConverter1(sm.getSaledt())));
            preparedStatementInsert1.setString(4, sm.getOrdno());
            preparedStatementInsert1.setDate(5, java.sql.Date.valueOf(DateConverter.dateConverter1(sm.getOrddt())));
            preparedStatementInsert1.setInt(6, Integer.parseInt(sm.getRetid()));
            preparedStatementInsert1.setString(7, sm.getDeliverynote());
            preparedStatementInsert1.setString(8, sm.getPaymentterm());
            preparedStatementInsert1.setString(9, sm.getTransporter());
            preparedStatementInsert1.setString(10, sm.getVehicleno());
            preparedStatementInsert1.setDate(11, java.sql.Date.valueOf(DateConverter.dateConverter1(sm.getSupplydt())));
            preparedStatementInsert1.setInt(12, Integer.parseInt(sm.getTotnoofitems()));
            preparedStatementInsert1.setDouble(13, Double.parseDouble(sm.getNetgross()));
            preparedStatementInsert1.setDouble(14, Double.parseDouble(sm.getNetitemdiscamt()));
            preparedStatementInsert1.setDouble(15, Double.parseDouble(sm.getNetgstamt()));
            preparedStatementInsert1.setDouble(16, Double.parseDouble(sm.getCashdiscper()));
            preparedStatementInsert1.setDouble(17, Double.parseDouble(sm.getNetcashdiscamt()));
            preparedStatementInsert1.setDouble(18, Double.parseDouble(sm.getNetamt()));
            preparedStatementInsert1.setDouble(19, Double.parseDouble(sm.getRoundoff()));
            preparedStatementInsert1.setDouble(20, Double.parseDouble(sm.getDispscheme()));
            preparedStatementInsert1.setDouble(21, Double.parseDouble(sm.getNetpayableamt()));
            preparedStatementInsert1.setDouble(22, Double.parseDouble(sm.getAmtpaid()));
            preparedStatementInsert1.setInt(23, Integer.parseInt(sm.getIsactive()));
            preparedStatementInsert1.setString(24, sm.getRemarks());
            preparedStatementInsert1.executeUpdate();
            
            preparedStatementInsert2 = conn.prepareStatement(insertTableSQL2);
            for(SaleSubV2 ss : sm.getSsAl()) {
                // Table SaleSubV2, no. of columns - 17
                /*
                salesid, salemid, psid, itemdid, mrp, gst, qty, free, unitnetrate, rate, 
                gross, itemdiscper, itemdiscamt, cashdiscamt, gstamt, amount, retqty
                */
                preparedStatementInsert2.setInt(1, ++salesid);
                preparedStatementInsert2.setString(2, sm.getSalemid());
                preparedStatementInsert2.setInt(3, Integer.parseInt(ss.getPsid()));
                preparedStatementInsert2.setInt(4, Integer.parseInt(ss.getItemdid()));
                preparedStatementInsert2.setDouble(5, Double.parseDouble(ss.getMrp()));
                preparedStatementInsert2.setDouble(6, Double.parseDouble(ss.getGst()));
                preparedStatementInsert2.setInt(7, Integer.parseInt(ss.getQty()));
                preparedStatementInsert2.setInt(8, Integer.parseInt(ss.getFree()));
                preparedStatementInsert2.setDouble(9, Double.parseDouble(ss.getUnitnetrate()));
                preparedStatementInsert2.setDouble(10, Double.parseDouble(ss.getRate()));
                preparedStatementInsert2.setDouble(11, Double.parseDouble(ss.getGross()));
                preparedStatementInsert2.setDouble(12, Double.parseDouble(ss.getItemdiscper()));
                preparedStatementInsert2.setDouble(13, Double.parseDouble(ss.getItemdiscamt()));
                preparedStatementInsert2.setDouble(14, Double.parseDouble(ss.getCashdiscamt()));
                preparedStatementInsert2.setDouble(15, Double.parseDouble(ss.getGstamt()));
                preparedStatementInsert2.setDouble(16, Double.parseDouble(ss.getAmount()));
                preparedStatementInsert2.setInt(17, Integer.parseInt(ss.getRetqty()));
                preparedStatementInsert2.addBatch(); 
            }
            preparedStatementInsert2.executeBatch();

            conn.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: insertToSaleMasterV2 ex: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            try {
                conn.rollback();
                return false;
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        } finally {
            if (preparedStatementInsert1 != null) {
                try {
                    preparedStatementInsert1.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (preparedStatementInsert2 != null) {
                try {
                    preparedStatementInsert2.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                db=null;
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return true;
    }
    
    public SaleMasterV2 getSaleMasterV2( String salemid ) {
        SaleMasterV2 sm=new SaleMasterV2();
        // Table SaleMasterV2, no. of columns - 24
        /*
        salemid, compid, saledt, ordno, orddt, retid, deliverynote, paymentterm, transporter, vehicleno, 
        supplydt, totnoofitems, netgross, netitemdiscamt, netgstamt, cashdiscper, netcashdiscamt, 
        netamt, roundoff, dispscheme, netpayableamt, amtpaid, isactive, remarks
        */
        String query="select salemid, compid, saledt, ordno, orddt, retid, deliverynote, paymentterm, "
                + "transporter, vehicleno, supplydt, totnoofitems, netgross, netitemdiscamt, netgstamt, "
                + "cashdiscper, netcashdiscamt, netamt, roundoff, dispscheme, netpayableamt, amtpaid, "
                + "isactive, remarks from SaleMasterV2 where salemid='"+salemid+"'"; 
        System.out.println(query);
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        try {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(query);
            while(rs.next()) {
                // Table SaleMasterV2, no. of columns - 24
                /*
                salemid, compid, saledt, ordno, orddt, retid, deliverynote, paymentterm, transporter, vehicleno, 
                supplydt, totnoofitems, netgross, netitemdiscamt, netgstamt, cashdiscper, netcashdiscamt, 
                netamt, roundoff, dispscheme, netpayableamt, amtpaid, isactive, remarks
                */
                sm.setSalemid(salemid);
                sm.setCompid(rs.getString("compid"));
                sm.setSaledt(DateConverter.dateConverter(rs.getString("saledt")));
                sm.setOrdno(rs.getString("ordno"));
                sm.setOrddt(DateConverter.dateConverter(rs.getString("orddt")));
                sm.setRetid(rs.getString("retid"));
                sm.setDeliverynote(rs.getString("deliverynote"));
                sm.setPaymentterm(rs.getString("paymentterm"));
                sm.setTransporter(rs.getString("transporter"));
                sm.setVehicleno(rs.getString("vehicleno"));
                sm.setSupplydt(DateConverter.dateConverter(rs.getString("supplydt")));
                sm.setTotnoofitems(rs.getString("totnoofitems"));
                sm.setNetgross(rs.getString("netgross"));
                sm.setNetitemdiscamt(rs.getString("netitemdiscamt"));
                sm.setNetgstamt(rs.getString("netgstamt"));
                sm.setCashdiscper(rs.getString("cashdiscper"));
                sm.setNetcashdiscamt(rs.getString("netcashdiscamt"));
                sm.setNetamt(rs.getString("netamt"));
                sm.setRoundoff(rs.getString("roundoff"));
                sm.setDispscheme(rs.getString("dispscheme"));
                sm.setNetpayableamt(rs.getString("netpayableamt"));
                sm.setAmtpaid(rs.getString("amtpaid"));
                sm.setIsactive(rs.getString("isactive"));
                sm.setRemarks(rs.getString("remarks"));
                
                ArrayList<SaleSubV2> ssAl=new ArrayList<>();
                // Table SaleSubV2, no. of columns - 17
                /*
                salesid, salemid, psid, itemdid, mrp, gst, qty, free, unitnetrate, rate, 
                gross, itemdiscper, itemdiscamt, cashdiscamt, gstamt, amount, retqty
                */
                query="select salesid, salemid, psid, itemdid, mrp, gst, qty, free, unitnetrate, rate, gross, "
                        + "itemdiscper, itemdiscamt, cashdiscamt, gstamt, amount, retqty from "
                        + "SaleSubV2 where salemid='"+salemid+"'"; 
                System.out.println(query);
                try {
                    Statement smt1=conn.createStatement();
                    ResultSet rs1=smt1.executeQuery(query);
                    while(rs1.next()) {    
                        SaleSubV2 ss = new SaleSubV2();
                        // Table SaleSubV2, no. of columns - 16
                        /*
                        salesid, salemid, psid, itemdid, mrp, gst, qty, free, rate, gross, itemdiscper, 
                        itemdiscamt, cashdiscamt, gstamt, amount, retqty
                        */
                        ss.setSalesid(rs1.getString("salesid"));
                        ss.setSalemid(rs1.getString("salemid"));
                        ss.setPsid(rs1.getString("psid"));
                        ss.setItemdid(rs1.getString("itemdid"));
                        ss.setMrp(rs1.getString("mrp"));
                        ss.setGst(rs1.getString("gst"));
                        ss.setQty(rs1.getString("qty"));
                        ss.setFree(rs1.getString("free"));
                        ss.setUnitnetrate(rs1.getString("unitnetrate"));
                        ss.setRate(rs1.getString("rate"));
                        ss.setGross(rs1.getString("gross"));
                        ss.setItemdiscper(rs1.getString("itemdiscper"));
                        ss.setItemdiscamt(rs1.getString("itemdiscamt"));
                        ss.setCashdiscamt(rs1.getString("cashdiscamt"));
                        ss.setGstamt(rs1.getString("gstamt"));
                        ss.setAmount(rs1.getString("amount"));
                        ss.setRetqty(rs1.getString("retqty"));
                        ssAl.add(ss);
                    }
                } catch(SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,"Query: getSaleMasterV2 ex: "+ex.getMessage(),
                            "SQL Error Found",JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                
                sm.setSsAl(ssAl);
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: getSaleMasterV2 ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        } finally {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e){}
        }
        return sm;
    }
    
    public SaleSub getSaleSub( String salesid )
    {
        SaleSub ss = new SaleSub();
        
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        
        try {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            // Number of columns in SaleSub: 20
            /* salesid, salemid, psid, itemdid, qty, mrp, gst, exgst, ingst, amt, itemdiscamt, 
            taxableamt, cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
            String query="select salesid, salemid, psid, itemdid, qty, mrp, gst, exgst, ingst, amt, "
                    + "itemdiscamt, taxableamt, cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, "
                    + "retqty from SaleSub where salesid="+salesid; 
            ResultSet rs=smt.executeQuery(query);
            
            if (rs.next()) {
                ss.setSalesid(rs.getString("salesid"));
                ss.setSalemid(rs.getString("salemid"));
                ss.setPsid(rs.getString("psid"));
                ss.setItemdid(rs.getString("itemdid"));
                ss.setQty(rs.getString("qty"));
                ss.setMrp(rs.getString("mrp"));
                ss.setGst(rs.getString("gst"));
                ss.setExgst(rs.getString("exgst"));
                ss.setIngst(rs.getString("ingst"));
                ss.setAmt(rs.getString("amt"));
                ss.setItemdiscamt(rs.getString("itemdiscamt"));
                ss.setTaxableamt(rs.getString("taxableamt"));
                ss.setCgstper(rs.getString("cgstper"));
                ss.setCgstamt(rs.getString("cgstamt"));
                ss.setSgstper(rs.getString("sgstper"));
                ss.setSgstamt(rs.getString("sgstamt"));
                ss.setIgstper(rs.getString("igstper"));
                ss.setIgstamt(rs.getString("igstamt"));
                ss.setTotal(rs.getString("total"));
                ss.setRetqty(rs.getString("retqty"));
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: getSaleSub ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e) {
                return null;
            }
        }
        return ss;
    }
    
    public Retailer getRetailer( String retid )
    {
        Retailer r=new Retailer();
        // Number of columns in Retailer: 18
        /* retid, beatid, retnm, contactperson, rstreet, rcity, rdist, rstate, rstatecode, 
        rpin, rcountry, rcontact, rmail, rgstno, rgstregntype, rpanno, raadhaarno, isactive*/
        String query="select retid, beatid, retnm, contactperson, rstreet, rcity, rdist, rstate, rstatecode, "
                + "rpin, rcountry, rcontact, rmail, rgstno, rgstregntype, rpanno, raadhaarno, isactive from"
                + " Retailer where retid="+retid; 
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        try
        {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(query);
            if(rs.next())
            {
                // Number of columns in Retailer: 18
                /* retid, beatid, retnm, contactperson, rstreet, rcity, rdist, rstate, rstatecode, 
                rpin, rcountry, rcontact, rmail, rgstno, rgstregntype, rpanno, raadhaarno, isactive*/
                r.setRetid(retid);
                r.setBeatid(rs.getString("beatid"));
                r.setRetnm(rs.getString("retnm"));
                r.setContactperson(rs.getString("contactperson"));
                r.setRstreet(rs.getString("rstreet"));
                r.setRcity(rs.getString("rcity"));
                r.setRdist(rs.getString("rdist"));
                r.setRstate(rs.getString("rstate"));
                r.setRstatecode(rs.getString("rstatecode"));
                r.setRpin(rs.getString("rpin"));
                r.setRcountry(rs.getString("rcountry"));
                r.setRcontact(rs.getString("rcontact"));
                r.setRmail(rs.getString("rmail"));
                r.setRgstno(rs.getString("rgstno"));
                r.setRgstregntype(rs.getString("rgstregntype"));
                r.setRpanno(rs.getString("rpanno"));
                r.setRaadhaarno(rs.getString("raadhaarno"));
                r.setIsactive(rs.getString("isactive"));
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: getRetailer ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e){}
        }
        return r;
    }
    
    public String getEnterpriseName()
    {
        // Number of columns in Enterprise: 16
        /* ename, estreet, ecity, edist, estate, estatecode, epin, ecountry, 
        eabbr, econtact, email, efax, egstno, egstregntype, epanno, eaadhaarno */
        String ename = "";
        String query="select ename from Enterprise"; 
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        try
        {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(query);
            if(rs.next())
            {
                // Number of columns in Enterprise: 16
                /* ename, estreet, ecity, edist, estate, estatecode, epin, ecountry, 
                eabbr, econtact, email, efax, egstno, egstregntype, epanno, eaadhaarno */
                ename = rs.getString("ename");
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: getEnterpriseName ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e){}
        }
        return ename;
    }
    
    public boolean updateSaleMaster(SaleMaster sm, ArrayList<SaleSub> oldSsAl )
    {
        dBConnection db=null;
        Connection conn = null;
        
        PreparedStatement preparedStatementInsert1 = null;
        PreparedStatement preparedStatementInsert2 = null;
        PreparedStatement preparedStatementInsert3 = null;
        
        int salesid = getMaxId("SaleSub", "salesid");

        // Number of columns in SaleMaster: 27
        /* salemid, compid, retid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
        supplydt, netqty, netgross, netitemdisc, nettaxable, netcgst, netsgst, netigst, nettotal, 
        cashdiscper, cashdiscamt, netamt01, roundoff, netamt02, amtpaid, isactive, remarks */
        String sql1 = "update SaleMaster set saledt=?, ordno=?, orddt=?, deliverynote=?, paymentterm=?, "
                + "transporter=?, vehicleno=?, supplydt=?, netqty=?, netgross=?, nettaxable=?, netcgst=?, netsgst=?, "
                + "nettotal=?, cashdiscper=?, cashdiscamt=?, netamt01=?, roundoff=?, netamt02=?, amtpaid=?, remarks=? where "
                + "salemid='"+sm.getSalemid()+"'";
        
        // Number of columns in SaleSub: 20
        /* salesid, salemid, psid, itemdid, qty, mrp, gst, exgst, ingst, amt, itemdiscamt, 
        taxableamt, cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
        String sql2 = "insert into SaleSub (salesid, salemid, psid, itemdid, qty, mrp, gst, "
                + "exgst, ingst, amt, itemdiscamt, taxableamt, cgstper, cgstamt, sgstper, sgstamt, igstper, "
                + "igstamt, total, retqty) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        // Number of columns in SaleSub: 20
        /* salesid, salemid, psid, itemdid, qty, mrp, gst, exgst, ingst, amt, itemdiscamt, 
        taxableamt, cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
        String sql3 = "delete from SaleSub where salesid=?";

        try 
        {
            db=new dBConnection();
            conn=db.setConnection();

            conn.setAutoCommit(false);
            
            // Number of columns in SaleMaster: 27
            /* salemid, compid, retid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
            supplydt, netqty, netgross, netitemdisc, nettaxable, netcgst, netsgst, netigst, nettotal, 
            cashdiscper, cashdiscamt, netamt01, roundoff, netamt02, amtpaid, isactive, remarks */
            preparedStatementInsert1 = conn.prepareStatement(sql1);
            preparedStatementInsert1.setDate(1, java.sql.Date.valueOf(DateConverter.dateConverter1(sm.getSaledt())));
            preparedStatementInsert1.setString(2, sm.getOrdno());
            preparedStatementInsert1.setDate(3, java.sql.Date.valueOf(DateConverter.dateConverter1(sm.getOrddt())));
            preparedStatementInsert1.setString(4, sm.getDeliverynote());
            preparedStatementInsert1.setString(5, sm.getPaymentterm());
            preparedStatementInsert1.setString(6, sm.getTransporter());
            preparedStatementInsert1.setString(7, sm.getVehicleno());
            preparedStatementInsert1.setDate(8, java.sql.Date.valueOf(DateConverter.dateConverter1(sm.getSupplydt())));
            preparedStatementInsert1.setInt(9, Integer.parseInt(sm.getNetqty()));
            preparedStatementInsert1.setDouble(10, Double.parseDouble(sm.getNetgross()));
            preparedStatementInsert1.setDouble(11, Double.parseDouble(sm.getNettaxable()));
            preparedStatementInsert1.setDouble(12, Double.parseDouble(sm.getNetcgst()));
            preparedStatementInsert1.setDouble(13, Double.parseDouble(sm.getNetsgst()));
            preparedStatementInsert1.setDouble(14, Double.parseDouble(sm.getNettotal()));
            preparedStatementInsert1.setDouble(15, Double.parseDouble(sm.getCashdiscper()));
            preparedStatementInsert1.setDouble(16, Double.parseDouble(sm.getCashdiscamt()));
            preparedStatementInsert1.setDouble(17, Double.parseDouble(sm.getNetamt01()));
            preparedStatementInsert1.setDouble(18, Double.parseDouble(sm.getRoundoff()));
            preparedStatementInsert1.setDouble(19, Double.parseDouble(sm.getNetamt02()));
            preparedStatementInsert1.setDouble(20, Double.parseDouble(sm.getAmtpaid()));
            preparedStatementInsert1.setString(21, sm.getRemarks());
            preparedStatementInsert1.executeUpdate();
            
            preparedStatementInsert2 = conn.prepareStatement(sql2);
            for(SaleSub ss : sm.getSsAl())
            {
                // Number of columns in SaleSub: 20
                /* salesid, salemid, psid, itemdid, qty, mrp, gst, exgst, ingst, amt, itemdiscamt, 
                taxableamt, cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
                preparedStatementInsert2.setInt(1, ++salesid);
                preparedStatementInsert2.setString(2, sm.getSalemid());
                preparedStatementInsert2.setInt(3, Integer.parseInt(ss.getPsid()));
                preparedStatementInsert2.setInt(4, Integer.parseInt(ss.getItemdid()));
                preparedStatementInsert2.setInt(5, Integer.parseInt(ss.getQty()));
                preparedStatementInsert2.setDouble(6, Double.parseDouble(ss.getMrp()));
                preparedStatementInsert2.setDouble(7, Double.parseDouble(ss.getGst()));
                preparedStatementInsert2.setDouble(8, Double.parseDouble(ss.getExgst()));
                preparedStatementInsert2.setDouble(9, Double.parseDouble(ss.getIngst()));
                preparedStatementInsert2.setDouble(10, Double.parseDouble(ss.getAmt()));
                preparedStatementInsert2.setDouble(11, Double.parseDouble(ss.getItemdiscamt()));
                preparedStatementInsert2.setDouble(12, Double.parseDouble(ss.getTaxableamt()));
                preparedStatementInsert2.setDouble(13, Double.parseDouble(ss.getCgstper()));
                preparedStatementInsert2.setDouble(14, Double.parseDouble(ss.getCgstamt()));
                preparedStatementInsert2.setDouble(15, Double.parseDouble(ss.getSgstper()));
                preparedStatementInsert2.setDouble(16, Double.parseDouble(ss.getSgstamt()));
                preparedStatementInsert2.setDouble(17, Double.parseDouble(ss.getIgstper()));
                preparedStatementInsert2.setDouble(18, Double.parseDouble(ss.getIgstamt()));
                preparedStatementInsert2.setDouble(19, Double.parseDouble(ss.getTotal()));
                preparedStatementInsert2.setInt(20, Integer.parseInt(ss.getRetqty()));
                preparedStatementInsert2.addBatch(); 
            }
            preparedStatementInsert2.executeBatch();
            
            preparedStatementInsert3 = conn.prepareStatement(sql3);
            for(SaleSub ss : oldSsAl)
            {
                preparedStatementInsert3.setInt(1, Integer.parseInt(ss.getSalesid()));
                preparedStatementInsert3.addBatch(); 
            }
            preparedStatementInsert3.executeBatch();

            conn.commit();
        } 
        catch (SQLException ex) 
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: updateSaleMaster ex: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            try {
                conn.rollback();
                return false;
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        } 
        finally 
        {
            if (preparedStatementInsert1 != null) 
            {
                try {
                    preparedStatementInsert1.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (preparedStatementInsert2 != null) 
            {
                try {
                    preparedStatementInsert2.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (preparedStatementInsert3 != null) 
            {
                try {
                    preparedStatementInsert3.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) 
            {
                db=null;
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return true;
    }
    
    public int updatePurchaseMaster(PurchaseMaster pm, ArrayList<PurchaseSub> oldPsAl)
    {
        dBConnection db=null;
        Connection conn = null;
        
        int psid = getMaxId("PurchaseSub", "psid");
        
        PreparedStatement preparedStatementInsert1 = null;
        PreparedStatement preparedStatementInsert2 = null;
        PreparedStatement preparedStatementInsert3 = null;

        // Number of columns in PurchaseMaster: 20
        /* pmid, ssid, compid, invno, invdt, netqty, netgross, netitemdisc, nettaxable, tradediscper, 
        nettradediscamt, replacementdiscper, netreplacementdiscamt, netgstamt, grosspayableamt, roundoff, netpayableamt, 
        isopening, amtpaid, isactive */
        String sql1 = "update PurchaseMaster set invdt=?, netqty=?, netgross=?, netitemdisc=?, nettaxable=?, "
                + "tradediscper=?, nettradediscamt=?, replacementdiscper=?, netreplacementdiscamt=?, netgstamt=?, "
                + "grosspayableamt=?, roundoff=?, netpayableamt=? where pmid=?";
        
        // Number of columns in PurchaseSub: 16
        /* psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, 
        taxableamt, gstamt, qtysold, retqty, tradediscamt, replacementdiscamt */
        String sql2 = "insert into PurchaseSub (psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, "
                + "taxableamt, gstamt, qtysold, retqty, tradediscamt, replacementdiscamt) values "
                + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        String sql3 = "delete from PurchaseSub where psid=?";

        try 
        {
            db=new dBConnection();
            conn=db.setConnection();

            conn.setAutoCommit(false);
            
            // Number of columns in PurchaseMaster: 20
            /* pmid, ssid, compid, invno, invdt, netqty, netgross, netitemdisc, nettaxable, tradediscper, 
            nettradediscamt, replacementdiscper, netreplacementdiscamt, netgstamt, grosspayableamt, roundoff, netpayableamt, 
            isopening, amtpaid, isactive */
            preparedStatementInsert1 = conn.prepareStatement(sql1);
            preparedStatementInsert1.setDate(1, java.sql.Date.valueOf(DateConverter.dateConverter1(pm.getInvdt())));
            preparedStatementInsert1.setInt(2, Integer.parseInt(pm.getNetqty()));
            preparedStatementInsert1.setDouble(3, Double.parseDouble(pm.getNetgross()));
            preparedStatementInsert1.setDouble(4, Double.parseDouble(pm.getNetitemdisc()));
            preparedStatementInsert1.setDouble(5, Double.parseDouble(pm.getNettaxable()));
            preparedStatementInsert1.setDouble(6, Double.parseDouble(pm.getTradediscper()));
            preparedStatementInsert1.setDouble(7, Double.parseDouble(pm.getNettradediscamt()));
            preparedStatementInsert1.setDouble(8, Double.parseDouble(pm.getReplacementdiscper()));
            preparedStatementInsert1.setDouble(9, Double.parseDouble(pm.getNetreplacementdiscamt()));
            preparedStatementInsert1.setDouble(10, Double.parseDouble(pm.getNetgstamt()));
            preparedStatementInsert1.setDouble(11, Double.parseDouble(pm.getGrosspayableamt()));
            preparedStatementInsert1.setDouble(12, Double.parseDouble(pm.getRoundoff()));
            preparedStatementInsert1.setDouble(13, Double.parseDouble(pm.getNetpayableamt()));
            preparedStatementInsert1.setInt(14, Integer.parseInt(pm.getPmid()));
            preparedStatementInsert1.executeUpdate();
            
            preparedStatementInsert2 = conn.prepareStatement(sql2);
            for(PurchaseSub ps : pm.getPsAl())
            {
                // Number of columns in PurchaseSub: 16
                /* psid, pmid, itemdid, gst, exgst, ingst, mrp, qty, gross, discamt, 
                taxableamt, gstamt, qtysold, retqty, tradediscamt, replacementdiscamt */
                preparedStatementInsert2.setInt(1, ++psid);
                preparedStatementInsert2.setInt(2, Integer.parseInt(pm.getPmid()));
                preparedStatementInsert2.setInt(3, Integer.parseInt(ps.getItemdid()));
                preparedStatementInsert2.setDouble(4, Double.parseDouble(ps.getGst()));
                preparedStatementInsert2.setDouble(5, Double.parseDouble(ps.getExgst()));
                preparedStatementInsert2.setDouble(6, Double.parseDouble(ps.getIngst()));
                preparedStatementInsert2.setDouble(7, Double.parseDouble(ps.getMrp()));
                preparedStatementInsert2.setInt(8, Integer.parseInt(ps.getQty()));
                preparedStatementInsert2.setDouble(9, Double.parseDouble(ps.getGross()));
                preparedStatementInsert2.setDouble(10, Double.parseDouble(ps.getDiscamt()));
                preparedStatementInsert2.setDouble(11, Double.parseDouble(ps.getTaxableamt()));
                preparedStatementInsert2.setDouble(12, Double.parseDouble(ps.getGstamt()));
                preparedStatementInsert2.setInt(13, Integer.parseInt(ps.getQtysold()));
                preparedStatementInsert2.setInt(14, Integer.parseInt(ps.getRetqty()));
                preparedStatementInsert2.setDouble(15, Double.parseDouble(ps.getTradediscamt()));
                preparedStatementInsert2.setDouble(16, Double.parseDouble(ps.getReplacementdiscamt()));
                preparedStatementInsert2.addBatch(); 
            }
            preparedStatementInsert2.executeBatch();
            
            preparedStatementInsert3 = conn.prepareStatement(sql3);
            for(PurchaseSub ss : oldPsAl)
            {
                preparedStatementInsert3.setInt(1, Integer.parseInt(ss.getPsid()));
                preparedStatementInsert3.addBatch(); 
            }
            preparedStatementInsert3.executeBatch();

            conn.commit();
        } 
        catch (SQLException ex) 
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: updatePurchaseMaster ex: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            try {
                conn.rollback();
                return 0;
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        } 
        finally 
        {
            if (preparedStatementInsert1 != null) {
                try {
                    preparedStatementInsert1.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (preparedStatementInsert2 != null) {
                try {
                    preparedStatementInsert2.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (preparedStatementInsert3 != null) {
                try {
                    preparedStatementInsert3.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) 
            {
                db=null;
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return Integer.parseInt(pm.getPmid());
    }
}
