package typingracebot.delivery.discord;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import typingracebot.application.StatsManager;
import typingracebot.application.RaceManager;

public class DiscordBot extends ListenerAdapter {

    public DiscordBot(
            String token,
            StatsManager statsManager,
            RaceManager raceManager
    ) throws Exception {

        JDABuilder.createDefault(token)
                .enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.MESSAGE_CONTENT
                )
                .addEventListeners(
                        new StartRaceCommand(raceManager),
                        new JoinCommand(raceManager),
                        new BeginCommand(raceManager),
                        new StatsCommand(statsManager),
                        new TypeResultListener(raceManager) // final listener
                )
                .build();
    }
}
