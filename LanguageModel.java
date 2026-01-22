import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
		In in = new In(fileName);
        
        StringBuilder buff = new StringBuilder();

        while (buff.length() < windowLength && in.hasNextChar()) {
            buff.append(in.readChar());
        }
        if (buff.length() < windowLength) {
            return;
        }

        String window = buff.toString();

        while (in.hasNextChar()) {
            char nextChar = in.readChar();
            List list = CharDataMap.get(window);
            if (list == null) {
                list = new List();
                CharDataMap.put(window, list);
            }
            list.update(nextChar);
            window = window.substring(1) + nextChar;
        }
        for (List list : CharDataMap.values()) {
            calculateProbabilities(list);
        }
	}
    // t1

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	void calculateProbabilities(List probs) {				
		double cp = 0.0;
        int n = 0;
        // Count how many chars in toto
        ListIterator countIter = probs.listIterator(0);
        while (countIter.hasNext()) {
            n += countIter.next().count;
        }
        // Set cp and p:
        ListIterator iterator = probs.listIterator(0);
        while (iterator.hasNext()) {
            CharData currentChar = iterator.next();
            currentChar.p = (double) currentChar.count / n;
            cp += currentChar.p;
            currentChar.cp = cp;
        }
	}

    // Returns a random character from the given probabilities list.
	char getRandomChar(List probs) {
        if (probs == null || probs.getSize() == 0) return ' ';
		double r = randomGenerator.nextDouble();
        // go on iterating using iterator;
        CharData[] arr = probs.toArray();
        for (int i = 0; i < arr.length; i++) {
            if (r < arr[i].cp) {
                return arr[i].chr;
            }
        }
        return arr[arr.length - 1].chr;
	}

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
		if (initialText.length() < windowLength) {
            return initialText;
        }
        String generatedText = initialText.replaceAll("\r", "");
        String window = generatedText.substring(generatedText.length() - windowLength);
        while (generatedText.length() < textLength + initialText.length()) {
            List probs = CharDataMap.get(window);
            if (probs == null) {
                return generatedText;
            }
            char nextChar = getRandomChar(probs);
            //System.out.println((int) nextChar);
            generatedText += nextChar;
            window = generatedText.substring(generatedText.length() - windowLength);
        }
        return generatedText;
	}

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder out = new StringBuilder();
        for (String key : CharDataMap.keySet()) {
            List probs = CharDataMap.get(key);
            out.append(key).append(" : " + probs + "\n");
        }
        return out.toString();
	}

    public static void main(String[] args) {
        int windowLength = Integer.parseInt(args[0]);
        String initialText = args[1];
        int generatedTextLength = Integer.parseInt(args[2]);
        Boolean randomGeneration = args[3].equals("random");
        String fileName = args[4];

        LanguageModel lm;
        if (randomGeneration) {
            lm = new LanguageModel(windowLength);
        } else {
            lm = new LanguageModel(windowLength, 20);
        }
        lm.train(fileName);
        System.out.println(lm.generate(initialText, generatedTextLength));
    }
}
