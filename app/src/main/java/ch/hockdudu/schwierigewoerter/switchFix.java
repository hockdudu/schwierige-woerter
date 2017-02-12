package ch.hockdudu.schwierigewoerter;

import android.content.Context;
import android.preference.SwitchPreference;
import android.util.AttributeSet;

//http://stackoverflow.com/questions/20906338/android-switchpreferences-change-together-in-preferenceactivity
//https://code.google.com/p/android/issues/detail?id=26194
//http://stackoverflow.com/questions/15632215/preference-items-being-automatically-re-set
class switchFix extends SwitchPreference {
        public switchFix(Context context) {
            super(context);
        }

        public switchFix(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public switchFix(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }
}
