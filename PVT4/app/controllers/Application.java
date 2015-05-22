package controllers;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.sql.*;
import java.sql.Date;

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
    
    public static Result joinTeam() {
    	return ok(joinTeam.render(""));
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
    
    public static Result signup() {
	    String currentUser = session("connected");
        if(currentUser != null) {
             return ok(index.render("Du är redan inloggad som " + currentUser + "!"));
        } 
		return ok(signup.render(""));
	}
    
    public static void InsertPictureToMySql(String s){
    	
//    	Class.forName("org.gjt.mm.mysql.Driver");
    	Connection conn = null;
//      Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/databaseName", "root", "root");
        String INSERT_PICTURE = "insert into MyPictures(id, name, photo) values (?, ?, ?)";

        FileInputStream fis = null;
        PreparedStatement ps = null;
        try {
//          conn.setAutoCommit(false);
          conn = DB.getConnection();
          
          File file = new File("myPhoto.png");
          fis = new FileInputStream(file);
          ps = conn.prepareStatement(INSERT_PICTURE);
          ps.setString(1, "001");
          ps.setString(2, "name");
          ps.setBinaryStream(3, fis, (int) file.length());
          ps.executeUpdate();
          conn.commit();
//        } finally {
//            ps.close();
//            fis.close();
//          }        
    } catch (SQLException se) {
			// Handle errors for JDBC
//			return internalServerError(se.toString());
//			return badRequest(joinTeam.render("Det laget du har valt är fullt/extisterar inte."));
		} catch (Exception e) {
			// Handle errors for Class.forName
//			return internalServerError(e.toString());
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
//				return internalServerError(se.toString());
			}
		}
    }
    
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
    
    public static Result addComment(){
    	
    	Connection conn = null;
    	Statement stmt = null;
		
		DynamicForm formData = Form.form().bindFromRequest();
    	String currentUser = session("connected");
    	
    	String comment = formData.get("comment");
		PreparedStatement preparedStatement = null;
		
		try {
			conn = DB.getConnection();
			stmt = conn.createStatement();
		
			String sql = "SELECT * FROM `teammember` WHERE `user` = " + "'" + currentUser + "'";
			
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			String teamName = rs.getString("team");
			rs.close();
			
			String sql2 = "SELECT * FROM `teamcomments`";
			
			ResultSet rs2 = stmt.executeQuery(sql2);
			
			int length = 1;
			while(rs2.next()){
				length++;
		   	}
		    rs2.close();
	
		
	
			String insertIntoDatabase = "INSERT INTO teamcomments (user, team, comment) VALUES(?, ?,?)";
		    
			preparedStatement = conn.prepareStatement(insertIntoDatabase);
			preparedStatement.setString(1, currentUser);
			preparedStatement.setString(2, teamName);
			preparedStatement.setString(3, comment);
			preparedStatement.executeUpdate();
 			return redirect(routes.Application.profilePage(currentUser));
			
		} catch (SQLException se){
 			return internalServerError(se.toString());
		} 
    	
    }
    
    public static String getComments(int i){
    	
    	Connection conn = null;
    	Statement stmt = null;
    	String currentUser = session("connected");

    	try{
			conn = DB.getConnection();
			stmt = conn.createStatement();
			
			String sql = "SELECT * FROM `teammember` WHERE `user` = " + "'" + currentUser + "'";
			
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			String teamName = rs.getString("team");
			rs.close();
			String sql2 = "SELECT * FROM `teamcomments` WHERE `team` = " + "'" + teamName + "'" + "ORDER BY `ID` ASC";
			
			ResultSet rs2 = stmt.executeQuery(sql2);
			
			ArrayList<String> list = new ArrayList<String>();
			ArrayList<String> listdate = new ArrayList<String>();
			ArrayList<String> listUser = new ArrayList<String>();
			
			
			
			while(rs2.next()){
			String comment = rs2.getString("comment");
			Timestamp date = rs2.getTimestamp("time");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			dateFormat.format(date);
			String S = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
			String user = rs2.getString("user");
			String datum = ""+S;
			listUser.add(user);
			listdate.add(datum);
			list.add(comment);
			}
			rs2.close();
			
			//If detta lag är på plats i sorterat efter poäng
			
			String returnString = "";
			String returnStringDate ="";
			String returnStringUser="";
			
				int p = 0;
			if (i==1)
				p = list.size()-1;
			if (i==2)
				p = list.size()-2;
			if (i==3)
				p = list.size()-3;
			if (i==4)
				p = list.size()-4;
			if (i==5)
				p = list.size()-5;
			if (i==6)
				p = list.size()-6;
			if (i==7)
				p = list.size()-7;
			if (i==8)
				p= list.size()-8;
			if (i==9)
				p = list.size()-9;
			if (i==10)
				p = list.size()-10;
			
			returnString = list.get(p);
			returnStringDate = listdate.get(p);
			returnStringUser = listUser.get(p);
				
			return returnStringUser +" skrev "+returnStringDate+": " +returnString;
			
    	} catch (SQLException se){
 			return se.toString();
		} 
//    	catch (SQLException se) {
// 			// Handle errors for JDBC
//// 			return internalServerError(se.toString());
//// 			return badRequest(index.render("Email/användarnamn är redan taget."));
//    		return null;
// 		}
    	catch (Exception e) {
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
    			ord2.add("Von Doom");
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
				returnString = "" + tree.pollFirst().name + ": ";
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
	
	public static Result registerCode() throws SQLException{
		Connection conn = null;
		conn = DB.getConnection();
		Code codeFromDB = new Code();
		Team teamFromDB = new Team();
		
		
		DynamicForm formData = Form.form().bindFromRequest();
    	String currentUser = session("connected");

		//String teamName = formData.get("team");

		String codeID = formData.get("codeID");
		
		PreparedStatement preparedStatement = null;
		PreparedStatement preparedStatementCode = null;
		
		teamFromDB = getTeam(currentUser);
		codeFromDB = getCode(codeID);

		codeFromDB.amount -= 1;
		teamFromDB.points += codeFromDB.value;

		String insertIntoDatabase = "UPDATE team SET points=? WHERE name=?";
			    
		preparedStatement = conn.prepareStatement(insertIntoDatabase);
		preparedStatement.setInt(1, teamFromDB.points);
		preparedStatement.setString(2, teamFromDB.name);
		preparedStatement.executeUpdate();
		
		String insertIntoDatabaseCode = "UPDATE code SET amount=? WHERE codeID=?";
		
		preparedStatementCode = conn.prepareStatement(insertIntoDatabaseCode);
		preparedStatementCode.setInt(1, codeFromDB.amount);
		preparedStatementCode.setString(2, codeFromDB.codeID);
		preparedStatementCode.executeUpdate();
		
		conn.close();
		
        return redirect(routes.Application.profilePage(currentUser));
		}

	public static Code getCode(String codeID) {
		Statement stmtCode = null;
		Connection conn = null;
		Code codeFromDB = new Code();
		conn = DB.getConnection();
		
		try {
			stmtCode = conn.createStatement();

			String sqlForCode = "SELECT * FROM `code` WHERE `codeID` = " + "'"
					+ codeID + "'";
			ResultSet rs = stmtCode.executeQuery(sqlForCode);
			rs.next();
			codeFromDB.value = rs.getInt("value");
			codeFromDB.amount = rs.getInt("amount");
			codeFromDB.codeID = rs.getString("codeID");
			rs.close();
			conn.close();
			return codeFromDB;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}    


