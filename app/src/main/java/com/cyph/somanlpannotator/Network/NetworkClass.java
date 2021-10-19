package com.cyph.somanlpannotator.Network;

import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 *
 */
public class NetworkClass {
    private final static String SOMA_MODEL_URL = "http://196.1.184.22:5005/model/parse";

    public static String getSomaResponse(String queryString) {
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("Log").child("A").setValue("A");

        try {
            URL requestURL = new URL(SOMA_MODEL_URL);
            firebaseDatabase.getReference().child("Log").child("B").setValue("B");
            httpURLConnection = (HttpURLConnection) requestURL.openConnection();
            firebaseDatabase.getReference().child("Log").child("C").setValue("C");
            httpURLConnection.setDoOutput(true);
            firebaseDatabase.getReference().child("Log").child("D").setValue("D");
            httpURLConnection.setRequestMethod("POST");
            firebaseDatabase.getReference().child("Log").child("E").setValue("E");

            OutputStream outputStream = httpURLConnection.getOutputStream();
            firebaseDatabase.getReference().child("Log").child("F").setValue("F");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            firebaseDatabase.getReference().child("Log").child("G").setValue("G");
            outputStreamWriter.write("{\"text\": \"" + queryString + "\"}");
            firebaseDatabase.getReference().child("Log").child("H").setValue("H");
            outputStreamWriter.flush();
            firebaseDatabase.getReference().child("Log").child("I").setValue("I");
            outputStreamWriter.close();
            firebaseDatabase.getReference().child("Log").child("J").setValue("J");
            outputStream.close();
            firebaseDatabase.getReference().child("Log").child("K").setValue("K");

            httpURLConnection.connect();
            firebaseDatabase.getReference().child("Log").child("L").setValue("L");

            InputStream inputStream = httpURLConnection.getInputStream();
            firebaseDatabase.getReference().child("Log").child("M").setValue("M");
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            firebaseDatabase.getReference().child("Log").child("N").setValue("N");
            StringBuilder stringBuilder = new StringBuilder();
            firebaseDatabase.getReference().child("Log").child("O").setValue("O");

            String line;
            firebaseDatabase.getReference().child("Log").child("P").setValue("P");
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            firebaseDatabase.getReference().child("Log").child("Q").setValue("Q");

            if (stringBuilder.length() == 0) {
                firebaseDatabase.getReference().child("Log").child("R").setValue("R");
                return "";
            }

            firebaseDatabase.getReference().child("Log").child("S").setValue("S");
            return stringBuilder.toString();
        } catch (IOException e) {
            firebaseDatabase.getReference().child("Log").child("T").setValue("T");
            firebaseDatabase.getReference().child("Log").child("U").setValue(e.getMessage());
            firebaseDatabase.getReference().child("Log").child("V").setValue(e.getLocalizedMessage());
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return "";
    }
}
