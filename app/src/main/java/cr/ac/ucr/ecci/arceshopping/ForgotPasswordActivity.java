package cr.ac.ucr.ecci.arceshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Random;
import java.util.UUID;

import at.favre.lib.crypto.bcrypt.BCrypt;
import cr.ac.ucr.ecci.arceshopping.db.DbUsers;
import cr.ac.ucr.ecci.arceshopping.model.EmailManager;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputLayout tilEmail;
    private TextInputLayout tilCode;
    private Button dialogButton;
    private String code;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        tilEmail = (TextInputLayout) findViewById(R.id.forgot_password_email);
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_layout);
        tilCode = (TextInputLayout) dialog.findViewById(R.id.dialog_code);
        dialogButton = (Button) dialog.findViewById(R.id.dialog_button);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCode();
            }
        });
    }

    public void changeForgotPassword(View view) {
        String email = tilEmail.getEditText().getText().toString();
        DbUsers dbUsers = new DbUsers(this);
        if (isValidEmail(email)) {
            Random random = new Random();
            int randomNumber = random.nextInt(999999);
            String cypher = String.format("%06d", randomNumber);
            EmailManager manager = new EmailManager();
            manager.sendPasswordEmail(email, cypher);
            code = Integer.toString(randomNumber);
            System.out.println(code);
            dialog.show();
        }
    }

    private boolean isValidEmail(String email) {
        if (email.length() == 0) {
            tilEmail.setError("Escriba su correo electrónico");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Correo electrónico inválido");
            return false;
        }
        DbUsers dbUsers = new DbUsers(this);
        if (dbUsers.selectUser(email) == null)
        {
            tilEmail.setError("Correo electrónico no existe");
            return false;
        }
        tilEmail.setError(null);
        return true;
    }

    public void checkCode() {
        if (code.compareTo(tilCode.getEditText().getText().toString()) != 0) {
            Toast.makeText(this, "Código incorrecto", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            String firstPassword = UUID.randomUUID().toString().substring(0, 16);
            String hashedPassword = BCrypt.withDefaults().hashToString(12, firstPassword.toCharArray());
            System.out.println(firstPassword); //prueba
            DbUsers dbUsers = new DbUsers(this);
            String email = tilEmail.getEditText().getText().toString();
            dbUsers.updateUserPassword(email, hashedPassword, 0);
            EmailManager manager = new EmailManager();
            manager.sendPasswordEmail(email, firstPassword);
            Toast.makeText(this, "Se le envió una contraseña temporal al correo",
                    Toast.LENGTH_LONG).show();
            Intent intent= new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}