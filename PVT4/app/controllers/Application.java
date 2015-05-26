package controllers;

import play.mvc.*;
import views.html.*;

public class Application extends Controller {
	
    public static Result index() {
    	session().clear();
        return ok(index.render(""));
    }
    
    public static Result joinTeam() {
    	return ok(joinTeam.render(""));
    }
    
    public static Result profilePage(String userName) {
    	String currentUser = session("connected");
    	if(currentUser == null) {
            return ok(index.render("Du måste logga in först."));
    	}
		return ok(profilePage.render(TeamDatabase.getTeam(userName), UserDatabase.getUser(userName), "Hejhej!"));
    }
    
    public static Result logout() {
	    session().clear();
    	return ok(index.render("Du är nu utloggad."));
    }
}