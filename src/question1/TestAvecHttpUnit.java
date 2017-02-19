package question1;

import com.meterware.httpunit.*;


public class TestAvecHttpUnit extends junit.framework.TestCase
{
    public void test_tp_http_accessible()
    {
        try {
            WebConversation conversation = new WebConversation();
            WebRequest request = new GetMethodWebRequest("http://jfod.cnam.fr/NSY102/tp_nsy102.html");
            WebResponse response = conversation.getResponse(request);
            assertTrue("jfod.cnam.fr inaccessible ?, peu probable...", response.getText().length() > 0);

            WebLink link = response.getLinkWith("tp_http.jar");
            assertNotNull("le lien du tp_http n'est pas actif, patience ...", link);

        } catch (Exception e) {
            fail("exception inattendue ! " + e.getMessage());
        }
    }
}
