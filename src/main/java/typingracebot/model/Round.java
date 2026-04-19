package typingracebot.model;

public class Round {

    private final int roundNumber;
    private final String text;
    private final long startMillis;

    public Round(int roundNumber, String text, long startMillis) {
        this.roundNumber = roundNumber;
        this.text = text;
        this.startMillis = startMillis;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public String getText() {
        return text;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public int countCorrectWords(String typed) {
        if (typed == null) {
            return 0;
        }

        String cleanCorrect = text.replaceAll(
                "[^a-zA-Z0-9\\s]", "").toLowerCase();
        String cleanTyped = typed.replaceAll(
                "[^a-zA-Z0-9\\s]", "").toLowerCase();

        String[] correctWords = cleanCorrect.split("\\s+");
        String[] typedWords = cleanTyped.split("\\s+");

        int limit = Math.min(correctWords.length, typedWords.length);
        int count = 0;

        for (int i = 0; i < limit; i++) {
            if (correctWords[i].equals(typedWords[i])) {
                count++;
            }
        }

        return count;
    }
}