package models;

import java.util.*;

import javax.persistence.*;
import javax.validation.*;

import play.api.data.validation.*;
import play.data.*;
import play.db.ebean.*;

@Entity
public class User extends Model{
	//Tydligen skyddar Play attributen automatiskt
	
	public String email;
	@Id
	public String name;
	public String password;
	public int points;
	
	public User(String email, String name, String password){
		this.email = email;
		this.name = name;
		this.password = password;
	}
	//Tydligen genererar Play getters och setters automatiskt
}
