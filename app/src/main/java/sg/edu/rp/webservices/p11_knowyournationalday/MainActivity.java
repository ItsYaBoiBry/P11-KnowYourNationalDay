package sg.edu.rp.webservices.p11_knowyournationalday;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    ListView lvFacts;
    ArrayAdapter<String> aaFacts;
    String[] factsList = {"Singapore National Day is on 9 Aug", "Singapore is 52 years old",
            "Theme is '#OneNationTogether'"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accessCodeDialog();

        lvFacts = (ListView) findViewById(R.id.listViewFacts);

        aaFacts = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, factsList);
        lvFacts.setAdapter(aaFacts);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_quit) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Quit?");

            builder.setPositiveButton("QUIT", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });

            builder.setNegativeButton("NOT REALLY", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        } else if (item.getItemId() == R.id.action_send) {
            String[] list = new String[]{"Email", "SMS"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Which is your freest weekday?");

            builder.setItems(list, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int option) {

                    final String title = "DO YOU KNOW ABOUT THESE FACTS?";
                    final String content = "\n1. " + factsList[0]
                            + "\n2. " + factsList[1]
                            + "\n3. " + factsList[2];

                    if (option == 0) {

                        Intent email = new Intent(Intent.ACTION_SEND);
                        // Put essentials like email address, subject & body text
                        email.putExtra(Intent.EXTRA_EMAIL,
                                new String[]{"bryanlowsk@gmail.com"});
                        email.putExtra(Intent.EXTRA_SUBJECT,
                                "Facts for National Day");
                        email.putExtra(Intent.EXTRA_TEXT,
                                title + content);

                        email.setType("message/rfc822");

                        startActivity(email);
                    } else {
                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                        sendIntent.putExtra("sms_body", title + content);
                        sendIntent.setType("vnd.android-dir/mms-sms");
                        startActivity(sendIntent);
                    }
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        } else if (item.getItemId() == R.id.action_quiz) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final LinearLayout quiz = (LinearLayout) inflater.inflate(R.layout.quiz, null);

            final RadioGroup rg1 = (RadioGroup) quiz.findViewById(R.id.radioGroup1);
            final RadioGroup rg2 = (RadioGroup) quiz.findViewById(R.id.radioGroup2);
            final RadioGroup rg3 = (RadioGroup) quiz.findViewById(R.id.radioGroup3);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Test Yourself!")
                    .setView(quiz)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            int score = 0;

                            int selectedButtonId1 = rg1.getCheckedRadioButtonId();
                            int selectedButtonId2 = rg2.getCheckedRadioButtonId();
                            int selectedButtonId3 = rg3.getCheckedRadioButtonId();
                            // Get the radio button object from the Id we had gotten above
                            RadioButton rb1 = (RadioButton) quiz.findViewById(selectedButtonId1);
                            RadioButton rb2 = (RadioButton) quiz.findViewById(selectedButtonId2);
                            RadioButton rb3 = (RadioButton) quiz.findViewById(selectedButtonId3);
                            if (rb1.getText().equals("No")) {
                                score += 1;
                            }

                            if(rb2.getText().equals("Yes")){
                                score += 1;
                            }
                            if(rb3.getText().equals("Yes")){
                                score += 1;
                            }
                            Toast.makeText(MainActivity.this,
                                    "Your Score is " + score, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("DON'T KNOW LAH", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    public void accessCodeDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout passPhrase = (LinearLayout) inflater.inflate(R.layout.login, null);
        final EditText etAccessCode = (EditText) passPhrase.findViewById(R.id.editTextAccessCode);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Login")
                .setView(passPhrase)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (etAccessCode.getText().toString().equals("738964")) {
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref",0);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("code","738964");
                            editor.apply();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "Wrong code! Try again later", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                })
                .setNegativeButton("No Access Code", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.SECOND, 0);

                        Intent intent = new Intent(MainActivity.this,
                                ScheduledNotificationReceiver.class);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                MainActivity.this, 1234,
                                intent, PendingIntent.FLAG_CANCEL_CURRENT);

                        AlarmManager am = (AlarmManager)
                                getSystemService(Activity.ALARM_SERVICE);
                        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                                pendingIntent);

                        finish();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String code = prefs.getString("code",null);

        boolean isLogin = false;

        if (isLogin == false){
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout passPhrase = (LinearLayout) inflater.inflate(R.layout.login, null);
            final EditText etAccessCode = (EditText) passPhrase.findViewById(R.id.editTextAccessCode);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please Login")
                    .setView(passPhrase)
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            if (etAccessCode.getText().toString().equals("738964")) {
                                dialog.dismiss();
                            } else {
                                Toast.makeText(MainActivity.this, "Wrong code! Try again later", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    })
                    .setNegativeButton("No Access Code", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Calendar cal = Calendar.getInstance();
                            cal.add(Calendar.SECOND, 0);

                            Intent intent = new Intent(MainActivity.this,
                                    ScheduledNotificationReceiver.class);

                            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                    MainActivity.this, 1234,
                                    intent, PendingIntent.FLAG_CANCEL_CURRENT);

                            AlarmManager am = (AlarmManager)
                                    getSystemService(Activity.ALARM_SERVICE);
                            am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                                    pendingIntent);

                            finish();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            super.onResume();

        }



    }
}