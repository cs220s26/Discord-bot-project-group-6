package typingracebot.delivery.discord;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import typingracebot.application.RaceManager;
import typingracebot.model.RaceResult;
import typingracebot.model.Round;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TypeResultListener extends ListenerAdapter {

    private final RaceManager raceManager;

    public TypeResultListener(RaceManager raceManager) {
        this.raceManager = raceManager;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        // Ignore bot messages
        if (event.getAuthor().isBot()) return;

        // Must be inside a guild
        if (!event.isFromGuild()) return;

        long guildId = event.getGuild().getIdLong();
        long userId = event.getAuthor().getIdLong();
        String typed = event.getMessage().getContentRaw();

        // Get current round
        Round round = raceManager.getActiveRound(guildId);
        if (round == null) return;  // no round active

        // Record score
        RaceResult result = raceManager.recordTyping(guildId, userId, typed);
        if (result == null) return; // duplicate submission

        // -------------------------------------------------------------------
        // 1️⃣ PERSONAL ONE-LINE RESULT
        // -------------------------------------------------------------------

        int correct = result.getCorrectWords();
        long ms = result.getTimeMs();
        double seconds = ms / 1000.0;
        double eff = result.getEfficiency();

        String oneLine =
                "**Round " + round.getRoundNumber() + "** | <@" + userId + "> → "
                        + correct + "w, "
                        + ms + "ms, "
                        + "eff=" + String.format("%.2f", eff);

        event.getChannel().sendMessage(oneLine).queue();


        // -------------------------------------------------------------------
        // 2️⃣ IF NOT ALL PLAYERS FINISHED → STOP HERE
        // -------------------------------------------------------------------
        if (!raceManager.isRoundFinished(guildId)) {
            return;
        }


        // -------------------------------------------------------------------
        // 3️⃣ ROUND SUMMARY FOR ALL PLAYERS
        // -------------------------------------------------------------------

        Map<Long, Double> roundScores = raceManager.getFinalScores(guildId);

        StringBuilder rs = new StringBuilder("🏁 **Round " + round.getRoundNumber() + " Results**\n\n");
        roundScores.entrySet()
                .stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .forEach(e -> {
                    rs.append("<@")
                            .append(e.getKey())
                            .append("> — ")
                            .append(String.format("%.2f", e.getValue()))
                            .append(" efficiency\n");
                });

        event.getChannel().sendMessage(rs.toString()).queue();


        // -------------------------------------------------------------------
        // 4️⃣ CHECK IF FINAL ROUND
        // -------------------------------------------------------------------
        if (raceManager.isFinalRound(guildId)) {

            Map<Long, Double> finalScores = raceManager.getFinalScores(guildId);

            StringBuilder fs = new StringBuilder("🎉 **Final Race Scores** 🎉\n\n");
            finalScores.entrySet()
                    .stream()
                    .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                    .forEach(e -> {
                        fs.append("<@")
                                .append(e.getKey())
                                .append("> — ")
                                .append(String.format("%.2f", e.getValue()))
                                .append(" total efficiency\n");
                    });

            event.getChannel().sendMessage(fs.toString()).queue();

            raceManager.clearRace(guildId);
            return;
        }


        // -------------------------------------------------------------------
        // 5️⃣ AUTOMATIC NEXT ROUND COUNTDOWN (3 → 2 → 1)
        // -------------------------------------------------------------------

        event.getChannel().sendMessage("⏳ Next round begins in **3**...").queue();

        event.getJDA().getGatewayPool().schedule(() ->
                        event.getChannel().sendMessage("⏳ 2...").queue(),
                1, TimeUnit.SECONDS);

        event.getJDA().getGatewayPool().schedule(() ->
                        event.getChannel().sendMessage("⏳ 1...").queue(),
                2, TimeUnit.SECONDS);

        // -------------------------------------------------------------------
        // 6️⃣ START NEXT ROUND
        // -------------------------------------------------------------------

        event.getJDA().getGatewayPool().schedule(() -> {
            long hostId = raceManager.getActiveRace(guildId).getHostId();
            Round next = raceManager.beginRound(guildId, hostId);

            event.getChannel().sendMessage(
                    "🔥 **Round " + next.getRoundNumber() + " — GO!**\n\n"
                            + "Type this paragraph:\n"
                            + next.getText()
            ).queue();

        }, 3, TimeUnit.SECONDS);
    }
}
