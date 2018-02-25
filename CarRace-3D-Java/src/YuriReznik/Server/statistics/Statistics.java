package YuriReznik.Server.statistics;

import YuriReznik.Server.statistics.stages.GamblersInfoStage;
import YuriReznik.Server.statistics.stages.RaceInfoStage;
import YuriReznik.Server.statistics.stages.SystemInfoStage;

/**
 * Show statistics about system
 */
public class Statistics {
    private RaceInfoStage raceInfoStage;
    private GamblersInfoStage gamblersInfoStage;
    private SystemInfoStage systemInfoStage;


    public Statistics() {
        raceInfoStage = new RaceInfoStage();
        gamblersInfoStage = new GamblersInfoStage();
        systemInfoStage = new SystemInfoStage();
    }
}
