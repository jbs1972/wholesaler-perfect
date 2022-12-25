package print;

import conn.dBConnection;
import dto.Enterprise;
import dto.Retailer;
import dto.SaleMaster;
import dto.SaleSub;
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
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import static print.PaperSize.a4Height;
import static print.PaperSize.a4Width;
import utilities.MyNumberFormat;
import utilities.NumberToString;

public class printSaleBill {
    
    private SaleMaster sm;
    private Enterprise e;
    private Retailer r;
    private String fontNm = "Arial";
    private DecimalFormat format = new DecimalFormat("0.#");
    private int noofrecStartnend=18;
    private int sln=0;
    private boolean flag;
    private NumberToString nts = new NumberToString();
    private int noOfPages;
    private double mrpTotal = 0.0;
    
    public printSaleBill(SaleMaster sm, Enterprise e, Retailer r) {
        this.sm = sm;
        this.e = e;
        this.r = r;
        PrinterJob printJob = PrinterJob.getPrinterJob();
        PageFormat pPageFormat = printJob.defaultPage();
        Paper pPaper =new Paper();
        pPaper.setSize(a4Width, a4Height);
        pPaper.setImageableArea(1.0, 1.0, pPaper.getWidth() - 2.0, pPaper.getHeight() - 2.0);
        pPageFormat.setPaper(pPaper);
        Book book = new Book();
        pPageFormat.setOrientation(PageFormat.LANDSCAPE);
        
        // Getting no. of records
        int totnoofrecords = sm.getSsAl().size();
        for ( int i = 0; i < totnoofrecords; i += noofrecStartnend ) {
            noOfPages ++;
            if ( i+noofrecStartnend >= totnoofrecords ) {
                flag = true;
            }
            book.append(new DocumentStartNEnd(i, i+noofrecStartnend>totnoofrecords?
                    totnoofrecords-1:i+noofrecStartnend-1, flag), pPageFormat);
        }
        printJob.setPageable(book);

        // Print with dialog
        if (printJob.printDialog())
        {
            try
            {
                printJob.print();
            }
            catch (Exception PrintException)
            {
                JOptionPane.showMessageDialog(null,"Printing Exception: "+PrintException.getMessage(),"Printing Error",JOptionPane.ERROR_MESSAGE);
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

    private class DocumentStartNEnd implements Printable
    {
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
        public int print(Graphics g, PageFormat pageFormat, int page)
        {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            g2d.setPaint(Color.black);
            if(counter!=0)
                print2wiseFlag=true;
            counter++;

            if(true)
                return myPrint(g2d, pageFormat, page);
            
            return (PAGE_EXISTS);
        }
        
        private int myPrint(Graphics2D g2d, PageFormat pageFormat, int page)
        {
            int leftbound=45;
            int rightbound=835;
            Font titleFont =null;
            FontMetrics fontMetrics = null;
            
            //Outer Rectangle
            g2d.setPaint(Color.LIGHT_GRAY);
            double titleX = leftbound;
            double titleY = 25;
            g2d.drawRoundRect((int)(titleX), (int)(titleY), 790, 550, 5, 5);
            
            // *********************** Begin Of Header Section 01
            
            //1st Horizontal Line within upper rectangle
            g2d.drawLine(leftbound, (int)(titleY+100), 835, (int)(titleY+100));
            //1st Vertical Line within upper rectangle
            g2d.drawLine(440, (int)(titleY), 440, (int)(titleY+100));
            
            // DISTRIBUTER NAME
            Font font = new Font(fontNm, Font.BOLD, 16);
            Map<TextAttribute, Object> map = new HashMap<TextAttribute, Object>();
            map.put(TextAttribute.FONT, font);
            map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            font = Font.getFont(map);
            g2d.setFont(font);
            fontMetrics = g2d.getFontMetrics();
            g2d.setPaint(Color.BLUE);
            String titleText = e.getEname();
//            titleX = 243 - (fontMetrics.stringWidth(titleText) / 2);
            titleX = 50;
            titleY=43;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Distributer Street
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            g2d.setPaint(Color.BLACK);
            titleText = e.getEstreet();
            titleX = 50;
            titleY+=17;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Distributer Dist. & Pin.
            titleText = e.getEdist()+" - "+e.getEpin();
            titleX = 50;
            titleY+=12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Distributer State & Country
            titleText = e.getEstate()+" - "+e.getEcountry();
            titleX = 50;
            titleY+=12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Distributer Phone & Fax
            titleText = "Ph. No. - "+e.getEcontact();
            titleX = 50;
            titleY+=12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleText = "Email - "+(e.getEmail().equals("N/A")?"":e.getEmail());
            titleX = 250;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Distributer GSTIN & GST Regn. No.
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "GSTIN: ";
            titleX = 50;
            titleY+=12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = e.getEgstno();
            titleX = 50+fontMetrics.stringWidth("GSTIN: ");
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "GST Regn. Type: ";
            titleX = 250;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = e.getEgstregntype().equals("N/A")?"":e.getEgstregntype();
            titleX = 250+fontMetrics.stringWidth("GST Regn. Type: REGULAR");
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Distributer GST State & State code
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "GST State: ";
            titleX = 50;
            titleY+=12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = e.getEstate();
            titleX = 50+fontMetrics.stringWidth("GST State: ");
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "State Code: ";
            titleX = 250;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = e.getEstatecode();
            titleX = 250+fontMetrics.stringWidth("State Code: ");
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            titleY = 25;
            g2d.setPaint(Color.LIGHT_GRAY);
            //1st Right Horizontal Line within upper rectangle
            g2d.drawLine(440, (int)(titleY+25), 835, (int)(titleY+25));
            //2nd Right Horizontal Line within upper rectangle
            g2d.drawLine(440, (int)(titleY+50), 835, (int)(titleY+50));
            //1st Right Horizontal Line within upper rectangle
            g2d.drawLine(440, (int)(titleY+75), 835, (int)(titleY+75));
            //1st Right Vertical Line within upper rectangle
            g2d.drawLine(638, (int)(titleY+25), 638, (int)(titleY+100));
            
            // DISTRIBUTER NAME
            font = new Font(fontNm, Font.BOLD, 14);
            map = new HashMap<TextAttribute, Object>();
            map.put(TextAttribute.FONT, font);
            map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            font = Font.getFont(map);
//            titleFont = new Font(fontNm, Font.BOLD, 10);
            g2d.setFont(font);
            fontMetrics = g2d.getFontMetrics();
            g2d.setPaint(Color.BLUE);
            titleText = "TAX INVOICE";
            titleX = 445;
            titleY=43;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "Issued Under Section 31 of GST Act.";
            titleX = 560;
            titleY=42;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            g2d.setPaint(Color.BLACK);
            titleText = "Page "+(page+1)+" of "+noOfPages;
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleX = titleX = rightbound - fontMetrics.stringWidth(titleText) - 5;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Invoice No. & Invoice Date
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "Inv. No.: ";
            titleX = 445;
            titleY+=25;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = sm.getSalemid();
            titleX = 445+fontMetrics.stringWidth("Inv. No.: ");
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "Inv. Dt.: ";
            titleX = 643;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = sm.getSaledt();
            titleX = 643+fontMetrics.stringWidth("Inv. Dt.: ");
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Ord. No. & Ord. Date
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "Order No.: ";
            titleX = 445;
            titleY+=23;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = sm.getOrdno().equals("N/A")?"":sm.getOrdno();
            titleX = 445+fontMetrics.stringWidth("Order No.: ");
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "Order Date: ";
            titleX = 643;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = sm.getOrddt().equals("01/01/2000")?"":sm.getOrddt();
            titleX = 643+fontMetrics.stringWidth("Order Date: ");
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Delv. Note & Pay Term
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "Delv. Note: ";
            titleX = 445;
            titleY+=25;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = sm.getDeliverynote().equals("N/A")?"":sm.getDeliverynote();
            titleX = 445+fontMetrics.stringWidth("Delv. Note: ");
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "Pay Term: ";
            titleX = 643;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = sm.getPaymentterm().equals("N/A")?"":sm.getPaymentterm();
            titleX = 643+fontMetrics.stringWidth("Pay Term: ");
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // *********************** End Of Header Section 01
            
            // *********************** Begin Of Header Section 02
            
            g2d.setPaint(Color.LIGHT_GRAY);
            titleY += 30;
            //2nd Horizontal Line within middle rectangle
            g2d.drawLine(leftbound, (int)(titleY), 835, (int)(titleY));
            //3rd Horizontal Line within middle rectangle
            g2d.drawLine(leftbound, (int)(titleY+100), 835, (int)(titleY+100));
            //1st Vertical Line within middle rectangle
            g2d.drawLine(238, (int)(titleY-20), 238, (int)(titleY+100));
            //2nd Vertical Line within middle rectangle
            g2d.drawLine(440, (int)(titleY-20), 440, (int)(titleY+100));
            //3rd Vertical Line within middle rectangle
            g2d.drawLine(638, (int)(titleY-20), 638, (int)(titleY+100));
            
            g2d.setPaint(Color.GRAY);
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "BILL TO:-";
            titleX = 50;
            titleY = 138;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleText = "SHIP TO:-";
            titleX = 241;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleText = "CUSTOMER:-";
            titleX = 445;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleText = "TRANSPORT:-";
            titleX = 643;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Bill To Info.
            g2d.setPaint(Color.BLACK);
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "Name: "+r.getRetnm();
            titleX = 50;
            titleY = 160;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            g2d.setPaint(Color.BLACK);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "Address: ";
            titleY += 12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleText = r.getRstreet().equals("N/A")?"":r.getRstreet();
            titleX = 55;
            titleY += 12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleText = (r.getRdist().equals("N/A")?"":r.getRdist())+", "+(r.getRpin().equals("N/A")?"":r.getRpin());
            titleX = 55;
            titleY += 12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleText = (r.getRstate().equals("N/A")?"":r.getRstate())+", "+(r.getRcountry().equals("N/A")?"":r.getRcountry());
            titleX = 55;
            titleY += 12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            // GST State
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "GST State: ";
            titleX = 50;
            titleY+=15;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = r.getRstate().equals("N/A")?"": r.getRstate();
            titleX = 50+fontMetrics.stringWidth("GST State: ");
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            // GST State
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "State Code: ";
            titleX = 50;
            titleY+=15;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = r.getRstatecode().equals("N/A")?"":r.getRstatecode();
            titleX = 50+fontMetrics.stringWidth("State Code: ");
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Ship To Info.
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "Name: "+r.getRetnm();
            titleX = 241;
            titleY = 160;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            g2d.setPaint(Color.BLACK);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "Address: ";
            titleY += 12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleText = r.getRstreet().equals("N/A")?"":r.getRstreet();
            titleX = 246;
            titleY += 12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleText = (r.getRdist().equals("N/A")?"":r.getRdist())+", "+(r.getRpin().equals("N/A")?"":r.getRpin());
            titleX = 246;
            titleY += 12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleText = (r.getRstate().equals("N/A")?"":r.getRstate())+", "+(r.getRcountry().equals("N/A")?"":r.getRcountry());
            titleX = 246;
            titleY += 12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            // GST State
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "GST State: ";
            titleX = 241;
            titleY+=15;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = r.getRstate().equals("N/A")?"":r.getRstate();
            titleX = 241+fontMetrics.stringWidth("GST State: ");
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            // GST State
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "State Code: ";
            titleX = 241;
            titleY+=15;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = r.getRstatecode().equals("N/A")?"":r.getRstatecode();
            titleX = 246+fontMetrics.stringWidth("State Code: ");
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Customer
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "Contact Name: ";
            titleX = 445;
            titleY = 159;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = r.getContactperson().equals("N/A")?"":r.getContactperson()+
                    (r.getRcontact().equals("N/A")?"":" ["+r.getRcontact()+"]");
            titleX = 450;
            titleY += 12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "GST Regn. No.: ";
            titleX = 445;
            titleY += 12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = r.getRgstno().equals("N/A")?"":r.getRgstno();
            titleX = 450;
            titleY += 12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "GST Regn. Type: ";
            titleX = 445;
            titleY += 12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = r.getRgstregntype().equals("N/A")?"":r.getRgstregntype();
            titleX = 450;
            titleY += 12;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "PAN No.: ";
            titleX = 445;
            titleY += 11;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = r.getRpanno().equals("N/A")?"":r.getRpanno();
            titleX = 450+fontMetrics.stringWidth("PAN No.: ");
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // Tranport Info.
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "Transporter: ";
            titleX = 643;
            titleY = 159;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = sm.getTransporter().equals("N/A")?"":sm.getTransporter();
            titleX = 648;
            titleY += 15;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "Vehicle No.: ";
            titleX = 643;
            titleY += 17;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = sm.getVehicleno().equals("N/A")?"":sm.getVehicleno();
            titleX = 648;
            titleY += 15;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = "Supply Date: ";
            titleX = 643;
            titleY += 17;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            titleText = sm.getSupplydt().equals("01/01/2000")?"":sm.getSupplydt();
            titleX = 648;
            titleY += 15;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            // *********************** End Of Header Section 02
            
            // *********************** Column Heading Section Heading Begin
            /*
            int leftbound=45;
            int rightbound=835;
            */
            g2d.setPaint(Color.LIGHT_GRAY);
            titleY += 8;
            // No. Of Columns - 16
            int endposes[] = {18/*sln-63*/, 212/*Product Dsecription-275*/, 40/*HSN-315*/, 30/*Qty-345*/,
                30/*UOM-375*/, 45/*Rate-420*/, 60/*Amt.-480*/, 30/*Disc %-510*/, 60/*Txbl. Amt.-570*/, 
                30/*CGST %-600*/, 40/*CGST Amt.-640*/, 30/*SGST %-670*/, 40/*SGST Amt.-710*/, 
                30/*IGST %-740*/, 40/*IGST Amt.-780*/ , 55/*Line Total-835*/};
            int prev = 0;
            for ( int i = 0; i < endposes.length-1; i++ ) {
                g2d.drawLine(prev+endposes[i]+45, (int)(titleY), prev+endposes[i]+45, (int)(titleY+250));
                prev += endposes[i];
            }
            //1st Horizontal Line within item details after column headings
            titleY += 23;
            g2d.drawLine(leftbound, (int)(titleY), 835, (int)(titleY));
            //1st Horizontal Line after item details
            titleY += 228;
            g2d.drawLine(leftbound, (int)(titleY), 835, (int)(titleY));
            titleY += 15;
            g2d.drawLine(leftbound, (int)(titleY), 835, (int)(titleY));
            
            // NO. OF COLUMNS: 16
            /* S No., Product Description, HSN, Qty., UOM, Rate, Amt., Disc. %, Txbl. Amt., CGST %, CGST Amt.,
            SGST %, SGST Amt., IGST %, IGST Amt., Line Total */
            String headingLine1[] = {"S", "", "", "", "", "", "", "Disc.", "Txbl.", "CGST", "CGST", "SGST", "SGST", "IGST", "IGST", "Line"};
            String headingLine2[] = {"", "Product Description", "HSN", "Qty.", "UOM", "Rate", "Amt.", "", "", "", "", "", "", "", "", ""};
            String headingLine3[] = {"No.", "", "", "", "", "", "", "", "Amt.", "%", "Amt.", "%", "Amt.", "%", "Amt.", "Total"};
            
            g2d.setPaint(Color.BLUE);
            titleFont = new Font("Arial", Font.BOLD, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            
            int startpos = leftbound;
            int endpos = startpos + endposes[0];
            titleY = 255;
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
            titleY = 260;
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
            titleY = 265;
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
            
            // *********************** Column Heading Section Heading End
            
            // *********************** Item Data Section Heading End
            /*
            recbegin;
            recstop;
            */
            titleY = 270;
            g2d.setPaint(Color.BLACK);
            titleFont = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(titleFont);
            fontMetrics = g2d.getFontMetrics();
            ArrayList<SaleSub> ssAl = sm.getSsAl();
            for ( int j = recbegin; j <= recstop; j++ ) {
                titleY += 12;
                SaleSub ss = ssAl.get(j);
                // Item Name, HSN, Measuring
                if(print2wiseFlag)
                {
                    String itemnm = "";
                    String hsn = "";
                    String munm = "";
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
                            itemnm = rs.getString("itemnm").replace("\\'", "'")+" ("
                                    + format.format(Double.parseDouble(ss.getMrp()))+")";
                            hsn = rs.getString("hsn");
                            munm = "Pcs.";
                        }
                    }
                    catch(SQLException ex)
                    {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null,"PrintSaleBill ex= "+ex,
                                "Error Found",JOptionPane.ERROR_MESSAGE);
                    }
                    finally
                    {
                        try {
                            if (conn!=null) conn.close();
                        }
                        catch(SQLException ex){}
                    }
                    
                    startpos = leftbound;
                    endpos = startpos + endposes[0];
                    // NO. OF COLUMNS: 16
                    /* S No., Product Description, HSN, Qty., UOM, Rate, Amt., Disc. %, Txbl. Amt., CGST %, CGST Amt.,
                    SGST %, SGST Amt., IGST %, IGST Amt., Line Total */
                    String data[] = { ++sln+"", itemnm, hsn, format.format(Double.parseDouble(ss.getQty())), munm, 
                        MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getExgst())), MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getAmt())),
                            "-"+MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getItemdiscamt())), MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getTaxableamt())),
                            format.format(Double.parseDouble(ss.getCgstper())), MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getCgstamt())),
                            format.format(Double.parseDouble(ss.getSgstper())), MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getSgstamt())),
                            format.format(Double.parseDouble(ss.getIgstper())), MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getIgstamt())),
                            MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getTotal())) };
