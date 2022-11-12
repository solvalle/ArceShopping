package cr.ac.ucr.ecci.arceshopping;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

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

import java.util.concurrent.Executor;

import at.favre.lib.crypto.bcrypt.BCrypt;
import cr.ac.ucr.ecci.arceshopping.db.DbUsers;
import cr.ac.ucr.ecci.arceshopping.model.User;

public class LoginActivity extends ConnectedActivity {
    private static final int REQUEST_CODE = 1010;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private Button biometricLoginButton;
    private FirebaseAuth mAuth;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Executor executor;
    private SharedPreferences sp;
    ConstraintLayout mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tilEmail = (TextInputLayout)findViewById(R.id.login_email);
        tilPassword = (TextInputLayout)findViewById(R.id.login_password);
        biometricLoginButton = findViewById(R.id.biometric_login);
        mAuth = FirebaseAuth.getInstance();
        sp = getSharedPreferences("login",MODE_PRIVATE);
        if (checkBiometricCompability())
        {
            buildFingerPrint();
        } else {
            System.out.println("invisible");
            biometricLoginButton.setVisibility(View.INVISIBLE);
        }
    }

    private boolean checkBiometricCompability()
    {
        boolean compatible = false;
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case 0:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                System.out.println("compatible");
                compatible = true;
                break;
            case 11:
                /**
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, REQUEST_CODE);
                break;
                 */
                Toast.makeText(this, "No tiene una huella registrada, hagalo en la configuración de su telefono",
                        Toast.LENGTH_LONG);
                break;
            default:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                break;
        }
        return compatible;
    }

    private void buildFingerPrint()
    {
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                                "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                String email = sp.getString("userEmail", "DEFAULT");
                String password = sp.getString("userPassword", "DEFAULT");
                System.out.println(email + " email");
                System.out.println(password+ "pas");
                if (email != null && email.compareTo("")!=0 && password != null && password.compareTo("")!=0) {
                    signIn(email, password, true);
                } else
                {
                    Toast.makeText(getApplicationContext(), "Debe usar sus credenciales al menos " +
                                    "una vez antes de usar la huella", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                                Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();
        biometricLoginButton.setOnClickListener(view -> {
            biometricPrompt.authenticate(promptInfo);
        });

    }

    public void login(View view) {
        String email = tilEmail.getEditText().getText().toString();
        String password = tilPassword.getEditText().getText().toString();

        boolean validEmail = isValidEmail(email);
        boolean validPassword = isValidPassword(password);

        if (validEmail && validPassword) {
            sp.edit().putString("userEmail" , email).apply();
            sp.edit().putString("userPassword" , password).apply();
            signIn(email, password, false);
        }
    }

    public void signIn(String email, String password, boolean fingerprint)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Exito",
                                    Toast.LENGTH_SHORT).show();
                            if (fingerprint) {
                                getReference(user.getUid());
                            } else {
                                selectNextActivity(true);
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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