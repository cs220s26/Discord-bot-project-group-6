package typingracebot.delivery.redis;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import typingracebot.model.Race;

public class RedisRaceRepository implements RaceRepository {

    private final Jedis jedis;
    private final Gson gson = new Gson();

    public RedisRaceRepository(Jedis jedis) {
        this.jedis = jedis;
    }

    private String key(long guildId) {
        return "race:" + guildId;
    }

    @Override
    public Race getActiveRace(long guildId) {
        String data = jedis.get(key(guildId));
        if (data == null) return null;
        return gson.fromJson(data, Race.class);
    }

    @Override
    public void saveActiveRace(long guildId, Race race) {
        jedis.set(key(guildId), gson.toJson(race));
    }

    @Override
    public void deleteActiveRace(long guildId) {
        jedis.del(key(guildId));
    }
}
