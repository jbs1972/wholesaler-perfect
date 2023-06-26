package dto;

import java.util.ArrayList;

public class PurchaseMaster {
    
    // Number of columns in PurchaseMaster: 22
    /* pmid, ssid, compid, invno, invdt, netqty, netgross, netitemdisc, nettaxable, tradediscper, 
    nettradediscamt, replacementdiscper, netreplacementdiscamt, netgstamt, grosspayableamt, roundoff, netpayableamt, 
    dispscheme, finalamt, isopening, amtpaid, isactive */
    private String pmid="";
    private String ssid="";
    private String compid="";
    private String invno="";
    private String invdt="";
    private String netqty="";
    private String netgross="";
    private String netitemdisc="";
    private String nettaxable="";
    private String tradediscper="";
    private String nettradediscamt="";
    private String replacementdiscper="";
    private String netreplacementdiscamt="";
    private String netgstamt="";
    private String grosspayableamt="";
    private String roundoff="";
    private String netpayableamt="";
    private String dispscheme="";
    private String finalamt="";
    private String isopening="";
    private String amtpaid="";
    private String isactive="";
    private ArrayList<PurchaseSub> psAl;

    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getCompid() {
        return compid;
    }

    public void setCompid(String compid) {
        this.compid = compid;
    }

    public String getInvno() {
        return invno;
    }

    public void setInvno(String invno) {
        this.invno = invno;
    }

    public String getInvdt() {
        return invdt;
    }

    public void setInvdt(String invdt) {
        this.invdt = invdt;
    }

    public String getNetqty() {
        return netqty;
    }

    public void setNetqty(String netqty) {
        this.netqty = netqty;
    }

    public String getNetgross() {
        return netgross;
    }

    public void setNetgross(String netgross) {
        this.netgross = netgross;
    }

    public String getNetitemdisc() {
        return netitemdisc;
    }

    public void setNetitemdisc(String netitemdisc) {
        this.netitemdisc = netitemdisc;
    }

    public String getNettaxable() {
        return nettaxable;
    }

    public void setNettaxable(String nettaxable) {
        this.nettaxable = nettaxable;
    }

    public String getTradediscper() {
        return tradediscper;
    }

    public void setTradediscper(String tradediscper) {
        this.tradediscper = tradediscper;
    }

    public String getNettradediscamt() {
        return nettradediscamt;
    }

    public void setNettradediscamt(String nettradediscamt) {
        this.nettradediscamt = nettradediscamt;
    }

    public String getReplacementdiscper() {
        return replacementdiscper;
    }

    public void setReplacementdiscper(String replacementdiscper) {
        this.replacementdiscper = replacementdiscper;
    }

    public String getNetreplacementdiscamt() {
        return netreplacementdiscamt;
    }

    public void setNetreplacementdiscamt(String netreplacementdiscamt) {
        this.netreplacementdiscamt = netreplacementdiscamt;
    }

    public String getNetgstamt() {
        return netgstamt;
    }

    public void setNetgstamt(String netgstamt) {
        this.netgstamt = netgstamt;
    }

    public String getGrosspayableamt() {
        return grosspayableamt;
    }

    public void setGrosspayableamt(String grosspayableamt) {
        this.grosspayableamt = grosspayableamt;
    }

    public String getRoundoff() {
        return roundoff;
    }

    public void setRoundoff(String roundoff) {
        this.roundoff = roundoff;
    }

    public String getNetpayableamt() {
        return netpayableamt;
    }

    public void setNetpayableamt(String netpayableamt) {
        this.netpayableamt = netpayableamt;
    }

    public String getIsopening() {
        return isopening;
    }

    public void setIsopening(String isopening) {
        this.isopening = isopening;
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

    public ArrayList<PurchaseSub> getPsAl() {
        return psAl;
    }

    public void setPsAl(ArrayList<PurchaseSub> psAl) {
        this.psAl = psAl;
    }

    public String getDispscheme() {
        return dispscheme;
    }

    public void setDispscheme(String dispscheme) {
        this.dispscheme = dispscheme;
    }

    public String getFinalamt() {
        return finalamt;
    }

    public void setFinalamt(String finalamt) {
        this.finalamt = finalamt;
    }
    
}
