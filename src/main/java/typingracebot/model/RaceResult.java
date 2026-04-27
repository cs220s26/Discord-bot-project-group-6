package typingracebot.model;

public class RaceResult {

    private final long userId;
    private final int correctWords;
    private final long timeMs;
    private final double efficiency;

    public RaceResult(long userId, int correctWords, long timeMs) {
        this.userId = userId;
        this.correctWords = correctWords;
        this.timeMs = timeMs;
        this.efficiency = this.correctWords / (this.timeMs / 1000.0);
    }

    public long getUserId() { return userId; }
    public int getCorrectWords() { return correctWords; }
    public long getTimeMs() { return timeMs; }
    public double getEfficiency() { return efficiency; }
}
