package cr.ac.ucr.ecci.arceshopping.model;

public class User {
    private String name;
    private String password;
    private String id;
    private String email;
    private int age;
    private String province;
    private boolean pass;

    public User(String email, String id, String name, int age, String province, String password) {
        this.name = name;
        this.password = password;
        this.id = id;
        this.email = email;
        this.province = province;
        this.age = age;
        this.pass = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getEmail() {
        return email;
    }

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
