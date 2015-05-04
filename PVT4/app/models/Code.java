package models;

import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import play.data.validation.*;
import play.data.*;
import play.db.ebean.*;
import play.db.ebean.Model.Finder;

@Entity
public class Code extends Model{
	
	@Id
	public String codeID; 
	public int value = 100;
	public int amount = 500;
	
	public static Finder<String,Code> find = new Finder<String,Code>(String.class, Code.class); 
}
