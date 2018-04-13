
public class Trie {
    public static class Entry implements Comparable<Entry> {
        char element;
        boolean isWordEnd = false;
		final int prefixLength;
        MyList<Entry> children = new MyList<Entry>();
        Entry(final char theElement, final Entry theParent) {
            element = theElement;
			parent = theParent;
			if (parent == null) {
				prefixLength = -1;
			} else {
				prefixLength = parent.prefixLength + 1;
			}
        }
		Entry(final char theElement) {
			this(theElement, null);
		}
        @Override
        public int compareTo(Entry other) {
            return Character.compare(element, other.element);
        }
        public void display() {
            System.out.printf("%s:%s%n", element, children);
        }
        public String toString() {
            return String.valueOf(element);
        }
    }
    private final Entry root;
    Trie() {
        root = new Entry(' ');
    }
    public final void insert(final String word) {
        insert(word, 0, root);
    }
    private void insert(final String word, final int index, final Entry current) {
        final MyList<Entry> kids = current.children;
        final char element = word.charAt(index);
        final Entry next = kids.insert(new Entry(element), current);
        if (index < word.length() - 1) {
            insert(word, index + 1, next);
        } else {
            next.isWordEnd = true;
        }

    }
    public void display() {
        display(root, "");
    }
	private static void display(final Entry e, final String s) {
		if (e.isWordEnd) {
			System.out.println(s + e);
		}
		for (Entry c : e.children) {
			display(c, s + e);
		}
		//System.out.println();
	}
	public void addWords(char c, MyList<String> list, int spaceBefore, int spaceAfter, byte[] hand) {
		for (Entry e : root.children) {
			if (e.element == 'c') {
				addWords(e, list, spaceBefore, spaceAfter, hand, c);
			}
		}
	}
	private void addWords(Entry e, MyList<String> list,  int spaceBefore, int spaceAfter, byte[] hand, String s) {
		for (Entry c : e.children) {
			if (c.element == 'c') {
				addWords(c, list, spaceBefore, spaceAfter, hand, c);
			}
		}
	}
}
