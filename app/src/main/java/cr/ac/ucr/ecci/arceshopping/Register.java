package cr.ac.ucr.ecci.arceshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;

import cr.ac.ucr.ecci.arceshopping.ProvinceCalculator;

public class Register extends AppCompatActivity {

    private TextInputLayout id;
    private TextInputLayout completeName;
    private TextInputLayout email;
    private TextInputLayout age;
    private Spinner province;
    private ProvinceCalculator provinceCalculator;
    private ArrayList<String> provincias;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        this.id = (TextInputLayout) findViewById(R.id.register_Id);
        this.completeName = (TextInputLayout) findViewById(R.id.register_complete_name);
        this.email = (TextInputLayout) findViewById(R.id.register_email);
        this.age = (TextInputLayout) findViewById(R.id.register_age);
        this.province = (Spinner) findViewById(R.id.register_province_spinner);
        this.provinceCalculator = new ProvinceCalculator();

        this.provincias = new ArrayList<String>(Arrays.asList("San José", "Alajuela", "Cartago", "Heredia",
                "Guanacaste", "Puntarenas", "Limón"));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item, provincias);
        this.province.setAdapter(adapter);

        if (!getLocation()) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        }
        this.province.setSelection(provinceCalculator.calculateProvince(latitude, longitude));
    }

    public void register(View view) {
        String theId = id.getEditText().getText().toString();
        String theCompleteName = completeName.getEditText().getText().toString();
        String theEmail = email.getEditText().getText().toString();
        String theAge = age.getEditText().getText().toString();
        String theProvince = province.getSelectedItem().toString();
        if (checkStrings(theId, theCompleteName, theEmail, theAge, theProvince)) {
            int ageInt = Integer.parseInt(theAge);
            if (checkAge(ageInt))
            {
                Toast.makeText(this, "Exito", Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean checkStrings(String theId, String theCompleteName, String theEmail, String theAge,
                                  String theProvince) {
        if (theId.length() == 0 || theCompleteName.length() == 0 || theEmail.length() == 0 ||
                theAge.length() == 0 || theProvince.length() == 0) {
            Toast.makeText(this, "Debe completar todos los campos",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public boolean checkAge(int theAge) {
        if (theAge <= 0)
        {
            Toast.makeText(this, "La edad debe ser mayor a 0",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public boolean getLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                this.latitude = location.getLatitude();
                this.longitude = location.getLongitude();
            }
            else {
                this.latitude = 0;
                this.longitude = 0;
            }
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
            Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            this.latitude = 0;
            this.longitude = 0;
        }
    }
}