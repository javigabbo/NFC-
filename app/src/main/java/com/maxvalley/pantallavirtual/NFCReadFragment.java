package com.maxvalley.pantallavirtual;

import android.app.DialogFragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import static java.lang.System.out;

import org.json.JSONObject;

import java.io.IOException;


public class NFCReadFragment extends DialogFragment implements ClientConnection.CallBack {

    public static final String TAG = NFCReadFragment.class.getSimpleName();
    private TextView mTvMessage;
    private Listener mListener;
    private ImageButton closeButton;
    ClientConnection clientConnection;

    public NFCReadFragment() {
    }

    public static NFCReadFragment newInstance() {
        return new NFCReadFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_read, container, false);
        initViews(view);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                System.out.println("pop");
            }
        });

        return view;
    }

    private void initViews(View view) {
        mTvMessage = (TextView) view.findViewById(R.id.tv_message);
        closeButton = (ImageButton) view.findViewById(R.id.closeBtn);
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


            String mensajes[] = message.split("/");


            if (mensajes[1].equals("reset")) {
                mTvMessage.setText("Reiniciando los videos en las tres pantalals");
            } else if (mensajes[3].equals("0") && mensajes[5].equals("1")) {
                mTvMessage.setText("Lanzamiento anuncio pantalla 1");
            } else if (mensajes[3].equals("1") && mensajes[5].equals("2")) {
                mTvMessage.setText("Lanzamiento anuncio pantalla 2");
            } else if (mensajes[3].equals("2") && mensajes[5].equals("3")) {
                mTvMessage.setText("Lanzamiento anuncio pantalla 3");
            } else if (mensajes[3].equals("all") && mensajes[5].equals("random")) {
                mTvMessage.setText("Todos los anuncios en las 3 pantallas");
            } else {
                mTvMessage.setText("Tag no reconocido");
            }

            System.out.println("EL MENSAJE::::::::::::::::::" + message);
            ndef.close();
            doRequest("http://" + message.trim(), "jfunes@maxvalley.com", "1234");

        } catch (IOException | FormatException e) {
            e.printStackTrace();

        }
    }


    private void doRequest(String nfcUrl, String email, String password) {

        System.out.println("LA URL ES ==================>" + nfcUrl);

        JSONObject obj = new JSONObject();
        JSONObject header = new JSONObject();

        try {
            obj.put("", "");
            obj.put("", "");
        } catch (Exception e) {
            e.printStackTrace();
        }


        clientConnection = new ClientConnection();
        clientConnection.POST_JSON(nfcUrl, obj, header, this);


        //clientConnection.POST_JSON(nfcUrl, obj, header, this);

    }


    @Override
    public void OnSuccess(JSONObject Res) {
        System.out.println("PETICIÓN::::::::::::::::: Todo okk");

        System.out.println("On success :::::::::" + Res);

        String rs = null;
        try {
            rs = Res.getString("success");
            System.out.println("DEVUELVE:::::::::::: " + rs);
        } catch (Exception e) {

        }

    }

    @Override
    public void OnError(String Error) {
        System.out.println("ERROR EN LA PETICIÓN:::::::::::" + Error);

    }
}