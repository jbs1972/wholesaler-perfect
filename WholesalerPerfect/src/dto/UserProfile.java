package dto;

/**
 *
 * @author Jayanta B. Sen
 */
public class UserProfile {

    private String uid="";
    private String unm="";
    private String privilege="";

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUnm() {
        return unm;
    }

    public void setUnm(String unm) {
        this.unm = unm;
    }

}
