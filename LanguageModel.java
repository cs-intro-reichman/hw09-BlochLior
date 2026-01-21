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
        
        // entire text because this is how the tests work apparently
        String text = in.readAll();

        for (int i = 0; i < text.length() - windowLength; i++) {
            String window = text.substring(i, i + windowLength);
            char nextChar = text.charAt(i + windowLength);
            List probs = CharDataMap.get(window);
            if (probs == null) {
                probs = new List();
                CharDataMap.put(window, probs);
            }
            probs.update(nextChar);
        }
	}

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
		double r = randomGenerator.nextDouble();
        // go on iterating using iterator;
        ListIterator iterator = probs.listIterator(0);
        while (iterator.hasNext()) {
            CharData currentChar = iterator.next();
            if (currentChar.cp > r) {
                return currentChar.chr;
            }
        }
        // shouldn't reach this point in the code, but rounding error failsafe
		return probs.get(probs.getSize() - 1).chr;
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
        String generatedText = initialText;
        String window = generatedText.substring(generatedText.length() - windowLength);
        while (generatedText.length() < textLength) {
            List probs = CharDataMap.get(window);
            if (probs == null) {
                return generatedText;
            }
            char nextChar = getRandomChar(probs);
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
            
            // Print the key
            out.append(key).append(" : ["); // Formatting key start
            
            // Iterate over the list to format items nicely
            for (int i = 0; i < probs.getSize(); i++) {
                CharData cd = probs.get(i);
                out.append("(");
                out.append(cd.chr).append(", ");
                out.append(cd.count).append(", ");
                out.append(cd.p).append(", ");
                out.append(cd.cp);
                out.append(")");
                
                // Add semicolon if not the last item
                if (i < probs.getSize() - 1) {
                    out.append("; ");
                }
            }
            out.append("]\n");
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
