package typingracebot.delivery.discord;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import typingracebot.application.RaceManager;

public class JoinCommand extends ListenerAdapter {

    private final RaceManager raceManager;

    public JoinCommand(RaceManager raceManager) {
        this.raceManager = raceManager;
    }

    static String buildJoinMessage(long userId) {
        return "🚗 Racer <@" + userId + "> has joined the grid!";
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equals("join")) {
            return;
        }
        if (event.getGuild() == null) {
            return;
        }

        long guildId = event.getGuild().getIdLong();
        long userId = event.getUser().getIdLong();

        try {
            raceManager.joinRace(guildId, userId);
            event.reply(buildJoinMessage(userId)).queue();
        } catch (Exception e) {
            event.reply("❌ " + e.getMessage()).queue();
        }
    }
}
