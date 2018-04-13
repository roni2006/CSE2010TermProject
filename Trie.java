
public class Trie {
    private static class Entry implements Comparable<Entry> {
        char element;
        boolean isWordEnd = false;
        MyList<Entry> children = new MyList<Entry>();
        Entry(final char theElement) {
            element = theElement;
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
        final Entry next = kids.insert(new Entry(element));
        if (index < word.length() - 1) {
            insert(word, index + 1, next);
        } else {
            next.isWordEnd = true;
        }

    }
    public void display() {
        display(root);
    }
    public static void display(final Entry e) {
		display(e, "");
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
}
