package cr.ac.ucr.ecci.arceshopping.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

public class Purchase implements Serializable {
    int total;
    String ownerEmail;
    String purchaseTime;
    HashMap<Integer, Integer> shoppingCart;

    public Purchase(int total, String purchaseTime, String ownerEmail,
                    HashMap<Integer,Integer> shoppingCart){
        this.total = total;
        this.purchaseTime = purchaseTime;
        this.ownerEmail = ownerEmail;
        this.shoppingCart = shoppingCart;
    }

    public int getTotal() {
        return total;
    }

    public HashMap<String, Integer> getShoppingCart() {
        //Firebase only allows HashMaps of <String, Integer> types.
        //This method converts the original shopping cart into an accepted.
        //Type of hash map.
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
}
