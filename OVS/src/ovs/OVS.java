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
 * @author Anas Khan & Saud Kadiri @ Group 7
 */

public class OVS {

    /**
     * @param args the command line arguments
     * sudo service apache2 start
     */
    Connection connect = null;
    private void OVS() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connect = DriverManager.getConnection("jdbc:mysql://localhost/ovs", "root", "saud");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void insert(String ID, 
                       String password,
                       String address,
                       String birth_place,
                       String date_of_birth,
                       String email,
                       String name,
                       String phone_number,
                       char gender, 
                       String job, 
                       char martial_status,
                       Boolean cast_status                       
                      ) throws SQLException {
        Connection connect = null;
        Statement stmnt = null; 
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connect = DriverManager.getConnection("jdbc:mysql://localhost/ovs", "root", "saud");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(home.class.getName()).log(Level.SEVERE, null, ex);
        }
        Cryptography crypt = new Cryptography();
        ID = crypt.cipher(ID);
        address = crypt.cipher(address);
        email = crypt.cipher(email);
        name = crypt.cipher(name);
        birth_place = crypt.cipher(birth_place);
        phone_number = crypt.cipher(phone_number);
        password = crypt.cipher(password);
        

        stmnt = connect.createStatement();
        String sep = "', '";
        System.out.println("INSERT INTO voter " + 
                "VALUES ( '" + ID + sep + password +  sep + address + sep + birth_place + sep + date_of_birth + sep + email + sep +  name + sep + phone_number + sep +  gender + sep + job + sep +  martial_status + sep + cast_status + "', '', '', '', '' );");
        int rows_affected = stmnt.executeUpdate("INSERT INTO voter " + 
                "VALUES ( '" + ID + sep + password +  sep + address + sep + birth_place + sep + date_of_birth + sep + email + sep +  name + sep + phone_number + sep +  gender + sep + job + sep +  martial_status + sep + '0' + "', '', '', '', '' );");
        System.out.println(rows_affected);
        if (connect != null) {
            connect.close();
        }
        if (stmnt != null) {
            stmnt.close();
        }
    }
}