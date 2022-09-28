package cr.ac.ucr.ecci.arceshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class PasswordChange extends AppCompatActivity {

    private TextInputLayout newPassword;
    private TextInputLayout confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        newPassword = (TextInputLayout) findViewById(R.id.Password_new);
        confirmPassword = (TextInputLayout) findViewById(R.id.Password_confirm);
    }

    protected void changePassword() {
        String theNewPassword = newPassword.getEditText().getText().toString();
        String theConfirmPassword = confirmPassword.getEditText().getText().toString();
        if (theNewPassword.equals(theConfirmPassword)) {
            Toast.makeText(this, "Exito", Toast.LENGTH_LONG).show();
            //aqui hay que obtener el user y cambiar la contraseña
            //luego intent a la pantalla tienda
        } else {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
        }
    }
}