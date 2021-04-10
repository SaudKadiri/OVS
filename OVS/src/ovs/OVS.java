/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ovs;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author blest
 */
public class OVS {

    /**
     * @param args the command line arguments
     * sudo service apache2 start
     */
    public void insert(String ID, 
                       String password,
                       String address,
                       String birth_place,
                       String date_of_birth,
                       String email,
                       String name,
                       int phone_number,
                       char gender, 
                       String job, 
                       char martial_status,
                       Boolean cast_status                       
                      ) throws SQLException {
        Connection connect = null;
        Statement stmnt = null; 
        try {
            Class.forName("com.mysql.ci.jdbc.Driver");
            connect = DriverManager.getConnection("jdbc:mysql://localhost/OVS", "root", "group7");
        } catch (ClassNotFoundException ex) {
            System.out.println("Execption");
            Logger.getLogger(home.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        stmnt = connect.createStatement();
        
        int rows_affected = stmnt.executeUpdate("INSERT INTO 'voter' ('ID', 'password', 'address', 'birth_place', 'date_of_birth', 'email', 'name', 'phone_number', 'gender', 'job', 'martial_status', 'cast_status')" +
                                                    "VALUES ( "+ID+" "+password+"  "+address+" "+birth_place+" "+date_of_birth+" "+email+" "+name+" "+phone_number+" "+gender+" "+job+" "+martial_status+" "+cast_status+")" );
        System.out.println(rows_affected);
        if (connect != null) {
            connect.close();
        }
        if (stmnt != null) {
            stmnt.close();
        }
    }
}