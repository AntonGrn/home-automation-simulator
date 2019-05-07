package mainPackage.modelClasses;

/**
 * Just a test class until we got the real one going.
 *
 */
public class Account {
    private String name;
    private String email;
    private int systemID;
    private boolean admin;
    private String password;

    public Account(String name, String email, int systemID, boolean admin, String password){
        this.name=name;
        this.email=email;
        this.systemID=systemID;
        this.admin = admin;
        this.password=password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSystemID() {
        return systemID;
    }

    public void setSystemID(int systemID) {
        this.systemID = systemID;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}