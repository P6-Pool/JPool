package CueZone;

import org.javatuples.Pair;

import java.util.ArrayList;

public class Benchmarker {
    public static BenchmarkStat RunOffBreakBenchMark(double noiseMag, ArrayList<Agent> agents, int numRunsPerConfig, int maxConcurrentGames) {

        ArrayList<Pair<Agent, ArenaStat>> results = new ArrayList<>();

        for (Agent agent : agents) {
            GameParams params = new GameParams(agent, new CueConcede(), noiseMag, 0, 0);
            ArenaStat stat = Arena.pvpAsync(params, numRunsPerConfig, maxConcurrentGames);
            results.add(new Pair<>(agent, stat));
        }

        return new BenchmarkStat(noiseMag, numRunsPerConfig, results);
    }
}
