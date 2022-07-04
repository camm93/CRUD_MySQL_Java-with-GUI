
package library;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;


/**
 *
 * @author CRISTIAN
 */
public class Library {

    protected Connection conn;
    public String path;
    
    /**
     * 
     * @throws IOException
     * @throws ParseException
     * @throws SQLException 
     */
    public Library() throws IOException, ParseException, SQLException {
        JSONParser parser = new JSONParser();
        String credentials_path = System.getProperty("user.dir") +
            "\\src\\stuff\\Credentials.json";
        try {
            FileReader fr = new FileReader(credentials_path);
            JSONObject jsonObject = (JSONObject) parser.parse(fr);
            String dbURL = (String) jsonObject.get("dbURL");
            String dbUsername = (String) jsonObject.get("dbUsername");
            String dbPassword = (String) jsonObject.get("dbPassword");

            conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (conn != null) {
            System.out.println("Connection Established!");
        }
    }
    
    /**
     * 
     * @param firstName
     * @param lastName
     * @param nationality
     * @return
     * @throws SQLException 
     */
    public boolean create(String firstName, String lastName, String nationality)
            throws SQLException {
        String sql = "INSERT INTO autor (aut_nombre, aut_apellido,"
                + " aut_nacionalidad) VALUES(?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(sql);
        
        statement.setString(1, firstName);
        statement.setString(2, lastName);
        statement.setString(3, nationality);
        
        int rowsInserted = statement.executeUpdate();
        return rowsInserted > 0;
    }
    
    /**
     * 
     * @param id
     * @param firstName
     * @param lastName
     * @param nationality
     * @return
     * @throws SQLException 
     */
    public Object[][] readAuthors(Integer id, String firstName, String lastName,
            String nationality) throws SQLException {
        ArrayList<Object[]> data = new ArrayList<Object[]>();
        
        String sql = "SELECT * FROM autor";
        String condition = "";
        
        if (id != null) {
            condition = "aut_id = " + id;
        }
        
        if (firstName != null) {
            if (condition.length() > 0){
                condition += " AND ";
            }
            condition = "aut_nombre = \"" + firstName + "\"";
        }
        
        if (lastName != null) {
            if (condition.length() > 0) {
                condition += " AND ";
            }
            condition += "aut_apellido = \"" + lastName + "\"";
        }

        if (nationality != null) {
            if (condition.length() > 0) {
                condition += " AND ";
            }
            condition += "aut_nacionalidad = \"" + nationality + "\"";
        }
        
        if (condition.length() > 0) {
            sql += " WHERE " + condition;
        }
        
        Statement statement = conn.createStatement();
        ResultSet result = statement.executeQuery(sql);
        
        while(result.next()) {
            Object[] array = new Object[4];
            array[0] = result.getString("aut_id");
            array[1] = result.getString("aut_nombre");
            array[2] = result.getString("aut_apellido");
            array[3] = result.getString("aut_nacionalidad");
            data.add(array);
        }
        
        Object[][] output = new Object[data.size()][4];
        for (int i=0; i<data.size(); i++){
            Object[] row = data.get(i);
            output[i][0] = row[0];
            output[i][1] = row[1];
            output[i][2] = row[2];
            output[i][3] = row[3];
        }
        return output;
    }
    
    /**
     * 
     * @param author
     * @return
     * @throws SQLException 
     */
    public boolean update(Author author) throws SQLException {
        String sql = "UPDATE autor SET aut_nombre=?, aut_apellido=?, "
                + "aut_nacionalidad=? WHERE aut_id=?";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, author.getFirstName());
        statement.setString(2, author.getLastName());
        statement.setString(3, author.getNationality());
        statement.setInt(4, author.getId());
        System.out.println(sql);
        
        int rowsUpdated = statement.executeUpdate();
        return rowsUpdated > 0;
    }
    
    /**
     * 
     * @param id
     * @param firstName
     * @param lastName
     * @param nationality
     * @throws SQLException 
     */
    public void delete(String id, String firstName, String lastName,
            String nationality) throws SQLException {
        String sql = "DELETE FROM autor";
        String condition = "";
        
        if (id != null) {
            condition = "aut_id = " + id;
        }
        
        if (firstName != null) {
            if (condition.length() > 0){
                condition += " AND ";
            }
            condition = "aut_nombre = \"" + firstName + "\"";
        }
        
        if (lastName != null) {
            if (condition.length() > 0) {
                condition += " AND ";
            }
            condition += "aut_apellido = \"" + lastName + "\"";
        }

        if (nationality != null) {
            if (condition.length() > 0) {
                condition += " AND ";
            }
            condition += "aut_nacionalidad = \"" + nationality + "\"";
        }
        
        if (condition.length() > 0) {
            sql += " WHERE " + condition;
        } else {
            sql = "";
        }     
        System.out.println(sql);
        
        PreparedStatement statement = conn.prepareStatement(sql);
        
        int rowsDeleted = statement.executeUpdate();
        System.out.println("Borrado exitoso " + (rowsDeleted > 0));
        
    }
    
    /**
     * 
     * @return
     * @throws SQLException 
     */
    public String[] listOfNationalities() throws SQLException {
        String sql = "SELECT DISTINCT aut_nacionalidad FROM autor ORDER BY 1";
        Statement statement = conn.createStatement();
        ResultSet result = statement.executeQuery(sql);
        
        ArrayList<String> nationalities = new ArrayList<String>();
        while (result.next()) {
            nationalities.add(result.getString(1));
        }
        return nationalities.toArray(new String[0]);
    }   
}
