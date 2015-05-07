package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.sql.*;
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
    	String currentUser = session("connected");
    	if(currentUser == null) {
            return ok(index.render("Du måste logga in först."));
    	}
    	return ok(team.render(getTeam(name)));
    }
    
    public static Result searchTeam() {
    	return ok(searchTeam.render(""));
    }
    
    public static Result profilePage(String userName) {
    	String currentUser = session("connected");
    	if(currentUser == null) {
            return ok(index.render("Du måste logga in först."));
    	}
		return ok(profilePage.render(getTeam(userName), getUser(userName)));
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
			 			return redirect(routes.Application.profilePage(userName));
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
    
    public static Result addTeam() {					//Som det är nu, om man är med i ett lag och försöker skapa ett nytt så 
    	if (Form.form(Team.class).bindFromRequest().hasErrors()){ 	//skapas laget fortfarande, men användaren kommer inte med i laget
 		    return badRequest(index.render("Nu har något skrivits in fel"));
 		}
    	
    	Team team = Form.form(Team.class).bindFromRequest().get();
		PreparedStatement preparedStatement;
 		Connection conn = null;
 		String teamName = team.name;
 		String userName = session("connected");
 		
 		try {
 			conn = DB.getConnection();
 			
 			String insertIntoDatabase = "INSERT INTO team (name) VALUES(?)";
 			preparedStatement = conn.prepareStatement(insertIntoDatabase);
			preparedStatement.setString(1, teamName);
			preparedStatement.executeUpdate();
			
			String insertIntoDatabase2 = "INSERT INTO teammember (user, team) VALUES(?,?)";
 			preparedStatement = conn.prepareStatement(insertIntoDatabase2);
			preparedStatement.setString(1, userName);
			preparedStatement.setString(2, teamName);
			preparedStatement.executeUpdate();
 			
//			return ok(profilePage.render(null));
			return redirect("/profile/" + userName);
			
 		} catch (SQLException se) {
 			// Handle errors for JDBC
// 			return internalServerError(se.toString());
 			return badRequest(index.render("Namn är redan taget."));
 		} catch (Exception e) {
 			// Handle errors for Class.forName
 			return internalServerError(e.toString());
 		} finally {
 			try {
 				if (conn != null)
 					conn.close();
 			} catch (SQLException se) {
 				return internalServerError(se.toString());
 			}
 		}
    }
    
    public static Result addTeamMember() {			//Går att gå med i ett Team som inte finns.  
    	if (Form.form(Team.class).bindFromRequest().hasErrors()){
 		    return badRequest(index.render("Fel i formulär"));
 		}
    	
    	PreparedStatement preparedStatement;
 		Connection conn = null;
		Statement stmt = null;
		
    	Team team = Form.form(Team.class).bindFromRequest().get();
 		String teamName = team.name;
 		String userName = session("connected");
 		
    	try {
 			conn = DB.getConnection();
			stmt = conn.createStatement();
		
			String sql = "SELECT * FROM `teammember` WHERE `team` = " + "'" + teamName + "'";
			
			ResultSet rs = stmt.executeQuery(sql);
			ArrayList<String> users = new ArrayList<String>();
			String user = "empty";
			
		    while(rs.next()){
				user = rs.getString("user");
				users.add(user);
		   	}
		    rs.close();
 			
 			if (users.size() < 4 && !user.equals("empty")){
 				
	 			String insertIntoDatabase = "INSERT INTO teammember (user, team) VALUES(?,?)";
	 			preparedStatement = conn.prepareStatement(insertIntoDatabase);
				preparedStatement.setString(1, userName);
				preparedStatement.setString(2, teamName);
				preparedStatement.executeUpdate();
	 			
				return redirect("/profile/" + userName);
 			}
 			
 			return badRequest(index.render("Teamet är redan fullt/extisterar ej."));
	 			
    	} catch (SQLException se) {
 			// Handle errors for JDBC
// 			return internalServerError(se.toString());
 			return badRequest(index.render("Det laget du har valt är fullt/extisterar inte."));
 		} catch (Exception e) {
 			// Handle errors for Class.forName
 			return internalServerError(e.toString());
 		} finally {
 			try {
 				if (conn != null)
 					conn.close();
 			} catch (SQLException se) {
 				return internalServerError(se.toString());
 			}
 		}
    }
    
    public static Result randomizeTeam(){			//Typ klar, blir knas om två nya har samma namn
    	
    	PreparedStatement preparedStatement;
 		Connection conn = null;
		Statement stmt = null;
    	
 		String userName = session("connected");
    	
    	try {
    	
    		conn = DB.getConnection();
			stmt = conn.createStatement();
		
			String sql = "SELECT * FROM team";
			
			ResultSet rs = stmt.executeQuery(sql);
			ArrayList<String> teams = new ArrayList<String>();
			String team;
			
		    while(rs.next()){
				team = rs.getString("name");
				teams.add(team);
		   	}
		    rs.close();							//Nu har vi alla teams namn
 			
		    for(int i = 0; i < teams.size(); i++) {
		    
		    	Random r = new Random();
		    	int low = 0;
		    	int high = teams.size();
		    	int R = r.nextInt(high-low) + low;
		    	String teamName = teams.get(R);
		    	
				String sql2 = "SELECT * FROM `teammember` WHERE `team` = " + "'" + teamName + "'";
				
				ResultSet rs2 = stmt.executeQuery(sql2);
				ArrayList<String> users = new ArrayList<String>();
				String user;
				
			    while(rs2.next()){
					user = rs2.getString("user");
					users.add(user);
			   	}
			    rs2.close();
		    
			    if (users.size() < 4){
	 				
		 			String insertIntoDatabase = "INSERT INTO teammember (user, team) VALUES(?,?)";
		 			preparedStatement = conn.prepareStatement(insertIntoDatabase);
					preparedStatement.setString(1, userName);
					preparedStatement.setString(2, teamName);
					preparedStatement.executeUpdate();
		 			
		 			return redirect(routes.Application.profilePage(userName));
			    }
		    }
		    
		
		String teamName = randomizeTeamName();
		//Skapa nytt team med namnet och lägg till skit
		
		String insertIntoDatabase = "INSERT INTO team (name) VALUES(?)";
			preparedStatement = conn.prepareStatement(insertIntoDatabase);
		preparedStatement.setString(1, teamName);
		preparedStatement.executeUpdate();
		
		String insertIntoDatabase2 = "INSERT INTO teammember (user, team) VALUES(?,?)";
			preparedStatement = conn.prepareStatement(insertIntoDatabase2);
		preparedStatement.setString(1, userName);
		preparedStatement.setString(2, teamName);
		preparedStatement.executeUpdate();
			
		return redirect(routes.Application.profilePage(userName));
		    
    	} catch (SQLException se) {
			// Handle errors for JDBC
//			return internalServerError(se.toString());
			return badRequest(index.render("Du är redan med i ett team."));
    	} catch (Exception e) {
 			// Handle errors for Class.forName
 			return internalServerError(e.toString());
 		} finally {
 			try {
 				if (conn != null)
 					conn.close();
 			} catch (SQLException se) {
 				return internalServerError(se.toString());
 			}
 		}
    }
    
    public static String randomizeTeamName(){
    	//Skapa nytt team som slumpar fram ett namn och gör hen till medlem
    			ArrayList<String> ord1 = new ArrayList<String>();
    			ord1.add("DJs of ");
    			ord1.add("Lucifers ");
    			ord1.add("Mommas ");
    			ord1.add("House ");
    			ord1.add("Party ");
    			ord1.add("Swag ");
    			ord1.add("YOLO ");
    			ord1.add("Summer ");
    			ord1.add("Bursting ");
    			ord1.add("Daddy's ");
    			ord1.add("Jultomtens ");
    			ord1.add("Aviciis ");
    			ord1.add("Axwells ");
    			ord1.add("Guettas ");
    			ord1.add("Cool ");
    			ord1.add("Discots");
    			ord1.add("Festens");

    			ArrayList<String> ord2 = new ArrayList<String>();
    			ord2.add("Doom");
    			ord2.add("Satan");
    			ord2.add("Småjävlar");
    			ord2.add("Famous");
    			ord2.add("Party");
    			ord2.add("Bästa");
    			ord2.add("Coolaste");
    			ord2.add("House-lovers");
    			ord2.add("Mad Team");
    			ord2.add("Badasses");
    			ord2.add("Kick-ass");
    			ord2.add("Rules");
    			ord2.add("Fetaste");
    			ord2.add("Madness");
    			ord2.add("Angels");
    			ord2.add("Dreams");
    			ord2.add("Makers");
    			ord2.add("Disco");
    			
    			Random r1 = new Random();
    	    	int low1 = 0;
    	    	int high1 = 9;
    	    	int R1 = r1.nextInt(high1-low1) + low1;
    	    	
    	    	Random r2 = new Random();
    	    	int low2 = 0;
    	    	int high2 = 9;
    	    	int R2 = r2.nextInt(high2-low2) + low2;
    	    	
    			String teamName = ord1.get(R1) + ord2.get(R2);
    			return teamName;
    }
    
    public static Team getTeam(String userName) {			
    	
		Connection conn = null;
		Statement stmt = null;
		
		Team t = new Team();
    	
    	try{
    		
			conn = DB.getConnection();
			stmt = conn.createStatement();
			
			String sql1 = "SELECT * FROM `teammember` WHERE `user` = " + "'" + userName + "'";
			
			ResultSet rs1 = stmt.executeQuery(sql1);
			rs1.next();
			String name = rs1.getString("team");
			rs1.close();
		
			String sql2 = "SELECT * FROM `team` WHERE `name` = " + "'" + name + "'";
			
			ResultSet rs2 = stmt.executeQuery(sql2);
			rs2.next();
			t.name = rs2.getString("name");
			t.points = rs2.getInt("points");
			rs2.close();
			
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
 			return redirect(routes.Application.searchTeam());

 			
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
}    


