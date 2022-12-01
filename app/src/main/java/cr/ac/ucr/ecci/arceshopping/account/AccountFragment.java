package cr.ac.ucr.ecci.arceshopping.account;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

import at.favre.lib.crypto.bcrypt.BCrypt;
import cr.ac.ucr.ecci.arceshopping.ImageGetter;
import cr.ac.ucr.ecci.arceshopping.LoginActivity;
import cr.ac.ucr.ecci.arceshopping.MainActivity;
import cr.ac.ucr.ecci.arceshopping.PasswordChangeActivity;
import cr.ac.ucr.ecci.arceshopping.R;
import cr.ac.ucr.ecci.arceshopping.databinding.FragmentAccountBinding;
import cr.ac.ucr.ecci.arceshopping.model.EmailManager;
import cr.ac.ucr.ecci.arceshopping.model.User;


public class AccountFragment extends Fragment {
    private User loggedInUser;
    private ImageView user_pic;
    private Button change_pic_button;
    private TextInputLayout til_name;
    private TextView tv_id;
    private TextView tv_email;
    private Spinner province_spinner; // Province dropdown list
    private TextView tv_age;
    private Button age_button;
    private Button update_password_button;
    private Button save_changes_button;
    private FragmentAccountBinding binding;
    private Uri pathToUserPic; //Contains user's profile picture path
    private ImageGetter imageGetter;
    private MaterialDatePicker materialDatePicker;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //Retrieve an instance so we can save changes to db
        getInstances();

        //Retrieve user email so we can retrieve their data from db
        SharedPreferences sp = getActivity().getSharedPreferences("login", MODE_PRIVATE);
        setUserFromDataBase(sp.getString("userEmail", "DEFAULT"));

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("Seleccione una fecha");
        materialDatePicker = materialDateBuilder.build();

        //Retrieve xml elements so we can populate them with user data
        retrieveXmlElements(root);

