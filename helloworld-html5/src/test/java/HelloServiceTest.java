import org.jboss.as.quickstarts.html5rest.HelloService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class HelloServiceTest {

    private HelloService helloService;

    @Before
    public void init() {

        helloService = new HelloService();
    }

    @Test
    public void testCorrect() {

        assertTrue(true);
    }

    @Test
    public void testFailing() {
        //todo implement test
    }

}
