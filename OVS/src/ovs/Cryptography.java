/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ovs;

/**
 *
 * @author blest
 */
public class Cryptography {
    private static final char[] KEY = "STREKYZVOGDXPJNCUIALFBMWHQ".toCharArray();

    protected String cipher(String string)
    {
        int i;
        String result = "";
        char[] text = string.toCharArray();
        //formulate the ciphertext
        for (char ch: text) {
            if (Character.isLetter(ch))  {
                if (Character.isUpperCase(ch)) {
                    i = (int) ch - 65;
                    result += KEY[i];
                } else {
                    i = (int)ch - 97;
                    result += (char)((int)KEY[i] + 32);
                }
            } else {
                if (Character.isDigit(ch)) {
                    i = (int)ch - 48;
                    i = (i + 7) % 10;
                    result += (char)(i + 48);
                } else {
                    result += ch;
                }
            }
        }
        return result;
    }
    
    protected String decipher(String string) {
        String result = "";
        char[] text = string.toCharArray();
        for (char ch: text) {
            if (Character.isLetter(ch))  {
                if (Character.isUpperCase(ch)) {
                    result+=(char) (65 + position(ch));
                } else {
                    result += (char)(97 + position(ch));
                }
            } else {
                if (Character.isDigit(ch)) {
                    int i = (int) ch - 48;
                    i = (i + 3) % 10;
                    result += (char)(i + 48);   
                } else {
                    result += ch;
                }
            }
        }
        return result;
    }   
    
    private static int position(char c) {
        c = Character.toUpperCase(c);
        int i = 0;
        for (char ch: KEY) {
            if (ch == c) {
                return i;
            }
            i++;
        }
        return 0;
    }
}
