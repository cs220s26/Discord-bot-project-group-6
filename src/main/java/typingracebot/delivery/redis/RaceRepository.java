package typingracebot.delivery.redis;

import typingracebot.model.Race;

public interface RaceRepository {

    Race getActiveRace(long guildId);

    void saveActiveRace(long guildId, Race race);

    void deleteActiveRace(long guildId);
}
