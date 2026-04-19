package typingracebot.delivery.redis;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import typingracebot.model.PlayerStats;

public class RedisStatsRepository implements StatsRepository {

    private final Jedis jedis;
    private final Gson gson = new Gson();

    public RedisStatsRepository(Jedis jedis) {
        this.jedis = jedis;
    }

    private String key(long userId) {
        return "stats:" + userId;
    }

    @Override
    public PlayerStats getStats(long userId) {
        String data = jedis.get(key(userId));
        if (data == null) {
            return null;
        }
        return gson.fromJson(data, PlayerStats.class);
    }

    @Override
    public void saveStats(PlayerStats stats) {
        jedis.set(key(stats.getUserId()), gson.toJson(stats));
    }

    @Override
    public void saveStatsAfterRound(Long userId, int correctWords, long ms) {
        PlayerStats stats = getStats(userId);
        if (stats == null) {
            stats = new PlayerStats(userId);
        }
        stats.incrementRaces();
        stats.addCorrectWords(correctWords);
        stats.addMilliseconds(ms);
        saveStats(stats);
    }
}