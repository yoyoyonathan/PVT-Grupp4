package models;

import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import play.api.data.validation.*;
import play.data.*;

public class User {
	//Tydligen skyddar Play attributen automatiskt
 // @Constraint.Required
	public String email;
	public String name;
	public String password;
	
	
//	public List<ValidationError> validate() {			
//	    List<ValidationError> errors = new ArrayList<ValidationError>();
//	    if ((email) != null) {
//	        errors.add(new ValidationError(email, "This e-mail is already registered."));
//	    }
//	    return errors.isEmpty() ? null : errors;
//	}

	
	public User(String email, String name, String password){
		this.email = email;
		this.name = name;
		this.password = password;
	}
	
//	Form<User> userForm = Form.form(User.class); vette fan


	
	//Tydligen genererar Play getters och setters automatiskt

}
