/*
  Authors (group members):Javier Munoz, Sung-Jun Baek, Thanart Pandey
  Email addresses of group members: jmunoz2014@my.fit.edu, sbaek2015@my.fit.edu, tpandey2017@my.fit.edu
  Group name: 14

  Course: CSE2010
  Section: 2

  Description of the overall algorithm and key data structures:
   To find a next best word selection from the dictionary for the Scrabble.
   The program mostly uses hash, binary representation (byte), and trie algorithm
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class ScrabblePlayer {
    Trie tree = new Trie();
    // initialize ScrabblePlayer with a file of English words
    public ScrabblePlayer(String wordFile) {
        final File inputFile = new File(wordFile);
        Scanner inputData;
        try {
            inputData = new Scanner(inputFile);
            while (inputData.hasNextLine()) {
                final String str = inputData.nextLine();
                if (str.length() > 1  && str.length() < 9) {
                    tree.insert(str.toUpperCase());
                }
            }
            //tree.compress();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // based on the board and available letters, 
    //    return a valid word with its location and orientation
    //    See ScrabbleWord.java for details of the ScrabbleWord class 
    //
    // board: 15x15 board, each element is an UPPERCASE letter or space;
    //    a letter could be an underscore representing a blank (wildcard);
    //    first dimension is row, second dimension is column
    //    ie, board[row][col]     
    //    row 0 is the top row; col 0 is the leftmost column
    // 
    // availableLetters: a char array that has seven letters available
    //    to form a word
    //    a blank (wildcard) is represented using an underscore '_'
    //

    public ScrabbleWord getScrabbleWord(char[][] board, char[] availableLetters) {
        final ScrabbleWord wordOnBoard = findWord(board);
        
        if (wordOnBoard == null) { //if there is no word on the board
            System.out.println("ERROR");
        }
        //convert available letters to the uppercase
        for (int i = 0; i < availableLetters.length; i++) {
            availableLetters[i] = Character.toUpperCase(availableLetters[i]);
        }
        //get the best word from trie tree
        final ScrabbleWord result = tree.getBestWord(wordOnBoard, availableLetters);
        return result;
    }
    
    //call this method to find a existing word on the board
    public static ScrabbleWord findWord(char[][] board) {
      for (int row = 0; row < board.length; row ++) {
          for (int col = 0; col < board.length; col++) {
              if (board[row][col] != ' ') {
                  char orientation;
                  if (col == board.length - 1) {
                      orientation = 'v'; //set as a verticle
                  } else if (row == board.length - 1) {
                      orientation = 'h'; //set as a horizontal
                  }else if (board[row][col + 1] != ' ') {
                      orientation = 'h'; //set as a horizontal
                  } else if (board[row + 1][col] != ' ') {
                      orientation = 'v'; //set as a verticle
                  } else {
                      orientation = 'o'; //if there is no specific orientation
                  }
                  //get word based on the given orientation and row and column
                  String word = getWord(orientation, board, row, col);
                  //return the collected infomation
                  return new ScrabbleWord(word, row, col, orientation);
              }
          }
      }
      return null; //return this if there is no word
    }

    //this method only supports findWord
    public static String getWord(char orientation, char[][] board, int row, int col) {
        if (orientation == 'h') { //if the word starts in horizontal direction
            int wordSize = board.length - col;
            String word = "";
            for (int c = 0; c < wordSize; c++) {
                if (board[row][col + c] == ' ') {
                    break;
                }
                word = word.concat(Character.toString(board[row][col + c]));
            }
            return word;
        } else if (orientation == 'v'){ //if the word starts in vertical direction
            int wordSize = board.length - row;
            String word = "";
            for (int r = 0; r < wordSize; r++) {
                if (board[row + r][col] == ' ') {
                    break;
                }
                word = word.concat(Character.toString(board[row + r][col]));
            }
            return word;
        } else {
            return Character.toString(board[row][col]);
        }
    }
}
