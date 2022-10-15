package cr.ac.ucr.ecci.arceshopping.account;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import cr.ac.ucr.ecci.arceshopping.ImageGetter;
import cr.ac.ucr.ecci.arceshopping.R;
import cr.ac.ucr.ecci.arceshopping.databinding.FragmentAccountBinding;
import cr.ac.ucr.ecci.arceshopping.db.DbUsers;
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
    private TextInputLayout til_age;
    private Button update_password_button;
    private Button save_changes_button;
    private FragmentAccountBinding binding;
    private Uri pathToUserPic;
    private ImageGetter imageGetter;
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
                goToPasswordChangeScreen();
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
            if(imageGetter.didUserTakePic()){}
            changesToSqlString += "path = \"" + imageGetter.getUriPath() + "\",";
            this.loggedInUser.setPath(imageGetter.getUriPath());
            System.out.println(this.loggedInUser.getPath());
        }

        int til_age_content = Integer.valueOf(til_age.getEditText().getText().toString());
        if(loggedInUser.getAge() != til_age_content) {
            changesToSqlString += "age = \"" + String.valueOf(til_age_content) + "\",";
            this.loggedInUser.setAge(til_age_content);
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

    private void goToPasswordChangeScreen(){
        //code to redirect here
    }

    private void setData(){
        //TODO: save image in table and retrieve it from user table
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
        til_age.getEditText().setText(String.valueOf(loggedInUser.getAge()));

        pathToUserPic = Uri.parse(loggedInUser.getPath());
        System.out.println(pathToUserPic.toString());
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
        til_age = root.findViewById(R.id.til_age);
        update_password_button = root.findViewById(R.id.change_password_button);
        save_changes_button = root.findViewById(R.id.save_changes_button);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}