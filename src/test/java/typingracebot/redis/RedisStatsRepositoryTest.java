package typingracebot.redis;

import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import typingracebot.delivery.redis.RedisStatsRepository;
import typingracebot.model.PlayerStats;

import static org.mockito.Mockito.*;

class RedisStatsRepositoryTest {

    @Test
    void saveStatsStoresJson() {
        Jedis jedis = mock(Jedis.class);
        RedisStatsRepository repo = new RedisStatsRepository(jedis);

        PlayerStats stats = new PlayerStats(5L);
        stats.addCorrectWords(10);

        repo.saveStats(stats);

        verify(jedis).set(eq("stats:5"), anyString());
    }
}
