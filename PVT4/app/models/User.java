package models;

import java.util.*;

import javax.persistence.*;
import javax.validation.*;

import play.data.validation.*;
import play.data.*;
import play.db.ebean.*;

@Entity
public class User extends Model{
	//Tydligen skyddar Play attributen automatiskt
	
	@Id
	@Constraints.Min(10)
	public String email;
	public String userName;
	@Constraints.Required
	public String password;
	public int birthDate;		//Tänkt att vara 6 siffror så kan man kolla ålder med krokodilmunnar
	
	public User(String email, String userName, String password, int birthDate){
		this.email = email;
		this.userName = userName;
		this.password = password;
		this.birthDate = birthDate;
	}
	
	public static Finder<String,User> find = new Finder<String,User>(
	        String.class, User.class
	); 
	//Tydligen genererar Play getters och setters automatiskt
}
