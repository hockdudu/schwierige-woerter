package ch.hockdudu.schwierigewoerter;


import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    //Initializes the ones who need to be initialized
    private getRnd random;
    private String[] history = new String[10];
    private boolean isAudioPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        utils.darkMode.set(this);
        utils.darkMode.holder = utils.darkMode.get(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        random = new getRnd();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        onSend();
        confToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (utils.darkMode.get(this) != utils.darkMode.holder) {
            recreate(); // TODO: 16/01/17 If user changes the theme, the previous audio won't be saved
            ((EditText) findViewById(R.id.text_type_here)).setText("");
        }
    }

    public class getRnd {

        String word;
        String audio;



        getRnd() {
            while (true) {
                //Gets the words (and audio) from the values.xml as the variable "allWordsArray"
                Resources res = getResources();
                String[] allWordsArray = res.getStringArray(R.array.wordlist);

                //Generates a random number between 0 and 1
                Random rnd = new Random();

                //Multiplies it by the length of the allWordsArray
                int rndInt = rnd.nextInt(allWordsArray.length);

                //Gets the string of that line
                String wordAndAudio = allWordsArray[rndInt];

                //Splits on the ";", saving as an array named wordAndAudioArray
                String[] wordAndAudioArray = wordAndAudio.split(";");

                //Sets the variables
                word = wordAndAudioArray[0];
                audio = wordAndAudioArray[1];


                //If it isn't repeated, break. Otherwise repeat the loop again
                if (!Arrays.asList(history).contains(word)) {
                    //Shifts the array to the side, and replaces the last by the new word
                    for(int i = 0; i < history.length - 1; i++) {
                        history[i] = history[i + 1];
                    }
                    history[history.length - 1] = word;
                    break;
                } else {
                    Log.v(this.getClass().getSimpleName(), "getRnd: Repeated (with the name \"" + word + "\") found. Looping again!");
                }

            }
        }
    }

    public void analyseMessage(View view) {

        EditText editText = (EditText) findViewById(R.id.text_type_here);
        String message = editText.getText().toString();
        if (!message.isEmpty()) {
            TextView t1 = (TextView) findViewById(R.id.text1);
            TextView t2 = (TextView) findViewById(R.id.text2);
            TextView t3 = (TextView) findViewById(R.id.text3);
            t1.setText("");
            t2.setText("");
            t3.setText("");

            if (!utils.smallScreenMode.get(this)) {
                t1.setText(random.word);
                t2.setText(message);

                if (message.trim().equals(random.word)) {
                    t2.setTextColor(ResourcesCompat.getColor(getResources(), R.color.textRight, null));
                } else {
                    t2.setTextColor(ResourcesCompat.getColor(getResources(), R.color.textWrong, null));
                }
            } else {
                t3.setText(random.word);

                if (message.trim().equals(random.word)) {
                    t3.setTextColor(ResourcesCompat.getColor(getResources(), R.color.textRight, null));
                } else {
                    t3.setTextColor(ResourcesCompat.getColor(getResources(), R.color.textWrong, null));
                }
            }
        }
    }

    public void nextWord(View view) {
        if (!isAudioPlaying) {
            random = new getRnd();

            TextView t1 = (TextView) findViewById(R.id.text1);
            TextView t2 = (TextView) findViewById(R.id.text2);
            TextView t3 = (TextView) findViewById(R.id.text3);
            t1.setText("");
            t2.setText("");
            t3.setText("");

            EditText editText = (EditText) findViewById(R.id.text_type_here);
            editText.setText("");

            playAudio(view);
        }
    }

    public void playAudio(View view) {
        if (!isAudioPlaying) {
            doPlayAudio(random.audio);
        }
    }

    private void  doPlayAudio(String assetName) {
        try {
            AssetFileDescriptor afd =  getAssets().openFd(assetName);
            MediaPlayer mp = new MediaPlayer();
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mp.prepare();
            mp.start();
            isAudioPlaying=true;

            //OLDIt never works
            //OLDhttp://stackoverflow.com/questions/8972182

            //http://stackoverflow.com/questions/5365323
            mp.setOnCompletionListener(this);

        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), "doPlayAudio: Exception caught", ex);
        }
    }

    private void onSend() {
        final TextView editText = (EditText) findViewById(R.id.text_type_here);
        editText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    analyseMessage(editText);
                    handled=true;
                }
                return handled;
            }
        });
    }



    private void confToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings_menu_item) {
            Intent intent = new Intent(this, SettingsActivity.class);
            /*EditText editText = (EditText) findViewById(R.id.edit_message);
            String message = editText.getText().toString();
            intent.putExtra(EXTRA_MESSAGE, message);*/
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        View view = findViewById(android.R.id.content);

        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                analyseMessage(view);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
                analyseMessage(view);
                return true;
            case KeyEvent.KEYCODE_TAB:
                nextWord(view);
                findViewById(R.id.text_type_here).requestFocus();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public void onCompletion(MediaPlayer s) {   // TODO: 16/01/17 Fix bug; somewhy this doesn't get called if user clicks the button repeatedly
                                                // TODO: 16/01/17 ^^ It didn't work on VM; maybe it was device specific?^^
        s.release();
        Log.v(this.getClass().getSimpleName(), "doPlayAudio: Audio released");
        isAudioPlaying=false;
    }




//    mediaplayer.setDataSource(context, Uri.parse("android.resource://ch.hockdudu.schwierigewoerter/res/raw/urmp3name");

}
