package controllers;

import play.libs.Json;
import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }
    
    public static Result helloWeb() {
        ObjectNode result = Json.newObject();
        result.put("content", "Hello Web");
        return ok(result); 
    }

}


