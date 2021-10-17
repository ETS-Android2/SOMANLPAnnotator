package com.cyph.somanlpannotator.Network;

import android.net.Uri;

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

        try {
            URL requestURL = new URL(SOMA_MODEL_URL);
            httpURLConnection = (HttpURLConnection) requestURL.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");

            OutputStream outputStream = httpURLConnection.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            outputStreamWriter.write("{\"text\": \"" + queryString + "\"}");
            outputStreamWriter.flush();
            outputStreamWriter.close();
            outputStream.close();

            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }

            if (stringBuilder.length() == 0) {
                return "";
            }

            return stringBuilder.toString();
        } catch (IOException e) {
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
