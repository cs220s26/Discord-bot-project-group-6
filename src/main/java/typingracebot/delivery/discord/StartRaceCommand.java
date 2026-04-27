package typingracebot.delivery.discord;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import typingracebot.application.RaceManager;
import typingracebot.model.Race;

public class StartRaceCommand extends ListenerAdapter {

    private final RaceManager raceManager;

    public StartRaceCommand(RaceManager raceManager) {
        this.raceManager = raceManager;
    }

    static String buildStartRaceMessage(long userId, int totalRounds) {
        return "🔥🚗 **Typing Race Created!**\n\n"
                + "Host: <@" + userId + ">\n"
                + "Rounds: " + totalRounds + "\n\n"
                + "**How to Play:**\n"
                + "• Players join using `/join`\n"
                + "• Host starts Round 1 using `/begin`\n"
                + "• Type fast & accurately to score points\n"
                + "• Score = correct words / time\n"
                + "• Highest total score after all rounds wins 🏁";
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equals("start_race")) {
            return;
        }
        if (event.getGuild() == null) {
            return;
        }

        long guildId = event.getGuild().getIdLong();
        long userId = event.getUser().getIdLong();

        try {
            Race race = raceManager.startRace(guildId, userId);

            event.reply(buildStartRaceMessage(userId, race.getTotalRounds())).queue();

        } catch (Exception e) {
            event.reply("❌ Error: " + e.getMessage()).queue();
        }
    }
}
