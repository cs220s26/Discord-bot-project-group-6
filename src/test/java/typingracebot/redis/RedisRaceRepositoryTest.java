package typingracebot.redis;

import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import typingracebot.delivery.redis.RedisRaceRepository;
import typingracebot.model.Race;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RedisRaceRepositoryTest {

    @Test
    void saveAndLoadRace() {
        Jedis jedis = mock(Jedis.class);
        RedisRaceRepository repo = new RedisRaceRepository(jedis);

        Race race = new Race("id");
        race.setHostId(10L);

        repo.saveActiveRace(1L, race);
        verify(jedis).set(eq("race:1"), anyString());
    }
}
