package controllers;

import java.util.List;
import java.sql.*;

import models.Code;
import models.Team;
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
    
    public static Result team(String name) {
        return ok(team.render(getTeam(name)));
    }
    
    public static Result profilePage(String email) {
    	String currentUser = session("connected");
    	if(currentUser == null) {
            return ok(index.render("Du måste logga in först."));
    	}
		return ok(profilePage.render(getUser(email)));
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
				int birthDate = rs.getInt("birthDate");
					
					if (userEmail.equals(email) && userPassword.equals(password)){
					    rs.close();
					    session("connected", userName);
			 			return redirect(routes.Application.index());
					}
				} 
				
				rs.close();
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
    
    public static Result signup() {
	    String currentUser = session("connected");
        if(currentUser != null) {
             return ok(index.render("Du är redan inloggad som " + currentUser + "!"));
        } 
		return ok(signup.render(""));
	}
    
    public static Result addTeam() {
    	if (Form.form(Team.class).bindFromRequest().hasErrors()){
 		    return badRequest(index.render("Nu har något skrivits in fel"));
 		}
    	
    	Team team = Form.form(Team.class).bindFromRequest().get();
		PreparedStatement preparedStatement;
 		Connection conn = null;
 		String teamName = team.name;
 		
 		try {
 			conn = DB.getConnection();
 			
 			String insertIntoDatabase = "INSERT INTO team (name) VALUES(?)";
 			preparedStatement = conn.prepareStatement(insertIntoDatabase);
			preparedStatement.setString(1, teamName);
			preparedStatement.executeUpdate();
 			
 			// user.save();
 			return redirect(routes.Application.index());
 			
 		} catch (SQLException se) {
 			// Handle errors for JDBC
// 			return internalServerError(se.toString());
 			return badRequest(index.render("Namn är redan taget."));
 		} catch (Exception e) {
 			// Handle errors for Class.forName
 			return internalServerError(e.toString());
 		} finally {
 			// finally block used to close resources
// 			try {
// 				if (stmt != null)
// 					conn.close();
// 			} catch (SQLException se) {
// 			}// do nothing
 			try {
 				if (conn != null)
 					conn.close();
 			} catch (SQLException se) {
 				return internalServerError(se.toString());
 			}// end finally try
 		}// end try
    }
    
    public static Team getTeam(String name) {			
    	
		Connection conn = null;
		Statement stmt = null;
		
		Team t = new Team();
    	
    	try{
    		
			conn = DB.getConnection();
			stmt = conn.createStatement();
		
			String sql = "SELECT * FROM `team` WHERE `name` = " + "'" + name + "'";
			
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			t.name = rs.getString("name");
			t.points = rs.getInt("points");
			rs.close();
			
			return t;
			
			}catch(SQLException se){
				//Handle errors for JDBC
		        return null;
			}
//    	catch(Exception e){
//		    	//Handle errors for Class.forName
//		        return internalServerError(e.toString());
//		 	}finally{
//				 //finally block used to close resources
//				 try{
//				    if(stmt!=null)
//				       conn.close();}
//				 catch(SQLException se){
//				 }// do nothing
//				 try{
//				    if(conn!=null)
//				       conn.close();
//				 }catch(SQLException se){
//				    return internalServerError(se.toString());
//				 }//end finally try
//		   	}//end try
	    	
	    }
    
    public static Result addPoints() {			//Kommer inte fungera såhör i praktiken, får anpassa efter frontend senare
    	Team team = Form.form(Team.class).bindFromRequest().get();
 		Connection conn = null;
 		Statement stmt = null;
		PreparedStatement preparedStatement = null;
 		
 		String teamName = team.name;
 		int teamPoints = team.points;
 		
 		try {
 			conn = DB.getConnection();
 			stmt = conn.createStatement();
 			
			String sql = "SELECT * FROM `team` WHERE `name` = " + "'" + teamName + "'";

			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			String name = rs.getString("name");
			int points = rs.getInt("points");
			teamPoints += points;
			rs.close();
			
			if (name == null || name.isEmpty() || !name.equals(teamName)) {
				throw new SQLException();
			}
			
			String insertIntoDatabase = "UPDATE team SET points=? WHERE name=?";
				    
			preparedStatement = conn.prepareStatement(insertIntoDatabase);
			preparedStatement.setInt(1, teamPoints);
			preparedStatement.setString(2, teamName);
			preparedStatement.executeUpdate();
				    
		 	return redirect(routes.Application.index());
			
 		} catch (SQLException se) {
 			// Handle errors for JDBC
 			return internalServerError(se.toString());
// 			return badRequest(index.render("Namn är redan taget."));
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
    
    public static Result addUser() {
			
    	if (Form.form(User.class).bindFromRequest().hasErrors()){
 		    return badRequest(signup.render("Nu har något skrivits in fel"));
 		}
 	    
 		User user = Form.form(User.class).bindFromRequest().get();
 		Connection conn = null;
		PreparedStatement preparedStatement;
 		String userEmail = user.email;
 		String userUserName = user.userName;
 		String userPassword = user.password;
 		int userBirthDate = user.birthDate;
 		
// 		if (userUserName.matches("^.*[^a-zA-Z0-9].*$")){		//Vi har ju inte riktigt bestämt oss hur vi ska göra med felhanteringen ännu
// 		    return badRequest(signup.render("Använd endast bokstäver och siffror till ditt användarnamn."));
// 		}

 		try {
 			conn = DB.getConnection();
 			
 			String insertIntoDatabase = "INSERT INTO user (email, username, password, birthdate) VALUES(?,?,?,?)";
 			preparedStatement = conn.prepareStatement(insertIntoDatabase);
			preparedStatement.setString(1, userEmail);
			preparedStatement.setString(2, userUserName);
			preparedStatement.setString(3, userPassword);
			preparedStatement.setInt(4, userBirthDate);
			preparedStatement.executeUpdate();
 			// execute insert SQL statement

 			// user.save();
 			session("connected", userUserName);
 			return redirect(routes.Application.index());
 			
 		} catch (SQLException se) {
// 			 Handle errors for JDBC
 			return internalServerError(se.toString());
// 			return badRequest(signup.render("Email/användarnamn är redan taget."));
 		} catch (Exception e) {
 			// Handle errors for Class.forName
 			return internalServerError(e.toString());
 		} finally {
 			// finally block used to close resources
// 			try {
// 				if (stmt != null)
// 					conn.close();
// 			} catch (SQLException se) {
 			// do nothing
 			try {
 				if (conn != null)
 					conn.close();
 			} catch (SQLException se) {
 				return internalServerError(se.toString());
 			}// end finally try
 		}// end try
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
    
	public static User getUser(String email) {			
	    	
		Connection conn = null;
		Statement stmt = null;
		
		User u = new User();
    	
    	try{
    		
			conn = DB.getConnection();
			stmt = conn.createStatement();
		
			String sql = "SELECT * FROM `user` WHERE `email` = " + "'" + email + "'";
			
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			u.email = rs.getString("email");
			u.userName = rs.getString("username");
			u.password = rs.getString("password");
			u.birthDate = rs.getInt("birthdate");
			rs.close();
			
			return u;
			
			}catch(SQLException se){
				//Handle errors for JDBC
		        return null;
			}
//    	catch(Exception e){
//		    	//Handle errors for Class.forName
//		        return internalServerError(e.toString());
//		 	}finally{
//				 //finally block used to close resources
//				 try{
//				    if(stmt!=null)
//				       conn.close();}
//				 catch(SQLException se){
//				 }// do nothing
//				 try{
//				    if(conn!=null)
//				       conn.close();
//				 }catch(SQLException se){
//				    return internalServerError(se.toString());
//				 }//end finally try
//		   	}//end try
	    	
	    }
	public static Result registerCode(){
		Connection conn = null;
		Statement stmt = null;
		conn = DB.getConnection();
		DynamicForm formData = Form.form().bindFromRequest();
		String teamName = formData.get("team");
		String codeID = formData.get("codeID");
		Code codeFromDB = new Code();
		try {
			stmt = conn.createStatement();
			String sql = "SELECT * FROM `Code` WHERE `codeID` = " + "'" + codeID + "'";
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			codeFromDB.value = rs.getInt("value");
			codeFromDB.amount = rs.getInt("amount");
			codeFromDB.codeID = rs.getString("codeID");
			rs.close();
		//	if(0 < codeFromDB.amount ){
				
		//	}else{
		//		return ok(index.render("Inga användningar kvar"));
		//	}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}return ok(index.render("codefromDB value;" + codeFromDB.value));		
	}	
}    


