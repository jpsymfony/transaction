package question1;

import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * Simple WebClient, which is capable
 * of fetching a website by the
 * specified address and print it to
 * the standard output.
 *
 * @author Hendrik Schreiber, Peter Roﬂbach
 * @version $Id: SimpleWebClient.java,v 1.11 2000/07/15 12:23:39 Hendrik Exp $
 */
public class SimpleWebClient
{
    public static void main(String args[])
    {
        // host name
        String host;
        // requested filename,
        String file;
        // Socket, where the connection
        // is to be created
        Socket socket;
        // OutputStream, where the GET
        // command is written
        OutputStream out = null;
        // InputStream, where the reply from
        // the webserver is being read.
        InputStream in = null;
        if (args.length != 1) {
            System.err.println("Usage : java SimpleWebClient <URL without protocol name>");
            System.err.println("Example: java SimpleWebClient www.yahoo.com/index.html");
            System.exit(1);
        }
        // parse parameters
        int delimiter = args[0].indexOf('/');
        if (delimiter == -1) {
            // in case no '/' occurs in the argument,
            // presume it is the hostname, and
            // request the file '/'
            host = args[0];
            file = new String("/");
        } else {
            // split the host and filename.
            host = args[0].substring(0, delimiter);
            file = args[0].substring(delimiter);
        }
        try {
            System.out.println("Connect to host: " + host);
            // create a connection to port 80 of the host
            socket = new Socket(host, 80);
            // provide OutputStream of the Socket..
            out = socket.getOutputStream();
            // tinker the command
            String command = new String("GET " + file + " HTTP/1.0\r\n\r\n");
            System.out.println("Write: " + command);
            // write HTTP-GET command.
            out.write(command.getBytes());
            // Make sure that the command actually
            // is sent and not resides in a buffer
            out.flush();
            System.out.println("Reply:\n");
            // provide InputStream of the Socket.
            in = socket.getInputStream();
            // Help variable
            int aByte;
            // Read from the InputStream until it ends.
            // Print every character to the standard
            // output. We are writing byte wise, to make
            // it simpler. In a real application,
            // this is *not* recommended.
            while ((aByte = in.read()) != -1) {
                System.out.write(aByte);
            }
            // Release all resources.
            out.close();
            in.close();
            socket.close();
        }
        // In case an error occurs, print this to the
        // standard output.
        catch (IOException ioe) {
            System.err.println(ioe.toString());
        }
    }
}