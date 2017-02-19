package question1;  // VOIR ligne 280, substituer "a completer"
                   /* -----------------------------------------*/

/*
 * Copyright (c) 1998-99 The WebApp Framework.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. The WebApp Framework may be used for evaluation, private or
 * educational purposes without fee.
 *
 * 4. The right to redistribute The WebApp Framework or any part of it
 * as part of free software is hereby granted. Redistribution as part
 * of a commercial product is NOT permitted in any form without prior
 * written permission.
 *
 * 5. Every modification must be notified to The WebApp Framework, i.e.
 * Peter Rossbach and Hendrik Schreiber, and redistribution of the
 * modified code without prior notification is NOT permitted in any form.
 *
 * 6. All advertising materials mentioning features or use of this
 * software must display the following acknowledgment: "This product
 * includes software developed by Peter Rossbach and Hendrik Schreiber
 * (http://www.webapp.de/)."
 *
 * 7. The names "WebApp" and "WebApp Framework" must not be used to
 * endorse or promote products derived from this software without prior
 * written permission.
 *
 * 8. Redistributions of any form whatsoever must retain the following
 * acknowledgment: "This product includes software developed by Peter
 * Rossbach and Hendrik Schreiber (http://www.webapp.de/)."
 *
 * 9. As this release also contains software by Sun Microsystems the
 * following conditions have to be met, too. They apply to the
 * files lib/servlet.jar, lib/mail.jar and lib/activation.jar
 * contained in this release.
 *
 * 10. Java Platform Interface.  Licensee may not modify the Java Platform
 * Interface (JPI, identified as classes contained within the javax
 * package or any subpackages of the javax package), by creating additional
 * classes within the JPI or otherwise causing the addition to or modification
 * of the classes in the JPI.  In the event that Licensee creates any 
 * Java-related API and distribute such API to others for applet or
 * application development, you must promptly publish broadly, an accurate 
 * specification for such API for free use by all developers of Java-based 
 * software.  
 *
 * 11. Restrictions.  Software is confidential copyrighted information of Sun and 
 * title to all copies is retained by Sun and/or its licensors.  Licensee 
 * shall not modify, decompile, disassemble, decrypt, extract, or otherwise 
 * reverse engineer Software.  Software may not be leased, assigned, or 
 * sublicensed, in whole or in part.  Software is not designed or intended 
 * for use in on-line control of aircraft, air traffic, aircraft navigation 
 * or aircraft communications; or in the design, construction, operation or 
 * maintenance of any nuclear facility.  Licensee warrants that it will not 
 * use or redistribute the Software for such purposes.
 *
 * 12. Disclaimer of Warranty.  Software is provided "AS IS," without a warranty 
 * of any kind. *  ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, 
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A 
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED.  
 *
 * 13. Termination.  This License is effective until terminated.  Licensee may 
 * terminate this License at any time by destroying all copies of Software.  
 * This License will terminate immediately without notice from Sun if Licensee
 * fails to comply with any provision of this License.  Upon such termination,
 * Licensee must destroy all copies of Software.
 *
 * 14. Export Regulations.  Software, including technical data, is subject to U.S.
 * export control laws, including the U.S.  Export Administration Act and its
 * associated regulations, and may be subject to export or import regulations
 * in other countries.  Licensee agrees to comply strictly with all such 
 * regulations and acknowledges that it has the responsibility to obtain
 * licenses to export, re-export, or import Software.  Software may not be 
 * downloaded, or otherwise exported or re-exported (i) into, or to a national
 * or resident of, Cuba, Iraq, Iran, North Korea, Libya, Sudan, Syria or any 
 * country to which the U.S. has embargoed goods; or (ii) to anyone on the 
 * U.S. Treasury Department's list of Specially Designated Nations or the U.S.
 * Commerce Department's Table of Denial Orders.
 *
 * THIS SOFTWARE IS PROVIDED BY PETER ROSSBACH AND HENDRIK SCHREIBER
 * "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL PETER
 * ROSSBACH AND HENDRIK SCHREIBER BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * For more information on The WebApp Framework, Peter Rossbach or
 * Hendrik Schreiber, please see <http://www.webapp.de/>.
 *
 */

// package de.webapp.Examples.HttpServer;

import java.io.*;
import java.net.*;
import java.util.*;

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
public class SimpleHttpd2 extends Thread
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
    public static int HTTP_PORT = 8100;

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
            docRoot = new File(".");
            canonicalDocRoot = docRoot.getCanonicalPath();
            ServerSocket listen = new ServerSocket(HTTP_PORT);
            listen.setSoTimeout(SimpleHttpd2.TIME_OUT);
            while (!stopped()) {
                try {
                    SimpleHttpd2 aRequest = new SimpleHttpd2(listen.accept());
                } catch (SocketTimeoutException e) {
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
    public SimpleHttpd2(Socket s)
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
            setHeader("Server", "SimpleHttpd2");
            BufferedReader is = new BufferedReader(new InputStreamReader(s.getInputStream()));
            DataOutputStream os = new DataOutputStream(s.getOutputStream());
            String request = is.readLine();
            System.out.println("[" + InetAddress.getLocalHost().getHostAddress() + "] -- Request: " + request);
            StringTokenizer st = new StringTokenizer(request);
            if ((st.countTokens() == 3) && st.nextToken().equals("GET")) {
                String filename = docRoot.getPath() + st.nextToken();
                if (filename.endsWith("/") || filename.equals(""))
                    filename += "index.html";
                File file = new File(filename);
                if (file.getCanonicalPath().startsWith(canonicalDocRoot))
                    sendDocument(os, file);
                else
                    sendError(SC_FORBIDDEN, os);
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


    // pour les tests, ne pas modifier
    public static boolean stop = false;

    public static boolean stopped()
    {
        return stop;
    }

    public static void stopServer()
    {
        stop = true;
    }

}

// end of class