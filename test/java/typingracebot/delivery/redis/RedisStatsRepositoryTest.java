/*
 * Read CONTRIBUTING.md before making any changes to this file
 */

package typingracebot.delivery.redis;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import typingracebot.model.PlayerStats;

public class RedisStatsRepositoryTest {

	@Test
	void getStatsReturnsNullWhenNoData() {
		RedisStatsRepository repository = new RedisStatsRepository(new FakeJedis());

		PlayerStats stats = repository.getStats(123L);

		assertNull(stats);
	}

	@Test
	void saveStatsPersistsAndGetStatsRestoresPlayerStats() {
		RedisStatsRepository repository = new RedisStatsRepository(new FakeJedis());
		PlayerStats stats = new PlayerStats(42L);
		stats.incrementRaces();
		stats.addCorrectWords(9);
		stats.addMilliseconds(1800L);

		repository.saveStats(stats);
		PlayerStats loaded = repository.getStats(42L);

		assertNotNull(loaded);
		assertEquals(42L, loaded.getUserId());
		assertEquals(1, loaded.getTotalRaces());
		assertEquals(9, loaded.getTotalCorrectWords());
		assertEquals(9, loaded.getBestScore());
		assertEquals(1800L, loaded.getTotalMilliseconds());
	}

	@Test
	void saveStatsAfterRoundCreatesStatsWhenMissing() {
		RedisStatsRepository repository = new RedisStatsRepository(new FakeJedis());

		repository.saveStatsAfterRound(7L, 5, 2000L);
		PlayerStats loaded = repository.getStats(7L);

		assertNotNull(loaded);
		assertEquals(1, loaded.getTotalRaces());
		assertEquals(5, loaded.getTotalCorrectWords());
		assertEquals(5, loaded.getBestScore());
		assertEquals(2000L, loaded.getTotalMilliseconds());
	}

	@Test
	void saveStatsAfterRoundAccumulatesAcrossMultipleRounds() {
		RedisStatsRepository repository = new RedisStatsRepository(new FakeJedis());

		repository.saveStatsAfterRound(7L, 5, 2000L);
		repository.saveStatsAfterRound(7L, 3, 1000L);
		repository.saveStatsAfterRound(7L, 8, 1500L);
		PlayerStats loaded = repository.getStats(7L);

		assertNotNull(loaded);
		assertEquals(3, loaded.getTotalRaces());
		assertEquals(16, loaded.getTotalCorrectWords());
		assertEquals(8, loaded.getBestScore());
		assertEquals(4500L, loaded.getTotalMilliseconds());
	}

	private static class FakeJedis extends Jedis {
		private final Map<String, String> data = new HashMap<>();

		@Override
		public String get(String key) {
			return data.get(key);
		}

		@Override
		public String set(String key, String value) {
			data.put(key, value);
			return "OK";
		}
	}
}
