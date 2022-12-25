package print;

public interface PaperSize {
    
    // For Receipt 10" X 6"
    //pPaper.setSize(642, 388);
    //pPaper.setSize(700, 388);
    //pPaper.setSize(800, 390);//For Draft and 12cpi Mode of Printer
    //pPaper.setSize(800, 410);
    double receiptWidth = 800.0;
    double receiptHeight = 432.0;
    
    // For A4
    double a4Width = 595.0;
    double a4Height = 862.0;
    
    // For Half Of A4
    double halfA4Width = 595.0;
    double halfA4Height = 430.0;
    
}
