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

/**
 * This class's purpose is to help the users to change their password. First they have to put their email.
 * If the email exists, a randomly generated 6-digit code is send. Then they have to put the code and then
 * a randomly generated password is generated and the user is send to the login screen
 */
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

    /**
     * Sends a randomly generated 6-digit code to the user's email and then shows a dialog where the
     * user can put the code
     */
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

    /**
     * Checks if the email is valid. It doesn't check if the email exists
     */
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

    /**
     * Checks if the entered code is correct. If it is correct, a new randomly generated password is
     * sent to the user's email and then the user is send to the login screen. If the code isn't correct
     * then the user is send to the login screen with a Toast message indicating that entered code was
     * incorrect
     */
    public void checkCode() {
        // if the entered code is incorrect, generate a "incorrect code" message
        if (code.compareTo(tilCode.getEditText().getText().toString()) != 0) {
            Toast.makeText(this, "Código incorrecto", Toast.LENGTH_LONG).show();
        // if the entered code is correct, send a new randomly generated password
        } else {
            String firstPassword = UUID.randomUUID().toString().substring(0, 16);
            String hashedPassword = BCrypt.withDefaults().hashToString(12, firstPassword.toCharArray());
            DbUsers dbUsers = new DbUsers(this);
            String email = tilEmail.getEditText().getText().toString();
            dbUsers.updateUserPassword(email, hashedPassword, 0);
            EmailManager manager = new EmailManager();
            manager.sendPasswordEmail(email, firstPassword);
            System.out.println(firstPassword);
            Toast.makeText(this, "Se le envió una contraseña temporal al correo",
                    Toast.LENGTH_LONG).show();
        }
        // go to the login screen
        Intent intent= new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}