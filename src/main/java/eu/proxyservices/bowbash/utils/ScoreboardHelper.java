package eu.proxyservices.bowbash.utils;

import eu.proxyservices.bowbash.BowBash;
import eu.proxyservices.bowbash.game.GameSession;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Objects;

public class ScoreboardHelper {

    private final GameSession gameSession;
    public ScoreboardHelper(GameSession gameSession) {
        this.gameSession = gameSession;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(BowBash.plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getScoreboard().getObjective("bow") == null) {
                    sendScoreboard(player);
                } else {
                    updateScoreboard(player);
                }
            }
        }, 0L, 5L);
    }

    public void sendScoreboard(Player player) {
        Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("bow", Criteria.DUMMY, "§6");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§bBowBash §7- §e[" + gameSession.getGameTeams().size() + "x" + gameSession.getMaxPlayersPerTeam() + "]");

        String[] lines = getLines(gameSession.getGameTeams().size());
        for(int i = 1; i <= lines.length; i++) {
            Team team = scoreboard.registerNewTeam("Line" + i);
            objective.getScore(getPr(i) + " ").setScore(i);
            team.addEntry(getPr(i) + " ");
            team.setPrefix(lines[lines.length - i]);
        }
        player.setScoreboard(scoreboard);
    }

    public void updateScoreboard(Player player) {
        try {
            Scoreboard scoreboard = player.getScoreboard();
            String[] lines = getLines(gameSession.getGameTeams().size());
            for (int i = 1; i <= lines.length; i++) {
                Team team = scoreboard.getTeam("Line" + i);
                assert team != null;
                team.setPrefix(lines[lines.length - i]);
            }
        } catch (NullPointerException ignored) {}
    }

    public String getPr(Integer i) {
        return switch (i) {
            case 2 -> "§2";
            case 3 -> "§3";
            case 4 -> "§4";
            case 5 -> "§5";
            case 6 -> "§6";
            case 7 -> "§7";
            case 8 -> "§8";
            case 9 -> "§9";
            case 10 -> "§a";
            case 11 -> "§b";
            case 12 -> "§c";
            case 13 -> "§d";
            default -> "§1";
        };
    }

    private String[] getLines(int teamSize) {
        if (teamSize == 2) {
            return new String[] {
                    "§6",
                    gameSession.getGameTeams().get(0).getColorCode() + "Team " + gameSession.getGameTeams().get(0).getName() + "§7: " + "§b" + gameSession.getGameTeams().get(0).getPoints() + " Punkte",
                    gameSession.getGameTeams().get(1).getColorCode() + "Team " + gameSession.getGameTeams().get(1).getName() + "§7: " + "§b" + gameSession.getGameTeams().get(1).getPoints() + " Punkte",
                    "§6",
                    "§7Zeit: §b" + TimeFormatter.formatTime(gameSession.getGameTime()),
                    "§8",
                    "§7Map: §b" + gameSession.getMap().getMapName(),
                    "§a",
                    "§7§oVer. " + BowBash.plugin.getDescription().getVersion() + " by " + BowBash.plugin.getDescription().getAuthors().get(0),
            };
        } else if (teamSize == 3) {
            return new String[] {
                    "§6",
                    gameSession.getGameTeams().get(0).getColorCode() + "Team " + gameSession.getGameTeams().get(0).getName() + "§7: " + "§b" + gameSession.getGameTeams().get(0).getPoints() + " Punkte",
                    gameSession.getGameTeams().get(1).getColorCode() + "Team " + gameSession.getGameTeams().get(1).getName() + "§7: " + "§b" + gameSession.getGameTeams().get(1).getPoints() + " Punkte",
                    gameSession.getGameTeams().get(2).getColorCode() + "Team " + gameSession.getGameTeams().get(2).getName() + "§7: " + "§b" + gameSession.getGameTeams().get(2).getPoints() + " Punkte",
                    "§6",
                    "§7Zeit: §b" + TimeFormatter.formatTime(gameSession.getGameTime()),
                    "§8",
                    "§7Map: §b" + gameSession.getMap().getMapName(),
                    "§a",
                    "§7§oVer. " + BowBash.plugin.getDescription().getVersion() + " by " + BowBash.plugin.getDescription().getAuthors().get(0),
            };
        } else if (teamSize == 4) {
            return new String[] {
                    "§6",
                    gameSession.getGameTeams().get(0).getColorCode() + "Team " + gameSession.getGameTeams().get(0).getName() + "§7: " + "§b" + gameSession.getGameTeams().get(0).getPoints() + " Punkte",
                    gameSession.getGameTeams().get(1).getColorCode() + "Team " + gameSession.getGameTeams().get(1).getName() + "§7: " + "§b" + gameSession.getGameTeams().get(1).getPoints() + " Punkte",
                    gameSession.getGameTeams().get(2).getColorCode() + "Team " + gameSession.getGameTeams().get(2).getName() + "§7: " + "§b" + gameSession.getGameTeams().get(2).getPoints() + " Punkte",
                    gameSession.getGameTeams().get(3).getColorCode() + "Team " + gameSession.getGameTeams().get(3).getName() + "§7: " + "§b" + gameSession.getGameTeams().get(3).getPoints() + " Punkte",
                    "§6",
                    "§7Zeit: §b" + TimeFormatter.formatTime(gameSession.getGameTime()),
                    "§8",
                    "§7Map: §b" + gameSession.getMap().getMapName(),
                    "§a",
                    "§7§oVer. " + BowBash.plugin.getDescription().getVersion() + " by " + BowBash.plugin.getDescription().getAuthors().get(0),
            };
        } else {
            return new String[]{};
        }
    }

}
