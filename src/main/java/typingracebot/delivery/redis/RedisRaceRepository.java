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

    @Override
    public void saveActiveRace(long guildId, Race race) {
        jedis.set("race:" + guildId, gson.toJson(race));
    }

    @Override
    public Race getActiveRace(long guildId) {
        String data = jedis.get("race:" + guildId);
        return data == null ? null : gson.fromJson(data, Race.class);
    }

    @Override
    public void deleteActiveRace(long guildId) {
        jedis.del("race:" + guildId);
    }
}