        return root;
    }
    /**
     * Load user info from Firebase
     * @param email the user email
     * */
    private void setUserFromDataBase(String email){
        db.collection("User")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(!task.getResult().isEmpty()){
                            DocumentSnapshot userData = task.getResult().getDocuments().get(0);
                            loggedInUser = new User(email, userData.get("id").toString(), userData.get("name").toString(),
                                    userData.get("path").toString(), Integer.parseInt(userData.get("age").toString()),
                                    userData.get("province").toString(), userData.getBoolean("passwordIsChanged"));

                            setData();
                            setClickEvents();
                        }
                    }
                });
    }


    /**
    * Connects the buttons with their methods. The method starts when the button is pressed
    */
    private void setClickEvents(){
        change_pic_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageGetter.startPicUpdate();
            }
        });

        save_changes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });

        update_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });

        this.age_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getActivity().getSupportFragmentManager(),
                        "MATERIAL_DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        calculateAge(selection);
                    }
                });
    }

    /**
     * Save all the changes that the user made in his/her account profile. This includes the profile
     * image
     */
    private void saveChanges() {
        //Build sql string as program verifies which fields have been modified
        boolean changes = false;
        String til_name_content = til_name.getEditText().getText().toString();
        if(!loggedInUser.getName().equals(til_name_content)) {
            this.loggedInUser.setName(til_name_content);
            changes = true;
        }

        if(!loggedInUser.getPath().equals(imageGetter.getUriPath())) {
            this.loggedInUser.setPath(imageGetter.getUriPath());
            changes = true;
        }

        int tv_age_content = Integer.valueOf(tv_age.getText().toString());
        if(loggedInUser.getAge() != tv_age_content) {
            this.loggedInUser.setAge(tv_age_content);
            changes = true;
        }

        String province_spinner_content = province_spinner.getSelectedItem().toString();
        if(!loggedInUser.getProvince().equals(province_spinner_content)) {
            this.loggedInUser.setProvince(province_spinner_content);
            changes = true;
        }
        //Only save into db if any field was actually modified
        if(changes) {
            updateUserDetails();
        } else {
            imageGetter.displayMessage("No hay cambios que guardar.");
        }
    }

    /**
     * Update user info in Firebase
     * */
    private void updateUserDetails() {
        db.collection("User")
                .whereEqualTo("email", loggedInUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(!task.getResult().isEmpty()){
                            String docId = task.getResult().getDocuments().get(0).getId();

                            db.collection("User")
                                    .document(docId)
                                    .set(loggedInUser)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused){
                                            Toast.makeText(getContext(), "Cambios guardados exitosamente", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }

    /**
     * Send a randomly generated password to the user's email as his/her new password. Then the user
     * can change it
     */
    private void changePassword(){
        String firstPassword = UUID.randomUUID().toString().substring(0, 16);
        String email = loggedInUser.getEmail();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }
        user.updatePassword(firstPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    EmailManager manager = new EmailManager();
                    manager.sendPasswordEmail(loggedInUser.getName(), email, firstPassword);
                    Toast.makeText(getContext(), "Se le envió un correo temporal",
                            Toast.LENGTH_LONG).show();
                    if (changeInDb(user.getUid())) {
                        mAuth.signOut();
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                } else
                {
                    Toast.makeText(getContext(), "Hubo un error, puede que tenga que cerrar sesión y volver a iniciar", Toast.LENGTH_LONG).show();
                    System.out.println(task.getException());
                    getInstances();
                }
            }
        });
    }

    public void getInstances() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Change the user's password in Cloud FireStone from fireBase
     * @param userId The id to identify the current user
     * @return true if the password was changed in FireBase, false otherwise
     */
    private boolean changeInDb(String userId)
    {
        final boolean[] success = {true};
        db.collection("User").document(userId).update("passwordIsChanged", false).
                addOnCompleteListener( new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful())
                        {
                            System.out.println(task.getException().toString());
                            Toast.makeText(getContext(), "Hubo un error, " +
                                    "intente otra vez", Toast.LENGTH_LONG).show();
                            success[0] = false;
                            System.out.println(task.getException());
                        }
                    }
                });
        return success[0];
    }

    /**
     * Method to calculate the user's current age with his/her birthday
     */
    private void calculateAge(long milliseconds) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(milliseconds);
        System.out.println(calendar.get(Calendar.DAY_OF_MONTH));
        LocalDate date = LocalDate.of(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        int theAge = Period.between(date, LocalDate.now( ZoneId.of( "Pacific/Auckland" ))).getYears();
        if (theAge > 0) {
            this.tv_age.setText(String.valueOf(theAge));
        } else
        {
            Toast.makeText(getContext(), "Fecha inválida, su edad debe ser mayor a 0", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Sets the data to the corresponding views
     */
    private void setData(){
        if(loggedInUser != null) {
            til_name.getEditText().setText(loggedInUser.getName());
            tv_id.setText(loggedInUser.getId());
            tv_email.setText(loggedInUser.getEmail());

            //TODO: Learn to save the provinces array in the strings.xml file so it can be used here and
            //on the register screen
            ArrayList<String> provinces = new ArrayList<String>(Arrays.asList("Alajuela", "Cartago", "Heredia",
                    "Guanacaste", "Puntarenas", "San José", "Limón"));
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    com.google.android.material.R.layout.support_simple_spinner_dropdown_item, provinces);
            province_spinner.setAdapter(adapter);
            //adapter.getPosition() is how we get the index number for the user's province
            province_spinner.setSelection(adapter.getPosition(loggedInUser.getProvince()));
            tv_age.setText(String.valueOf(loggedInUser.getAge()));

            //Get profile pic path from user and turn it into an URI object
            pathToUserPic = Uri.parse(loggedInUser.getPath());

            //Give a context, an image view and a URI object to imageGetter
            imageGetter = new ImageGetter(getContext(), user_pic, pathToUserPic);
            imageGetter.retrieveUserPic();

            //ImageGetter needs to be attached to account fragment's activity so it can start intents
            // and avoid runtime errors
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, imageGetter).commit();
        }
    }

    /**
     * Initiates the views
     */
    private void retrieveXmlElements(View root) {
        user_pic = root.findViewById(R.id.user_pic);
        change_pic_button = root.findViewById(R.id.change_pic_button);
        til_name = root.findViewById(R.id.til_name);
        tv_id = root.findViewById(R.id.tv_id);
        tv_email = root.findViewById(R.id.tv_email);
        province_spinner = root.findViewById(R.id.account_province_spinner);
        tv_age = root.findViewById(R.id.user_age);
        age_button = root.findViewById(R.id.date_button);
        update_password_button = root.findViewById(R.id.change_password_button);
        save_changes_button = root.findViewById(R.id.save_changes_button);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}