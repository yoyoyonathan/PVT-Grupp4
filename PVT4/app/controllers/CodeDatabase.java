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

			codeFromDB.amount -= 1;
			teamFromDB.points += codeFromDB.value;

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			registerCodeToTeam(teamFromDB, codeID);
			return redirect(routes.Application.profilePage(currentUser));
		}
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
