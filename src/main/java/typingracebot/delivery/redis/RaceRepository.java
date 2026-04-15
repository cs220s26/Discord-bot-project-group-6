package typingracebot.delivery.redis;

import typingracebot.model.Race;

/**
 * This MUST be an interface.
 * If it says 'public class', your RedisRaceRepository will fail to compile.
 */
public interface RaceRepository {

    void saveActiveRace(long guildId, Race race);

    Race getActiveRace(long guildId);

    void deleteActiveRace(long guildId);
}