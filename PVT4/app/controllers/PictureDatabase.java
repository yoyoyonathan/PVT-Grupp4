package controllers;

import java.util.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import javax.imageio.ImageIO;
import models.*;
import play.*;
import play.api.libs.json.*;
import play.data.*;
import play.db.DB;
import play.db.ebean.Model;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import views.html.*;
import static play.libs.Json.toJson;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
//Picture imports
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import views.html.*;
import play.data.Form;
import play.db.*;
import views.*;
//Imports for picture
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Http.Response;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.DatatypeConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PictureDatabase extends Controller{
	
	public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}  

	public static Result savePicture() {			//Felhantering!
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		String currentuser = session("connected");
		BufferedImage img = null;
		BufferedImage finalImg = null;
		InputStream inputStream = null;
		String[] acceptedTypes = new String[] {"jpeg", "jpg", "jfif", "jpeg 2000", "tiff",
				"riff", "png", "gif", "bmp", "png", "jpeg xr", "img", "bpg", "webp" };
		
		try {
			conn = DB.getConnection();

			String sql = "INSERT INTO userpic (user, picture, type) VALUES(?,?,?)";
			preparedStatement = conn.prepareStatement(sql);

			MultipartFormData body = request().body().asMultipartFormData();

			FilePart picture = body.getFile("picture");

			if (picture != null) {
				File file = picture.getFile();
				img = ImageIO.read(file);
				String type = picture.getContentType().substring(
						picture.getContentType().lastIndexOf("/") + 1);
				
				if(!Arrays.asList(acceptedTypes).contains(type)){
					return redirect(routes.Application.profilePage(currentuser));		//"File format is not supported"
				}
				
//				finalImg = resize(img, 400, 400);

//				File outputfile = new File("image." + type);
//				ImageIO.write(finalImg, type, outputfile);
				
				inputStream = new FileInputStream(file);
//				inputStream = new FileInputStream(outputfile);

				preparedStatement.setString(1, currentuser);
				preparedStatement.setBlob(2, inputStream);
				preparedStatement.setString(3, type);
				preparedStatement.executeUpdate();

				return redirect(routes.Application.profilePage(currentuser));
		
			} else {
				return redirect(routes.Application.profilePage(currentuser));	//Tom bild, bör bli ett fel
			}

		} catch (Exception e) {
			// Handle errors for Class.forName
			return ok(e.toString());
		} finally {
			// finally block used to close resources
			try {
				if (preparedStatement != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothin
		}

	}
	
	public static String getPictureUser(int i) {
		String currentUser = session("connected");
		Connection conn = null;
		Statement stmt = null;
		
		try {
			
			conn = DB.getConnection();
			stmt = conn.createStatement();
			
			String team = TeamDatabase.getTeamName();
			
			String sql2 = "SELECT DISTINCT s.user FROM teammember s INNER JOIN userpic d ON d.user = s.user WHERE `team` = " + "'" + team + "'";
			ResultSet rs2 = stmt.executeQuery(sql2);
			ArrayList<String> members = new ArrayList<String>();
			while (rs2.next()) {
				String user = rs2.getString("user");
				members.add(user);
			}
			rs2.close();
			
			ArrayList<Integer> ids = new ArrayList<Integer>();
			
			for (int j = 0; j < members.size(); j++) {
				
				String sql = "SELECT * FROM userpic WHERE user = " + "'" + members.get(j) + "'";
				ResultSet rs = stmt.executeQuery(sql);
				
				while (rs.next()){
					int id = rs.getInt("ID");
					ids.add(id);
				}
				rs.close();
			}
			
			String userName = null;
			ArrayList<String> userNames = new ArrayList<String>();
			
			for (int j = 0; j < ids.size(); j++){
			
		 		String sql3 = "SELECT * FROM `userpic` WHERE `ID` = " + "'" + ids.get(j) + "'";
				ResultSet rs3 = stmt.executeQuery(sql3);
				
				rs3.next();
				userName = rs3.getString("user");
				userNames.add(userName);
				rs3.close();
			}
			
			int behind = userNames.size() - i;
			
			return userNames.get(behind) + " delar en bild";
			
		} catch (SQLException se) {
			return se.toString();
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				return se.toString();
			} // end finally try
		} // end try
	}
	
	
	public static Result getPicture(int i) {
		
		String currentUser = session("connected");
		Connection conn = null;
		Statement stmt = null;
		
		try {
			
			conn = DB.getConnection();
			stmt = conn.createStatement();
			
//			String sql1 = "SELECT * FROM `teammember` WHERE `user` = " + "'" + currentUser + "'";
//			ResultSet rs1 = stmt.executeQuery(sql1);
//			rs1.next();
			String team = TeamDatabase.getTeamName();
//			rs1.close();
			
			String sql2 = "SELECT DISTINCT s.user FROM teammember s INNER JOIN userpic d ON d.user = s.user WHERE `team` = " + "'" + team + "'";
			ResultSet rs2 = stmt.executeQuery(sql2);
			ArrayList<String> members = new ArrayList<String>();
			while (rs2.next()) {
				String user = rs2.getString("user");
				members.add(user);
			}
			rs2.close();
			
			ArrayList<Integer> ids = new ArrayList<Integer>();
			
			for (int j = 0; j < members.size(); j++) {
				
				String sql = "SELECT * FROM userpic WHERE user = " + "'" + members.get(j) + "'";
				ResultSet rs = stmt.executeQuery(sql);
				
				while (rs.next()){
					int id = rs.getInt("ID");
					ids.add(id);
				}
				rs.close();
			}
			
			Blob image = null;
			String type = null;
			ArrayList<Blob> pictures = new ArrayList<Blob>();
			ArrayList<String> types = new ArrayList<String>();
			
			for (int j = 0; j < ids.size(); j++){
			
		 		String sql3 = "SELECT * FROM `userpic` WHERE `ID` = " + "'" + ids.get(j) + "'";
				ResultSet rs3 = stmt.executeQuery(sql3);
				
				rs3.next();
				image = rs3.getBlob("picture");
				type = rs3.getString("type");
				pictures.add(image);
				types.add(type);
				rs3.close();
			}
			
			int behind = pictures.size() - i;
					
			int blobLength = (int) pictures.get(behind).length();
			byte[] bytes = pictures.get(behind).getBytes(1, blobLength);
			type = types.get(behind);
			
			return ok(bytes).as("image/" + type);
			
		} catch (SQLException se) {
			return badRequest(se.toString());
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				return badRequest(se.toString());
			} // end finally try
		} // end try
	}

}
