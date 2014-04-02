package uk.ac.gla.uvnfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Calendar;


public class ProgDeviceActivity extends ActionBarActivity {

    /**
     * declaring UI elements
     */
    public EditText sensorid, measint;
    public Button send, QRbut;
    public TextView nfcstatus;
    public String sensString, measString;
    public int sensInt=0;
    public int measInt=0;
    public byte sensByte, measByte;
    /**
     * came from main activity in NFC program
     */
    private NfcAdapter mAdapter; //reference to NFC adaptor for future use
    private  boolean mInWriteMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prog_device_activity);

        //added RL 27th PM
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = getIntent();

        sensorid =  (EditText)findViewById(R.id.N_ET_SensorID_Program);
        measint = (EditText)findViewById(R.id.N_ET_MeasInt);
        send = (Button)findViewById(R.id.B_Send);
        QRbut = (Button)findViewById(R.id.B_ScanQR_Prog);
        nfcstatus = (TextView)findViewById((R.id.TV_NFCStatus));


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        Intent intent2 = getIntent();
        String message = intent2.getStringExtra(QrProgClass.RESQR);
        sensorid.setText(message);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.prog_device, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_prog_device, container, false);
            return rootView;
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        disableWriteMode();

    }


    public void onClick (View v){
        if (v.getId()==R.id.B_Send){

            if((sensorid.length()!=0)&(measint.length()!=0))    //make sure the user has entered something in the fields
            {

                //Grab data from EditTexts
                sensString = sensorid.getText().toString();
                measString = measint.getText().toString();

                sensInt = (Integer.parseInt(sensString));
                measInt = (Integer.parseInt(measString));
                sensByte = (byte)(Integer.parseInt(sensString));
                measByte = (byte)(Integer.parseInt(measString));

                if((measInt>255) | (sensInt>255)){
                    displayMessage("Entered values out of range, please check and try again.");


                }else{
                    //Tell user to scan tag
                    displayMessage("Touch phone to device to begin write operation ***dev"+sensString+measString);

                    //enable NFC

                    enableWriteMode();
                }
            }else{
                displayMessage("Please make sure you have entered sensor ID and measurement interval");
            }
        }


        /* onClick handler for scanning qr code
        *
        *
        *
         */
        if(v.getId()==R.id.B_ScanQR_Prog){
            Intent intent = new Intent(this, QrProgClass.class);
            this.startActivity(intent);
        }

        /**
         * end of onClick handler
         */



    }


    /**
     * from rl nfc MainActivity
     */
    private void enableWriteMode() {
        mInWriteMode = true;

        // set up a PendingIntent to open the app when a tag is scanned
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] filters = new IntentFilter[] { tagDetected };

        mAdapter.enableForegroundDispatch(this, pendingIntent, filters, null);
    }

    /**
     * from rl nfc MainActivity
     */
    private void disableWriteMode() {
        mAdapter.disableForegroundDispatch(this);
    }


    /**
     * from rl nfc MainActivity
     * Called when our device is scanned executing the PendingIntent
     */
    @Override
    public void onNewIntent(Intent intent) {
        if(mInWriteMode) {
            mInWriteMode = false;

            // write to newly scanned tag
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            writeTag(tag,sensByte,measByte);
        }
    }


    /**Format tag and write NDEF message*/
    private boolean writeTag(Tag tag, byte sensor, byte measint) {

        int x;

        // make application record
        NdefRecord appRecord = NdefRecord.createApplicationRecord("uk.ac.gla.uvnfc");

        //Added for debugging

        displayMessage("***Dev: Entered writeTag function");
        byte[] payload = get_time_date();                 //get the current time and date
        payload[0]=sensor;
        payload[6]=measint;

        byte[] mimeBytes = MimeType.NFC_DEMO.getBytes(Charset.forName("US-ASCII"));
        NdefRecord cardRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes,
                new byte[0], payload);
        NdefMessage message = new NdefMessage(new NdefRecord[] { cardRecord, appRecord});

        try {
            // see if tag is already NDEF formatted
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    displayMessage("Read-only tag.");
                    return false;
                }

                // work out how much space we need for the data
                int size = message.toByteArray().length;
                if (ndef.getMaxSize() < size) {
                    displayMessage("Tag doesn't have enough free space.");
                    return false;
                }

                ndef.writeNdefMessage(message);
                displayMessage("Tag written successfully.");
                return true;
            } else {
                // attempt to format tag
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        displayMessage("Tag written successfully!\nClose this app and scan tag.");
                        return true;
                    } catch (IOException e) {
                        displayMessage("Unable to format tag to NDEF.");
                        return false;
                    }
                } else {
                    displayMessage("Tag doesn't appear to support NDEF format.");
                    return false;
                }
            }
        } catch (Exception e) {
            displayMessage("Failed to write tag");
        }

        return false;
    }

    /**
     * get time and date and format in to correct string for NDEF message to send over NFC
     * written by JL
     * last edited 26/3/14 by JL
     */
    byte[] get_time_date(){

        byte[] now = new byte[7];

        /*get date*/
        final Calendar c = Calendar.getInstance();         //grab an instance of calendar
        now[1] = (byte)(c.get(Calendar.YEAR)-2000);              //get the year, and subtract 2000
        int day = c.get(Calendar.DAY_OF_YEAR);             //get the day of the year
        now[3] = (byte)(day & 0XFF);                      //get the LSB of the day int
        now[2] = (byte)((day>>8) & 0xFF);                  //get the MSB of the day int


        /*get time*/
        now[4] = (byte)(c.get(Calendar.HOUR_OF_DAY));             //get hour of day from calendar
        now[5] = (byte)(c.get(Calendar.MINUTE));                   //get minute of hour from calendar


        return now;

    }


    //function to display text in the textview
    private void displayMessage(String message) {
        nfcstatus.setText(message);
    }


}
