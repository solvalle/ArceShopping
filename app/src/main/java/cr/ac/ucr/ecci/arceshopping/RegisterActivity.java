package cr.ac.ucr.ecci.arceshopping;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import at.favre.lib.crypto.bcrypt.BCrypt;
import cr.ac.ucr.ecci.arceshopping.db.DbUsers;
import cr.ac.ucr.ecci.arceshopping.model.EmailManager;
import cr.ac.ucr.ecci.arceshopping.model.User;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class RegisterActivity extends ConnectedActivity {
    private TextInputLayout id;
    private TextInputLayout completeName;
    private TextInputLayout email;
    private TextInputLayout age;
    private Spinner province;
    ProvinceCalculator provinceCalculator;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    private double latitude;
    private double longitude;
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.id = (TextInputLayout) findViewById(R.id.register_Id);
        this.completeName = (TextInputLayout) findViewById(R.id.register_complete_name);
        this.email = (TextInputLayout) findViewById(R.id.register_email);
        this.age = (TextInputLayout) findViewById(R.id.register_age);
        this.province = (Spinner) findViewById(R.id.register_province_spinner);
        provinceCalculator = new ProvinceCalculator();
        ArrayList<String> provinces = new ArrayList<String>(Arrays.asList("San José", "Alajuela", "Cartago", "Heredia",
                "Guanacaste", "Puntarenas", "Limón"));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item, provinces);
        this.province.setAdapter(adapter);

        checkPermissions();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

    }

    public void register(View view) {
        String theId = id.getEditText().getText().toString();
        String theCompleteName = completeName.getEditText().getText().toString();
        String theEmail = email.getEditText().getText().toString();
        String theAge = age.getEditText().getText().toString();
        String theProvince = province.getSelectedItem().toString();
        if (checkStrings(theId, theCompleteName, theEmail, theAge)) {
            String firstPassword = UUID.randomUUID().toString().substring(0, 16);
            String hashedPassword = BCrypt.withDefaults().hashToString(12, firstPassword.toCharArray());
            DbUsers dbUsers = new DbUsers(this);
            User user = dbUsers.selectUser(theEmail);
            if (user == null) {
                //empty space is path to user
                long insert_id = dbUsers.insertUser(theEmail, theId, theCompleteName, "",Integer.parseInt(theAge), theProvince, hashedPassword);
                if (insert_id > 0) {
                    EmailManager emailManager = new EmailManager();
                    System.out.println(firstPassword);
                    emailManager.sendPasswordEmail(theCompleteName,theEmail, firstPassword);
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Error al registrar", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Correo electronico ya registrado", Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean checkStrings(String theId, String theCompleteName, String theEmail, String theAge) {
        boolean validID = checkID(theId);
        boolean validName = checkName(theCompleteName);
        boolean validEmail = checkEmail(theEmail);
        boolean validAge = checkAge(theAge);
        if (validID && validName && validEmail && validAge) {
            return true;
        }
        Toast.makeText(this, "Tiene campos sin completar", Toast.LENGTH_LONG).show();
        return false;
    }

    public boolean checkID(String TheID) {
        if (TheID.length() == 0) {
            id.setError("Debe ingresar su número de identidad");
            return false;
        }
        id.setError(null);
        return true;
    }

    public boolean checkName(String TheName) {
        if (TheName.length() == 0) {
            completeName.setError("Debe ingresar su nombre completo");
            return false;
        }
        Pattern patron = Pattern.compile("^[a-zA-Z ]+$");
        if (!patron.matcher(TheName).matches()) {
            completeName.setError("nombre invalido");
            return false;
        }
        if (TheName.length() > 30) {
            completeName.setError("Su nombre no puede ser mayor a 30 caracteres");
        }
        completeName.setError(null);
        return true;
    }

    public Boolean checkEmail(String theEmail) {
        if (theEmail.length() == 0) {
            email.setError("Debe ingresar su número de correo electrónico");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(theEmail).matches()) {
            email.setError("Correo electrónico inválido");
            return false;
        }
        email.setError(null);
        return true;
    }

    public boolean checkAge(String theAge) {
        if (theAge.length() == 0) {
            age.setError("Debe ingresar su edad");
            return false;
        }
        if (Integer.parseInt(theAge) <= 0)
        {
            age.setError("La edad debe ser mayor a 0");
            return false;
        }
        age.setError(null);
        return true;
    }

    public void checkPermissions() {


        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        } else {
            getLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Permiso Denegado", Toast.LENGTH_SHORT).show();
                this.latitude = 0;
                this.longitude = 0;
                this.province.setSelection(provinceCalculator.calculateProvince(latitude, longitude));
            }
        }
    }

    private void getLocation() {
        try {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest,
                    new LocationCallback()
            {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LocationServices.getFusedLocationProviderClient(RegisterActivity.this).removeLocationUpdates(this);
                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                        int latestLocationIndex = locationResult.getLocations().size() - 1;
                        latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                        longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                        System.out.println(latitude + " " + longitude);
                        province.setSelection(provinceCalculator.calculateProvince(latitude, longitude));
                    }
                }

            }, Looper.myLooper());

        }catch (Exception e){
            System.out.println(e);
        }
    }
}