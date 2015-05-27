package controllers;

import java.util.*;
import java.sql.*;

import models.*;
import play.api.mvc.Session;
import play.data.*;
import play.db.DB;
import play.mvc.*;
import views.html.*;

public class TeamDatabase extends Controller {
	
	public static Result addTeam() {					//Som det är nu, om man är med i ett lag och försöker skapa ett nytt så 
    	if (Form.form(Team.class).bindFromRequest().hasErrors()){ 	//skapas laget fortfarande, men användaren kommer inte med i laget
 		    return badRequest(joinTeam.render("Nu har något skrivits in fel"));
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
//			return redirect("/profile/" + userName);
 			return redirect(routes.Application.profilePage(userName));

			
 		} catch (SQLException se) {
 			// Handle errors for JDBC
// 			return internalServerError(se.toString());
 			return badRequest(joinTeam.render("Namn är redan taget."));
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
 			
 			return badRequest(joinTeam.render("Teamet är redan fullt/extisterar ej."));
	 			
    	} catch (SQLException se) {
 			// Handle errors for JDBC
// 			return internalServerError(se.toString());
 			return badRequest(joinTeam.render("Det laget du har valt är fullt/extisterar inte."));
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
			return badRequest(joinTeam.render("Du är redan med i ett team."));
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
    			ord1.add("Flaskhals ");
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
    			ord1.add("Discots ");
    			ord1.add("Festens ");
    			ord1.add("Livets ");

    			ArrayList<String> ord2 = new ArrayList<String>();
    			ord2.add("Von Anka");
    			ord2.add("Satan");
    			ord2.add("Småjävlar");
    			ord2.add("Klubbor");
    			ord2.add("Party");
    			ord2.add("Laddare");
    			ord2.add("Energy");
    			ord2.add("Style");
    			ord2.add("Heroes");
    			ord2.add("People");
    			ord2.add("Voices");
    			ord2.add("Shouts");
    			ord2.add("Rockers");
    			ord2.add("Knäckers");

    			
    			Random r1 = new Random();
    	    	int low1 = 0;
    	    	int high1 = ord1.size()-1;
    	    	int R1 = r1.nextInt(high1-low1) + low1;
    	    	
    	    	Random r2 = new Random();
    	    	int low2 = 0;
    	    	int high2 = ord2.size()-1;
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
    
    public static int teamRank(){
    	
    	Connection conn = null;
		Statement stmt = null;
		String currentUser = session("connected");
		Team t = getTeam(currentUser);
		String team = t.name;
		
    	try{
    		
			conn = DB.getConnection();
			stmt = conn.createStatement();
			String sql = "SELECT * FROM team ORDER BY points DESC";
			
			ResultSet rs = stmt.executeQuery(sql);
			
			ArrayList<String> teams = new ArrayList<String>();
			int position = 0;
			
			while(rs.next()){
				String name = rs.getString("name");
				teams.add(name);
			}
			rs.close();
			
			for(int i = 0; i < teams.size(); i++){
				
				if(teams.get(i).equals(team)){
					position = i+1;
					
				}
					
				
			}
			
			return position;
			
    	} catch (SQLException se) {
 			// Handle errors for JDBC
// 			return internalServerError(se.toString());
// 			return badRequest(index.render("Email/användarnamn är redan taget."));
    		return 0;
 		} catch (Exception e) {
 			// Handle errors for Class.forName
// 			return internalServerError(e.toString());
 			return 0;
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
// 				return internalServerError(se.toString());
 				return 0;
 			}// end finally try
 		}// end try
    }
    
    public static String topTeamName(int i) {
    	
    	Connection conn = null;
		Statement stmt = null;
		
    	try{
    		
			conn = DB.getConnection();
			stmt = conn.createStatement();
			String returnString = "";
			String sql = "SELECT * FROM team";
			
			ResultSet rs = stmt.executeQuery(sql);
			
			TreeSet<Team> tree = new TreeSet<Team>();
			
			while(rs.next()){
			String name = rs.getString("name");
			int points = rs.getInt("points");
			
			Team team = new Team();
			team.name = name;
			team.points = points;
			tree.add(team);
			}
			rs.close();
			
			//If detta lag är på plats i sorterat efter poäng
			
//			String n = tree.values().toArray()[tree.size()-i] + ": " + tree.keySet().toArray()[tree.size()-i];
			for( int i2 = 0; i2 <= i-1;i2++){
				returnString = "" + tree.pollFirst().name + " ";
			}
			return returnString;
			
			
    	} catch (SQLException se) {
 			// Handle errors for JDBC
// 			return internalServerError(se.toString());
// 			return badRequest(index.render("Email/användarnamn är redan taget."));
    		return null;
 		} catch (Exception e) {
 			// Handle errors for Class.forName
// 			return internalServerError(e.toString());
 			return null;
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
// 				return internalServerError(se.toString());
 				return null;
 			}// end finally try
 		}// end try
    }
    
