package controllers;

import java.util.*;

import models.User;
import play.*;
import play.data.Form;
import play.db.ebean.Model;
import play.mvc.*;
import views.html.*;
import static play.libs.Json.toJson;

public class Application extends Controller {
	
    public static Result index() {
        return ok(index.render());
    }
    
    public static Result artister() {
        return ok(artister.render());
    }
    
    public static Result profil() {
		return ok(profil.render());
    }
    
    public static Result login() {
    	return ok(login.render());
    }
    
    public static Result addUser() {
    	User user = Form.form(User.class).bindFromRequest().get();
    	user.save();
    	return redirect(routes.Application.login());
    }
    
    public static Result getUsers() {
    	List<User> users = new Model.Finder(String.class, User.class).all();
    	return ok(toJson(users));
    }
}
