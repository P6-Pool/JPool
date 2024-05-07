package org.CueCraft.Agent;

import CueZone.AgentConfig;

public class CueCraftConfig extends AgentConfig {
    final String name;
    final double noiseMag;
    final int shotDepth;
    final int numVelocitySamples;
    final int monteCarloDepth;
    final int numMonteCarloSamples;

    public CueCraftConfig(String name, double noiseMag, int shotDepth, int numVelocitySamples, int monteCarloDepth, int numMonteCarloSamples) {
        this.name = name;
        this.noiseMag = noiseMag;
        this.shotDepth = shotDepth;
        this.numVelocitySamples = numVelocitySamples;
        this.monteCarloDepth = monteCarloDepth;
        this.numMonteCarloSamples = numMonteCarloSamples;
    }

    @Override
    public String toString() {
        return name + "-" + shotDepth + "-" + numVelocitySamples + "-" + monteCarloDepth + "-" + numMonteCarloSamples;
    }
}

