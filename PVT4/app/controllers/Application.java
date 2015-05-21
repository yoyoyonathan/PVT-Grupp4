package controllers;

import java.sql.*;
import models.*;
import play.data.*;
import play.db.DB;
import play.mvc.*;
import views.html.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

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
		return ok(profilePage.render(TeamDatabase.getTeam(userName), UserDatabase.getUser(userName)));
    }
    
    public static Result logout() {
    	String currentUser = session("connected");
    	if(currentUser == null) {
            return ok(index.render("Du måste logga in först."));
    	}
	    session().clear();
    	return ok(index.render("Du är nu utloggad."));
    }
}
