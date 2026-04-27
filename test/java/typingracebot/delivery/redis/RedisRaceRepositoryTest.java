/*
 * Read CONTRIBUTING.md before making any changes to this file
 */

package typingracebot.delivery.redis;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import typingracebot.model.Race;

public class RedisRaceRepositoryTest {

	@Test
	void getActiveRaceReturnsNullWhenMissing() {
		RedisRaceRepository repository = new RedisRaceRepository(new FakeJedis());

		Race loaded = repository.getActiveRace(42L);

		assertNull(loaded);
	}

	@Test
	void saveActiveRaceAndGetActiveRaceRoundTrip() {
		RedisRaceRepository repository = new RedisRaceRepository(new FakeJedis());
		Race race = new Race("race-100");
		race.setHostId(42L);
		race.setCurrentRound(2);
		race.setTotalRounds(5);
		race.addPlayer(42L);
		race.addPlayer(22L);

		repository.saveActiveRace(100L, race);
		Race loaded = repository.getActiveRace(100L);

		assertNotNull(loaded);
		assertEquals("race-100", loaded.getRaceId());
		assertEquals(42L, loaded.getHostId());
		assertEquals(2, loaded.getCurrentRound());
		assertEquals(5, loaded.getTotalRounds());
		assertTrue(loaded.getPlayers().contains(42L));
		assertTrue(loaded.getPlayers().contains(22L));
	}

	@Test
	void deleteActiveRaceRemovesStoredRace() {
		RedisRaceRepository repository = new RedisRaceRepository(new FakeJedis());
		Race race = new Race("race-321");

		repository.saveActiveRace(321L, race);
		repository.deleteActiveRace(321L);

		assertNull(repository.getActiveRace(321L));
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

		@Override
		public long del(String key) {
			return data.remove(key) == null ? 0L : 1L;
		}
	}
}

