package typingracebot.model;

public class PlayerStats {

    private final long userId;
    private int totalRaces = 0;
    private int totalCorrectWords = 0;
    private int bestScore = 0;
    private long totalMilliseconds = 0;

    public PlayerStats(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public int getTotalRaces() {
        return totalRaces;
    }

    public int getTotalCorrectWords() {
        return totalCorrectWords;
    }

    public int getBestScore() {
        return bestScore;
    }

    public long getTotalMilliseconds() {
        return totalMilliseconds;
    }

    public void incrementRaces() {
        totalRaces++;
    }

    public void addCorrectWords(int words) {
        totalCorrectWords += words;
        if (words > bestScore) {
            bestScore = words;
        }
    }

    public void addMilliseconds(long ms) {
        totalMilliseconds += ms;
    }
}
