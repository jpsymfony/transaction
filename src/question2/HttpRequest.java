package question2;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.util.Properties;
import java.util.Enumeration;

import java.net.URL;
import java.net.URLConnection;

/**
 * Classe Utilitaire simplifiee d'interrogation de sites HTTP.
 *
 * @author jm Douin
 */
public class HttpRequest
{
    /**
     * vide, pour une requete sans parametre
     */
    private final static Properties VIDE = new Properties();

    /**
     * Effectue une requete sans parametres
     *
     * @param urlString l'URL choisie
     * @throws si une erreur se produit
     */
    public static String executeGET(String urlString) throws Exception
    {
        return executeGET(urlString, VIDE);
    }

    /**
     * Effectue une requete HTTP,
     *
     * @param urlString l'URL choisie
     * @throws si une erreur se produit
     * @params les parametres de type CGI, places ici dans une instance de Properties
     */
    public static String executeGET(String urlString, Properties params) throws Exception
    {
        String result = new String("");
        try {
            String paramsCGI = "";
            // les parametres (type CGI)
            if (!params.isEmpty()) {
                if (!urlString.endsWith("/")) urlString += "/";

                urlString += "?";
                for (Enumeration e = params.keys(); e.hasMoreElements(); ) {
                    String key = (String) e.nextElement();
                    String value = (String) params.get(key);
                    paramsCGI += key + "=" + value;
                    //System.out.println(key + "=" + value); // debug
                    if (e.hasMoreElements()) paramsCGI += "&";
                }
            }
            //System.out.println("url : " + urlString + paramsCGI);
            URL url = new URL(urlString + paramsCGI);
            URLConnection connection = url.openConnection();

            connection.setDoInput(true);
            // lecture en retour
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine = in.readLine();
            while (inputLine != null) {
                result = result + inputLine;
                inputLine = in.readLine();
            }
            in.close();
        } catch (Exception e) {
            throw new Exception(urlString); // TINI, e.getCause()); // l'exception est ici decoree
        }
        return result;
    }


    /**
     * Effectue une requete sans parametres, method POST
     *
     * @param urlString l'URL choisie
     * @throws si une erreur se produit
     */
    public static String executePOST(String urlString) throws Exception
    {
        return executePOST(urlString, VIDE);
    }

    /**
     * Effectue une requete HTTP, method POST
     *
     * @param urlString l'URL choisie
     * @throws si une erreur se produit
     * @params les parametres de type CGI, places ici dans une instance de Properties
     */
    public static String executePOST(String urlString, Properties params) throws Exception
    {
        String result = new String("");
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            // les parametres (type CGI)
            for (Enumeration e = params.keys(); e.hasMoreElements(); ) {
                String key = (String) e.nextElement();
                String value = (String) params.get(key);
                out.print(key + "=" + value);
                //System.out.println(key + "=" + value); // debug
                if (e.hasMoreElements()) out.print("&");
            }
            out.close();

            // lecture en retour
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine = in.readLine();
            while (inputLine != null) {
                result = result + inputLine;
                inputLine = in.readLine();
            }
            in.close();
        } catch (Exception e) {
            throw new Exception(urlString); // TINI, e.getCause()); // l'exception est ici decoree
        }
        return result;
    }

    /**
     * Mise en place du proxy si necessaire
     * exemple : proxyHost=proxy.cnam.fr proxyPort=3128
     * attention, aucune verification de la validite de l'URL transmise n'est effectuee
     *
     * @param proxyHost adresse du proxy
     * @param proxyPort le port du proxy
     */
    public static void setHttpProxy(String proxyHost, int proxyPort)
    {
        Properties prop = System.getProperties();
        prop.put("proxySet", "true");
        prop.put("http.proxyHost", proxyHost);
        prop.put("http.proxyPort", Integer.toString(proxyPort));
    }


}

