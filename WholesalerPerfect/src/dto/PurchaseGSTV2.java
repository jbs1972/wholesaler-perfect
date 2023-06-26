package dto;

public class PurchaseGSTV2 {
    // Table PurchaseGSTV2, no. of columns - 5
    /*
    pgstid, pmid, gstper, taxableamt, gstamt
    */
    private String pgstid = "";
    private String pmid = "";
    private String gstper = "";
    private String taxableamt = "";
    private String gstamt = "";

    public String getPgstid() {
        return pgstid;
    }

    public void setPgstid(String pgstid) {
        this.pgstid = pgstid;
    }

    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public String getGstper() {
        return gstper;
    }

    public void setGstper(String gstper) {
        this.gstper = gstper;
    }

    public String getTaxableamt() {
        return taxableamt;
    }

    public void setTaxableamt(String taxableamt) {
        this.taxableamt = taxableamt;
    }

    public String getGstamt() {
        return gstamt;
    }

    public void setGstamt(String gstamt) {
        this.gstamt = gstamt;
    }
}
