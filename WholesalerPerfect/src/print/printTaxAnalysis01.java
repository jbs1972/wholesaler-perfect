package print;

import conn.dBConnection;
import dto.Enterprise;
import dto.Retailer;
import dto.SaleMasterV2;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import utilities.MyNumberFormat;

public class printTaxAnalysis01 {
    
    private DecimalFormat format1 = new DecimalFormat("0.#");
    private SaleMasterV2 sm;
    private Enterprise e;
    private Retailer r;
    private String fontNm = "Arial";
    
    public printTaxAnalysis01(SaleMasterV2 sm, Enterprise e, Retailer r) {
        this.sm = sm;
        this.e = e;
        this.r = r;
        PrinterJob printJob = PrinterJob.getPrinterJob();
        PageFormat pPageFormat = printJob.defaultPage();
        Paper pPaper =new Paper();
        pPaper.setSize(595, 842);
        pPaper.setImageableArea(1.0, 1.0, pPaper.getWidth() - 2.0, pPaper.getHeight() - 2.0);
        pPageFormat.setPaper(pPaper);
        Book book = new Book();
        pPageFormat.setOrientation(PageFormat.PORTRAIT);
        
        book.append(new IntroPage(), pPageFormat);
        printJob.setPageable(book);

        // Print with dialog
        if (printJob.printDialog()) {
            try {
                printJob.print();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"printTaxAnalysis01 ex?= "+ex,"Error Found",JOptionPane.ERROR_MESSAGE);
            }
        }

        //Silent Print Option
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
    
    private class IntroPage implements Printable {
        
        private int counter;
        private boolean print2wiseFlag;

