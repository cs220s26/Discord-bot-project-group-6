package typingracebot;

import redis.clients.jedis.Jedis;
import typingracebot.application.RaceManager;
import typingracebot.application.StatsManager;
import typingracebot.application.TextProvider;
import typingracebot.delivery.discord.DiscordBot;
import typingracebot.delivery.redis.RaceRepository;
import typingracebot.delivery.redis.RedisRaceRepository;

public class App {
    public static void main(String[] args) {
        // 1. Load the Discord Token from Environment Variables
        String token = System.getenv("DISCORD_TOKEN");
        if (token == null || token.isEmpty()) {
            System.err.println("❌ ERROR: DISCORD_TOKEN environment variable is not set.");
            System.err.println("Use: export DISCORD_TOKEN=\"your_token_here\"");
            return;
        }

        try {
            // 2. Initialize Redis Connection
            // Ensure redis-server is running on your machine!
            Jedis jedis = new Jedis("localhost", 6379);
            System.out.println("🔄 Connecting to Redis...");
            System.out.println("✅ Redis Status: " + jedis.ping());

            // 3. Initialize Repositories (Data Layer)
            // We use the interface RaceRepository and the implementation RedisRaceRepository
            RaceRepository raceRepo = new RedisRaceRepository(jedis);

            // 4. Initialize Managers (Application Layer)
            TextProvider textProvider = new TextProvider();
            StatsManager statsManager = new StatsManager(jedis);

            // Set the race for 5 rounds by default
            RaceManager raceManager = new RaceManager(raceRepo, textProvider, 5);

            // 5. Start Discord Bot (Delivery Layer)
            System.out.println("🚀 Starting Discord Bot...");
            new DiscordBot(token, statsManager, raceManager);

        } catch (Exception e) {
            System.err.println("❌ Fatal error during startup:");
            e.printStackTrace();
        }
    }
}
