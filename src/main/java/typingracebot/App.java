package typingracebot;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import redis.clients.jedis.Jedis;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import typingracebot.application.RaceManager;
import typingracebot.application.StatsManager;
import typingracebot.application.TextProvider;
import typingracebot.delivery.discord.DiscordBot;
import typingracebot.delivery.redis.RaceRepository;
import typingracebot.delivery.redis.RedisRaceRepository;

public class App {
    public static void main(String[] args) {
        // 1. Load the Discord Token from AWS Secrets Manager
        String token = loadDiscordToken();
        if (token == null || token.isEmpty()) {
            System.err.println("❌ ERROR: Could not load DISCORD_TOKEN from AWS Secrets Manager.");
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

    private static String loadDiscordToken() {
        try (SecretsManagerClient client = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .build()) {

            GetSecretValueRequest request = GetSecretValueRequest.builder()
                    .secretId("220_Discord_Token")
                    .build();

            GetSecretValueResponse response = client.getSecretValue(request);
            String secretString = response.secretString();

            // Secrets Manager returns a JSON string like {"DISCORD_TOKEN":"..."}
            JsonObject json = JsonParser.parseString(secretString).getAsJsonObject();
            return json.get("DISCORD_TOKEN").getAsString();

        } catch (Exception e) {
            System.err.println("❌ Failed to fetch secret from AWS Secrets Manager:");
            e.printStackTrace();
            return null;
        }
    }
}
