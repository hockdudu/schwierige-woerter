package ch.hockdudu.schwierigewoerter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;


public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        utils.darkMode.set(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Display the fragment as the main content
        getFragmentManager().beginTransaction()
                .replace(R.id.content_placeholder_frame, new SettingsFragment())
                .commit();


        confToolbar();
    }

    private void confToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.settings);
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


}

//http://stackoverflow.com/questions/26564400/creating-a-preference-screen-with-support-v21-toolbar
//http://stackoverflow.com/questions/2198788/how-to-connect-a-layout-view-with-an-activity