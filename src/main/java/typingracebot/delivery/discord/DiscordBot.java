package typingracebot.delivery.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import typingracebot.application.StatsManager;
import typingracebot.application.RaceManager;

public class DiscordBot {

    public DiscordBot(String token, StatsManager statsManager, RaceManager raceManager) throws Exception {

        JDA jda = JDABuilder.createDefault(token)
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
                        new TypeResultListener(raceManager, statsManager) // Pass statsManager here too
                )
                .build();

        jda.awaitReady();

        // This registers the command with Discord's servers
        jda.updateCommands().addCommands(
                Commands.slash("start_race", "Host a new typing race"),
                Commands.slash("join", "Join the current active race"),
                Commands.slash("begin", "Start the current round"),
                Commands.slash("stats", "View your personal typing statistics")
        ).queue();

        System.out.println("✅ Bot is online and commands are registered.");
    }
}