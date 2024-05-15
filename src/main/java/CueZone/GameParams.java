package CueZone;

public record GameParams(
        Agent player1,
        Agent player2,
        double noiseMag,
        double timePlayer1,
        double timePlayer2,
        boolean breakShotsEnabled
) {
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Game type: Eightball").append("\n");
        str.append("Player 1: ").append(player1.getName()).append(" (t=").append(timePlayer1).append(")").append("\n");
        str.append("Player 2: ").append(player2.getName()).append(" (t=").append(timePlayer2).append(")").append("\n");
        str.append("Noise magnitude: ").append(noiseMag).append("\n");
        str.append("Break shots enabled: ").append(breakShotsEnabled).append("\n");
        return str.toString();
    }
}
