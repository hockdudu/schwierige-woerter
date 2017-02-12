package ch.hockdudu.schwierigewoerter;

import android.app.Activity;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.Arrays;
import java.util.Random;


class utils {

    public static class darkMode extends Activity{

        public static boolean holder;

        public static void set(Activity activity) {
            if (get(activity)) {
                activity.setTheme(R.style.AppTheme_Dark);
            } else {
                activity.setTheme(R.style.AppTheme);
            }
        }

        public static boolean get(Activity activity) {
            PreferenceManager.setDefaultValues(activity, R.xml.preferences, false);
            return PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(
                    activity.getResources().getString(R.string.key_darkTheme)
                    , false);
        }
    }

    public static class smallScreenMode extends Activity {

        public static boolean holder;

        public static boolean get(Activity activity) {
            PreferenceManager.setDefaultValues(activity, R.xml.preferences, false);
            return PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(
                    activity.getResources().getString(R.string.key_smallScreen)
                    , false);
        }

    }

    public static class random extends Activity {

        String word;
        String audio;
        String[] history = new String[10];

        random(Activity activity) {
            newRandom(activity);
        }


        void newRandom(Activity activity) {
            while (true) {
                //Gets the words (and audio) from the values.xml as the variable "allWordsArray"
                Resources res = activity.getResources();
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


                //If it isn't repeated, add it to the history (and breaks). Otherwise repeat the loop again
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
}


