package controllers;

import java.util.*;
import java.io.*;
import java.sql.*;

import models.*;
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

public class UserDatabase extends Controller {
	
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
 		String userBirthDate = user.birthDate;
 		
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
			preparedStatement.setString(4, userBirthDate);
			preparedStatement.executeUpdate();
 			// execute insert SQL statement

 			// user.save();
 			session("connected", userUserName);
 			return redirect(routes.Application.joinTeam());

 			
 		} catch (SQLException se) {
 			// Handle errors for JDBC
// 			return internalServerError(se.toString());
 			return badRequest(index.render("Email/användarnamn är redan taget."));
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
	
	public static User getUser(String userName) {			
    	
		Connection conn = null;
		Statement stmt = null;
		
		User u = new User();
    	
    	try{
    		
			conn = DB.getConnection();
			stmt = conn.createStatement();
		
			String sql = "SELECT * FROM `user` WHERE `username` = " + "'" + userName + "'";
			
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			u.email = rs.getString("email");
			u.userName = rs.getString("username");
			u.password = rs.getString("password");
			u.birthDate = rs.getString("birthdate");
			rs.close();
			
			return u;
			
			}catch(SQLException se){
				//Handle errors for JDBC
		        return null;
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
				 }//end finally try
		   	}//end try
	    	
	    }

}
