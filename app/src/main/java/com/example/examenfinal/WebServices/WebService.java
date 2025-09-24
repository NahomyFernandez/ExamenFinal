package com.example.examenfinal.WebServices;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class WebService extends AsyncTask<String, Long, String> {

    //Variable con los datos para pasar al web service
    private Map<String, String> datos;
    //Url del servicio web
    private String url= "http://192.168.1.46/codigobarras/";

    //Actividad para mostrar el cuadro de progreso
    private Context actividad;

    //Resultado
    private String xml=null;

    //Clase a la cual se le retorna los datos dle ws
    private Asynchtask callback=null;

    public Asynchtask getCallback() {
        return callback;
    }
    public void setCallback(Asynchtask callback) {
        this.callback = callback;
    }

    ProgressDialog progDailog;

    /**
     * Crea una estancia de la clase webService para hacer consultas a ws
     * @param urlWebService Url del servicio web
     * @param data Datos a enviar del servicios web
     * @param activity Actividad de donde se llama el servicio web, para mostrar el cuadro de "Cargando"
     * @param callback CLase a la que se le retornara los datos del servicio web
     */
    public  WebService(String urlWebService,Map<String, String> data, Context activity, Asynchtask callback) {
        this.url=urlWebService;
        this.datos=data;
        this.actividad=activity;
        this.callback=callback;
    }
    public WebService() {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progDailog = new ProgressDialog(actividad);
        progDailog.setMessage("Cargando Web Service...");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(true);
        progDailog.show();
    }
    @Override
    protected String doInBackground(String... params) {
        String result = "";
        try {

            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            URL url = new URL(this.url);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod(params[0]);
            if(params.length>2) conn.setRequestProperty("Authorization", params[1] + params[2]);
            conn.setDoInput(true);

            int responseCode = conn.getResponseCode();
            InputStream inputStream = (responseCode == 200) ?
                    conn.getInputStream() : conn.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            result = sb.toString();
            reader.close();
            inputStream.close();
            conn.disconnect();

        } catch (Exception e) {
            Log.e("WebServiceSecure", "Error: " + e.getMessage(), e);
            result = "ERROR: " + e.getMessage();
        }

        return result;

        /*try {

            HttpRequest h=new HttpRequest(this.url,params[0]);
            for (int k=1;k< params.length;k+=2)
            {
                h.header(params[k],params[k+1]);
            }

            String r=  h.form(this.datos).body();

            return r;

        } catch (HttpRequest.HttpRequestException exception) {
            Log.e("doInBackground", exception.getMessage());

            return "Error HttpRequestException";
        } catch (Exception e) {
            Log.e("doInBackground", e.getMessage());
            return "Error Exception " +  e.getMessage();
        }*/
    }
    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        this.xml=response;
        progDailog.dismiss();
        //Retorno los datos
        try {
            callback.processFinish(this.xml);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    public Map<String, String> getDatos() {
        return datos;
    }

    public void setDatos(Map<String, String> datos) {
        this.datos = datos;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Context getActividad() {
        return actividad;
    }

    public void setActividad(Context actividad) {
        this.actividad = actividad;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public ProgressDialog getProgDailog() {
        return progDailog;
    }

    public void setProgDailog(ProgressDialog progDailog) {
        this.progDailog = progDailog;
    }
}