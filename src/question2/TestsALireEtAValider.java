package question2;

import com.meterware.httpunit.*;

import java.lang.reflect.*;
import java.io.*;

public class TestsALireEtAValider extends junit.framework.TestCase
{

    private final static int OBSERVABLE_PORT_TEST = 8222;
    private final static int OBSERVER_PORT_TEST = 9222;

    private final static int TIME_OUT_TEST = 200;
    private final static String SERVER_TEST = "localhost"; // "jfod.cnam.fr";


    public void setUp()
    {
        try {
// 	    Field port = ObservableServer.class.getDeclaredField("HTTP_PORT");
//       port.setAccessible(true);
//       port.set(ObservableServer.class,OBSERVABLE_PORT_TEST);
            Field time_out = ObservableServer.class.getDeclaredField("TIME_OUT");
            time_out.setAccessible(true);
            time_out.set(ObservableServer.class, TIME_OUT_TEST);

//       port = ObserverServer.class.getDeclaredField("HTTP_PORT");
//       port.setAccessible(true);
//       port.set(ObservableServer.class,OBSERVER_PORT_TEST);
            time_out = ObserverServer.class.getDeclaredField("TIME_OUT");
            time_out.setAccessible(true);
            time_out.set(ObserverServer.class, TIME_OUT_TEST);

            PrintWriter pw = new PrintWriter(new BufferedWriter
                    (new FileWriter("observers.txt", false)));
            pw.print("");
            pw.close();
            pw = new PrintWriter(new BufferedWriter
                    (new FileWriter("saveObservers.txt", false)));
            pw.print("");
            pw.close();
            pw = new PrintWriter(new BufferedWriter
                    (new FileWriter("deadObservers.txt", false)));
            pw.print("");
            pw.close();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void test_addObserver()
    {
        try {
            new Thread(new Runnable()
            {
                public void run()
                {
                    try {
                        Class.forName("question2.ObservableServer", true, this.getClass().getClassLoader());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ObservableServer.main(new String[]{"8222"});
                }
            }).start();

            WebConversation conversation = new WebConversation();
            WebRequest request = new GetMethodWebRequest("http://localhost:8222/addObserver/?url=http://localhost:9222/update/");
            WebResponse response = conversation.getResponse(request);
            System.out.println(response.getText());
            assertTrue(" pas de reponse ???", response.getText().length() > 0);
            assertTrue(" la reponse doit ici retournee true ???", response.getText().contains("true"));

            request = new GetMethodWebRequest("http://localhost:8222/addObserver/?url=http://localhost:9333/update/");
            response = conversation.getResponse(request);
            assertTrue(" pas de reponse ???", response.getText().length() > 0);
            assertTrue(" la reponse doit ici retournee true ???", response.getText().contains("true"));

            request = new GetMethodWebRequest("http://localhost:8222/observersList/");
            response = conversation.getResponse(request);
            System.out.println("response.getText(): " + response.getText());
            assertTrue("La liste des observateurs retournee est erronee, il manque l'url d'un observateur ???", response.getText().contains("http://localhost:9222/update/"));
            assertTrue("La liste des observateurs retournee est erronee, il manque l'url d'un observateur ???", response.getText().contains("http://localhost:9333/update/"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception inattendue ? " + e.getMessage());
        } finally {
            ObservableServer.stopServer();
            try {
                Thread.sleep(TIME_OUT_TEST);
            } catch (Exception e) {
            }
        }
    }

    public void test_addObserver_sans_doublon()
    {
        try {
            new Thread(new Runnable()
            {
                public void run()
                {
                    try {
                        Class.forName("question2.ObservableServer", true, this.getClass().getClassLoader());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ObservableServer.main(new String[]{"8223"});
                }
            }).start();

            WebConversation conversation = new WebConversation();
            WebRequest request = new GetMethodWebRequest("http://localhost:8223/addObserver/?url=http://localhost:9222/update/");
            WebResponse response = conversation.getResponse(request);
            assertTrue(" pas de reponse ???", response.getText().length() > 0);
            assertTrue(" la reponse doit ici retourner true ???", response.getText().contains("true"));

            request = new GetMethodWebRequest("http://localhost:8223/addObserver/?url=http://localhost:9222/update/");
            response = conversation.getResponse(request);
            assertTrue(" pas de reponse ???", response.getText().length() > 0);
            assertTrue(" la reponse doit ici retourner false, (url doit etre unique) ???", response.getText().contains("false"));

            request = new GetMethodWebRequest("http://localhost:8223/observersList/");
            response = conversation.getResponse(request);
            System.out.println("response.getText(): " + response.getText());
            assertTrue("La liste des observateurs retournee est erronee, il manque l'url d'un observateur ???", response.getText().contains("http://localhost:9222/update/"));
        } catch (Exception e) {
            fail("Exception inattendue ? " + e.getMessage());
        } finally {
            ObservableServer.stopServer();
            try {
                Thread.sleep(TIME_OUT_TEST);
            } catch (Exception e) {
            }
        }
    }

    public void test_notifyObservers()
    {
        try {
            new Thread(new Runnable()
            {
                public void run()
                {
                    ObservableServer.main(new String[]{"8224"});
                }
            }).start();

            new Thread(new Runnable()
            {
                public void run()
                {
                    ObserverServer.main(new String[]{"9224"});
                }
            }).start();


            WebConversation conversation = new WebConversation();
            WebRequest request = new GetMethodWebRequest("http://localhost:8224/addObserver/?url=http://localhost:9224/update/");
            WebResponse response = conversation.getResponse(request);
            assertTrue(" pas de reponse ???", response.getText().length() > 0);
            assertTrue(" la reponse doit doit ici retournee true ???", response.getText().contains("true"));

            request = new GetMethodWebRequest("http://localhost:8224/notifyObservers/?temperature=300&capteur=DS1921");
            response = conversation.getResponse(request);
            System.out.println("response.getText():_____ " + response.getText());
            assertTrue(" pas de reponse ???", response.getText().length() > 0);
            assertTrue(" la reponse doit doit ici retournee true ???", response.getText().contains("true"));

            request = new GetMethodWebRequest("http://localhost:9224/lastUpdate/");
            response = conversation.getResponse(request);
            System.out.println("response.getText():::: " + response.getText());

            assertTrue(" pas de reponse ???", response.getText().length() > 0);
            assertTrue(" la reponse de l'observateur doit contenir (au moins) les parametres envoyes par l'observable ???", response.getText().contains("temperature=300"));
            assertTrue(" la reponse de l'observateur doit contenir (au moins) les parametres envoyes par l'observable ???", response.getText().contains("capteur=DS1921"));

        } catch (Exception e) {
            fail("Exception inattendue ? " + e.getMessage());
        } finally {
            ObservableServer.stopServer();
            ObserverServer.stopServer();
            try {
                Thread.sleep(TIME_OUT_TEST);
            } catch (Exception e) {
            }
            try {
                Thread.sleep(TIME_OUT_TEST);
            } catch (Exception e) {
            }
        }
    }

    public void test_notifyObservers_with_failure()
    {
        try {
            new Thread(new Runnable()
            {
                public void run()
                {
                    ObservableServer.main(new String[]{"8225"});
                }
            }).start();

            new Thread(new Runnable()
            {
                public void run()
                {
                    ObserverServer.main(new String[]{"9225"});
                }
            }).start();

            WebConversation conversation = new WebConversation();
            WebRequest request = new GetMethodWebRequest("http://localhost:8225/addObserver/?url=http://localhost:9225/update/");
            WebResponse response = conversation.getResponse(request);
            assertTrue(" pas de reponse ???", response.getText().length() > 0);
            assertTrue(" la reponse doit doit ici retournee true ???", response.getText().contains("true"));

            request = new GetMethodWebRequest("http://localhost:8225/notifyObservers/?temperature=300&capteur=DS1921");
            response = conversation.getResponse(request);
            System.out.println("response.getText(): " + response.getText());
            assertTrue(" pas de reponse ???", response.getText().length() > 0);
            assertTrue(" la reponse doit doit ici retournee true ???", response.getText().contains("true"));

            request = new GetMethodWebRequest("http://localhost:9225/lastUpdate/");
            response = conversation.getResponse(request);
            System.out.println("response.getText():----- " + response.getText());

            assertTrue(" pas de reponse ???", response.getText().length() > 0);
            assertTrue(" la reponse de l'observateur doit contenir (au moins) les parametres envoyes par l'observable ???", response.getText().contains("temperature=300"));
            assertTrue(" la reponse de l'observateur doit contenir (au moins) les parametres envoyes par l'observable ???", response.getText().contains("capteur=DS1921"));

            ObserverServer.stopServer();
            try {
                Thread.sleep(TIME_OUT_TEST);
            } catch (Exception e) {
            }

            request = new GetMethodWebRequest("http://localhost:8225/notifyObservers/?temperature=300&capteur=DS1921");
            response = conversation.getResponse(request);
            System.out.println("response.getText(): ((((((" + response.getText());
            assertTrue(" pas de reponse ???", response.getText().length() > 0);
            //assertTrue(" la reponse doit doit (au moins) retournee false (aucun observateur notifie) ???", response.getText().contains("false"));

            //request = new GetMethodWebRequest("http://localhost:8225/deadObserversList/");
            //response = conversation.getResponse(request);
            //System.out.println("response.getText(): " + response.getText());
            //assertTrue(" pas de reponse ???", response.getText().length() > 0);
            //assertTrue(" la reponse doit contenir l'observateur \"en panne \" ???", response.getText().contains("http://localhost:9225/update/"));
        } catch (Exception e) {
            fail("Exception inattendue ? " + e.getMessage());
        } finally {
            ObservableServer.stopServer();
            try {
                Thread.sleep(TIME_OUT_TEST);
            } catch (Exception e) {
            }

        }
    }

    public void test_save_restore()
    {
        try {
            new Thread(new Runnable()
            {
                public void run()
                {
                    try {
                        Class.forName("question2.ObservableServer", true, this.getClass().getClassLoader());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ObservableServer.main(new String[]{"8226"});
                }
            }).start();

            WebConversation conversation = new WebConversation();
            WebRequest request = new GetMethodWebRequest("http://localhost:8226/addObserver/?url=http://localhost:9226/update/");
            WebResponse response = conversation.getResponse(request);
            assertTrue(" pas de reponse ???", response.getText().length() > 0);
            assertTrue(" la reponse lors d'un addObserver doit ici retourner true ???", response.getText().contains("true"));

            request = new GetMethodWebRequest("http://localhost:8226/addObserver/?url=http://localhost:9227/update/");
            response = conversation.getResponse(request);
            assertTrue(" pas de reponse ???", response.getText().length() > 0);
            assertTrue(" la reponse lors d'un addObserver doit ici retourner true ???", response.getText().contains("true"));

            request = new GetMethodWebRequest("http://localhost:8226/observersList/");
            response = conversation.getResponse(request);
            System.out.println("response.getText(): " + response.getText());
            assertTrue("La liste des observateurs retournee est erronee, il manque l'url d'un observateur ???", response.getText().contains("http://localhost:9226/update/"));
            assertTrue("La liste des observateurs retournee est erronee, il manque l'url d'un observateur ???", response.getText().contains("http://localhost:9227/update/"));

            request = new GetMethodWebRequest("http://localhost:8226/saveObserversList/");
            response = conversation.getResponse(request);
            System.out.println("response.getText(): " + response.getText());
            assertTrue(" la reponse pour saveObserversList doit ici retourner true ???", response.getText().contains("true"));

            request = new GetMethodWebRequest("http://localhost:8226/notifyObservers/?temperature=300&capteur=DS1921");
            response = conversation.getResponse(request);
            System.out.println("response.getText(): " + response.getText());
            request = new GetMethodWebRequest("http://localhost:8226/observersList/");
            response = conversation.getResponse(request);
            System.out.println("response.getText(): " + response.getText());
            assertTrue("La liste des observateurs retournee est erronee, elle doit etre vide pour ce test ???", response.getText().contains("[]"));

            request = new GetMethodWebRequest("http://localhost:8226/restoreObserversList/");
            response = conversation.getResponse(request);
            System.out.println("response.getText(): " + response.getText());
            assertTrue(" la reponse pour restoreObserversList doit ici retourner true ???", response.getText().contains("true"));

            request = new GetMethodWebRequest("http://localhost:8226/observersList/");
            response = conversation.getResponse(request);
            System.out.println("response.getText(): " + response.getText());
            assertTrue("La liste des observateurs apres un restore est erronee, il manque l'url d'un observateur ???", response.getText().contains("http://localhost:9226/update/"));
            assertTrue("La liste des observateurs apres un restore est erronee, il manque l'url d'un observateur ???", response.getText().contains("http://localhost:9227/update/"));

            new Thread(new Runnable()
            {
                public void run()
                {
                    ObserverServer.main(new String[]{"9226"});
                }
            }).start();

            request = new GetMethodWebRequest("http://localhost:8226/notifyObservers/?temperature=300&capteur=DS1921");
            response = conversation.getResponse(request);
            System.out.println("response.getText():_____ " + response.getText());
            assertTrue(" pas de reponse ???", response.getText().length() > 0);
            assertTrue(" la reponse doit doit ici retourner true ???", response.getText().contains("true"));

            request = new GetMethodWebRequest("http://localhost:8226/observersList/");
            response = conversation.getResponse(request);
            System.out.println("response.getText(): " + response.getText());
            //assertTrue("La liste des observateurs retournee est erronee, il manque l'url d'un observateur ???", response.getText().contains("http://localhost:9226/update/"));

            request = new GetMethodWebRequest("http://localhost:8226/deadObserversList/");
            response = conversation.getResponse(request);
            System.out.println("response.getText(): " + response.getText());
            // assertTrue("La liste des observateurs inaccessibles retournee est erronee, il manque l'url d'un observateur ???", response.getText().contains("http://localhost:9227/update/"));

            request = new GetMethodWebRequest("http://localhost:8226/saveObserversList/");
            response = conversation.getResponse(request);
            System.out.println("response.getText(): " + response.getText());
            assertTrue(" la reponse pour saveObserversList doit ici retournee true ???", response.getText().contains("true"));

            ObserverServer.stopServer();
            try {
                Thread.sleep(TIME_OUT_TEST);
            } catch (Exception e) {
            }

            request = new GetMethodWebRequest("http://localhost:8226/notifyObservers/?temperature=300&capteur=DS1921");
            response = conversation.getResponse(request);
            System.out.println("response.getText():_____ " + response.getText());
            assertTrue(" pas de reponse ???", response.getText().length() > 0);
            //assertTrue(" la reponse pour notifyObservers doit ici retournee false ???", response.getText().contains("false"));

            request = new GetMethodWebRequest("http://localhost:8226/observersList/");
            response = conversation.getResponse(request);
            //assertTrue("La liste des observateurs retournee est erronee, elle doit etre vide pour ce test ???", response.getText().contains("[]"));

            request = new GetMethodWebRequest("http://localhost:8226/deadObserversList/");
            response = conversation.getResponse(request);
            //assertTrue("La liste des observateurs inaccessibles retournee est erronee, elle doit etre vide pour ce test ???", response.getText().contains("http://localhost:9226/update/"));

            request = new GetMethodWebRequest("http://localhost:8226/restoreObserversList/");
            response = conversation.getResponse(request);
            //System.out.println("response.getText(): " + response.getText());
            assertTrue(" la reponse pour restoreObserversList doit ici retournee true ???", response.getText().contains("true"));

            request = new GetMethodWebRequest("http://localhost:8226/deadObserversList/");
            response = conversation.getResponse(request);
            assertFalse("La liste des observateurs inaccessibles retournee apres restore est erronee ???", response.getText().contains("http://localhost:9226/update/"));

            request = new GetMethodWebRequest("http://localhost:8226/observersList/");
            response = conversation.getResponse(request);
            assertTrue("La liste des observateurs retournee est erronee, elle doit etre vide pour ce test ???", response.getText().contains("http://localhost:9226/update/"));
        } catch (Exception e) {
            fail("Exception inattendue ? " + e.getMessage());
        } finally {
            ObservableServer.stopServer();

            try {
                Thread.sleep(TIME_OUT_TEST);
            } catch (Exception e) {
            }
        }
    }
}