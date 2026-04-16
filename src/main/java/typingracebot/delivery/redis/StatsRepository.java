package typingracebot.delivery.redis;

import typingracebot.model.PlayerStats;

public interface StatsRepository {

    PlayerStats getStats(long userId);

    void saveStats(PlayerStats stats);

    void saveStatsAfterRound(Long userId, int correctWords, long milliseconds);
}
