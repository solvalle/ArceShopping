package cr.ac.ucr.ecci.arceshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import javax.mail.Store;

import at.favre.lib.crypto.bcrypt.BCrypt;
import cr.ac.ucr.ecci.arceshopping.db.DbUsers;
import cr.ac.ucr.ecci.arceshopping.model.User;

public class LoginActivity extends ConnectedActivity {
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tilEmail = (TextInputLayout)findViewById(R.id.login_email);
        tilPassword = (TextInputLayout)findViewById(R.id.login_password);
    }

    public void login(View view) {
        String email = tilEmail.getEditText().getText().toString();
        String password = tilPassword.getEditText().getText().toString();

        boolean validEmail = isValidEmail(email);
        boolean validPassword = isValidPassword(password);

        if (validEmail && validPassword) {
            DbUsers dbUsers = new DbUsers(this);
            User user = dbUsers.selectUser(email);
            boolean validCredentials = validateCredentials(user, email, password);

            if(validCredentials) {
                Toast.makeText(this, "Se ingresó correctamente", Toast.LENGTH_LONG).show();
                SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);
                sp.edit().putBoolean("logged" , true).apply();
                sp.edit().putString("userEmail" , email).apply();
                if (user.getPasswordIsChanged()) {
                    Toast.makeText(this, "Ir a tienda", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent( this , StoreActivity.class );
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, PasswordChangeActivity.class);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void goToRegister(View view) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    public void goToForgotPassword(View view) {
        Intent forgotPasswordIntent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(forgotPasswordIntent);
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