package models;

import java.util.*;

import javax.persistence.*;
import javax.validation.*;
import play.data.validation.*;
import play.data.*;
import play.db.ebean.*;
import play.db.ebean.Model.Finder;

@Entity
public class Team extends Model implements Comparable<Team> {
	
	@Id
	@Constraints.Required
	public String name;
	public int points;
	public String user;
	public ArrayList<String> wall;
	
	public static Finder<String,Team> find = new Finder<String,Team>(
	        String.class, Team.class
	); 
	
	@Override
	public int compareTo(Team team2) {
		int returnInt = team2.points - points;
		if(returnInt != 0 ){
			return returnInt;
		}else{
			return team2.name.compareTo(name);
		}
	}
}
