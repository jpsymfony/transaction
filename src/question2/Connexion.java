package question2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.util.Properties;
import java.util.Enumeration;

import java.net.URL;
import java.net.URLConnection;

public class Connexion extends Thread
{
    private String url;
    private String parametres;
    private String result;

    public Connexion(String url, String parametres)
    {
        if (!url.endsWith("/")) url += "/";
        if (parametres != null && !parametres.equals("")) {
            url = url + "?" + parametres;
        }
        this.url = url;
        this.result = new String("");
        this.start();
    }

    public Connexion(String url, Properties parametres)
    {
        if (!url.endsWith("/")) url += "/";
        if (parametres != null && !parametres.isEmpty()) {
            url += "?";
            String paramsCGI = "";
            for (Enumeration e = parametres.keys(); e.hasMoreElements(); ) {
                String key = (String) e.nextElement();
                String value = (String) parametres.get(key);
                paramsCGI += key + "=" + value;
                //System.out.println(key + "=" + value); // debug
                if (e.hasMoreElements()) paramsCGI += "&";
            }
            url = url + paramsCGI;
        }

        this.url = url;
        this.result = new String();
        this.start();
    }

    public String result()
    {
        try {
            this.join();
        } catch (InterruptedException ie) {
            //ie.printStackTrace();
        }
        return result;
    }

    public void run()
    {
        this.result = new String();
        try {
            URL urlConnection = new URL(url);
            URLConnection connection = urlConnection.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine = in.readLine();
            while (inputLine != null) {
                result += inputLine;
                inputLine = in.readLine();
            }
            in.close();
        } catch (Exception e) {
            this.result = "exception " + e.getMessage();
            //e.printStackTrace();
        }
    }

}