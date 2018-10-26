package lczero.worker.model;

public class TestConfig {
    public long testID;

    public String baseUrlForLc0;
    public String lc0filename; // Where to download binary for test.
    public String baseUrlForTools; // Where to get tools and such.
    public String network1; // sha1  for network1.
    public String network2;

    public String parameters; // Additional command line to use.
    public String tcControl; // What sort of TC to use "short" or "long"
}
