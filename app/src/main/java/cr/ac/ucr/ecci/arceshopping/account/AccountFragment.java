package cr.ac.ucr.ecci.arceshopping.account;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

import at.favre.lib.crypto.bcrypt.BCrypt;
import cr.ac.ucr.ecci.arceshopping.ImageGetter;
import cr.ac.ucr.ecci.arceshopping.LoginActivity;
import cr.ac.ucr.ecci.arceshopping.R;
import cr.ac.ucr.ecci.arceshopping.databinding.FragmentAccountBinding;
import cr.ac.ucr.ecci.arceshopping.db.DbUsers;
import cr.ac.ucr.ecci.arceshopping.model.EmailManager;
import cr.ac.ucr.ecci.arceshopping.model.User;


public class AccountFragment extends Fragment {
    private User loggedInUser;
    private DbUsers dbUsers;
    private ImageView user_pic;
    private Button change_pic_button;
    private TextInputLayout til_name;
    private TextView tv_id;
    private TextView tv_email;
    private Spinner province_spinner;
    private TextView tv_age;
    private Button age_button;
    private Button update_password_button;
    private Button save_changes_button;
    private FragmentAccountBinding binding;
    private Uri pathToUserPic;
    private ImageGetter imageGetter;
    private MaterialDatePicker materialDatePicker;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //Retrieve an instance so we can save changes to db
        dbUsers = new DbUsers(getActivity());
        //Retrieve user email so we can retrieve their data from db
        SharedPreferences sp = getActivity().getSharedPreferences("login", MODE_PRIVATE);
        loggedInUser = dbUsers.selectUser(sp.getString("userEmail", "DEFAULT"));
        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("Seleccione una fecha");
        materialDatePicker = materialDateBuilder.build();

        //Retrieve xml elements so we can populate them with user data
        retrieveXmlElements(root);
        setData();
        setClickEvents();

        return root;
    }


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
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        calculateAge(materialDatePicker.getHeaderText());
                    }
                });
    }


    private void saveChanges() {
        //Build sql string as program verifies which fields have been modified
        String changesToSqlString = " SET ";
        String til_name_content = til_name.getEditText().getText().toString();
        if(!loggedInUser.getName().equals(til_name_content)) {
            changesToSqlString += "name = \"" +  til_name_content +"\",";
            this.loggedInUser.setName(til_name_content);
        }
/*
        String til_email_content = til_email.getEditText().getText().toString();
        if(!loggedInUser.getEmail().equals(til_email_content)) {
            changesToSqlString += "email = \"" + til_email_content + "\",";
        }

        String til_id_content = til_id.getEditText().getText().toString();
        if(!loggedInUser.getId().equals(til_id_content)) {
            changesToSqlString += "id =\"" + til_id_content + "\",";
        }
*/
        if(!loggedInUser.getPath().equals(imageGetter.getUriPath())) {
            changesToSqlString += "path = \"" + imageGetter.getUriPath() + "\",";
            this.loggedInUser.setPath(imageGetter.getUriPath());
            System.out.println(this.loggedInUser.getPath());
        }

        int tv_age_content = Integer.valueOf(tv_age.getText().toString());
        if(loggedInUser.getAge() != tv_age_content) {
            changesToSqlString += "age = \"" + String.valueOf(tv_age_content) + "\",";
            this.loggedInUser.setAge(tv_age_content);
        }

        String province_spinner_content = province_spinner.getSelectedItem().toString();
        if(!loggedInUser.getProvince().equals(province_spinner_content)) {
            changesToSqlString += "province = \"" + province_spinner_content + "\"";
            this.loggedInUser.setProvince(province_spinner_content);
        }
        //Only save into db if any field was actually modified
        if(changesToSqlString.length() > 6) {
            //Remove last comma if there is any, to avoid syntax errors
            char lastComma = changesToSqlString.charAt(changesToSqlString.length() -1);

            if(lastComma == ',')
            {
                changesToSqlString = changesToSqlString.substring(0, changesToSqlString.length()-1);
                System.out.println(changesToSqlString);
            }
            //Finally, save changes into user's row
            changesToSqlString += " WHERE email = \"" + loggedInUser.getEmail() + "\"";
            if(dbUsers.updateUserDetails(changesToSqlString)){
                /*
                //Update data in memory and update shared preferences
                SharedPreferences sp = getActivity().getSharedPreferences("login", MODE_PRIVATE);
                sp.edit().putString("userEmail",til_email_content).apply();
                loggedInUser = dbUsers.selectUser(sp.getString("userEmail", "DEFAULT"));
                */
                imageGetter.displayMessage("Cambios guardados exitosamente");

            }else{
                imageGetter.displayMessage("Ocurrió un error");
            }
        } else {
            imageGetter.displayMessage("No hay cambios que guardar.");
        }
    }

    private void changePassword(){
        String firstPassword = UUID.randomUUID().toString().substring(0, 16);
        String hashedPassword = BCrypt.withDefaults().hashToString(12, firstPassword.toCharArray());
        String email = loggedInUser.getEmail();
        this.dbUsers.updateUserPassword(email, hashedPassword, 0);
        EmailManager manager = new EmailManager();
        manager.sendPasswordEmail(loggedInUser.getName(), email, firstPassword);
        System.out.println(firstPassword);
        Toast.makeText(getContext(), "Se le envió una contraseña temporal al correo",
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }

    private void calculateAge(String stringDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
        LocalDate date = LocalDate.parse(stringDate, formatter);
        int theAge = Period.between(date, LocalDate.now( ZoneId.of( "Pacific/Auckland" ))).getYears();
        this.tv_age.setText(String.valueOf(theAge));
    }

    private void setData(){
        til_name.getEditText().setText(loggedInUser.getName());
        tv_id.setText(loggedInUser.getId());
        tv_email.setText(loggedInUser.getEmail());

        //TODO: Learn to save the provinces array in the strings.xml file so it can be used here and
        //on the register screen
        ArrayList<String> provinces = new ArrayList<String>(Arrays.asList("Alajuela", "Cartago", "Heredia",
                "Guanacaste", "Puntarenas","San José",  "Limón"));
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