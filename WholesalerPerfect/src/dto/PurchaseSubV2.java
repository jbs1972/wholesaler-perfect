package dto;

public class PurchaseSubV2 {
    // Table PurchaseSubV2, no. of columns - 13
    /*
    psid, pmid, itemdid, mrp, gst, qty, rate, discper, discamt, amount, qtysold, retqty
    */
    private String psid="";
    private String pmid="";
    private String itemdid="";
    private String mrp="";
    private String gst="";
    private String qty="";
    private String rate="";
    private String discper="";
    private String discamt="";
    private String amount="";
    private String qtysold="";
    private String retqty="";

    public String getPsid() {
        return psid;
    }

    public void setPsid(String psid) {
        this.psid = psid;
    }

    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public String getItemdid() {
        return itemdid;
    }

    public void setItemdid(String itemdid) {
        this.itemdid = itemdid;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getDiscper() {
        return discper;
    }

    public void setDiscper(String discper) {
        this.discper = discper;
    }

    public String getDiscamt() {
        return discamt;
    }

    public void setDiscamt(String discamt) {
        this.discamt = discamt;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getQtysold() {
        return qtysold;
    }

    public void setQtysold(String qtysold) {
        this.qtysold = qtysold;
    }

    public String getRetqty() {
        return retqty;
    }

    public void setRetqty(String retqty) {
        this.retqty = retqty;
    }
}
