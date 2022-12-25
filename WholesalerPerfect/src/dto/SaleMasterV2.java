package dto;

import java.util.ArrayList;

public class SaleMasterV2 {
    // Table SaleMasterV2, no. of columns - 24
    /*
    salemid, compid, saledt, ordno, orddt, retid, deliverynote, paymentterm, transporter, vehicleno, 
    supplydt, totnoofitems, netgross, netitemdiscamt, netgstamt, cashdiscper, netcashdiscamt, 
    netamt, roundoff, dispscheme, netpayableamt, amtpaid, isactive, remarks
    */
    private String salemid = ""; 
    private String compid = ""; 
    private String saledt = ""; 
    private String ordno = ""; 
    private String orddt = ""; 
    private String retid = ""; 
    private String deliverynote = ""; 
    private String paymentterm = ""; 
    private String transporter = ""; 
    private String vehicleno = ""; 
    private String supplydt = ""; 
    private String totnoofitems = ""; 
    private String netgross = ""; 
    private String netitemdiscamt = ""; 
    private String netgstamt = ""; 
    private String cashdiscper = ""; 
    private String netcashdiscamt = ""; 
    private String netamt = ""; 
    private String roundoff = ""; 
    private String dispscheme = ""; 
    private String netpayableamt = ""; 
    private String amtpaid = ""; 
    private String isactive = ""; 
    private String remarks = "";
    private ArrayList<SaleSubV2> ssAl;

    public String getSalemid() {
        return salemid;
    }

    public void setSalemid(String salemid) {
        this.salemid = salemid;
    }

    public String getCompid() {
        return compid;
    }

    public void setCompid(String compid) {
        this.compid = compid;
    }

    public String getSaledt() {
        return saledt;
    }

    public void setSaledt(String saledt) {
        this.saledt = saledt;
    }

    public String getOrdno() {
        return ordno;
    }

    public void setOrdno(String ordno) {
        this.ordno = ordno;
    }

    public String getOrddt() {
        return orddt;
    }

    public void setOrddt(String orddt) {
        this.orddt = orddt;
    }

    public String getRetid() {
        return retid;
    }

    public void setRetid(String retid) {
        this.retid = retid;
    }

    public String getDeliverynote() {
        return deliverynote;
    }

    public void setDeliverynote(String deliverynote) {
        this.deliverynote = deliverynote;
    }

    public String getPaymentterm() {
        return paymentterm;
    }

    public void setPaymentterm(String paymentterm) {
        this.paymentterm = paymentterm;
    }

    public String getTransporter() {
        return transporter;
    }

    public void setTransporter(String transporter) {
        this.transporter = transporter;
    }

    public String getVehicleno() {
        return vehicleno;
    }

    public void setVehicleno(String vehicleno) {
        this.vehicleno = vehicleno;
    }

    public String getSupplydt() {
        return supplydt;
    }

    public void setSupplydt(String supplydt) {
        this.supplydt = supplydt;
    }

    public String getTotnoofitems() {
        return totnoofitems;
    }

    public void setTotnoofitems(String totnoofitems) {
        this.totnoofitems = totnoofitems;
    }

    public String getNetgross() {
        return netgross;
    }

    public void setNetgross(String netgross) {
        this.netgross = netgross;
    }

    public String getNetitemdiscamt() {
        return netitemdiscamt;
    }

    public void setNetitemdiscamt(String netitemdiscamt) {
        this.netitemdiscamt = netitemdiscamt;
    }

    public String getNetgstamt() {
        return netgstamt;
    }

    public void setNetgstamt(String netgstamt) {
        this.netgstamt = netgstamt;
    }

    public String getCashdiscper() {
        return cashdiscper;
    }

    public void setCashdiscper(String cashdiscper) {
        this.cashdiscper = cashdiscper;
    }

    public String getNetcashdiscamt() {
        return netcashdiscamt;
    }

    public void setNetcashdiscamt(String netcashdiscamt) {
        this.netcashdiscamt = netcashdiscamt;
    }

    public String getNetamt() {
        return netamt;
    }

    public void setNetamt(String netamt) {
        this.netamt = netamt;
    }

    public String getRoundoff() {
        return roundoff;
    }

    public void setRoundoff(String roundoff) {
        this.roundoff = roundoff;
    }

    public String getDispscheme() {
        return dispscheme;
    }

    public void setDispscheme(String dispscheme) {
        this.dispscheme = dispscheme;
    }

    public String getNetpayableamt() {
        return netpayableamt;
    }

    public void setNetpayableamt(String netpayableamt) {
        this.netpayableamt = netpayableamt;
    }

    public String getAmtpaid() {
        return amtpaid;
    }

    public void setAmtpaid(String amtpaid) {
        this.amtpaid = amtpaid;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public ArrayList<SaleSubV2> getSsAl() {
        return ssAl;
    }

    public void setSsAl(ArrayList<SaleSubV2> ssAl) {
        this.ssAl = ssAl;
    }
}
