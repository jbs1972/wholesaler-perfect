package dto;

public class SaleSubV2 {
    // Table SaleSubV2, no. of columns - 17
    /*
    salesid, salemid, psid, itemdid, mrp, gst, qty, free, unitnetrate, rate, 
    gross, itemdiscper, itemdiscamt, cashdiscamt, gstamt, amount, retqty
    */
    private String salesid = ""; 
    private String salemid = ""; 
    private String psid = ""; 
    private String itemdid = ""; 
    private String mrp = ""; 
    private String gst = ""; 
    private String qty = ""; 
    private String free = ""; 
    private String unitnetrate = "";
    private String rate = ""; 
    private String gross = ""; 
    private String itemdiscper = ""; 
    private String itemdiscamt = ""; 
    private String cashdiscamt = ""; 
    private String gstamt = ""; 
    private String amount = "";
    private String retqty = "";

    public String getSalesid() {
        return salesid;
    }

    public void setSalesid(String salesid) {
        this.salesid = salesid;
    }

    public String getSalemid() {
        return salemid;
    }

    public void setSalemid(String salemid) {
        this.salemid = salemid;
    }

    public String getPsid() {
        return psid;
    }

    public void setPsid(String psid) {
        this.psid = psid;
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

    public String getFree() {
        return free;
    }

    public void setFree(String free) {
        this.free = free;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getUnitnetrate() {
        return unitnetrate;
    }

    public void setUnitnetrate(String unitnetrate) {
        this.unitnetrate = unitnetrate;
    }

    public String getGross() {
        return gross;
    }

    public void setGross(String gross) {
        this.gross = gross;
    }

    public String getItemdiscper() {
        return itemdiscper;
    }

    public void setItemdiscper(String itemdiscper) {
        this.itemdiscper = itemdiscper;
    }

    public String getItemdiscamt() {
        return itemdiscamt;
    }

    public void setItemdiscamt(String itemdiscamt) {
        this.itemdiscamt = itemdiscamt;
    }

    public String getCashdiscamt() {
        return cashdiscamt;
    }

    public void setCashdiscamt(String cashdiscamt) {
        this.cashdiscamt = cashdiscamt;
    }

    public String getGstamt() {
        return gstamt;
    }

    public void setGstamt(String gstamt) {
        this.gstamt = gstamt;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getRetqty() {
        return retqty;
    }

    public void setRetqty(String retqty) {
        this.retqty = retqty;
    }
}
