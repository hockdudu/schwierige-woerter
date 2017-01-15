package ch.hockdudu.schwierigewoerter;

import android.app.Activity;
import android.preference.PreferenceManager;


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
            return PreferenceManager.getDefaultSharedPreferences(activity)
                    .getBoolean("pref_key_darkTheme", false);
        }
    }

    public static class smallScreenMode extends Activity {
        public static boolean get(Activity activity) {
            PreferenceManager.setDefaultValues(activity, R.xml.preferences, false);
            return PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(
                    activity.getResources().getString(R.string.key_smallScreen)
                    , false);
        }
    }
}


