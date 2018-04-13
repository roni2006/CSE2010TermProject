/*

  Authors (group members):
  Email addresses of group members:
  Group name:

  Course:
  Section:

  Description of the overall algorithm and key data structures:


*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class ScrabblePlayer
{
    //Trie tree = new Trie();
    // initialize ScrabblePlayer with a file of English words
    public ScrabblePlayer(String wordFile)
    
    {
        final File inputFile = new File(wordFile);
        Scanner inputData;
        try {
            inputData = new Scanner(inputFile);
            while (inputData.hasNextLine()) {
                final String str = inputData.nextLine();
                tree.insert(str);
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

    public ScrabbleWord getScrabbleWord(char[][] board, char[] availableLetters)
    {





        return  new ScrabbleWord("MYWORD", 0, 0, 'h');
    }

}
