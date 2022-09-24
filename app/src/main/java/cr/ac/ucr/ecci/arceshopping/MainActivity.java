package cr.ac.ucr.ecci.arceshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
        User user = new User("Asdrubal", "123", "3",
                "asdrubal.villegasm@gmail.com", 23);
        userList.add(user);
    }

    public void login(View view) {
        String theUsername = username.getEditText().getText().toString();
        String thePassword = password.getEditText().getText().toString();
        if (checkTextfield(theUsername, thePassword) && loginSuceed(theUsername, thePassword)) {
            Toast.makeText(this, "Exito", Toast.LENGTH_LONG).show();
        }
    }

    public boolean checkTextfield(String username, String password) {
        boolean complete = true;
        if (username.length() == 0 || password.length() == 0) {
            complete = false;
            Toast.makeText(this, "Debe poner su nombre de usuario y contraseña",
                    Toast.LENGTH_LONG).show();
        }
        return complete;
    }

    public boolean loginSuceed(String theUsername, String thePassword) {
        for (int index = 0; index < userList.size(); index++) {
            if (this.userList.get(index).getUsername().equals(theUsername)) {
                if (this.userList.get(index).getPassword().equals(thePassword)) {
                    return true;
                }
            }
        }
        Toast.makeText(this, "Usuario o contraseña incorrectos",
                Toast.LENGTH_LONG).show();
        return false;
    }

    public void goToRegister(View view) {
        Intent registerIntent = new Intent(this, Register.class);
        startActivity(registerIntent);
    }
}