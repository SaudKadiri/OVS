/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ovs;
/*  This program is developed by Group 7 by putting a combined effort.
 *
 *  Write History:
 *      26th September, 2020 :-> Standard Declaration:- main() and other functionalites.
 *      5th  October,   2020 :-> Class accesses.
 *      20th October,   2020 :-> Completed the program.
 *      7th  November,  2020 :-> Documentation.
 *
 * Update History:
 *      15th December, 2020 :-> Bugs fixation.
 *      29th March, 2021    :-> Database Connectivity and GUI.
 *      24th May, 2021      :-> Project completion.
 *
 *  There are three classes.
 *      Candidate   :  Used to represent a candidate and his/her associated members.
 *      OVS         :  Consists of main method.
 *      IVR         :  This class consists of the functionality to perform the algorithm.
 *
 *   The six standard voting  functions are:
 *      vote()        : updates the preferences array to indicate that the voter has that candidate as their rank preference 
 *                        (where 0 is the first preference, 1 is the second preference, etc.).
 *      tabulate()    : The function updates the number of votes each candidate has at every stage in the election.
 *      printWinner() : Once calculated, prints the winner of the election.
 *      findMin()     : The function returns the minimum vote total for any candidate who is still in the election.
 *      isTie()       : The function returns true if every candidate remaining in the election has the same number of votes,
 *                        and false otherwise.
 *      eliminate()   : The function eliminates the candidate (or candidates) who have min number of votes.
 * 
 *    About:
 *          1. Every voter ranks their preferences.
 *          2. If a candidate has a majority (more than half) of the votes, they are the  winner.
 *          3. Otherwise; Eliminate the candidate with the fewest votes and re-run the election without them.
 * 
 *    Summary:
 *          1. We've used RCV(Ranked Choice Voting) System as the alogirthm.
 *          2. In particular, IRV(Instant Runoff Voting) (a type of RCV) is used.
 *          3. We've tried the program to be as optimize as possible.
 */
/**
 *
 * @author Grp 7
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


import java.util.Scanner;


public class Runoff {
    public static void main(String[] argv) throws SQLException {
        Connection connect = null;
        Statement stmnt = null; 
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connect = DriverManager.getConnection("jdbc:mysql://localhost/ovs", "root", "saud");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Runoff.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            stmnt = connect.createStatement();
        } catch (SQLException exp) {
            System.out.println(exp);
        }
        while (true) {
            // Calculate votes given remaining candidates
            IRV.tabulate(stmnt);

      
            // Check if election has been won
            Boolean won = IRV.printWinner(stmnt);
            if (won) {
                break;
            }

            // Eliminate last-place candidates
            int min = IRV.findMin(stmnt);
            Boolean tie = IRV.isTie(min, stmnt);
           // If tie, everyone wins
            if (tie) {
                System.out.println("It's a tie:");
                try {
                    ResultSet rs = stmnt.executeQuery("SELECT Candidate_id AS id FROM candidate WHERE is_eliminated = 0;");
                    while(rs.next()) {
                        System.out.println("Result: " + rs.getString("id"));
                    }
                rs.close();
                } catch (SQLException exp) {
                    System.out.println(exp);
                }
                break;
            }

            // Eliminate anyone with minimum number of votes
            IRV.eliminate(min, stmnt);

            // Reset vote counts back to zero
            try {
                int ret = stmnt.executeUpdate("UPDATE candidate SET votes = 0;");
            } catch (SQLException exp) {
                System.out.println(exp);
            }
        }
    }
}


class IRV {
    final static int candidate_count = 4;

    // Tabulate votes for non-eliminated candidates
    static void tabulate(Statement stmnt) {
        Cryptography encrypt = new Cryptography();
        for (int i = 0, j = 1; i < candidate_count; i++) {
            j = 1;
            try {
                ResultSet rs = stmnt.executeQuery("SELECT is_eliminated as state from candidate WHERE Candidate_id" + " = " + encrypt.cipher("'flxvvcxr" + (i + 1) + "u'") + ";");

                while (rs.next()) {
                    System.out.println("Eliminated.");
                    j++;
                }
            } catch (SQLException exp) {
                System.out.println(exp);
            }
            
            
            System.out.println("SELECT COUNT(*) AS votecount FROM voter WHERE choice" + j + " = " + "'flxvvcxr" + (i + 1) + "u';");
            int count = 0;
            try {
                ResultSet r = stmnt.executeQuery("SELECT COUNT(*) AS votecount FROM voter WHERE choice" + j + " = " + "'flxvvcxr" + (i + 1) + "u';");
                r.next();
                count = r.getInt("votecount");
                System.out.println("Update voter SET votes = " + count + " WHERE Candidate_id = '" + "flxvvcxr" + (i + 1) + "u';");
                int ret = stmnt.executeUpdate("Update candidate SET votes = " + count + " WHERE Candidate_id = '" + encrypt.cipher("flxvvcxr" + (i + 1) + "u") +  "';");
                System.out.println(ret);
                r.close() ;
            } catch (SQLException exp) {
                System.out.println(exp);
            }             
        }
    }

    // Print the winner of the election, if there is one
    static Boolean printWinner(Statement stmnt) {
        int totalVotes = 0;
        Cryptography decrypt = new Cryptography();
        String id = "";
        try {
                ResultSet r = stmnt.executeQuery("SELECT SUM(votes) AS totalvotes FROM candidate;");
                r.next();
                totalVotes = r.getInt("totalvotes");
                System.out.println(totalVotes);
                r.close() ;
        } catch (SQLException exp) {
            System.out.println(exp);
        }
        int majority = totalVotes / 2;
        try {
            ResultSet r = stmnt.executeQuery("SELECT Candidate_id AS id, votes FROM candidate WHERE votes > " + majority + ";");
            while(r.next()) {
                id = r.getString("id");
                System.out.println("Winner is: " + decrypt.decipher(id));
            } 
            r.close();
        } catch (SQLException exp) {
            System.out.println(exp);
        }
        if (id == "") {
            return false;
        }
        return true;
    }

    // Return the minimum number of votes any remaining candidate has
    static int findMin(Statement stmnt) {
        int min = 900;
        try {
            ResultSet r = stmnt.executeQuery("SELECT MIN(votes) AS min_votes FROM candidate;");
            r.next();
            min = r.getInt("min_votes");
            r.close() ;
        } catch (SQLException exp) {
            System.out.println(exp);
        }
        return min;
    }

    // Return true if the election is tied between all candidates, false otherwise
    static Boolean isTie(int min, Statement stmnt) {
        int count;
        try {
            ResultSet r = stmnt.executeQuery("SELECT votes AS vote_count FROM candidate;");
            while(r.next()) {
                count = r.getInt("vote_count");
                if (count != min) {
                    r.close();
                    return false;
                }
            }
            r.close();
        } catch (SQLException exp) {
            System.out.println(exp);
        }
        return true;
    }

    // Eliminate the candidate (or candidiates) in last place
    static void eliminate(int min, Statement stmnt) {
        int count;
        try {
            int ret = stmnt.executeUpdate("Update candidate SET is_eliminated = 1 WHERE votes = " + min + ";");
        } catch (SQLException exp) {
            System.out.println(exp);
        }
    }
}