        @Override
        public int print(Graphics g, PageFormat pageFormat, int page) {
            int leftbound=50;
            int rightbound=545;
            Font titleFont =null;
            FontMetrics fontMetrics = null;
            String titleText = null;

            if(counter!=0) {
                print2wiseFlag=true;
            }
            counter++;

            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            
            Font font = new Font(fontNm, Font.BOLD, 14);
            Map<TextAttribute, Object> map = new HashMap<TextAttribute, Object>();
            map.put(TextAttribute.FONT, font);
            map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            font = Font.getFont(map);
            g2d.setFont(font);
            fontMetrics = g2d.getFontMetrics();
            g2d.setPaint(Color.BLUE);
            titleText = "INVOICE";
            double titleX = (pageFormat.getImageableWidth() / 2) - (fontMetrics.stringWidth(titleText) / 2);
            double titleY=40;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            titleFont = new Font(fontNm, Font.PLAIN, 10);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            g2d.setPaint(Color.black);
            
            titleText = "(TAX ANALYSIS)";
            titleX = (pageFormat.getImageableWidth() / 2) - (fontMetrics.stringWidth(titleText) / 2);
            titleY += 18;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Invoice No. - Left
            titleFont = new Font("Arial", Font.BOLD, 10);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "Inv. No.: ";
            titleX = leftbound;
            titleY+=25;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 10);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = sm.getSalemid();
            titleX = leftbound + fontMetrics.stringWidth("Inv. No.: ");
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Invoice Date - Right
            titleFont = new Font("Arial", Font.BOLD, 10);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "Inv. Dt.: ";
            titleX = rightbound - fontMetrics.stringWidth("Inv. Dt.: " + sm.getSaledt());
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 10);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = sm.getSaledt();
            titleX = rightbound - fontMetrics.stringWidth(titleText);
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // SELLER INFORMATION - Left [Heading]
            font = new Font(fontNm, Font.PLAIN, 12);
            map = new HashMap<TextAttribute, Object>();
            map.put(TextAttribute.FONT, font);
            map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            font = Font.getFont(map);
            g2d.setFont(font);
            fontMetrics = g2d.getFontMetrics();
            g2d.setPaint(Color.BLUE);
            titleText = "SELLER INFORMATION";
            titleX = leftbound;
            titleY+=25;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // RETAILER INFORMATION - Right [Heading]
            titleText = "RETAILER INFORMATION";
            titleX = rightbound - fontMetrics.stringWidth("RETAILER INFORMATION");
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // SELLER NAME - Left
            titleFont = new Font("Arial", Font.BOLD, 10);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            g2d.setPaint(Color.BLACK);
            titleText = e.getEname();
            titleX = leftbound;
            titleY+=15;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // RETAILER NAME - Right
            titleText = r.getRetnm();
            titleX = rightbound - fontMetrics.stringWidth(r.getRetnm());
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Distributer Street
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = e.getEstreet();
            titleX = leftbound;
            titleY+=12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Retailer Street - Right
            titleText = r.getRstreet().equals("N/A")?"":r.getRstreet();
            titleX = rightbound - fontMetrics.stringWidth(r.getRstreet());
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Distributer Dist. & Pin.
            titleText = e.getEdist()+" - "+e.getEpin();
            titleX = leftbound;
            titleY+=12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Retailer Dist. & Pin. - Right
            titleText = (r.getRdist().equals("N/A")?"":r.getRdist())+" - "+(r.getRpin().equals("N/A")?"":r.getRpin());
            titleX = rightbound - fontMetrics.stringWidth(r.getRdist()+" - "+r.getRpin());
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Distributer State & Country
            titleText = e.getEstate()+" - "+e.getEcountry();
            titleX = leftbound;
            titleY+=12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Retailer State & Country - Right
            titleText = (r.getRstate().equals("N/A")?"":r.getRstate())+" - "+(r.getRcountry().equals("N/A")?"":r.getRcountry());
            titleX = rightbound - fontMetrics.stringWidth(r.getRstate()+" - "+r.getRcountry());
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Distributer Phone & Fax
            titleText = "Ph. No. - "+e.getEcontact();
            titleX = leftbound;
            titleY+=12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleText = "Email - "+(e.getEmail().equals("N/A")?"":e.getEmail());
            titleX = 170;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Retailer Phone - Right
            titleText = "Ph. No. - "+(r.getRcontact().equals("N/A")?"":r.getRcontact());
            titleX = rightbound - fontMetrics.stringWidth(titleText);
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Distributer GSTIN & GST Regn. No.
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "GSTIN: ";
            titleX = leftbound;
            titleY+=12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = e.getEgstno();
            titleX = leftbound+fontMetrics.stringWidth("GSTIN: ");
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "GST Regn. Type: REGULAR";
            titleX = 170;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = e.getEgstregntype().equals("N/A")?"":e.getEgstregntype();
            titleX = 170+fontMetrics.stringWidth("GST Regn. Type: ");
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Retailer GSTIN - Right
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "GSTIN: ";
            titleX = rightbound - fontMetrics.stringWidth("GSTIN: "+r.getRgstno());
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = r.getRgstno().equals("N/A")?"":r.getRgstno();
            titleX = rightbound - fontMetrics.stringWidth(r.getRgstno());
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Distributer GST State & State code
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "GST State: ";
            titleX = leftbound;
            titleY+=12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = e.getEstate();
            titleX = leftbound+fontMetrics.stringWidth("GST State: ");
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "State Code: ";
            titleX = 170;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = e.getEstatecode();
            titleX = 170+fontMetrics.stringWidth("State Code: ");
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Retailer GST State & State code
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "GST State: ";
            titleX = rightbound - fontMetrics.stringWidth("GST State: "+r.getRstate()+".    State Code: "+r.getRstatecode());
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            // ******************************
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = r.getRstate().equals("N/A")?"":r.getRstate();
            titleX = rightbound - fontMetrics.stringWidth(r.getRstate()+".    State Code: "+r.getRstatecode());
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            // ******************************
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = ".    State Code: ";
            titleX = rightbound - fontMetrics.stringWidth(".    State Code: "+r.getRstatecode());
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            // ******************************
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = r.getRstatecode().equals("N/A")?"":r.getRstatecode();
            titleX = rightbound - fontMetrics.stringWidth(r.getRstatecode());
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Tax Analysis
            g2d.setPaint(Color.LIGHT_GRAY);
            //Outer Rectangle
            g2d.drawRoundRect(leftbound, (int)(titleY+20), 495, 160, 8, 8);
            //1st Horizontal Line within rectangle
            g2d.drawLine(leftbound, (int)(titleY+50), rightbound, (int)(titleY+50));
            //2nd Horizontal Line within rectangle
            g2d.drawLine(leftbound, (int)(titleY+155), rightbound, (int)(titleY+155));
            
