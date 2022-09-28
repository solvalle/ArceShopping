package cr.ac.ucr.ecci.arceshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import at.favre.lib.crypto.bcrypt.BCrypt;
import cr.ac.ucr.ecci.arceshopping.db.DbUsers;
import cr.ac.ucr.ecci.arceshopping.model.User;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tilEmail = (TextInputLayout)findViewById(R.id.login_email);
        tilPassword = (TextInputLayout)findViewById(R.id.login_password);
    }

    public void login(View view) {
        String email = tilEmail.getEditText().getText().toString();
        String password = tilPassword.getEditText().getText().toString();

        DbUsers dbUsers = new DbUsers(this);
        User user = dbUsers.selectUser(email);

        boolean validEmail = isValidEmail(email);
        boolean validPassword = isValidPassword(password);
        boolean validCredentials = validateCredentials(user, email, password);

        if (validEmail && validPassword) {
            if(validCredentials) {
                Toast.makeText(this, "Se ingresó correctamente", Toast.LENGTH_LONG).show();
                this.user = user;

                if (user.getPass()) {
                    Toast.makeText(this, "Implementar tienda", Toast.LENGTH_LONG).show();
                    // Intent intent = new Intent(this, StoreActivity.class);
                    // startActivity(intent);
                } else {
                    Intent intent = new Intent(this, PasswordChange.class);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void goToRegister(View view) {
        Intent registerIntent = new Intent(this, Register.class);
        startActivity(registerIntent);
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
        tilEmail.setError(null);
        return true;
    }

    private boolean isValidPassword(String password) {
        if (password.length() == 0) {
            tilPassword.setError("Escriba su contraseña");
            return false;
        }
        tilPassword.setError(null);
        return true;
    }

    private boolean validateCredentials(User user, String email, String password){
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(),user.getPassword());

        return email.compareTo(user.getEmail()) == 0 && result.verified;
    }

}