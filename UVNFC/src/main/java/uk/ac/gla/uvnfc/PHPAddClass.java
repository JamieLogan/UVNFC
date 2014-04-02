package uk.ac.gla.uvnfc;

import android.os.AsyncTask;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by jaimelogan on 02/04/2014.
 */
public class PHPAddClass extends AsyncTask<String,Void,String> {

    public PHPAddClass(){

    }

    @Override
    protected void onPreExecute(){
    }

    //String Sensor, String Date, String Time, String UV, String Amb
    @Override
    protected String doInBackground(String... arg0){
        try{
            String link = "http://cnut.eng.gla.ac.uk/dsp5/add_record.php?sensor="
                    +arg0[0]+"&date="+arg0[1]+"&time="+arg0[2]+"&uv="+arg0[3]+"&amb="+arg0[4];
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(link);
            httpclient.execute(post);
        }catch(Exception e){
            return e.getMessage();
        }
        return "data added";
    }

    @Override
    protected void onPostExecute(String result){

    }
}
