package typingracebot;

import redis.clients.jedis.Jedis;
import typingracebot.application.StatsManager;
import typingracebot.application.TextProvider;
import typingracebot.delivery.discord.DiscordBot;
import typingracebot.delivery.redis.RaceRepository;
import typingracebot.delivery.redis.RedisRaceRepository;
import typingracebot.delivery.redis.RedisStatsRepository;
import typingracebot.delivery.redis.StatsRepository;
import typingracebot.application.RaceManager;


import java.util.Arrays;
import java.util.List;

public class App {

    public static void main(String[] args) throws Exception {

        // ---------- Redis ----------
        Jedis jedis = new Jedis("localhost", 6379);

        RaceRepository raceRepo = new RedisRaceRepository(jedis);
        StatsRepository statsRepo = new RedisStatsRepository(jedis);

        // ---------- Texts for rounds ----------
        List<String> texts = Arrays.asList(
                "Typing races are fun and improve your speed!",
                "Creativity often appears when pressure fades and curiosity takes the lead.",
                "Technology changes quickly, but communication changes even faster.",
                "Success is rarely a straight line from effort to reward.",
                "Practice typing daily to build accuracy and confidence."
        );

        TextProvider textProvider = new TextProvider(texts);

        // ---------- Core managers ----------
        int totalRounds = 5;
        RaceManager raceManager = new RaceManager(raceRepo, textProvider, totalRounds);
        StatsManager statsManager = new StatsManager(statsRepo);

        // ---------- Discord bot ----------
        String token = System.getenv("DISCORD_TOKEN");
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("DISCORD_TOKEN environment variable is not set");
        }

        new DiscordBot(token, statsManager, raceManager);
    }
}