    public static String topTeamPoints(int i) {
    	
    	Connection conn = null;
		Statement stmt = null;
		
    	try{
    		
			conn = DB.getConnection();
			stmt = conn.createStatement();
			String returnString = "";
			String sql = "SELECT * FROM team";
			
			ResultSet rs = stmt.executeQuery(sql);
			
			TreeSet<Team> tree = new TreeSet<Team>();
			
			while(rs.next()){
			String name = rs.getString("name");
			int points = rs.getInt("points");
			Team team = new Team();
			team.name = name;
			team.points = points;
			tree.add(team);
			}
			rs.close();
			
			//If detta lag är på plats i sorterat efter poäng
			
//			String n = tree.values().toArray()[tree.size()-i] + ": " + tree.keySet().toArray()[tree.size()-i];
			for( int i2 = 0; i2 <= i-1;i2++){
				returnString = "" + tree.pollFirst().points;
			}
			return returnString;
			
			
    	} catch (SQLException se) {
 			// Handle errors for JDBC
// 			return internalServerError(se.toString());
// 			return badRequest(index.render("Email/användarnamn är redan taget."));
    		return null;
 		} catch (Exception e) {
 			// Handle errors for Class.forName
// 			return internalServerError(e.toString());
 			return null;
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
// 				return internalServerError(se.toString());
 				return null;
 			}// end finally try
 		}// end try
    }
    
    public static String getMember(int i){
    	
    	Connection conn = null;
		Statement stmt = null;
		String currentUser = session("connected");
    	
		try{
    		
			conn = DB.getConnection();
			stmt = conn.createStatement();
			
			String sql = "SELECT * FROM teammember WHERE user = " + "'" + currentUser + "'";
			
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			String team = rs.getString("team");
			rs.close();
			
			String sql2 = "SELECT * FROM teammember WHERE team = " + "'" + team + "'";
			
			ResultSet rs2 = stmt.executeQuery(sql2);
			ArrayList<String> members = new ArrayList<String>();
			
			while(rs2.next()){
				String member = rs2.getString("user");
				members.add(member);
			}
			rs2.close();
			
			String s;
			if(!(i >= members.size())) {
				s = members.get(i);
				return s;
			}
    	
	    	return "";
    	
		}catch(SQLException se){
			//Handle errors for JDBC
	        return se.toString();
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
    
	public static String getTeamName(){
	    	
	    	Connection conn = null;
			Statement stmt = null;
			String currentUser = session("connected");
	    	
			try{
	    		
				conn = DB.getConnection();
				stmt = conn.createStatement();
				
				String sql = "SELECT * FROM teammember WHERE user = " + "'" + currentUser + "'";
				
				ResultSet rs = stmt.executeQuery(sql);
				rs.next();
				String team = rs.getString("team");
				rs.close();
				
		    	return team;
	    	
			}catch(SQLException se){
				//Handle errors for JDBC
		        return se.toString();
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
