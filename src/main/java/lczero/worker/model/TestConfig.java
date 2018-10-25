package lczero.worker.model;

public class TestConfig {
    public long testID;
    // TODO: change it to Base URL + filename
    public String lc0url; // Where to download binary for test.
    public String baseUrlForTools; // Where to get tools and such.
    public String parameters; // Additional command line to use.
    public String tcControl; // What sort of TC to use "short" or "long"
}
