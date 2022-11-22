package cr.ac.ucr.ecci.arceshopping.db;
import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;

import cr.ac.ucr.ecci.arceshopping.ICartResponder;
import cr.ac.ucr.ecci.arceshopping.model.ShoppingCartRow;
import cr.ac.ucr.ecci.arceshopping.model.User;


public class FirebaseHelper {
    static final int LIMIT = 10;
    private FirebaseFirestore db;
    private ICartResponder cartResponder;

    public FirebaseHelper(){
        db = FirebaseFirestore.getInstance();
    }
    public void setCartResponder(ICartResponder cr) {this.cartResponder=cr;}

    public void insertShoppingCartRow(ShoppingCartRow newRow, Toast toast, int stock, Activity context){
        //Check if incoming product has already been added to sc.
        //If so, check how many stock there is available and if customer
        //hasn't exceeded the allowed maximum.
        db.collection("Shopping_cart")
                .whereEqualTo("ownerEmail", newRow.getOwnerEmail())
                .whereEqualTo("productId", newRow.getProductId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult().getDocuments().isEmpty()){
                            db.collection("Shopping_cart")
                                    .add(newRow)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(Task<DocumentReference> task) {
                                    if(task.isSuccessful()){
                                        toast.setText("Producto añadido");
                                        toast.show();
                                        context.finish();
                                    }
                                }
                            });
                        } else {
                            int quantity = Integer.parseInt(task.getResult().getDocuments().get(0).get("quantity").toString());
                            increaseItemQuantity(newRow, toast, quantity, stock, task.getResult().getDocuments().get(0).getId(), context);
                        }
                    }
                });
    }

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

    public void increaseItemQuantity(ShoppingCartRow newRow, Toast toast,
                                      int quantity, int stock, String docId, Activity context){
        if (newRow.getQuantity() != 0) {
            //Quantity previously set plus quantity user wants to add.
            int total = newRow.getQuantity() + quantity;
            //Determine if final quantity is bigger than available stock of the prouduct or
            //more than 10, the most a customer can order of any product
            if (total > stock || total > LIMIT) {
                toast.setText("La cantidad deseada excede el límite del stock o carrito");
                toast.show();
            }else{
                DocumentReference docRef = db.collection("Shopping_cart").document(docId);
                docRef.update("quantity", total).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //If it is null, that means that this method was not invoked from shopping cart tab.
                        if(cartResponder == null){
                            toast.setText("Cantidad añadida");
                            toast.show();
                            context.finish();
                        }else{
                            getTotalPriceOfUserShoppingCart(newRow.getOwnerEmail());
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

    public void getShoppingCart(String ownerEmail, HashMap<Integer,Integer> shoppingCart){
        db.collection("Shopping_cart")
                .whereEqualTo("ownerEmail", ownerEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(!task.getResult().getDocuments().isEmpty()) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            for(int i = 0; i < task.getResult().size(); i++) {
                                shoppingCart.put(Integer.parseInt(documents.get(i).get("productId").toString()),
                                                 Integer.parseInt(documents.get(i).get("quantity").toString()));
                            }

                        }
                        cartResponder.onShoppingCartLoaded();
                    }
                });
    }
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

}


