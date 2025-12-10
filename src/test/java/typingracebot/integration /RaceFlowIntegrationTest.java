package typingracebot.integration;

import org.junit.jupiter.api.Test;
import typingracebot.application.RaceManager;
import typingracebot.application.TextProvider;
import typingracebot.delivery.redis.RaceRepository;
import typingracebot.model.RaceResult;
import typingracebot.model.Race;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RaceFlowIntegrationTest {

    @Test
    void fullRaceFlowWorks() {
        RaceRepository repo = mock(RaceRepository.class);

        TextProvider provider = new TextProvider(List.of("hello world"));
        RaceManager manager = new RaceManager(repo, provider, 3);

        // Start race
        Race race = manager.startRace(1L, 10L);
        when(repo.getActiveRace(1L)).thenReturn(race);

        manager.joinRace(1L, 10L);
        manager.joinRace(1L, 20L);

        // Round 1
        manager.beginRound(1L, 10L);
        manager.recordTyping(1L, 10L, "hello world");
        manager.recordTyping(1L, 20L, "hello world");

        assertTrue(manager.isRoundFinished(1L));
    }
}
