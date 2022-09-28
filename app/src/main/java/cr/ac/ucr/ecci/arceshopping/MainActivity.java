package cr.ac.ucr.ecci.arceshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout username;
    private TextInputLayout password;
    private ArrayList<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = (TextInputLayout)findViewById(R.id.login_username);
        password = (TextInputLayout)findViewById(R.id.login_password);
        userList = new ArrayList<>();
        // esta es olo de prueba, borrar cuando se implemente almacenamiento
        User user = new User("3", "asdrubal.villegasm@gmail.com", "123", 23,
                "Heredia");
        userList.add(user);
    }

    public void login(View view) {
        String theUsername = username.getEditText().getText().toString();
        String thePassword = password.getEditText().getText().toString();
        boolean validUsername = checkEmail(theUsername);
        boolean validPassword = checkPassword(thePassword);
        if (validUsername && validPassword) {
            User theUser = lookForUser(theUsername, thePassword);
            if (theUser != null) {
                if (theUser.getPass()) {
                    Toast.makeText(this, "Implementar tienda", Toast.LENGTH_LONG).show();
                 } else {
                    Intent intent = new Intent(this, PasswordChange.class);
                    startActivity(intent);
                } //Borrar el toast una vez que el codigo comentado se implemente
            }
        }
    }

    public User lookForUser(String username, String password)
    {
        for (int index = 0; index < userList.size(); index++) {
            User dummyUser = this.userList.get(index);
            if (dummyUser.getEmail().equals(username)) {
                if (dummyUser.getPassword().equals(password)) {
                    return dummyUser;
                }
                break;
            }
        }
        Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
        return null;
    }

    public boolean checkEmail(String theEmail) {
        if (theEmail.length() == 0) {
            username.setError("Escriba su correo electrónico");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(theEmail).matches()) {
            username.setError("Correo electrónico inválido");
            return false;
        }
        username.setError(null);
        return true;
    }

    public boolean checkPassword(String thePassword) {
        if (thePassword.length() == 0) {
            password.setError("Escriba su contraseña");
            return false;
        }
        password.setError(null);
        return true;
    }

    public void goToRegister(View view) {
        Intent registerIntent = new Intent(this, Register.class);
        startActivity(registerIntent);
    }
}