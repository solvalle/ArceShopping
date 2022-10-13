package cr.ac.ucr.ecci.arceshopping.account;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;

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
    private TextInputLayout til_id;
    private TextInputLayout til_email;
    private Spinner province_spinner;
    private TextInputLayout til_age;
    private Button update_password_button;
    private Button save_changes_button;
    private FragmentAccountBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        dbUsers = new DbUsers(getActivity());
        SharedPreferences sp = getActivity().getSharedPreferences("login", MODE_PRIVATE);
        loggedInUser = dbUsers.selectUser(sp.getString("userEmail", "DEFAULT"));
        retrieveXmlElements(root);
        setData();
        return root;
    }

    private void setData(){
        //TODO: save image in table and retrieve it from user table
        til_name.getEditText().setText(loggedInUser.getName());
        til_id.getEditText().setText(loggedInUser.getId());
        til_email.getEditText().setText(loggedInUser.getEmail());

        //TODO: Learn to save the provinces array in the strings.xml file so it can be used here and
        //on the register screen
        ArrayList<String> provinces = new ArrayList<String>(Arrays.asList("Alajuela", "Cartago", "Heredia",
                "Guanacaste", "Puntarenas","San José",  "Limón"));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item, provinces);
        province_spinner.setAdapter(adapter);
        //adapter.getPosition() is how we get the index number for the user's province
        province_spinner.setSelection(adapter.getPosition(loggedInUser.getProvince()));
        til_age.getEditText().setText(loggedInUser.getAge());
    }
    private void retrieveXmlElements(View root) {
        user_pic = root.findViewById(R.id.user_pic);
        change_pic_button = root.findViewById(R.id.change_pic_button);
        til_name = root.findViewById(R.id.til_name);
        til_id = root.findViewById(R.id.til_id);
        til_email = root.findViewById(R.id.til_email);
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