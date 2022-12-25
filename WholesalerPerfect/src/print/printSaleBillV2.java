package print;

import conn.dBConnection;
import dto.Enterprise;
import dto.Retailer;
import dto.SaleMasterV2;
import dto.SaleSubV2;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import utilities.MyNumberFormat;
import utilities.NumberToString;

public class printSaleBillV2 {
    
    private SaleMasterV2 sm;
    private Enterprise e;
    private Retailer r;
    private int noOfPages;
    private int noofrecStartnend=13;
    private boolean flag;
    private String fontNm = "Courier New";
    private DecimalFormat format = new DecimalFormat("0.#");
    private int sln=0;
    private NumberToString nts = new NumberToString();
    
    public printSaleBillV2(SaleMasterV2 sm, Enterprise e, Retailer r) {
        this.sm = sm;
        this.e = e;
        this.r = r;
        PrinterJob printJob = PrinterJob.getPrinterJob();
        PageFormat pPageFormat = printJob.defaultPage();
        Paper pPaper =new Paper();
//        pPaper.setSize(595, 842/2);
        pPaper.setSize(595, 842); // For New Printer Version
        pPaper.setImageableArea(1.0, 1.0, pPaper.getWidth() - 2.0, pPaper.getHeight() - 2.0);
        pPageFormat.setPaper(pPaper);
        Book book = new Book();
        pPageFormat.setOrientation(PageFormat.PORTRAIT);
        
        // Getting no. of records
        int totnoofrecords = sm.getSsAl().size();
        for ( int i = 0; i < totnoofrecords; i += noofrecStartnend ) {
            noOfPages ++;
            if ( i+noofrecStartnend >= totnoofrecords ) {
                flag = true;
            }
            book.append(new printSaleBillV2.DocumentStartNEnd(i, i+noofrecStartnend>totnoofrecords?
                    totnoofrecords-1:i+noofrecStartnend-1, flag), pPageFormat);
        }
        printJob.setPageable(book);

        // Print with dialog
        if (printJob.printDialog()) {
            try {
                printJob.print();
            } catch (Exception PrintException) {
                JOptionPane.showMessageDialog(null,"Printing Exception: "+PrintException.getMessage(),
                        "Printing Error",JOptionPane.ERROR_MESSAGE);
                PrintException.printStackTrace();
            }
        }

//        //Silent Print Option
//        try
//        {
//            printJob.print();
//        }
//        catch (Exception PrintException)
//        {
//            JOptionPane.showMessageDialog(null,"Printing Exception: "+PrintException.getMessage(),"Printing Error",JOptionPane.ERROR_MESSAGE);
//            PrintException.printStackTrace();
//        }
    }
    
    private class DocumentStartNEnd implements Printable {
        private boolean print2wiseFlag;
        private int counter;
        
        private int recbegin;
        private int recstop;
        private boolean lastPageFlag;
        
        public DocumentStartNEnd(int recbegin, int recstop, boolean lastPageFlag) {
            this.recbegin = recbegin;
            this.recstop = recstop;
            this.lastPageFlag=lastPageFlag;
        }
        
        @Override
        public int print(Graphics g, PageFormat pageFormat, int page) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            g2d.setPaint(Color.black);
            if(counter!=0) {
                print2wiseFlag=true;
            }
            counter++;

            if(true) {
                return myPrint(g2d, pageFormat, page);
            }
            
            return (PAGE_EXISTS);
        }
        
