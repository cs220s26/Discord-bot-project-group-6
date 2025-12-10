package typingracebot.application;

import typingracebot.delivery.redis.StatsRepository;
import typingracebot.model.PlayerStats;

public class StatsManager {

    private final StatsRepository statsRepo;

    public StatsManager(StatsRepository statsRepo) {
        this.statsRepo = statsRepo;
    }

    public void recordResult(long userId, int correctWords, long ms) {
        PlayerStats stats = statsRepo.getStats(userId);
        if (stats == null) {
            stats = new PlayerStats(userId);
        }

        stats.incrementRaces();
        stats.addCorrectWords(correctWords);
        stats.addMilliseconds(ms);

        statsRepo.saveStats(stats);
    }

    public PlayerStats getStats(long userId) {
        return statsRepo.getStats(userId);
    }
}
