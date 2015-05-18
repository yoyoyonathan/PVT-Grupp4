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
	// FakeApplication fakeAppWithGlobal = fakeApplication(new GlobalSettings() {
	  // @Override
	  // public void onStart(Application app) {
		// System.out.println("Starting FakeApplication");
	  // }
	// });
	
	FakeApplication fakeAppWithMemoryDb = fakeApplication(inMemoryDatabase("test"));

	@Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertThat(a).isEqualTo(2);
    }
	@Test
	public void testMock(){
		
		// Create and train mock		
		List<String> mockedList = mock(List.class);
		when(mockedList.get(0)).thenReturn("first");

		// check value
		assertEquals("first", mockedList.get(0));

		// verify interaction
		verify(mockedList).get(0);
		
		//nytt test
		controllers.Application test = mock(controllers.Application.class);
		verify(test, never()).getCode("hej");
		
	}

    @Test
    public void renderTemplate() {
        Content html = views.html.index.render("hej"); //Ändrade och tog bort text från parantesen "Your new application is ready."
        assertThat(contentType(html)).isEqualTo("text/html");
        assertThat(contentAsString(html)).contains("");//Ändrade och tog bort text från parantesen "Your new application is ready."
	}
	@Test
	public void indexTest()	{
		Result result = controllers.Application.index();
		  
		assertThat(status(result)).isEqualTo(OK);
		assertThat(contentType(result)).contains("text/html");
		assertThat(charset(result)).isEqualTo("utf-8");
		assertThat(contentAsString(result)).contains("");
	}
	@Test
	public void teamTest()	{
		Result result = callAction(controllers.routes.ref.Application.team(""),
		new FakeRequest(GET, "/"));
		
		assertThat(status(result)).isEqualTo(OK);
	}
	@Test
	public void testCallProfilePage()	{
		Result result = callAction(controllers.routes.ref.Application.profilePage(""),
		new FakeRequest(GET, "/"));
		
		assertThat(status(result)).isEqualTo(OK);
	}
	@Test
	public void testCallLoginPage() {
		Result result = callAction(controllers.routes.ref.Application.loginPage(),
		new FakeRequest(GET, "/"));
		
		assertThat(status(result)).isEqualTo(OK);
		assertThat(contentType(result)).contains("text/html");
	}
	// @Ignore
	// public void testLogin()	{
			// fakeApp = running(fakeApplication(), new Runnable() {
				// public void run(){
					// System.out.println("hej");
				// }
			// });{
			// Result result = callAction(controllers.routes.ref.Application.login(),
			// new FakeRequest(GET, "/"));
		// }
	// }
	@Test
	public void testAddNewMember(){
		
	}
	
}
