package cr.ac.ucr.ecci.arceshopping.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.id = in.readString();
        this.path = in.readString();
        this.email = in.readString();
        this.province = in.readString();
        this.age = in.readInt();
        this.passwordIsChanged = Boolean.valueOf(in.readString());
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(id);
        parcel.writeString(path);
        parcel.writeString(email);
        parcel.writeString(province);
        parcel.writeInt(age);
        parcel.writeString(String.valueOf(passwordIsChanged));
    }

    public static Creator<User> CREATOR = new Creator<User>() {

        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
