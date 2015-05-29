package controllers;

import java.sql.*;
import java.util.ArrayList;

import models.*;
import play.data.*;
import play.db.DB;
import play.mvc.*;

public class CodeDatabase extends Controller {

	public static Result registerCode() {
		Connection conn = null;
		conn = DB.getConnection();
		Code codeFromDB = new Code();
		Team teamFromDB = new Team();

		DynamicForm formData = Form.form().bindFromRequest();
		String currentUser = session("connected");
		String codeID = formData.get("codeID");

		PreparedStatement preparedStatement = null;
		PreparedStatement preparedStatementCode = null;

		teamFromDB = TeamDatabase.getTeam(currentUser);
		if (!codeRegisteredToTeam(teamFromDB, codeID)) {

			codeFromDB = getCode(codeID);
			try {
				codeFromDB.amount -= 1;
				teamFromDB.points += codeFromDB.value;
			} catch (NullPointerException e) {
				try {
					conn.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
				return badRequest("fel vid codeFromDB-1");
			}
			String insertIntoDatabase = "UPDATE team SET points=? WHERE name=?";

			try {
				preparedStatement = conn.prepareStatement(insertIntoDatabase);

				preparedStatement.setInt(1, teamFromDB.points);
				preparedStatement.setString(2, teamFromDB.name);
				preparedStatement.executeUpdate();

				String insertIntoDatabaseCode = "UPDATE code SET amount=? WHERE codeID=?";

				preparedStatementCode = conn
						.prepareStatement(insertIntoDatabaseCode);
				preparedStatementCode.setInt(1, codeFromDB.amount);
				preparedStatementCode.setString(2, codeFromDB.codeID);
				preparedStatementCode.executeUpdate();

				conn.close();
			} catch (SQLException e) {
				try {
					conn.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}// TODO Auto-generated catch block
				e.printStackTrace();
			}
			registerCodeToTeam(teamFromDB, codeID);
			return redirect(routes.Application.profilePage(currentUser));
			// return redirect("/profile/" + session(currentUser) + "#redeem");
		}
		return badRequest();
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
			try {
				conn.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static boolean codeRegisteredToTeam(Team t, String c) {
		Connection conn = DB.getConnection();
		Statement stmt;
		try {
			stmt = conn.createStatement();

			String sql = "SELECT * FROM registeredcode WHERE team = '" + t.name
					+ "' AND code = '" + c + "'";
			ResultSet rs = stmt.executeQuery(sql);
			String codeInDB = null;
			rs.next();
			codeInDB = rs.getString("code");
			rs.close();
			conn.close();
			if (!c.equals(codeInDB) || codeInDB.isEmpty()) {
				return false;
			} else {
				return true;
			}
		} catch (SQLException e) {
			try {
				conn.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		}

	}

	public static void registerCodeToTeam(Team team, String codeID) {
		PreparedStatement stmt = null;
		Connection conn = DB.getConnection();
		String insertIntoDB = "INSERT INTO registeredcode (team, code) VALUES(?,?)";
		try {
			stmt = conn.prepareStatement(insertIntoDB);
			stmt.setString(1, team.name);
			stmt.setString(2, codeID);
			stmt.executeUpdate();
			conn.close();
		} catch (SQLException e) {
			try {
				conn.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String listCodes() {
		Statement stmt = null;
		Connection conn = DB.getConnection();

		try {

			conn = DB.getConnection();
			stmt = conn.createStatement();

			String sql = "SELECT * FROM registeredcode WHERE team = " + "'"
					+ TeamDatabase.getTeamName() + "'";
			ResultSet rs = stmt.executeQuery(sql);

			ArrayList<String> codes = new ArrayList<String>();
			while (rs.next()) {
				String code = rs.getString("code");
				codes.add(code);
			}
			rs.close();

			String listString = "";

			for (String s : codes) {
				listString += s + "\t";
			}
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(listString == "")
				
				return "Inga koder registrerade";
			
			return listString;
	}catch(SQLException se){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        return null;
	}finally{
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

	public static String latestRegistredCode(){
		
		try {
			Thread.sleep(250);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Statement stmt = null;
		Connection conn = DB.getConnection();
		String codeFromDB = null;
		String codeMSG;
		String sql = "SELECT * FROM registeredcode WHERE team = " + "'"
					+ TeamDatabase.getTeamName() + "'" + "AND time = (select max(time) FROM registeredcode WHERE team = " + "'" + 
					TeamDatabase.getTeamName() + "')";
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
			codeFromDB = rs.getString("code");
			}
			rs.close();
			conn.close();
		} catch (SQLException e) {
			try {
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		if(codeFromDB != null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return codeMSG = "Ditt lags senaste registrerade kod är: \n" + codeFromDB;
		}else{
		return "";	
		}
	}
}
