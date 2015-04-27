package models;

import java.util.*;

import javax.persistence.*;
import javax.validation.*;
import play.data.validation.*;
import play.data.*;
import play.db.ebean.*;
import play.db.ebean.Model.Finder;

@Entity
public class Team extends Model {
	
	@Id
	@Constraints.Required
	public String name;
//	public ArrayList<User> members;
	
	public Team(String name) {
		this.name = name;
	}
	
	public static Finder<String,Team> find = new Finder<String,Team>(
	        String.class, Team.class
	); 
}
