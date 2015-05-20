package controllers;

import java.sql.*;
import models.*;
import play.data.*;
import play.db.DB;
import play.mvc.*;
import views.html.*;
import com.fasterxml.jackson.databind.node.ObjectNode;


import play.libs.Json;

public class Application extends Controller {
	
    public static Result index() {
        return ok(index.render(""));
    }
    
    public static Result team(String name) {
    	String currentUser = session("connected");
    	if(currentUser == null) {
            return ok(index.render("Du måste logga in först."));
    	}
    	return ok(team.render(TeamDatabase.getTeam(name)));
    }
    
    public static Result joinTeam() {
    	return ok(joinTeam.render(""));
    }
    
    public static Result profilePage(String userName) {
    	String currentUser = session("connected");
    	if(currentUser == null) {
            return ok(index.render("Du måste logga in först."));
    	}
		return ok(profilePage.render(TeamDatabase.getTeam(userName), UserDatabase.getUser(userName)));
    }
    
    public static Result loginPage() {
    	String currentUser = session("connected");
        if(currentUser != null) {
             return ok(index.render("Du är redan inloggad som " + currentUser + "."));
        } 
    	return ok(loginPage.render(""));
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
			 			return redirect(routes.Application.profilePage(userName));
					}
				} 
				
				rs.close();
//				return redirect("/profile/" + userName);
				return ok(index.render("Fel email/lösenord."));
				
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
	    	
    public static Result getUsers() {			//Används inte i nuläget för något
    	
    	ObjectNode result = Json.newObject();
		Connection conn = null;
		Statement stmt = null;
    	
    	try{
    		
			conn = DB.getConnection();
			stmt = conn.createStatement();
			
			String sql = "SELECT * FROM user";
			
			ResultSet rs = stmt.executeQuery(sql);
		
		    while(rs.next()){

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


