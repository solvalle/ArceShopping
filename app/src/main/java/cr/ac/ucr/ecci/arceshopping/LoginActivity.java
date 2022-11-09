package cr.ac.ucr.ecci.arceshopping;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import at.favre.lib.crypto.bcrypt.BCrypt;
import cr.ac.ucr.ecci.arceshopping.db.DbUsers;
import cr.ac.ucr.ecci.arceshopping.model.User;

public class LoginActivity extends ConnectedActivity {
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tilEmail = (TextInputLayout)findViewById(R.id.login_email);
        tilPassword = (TextInputLayout)findViewById(R.id.login_password);
        mAuth = FirebaseAuth.getInstance();
    }

    public void login(View view) {
        String email = tilEmail.getEditText().getText().toString();
        String password = tilPassword.getEditText().getText().toString();

        boolean validEmail = isValidEmail(email);
        boolean validPassword = isValidPassword(password);

        if (validEmail && validPassword) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Exito",
                                    Toast.LENGTH_SHORT).show();
                            getReference(user.getUid());

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            });
        }
    }

    public void getReference(String userId)
    {
        DocumentReference reference = FirebaseFirestore.getInstance().collection("User").document(userId);
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        boolean isChanged = document.getBoolean("passwordIsChanged");
                        selectNextActivity(isChanged);
                    } else {
                        System.out.println("Document doesn't exist");
                    }
                } else {
                    System.out.println(task.getException().toString());
                }
            }
        });
    }

    public void selectNextActivity(boolean isChanged)
    {
        Intent intent;
        if (isChanged)
        {
            intent = new Intent(this, MainActivity.class);
        } else
        {
            intent = new Intent(this, PasswordChangeActivity.class);
        }
        startActivity(intent);
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
}