package cr.ac.ucr.ecci.arceshopping.db;
import android.app.Activity;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cr.ac.ucr.ecci.arceshopping.ICartResponder;
import cr.ac.ucr.ecci.arceshopping.IPurchaseHistoryReceiver;
import cr.ac.ucr.ecci.arceshopping.model.Purchase;
import cr.ac.ucr.ecci.arceshopping.model.ShoppingCartRow;
import cr.ac.ucr.ecci.arceshopping.model.User;


public class FirebaseHelper {
    static final int LIMIT = 10;
    private FirebaseFirestore db;
    private ICartResponder cartResponder;
    private IPurchaseHistoryReceiver purchaseHistoryReceiver;

    public FirebaseHelper(){
        db = FirebaseFirestore.getInstance();
    }
    public void setCartResponder(ICartResponder cr) {this.cartResponder=cr;}
    public void setPurchaseHistoryReceiver(IPurchaseHistoryReceiver pr) {this.purchaseHistoryReceiver = pr;}

    public void insertShoppingCartRow(ShoppingCartRow newRow, Toast toast, int stock, Activity context){
        //Check if incoming product has already been added to sc.
        //If so, check how many stock there is available and if customer
        //hasn't exceeded the allowed maximum.
        db.collection("Shopping_cart")//Name of the collection to find.
                .whereEqualTo("ownerEmail", newRow.getOwnerEmail())//Find documents whose ownerEmail field's content is the same as the newRow's.
                .whereEqualTo("productId", newRow.getProductId())//Find document whose productId is the same as the new row's productID.
                .get()//Execute query
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    //Callback that will be invoked when we receive a response from firebase.
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult().getDocuments().isEmpty()){
                            //This confirms that the product is being added to the shopping cart for the first time.
                            db.collection("Shopping_cart")
                                    .add(newRow)//Firebase allows to pass an object as argument to insert into a collection.
                                                //Just make sure that every class getter has been implemented.
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(Task<DocumentReference> task) {
                                    //When firebase responds to the insert operation...
                                    if(task.isSuccessful()){
                                        toast.setText("Producto añadido");
                                        toast.show();
                                        context.finish();
                                    }
                                }
                            });
                        } else {
                            //This confirms that the product has been previously included, so we start the quantity modification process.
                            int quantity = Integer.parseInt(task.getResult().getDocuments().get(0).get("quantity").toString());
                            increaseItemQuantity(newRow, toast, quantity, stock, task.getResult().getDocuments().get(0).getId(), context);
                        }
                    }
                });
    }

    //This method finds the data related with the currently logged in user.
    public void selectUser(String ownerEmail){
        db.collection("User")
                .whereEqualTo("email", ownerEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(!task.getResult().isEmpty()){
                            DocumentSnapshot userData = task.getResult().getDocuments().get(0);
                            User user = new User(ownerEmail, userData.get("id").toString(), userData.get("name").toString(),
                                                userData.get("path").toString(), Integer.parseInt(userData.get("age").toString()),
                                                userData.get("province").toString(), userData.getBoolean("passwordIsChanged"));
                            cartResponder.onUserDataLoaded(user);
                        }
                    }
                });
    }

    public void increaseItemQuantity(ShoppingCartRow existingRow, Toast toast,
                                      int quantity, int stock, String docId, Activity context){
        if (existingRow.getQuantity() != 0) {
            //Quantity previously set plus quantity user wants to add.
            int total = existingRow.getQuantity() + quantity;
            //Determine if final quantity is bigger than available stock of the product or
            //more than 10, which is the most a customer can order of any product
            if (total > stock || total > LIMIT) {
                toast.setText("La cantidad deseada excede el límite del stock o carrito");
                toast.show();
            }else{
                //The product's quantity the user wants to buy is allowed, so update the firebase doc.
                DocumentReference docRef = db.collection("Shopping_cart").document(docId);
                docRef.update("quantity", total).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //If cartResponder is null, that means that this method was not invoked from shopping cart tab.
                        if(cartResponder == null){
                            toast.setText("Cantidad añadida");
                            toast.show();
                            context.finish();
                        }else{
                            getTotalPriceOfUserShoppingCart(existingRow.getOwnerEmail());
                            toast.setText("Actualizando cantidad...");
                            toast.show();
                        }
                    }
                });
            }
        }else{
            toast.setText("No hay cambios que añadir");
            toast.show();
        }

    }
    //It retrieves user's shopping cart
    public void getShoppingCart(String ownerEmail, HashMap<String,Integer> shoppingCart){
        db.collection("Shopping_cart")
                .whereEqualTo("ownerEmail", ownerEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(!task.getResult().getDocuments().isEmpty()) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            for(int i = 0; i < task.getResult().size(); i++) {
                                shoppingCart.put(documents.get(i).get("productId").toString(),
                                                 Integer.parseInt(documents.get(i).get("quantity").toString()));
                            }

                        }
                        cartResponder.onShoppingCartLoaded();
                    }
                });
    }

    //This method discards a specific item from the user's shopping cart.
    public void deleteItem(String ownerEmail, int productId){
        db.collection("Shopping_cart")
                .whereEqualTo("productId",productId)
                .whereEqualTo("ownerEmail", ownerEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        task.getResult().getDocuments().get(0).getReference().delete();
                        getTotalPriceOfUserShoppingCart(ownerEmail);
                    }
                });
    }

    // This method empties user's shopping cart and saves the change in firebase.
    public void clearShoppingCart(String ownerEmail){
        db.collection("Shopping_cart")
                .whereEqualTo("ownerEmail", ownerEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        boolean deleted = false;
                        if(!task.getResult().isEmpty()) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            for(int i = 0; i < documents.size(); i++) {
                                documents.get(i).getReference().delete();
                            }
                            deleted = true;
                        }
                        cartResponder.onShoppingCartEmptied(deleted);
                    }
                });
    }
    //This method commits the purchase and saves it in the corresponding firebase collection.
    public void commitPurchase(int total, String ownerEmail,
                               HashMap<String,Integer> shoppingCart) {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(currentTime);
        Purchase newPurchase = new Purchase(total, formattedDate, ownerEmail,shoppingCart);
        db.collection("Purchase")
                .add(newPurchase)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            clearShoppingCart(ownerEmail);
                            cartResponder.onSuccessfulPurchase(true);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        cartResponder.onSuccessfulPurchase(false);
                    }
                });
    }

    //This method calculates the total price that the products in the shopping cart
    //are worth.
    public void getTotalPriceOfUserShoppingCart(String ownerEmail){
        db.collection("Shopping_cart")
                .whereEqualTo("ownerEmail", ownerEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int total = 0;
                        if(!task.getResult().isEmpty()){

                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            for(int i = 0; i < documents.size(); i++){
                                total += Integer.parseInt(documents.get(i).get("quantity").toString())*Integer.parseInt(documents.get(i).get("price").toString());
                            }

                        }
                        cartResponder.onPriceCalculated(total);
                    }
                });
    }
    //Method meant to be invoked by PurchaseHistoryActivity.
    public void retrieveUserPurchases(String ownerEmail){
        db.collection("Purchase")
                .whereEqualTo("ownerEmail",ownerEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //If query result is not empty, then populate Purchase array and then use interface.
                if(!task.getResult().isEmpty()){
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                    Purchase[] purchases = new Purchase[docs.size()];
                    for(int i = 0; i < docs.size(); i++){
                        purchases[i] = new Purchase(Integer.parseInt(docs.get(i).get("total").toString()),
                                                    docs.get(i).get("purchaseTime").toString(), ownerEmail,
                                                    (HashMap<String, Long>) docs.get(i).get("shoppingCart"), false);
                    }

                    purchaseHistoryReceiver.onHistoryLoaded(purchases);
                }
            }
        });
    }

}


