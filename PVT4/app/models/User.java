package models;

import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import play.data.validation.*;
import play.data.*;
import play.db.ebean.*;

@Entity
public class User extends Model{
	
	@Id
	@Constraints.Required
	public String email;
	public String userName;
	@Constraints.Required
	public String password;
	public String birthDate;		
	
//	public User(String email, String userName, String password, int birthDate){
//		this.email = email;
//		this.userName = userName;
//		this.password = password;
//		this.birthDate = birthDate;
//	}
	
	public static Finder<String,User> find = new Finder<String,User>(
	        String.class, User.class
	); 
	//Tydligen genererar Play getters och setters automatiskt
}
