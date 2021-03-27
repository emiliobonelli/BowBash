package eu.proxyservices.bowbash.game.gamestates.ingame;

public enum GameKit {
    STANDARD(""),
    RESCUE("bb.kit.rescue");

    final String permission;


    GameKit(String permission) {
        this.permission = permission;
    }
}
