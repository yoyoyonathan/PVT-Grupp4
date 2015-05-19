import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import play.mvc.*;
import play.test.*;
import play.data.DynamicForm;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.RequiredValidator;
import play.i18n.Lang;
import play.libs.F;
import play.libs.F.*;
import play.twirl.api.Content;

import static play.test.Helpers.*;
import static play.test.Helpers.GlobalSettings;
import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;


/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest extends WithApplication {
	
	FakeApplication fakeApp = Helpers.fakeApplication();

	FakeApplication fakeAppWithMemoryDb = fakeApplication(inMemoryDatabase("test"));
	
	@Before
	public void beforeEachTest(){
	FakeApplication fakeAppWithGlobal = Helpers.fakeApplication(new Helpers.GlobalSettings() {
		@Override
		public void onStart(Application app) {
			System.out.println("Starting FakeApplication");
			}
		});
	}
	
	@Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertThat(a).isEqualTo(2);
    }
	
}
