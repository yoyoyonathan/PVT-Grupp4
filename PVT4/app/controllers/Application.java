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
        return ok(index.render(""));
    }
    
    public static Result artister() {
        return ok(artister.render());
    }
    
    public static Result profil() {
		return ok(profil.render());
    }
    
    public static Result loginPage() {
    	String currentUser = session("connected");
        if(currentUser != null) {
             return ok(index.render("You are already logged in as " + currentUser + "!!!!!"));
        } 
    	return ok(loginPage.render());
    }
    
    public static Result login() {
    	
//    	ObjectNode result = Json.newObject();
//		Connection conn = null;
//		Statement stmt = null;
//    	
//    	try{
//    		
//			conn = DB.getConnection();
//			stmt = conn.createStatement();
//		
//			String sql = "SELECT * FROM user";
//			
//			ResultSet rs = stmt.executeQuery(sql);
//		
//		    while(rs.next()){
//				//Retrieve by column name
//
//				String email  = rs.getString("email");
//				String password = rs.getString("password");
//				ObjectNode user = Json.newObject();
//				user.put("Email", email);
//				user.put("Password", password);
//				
//				result.put(email, password);
//		   	}
//		    rs.close();
//
//			return ok(result);
//		}catch(SQLException se){
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
    	
		return redirect(routes.Application.index());
    }
    
    public static Result logout() {
	    session().clear();
    	return ok(index.render("You are now logged out!"));
    }
    
    public static Result signup() {
	    String currentUser = session("connected");
        if(currentUser != null) {
             return ok(index.render("You are already logged in as " + currentUser + "!!!!!"));
        } 
		return ok(signup.render(""));
	}

    
    public static Result addUser() {
			
    	if (Form.form(User.class).bindFromRequest().hasErrors()){
 		    return badRequest(signup.render("???"));
 		}
 	    
 		User user = Form.form(User.class).bindFromRequest().get();
 		ObjectNode result = Json.newObject();
 		Connection conn = null;
 		Statement stmt = null;
 		String userEmail = user.email;
 		String userUserName = user.userName;
 		String userPassword = user.password;
 		int userBirthDate = user.birthDate;
 		
// 		if (userUserName.matches("^.*[^a-zA-Z0-9].*$")){
// 		    return badRequest(signup.render("Please only use letters and numbers for the username"));
// 		}

 		try {
 			conn = DB.getConnection();
 			stmt = conn.createStatement();
 			
 			//PreparedStatement statement = conn.prepareStatement("INSERT INTO user(email,userName,password,birthDate) VALUES(?,?,?,?)");
 			//statement.setString(1, userEmail);
 			//statement.setString(2, userName);
 			//statement.setString(3, userPassword);
 			//statement.setInt(4, userBirthDate);
 			
 			String insertIntoDatabase = "INSERT INTO user" 
 			+ "(email, userName, password, birthDate) " + "VALUES" + "(" + "'" +userEmail + "'" + "," + "'" + userUserName + "'" 
 					+ "," + "'" + userPassword + "'" + "," + "'" + userBirthDate + "'" + ")";
 			
 			// execute insert SQL statement
 			stmt.executeUpdate(insertIntoDatabase);

 			// user.save();
 			session("connected", userEmail);
 			return redirect(routes.Application.index());
 			
 		} catch (SQLException se) {
 			// Handle errors for JDBC
 			return internalServerError(se.toString());
 		} catch (Exception e) {
 			// Handle errors for Class.forName
 			return internalServerError(e.toString());
 		} finally {
 			// finally block used to close resources
 			try {
 				if (stmt != null)
 					conn.close();
 			} catch (SQLException se) {
 			}// do nothing
 			try {
 				if (conn != null)
 					conn.close();
 			} catch (SQLException se) {
 				return internalServerError(se.toString());
 			}// end finally try
 		}// end try
    }
	    	
    public static Result getUsers() {
    	
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