        private int myPrint(Graphics2D g2d, PageFormat pageFormat, int page) {
            int leftbound=13;
            int rightbound=571;
            int middleBound=250;
            FontMetrics fontMetrics = null;
            String titleText = null;
            Font font = null;
            double titleX = 0;
            double titleY = 0;
            // _ 104
            String horizontalFullLineLines = "---------------------------------------------------------------------------------------------------------";
            // _ 117
            String horizontalFullLineLines8 = "----------------------------------------------------------------------------------------------------------------------";
            
            font = new Font(fontNm, Font.BOLD, 14);
            g2d.setFont(font);
//            fontMetrics = g2d.getFontMetrics();
            g2d.setPaint(Color.BLACK);
            titleText = e.getEname();
            titleX = leftbound;
            titleY = 25;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            font = new Font(fontNm, Font.BOLD, 12);
            g2d.setFont(font);
            titleText = "TAX INVOICE";
            titleX = rightbound-middleBound;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Distributer Street & City
            font = new Font(fontNm, Font.PLAIN, 9);
            g2d.setFont(font);
//            fontMetrics = g2d.getFontMetrics();
            titleText = e.getEstreet() + ", " + e.getEcity();
            titleX = leftbound;
            titleY+=12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Invoice No.
            titleText ="INVOICE NO. : " + sm.getSalemid();
            titleX = rightbound-middleBound;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // DISTRIBUTER DIST. & PIN
            titleText = e.getEdist() + ", " + e.getEpin() + ", EMAIL: " + e.getEmail();
            titleX = leftbound;
            titleY+=11;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // INVOICE DATE & DELIVERY DATE
            titleText = "INVOICE DATE: "+ sm.getSaledt()+ " SLP. DATE : "
                    + (sm.getSupplydt().equals("01/01/2000")?" ":sm.getSupplydt());
            titleX = rightbound-middleBound;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // DISTRIBUTER STATE NAME. & STATE CODE
            titleText = "STATE NAME: " + e.getEstate() + ", STATE CODE : " + e.getEstatecode();
            titleX = leftbound;
            titleY+=11;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // ORDER NO. & ORDER DATE
            titleText = "ORDER NO.   : " + (sm.getOrdno().equals("N/A")?" ":sm.getOrdno())
                    + " ORDER DATE : " + (sm.getOrddt().equals("01/01/2000")?" ":sm.getOrddt());
            titleX = rightbound-middleBound;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // DISTRIBUTER PHONE & GSTIN
            titleText = "PH: "+e.getEcontact();
            titleX = leftbound;
            titleY+=11;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            font = new Font(fontNm, Font.BOLD, 9);
            g2d.setFont(font);
            fontMetrics = g2d.getFontMetrics();
            titleX = leftbound+fontMetrics.stringWidth(titleText);
            titleText = "     FIRM GSTN: " + e.getEgstno();
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // PAYMENT TERM & PAGE NO.
            font = new Font(fontNm, Font.PLAIN, 9);
            g2d.setFont(font);
//            fontMetrics = g2d.getFontMetrics();
            titleText = "PAY. TERM   : " + (sm.getPaymentterm().equals("N/A")?" ":sm.getPaymentterm())
                    + " PAGE NO. : " + (page+1)+"/"+noOfPages;
            titleX = rightbound-middleBound;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // 1ST HORIZONTAL LINE
            titleText = horizontalFullLineLines;
            titleX = leftbound;
            titleY+=11;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Details of Receiver (Billed to)
            titleText = "Details of Receiver (Billed to)";
            titleX = leftbound;
            titleY+=11;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Details of Consignee (Shipped to)
            titleText = "Details of Consignee (Shipped to)";
            titleX = rightbound-middleBound;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Consignee Name(Billed to)
            font = new Font(fontNm, Font.BOLD, 9);
            g2d.setFont(font);
//            fontMetrics = g2d.getFontMetrics();
            titleText = r.getRetnm();
            titleX = leftbound;
            titleY+=11;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Consignee Name(Shipped to)
            titleText = r.getRetnm();
            titleX = rightbound-middleBound;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Consignee Street & City(Billed to)
            font = new Font(fontNm, Font.PLAIN, 9);
            g2d.setFont(font);
//            fontMetrics = g2d.getFontMetrics();
            titleText = (r.getRstreet().equals("N/A")?" ":r.getRstreet()) + "   " 
                    + (r.getRcity().equals("N/A")?" ":r.getRcity());
            titleX = leftbound;
            titleY+=11;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Consignee Street & City(Shipped to)
            titleText = (r.getRstreet().equals("N/A")?" ":r.getRstreet()) + "   " 
                    + (r.getRcity().equals("N/A")?" ":r.getRcity());
            titleX = rightbound-middleBound;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Consignee State & State Code(Billed to)
            titleText = "STATE : " + (r.getRstate().equals("N/A")?" ":r.getRstate()) 
                    + "   STATE CODE : "+(r.getRstatecode().equals("N/A")?" ":r.getRstatecode());
            titleX = leftbound;
            titleY+=11;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Consignee State & State Code(Shipped to)
            titleText = "STATE : " + (r.getRstate().equals("N/A")?" ":r.getRstate()) 
                    + "   STATE CODE : " + (r.getRstatecode().equals("N/A")?" ":r.getRstatecode());
            titleX = rightbound-middleBound;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Consignee GSTIN & PAN NO.(Billed to)
            font = new Font(fontNm, Font.BOLD, 9);
            g2d.setFont(font);
//            fontMetrics = g2d.getFontMetrics();
            titleText = "GSTIN : " + (r.getRgstno().equals("N/A")?" ":r.getRgstno()) 
                    + "   PAN NO. : " + (r.getRpanno().equals("N/A")?" ":r.getRpanno());
            titleX = leftbound;
            titleY+=11;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Consignee GSTIN & PAN NO.(Shipped to)
            titleText = "GSTIN : " + (r.getRgstno().equals("N/A")?" ":r.getRgstno()) 
                    + "   PAN NO. : "+(r.getRpanno().equals("N/A")?" ":r.getRpanno());
            titleX = rightbound-middleBound;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Consignee PHONE & MAIL(Billed to)
            font = new Font(fontNm, Font.PLAIN, 9);
            g2d.setFont(font);
//            fontMetrics = g2d.getFontMetrics();
            titleText = "PHONE : " + (r.getRcontact().equals("N/A")?" ":r.getRcontact()) 
                    + "   EMAIL. : "+(r.getRmail().equals("N/A")?" ":r.getRmail());
            titleX = leftbound;
            titleY+=11;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Consignee PHONE & MAIL(Shipped to)
            titleText = "PHONE : " + (r.getRcontact().equals("N/A")?" ":r.getRcontact()) 
                    + "   EMAIL. : " + (r.getRmail().equals("N/A")?" ":r.getRmail());
            titleX = rightbound-middleBound;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // 2ND HORIZONTAL LINE
            font = new Font(fontNm, Font.PLAIN, 8);
            g2d.setFont(font);
            fontMetrics = g2d.getFontMetrics();
            titleText = horizontalFullLineLines8;
            titleX = leftbound;
            titleY+=11;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // _117
            titleText = "SN|      DESCRIPTION      | MRP|   HSN  |QTY|FRE|    RATE|  ITM DISC. | CD DISC.|    CGST    |    SGST    |GROSS AMT.";
            titleX = leftbound;
            titleY+=11;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            titleText = "  |                       |    |        |   |   |        |  %|    AMT.|     AMT.|  %|    AMT.|  %|    AMT.|";
            titleX = leftbound;
            titleY+=11;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // 3RD HORIZONTAL LINE
            titleText = horizontalFullLineLines8;
            titleX = leftbound;
            titleY+=11;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            ArrayList<SaleSubV2> ssAl = sm.getSsAl();
            int lineNo = 0;
            for ( int j = recbegin; j <= recstop; j++ ) {
                titleY += 11;
                SaleSubV2 ss = ssAl.get(j);
                String itemnm = "";
                String mrp = "";
                String hsn = "";
                String munm = "";
                // Item Name, HSN, Measuring
                if(print2wiseFlag) {
                    sln++;
                    lineNo++;
                    // Number of columns in ItemMaster: 5
                    /* itemmid, compid, itemnm, hsn, isactive */
                    // Number of columns in ItemDetails: 10
                    /* itemdid, itemmid, mrp, gst, pexgst, pingst, sexgst, singst, onhand, isactive */
                    String query="select itemnm, hsn from ItemMaster, ItemDetails"
                            + " where ItemMaster.itemmid=ItemDetails.itemmid and itemdid="+ss.getItemdid();
                    System.out.println(query);
                    dBConnection db=new dBConnection();
                    Connection conn=db.setConnection();
                    try {
                        Statement stm=conn.createStatement();
                        ResultSet rs=stm.executeQuery(query);
                        if(rs.next()) {
                            itemnm = rs.getString("itemnm").replace("\\'", "'");
                            mrp = format.format(Double.parseDouble(ss.getMrp()));
                            hsn = rs.getString("hsn");
                        }
                    } catch(SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null,"PrintSaleBillV2 ex= "+ex,
                                "Error Found",JOptionPane.ERROR_MESSAGE);
                    } finally {
                        try {
                            if (conn!=null) conn.close();
                        } catch(SQLException ex){}
                    }
                }
                
                // SN|      DESCRIPTION      | MRP|   HSN  |QTY|FRE|    RATE|  ITM DISC. | CD DISC.|    CGST    |    SGST    |GROSS AMT.
                //   |                       |    |        |   |   |        |  %|    AMT.|     AMT.|  %|    AMT.|  %|    AMT.|
                // SN
                titleX = leftbound;
                titleText = sln+spaceCompute(String.valueOf(sln).length(),2)+"|";
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // DESCRIPTION
                titleX = titleX+fontMetrics.stringWidth(titleText);
                titleText = itemnm+spaceCompute(itemnm.length(),23)+"|";
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // MRP
                titleX = titleX+fontMetrics.stringWidth(titleText);
                titleText = mrp+spaceCompute(mrp.length(),4)+"|";
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // HSN
                titleX = titleX+fontMetrics.stringWidth(titleText);
                titleText = hsn+spaceCompute(hsn.length(),8)+"|";
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // SN|      DESCRIPTION      | MRP|   HSN  |QTY|FRE|    RATE|  ITM DISC. | CD DISC.|    CGST    |    SGST    |GROSS AMT.
                //   |                       |    |        |   |   |        |  %|    AMT.|     AMT.|  %|    AMT.|  %|    AMT.|
                // QTY
                titleX = titleX+fontMetrics.stringWidth(titleText);
                String sqty = format.format(Double.parseDouble(ss.getQty()));
                titleText = spaceCompute(sqty.length(),3)+sqty+"|";
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // FRE
                titleX = titleX+fontMetrics.stringWidth(titleText);
                String sfree = format.format(Double.parseDouble(ss.getFree()));
                titleText = spaceCompute(sfree.length(),3)+sfree+"|";
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // RATE
                titleX = titleX+fontMetrics.stringWidth(titleText);
                String srate = MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getRate()));
                titleText = spaceCompute(srate.length(),8)+srate+"|";
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // SN|      DESCRIPTION      | MRP|   HSN  |QTY|FRE|    RATE|  ITM DISC. | CD DISC.|    CGST    |    SGST    |GROSS AMT.
                //   |                       |    |        |   |   |        |  %|    AMT.|     AMT.|  %|    AMT.|  %|    AMT.|
                // ITM DISC %
                titleX = titleX+fontMetrics.stringWidth(titleText);
                String itemdiscper = Double.parseDouble(ss.getItemdiscper())>0?format.format(Double.parseDouble(ss.getItemdiscper())):"0";
                titleText = spaceCompute(itemdiscper.length(),3)+itemdiscper+"|";
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // ITM DISC.
                titleX = titleX+fontMetrics.stringWidth(titleText);
