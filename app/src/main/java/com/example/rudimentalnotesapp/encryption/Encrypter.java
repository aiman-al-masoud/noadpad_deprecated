package com.example.rudimentalnotesapp.encryption;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Random;

public class Encrypter {
    public char[] getLettersArray() {
        return lettersArray;
    }

    //this table is basically a 26*26 letters matrix
    private char[][] tabulaRecta;

    //this is a letters array in standard alph order
    private char[] lettersArray;

    public Encrypter(){

        //initializing alph array
        lettersArray = new char[26];
        for(int i =0; i<26; i++) {
            lettersArray[i] = (char)(i+65);
        }

        //initializing tabula recta
        //i- rows: keyword letters. j-columns: plaintext letters
        tabulaRecta = new char[26][26];
        int displacement = 0;
        for(int i=0; i<26; i++) {
            for(int j=0; j<26; j++) {
                int slideChar = j+65+displacement;
                tabulaRecta[i][j] = (char)( slideChar<=90 ? slideChar : slideChar-26);
            }
            displacement++;
        }

    }

    //takes a letter of the key, a letter of plaintext and converts the latter
    //to encrypted text
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private char encryptLetter(char keyLetter, char plaintextLetter) {

        //if not alpha, return unchanged
        if(!Character.isAlphabetic(plaintextLetter)){
            return plaintextLetter;
        }

        //convert to upper case for case-insensitivity while looking
        //up in the table
        keyLetter = Character.toUpperCase(keyLetter);
        char plaintextLetterToUpper = Character.toUpperCase(plaintextLetter);

        //to leave case unchanged
        if(Character.isUpperCase(plaintextLetter)) {
            return tabulaRecta[keyLetter-65][plaintextLetter-65];
        }

        return Character.toLowerCase(tabulaRecta[keyLetter-65][plaintextLetterToUpper-65]);
    }


    //encrypt a string
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String encryptString(String key, String plaintext) {

        //this is the string that will be returned
        String encryptedText = "";

        //parallel running key string counter
        int keyStringCounter = 0;

        //go through each letter of the plaintext
        for(int i =0; i<plaintext.length(); i++) {
            encryptedText += encryptLetter( key.charAt(keyStringCounter) , plaintext.charAt(i));
            keyStringCounter++;
            if(keyStringCounter==key.length()) {
                keyStringCounter = 0;
            }
        }

        return encryptedText;
    }


    //decript letter
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private char decryptLetter(char keyLetter, char encryptedLetter) {

        //if not alpha, return unchanged
        if(!Character.isAlphabetic(encryptedLetter)){
            return encryptedLetter;
        }

        //convert keyLetter to uppercase
        keyLetter = Character.toUpperCase(keyLetter);
        char encryptedLetterToUpper = Character.toUpperCase(encryptedLetter);

        //search for encryptedLetter in keyLetter's row
        for(int i =0; i<26; i++) {
            if(tabulaRecta[keyLetter-65][i]==encryptedLetterToUpper) {
                if(Character.isUpperCase(encryptedLetter)) {
                    return lettersArray[i];
                }else {
                    return Character.toLowerCase(lettersArray[i]);
                }

            }
        }


        return 0;
    }


    //decrypt string
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String decryptString(String key, String encryptedText) {

        //result to be returned
        String decryptedText = "";

        //parallel running key string counter
        int keyStringCounter = 0;

        //go through each letter of the encrypted text
        for(int i=0; i<encryptedText.length(); i++) {
            decryptedText += decryptLetter(key.charAt(keyStringCounter), encryptedText.charAt(i));
            keyStringCounter++;
            if(keyStringCounter==key.length()) {
                keyStringCounter = 0;
            }
        }

        return decryptedText;
    }


    //generate a random key. Can be used for OTPs
    public String generateRandomKey(int keyLength) {
        String result = "";
        Random rand = new Random();

        for(int i = 0; i<keyLength; i++) {
            result+=(char)(rand.nextInt(90-65 +1) +65);
        }

        return result;
    }

}

