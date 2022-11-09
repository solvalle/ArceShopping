package cr.ac.ucr.ecci.arceshopping.model;

public class User {
    private String name;
    private String id;
    private String email;
    private String path;
    private int age;
    private String province;
    private boolean passwordIsChanged;

    public User(String email, String id, String name,  String path,
                int age, String province, boolean passwordIsChanged) {
        this.name = name;
        this.id = id;
        this.path = path;
        this.email = email;
        this.province = province;
        this.age = age;
        this.passwordIsChanged = passwordIsChanged;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public boolean getPasswordIsChanged() {
        return passwordIsChanged;
    }

    @Override
    public String toString() {
        return
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", province='" + province + '\'' +
                ", passwordIsChanged=" + passwordIsChanged;
    }
}