//                String sitemdisc = Double.parseDouble(ss.getItemdiscamt())>0?("-"+MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getItemdiscamt()))):"0";
                String sitemdisc = Double.parseDouble(ss.getItemdiscamt())>0?MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getItemdiscamt())):"0";
                titleText = spaceCompute(sitemdisc.length(),8)+sitemdisc+"|";
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // CD DISC.
                titleX = titleX+fontMetrics.stringWidth(titleText);
//                String scddisc = Double.parseDouble(ss.getCashdiscamt())>0?("-"+MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getCashdiscamt()))):"0";
                String scddisc = Double.parseDouble(ss.getCashdiscamt())>0?MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getCashdiscamt())):"0";
                titleText = spaceCompute(scddisc.length(),9)+scddisc+"|";
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // GST Calculation
                double each_gst_per = Double.parseDouble(ss.getGst()) / 2.0;
                double each_gst_amt = Double.parseDouble(ss.getGstamt()) / 2.0;
                
                // CGST %
                // SN|      DESCRIPTION      | MRP|   HSN  |QTY|FRE|    RATE|  ITM DISC. | CD DISC.|    CGST    |    SGST    |GROSS AMT.
                //   |                       |    |        |   |   |        |  %|    AMT.|     AMT.|  %|    AMT.|  %|    AMT.|
                titleX = titleX+fontMetrics.stringWidth(titleText);
                String scgstper = format.format(each_gst_per);
                titleText = spaceCompute(scgstper.length(),3)+scgstper+"|";
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // CGST AMT.
                // SN|      DESCRIPTION      | MRP|   HSN  |QTY|FRE|    RATE|  ITM DISC. | CD DISC.|    CGST    |    SGST    |GROSS AMT.
                //   |                       |    |        |   |   |        |  %|    AMT.|     AMT.|  %|    AMT.|  %|    AMT.|
                titleX = titleX+fontMetrics.stringWidth(titleText);
                String scgstamt = MyNumberFormat.rupeeFormat(each_gst_amt);
                titleText = spaceCompute(scgstamt.length(),8)+scgstamt+"|";
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // SGST %
                // SN|      DESCRIPTION      | MRP|   HSN  |QTY|FRE|    RATE|  ITM DISC. | CD DISC.|    CGST    |    SGST    |GROSS AMT.
                //   |                       |    |        |   |   |        |  %|    AMT.|     AMT.|  %|    AMT.|  %|    AMT.|
                titleX = titleX+fontMetrics.stringWidth(titleText);
                String ssgstper = format.format(each_gst_per);
                titleText = spaceCompute(ssgstper.length(),3)+ssgstper+"|";
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // SGST AMT.
                // SN|      DESCRIPTION      | MRP|   HSN  |QTY|FRE|    RATE|  ITM DISC. | CD DISC.|    CGST    |    SGST    |GROSS AMT.
                //   |                       |    |        |   |   |        |  %|    AMT.|     AMT.|  %|    AMT.|  %|    AMT.|
                titleX = titleX+fontMetrics.stringWidth(titleText);
                String ssgstamt = MyNumberFormat.rupeeFormat(each_gst_amt);
                titleText = spaceCompute(ssgstamt.length(),8)+ssgstamt+"|";
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // GROSS AMT.
                // SN|      DESCRIPTION      | MRP|   HSN  |QTY|FRE|    RATE|  ITM DISC. | CD DISC.|    CGST    |    SGST    |GROSS AMT.
                //   |                       |    |        |   |   |        |  %|    AMT.|     AMT.|  %|    AMT.|  %|    AMT.|
                titleX = titleX+fontMetrics.stringWidth(titleText);
                String stotal = MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getGross()));
                titleText = spaceCompute(stotal.length(),10)+stotal                      ;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
            }
            for (int i = 1; i < noofrecStartnend - lineNo; i++) {
                titleY += 11;
            }
            
            // 4TH HORIZONTAL LINE
            titleText = horizontalFullLineLines8;
            titleX = leftbound;
            titleY+=11;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // *********************** Begin last page summary
            if(lastPageFlag) {
                font = new Font(fontNm, Font.BOLD, 8);
                g2d.setFont(font);
                fontMetrics = g2d.getFontMetrics();
                titleX = leftbound;
                titleY += 10.5;
                double subtotal=Double.parseDouble(MyNumberFormat.rupeeFormat(Double.parseDouble(
                        sm.getNetpayableamt())).replaceAll(",", ""));
                int m=(int)subtotal;
                int s=(int)((subtotal-m)*100.0);
                String amtinword = "";
                if(s==0) {
                    amtinword = "TOTAL VALUE (IN WORD): " + nts.numberToString(m).toUpperCase() + "Only.";
                } else {
                    amtinword = "TOTAL VALUE (IN WORD): " + nts.numberToString(m).toUpperCase() + "and " 
                            + nts.numberToString(s).toUpperCase() + "Paise Only.";
                }
                g2d.drawString(amtinword, (int) titleX, (int) titleY);
                
                // Taxable Amt. Calculation
                double netTaxableAmt = Double.parseDouble(sm.getNetgross());
                
                font = new Font(fontNm, Font.PLAIN, 8);
                g2d.setFont(font);
                fontMetrics = g2d.getFontMetrics();
                titleText = "TAXABLE AMT." + spaceCompute("TAXABLE AMT.".length(),16)+":-";
                titleX = rightbound-middleBound+90;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                titleText = MyNumberFormat.rupeeFormat(netTaxableAmt);
                titleX = rightbound - fontMetrics.stringWidth(titleText) + 5;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // Bank details
                titleText = "BANK DETAILS: BANK OF INDIA A/C NO. 410820110000045 IFSC NO. BKID0004108";
                titleY += 10.5;
                titleX = leftbound;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // ITEM DISC. AMT.
                titleText = "ITEM DISC. AMT."+spaceCompute("ITEM DISC. AMT.".length(),16)+":-";
                titleX = rightbound-middleBound+90;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                titleText = "-"+MyNumberFormat.rupeeFormat(Double.parseDouble(sm.getNetitemdiscamt()));
                titleX = rightbound - fontMetrics.stringWidth(titleText) + 5;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                titleText = "REMARKS: "+(sm.getRemarks().equals("N/A")?"":sm.getRemarks());
                titleX = leftbound;
                titleY += 10.5;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // CD % AND CD AMT.
                titleText = "CD: "+format.format(Double.parseDouble(sm.getCashdiscper()))+"% AMT."
                        +spaceCompute(("CD: "+format.format(Double.parseDouble(sm.getCashdiscper()))+"% CD AMT.").length(),19)+":-";
                titleX = rightbound-middleBound+90;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                titleText = "-"+MyNumberFormat.rupeeFormat(Double.parseDouble(sm.getNetcashdiscamt()));
                titleX = rightbound - fontMetrics.stringWidth(titleText) + 5;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // GST AMT.
                titleText = "GST AMT."+spaceCompute("GST AMT.".length(),16)+":-";
                titleX = rightbound-middleBound+90;
                titleY += 10.5;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                titleText = "+"+MyNumberFormat.rupeeFormat(Double.parseDouble(sm.getNetgstamt()));
                titleX = rightbound - fontMetrics.stringWidth(titleText) + 5;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                titleText = "ROUNDOFF"+spaceCompute("ROUNDOFF".length(),16)+":-";
                titleY += 10.5;
                titleX = rightbound-middleBound+90;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                titleText = MyNumberFormat.rupeeFormat(Double.parseDouble(sm.getRoundoff()));
                titleX = rightbound - fontMetrics.stringWidth(titleText) + 5;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                titleText = "DISP. SCHM. AMT."+spaceCompute("DISP. SCHM. AMT.".length(),11)+":-";
                titleY += 10.5;
                titleX = rightbound-middleBound+90;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                titleText = "-"+MyNumberFormat.rupeeFormat(Double.parseDouble(sm.getDispscheme()));
                titleX = rightbound - fontMetrics.stringWidth(titleText) + 5;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                font = new Font(fontNm, Font.BOLD, 8);
                g2d.setFont(font);
                fontMetrics = g2d.getFontMetrics();
                titleText = "FOR "+e.getEname();
                titleY += 10.5;
                titleX = leftbound;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                titleText = "NET AMOUNT"+spaceCompute("NET AMOUNT".length(),16)+":-";
                titleX = rightbound-middleBound+90;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                titleText = MyNumberFormat.rupeeFormat(Double.parseDouble((sm.getNetpayableamt())));
                titleX = rightbound - fontMetrics.stringWidth(titleText) + 5;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
            } else {
                titleText = "CONTINUED TO PAGE: "+(page+2);
//                titleX = rightbound - fontMetrics.stringWidth(titleText) - 5;
                titleX = leftbound;
                titleY += 11;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
            }
            
            return (PAGE_EXISTS);
        }
        
        private String spaceCompute(int len, int totlen) {
            StringBuilder sb = new StringBuilder();
            int remaining = totlen - len;
            for (int i = 1; i <= remaining; i++) {
                sb.append(" ");
            }
            return sb.toString();
        }
    }
}
