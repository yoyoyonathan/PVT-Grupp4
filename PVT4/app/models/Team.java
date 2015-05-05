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
	public int points;
	public ArrayList<User> members;
	public ArrayList<String> wall;
	
	public void addMember(User user) {
		if (members.size() < 5)
			members.add(user);
	}
	
	public static Finder<String,Team> find = new Finder<String,Team>(
	        String.class, Team.class
	); 
}
