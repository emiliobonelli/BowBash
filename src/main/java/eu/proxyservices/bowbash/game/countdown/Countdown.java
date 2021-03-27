package eu.proxyservices.bowbash.game.countdown;

public interface Countdown extends Runnable {
    void start();

    void interrupt();

    boolean isRunning();

    int time();

    boolean fast_start();

    CountdownType getType();

}
