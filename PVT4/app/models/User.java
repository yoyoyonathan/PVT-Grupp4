package models;

import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import play.api.data.validation.*;
import play.data.*;

public class User {
	//Tydligen skyddar Play attributen automatiskt
	
	@Id
	public String email;
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
