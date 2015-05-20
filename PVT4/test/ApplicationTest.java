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
public class ApplicationTest {
	
	
	@Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertThat(a).isEqualTo(2);
    }
	
	@Test
	public void testCallIndex() {
		Result result = callAction(
		 controllers.routes.ref.Application.index(),
		 new FakeRequest(GET, "/")
		);
		assertThat(status(result)).isEqualTo(OK);
	}
	@Test
	public void testCallProfilePage() {
		Result result = callAction(
		 controllers.routes.ref.Application.profilePage("String"),
		 new FakeRequest(GET, "/")
		);
		assertThat(status(result)).isEqualTo(OK);
	}
}
