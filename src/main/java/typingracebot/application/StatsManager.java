package typingracebot.application;

import redis.clients.jedis.Jedis;

public class StatsManager {
    private final Jedis jedis;

    public StatsManager(Jedis jedis) {
        this.jedis = jedis;
    }

    public double getUserStats(long userId) {
        try {
            String stats = jedis.get("stats:" + userId);
            return stats == null ? 0.0 : Double.parseDouble(stats);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public void updateStats(long userId, double score) {
        try {
            jedis.incrByFloat("stats:" + userId, score);
        } catch (Exception e) {
            System.err.println("Failed to update stats for " + userId);
        }
    }
}
