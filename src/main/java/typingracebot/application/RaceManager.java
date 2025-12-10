package typingracebot.application;

import typingracebot.delivery.redis.RaceRepository;
import typingracebot.model.Race;
import typingracebot.model.RaceResult;
import typingracebot.model.Round;

import java.util.*;

public class RaceManager {

    private final RaceRepository raceRepo;
    private final TextProvider textProvider;
    private final int totalRounds;

    private final Map<Long, Round> activeRounds = new HashMap<>();
    private final Map<Long, Integer> currentRound = new HashMap<>();
    private final Map<Long, Set<Long>> submitted = new HashMap<>();
    private final Map<Long, Map<Long, Double>> accumulatedScores = new HashMap<>();

    public RaceManager(RaceRepository raceRepo, TextProvider textProvider, int totalRounds) {
        this.raceRepo = raceRepo;
        this.textProvider = textProvider;
        this.totalRounds = totalRounds;
    }

    // ------------------------------------
    // RACE CONTROL
    // ------------------------------------

    public Race startRace(long guildId, long hostId) {
        clearRace(guildId);

        Race race = new Race("race-" + guildId);
        race.setHostId(hostId);
        race.setTotalRounds(totalRounds);
        race.setCurrentRound(0);

        raceRepo.saveActiveRace(guildId, race);

        currentRound.put(guildId, 0);
        submitted.put(guildId, new HashSet<>());
        accumulatedScores.put(guildId, new HashMap<>());

        return race;
    }

    public void joinRace(long guildId, long userId) {
        Race race = raceRepo.getActiveRace(guildId);
        if (race == null) {
            throw new IllegalStateException("No active race.");
        }
        race.addPlayer(userId);
        raceRepo.saveActiveRace(guildId, race);
    }

    public Race getActiveRace(long guildId) {
        return raceRepo.getActiveRace(guildId);
    }

    // ------------------------------------
    // ROUND LOGIC
    // ------------------------------------

    public Round beginRound(long guildId, long requesterId) {
        Race race = raceRepo.getActiveRace(guildId);
        if (race == null) throw new IllegalStateException("No active race.");
        if (race.getHostId() != requesterId)
            throw new IllegalStateException("Only the host can begin.");

        int next = currentRound.getOrDefault(guildId, 0) + 1;
        if (next > totalRounds)
            throw new IllegalStateException("All rounds completed.");

        currentRound.put(guildId, next);
        submitted.get(guildId).clear();

        String text = textProvider.getRandomText();
        long start = System.currentTimeMillis();

        Round round = new Round(next, text, start);
        activeRounds.put(guildId, round);

        race.setCurrentRound(next);
        raceRepo.saveActiveRace(guildId, race);

        return round;
    }

    public Round getActiveRound(long guildId) {
        return activeRounds.get(guildId);
    }

    // ------------------------------------
    // TYPING → SCORING
    // ------------------------------------

    public RaceResult recordTyping(long guildId, long userId, String typed) {
        Round round = activeRounds.get(guildId);
        if (round == null) return null;

        Set<Long> roundSubmitted = submitted.get(guildId);
        if (roundSubmitted.contains(userId))
            return null; // duplicate

        int correct = round.countCorrectWords(typed);
        long now = System.currentTimeMillis();
        long elapsed = Math.max(1, now - round.getStartMillis());

        double seconds = elapsed / 1000.0;
        double efficiency = seconds == 0 ? 0 : correct / seconds;

        RaceResult result = new RaceResult(userId, correct, elapsed);

        accumulatedScores
                .get(guildId)
                .merge(userId, efficiency, Double::sum);

        roundSubmitted.add(userId);

        return result;
    }

    public boolean isRoundFinished(long guildId) {
        Race race = raceRepo.getActiveRace(guildId);
        if (race == null) return false;

        return submitted.get(guildId).containsAll(race.getPlayers());
    }

    public boolean isFinalRound(long guildId) {
        return currentRound.getOrDefault(guildId, 0) >= totalRounds;
    }

    public Map<Long, Double> getFinalScores(long guildId) {
        return accumulatedScores.getOrDefault(guildId, Map.of());
    }

    // ------------------------------------
    // RESET
    // ------------------------------------

    public void clearRace(long guildId) {
        activeRounds.remove(guildId);
        currentRound.remove(guildId);
        submitted.remove(guildId);
        accumulatedScores.remove(guildId);
        raceRepo.deleteActiveRace(guildId);
    }
}