//                    System.out.println("QTY: "+format.format(Double.parseDouble(ss.getQty())));
//                    System.out.println("MRP: "+format.format(Double.parseDouble(ss.getMrp())));
                    mrpTotal += Double.parseDouble(format.format(Double.parseDouble(ss.getQty()))) * 
                            Double.parseDouble(format.format(Double.parseDouble(ss.getMrp())));
                    int k = 0;
                    for ( ; k < endposes.length - 1; k++ ) {
                        titleText = data[k];
                        if ( k == 1 ) {
                            titleX = startpos + 5;
                        } else {
                            titleX = startpos + ((endposes[k] - fontMetrics.stringWidth(titleText)) / 2);
                        }
                        g2d.drawString(titleText, (int) titleX, (int) titleY);
                        startpos = endpos; 
                        endpos += endposes[k + 1];
                    }
                    titleText = data[k];
                    titleX = startpos + ((endposes[k] - fontMetrics.stringWidth(titleText)) / 2);
                    g2d.drawString(titleText, (int) titleX, (int) titleY);
                }
            }
            // *********************** Item Data Section Heading End
            
            // *********************** Begin last page summary
            if(lastPageFlag)
            {
                // Printing Item Summary
                // NO. OF COLUMNS: 16
                /* S No., Product Description, HSN, Qty., UOM, Rate, Amt., Disc. %, Txbl. Amt., CGST %, CGST Amt.,
                SGST %, SGST Amt., IGST %, IGST Amt., Line Total */
                titleY = 508;
                startpos = leftbound;
                endpos = startpos + endposes[0];
                String data[] = { "Total", "", "", format.format(Double.parseDouble(sm.getNetqty())), "", "", "",
                        "", MyNumberFormat.rupeeFormat(Double.parseDouble(sm.getNettaxable())),
                        "", MyNumberFormat.rupeeFormat(Double.parseDouble(sm.getNetcgst())),
                        "", MyNumberFormat.rupeeFormat(Double.parseDouble(sm.getNetsgst())),
                        "", MyNumberFormat.rupeeFormat(Double.parseDouble(sm.getNetigst())),
                        MyNumberFormat.rupeeFormat(Double.parseDouble(sm.getNettotal())) };
                int k = 0;
                for ( ; k < endposes.length - 1; k++ ) {
                    titleText = data[k];
                    titleX = startpos + ((endposes[k] - fontMetrics.stringWidth(titleText)) / 2);
                    if ( k == 0 ) {
                        titleX += 5;
                    }
                    g2d.drawString(titleText, (int) titleX, (int) titleY);
                    startpos = endpos; 
                    endpos += endposes[k + 1];
                }
                titleText = data[k];
                titleX = startpos + ((endposes[k] - fontMetrics.stringWidth(titleText)) / 2);
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                g2d.setPaint(Color.LIGHT_GRAY);
                titleY = 512;
                //1st Vertical Line within last page footer
                g2d.drawLine(510, (int)(titleY), 510, (int)(titleY+63));
                //1st Horizontal Line within last page footer
                g2d.drawLine(leftbound, (int)(titleY+15), 835, (int)(titleY+15));
                //2nd Horizontal Line within last page footer
                g2d.drawLine(leftbound, (int)(titleY+30), 835, (int)(titleY+30));
                //3rd Horizontal 1/2 Line within last page footer
                g2d.drawLine(510, (int)(titleY+45), 835, (int)(titleY+45));
                
                // Amount in words
                g2d.setPaint(Color.BLACK);
                titleFont = new Font("Arial", Font.PLAIN, 8);
                g2d.setFont(titleFont);
                fontMetrics = g2d.getFontMetrics();
                double subtotal=Double.parseDouble(MyNumberFormat.rupeeFormat(Double.parseDouble(
                        sm.getNetamt02())).replaceAll(",", ""));
                int m=(int)subtotal;
                int s=(int)((subtotal-m)*100.0);
//                System.out.println("Mantisa: "+m+" and Scale: "+s);
                titleX = leftbound+5;
                titleY = 525;
                String amtinword = "";
                if(s==0)
                    amtinword = "IN WORD: "+nts.numberToString(m)+"Only.";
                else
                    amtinword = "IN WORD: "+nts.numberToString(m)+"and "+nts.numberToString(s)
                            +"Paise Only.";
                g2d.drawString(amtinword, (int) titleX, (int) titleY);
                
                // Advance Amount
                titleText = "CD : "+format.format(Double.parseDouble(sm.getCashdiscper()))+"% CD Amt.: ";
                titleX = 515;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                titleText = MyNumberFormat.rupeeFormat(Double.parseDouble(sm.getCashdiscamt()));
                titleX = rightbound - fontMetrics.stringWidth(titleText) - 5;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // Remarks
                titleText = "Remarks: "+(sm.getRemarks().equals("N/A")?"":sm.getRemarks());
                titleX = leftbound+5;
                titleY += 15;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                                
                // Round-off
                titleText = "Round-off: ";
                titleX = 515;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                titleText = MyNumberFormat.rupeeFormat(Double.parseDouble(sm.getRoundoff()));
                titleX = rightbound - fontMetrics.stringWidth(titleText) - 5;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // Window Display
//                if ( Double.parseDouble(sm.getDisplayamt()) != 0.0 ) {
//                    titleText = sm.getDisplaynote();
//                    titleX = 515;
//                    titleY += 15;
//                    g2d.drawString(titleText, (int) titleX, (int) titleY);
//                    titleText = MyNumberFormat.rupeeFormat(Double.parseDouble(sm.getDisplayamt()));
//                    titleX = rightbound - fontMetrics.stringWidth(titleText) - 5;
//                    g2d.drawString(titleText, (int) titleX, (int) titleY);
//                } else {
//                    titleY += 15;
//                }
                titleY += 15;
                
                // Net Amount Receivable
                titleFont = new Font("Arial", Font.BOLD, 8);
                g2d.setFont(titleFont);
                fontMetrics = g2d.getFontMetrics();
                titleText = "Net Amount Receivable: ";
                titleX = 515;
                titleY += 14;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                titleText = MyNumberFormat.rupeeFormat(Double.parseDouble(sm.getNetamt02()));
                titleX = rightbound - fontMetrics.stringWidth(titleText) - 5;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // MRP TOTAL
                titleFont = new Font("Arial", Font.PLAIN, 8);
                g2d.setFont(titleFont);
                fontMetrics = g2d.getFontMetrics();
                titleText = "MRP Total: "+MyNumberFormat.rupeeFormat(mrpTotal);
                titleX = leftbound+5;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // Signature
                titleFont = new Font("Arial", Font.PLAIN, 8);
                g2d.setFont(titleFont);
                fontMetrics = g2d.getFontMetrics();
                titleText = "For "+e.getEname();
                titleX = 505;
                g2d.drawString(titleText, ((int) titleX)- fontMetrics.stringWidth(titleText), (int) titleY);
                
                //Declaration: This is Computer Generated Invoice
                titleFont = new Font("Arial", Font.PLAIN, 6);
                g2d.setFont(titleFont);
                fontMetrics = g2d.getFontMetrics();
                titleText = "This is Computer Generated Invoice";
                titleX = (pageFormat.getImageableWidth() / 2) - (fontMetrics.stringWidth(titleText) / 2);
                titleY += 15;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
            } else {
                titleText = "CONTINUED TO PAGE: "+(page+2);
                titleX = rightbound - fontMetrics.stringWidth(titleText) - 5;
                titleY = 545;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
            }
            // *********************** End last page summary
            
            return (PAGE_EXISTS);
        }
    }
    
}
