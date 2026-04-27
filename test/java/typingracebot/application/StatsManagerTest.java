/*
 * Read CONTRIBUTING.md before making any changes to this file
 */

package typingracebot.application;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.*;

public class StatsManagerTest {

	@Test
	void getUserStatsReturnsStoredValue() {
		FakeJedis jedis = new FakeJedis();
		jedis.values.put("stats:42", "12.5");
		StatsManager manager = new StatsManager(jedis);

		assertEquals(12.5, manager.getUserStats(42L));
	}

	@Test
	void getUserStatsReturnsZeroWhenValueMissing() {
		StatsManager manager = new StatsManager(new FakeJedis());

		assertEquals(0.0, manager.getUserStats(42L));
	}

	@Test
	void getUserStatsReturnsZeroWhenRedisThrows() {
		FakeJedis jedis = new FakeJedis();
		jedis.throwOnGet = true;
		StatsManager manager = new StatsManager(jedis);

		assertEquals(0.0, manager.getUserStats(42L));
	}

	@Test
	void updateStatsAddsScoreUsingExpectedKey() {
		FakeJedis jedis = new FakeJedis();
		StatsManager manager = new StatsManager(jedis);

		manager.updateStats(42L, 3.75);

		assertEquals("stats:42", jedis.lastIncrKey);
		assertEquals(3.75, jedis.lastIncrAmount);
		assertEquals("3.75", jedis.values.get("stats:42"));
	}

	@Test
	void updateStatsSwallowsRedisException() {
		FakeJedis jedis = new FakeJedis();
		jedis.throwOnIncr = true;
		StatsManager manager = new StatsManager(jedis);

		assertDoesNotThrow(() -> manager.updateStats(42L, 1.5));
	}

	private static class FakeJedis extends Jedis {
		private final Map<String, String> values = new HashMap<>();
		private boolean throwOnGet;
		private boolean throwOnIncr;
		private String lastIncrKey;
		private double lastIncrAmount;

		@Override
		public String get(String key) {
			if (throwOnGet) {
				throw new RuntimeException("get failed");
			}
			return values.get(key);
		}

		@Override
		public double incrByFloat(String key, double increment) {
			if (throwOnIncr) {
				throw new RuntimeException("incr failed");
			}

			lastIncrKey = key;
			lastIncrAmount = increment;

			double current = values.containsKey(key) ? Double.parseDouble(values.get(key)) : 0.0;
			double updated = current + increment;
			values.put(key, String.valueOf(updated));
			return updated;
		}
	}
}
