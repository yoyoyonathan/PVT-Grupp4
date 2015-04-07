package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Testtesttest2222."));
    }
    
    public static Result artister() {
        return ok(artister.render("Artister.scala.html"));
    }
}


