package models;

import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import play.api.data.validation.*;
import play.data.*;

public class User {
	//Tydligen skyddar Play attributen automatiskt
<<<<<<< Updated upstream
 // @Constraint.Required
=======
	
	@Id
>>>>>>> Stashed changes
	public String email;
	public String name;
	public String password;
	
<<<<<<< Updated upstream
	
//	public List<ValidationError> validate() {			
//	    List<ValidationError> errors = new ArrayList<ValidationError>();
//	    if ((email) != null) {
//	        errors.add(new ValidationError(email, "This e-mail is already registered."));
//	    }
//	    return errors.isEmpty() ? null : errors;
//	}

	
=======
>>>>>>> Stashed changes
	public User(String email, String name, String password){
		this.email = email;
		this.name = name;
		this.password = password;
	}
	//Tydligen genererar Play getters och setters automatiskt
}
