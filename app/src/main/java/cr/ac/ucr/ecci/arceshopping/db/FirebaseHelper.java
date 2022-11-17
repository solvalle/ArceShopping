package cr.ac.ucr.ecci.arceshopping.db;
import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import cr.ac.ucr.ecci.arceshopping.model.ShoppingCartRow;

public class FirebaseHelper {
    static final int LIMIT = 10;
    private FirebaseFirestore db;
    public FirebaseHelper(){
        db = FirebaseFirestore.getInstance();
    }

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

    private void increaseItemQuantity(ShoppingCartRow newRow, Toast toast,
                                      int quantity, int stock, String docId, Activity context){
        if (newRow.getQuantity() > 0) {
            int total = newRow.getQuantity() + quantity;
            if (total > stock || total > LIMIT) {
                toast.setText("La cantidad deseada excede el límite del stock o carrito");
                toast.show();
            }else{
                DocumentReference docRef = db.collection("Shopping_cart").document(docId);
                docRef.update("quantity", total).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        toast.setText("Cantidad añadida");
                        toast.show();
                        context.finish();
                    }
                });
            }
        }else{
            toast.setText("No hay cambios que añadir");
            toast.show();
        }

    }

    public void getShoppingCart(){}
    public void deleteItem(){}
    public void clearShoppingCart(){}


}
