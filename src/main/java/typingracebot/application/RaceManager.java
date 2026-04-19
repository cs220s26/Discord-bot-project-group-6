package typingracebot.application;

import typingracebot.delivery.redis.RaceRepository;
import typingracebot.model.Race;
import typingracebot.model.RaceResult;
import typingracebot.model.Round;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RaceManager {
    private final RaceRepository raceRepo;
    private final TextProvider textProvider;
    private final int totalRounds;
    private final Map<Long, Round> activeRounds = new HashMap<>();
    private final Map<Long, Integer> currentRound = new HashMap<>();
    private final Map<Long, Set<Long>> submitted = new HashMap<>();
    private final Map<Long, Map<Long, Double>> accumulatedScores =
            new HashMap<>();

    public RaceManager(RaceRepository raceRepo,
                       TextProvider textProvider, int totalRounds) {
        this.raceRepo = raceRepo;
        this.textProvider = textProvider;
        this.totalRounds = totalRounds;
    }

    public Race startRace(long guildId, long hostId) {
        clearRace(guildId);
        Race race = new Race("race-" + guildId);
        race.setHostId(hostId);
        race.setTotalRounds(totalRounds);
        raceRepo.saveActiveRace(guildId, race);
        currentRound.put(guildId, 0);
        submitted.put(guildId, new HashSet<>());
        accumulatedScores.put(guildId, new HashMap<>());
        return race;
    }

    public void joinRace(long guildId, long userId) {
        Race race = raceRepo.getActiveRace(guildId);
        if (race == null) {
            throw new IllegalStateException("No active race. Use /start_race");
        }
        race.addPlayer(userId);
        raceRepo.saveActiveRace(guildId, race);
    }

    public Round beginRound(long guildId, long userId) {
        Race race = raceRepo.getActiveRace(guildId);
        if (race == null) {
            throw new IllegalStateException("No active race.");
        }
        if (race.getHostId() != userId) {
            throw new IllegalStateException("Only the host can start!");
        }
        int next = currentRound.getOrDefault(guildId, 0) + 1;
        Round round = new Round(next,
                textProvider.getRandomText(),
                System.currentTimeMillis());
        activeRounds.put(guildId, round);
        currentRound.put(guildId, next);
        submitted.get(guildId).clear();
        return round;
    }

    public RaceResult recordTyping(long guildId, long userId, String typed) {
        Round round = activeRounds.get(guildId);
        if (round == null || submitted.get(guildId).contains(userId)) {
            return null;
        }
        RaceResult result = new RaceResult(userId,
                round.countCorrectWords(typed),
                System.currentTimeMillis() - round.getStartMillis());
        accumulatedScores.get(guildId).merge(
                userId, result.getEfficiency(), Double::sum);
        submitted.get(guildId).add(userId);
        return result;
    }

    public boolean isRoundFinished(long guildId) {
        Race race = getActiveRace(guildId);
        Set<Long> participants = submitted.get(guildId);
        return race != null && participants != null
                && participants.containsAll(race.getPlayers());
    }

    public boolean isFinalRound(long guildId) {
        return currentRound.getOrDefault(guildId, 0) >= totalRounds;
    }

    public Map<Long, Double> getFinalScores(long guildId) {
        return accumulatedScores.getOrDefault(guildId, new HashMap<>());
    }

    public Race getActiveRace(long guildId) {
        return raceRepo.getActiveRace(guildId);
    }

    public Round getActiveRound(long guildId) {
        return activeRounds.get(guildId);
    }

    public void clearRace(long guildId) {
        activeRounds.remove(guildId);
        currentRound.remove(guildId);
        submitted.remove(guildId);
        accumulatedScores.remove(guildId);
        raceRepo.deleteActiveRace(guildId);
    }
}