package cr.ac.ucr.ecci.arceshopping;

public class User {
    private String username;
    private String password;
    private String id;
    private String email;
    private int age;

    public User(String username, String password, String id, String email, int age) {
        this.username = username;
        this.password = password;
        this.id = id;
        this.email = email;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
