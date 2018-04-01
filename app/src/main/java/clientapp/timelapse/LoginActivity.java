package clientapp.timelapse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static clientapp.timelapse.R.layout.activity_login;

/*
 *LoginActivity class bound to activity_login layout get login data and carry them ChatActivity
 */

public class LoginActivity extends Activity {

    private Button buttonNext;
    private EditText username;
    private EditText ip;
    private EditText portNumber;
    private String name;
    private String ipU;
    private int port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_login);
        buttonNext = (Button) findViewById(R.id.buttonNext);
        username = (EditText) findViewById(R.id.username);
        ip = (EditText) findViewById(R.id.ip);
        portNumber = (EditText) findViewById(R.id.portNumber);

        buttonNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                if (username.getText().toString().trim().length() > 0) {
                    name = username.getText().toString();
                    intent.putExtra("name", name);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter username", Toast.LENGTH_LONG).show();
                }
                if (ip.getText().toString().trim().length() > 0) {
                    ipU = ip.getText().toString();
                    intent.putExtra("ip", ipU);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter ip", Toast.LENGTH_LONG).show();
                }
                if (portNumber.getText().toString().trim().length() > 0) {
                    port = Integer.parseInt(portNumber.getText().toString());
                    intent.putExtra("port", port);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter port number", Toast.LENGTH_LONG).show();
                }
                startActivity(intent);
            }


        });
    }
    }
