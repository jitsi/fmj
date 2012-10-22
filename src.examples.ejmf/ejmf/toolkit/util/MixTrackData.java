package ejmf.toolkit.util;

public class MixTrackData {
    public double startTime;
    public double playingTime;
    public String mediaFileName;

    public MixTrackData(String file, double startTime, double playingTime) {
        this.startTime = startTime;
	this.playingTime = playingTime;
	this.mediaFileName = file;
    }
}
