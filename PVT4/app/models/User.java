package models;

import java.util.*;

import javax.persistence.*;
import javax.validation.*;

import play.api.data.validation.*;
import play.data.*;
import play.db.ebean.Model;

@Entity
public class User extends Model{
	//Tydligen skyddar Play attributen automatiskt
 // @Constraint.Required
	
	@Id
	public String email;
	public String name;
	public String password;
	
	
	
	public User(String email, String name, String password){
		this.email = email;
		this.name = name;
		this.password = password;
	}
	
//	Form<User> userForm = Form.form(User.class); vette fan


	
	//Tydligen genererar Play getters och setters automatiskt

}
