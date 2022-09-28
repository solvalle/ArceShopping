package cr.ac.ucr.ecci.arceshopping;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


public class MainActivity extends ConnectedActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        if (sp.getBoolean("logged", true)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}