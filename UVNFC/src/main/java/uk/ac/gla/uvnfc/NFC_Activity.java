package uk.ac.gla.uvnfc;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import java.util.Calendar;

public class NFC_Activity extends ActionBarActivity{

    private TextView NFCdisp, NFCstatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_activity);

        /**
         * This is deleted in RL CardActivity

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        */


        NFCdisp = (TextView)findViewById(R.id.TV_NFC_Result);
        NFCstatus = (TextView)findViewById(R.id.TV_NFCStatus);

            // see if app was started from a tag and show game console
        Intent intent = getIntent();
        if(intent.getType() != null && intent.getType().equals(MimeType.NFC_DEMO)) {
            Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msg = (NdefMessage) rawMsgs[0];
            NdefRecord deviceRecord = msg.getRecords()[0];
            byte[] NDEFMSG=(deviceRecord.getPayload());

            NFCstatus.setText("DEV *** Tag has been read! \n\t");

            //convert 3 bytes of mem pointer to an int

            int mescount=NDEFMSG[9];
            mescount+=(NDEFMSG[8]<<8);
            mescount+=((NDEFMSG[7]&0x01)<<16);
            int DofY=NDEFMSG[3];
            DofY+=((NDEFMSG[2]&0x03)<<8);
            Calendar mestime = Calendar.getInstance();
            mestime.set(Calendar.YEAR, (NDEFMSG[1]+2000));
            mestime.set(Calendar.DAY_OF_YEAR, DofY);
            mestime.set(Calendar.HOUR_OF_DAY, ((int) NDEFMSG[4]));
            mestime.set(Calendar.MINUTE, ((int) NDEFMSG[5]));

            int mesint= NDEFMSG[6];
            int x;
            String[][] data = new String[mescount][5];
            for(x=0; x<mescount; x++){
                data[x][0]=Byte.toString(NDEFMSG[0]);
                mestime.add(Calendar.MINUTE, (mesint*x));
                data[x][1]=""+mestime.get(Calendar.YEAR)+"-"+mestime.get(Calendar.MONTH)+"-"+mestime.get(Calendar.DAY_OF_MONTH);
                data[x][2]=""+mestime.get(Calendar.HOUR_OF_DAY)+":"+mestime.get(Calendar.MINUTE);
                data[x][3]=""+NDEFMSG[10+(x*2)];
                data[x][4]=""+NDEFMSG[11+(x*2)];
            }






            }

        }
        /*These were used for sending messages across activities, not useful here*/
        //String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        //rTextView.setText(message);


    }



/*

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nfc_, menu);
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

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_nfc, container, false);
            return rootView;
        }
    }
    */
