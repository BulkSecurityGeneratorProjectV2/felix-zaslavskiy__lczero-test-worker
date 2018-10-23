package lczero.worker.model;

public class Game {
    public long testID; // Worker can lookup Configs by the testID
    public long expirationDate; // How long server will wait for results

    public String openingPGN; // This game has preset opening moves. Optional

}
