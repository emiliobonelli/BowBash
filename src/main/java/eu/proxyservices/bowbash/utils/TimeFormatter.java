package eu.proxyservices.bowbash.utils;

import java.time.Duration;
import java.time.Instant;

public class TimeFormatter {

    public static String formatTime(long timeInSeconds) {
        if (timeInSeconds < 0) {
            return "00:00";
        }
        Duration duration = Duration.ofSeconds(timeInSeconds);
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%02d:%02d", minutes, seconds);
    }

}
