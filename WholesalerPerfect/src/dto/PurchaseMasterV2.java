package dto;

import java.util.ArrayList;

public class PurchaseMasterV2 {
    // Table PurchaseMasterV2, no. of columns - 20
    /*
    pmid, ssid, compid, invno, invdt, netqty, netitemdisc, netgross, tradediscper, nettradediscamt, 
    replacementdiscper, netreplacementdiscamt, netgstamt, amtaftergst, roundoff, dispscheme, netpayableamt, 
    isopening, amtpaid, isactive
    */
    private String pmid = "";
    private String ssid = "";
    private String compid = "";
    private String invno = "";
    private String invdt = "";
    private String netqty = "";
    private String netitemdisc = "";
    private String netgross = "";
    private String tradediscper = "";
    private String nettradediscamt = "";
    private String replacementdiscper = "";
    private String netreplacementdiscamt = "";
    private String netgstamt = "";
    private String amtaftergst = "";
    private String roundoff = "";
    private String dispscheme = "";
    private String netpayableamt = "";
    private String isopening = "";
    private String amtpaid = "";
    private String isactive = "";
    private ArrayList<PurchaseSubV2> psAl;
    private ArrayList<PurchaseGSTV2> pgstAl;

    public ArrayList<PurchaseSubV2> getPsAl() {
        return psAl;
    }

    public void setPsAl(ArrayList<PurchaseSubV2> psAl) {
        this.psAl = psAl;
    }

    public ArrayList<PurchaseGSTV2> getPgstAl() {
        return pgstAl;
    }

    public void setPgstAl(ArrayList<PurchaseGSTV2> pgstAl) {
        this.pgstAl = pgstAl;
    }

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

    public String getNetitemdisc() {
        return netitemdisc;
    }

    public void setNetitemdisc(String netitemdisc) {
        this.netitemdisc = netitemdisc;
    }

    public String getNetgross() {
        return netgross;
    }

    public void setNetgross(String netgross) {
        this.netgross = netgross;
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

    public String getAmtaftergst() {
        return amtaftergst;
    }

    public void setAmtaftergst(String amtaftergst) {
        this.amtaftergst = amtaftergst;
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
}
