import java.io.File;
import java.io.FileNotFoundException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.io.BufferedInputStream;

public class Test {
	private static final Scanner STDIN =
            new Scanner(new BufferedInputStream(System.in));
    public static void main(final String[] args) throws FileNotFoundException {
        final File inputFile = new File(args[0]);
        final Scanner inputData = new Scanner(inputFile);
        Trie tree = new Trie();
        while (inputData.hasNextLine()) {
            final String str = inputData.nextLine();
            if (str.length() > 1  && str.length() < 9) {
                tree.insert(str.toUpperCase());
            }
        }
        tree.display();
        long memory = peakMemoryUsage();
        System.out.println(NumberFormat.getNumberInstance(Locale.US).format(memory));
    }
    private static long peakMemoryUsage() {

    List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
		long total = 0;
		for (MemoryPoolMXBean memoryPoolMXBean : pools) {
			if (memoryPoolMXBean.getType() == MemoryType.HEAP) {
				long peakUsage = memoryPoolMXBean.getPeakUsage().getUsed();
				// System.out.println("Peak used for: " + memoryPoolMXBean.getName() + " is: " + peakUsage);
				total = total + peakUsage;
			}
    }
    return total;
    }
	/*
	public static MyList<String> findAllPossibleWords(final Trie tree, final char[] tilesInHand, final char[] tilesOnBoard,
													  final int spaceBefore, final int spaceAfter, final MyList<String> list) {
		for (char c : tilesOnBoard) {
			tree.addWords(c, list, spaceBefore, spaceAfter, tilesInHand);
		}
		return null;
	}
	 //call this method
	  public static ScrabbleWord findWord(char[][] board) {
		for (int row = 0; row < board.length; row ++) {
			for (int col = 0; col < board.length; col++) {
				if (board[row][col] != ' ') {
					char orientation;
					if (board[row][col + 1] != ' ') {
						orientation = 'h';
					} else if (board[row + 1][col] != ' ') {
						orientation = 'v';
					} else {
						orientation = 'o';

					}
					String word = getWord(orientation, board, row, col);
					return new ScrabbleWord(word, row, col, orientation);
				}
			}
		}
		return null;
	  }

	//this method only supports findWord
	public static String getWord(char orientation, char[][] board, int row, int col) {
		if (orientation == 'h') {
			int wordSize = board.length - col;
			String word = "";
			for (int c = 0; c < wordSize; c++) {
				word = word.concat(Character.toString(board[row][col + c]));
			}
			return word;
		} else if (orientation == 'v'){
			int wordSize = board.length - row;
			String word = "";
			for (int r = 0; r < wordSize; r++) {
				word = word.concat(Character.toString(board[row + r][col]));
			}
			return word;
		} else {
			return Character.toString(board[row][col]);
		}
	}
	*/
}
