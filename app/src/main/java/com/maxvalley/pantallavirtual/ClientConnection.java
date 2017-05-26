package com.maxvalley.pantallavirtual;

/**
 * Created by Javigabbo on 8/5/17.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.app.Activity;
import android.content.Context;
import android.os.StrictMode;
import android.widget.Toast;

import static com.maxvalley.pantallavirtual.ClientConnection.GET;
import static java.security.AccessController.getContext;



/*
kkkk(new CallBack() {
   @Override
   public void OnSuccess(JSONObject Res) {

   }
   @Override
   public void OnError(String Error) {

   }
});*/

public class ClientConnection {

    public interface CallBack {
        public void OnSuccess(JSONObject Res);

        public void OnError(String Error);
    }

    public static void POST_JSON(final String url, final JSONObject data, final JSONObject Headers, final CallBack cb) {

        new AsyncTask<Void, Void, JSONObject>() {
            String ErrorMsg = null;

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    return _POST_JSON(url.trim(), data, Headers);
                } catch (Exception e) {
                    ErrorMsg = e.getLocalizedMessage();
                }

                return null;
            }

            @Override
            public void onPostExecute(JSONObject result) {
                if (cb != null) {
                    if (ErrorMsg != null)
                        cb.OnError(ErrorMsg);
                    else
                        cb.OnSuccess(result);
                }
            }

        }.execute();
    }


    static JSONObject _POST_JSON(String url, JSONObject data, JSONObject Headers) throws IOException, JSONException {
        if (Headers == null)
            Headers = new JSONObject();

        Headers.put("Content-Type", "application/json");

        byte[] dataBin = null;

        if (data != null)
            dataBin = data.toString().getBytes();


        String Res = POST(url, dataBin, Headers);

        return new JSONObject(Res);

    }

    public static String POST(String url, byte[] data, JSONObject Headers) throws IOException, JSONException {
        InputStream inputStream = null;
        String result = "";

        // create HttpClient
        HttpClient httpclient = new DefaultHttpClient();

        HttpPost post = new HttpPost(url);

        if (data != null) {
            ByteArrayEntity entity = new ByteArrayEntity(data);
            post.setEntity(entity);
        }


        //Headers
        if (Headers != null) {
            Iterator<String> iterator = Headers.keys();

            while (iterator.hasNext()) {
                String Key = (String) iterator.next();

                post.addHeader(Key, Headers.getString(Key));

            }
        }

        /*
        post.addHeader(header)
        Header d = new Header();

        post.setHeaders(headers);*/

        // make GET request to the given URL
        HttpResponse httpResponse = httpclient.execute(post);

        // receive response as inputStream
        inputStream = httpResponse.getEntity().getContent();

        // convert inputstream to string
        if (inputStream != null)
            result = convertInputStreamToString(inputStream);
        else
            result = "Did not work!";


        return result;
    }

    public static String GET(String url) throws ClientProtocolException, IOException {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        InputStream inputStream = null;
        String result = "";

        // create HttpClient
        HttpClient httpclient = new DefaultHttpClient();

        // make GET request to the given URL
        HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
        // receive response as inputStream
        inputStream = httpResponse.getEntity().getContent();

        // convert inputstream to string
        if (inputStream != null)
            result = convertInputStreamToString(inputStream);
        else
            result = "Did not work!";

        /*} catch (Exception e) {
            Log.d("libWindpix", e.getLocalizedMessage());
        }*/

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public boolean isConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }



/*
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            try {
                return GET(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getContext(), "Received!", Toast.LENGTH_SHORT).show();
       }


    }
    */


}

