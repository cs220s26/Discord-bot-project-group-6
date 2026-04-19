package typingracebot.delivery.discord;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import typingracebot.application.RaceManager;
import typingracebot.model.Round;

public class BeginCommand extends ListenerAdapter {

    private final RaceManager raceManager;

    public BeginCommand(RaceManager raceManager) {
        this.raceManager = raceManager;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("begin")) {
            return;
        }
        if (event.getGuild() == null) {
            return;
        }

        long guildId = event.getGuild().getIdLong();
        long userId = event.getUser().getIdLong();

        try {
            Round round = raceManager.beginRound(guildId, userId);
            event.reply("🔥 **Round " + round.getRoundNumber()
                    + " — GO!**\n\n"
                    + "Type this paragraph:\n"
                    + round.getText()).queue();
        } catch (Exception e) {
            event.reply("❌ " + e.getMessage()).queue();
        }
    }
}
