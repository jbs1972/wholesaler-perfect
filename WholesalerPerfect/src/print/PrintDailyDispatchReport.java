package print;

import dto.DailyDispatchReportDto;
import dto.Enterprise;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import utilities.MyNumberFormat;
import utilities.NumberToString;

public class PrintDailyDispatchReport {
    
    private ArrayList<DailyDispatchReportDto> ddrAl;
    private Enterprise e;
    private String fromDate;
    private String toDate;
    
    private int noOfPages;
    private int noofrecStartnend=30; // Ought to be 30
    private boolean flag;
    private String fontNm = "Courier New";
    private DecimalFormat format = new DecimalFormat("0.#");
    private NumberToString nts = new NumberToString();
    
    private int sln=0;
    
    public PrintDailyDispatchReport(ArrayList<DailyDispatchReportDto> ddrAl, Enterprise e, String fromDate, String toDate) {
        this.ddrAl = ddrAl;
        this.e = e;
        this.fromDate = fromDate;
        this.toDate = toDate;
        
        PrinterJob printJob = PrinterJob.getPrinterJob();
        PageFormat pPageFormat = printJob.defaultPage();
        Paper pPaper =new Paper();
        pPaper.setSize(595, 842);
        pPaper.setImageableArea(1.0, 1.0, pPaper.getWidth() - 2.0, pPaper.getHeight() - 2.0);
        pPageFormat.setPaper(pPaper);
        Book book = new Book();
        pPageFormat.setOrientation(PageFormat.PORTRAIT);
        
        // Getting no. of records
        int totnoofrecords = ddrAl.size();
        for ( int i = 0; i < totnoofrecords; i += noofrecStartnend ) {
            noOfPages ++;
            if ( i+noofrecStartnend >= totnoofrecords ) {
                flag = true;
            }
            book.append(new PrintDailyDispatchReport.DocumentStartNEnd(i, i+noofrecStartnend>totnoofrecords?
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
            int leftbound=13;
            int rightbound=571;
            int middleBound=250;
            FontMetrics fontMetrics = null;
            String titleText = null;
            Font font = null;
            double titleX = 0;
            double titleY = 0;
            // _ 117
            String horizontalFullLineLines8 = "----------------------------------------------------------------------------------------------------------------------";
            
            font = new Font(fontNm, Font.BOLD, 10);
            g2d.setFont(font);
            fontMetrics = g2d.getFontMetrics();
            g2d.setPaint(Color.BLACK);
            titleText = e.getEname();
            titleX = leftbound;
            titleY = 25;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            titleText = "["+PrintDailyDispatchReport.this.fromDate+" To "+PrintDailyDispatchReport.this.toDate+"] - DAILY DESPATCH REPORT";
            titleX = rightbound - fontMetrics.stringWidth(titleText);
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
            titleText = "SN|          COMPANY          |                 ITEM                 |    QUANTITY    |        MRP       |     HSN     ";
            titleX = leftbound;
            titleY+=11;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            titleText = horizontalFullLineLines8;
            titleX = leftbound;
            titleY+=11;
            g2d.drawString(titleText, (int) titleX, (int) titleY);
            
            int lineNo = 0;
            for ( int j = recbegin; j <= recstop; j++ ) {
                titleY += 11;
                if(print2wiseFlag)
                {
                    sln++;
                }
                DailyDispatchReportDto dailyDispatchReportDto = ddrAl.get(j);
                
                // SN
                titleX = leftbound;
                titleText = sln+spaceCompute(String.valueOf(sln).length(),2)+"|";
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // COMPANY
                titleX = titleX+fontMetrics.stringWidth(titleText);
                titleText = dailyDispatchReportDto.getCompany()+spaceCompute(dailyDispatchReportDto.getCompany().length(),27)+"|";;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // ITEM
                titleX = titleX+fontMetrics.stringWidth(titleText);
                titleText = dailyDispatchReportDto.getItem()+spaceCompute(dailyDispatchReportDto.getItem().length(),38)+"|";;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // QUANTITY
                titleX = titleX+fontMetrics.stringWidth(titleText);
                String sqty = format.format(Double.parseDouble(dailyDispatchReportDto.getQty()));
                titleText = spaceCompute(sqty.length(),14)+sqty+"  |";
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // MRP
                titleX = titleX+fontMetrics.stringWidth(titleText);
                String smrp = MyNumberFormat.rupeeFormat(Double.parseDouble(dailyDispatchReportDto.getMrp()));
                titleText = spaceCompute(smrp.length(),16)+smrp+"  |";
                g2d.drawString(titleText, (int) titleX, (int) titleY);
                
                // HSN
                titleX = titleX+fontMetrics.stringWidth(titleText);
                titleText = dailyDispatchReportDto.getHsn()+spaceCompute(dailyDispatchReportDto.getHsn().length(),15);;
                g2d.drawString(titleText, (int) titleX, (int) titleY);
            }
            
            return (PAGE_EXISTS);
        }
        
        private String spaceCompute(int len, int totlen) {
            StringBuffer sb = new StringBuffer();
            int remaining = totlen - len;
            for (int i = 1; i <= remaining; i++) {
                sb.append(" ");
            }
            return sb.toString();
        }
    }
}
