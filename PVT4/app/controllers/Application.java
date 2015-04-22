package controllers;

import java.util.List;
import java.sql.*;

import models.User;
import play.*;
import play.api.libs.json.*;
import play.data.*;
import play.db.DB;
import play.db.ebean.Model;
import play.mvc.*;
import views.html.*;
import static play.libs.Json.toJson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;

public class Application extends Controller {
	
    public static Result index() {
        return ok(index.render());
    }
    
    public static Result artister() {
        return ok(artister.render());
    }
    
    public static Result profil() {
		return ok(profil.render());
    }
    
    public static Result login() {
    	return ok(login.render());
    }
    
    public static Result signup() {
    	return ok(signup.render());
    }
    
    public static Result addUser() {
//    	User user = Form.form(User.class).bindFromRequest().get();
//    	user.save();
//    	
//    	ObjectNode result = Json.newObject();
//		Connection conn = null;
//		Statement stmt = null;
//		
//		try{
//    		
//			conn = DB.getConnection();
//			stmt = conn.createStatement();
//			
//			String sql = "SELECT * FROM user";
//			
//			ResultSet rs = stmt.executeQuery(sql);
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			
	    	return redirect(routes.Application.login());
//
//			
//		} catch(SQLException se){
//			//Handle errors for JDBC
//	        return internalServerError(se.toString());
//		}catch(Exception e){
//	    	//Handle errors for Class.forName
//	        return internalServerError(e.toString());
//	 	}finally{
//			 //finally block used to close resources
//			 try{
//			    if(stmt!=null)
//			       conn.close();
//			 }catch(SQLException se){
//			 }// do nothing
//			 try{
//			    if(conn!=null)
//			       conn.close();
//			 }catch(SQLException se){
//			    return internalServerError(se.toString());
//			 }//end finally try
//	   	}//end try
    }
    
    public static Result getUsers() {
//    	List<User> users = new Model.Finder(String.class, User.class).all();
//    	return ok(toJson(users));
    	
    	ObjectNode result = Json.newObject();
		Connection conn = null;
		Statement stmt = null;
    	
    	try{
    		
			conn = DB.getConnection();
			stmt = conn.createStatement();
		
		
			String sql = "SELECT * FROM user";
			
			ResultSet rs = stmt.executeQuery(sql);
		
		    while(rs.next()){
				//Retrieve by column name

				String email  = rs.getString("email");
				String password = rs.getString("password");
				String userName = rs.getString("userName");
				int birthDate = rs.getInt("birthDate");
				ObjectNode user = Json.newObject();
				user.put("Email", email);
				user.put("Password", password);
				user.put("userName", userName);
				user.put("birthDate", birthDate);
				
				result.put(email, password);
				result.put(userName, birthDate);
		   	}
		    rs.close();

			return ok(result);
		}catch(SQLException se){
			//Handle errors for JDBC
	        return internalServerError(se.toString());
		}catch(Exception e){
	    	//Handle errors for Class.forName
	        return internalServerError(e.toString());
	 	}finally{
			 //finally block used to close resources
			 try{
			    if(stmt!=null)
			       conn.close();
			 }catch(SQLException se){
			 }// do nothing
			 try{
			    if(conn!=null)
			       conn.close();
			 }catch(SQLException se){
			    return internalServerError(se.toString());
			 }//end finally try
	   	}//end try
    	
    }
}    


