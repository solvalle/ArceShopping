package cr.ac.ucr.ecci.arceshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

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
import cr.ac.ucr.ecci.arceshopping.model.User;

public class RegisterActivity extends ConnectedActivity {
    private TextInputLayout id;
    private TextInputLayout completeName;
    private TextInputLayout email;
    private TextInputLayout age;
    private Spinner province;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.id = (TextInputLayout) findViewById(R.id.register_Id);
        this.completeName = (TextInputLayout) findViewById(R.id.register_complete_name);
        this.email = (TextInputLayout) findViewById(R.id.register_email);
        this.age = (TextInputLayout) findViewById(R.id.register_age);
        this.province = (Spinner) findViewById(R.id.register_province_spinner);
        ProvinceCalculator provinceCalculator = new ProvinceCalculator();

        ArrayList<String> provinces = new ArrayList<String>(Arrays.asList("San José", "Alajuela", "Cartago", "Heredia",
                "Guanacaste", "Puntarenas", "Limón"));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item, provinces);
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
        if (checkStrings(theId, theCompleteName, theEmail, theAge)) {
            String firstPassword = UUID.randomUUID().toString().substring(0, 16);
            String hashedPassword = BCrypt.withDefaults().hashToString(12, firstPassword.toCharArray());
            System.out.println(firstPassword);
            DbUsers dbUsers = new DbUsers(this);
            User user = dbUsers.selectUser(theEmail);
            if (user == null) {
                //empty space is path to user
                long insert_id = dbUsers.insertUser(theEmail, theId, theCompleteName, "",Integer.parseInt(theAge), theProvince, hashedPassword);
                if (insert_id > 0) {
                    sendPasswordEmail(theEmail, firstPassword);

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

    public void sendPasswordEmail(String receiverEmail, String password) {
        String senderEmail = "swapitecci@gmail.com";
        String passwordSenderEmail = "azakfdtukfskysdy";
        String host = "smtp.gmail.com";

        Properties properties = System.getProperties();

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        try {
            Session session = Session.getDefaultInstance(properties,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(senderEmail, passwordSenderEmail);
                        }
                    });

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));

            mimeMessage.setSubject("ArceShopping: clave temporal");
            mimeMessage.setText("¡Gracias por registrarse en ArceShopping!\n\nSu nueva clave temporal es: " + password);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } catch (MessagingException e) {
            e.printStackTrace();
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