            /*
            int leftbound=50;
            int rightbound=545;
            */
            // No. Of Columns - 7
            int endposes[] = {40/*sln-90*/, 100/*Taxable Amt.-190*/, 50/*CGST %-240*/, 80/*CGST Amt.-320*/,
                50/*SGST %-370*/, 80/*SGST Amt.-450*/, 95/*Tax Total-845*/};
            int prev = 0;
            for ( int i = 0; i < endposes.length-1; i++ ) {
                g2d.drawLine(prev+endposes[i]+50, (int)(titleY+20), prev+endposes[i]+50, (int)(titleY+180));
                prev += endposes[i];
            }
            
            // NO. OF COLUMNS: 7
            /* S No., Taxable Amt., CGST %, CGST Amt., SGST %, SGST Amt., Tax Total */
            String headingLine1[] = {"S", "", "CGST", "CGST", "SGST", "SGST", "Tax"};
            String headingLine2[] = {"", "Taxable Amt.", "", "", "", "", ""};
            String headingLine3[] = {"No.", "", "%", "Amt.", "%", "Amt.", "Total"};
            
            g2d.setPaint(Color.BLUE);
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            
            int startpos = leftbound;
            int endpos = startpos + endposes[0];
            titleY = 227;
            int i = 0;
            for ( ; i < endposes.length - 1; i++ ) {
                titleText = headingLine1[i];
                titleX = startpos + ((endposes[i] - fontMetrics.stringWidth(titleText)) / 2);
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                startpos = endpos; 
                endpos += endposes[i + 1];
            }
            titleText = headingLine1[i];
            titleX = startpos + ((endposes[i] - fontMetrics.stringWidth(titleText)) / 2);
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            startpos = leftbound;
            endpos = startpos + endposes[0];
            titleY = 232;
            i = 0;
            for ( ; i < endposes.length - 1; i++ ) {
                titleText = headingLine2[i];
                titleX = startpos + ((endposes[i] - fontMetrics.stringWidth(titleText)) / 2);
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                startpos = endpos; 
                endpos += endposes[i + 1];
            }
            titleText = headingLine2[i];
            titleX = startpos + ((endposes[i] - fontMetrics.stringWidth(titleText)) / 2);
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            startpos = leftbound;
            endpos = startpos + endposes[0];
            titleY = 237;
            i = 0;
            for ( ; i < endposes.length - 1; i++ ) {
                titleText = headingLine3[i];
                titleX = startpos + ((endposes[i] - fontMetrics.stringWidth(titleText)) / 2);
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                startpos = endpos; 
                endpos += endposes[i + 1];
            }
            titleText = headingLine3[i];
            titleX = startpos + ((endposes[i] - fontMetrics.stringWidth(titleText)) / 2);
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Tax Analysis Calculation
            g2d.setPaint(Color.BLACK);
            if(print2wiseFlag) {
                // Number of columns in SaleSub: 19
                /* salesid, salemid, psid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, 
                cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
                /*
                String query="select sum(taxableamt) as tottaxableamt, cgstper, sum(cgstamt) as "
                        + "totcgstamt, sgstper, sum(sgstamt) as totsgstamt from SaleSub where "
                        + "salemid='"+sm.getSalemid()+"' group by cgstper, sgstper order by cgstper";
                */
                // Table SaleSubV2, no. of columns - 16
                /*
                salesid, salemid, psid, itemdid, mrp, gst, qty, free, rate, gross, itemdiscper, 
                itemdiscamt, cashdiscamt, gstamt, amount, retqty
                */
                String query="select sum((qty*rate)-itemdiscamt-cashdiscamt) as tottaxableamt, "
                        + "(gst/2.0) as cgstper, sum(gstamt/2.0) as totcgstamt, (gst/2.0) as sgstper, "
                        + "sum(gstamt/2.0) as totsgstamt from SaleSubV2 where salemid='"+sm.getSalemid()+"' "
                        + "group by gst order by gst";
                System.out.println(query);
                dBConnection db=new dBConnection();
                Connection conn=db.setConnection();
                try
                {
                    Statement stm=conn.createStatement();
                    ResultSet rs=stm.executeQuery(query);
                    int sln = 0;
                    titleY += 8;
                    while(rs.next())
                    {
                        titleY += 12;
                        startpos = leftbound;
                        endpos = startpos + endposes[0];
                        // NO. OF COLUMNS: 7
                        /* S No., Taxable Amt., CGST %, CGST Amt., SGST %, SGST Amt., Tax Total */
                        // tottaxableamt, cgstper, totcgstamt, sgstper, totsgstamt
                        String data[] = { ++sln+"", MyNumberFormat.rupeeFormat(Double.parseDouble(rs.getString("tottaxableamt"))),
                                format1.format(Double.parseDouble(rs.getString("cgstper"))), MyNumberFormat.rupeeFormat(Double.parseDouble(rs.getString("totcgstamt"))),
                                format1.format(Double.parseDouble(rs.getString("sgstper"))), MyNumberFormat.rupeeFormat(Double.parseDouble(rs.getString("totsgstamt"))),
                                MyNumberFormat.rupeeFormat(Double.parseDouble(rs.getString("totcgstamt")) + Double.parseDouble(rs.getString("totsgstamt"))) };
                        for ( i = 0; i < endposes.length - 1; i++ ) {
                            titleText = data[i];
                            titleX = startpos + ((endposes[i] - fontMetrics.stringWidth(titleText)) / 2);
                            g2d.drawString(titleText, (int) titleX, (int) titleY);
                            startpos = endpos; 
                            endpos += endposes[i + 1];
                        }
                        titleText = data[i];
                        titleX = startpos + ((endposes[i] - fontMetrics.stringWidth(titleText)) / 2);
                        g2d.drawString(titleText, (int) titleX, (int) titleY);
                    }
                } catch(SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,"printTaxAnalysis01 ex?= "+ex,
                            "Error Found",JOptionPane.ERROR_MESSAGE);
                } finally {
                    try {
                        if (conn!=null) conn.close();
                    } catch(SQLException ex){}
                }
                
