package question2;

// package de.webapp.Examples.HttpServer;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Server, which is capable of performing
 * the <code>GET</code> command. Compared to {@link SimpleHttp}
 * some improvements are found. For instance, the error- and
 * status codes are set correctly. The Content-Type is set,
 * if a file <code>mime.types</code> is located in the current
 * directory. It should conform the following format:
 * <xmp>
 * .ra=audio/x-realaudio
 * .wav=audio/x-wav
 * .gif=image/gif
 * .jpeg=image/jpeg
 * .jpg=image/jpeg
 * .png=image/png
 * .tiff=image/tiff
 * .html=text/html
 * .htm=text/html
 * .txt=text/plain
 * </xmp>
 *
 * @author Hendrik Schreiber, Peter Rossbach
 * @version $Id: SimpleHttpd2.java,v 1.7 2000/07/15 12:23:39 Hendrik Exp $
 * @see OneShotHttpd
 * @see SimpleHttpd
 */
public class ObserverServer extends Thread
{
    /**
     * Version
     */
    public static String vcid = "$Id: SimpleHttpd2.java,v 1.7 2000/07/15 12:23:39 Hendrik Exp $";

    /**
     * Socket of a request.
     */
    protected Socket s = null;

    /**
     * Document root.
     */
    protected static File docRoot;

    /**
     * Canonical document root.
     */
    protected static String canonicalDocRoot;

    /**
     * The port the server will listen to
     */
    public static int HTTP_PORT = 9080;

    /**
     * CRLF
     */
    public final static String CRLF = "\r\n";

    /**
     * Protocol out server understands.
     */
    public final static String PROTOCOL = "HTTP/1.0 ";

    /**
     * Status code: All OK.
     */
    public final static String SC_OK = "200 OK";

    /**
     * Status code: Bad request.
     */
    public final static String SC_BAD_REQUEST = "400 Bad Request";

    /**
     * Status code: Forbidden request.
     */
    public final static String SC_FORBIDDEN = "403 Forbidden";

    /**
     * Status code: Resource not found.
     */
    public final static String SC_NOT_FOUND = "404 Not Found";

    /**
     * Content type map.
     */
    protected static Properties typeMap = new Properties();


    static {
        // seuls ces type mimes sont renseignes
        typeMap.put(".class", "application/octet-stream");
        typeMap.put(".html", "text/html");
        typeMap.put(".htm", "text/html");
        typeMap.put(".java", "text/html");
    }

    /**
     * Current status code.
     */
    protected String statusCode = SC_OK;

    /**
     * the server time out, osec
     */
    protected static int TIME_OUT = 1000;

    /**
     * Current header.
     */
    protected Hashtable<String, String> myHeaders = new Hashtable<String, String>();


    /**
     * Waits for an <code>GET</code> request and performs it.
     * Objects are searched for relatively to the current directory
     * (Document root = "./"). If no file is specified, only
     * a directory, the file <code>index.html</code> is delivered.<br>
     * If the request is not a <code>GET</code> request, the error
     * message 400, <code>Bad Request</code>, is sent to the client.
     */
    public static void main(String argv[])
    {
        try {
            ObserverServer.stop = false;

            try {
                HTTP_PORT = Integer.parseInt(argv[0]);
            } catch (Exception e) {
            }
            docRoot = new File(".");
            canonicalDocRoot = docRoot.getCanonicalPath();
            ServerSocket listen = new ServerSocket(HTTP_PORT);
            System.out.println("debut de service en " + HTTP_PORT);
            listen.setSoTimeout(ObserverServer.TIME_OUT);

            while (!stopped()) {
                try {
                    ObserverServer aRequest = new ObserverServer(listen.accept());
                } catch (Exception e) {
                }
            }
            listen.close();
        } catch (IOException e) {
            System.err.println("Error: " + e.toString());
        }
    }

    /**
     * Sets the socket of this request and starts the thread.
     *
     * @param s Socket of a request
     */
    public ObserverServer(Socket s)
    {
        this.s = s;
        start();
    }

