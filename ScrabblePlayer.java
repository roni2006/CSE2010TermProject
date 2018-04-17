/*
  Authors (group members):Javier Munoz, Sung-Jun Baek, Thanart Pandey
  Email addresses of group members: jmunoz2014@my.fit.edu, sbaek2015@my.fit.edu, tpandey2017@my.fit.edu
  Group name: 14

  Course: CSE2010
  Section: 2

  Description of the overall algorithm and key data structures:
      The dictionary is stored as a trie.
          Each entry in the trie has a byte element, representing its index in the alphabet
          Each entry has a size 26 array of children
      To find the best Scrabble Word
          Find the word on the board, return as original word
          Find words the start with one of the letters from original word
          Find words that are the original word + a suffix
          Return the word that is worth the most points
      Finding words (availableCharacters, currentEntry, currentResult)
          if current entry is the end of a word
              check to see if its more points than the currentResult
              if it is, currentResult = entry -> scrabble word
          For each available character c, convert c to it's index in the alphabet array
              This uses hashing to make this constant time
              find the entry next at the same index in currentEntry.children
              Remove c from availableCharacters
              if next entry is the end of a word
                  check to see if its more points than the currentResult
                  if it is, currentResult = entry -> scrabble word
              recurse using nextEntry, the new availableCharacters, and the current best result
          return the best result
              
   To find a next best word selection from the dictionary for the Scrabble.
   The program mostly uses hash, binary representation (byte), and trie algorithm
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class ScrabblePlayer {
    Trie tree = new Trie();
    // initialize ScrabblePlayer with a file of English words
    public ScrabblePlayer(final String wordFile) {
        final File inputFile = new File(wordFile);
        final Scanner inputData;
        try {
            inputData = new Scanner(inputFile);
            while (inputData.hasNextLine()) {
                final String str = inputData.nextLine();
                if (str.length() > 1  && str.length() < 9) { //We only add words with length 2-8, words outside this range can't be played
                    tree.insert(str.toUpperCase());  //Above comment isn't always true, extending words can be up to 14 char long
                }                                   //But that requires double the memory
            }                                       //And it doesn't increase points by much
        } catch (final FileNotFoundException e) {
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
    /**
     * Finds word on board and passes it along with the availableLetters to the trie to find a word to return
     * @param board
     * @param availableLetters
     * @return
     */
    public ScrabbleWord getScrabbleWord(final char[][] board, final char[] availableLetters) {
        final ScrabbleWord wordOnBoard = findWord(board);
        final ScrabbleWord result = tree.getBestWord(wordOnBoard, availableLetters);
        return result;
    }
    /**
     * Finds the word on the board by searching the board until it finds a character that is not
     * a space. Then it checks the orientation of the word.
     * @param board
     * @return
     */
    public static ScrabbleWord findWord(final char[][] board) {
      for (int row = 0; row < board.length; row ++) {
          for (int col = 0; col < board.length; col++) {
              if (board[row][col] != ' ') {
                  final char orientation;
                  if (col == board.length - 1) { //Word can't be horizontal because no space
                      orientation = 'v';
                  } else if (row == board.length - 1) { //Can't be vertical
                      orientation = 'h';
                  } else if (board[row][col + 1] != ' ') { //If next letter is right
                      orientation = 'h';                   //Then horizontal
                  } else if (board[row + 1][col] != ' '){  //If next letter is down
                      orientation = 'v';                   //It's vertical
                  } else {
                      orientation = 'o'; //Single character words don't have a specific direction
                  }
                  final String word = getWord(orientation, board, row, col);
                  return new ScrabbleWord(word, row, col, orientation);
              }
          }
      }
      return null; //Didn't find the word
    }

    /**
     * Given the starting row/col and orientation of the word, this returns the word's string
     * @param orientation
     * @param board
     * @param row
     * @param col
     * @return
     */
    public static String getWord(final char orientation, final char[][] board, final int row, final int col) {
        if (orientation == 'h') { //If horizontal
            final int maxWordSize = board.length - col;
            String word = "";
            for (int c = 0; c < maxWordSize; c++) { //Check to right until you hit a space
                if (board[row][col + c] == ' ') {
                    break;
                }
                word = word.concat(Character.toString(board[row][col + c]));
            }
            return word;
        } else if (orientation == 'v'){ //If vertical
            final int maxWordSize = board.length - row;
            String word = "";
            for (int r = 0; r < maxWordSize; r++) { //Check down until you hit a space
                if (board[row + r][col] == ' ') {
                    break;
                }
                word = word.concat(Character.toString(board[row + r][col]));
            }
            return word;
        } else { //It's a single character word
            return Character.toString(board[row][col]);
        }
    }
}
