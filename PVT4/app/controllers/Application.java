package controllers;

import models.User;
import play.*;
import play.data.Form;
import play.mvc.*;
import views.html.*;

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
    
    public static Result addUser() {
    	User user = Form.form(User.class).bindFromRequest().get();
    	user.save();
    	return redirect(routes.Application.index());
    }
}


