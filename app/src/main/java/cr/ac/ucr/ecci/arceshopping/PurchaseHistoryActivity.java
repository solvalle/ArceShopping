package cr.ac.ucr.ecci.arceshopping;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class PurchaseHistoryActivity extends ConnectedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("lol");
        myRef.setValue("Hola, desde el curso CI-0161");
        setContentView(R.layout.activity_purchase_history);
    }
}