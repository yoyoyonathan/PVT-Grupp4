package controllers;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import models.*;
import play.data.*;
import play.db.DB;
import play.mvc.*;

public class CommentDatabase extends Controller {

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
			
			if(comment == ""){
				return redirect(routes.Application.profilePage(currentUser));
			}
		
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
		    conn.close();
		
	
			String insertIntoDatabase = "INSERT INTO teamcomments (user, team, comment) VALUES(?, ?,?)";
		    
			preparedStatement = conn.prepareStatement(insertIntoDatabase);
			preparedStatement.setString(1, currentUser);
			preparedStatement.setString(2, teamName);
			preparedStatement.setString(3, comment);
			preparedStatement.executeUpdate();
 			return redirect(routes.Application.profilePage(currentUser));
			
		} catch (SQLException se){
 			return badRequest(se.toString());
		} 
    	catch (Exception e) {
// 			return internalServerError(e.toString());
 			return null;
    	} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				return badRequest(se.toString());
			} 
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
				
			return returnStringUser +" skrev "+returnStringDate+": \n" +returnString;
			
    	} catch (SQLException se){
 			return se.toString();
		} 
    	catch (Exception e) {
// 			return internalServerError(e.toString());
 			return null;
    	} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				return se.toString();
			} 
		} 
    }
	
}
