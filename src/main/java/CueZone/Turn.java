package CueZone;

import JFastfiz.*;

public record Turn(
        TurnType turnType,
        Agent player,
        TableState tableStateBefore,
        TableState tableStateAfter,
        GameShot gameShot,
        ShotResult shotResult
) {
}