                // Total
                titleY = 365;
                // Table SaleMasterV2, no. of columns - 24
                /*
                salemid, compid, saledt, ordno, orddt, retid, deliverynote, paymentterm, transporter, vehicleno, 
                supplydt, totnoofitems, netgross, netitemdiscamt, netgstamt, cashdiscper, netcashdiscamt, 
                netamt, roundoff, dispscheme, netpayableamt, amtpaid, isactive, remarks
                */
                String data[] = { "", "Total", "", MyNumberFormat.rupeeFormat(Double.parseDouble(sm.getNetgstamt())/2.0), "", 
                    MyNumberFormat.rupeeFormat(Double.parseDouble(sm.getNetgstamt())/2.0), 
                    MyNumberFormat.rupeeFormat(Double.parseDouble(sm.getNetgstamt())/2.0 + Double.parseDouble(sm.getNetgstamt())/2.0) };
                startpos = leftbound;
                endpos = startpos + endposes[0];
                for ( i = 0; i < endposes.length - 1; i++ ) {
                    titleText = data[i];
                    titleX = startpos + ((endposes[i] - fontMetrics.stringWidth(titleText)) / 2);
                    g2d.drawString(titleText, (int) titleX, (int) titleY);
                    startpos = endpos; 
                    endpos += endposes[i + 1];
                }
                titleText = data[i];
                titleX = startpos + ((endposes[i] - fontMetrics.stringWidth(titleText)) / 2);
                g2d.drawString(titleText, (int) titleX, (int) titleY);
            }
            
            // Distributer Signing
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "FOR "+e.getEname();
            titleY = 410;
            titleX = rightbound - fontMetrics.stringWidth("FOR "+e.getEname()) + 125;
            g2d.drawString(titleText, ((int) titleX)- fontMetrics.stringWidth(titleText), (int) titleY);
            
            return (PAGE_EXISTS);
        } 
    } 
}
