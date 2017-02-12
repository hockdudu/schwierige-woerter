package ch.hockdudu.schwierigewoerter;


import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
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


public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    // Initializes the ones who need to be initialized
    private utils.random random;
    //public String[] history = new String[10];
    private boolean isAudioPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Theme needs to be added before layout draw
        utils.darkMode.set(this);
        utils.darkMode.holder = utils.darkMode.get(this);

        utils.smallScreenMode.holder = utils.smallScreenMode.get(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //random = new getRnd();
        //random = new utils().new random(this);
        random = new  utils.random(this);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        onSend();
        confToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(this.getClass().getSimpleName(), "onResume: Resumed");

        if ((utils.smallScreenMode.get(this) != utils.smallScreenMode.holder) && (
                ((TextView) findViewById(R.id.text1)).getText().toString().isEmpty() ^
                        ((TextView) findViewById(R.id.text3)).getText().toString().isEmpty())) {

            Log.v(this.getClass().getSimpleName(), "onResume: Small screen mode changed");

            utils.smallScreenMode.holder = utils.smallScreenMode.get(this);
            analyseMessage(findViewById(R.id.text_type_here));

        }

        if (utils.darkMode.get(this) != utils.darkMode.holder) {
            Log.v(this.getClass().getSimpleName(), "onResume: Theme changed");
            recreate();
            //((EditText) findViewById(R.id.text_type_here)).setText("");
        }


    }


    public void analyseMessage(View view) {

        String message = ((EditText) findViewById(R.id.text_type_here)).getText().toString();

        if (!message.isEmpty()) {
            doAnalyseMessage(message, random.word);
        }

    }

    public void nextWord(View view) {
        if (!isAudioPlaying) {
            //random = new getRnd();
            random.newRandom(this);

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


    private void doPlayAudio(String assetName) {
        try {
            AssetFileDescriptor afd =  getAssets().openFd(assetName);
            MediaPlayer mp = new MediaPlayer();
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mp.prepare();
            mp.start();
            isAudioPlaying=true;

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

    private void doAnalyseMessage(String message, String randomWord) {

        Log.v(this.getClass().getSimpleName(), "doAnalyseMessage: Class doAnalyseMessage was called");

        TextView t1 = (TextView) findViewById(R.id.text1);
        TextView t2 = (TextView) findViewById(R.id.text2);
        TextView t3 = (TextView) findViewById(R.id.text3);
        t1.setText("");
        t2.setText("");
        t3.setText("");

        if (!utils.smallScreenMode.get(this)) {
            t1.setText(randomWord);
            t2.setText(message);

            if (message.trim().equals(randomWord)) {
                t2.setTextColor(ResourcesCompat.getColor(getResources(), R.color.textRight, null));
            } else {
                t2.setTextColor(ResourcesCompat.getColor(getResources(), R.color.textWrong, null));
            }
        } else {
            t3.setText(randomWord);

            if (message.trim().equals(randomWord)) {
                t3.setTextColor(ResourcesCompat.getColor(getResources(), R.color.textRight, null));
            } else {
                t3.setTextColor(ResourcesCompat.getColor(getResources(), R.color.textWrong, null));
            }
        }
    }

    private void confToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        //True if both (text1 and text3) are empty
        savedInstanceState.putBoolean("isEmpty", ((TextView) findViewById(R.id.text1)).getText().toString().isEmpty() &&
                ((TextView) findViewById(R.id.text3)).getText().toString().isEmpty());

        savedInstanceState.putString("randomWord", random.word);
        savedInstanceState.putString("randomAudio", random.audio);
        savedInstanceState.putStringArray("history", random.history);
        savedInstanceState.putString("message", ((EditText) findViewById(R.id.text_type_here)).getText().toString());

        //Superclass must be called at the end
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Call superclass first!
        super.onRestoreInstanceState(savedInstanceState);

        // If there was any text on "text1" or "text3", it has to be reset (set again)
        if (!savedInstanceState.getBoolean("isEmpty")) {

            doAnalyseMessage(savedInstanceState.getString("message"), savedInstanceState.getString("randomWord"));

        }

        random.audio = savedInstanceState.getString("randomAudio");
        random.word = savedInstanceState.getString("randomWord");
        random.history = savedInstanceState.getStringArray("history");

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
