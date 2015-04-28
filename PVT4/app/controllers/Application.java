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
    	String currentUser = session("connected");
    	if(currentUser == null) {
            return ok(index.render("Du måste logga in först."));
    	}
        return ok(artister.render("DJ " + currentUser));
    }
    
    public static Result profilePage() {
    	String currentUser = session("connected");
    	if(currentUser == null) {
            return ok(index.render("Du måste logga in först."));
    	}
		return ok(profilePage.render("You are " + currentUser + "."));
    }
    
    public static Result loginPage() {
    	String currentUser = session("connected");
        if(currentUser != null) {
             return ok(index.render("Du är redan inloggad som " + currentUser + "."));
        } 
    	return ok(loginPage.render(""));
    }
    
    public static Result teamPage() {
    	return ok(teamPage.render(""));
    }
    
    
    public static Result login() {
    	
		Connection conn = null;
		Statement stmt = null;
    	
    	try{
    		
     		User user = Form.form(User.class).bindFromRequest().get();
			conn = DB.getConnection();
			stmt = conn.createStatement();
			
			String userEmail = user.email;
	 		String userPassword = user.password;
		
	 		String sql = "SELECT * FROM `user` WHERE `email` = " + "'" + userEmail + "'";
			
			ResultSet rs = stmt.executeQuery(sql);
			
			if(rs.isBeforeFirst()){
				rs.next();
				
				String email = rs.getString("email");
				String password = rs.getString("password");
				String userName = rs.getString("userName");
					
					if (userEmail.equals(email) && userPassword.equals(password)){
					    rs.close();
					    session("connected", userName);
			 			return redirect(routes.Application.index());
					}
				} 
				
				rs.close();
				return ok(loginPage.render("Fel email/lösenord."));
				
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
    
    public static Result logout() {
    	String currentUser = session("connected");
    	if(currentUser == null) {
            return ok(index.render("Du måste logga in först."));
    	}
	    session().clear();
    	return ok(index.render("Du är nu utloggad."));
    }
    
    public static Result signup() {
	    String currentUser = session("connected");
        if(currentUser != null) {
             return ok(index.render("Du är redan inloggad som " + currentUser + "!"));
        } 
		return ok(signup.render(""));
	}
    
    public static Result addUser() {
			
    	if (Form.form(User.class).bindFromRequest().hasErrors()){
 		    return badRequest(signup.render("Nu har något skrivits in fel"));
 		}
 	    
 		User user = Form.form(User.class).bindFromRequest().get();
 		ObjectNode result = Json.newObject();
 		Connection conn = null;
 		Statement stmt = null;
 		String userEmail = user.email;
 		String userUserName = user.userName;
 		String userPassword = user.password;
 		int userBirthDate = user.birthDate;
 		
 		if (userUserName.matches("^.*[^a-zA-Z0-9].*$")){
 		    return badRequest(signup.render("Använd endast bokstäver och siffror till ditt användarnamn."));
 		}

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
 			session("connected", userUserName);
 			return redirect(routes.Application.index());
 			
 		} catch (SQLException se) {
 			// Handle errors for JDBC
// 			return internalServerError(se.toString());
 			return badRequest(signup.render("Email/användarnamn är redan taget."));
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


