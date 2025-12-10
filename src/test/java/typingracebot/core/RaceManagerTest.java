package typingracebot.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import typingracebot.application.RaceManager;
import typingracebot.application.TextProvider;
import typingracebot.delivery.redis.RaceRepository;
import typingracebot.model.Race;
import typingracebot.model.RaceResult;
import typingracebot.model.Round;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RaceManagerTest {

    RaceRepository repo;
    TextProvider provider;
    RaceManager manager;

    @BeforeEach
    void setup() {
        repo = mock(RaceRepository.class);
        provider = new TextProvider(List.of("hello world test"));
        manager = new RaceManager(repo, provider, 5);
    }

    @Test
    void startRaceCreatesNewRace() {
        manager.startRace(1L, 10L);
        verify(repo, times(1)).saveActiveRace(eq(1L), any(Race.class));
    }

    @Test
    void joinRaceThrowsIfNoRace() {
        when(repo.getActiveRace(1L)).thenReturn(null);

        assertThrows(IllegalStateException.class,
                () -> manager.joinRace(1L, 55L));
    }

    @Test
    void beginRoundRequiresHost() {
        Race r = new Race("id");
        r.setHostId(10L);
        r.addPlayer(10L);

        when(repo.getActiveRace(1L)).thenReturn(r);

        Round round = manager.beginRound(1L, 10L);
        assertEquals(1, round.getRoundNumber());
    }

    @Test
    void beginRoundThrowsIfNotHost() {
        Race r = new Race("id");
        r.setHostId(10L);
        r.addPlayer(10L);

        when(repo.getActiveRace(1L)).thenReturn(r);

        assertThrows(IllegalStateException.class,
                () -> manager.beginRound(1L, 999L));
    }

    @Test
    void recordTypingReturnsRaceResult() {
        Race r = new Race("id");
        r.setHostId(1L);
        r.addPlayer(1L);

        when(repo.getActiveRace(1L)).thenReturn(r);

        Round round = manager.beginRound(1L, 1L);
        RaceResult result = manager.recordTyping(1L, 1L, "hello world test");

        assertNotNull(result);
        assertTrue(result.getCorrectWords() >= 1);
    }

    @Test
    void roundFinishesWhenAllPlayersSubmit() {
        Race r = new Race("id");
        r.setHostId(1L);
        r.addPlayer(1L);
        r.addPlayer(2L);

        when(repo.getActiveRace(1L)).thenReturn(r);

        manager.beginRound(1L, 1L);

        manager.recordTyping(1L, 1L, "hello world test");
        manager.recordTyping(1L, 2L, "hello world test");

        assertTrue(manager.isRoundFinished(1L));
    }
}
