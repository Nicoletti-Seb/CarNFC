package app.mbds.fr.unice.carnfc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import app.mbds.fr.unice.carnfc.entity.Person;
import app.mbds.fr.unice.carnfc.service.RegisterTask;

/**
 * Created by MBDS on 13/04/2017.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";

    private EditText lastname;
    private EditText firstname;
    private EditText email;
    private EditText pass;
    private EditText passVerify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //init listener button
        Button buttonRegister = (Button)findViewById(R.id.register_send);
        buttonRegister.setOnClickListener(this);

        //Components
        lastname = (EditText) findViewById(R.id.register_lastname);
        firstname = (EditText) findViewById(R.id.register_firstname);
        email = (EditText) findViewById(R.id.register_email);
        pass = (EditText) findViewById(R.id.register_pass);
        passVerify = (EditText) findViewById(R.id.register_pass_verify);
    }

    private boolean verifyForm(){
        String emailString = email.getText().toString();
        String passString = pass.getText().toString();
        String passVerifyString = passVerify.getText().toString();

        if(emailString.isEmpty()){
            email.setError(getString(R.string.inscription_error_field_empty));
            email.setText("");
            return false;
        }

        if(passString.isEmpty()){
            pass.setError(getString(R.string.inscription_error_field_empty));
            pass.setText("");
            return false;
        }

        //Identical password
        if(!passString.equals(passVerifyString)){
            String errorMessage = getString(R.string.inscription_error_identical_password);
            pass.setError(errorMessage);
            passVerify.setError(errorMessage);

            pass.setText("");
            passVerify.setText("");
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        //reset error
        pass.setError(null);
        passVerify.setError(null);
        email.setError(null);

        if(!verifyForm()){
            return;
        }

        Person person = new Person();
        person.setLastName(lastname.getText().toString());
        person.setFirstName(firstname.getText().toString());
        person.setEmail(email.getText().toString());
        person.setPassword(pass.getText().toString());

        new RegisterTask(this).execute(person);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
