/*
 * Read CONTRIBUTING.md before making any changes to this file
 */

package typingracebot.application;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.Test;
import typingracebot.delivery.redis.RaceRepository;
import typingracebot.model.*;

public class RaceManagerTest {

	@Test
	void startRaceInitializesRaceAndPersistsIt() {
		InMemoryRaceRepository repo = new InMemoryRaceRepository();
		RaceManager manager = new RaceManager(repo, fixedTextProvider(), 3);

		Race race = manager.startRace(100L, 7L);

		assertEquals(7L, race.getHostId());
		assertEquals(3, race.getTotalRounds());
		assertNotNull(repo.getActiveRace(100L));
	}

	@Test
	void joinRaceAddsPlayerToActiveRace() {
		InMemoryRaceRepository repo = new InMemoryRaceRepository();
		RaceManager manager = new RaceManager(repo, fixedTextProvider(), 3);
		manager.startRace(100L, 7L);

		manager.joinRace(100L, 55L);

		assertTrue(manager.getActiveRace(100L).getPlayers().contains(55L));
	}

	@Test
	void joinRaceThrowsWhenThereIsNoActiveRace() {
		InMemoryRaceRepository repo = new InMemoryRaceRepository();
		RaceManager manager = new RaceManager(repo, fixedTextProvider(), 3);

		assertThrows(IllegalStateException.class, () -> manager.joinRace(100L, 55L));
	}

	@Test
	void beginRoundRequiresHostAndUsesProviderText() {
		InMemoryRaceRepository repo = new InMemoryRaceRepository();
		RaceManager manager = new RaceManager(repo, fixedTextProvider(), 3);
		manager.startRace(100L, 7L);

		assertThrows(IllegalStateException.class, () -> manager.beginRound(100L, 8L));

		Round round = manager.beginRound(100L, 7L);
		assertEquals(1, round.getRoundNumber());
		assertEquals("alpha beta", round.getText());
	}

	@Test
	void recordTypingReturnsNullWhenRoundMissingOrDuplicateSubmission() {
		InMemoryRaceRepository repo = new InMemoryRaceRepository();
		RaceManager manager = new RaceManager(repo, fixedTextProvider(), 3);
		manager.startRace(100L, 7L);
		manager.joinRace(100L, 55L);

		assertNull(manager.recordTyping(100L, 55L, "alpha beta"));

		manager.beginRound(100L, 7L);
		RaceResult first = manager.recordTyping(100L, 55L, "alpha beta");
		RaceResult duplicate = manager.recordTyping(100L, 55L, "alpha beta");

		assertNotNull(first);
		assertNull(duplicate);
	}

	@Test
	void roundFinishedAndFinalRoundBehaveAsExpected() {
		InMemoryRaceRepository repo = new InMemoryRaceRepository();
		RaceManager manager = new RaceManager(repo, fixedTextProvider(), 2);
		manager.startRace(100L, 7L);
		manager.joinRace(100L, 55L);
		manager.joinRace(100L, 56L);

		manager.beginRound(100L, 7L);
		manager.recordTyping(100L, 55L, "alpha beta");
		assertTrue(!manager.isRoundFinished(100L));
		manager.recordTyping(100L, 56L, "alpha beta");
		assertTrue(manager.isRoundFinished(100L));
		assertTrue(!manager.isFinalRound(100L));

		manager.beginRound(100L, 7L);
		assertTrue(manager.isFinalRound(100L));
	}

	@Test
	void getFinalScoresAccumulatesAcrossRounds() {
		InMemoryRaceRepository repo = new InMemoryRaceRepository();
		RaceManager manager = new RaceManager(repo, fixedTextProvider(), 2);
		manager.startRace(100L, 7L);
		manager.joinRace(100L, 55L);

		manager.beginRound(100L, 7L);
		manager.recordTyping(100L, 55L, "alpha beta");
		manager.beginRound(100L, 7L);
		manager.recordTyping(100L, 55L, "alpha beta");

		Map<Long, Double> scores = manager.getFinalScores(100L);
		assertTrue(scores.containsKey(55L));
		assertTrue(scores.get(55L) > 0.0);
	}

	@Test
	void clearRaceRemovesStateAndRepositoryEntry() {
		InMemoryRaceRepository repo = new InMemoryRaceRepository();
		RaceManager manager = new RaceManager(repo, fixedTextProvider(), 3);
		manager.startRace(100L, 7L);
		manager.beginRound(100L, 7L);

		manager.clearRace(100L);

		assertNull(manager.getActiveRace(100L));
		assertNull(manager.getActiveRound(100L));
		assertTrue(manager.getFinalScores(100L).isEmpty());
	}

	private TextProvider fixedTextProvider() {
		return new TextProvider(List.of("alpha beta"));
	}

	private static class InMemoryRaceRepository implements RaceRepository {
		private final Map<Long, Race> activeRaces = new HashMap<>();

		@Override
		public void saveActiveRace(long guildId, Race race) {
			activeRaces.put(guildId, race);
		}

		@Override
		public Race getActiveRace(long guildId) {
			return activeRaces.get(guildId);
		}

		@Override
		public void deleteActiveRace(long guildId) {
			activeRaces.remove(guildId);
		}
	}
}
