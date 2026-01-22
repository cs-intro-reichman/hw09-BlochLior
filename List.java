/** A linked list of character data objects.
 *  (Actually, a list of Node objects, each holding a reference to a character data object.
 *  However, users of this class are not aware of the Node objects. As far as they are concerned,
 *  the class represents a list of CharData objects. Likwise, the API of the class does not
 *  mention the existence of the Node objects). */
public class List {
    
    // Points to the first node in this list
    private Node first;

    // The number of elements in this list
    private int size;
	
    /** Constructs an empty list. */
    public List() {
        first = null;
        size = 0;
    }
    
    /** Returns the number of elements in this list. */
    public int getSize() {
        return size;
    }

    /** Returns the CharData of the first element in this list. */
    public CharData getFirst() {
        return first.cp;
    }

    /** GIVE Adds a CharData object with the given character to the beginning of this list. */
    public void addFirst(char chr) {
        // New CharData obj:
        CharData addedData = new CharData(chr);
        // Node for new CharData obj:
        Node first = new Node(addedData, this.first);
        // Updating first field for List, and updating size
        first.next = this.first;
        this.first = first;
        size++;
    }
    
    /** GIVE Textual representation of this list. */
    public String toString() {
        String out = "(";
        Node current = first;
        while (current != null) {
            out += current.cp;
            current = current.next;
            if (current != null) {
                out += " ";
            }
        }
        out += ")";
        return out;
    }

    /** Returns the index of the first CharData object in this list
     *  that has the same chr value as the given char,
     *  or -1 if there is no such object in this list. */
    public int indexOf(char chr) {
        Node currentNode = first;
        int index = 0;
        
        while (currentNode != null) {
            if (currentNode.cp.equals(chr)) {
                return index;
            }
            currentNode = currentNode.next;
            index++;
        }
        return -1;
    }

    /** If the given character exists in one of the CharData objects in this list,
     *  increments its counter. Otherwise, adds a new CharData object with the
     *  given chr to the beginning of this list. */
    public void update(char chr) {
        int index = indexOf(chr);
        if (index != -1) {
            Node current = first;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
            current.cp.count++;
        } else {
            // New node needed as none fitting exist yet:
            addFirst(chr);
        }
    }

    /** GIVE If the given character exists in one of the CharData objects
     *  in this list, removes this CharData object from the list and returns
     *  true. Otherwise, returns false. */
    // Case 3: if to be removed is the last one, then the size remains the
        // original size, and the currentNode pointer in the while loop moved until
        // the last index; as this is a single case, i decided to not add a field
        // in the list for the last node and second to last node, as i think it just
        // adds fields that make the code less readable, and as such less-than this
        // version, just for a single case use of removal of last node.
    public boolean remove(char chr) {
        if (first == null) {
            return false;
        }
        if (first.cp.equals(chr)) {
            first = first.next;
            size--;
            return true;
        }
        Node current = first;
        while (current.next != null) {
            if (current.next.cp.equals(chr)) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /** Returns the CharData object at the specified index in this list. 
     *  If the index is negative or is greater than the size of this list, 
     *  throws an IndexOutOfBoundsException. */
    public CharData get(int index) {
        // Sanity check:
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        Node current = first;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.cp;
    }
    
    /** Returns an array of CharData objects, containing all the CharData objects in this list. */
    public CharData[] toArray() {
	    CharData[] arr = new CharData[size];
	    Node current = first;
	    int i = 0;
        while (current != null) {
    	    arr[i++]  = current.cp;
    	    current = current.next;
        }
        return arr;
    }

    /** Returns an iterator over the elements in this list, starting at the given index. */
    public ListIterator listIterator(int index) {
	    // If the list is empty, there is nothing to iterate   
	    if (size == 0) return null;
	    // Gets the element in position index of this list
	    Node current = first;
	    int i = 0;
        while (i < index) {
            current = current.next;
            i++;
        }
        // Returns an iterator that starts in that element
	    return new ListIterator(current);
    }
}