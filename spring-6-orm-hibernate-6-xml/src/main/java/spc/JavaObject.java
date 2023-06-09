package spc;

public class JavaObject {

    private int id;
    private String vendor;
    private String version;

    public JavaObject() {
        // Hibernate
    }

    public JavaObject(int id, String vendor, String version) {
        this.id = id;
        this.vendor = vendor;
        this.version = version;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
