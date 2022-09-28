package cr.ac.ucr.ecci.arceshopping;

public class User {
    private String id;
    private String email;
    private String password;
    private int age;
    private String province;
    private boolean pass;

    public User(String id, String email, String password, int age, String province) {
        this.password = password;
        this.id = id;
        this.email = email;
        this.age = age;
        this.province = province;
        this.pass = false;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getProvince() { return province; }

    public void setProvince(String province) {
        this.province = province;
    }

    public boolean getPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }
}
