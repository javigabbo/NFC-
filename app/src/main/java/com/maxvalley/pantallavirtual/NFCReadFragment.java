package com.maxvalley.pantallavirtual;

import android.app.DialogFragment;
import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class NFCReadFragment extends DialogFragment implements ClientConnection.CallBack{

    public static final String TAG = NFCReadFragment.class.getSimpleName();

    ClientConnection clientConnection;

    public static NFCReadFragment newInstance() {
        return new NFCReadFragment();

    }


    private TextView mTvMessage;
    private Listener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_read, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {

        mTvMessage = (TextView) view.findViewById(R.id.tv_message);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (MainActivity) context;
        mListener.onDialogDisplayed();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onDialogDismissed();
    }

    public void onNfcDetected(Ndef ndef) {

        readFromNFC(ndef);
    }

    private void readFromNFC(Ndef ndef) {

        try {
            ndef.connect();
            NdefMessage ndefMessage = ndef.getNdefMessage();
            String message = new String(ndefMessage.getRecords()[0].getPayload());
            Log.d(TAG, "readFromNFC: " + message);
            mTvMessage.setText(message);
            System.out.println("EL MENSAJE::::::::::::::::::" + message);
            ndef.close();
            doRequest("http://"+message.trim(), "jfunes@maxvalley.com", "1234");

        } catch (IOException | FormatException e) {
            e.printStackTrace();

        }
    }


    private void doRequest(String nfcUrl, String email, String password) {

        System.out.println("LA URL ES ==================>" + nfcUrl);

        JSONObject obj = new JSONObject();
        JSONObject header = new JSONObject();

        try{
            obj.put("", "");
            obj.put("", "");
        } catch (Exception e){
            e.printStackTrace();
        }



        clientConnection = new ClientConnection();
        clientConnection.POST_JSON(nfcUrl, obj, header, this);

    }


    @Override
    public void OnSuccess(JSONObject Res) {
        System.out.println("PETICIÓN::::::::::::::::: Todo okk");
        String rs = null;
        try {
            rs = Res.getString("success");
            System.out.println("DEVUELVE:::::::::::: " + rs);
        } catch (Exception e){

        }

    }

    @Override
    public void OnError(String Error) {
        System.out.println("ERROR EN LA PETICIÓN:::::::::::" + Error);
    }
}