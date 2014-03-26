package uk.ac.gla.uvnfc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MenuActivity extends ActionBarActivity {

    /**
     * declareing UI elements
     */
    public Button prog, querydb, openCV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);


        /**
         * Find and declare all our buttons from the UI
         * RL JL - Tues
         */
        prog = (Button) findViewById(R.id.B_ProgDevice);
        querydb = (Button) findViewById(R.id.B_QueryDb);
        //opencv = placeholder button ***FFD


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
            return rootView;
        }
    }


    /**
     * OnClick handler, dictates what to do when a button is pressed
     * RL - Tues
     */
    public void onClick (View v){
        if (v.getId()==R.id.B_ProgDevice){                              //If the button pressed was ProgDevice...

            Intent intent = new Intent(this, ProgDeviceActivity.class); //Create an intent calling for that activity
            startActivity(intent);                                      //start an instance of it
        }
        if (v.getId()==R.id.B_QueryDb){                                 //If the button pressed was QueryDb...
            Intent intent = new Intent(this, QueryActivity.class);      //Create an intent calling for that activity
            startActivity(intent);                                      //start an instance of it
        }
        /*openCV is a dummy button FFD*/
    }

    public String fStringToHex(String str){

        char[] chars = str.toCharArray();

        StringBuffer hex = new StringBuffer();
        for(int i = 0; i < chars.length; i++){
            if ((i & 1) !=0){
                hex.append(",");
            }
            hex.append(Integer.toHexString((int)chars[i]));
        }

        return hex.toString();
    }

}
