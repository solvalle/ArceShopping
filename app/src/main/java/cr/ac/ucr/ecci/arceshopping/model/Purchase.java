package cr.ac.ucr.ecci.arceshopping.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
//Class meant to represent purchase prevously made by user.
public class Purchase implements Parcelable {
    int total;
    String ownerEmail;
    String purchaseTime;
    HashMap<Integer, Integer> shoppingCart;
    HashMap<String, Integer> shoppingCartB;
    public Purchase(int total, String purchaseTime, String ownerEmail,
                    HashMap<Integer,Integer> shoppingCart){
        this.total = total;
        this.purchaseTime = purchaseTime;
        this.ownerEmail = ownerEmail;
        this.shoppingCart = shoppingCart;
    }

    //Constructor meant to be used when retrieving purchase history from firebase
    public Purchase(int total, String purchaseTime, String ownerEmail,
                    HashMap<String,Integer> loadedCart, boolean notToUse){
        this.total = total;
        this.purchaseTime = purchaseTime;
        this.ownerEmail = ownerEmail;
        this.shoppingCartB = loadedCart ;
        /*
        * For some reason, this block throws an error saying that a long
        * is being inserted as key.
        * for (String productId : loadedCart.keySet()) {
            System.out.println(productId);
            System.out.println(Integer.parseInt(productId));
            System.out.println(Integer.valueOf(productId));
            Integer key = Integer.valueOf(productId);
            System.out.println(key.getClass().toString());
            this.shoppingCart.put(key.,loadedCart.get(productId));
        }
        * */
    }

    public int getTotal() {
        return total;
    }

    public HashMap<String, Integer> getShoppingCart() {
        //Firebase only allows HashMaps of <String, Integer> type.
        //This method converts the original shopping cart into an accepted
        //type of hash map.
        HashMap<String, Integer> newShoppingCart = new HashMap<String,Integer>();

        for (int productId : shoppingCart.keySet()) {
            newShoppingCart.put(String.valueOf(productId),shoppingCart.get(productId));
        }
        return newShoppingCart;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getPurchaseTime() {
        return purchaseTime;
    }

    public HashMap<String, Integer> getShoppingCartB() {
        return shoppingCartB;
    }

    protected Purchase(Parcel in){
        this.total = in.readInt();
        this.ownerEmail = in.readString();
        this.purchaseTime = in.readString();
        this.shoppingCartB = in.readHashMap(String.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(total);
        parcel.writeString(ownerEmail);
        parcel.writeString(purchaseTime);
        parcel.writeMap(shoppingCartB);
    }

    public static Creator<Purchase> CREATOR = new Creator<Purchase>() {

        @Override
        public Purchase createFromParcel(Parcel in) {
            return new Purchase(in);
        }

        @Override
        public Purchase[] newArray(int size) {
            return new Purchase[size];
        }
    };

    @Override
    public String toString() {
        return "Purchase{" +
                "total=" + total +
                ", ownerEmail='" + ownerEmail + '\'' +
                ", purchaseTime='" + purchaseTime + '\'' +
                ", shoppingCart=" + shoppingCart +
                ", shoppingCartB=" + shoppingCartB +
                '}';
    }
}