    /**
     * The actually slogger of this class. The request is parsed
     * and the method {@link #getDocument()} is called
     */
    public void run()
    {
        try {
            setHeader("Server", "Observer");
            BufferedReader is = new BufferedReader(new InputStreamReader(s.getInputStream()));
            DataOutputStream os = new DataOutputStream(s.getOutputStream());
            String request = is.readLine();
            System.out.println("[" + InetAddress.getLocalHost().getHostAddress() + "] -- Request: " + request);
            StringTokenizer st = new StringTokenizer(request);

            if (st.countTokens() == 3) {
                String next = st.nextToken();
                if (next.equals("GET") || next.equals("HEAD")) {
                    String next2 = st.nextToken();

                    String filename = docRoot.getPath() + next2;

                    if (next2.startsWith("/update/")) {
                        String[] parts = next2.split(Pattern.quote("/update/"));
                        String params = parts[1];
                        String result = "true";

                        if (stopped() || params.equals("")) {
                            sendDocument(os, "false");

                            FileWriter fw = new FileWriter("deadObservers.txt", true); //the true will append the new data
                            boolean isPresent = isObserverInList("deadObservers.txt", "http://localhost:" + HTTP_PORT);

                            if (!isPresent) {
                                fw.write("http://localhost:" + HTTP_PORT + "/" + params + "\n");
                            }

                            fw.close();

                        } else {
                            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                                    new FileOutputStream("lastUpdate.txt"), "utf-8"))) {
                                writer.write(params);
                            }
                        }

                        sendDocument(os, result);
                    } else if (next2.startsWith("/lastUpdate/")) {
                        String result = "";
                        try {
                            File file = new File("lastUpdate.txt");
                            Scanner scanner = new Scanner(file);
                            while (scanner.hasNextLine()) {
                                result = scanner.nextLine();
                            }
                            scanner.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        sendDocument(os, result);
                    } else {
                        if (filename.endsWith("/") || filename.equals(""))
                            filename += "index.html";
                        File file = new File(filename);
                        if (file.getCanonicalPath().startsWith(canonicalDocRoot))
                            sendDocument(os, file);
                        else
                            sendError(SC_FORBIDDEN, os);
                    }
                }
            } else {
                sendError(SC_BAD_REQUEST, os);
            }
            is.close();
            os.close();
            s.close();
        } catch (IOException ioe) {
            System.err.println("Error: " + ioe.toString());
        }
    }

    private boolean isObserverInList(String filename, String observer)
    {
        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);

            String sCurrentLine;

            br = new BufferedReader(new FileReader(filename));

            while ((sCurrentLine = br.readLine()) != null) {
                System.out.println("line: " + sCurrentLine);
                if (sCurrentLine.equals(observer)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Properties extractFromParameters(String paramUrl)
    {
        StringTokenizer st = new StringTokenizer(paramUrl, "/&?= ");
        String token = st.nextToken();
        Properties params = new Properties();
        params.put("paramUrl", paramUrl);
        while (st.hasMoreTokens()) {
            String key = st.nextToken(" /&?=");
            String value = st.nextToken(" &?=");
            params.put(key, value);
        }
        return params;
    }


    public void sendDocument(DataOutputStream os, String message) throws IOException
    {
        try {
            os.write("HTTP/1.0 200 OK\r\n".getBytes());
            os.write(new String("Content-Length: " + message.length() + "\r\n").getBytes());
            os.write("Content-Type: text/html\r\n\r\n".getBytes());
            os.write(message.getBytes());
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Reads the file, specified in <code>request</code> and writes it to
     * the OutputStream.<br>
     * If the file could not be found, the error message 404,
     * <code>Not Found</code>, is returned.
     *
     * @param os   Stream, where the requested object is to be copied to.
     * @param file file to copy.
     * @throws IOException in case writing to the <code>DataOutputStream</code>
     *                     fails.
     */
    protected void sendDocument(DataOutputStream os, File file) throws IOException
    {
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            sendStatusLine(os);
            setHeader("Content-Length", (new Long(file.length())).toString());
            setHeader("Content-Type", guessType(file.getPath()));
            sendHeader(os);
            os.writeBytes(CRLF);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, len);
            }
            in.close();
        } catch (FileNotFoundException fnfe) {
            sendError(SC_NOT_FOUND, os);
        }
    }

    /**
     * Sets a status code.
     *
     * @param statusCode status code
     */
    protected void setStatusCode(String statusCode)
    {
        this.statusCode = statusCode;
    }

    /**
     * Gets the status code.
     *
     * @return status code
     */
    protected String getStatusCode()
    {
        return statusCode;
    }

    /**
     * Writes the status line to the consigned <code>DataOutputStream</code>.
     *
     * @param out DataOutputStream where the line is to be written.
     * @throws IOException in case writing fails.
     */
    protected void sendStatusLine(DataOutputStream out) throws IOException
    {
        out.writeBytes(PROTOCOL + getStatusCode() + CRLF);
    }

    /**
     * Sets an header value.
     *
     * @param key   key of the header value.
     * @param value the header value.
     */
    protected void setHeader(String key, String value)
    {
        myHeaders.put(key, value);
    }

    /**
     * Writes the header to the consigned <code>DataOutputStream</code>.
     *
     * @param out DataOutputStream where the header is to be written.
     * @throws IOException in case writing fails.
     */
    protected void sendHeader(DataOutputStream out) throws IOException
    {
        String line;
        String key;
        Enumeration e = myHeaders.keys();
        while (e.hasMoreElements()) {
            key = (String) e.nextElement();
            out.writeBytes(key + ": " + myHeaders.get(key) + CRLF);
        }
    }

    /**
     * Writes an error message to the consigned <code>DataOutputStream</code>.
     *
     * @param out        DataOutputStream where the error message is to be written.
     * @param statusCode status code.
     * @throws IOException in case writing fails.
     */
    protected void sendError(String statusCode, DataOutputStream out) throws IOException
    {
        setStatusCode(statusCode);
        sendStatusLine(out);
        out.writeBytes(CRLF + "<html>" + "<head><title>" + getStatusCode() + "</title></head>" + "<body><h1>" + getStatusCode() + "</h1></body>" + "</html>");
        System.err.println(getStatusCode());
    }

    /**
     * Surmise the <code>Content-Type</code> of the file by means
     * of the file extension.
     *
     * @param filename file name
     * @return Content-Type or "unknown/unknown" in case no
     * appropriate type is found.
     */
    public String guessType(String filename)
    {
        String type = null;
        int i = filename.lastIndexOf(".");
        if (i > 0)
            type = typeMap.getProperty(filename.substring(i));
        if (type == null)
            type = "unknown/unknown";
        return type;
    }

    public static boolean stop = false;

    public static boolean stopped()
    {
        return stop;
    }

    public static void stopServer()
    {
        System.out.println("Serveur stoppe?");
        stop = true;
    }
}

// end of class