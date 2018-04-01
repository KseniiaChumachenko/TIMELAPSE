package clientapp.timelapse;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

    /*
     *Receiving and sending of messages
     */
public class ChatActivity extends Activity {

    private EditText textField;
    private Button button;
    private Socket client;
    private PrintWriter printwriter;
    private BufferedReader bufferedReader;
    private String ip;
    private int port;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_chat);
        textField = (EditText) findViewById(R.id.editText1);
        button = (Button) findViewById(R.id.button1);

        ChatOperator chatOperator = new ChatOperator();
        chatOperator.execute();

    }

    /*
     * This AsyncTask create the connection with the server and initialize the
     * chat senders and receivers.
     */
    private class ChatOperator extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            Intent intent = getIntent();
            name = intent.getStringExtra("name");
            ip = intent.getStringExtra("ip");
            port = intent.getIntExtra("port", 0);

            try {
                client = new Socket(ip, port); // Creating the server socket.

                if (client != null) {
                    printwriter = new PrintWriter(client.getOutputStream(), true);
                    InputStreamReader inputStreamReader = new InputStreamReader(client.getInputStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                } else {
                    System.out.println("Server has not bean started on port.");
                }
            } catch (UnknownHostException e) {
                System.out.println("Faild to connect server " + ip);
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Faild to connect server " + ip);
                e.printStackTrace();
            }
            return null;
        }

        /*
         * Following method is executed at the end of doInBackground method.
         */
        @Override
        protected void onPostExecute(Void result) {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final Sender messageSender = new Sender(); // Initialize chat sender AsyncTask.
                    messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });
            Receiver receiver = new Receiver(); // Initialize chat receiver AsyncTask.
            receiver.execute();
        }
    }

    /*
     * This AsyncTask continuously reads the input buffer and show the chat
     * message if a message is available.
     */
    private class Receiver extends AsyncTask<Void, Void, Void> {

        private String message;
        private Timestamp time = new Timestamp(System.currentTimeMillis());
        private DateFormat format = new SimpleDateFormat("h:mm a");
        private String timestamp = format.format(time);

        @Override
        protected Void doInBackground(Void... params) {
            while (true) {
                try {

                    if (bufferedReader.ready()) {
                        message = bufferedReader.readLine();
                        publishProgress(null);
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            final LinearLayout conversation = (LinearLayout) findViewById(R.id.linearConversation);
            TextView chatBubble = new TextView(getApplicationContext());

            conversation.addView(chatBubble);
            chatBubble.setText("   " + "Server:\n" + message + "\n   " + timestamp);

            LinearLayout.LayoutParams textViewParams = (LinearLayout.LayoutParams) chatBubble.getLayoutParams();
            textViewParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            textViewParams.width = 400;
            textViewParams.topMargin = 30;

            chatBubble.setBackgroundResource(R.drawable.bubble_recived);
            textViewParams.gravity = Gravity.LEFT;
            chatBubble.setLayoutParams(textViewParams);
            scrollDown();
        }
    }

    /*
     * This AsyncTask sends the chat message through the output stream.
     */
    private class Sender extends AsyncTask<Void, Void, Void> {

        private String message;
        private Timestamp time = new Timestamp(System.currentTimeMillis());
        private DateFormat format = new SimpleDateFormat("h:mm a");
        private String timestamp = format.format(time);

        @Override
        protected Void doInBackground(Void... params) {
            message = textField.getText().toString();
            printwriter.write(name + ":" + message + "\n");
            printwriter.flush();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            textField.setText(""); // Clear the chat box

            final LinearLayout conversation = (LinearLayout) findViewById(R.id.linearConversation);
            TextView chatBubble = new TextView(getApplicationContext());

            conversation.addView(chatBubble);
            chatBubble.setText("   " + name + ":\n" + message + "\n   " + timestamp);

            LinearLayout.LayoutParams textViewParams = (LinearLayout.LayoutParams) chatBubble.getLayoutParams();
            textViewParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            textViewParams.width = 400;
            textViewParams.topMargin = 30;

            chatBubble.setBackgroundResource(R.drawable.bubble_sent);
            textViewParams.gravity = Gravity.RIGHT;
            chatBubble.setLayoutParams(textViewParams);
            scrollDown();
        }
    }

    private void scrollDown() {
        ScrollView sv = (ScrollView) findViewById(R.id.scrollView);
        sv.fullScroll(View.FOCUS_DOWN);
    }
}