package cr.ac.ucr.ecci.arceshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import javax.mail.Store;

import at.favre.lib.crypto.bcrypt.BCrypt;
import cr.ac.ucr.ecci.arceshopping.db.DbUsers;
import cr.ac.ucr.ecci.arceshopping.model.User;

public class PasswordChangeActivity extends ConnectedActivity {

    private TextInputLayout newPassword;
    private TextInputLayout confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        newPassword = (TextInputLayout) findViewById(R.id.Password_new);
        confirmPassword = (TextInputLayout) findViewById(R.id.Password_confirm);
    }

    public void changePassword(View view) {
        String theNewPassword = newPassword.getEditText().getText().toString();
        String theConfirmPassword = confirmPassword.getEditText().getText().toString();
        boolean validNewPassword = isValidNewPassword(theNewPassword);
        boolean validConfirmPassword = isValidConfirmPassword(theConfirmPassword);
        if (theNewPassword.equals(theConfirmPassword) && validNewPassword && validConfirmPassword) {
            SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);
            String email = sp.getString("userEmail","");

            String hashedPassword = BCrypt.withDefaults().hashToString(12,theNewPassword.toCharArray());

            DbUsers dbUsers = new DbUsers(this);
            boolean passwordUpdated = dbUsers.updateUserPassword(email, hashedPassword, 1);
            if(passwordUpdated) {
                Toast.makeText(this, "Cambio de contraseña exitoso", Toast.LENGTH_LONG).show();
                Intent intent = new Intent( this , StoreActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Ocurrió un error al cambiar la contraseña. Intentelo de nuevo", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isValidNewPassword(String password) {
        if (password.length() == 0) {
            newPassword.setError("Escriba su contraseña");
            return false;
        }
        newPassword.setError(null);
        return true;
    }

    private boolean isValidConfirmPassword(String password) {
        if (password.length() == 0) {
            confirmPassword.setError("Escriba su contraseña");
            return false;
        }
        confirmPassword.setError(null);
        return true;
    }
}