package sk.uniba.fmph.dcs.game_board;

import sk.uniba.fmph.dcs.stone_age.Effect;

import java.util.Arrays;

public final class GetSomethingThrow implements EvaluateCivilizationCardImmediateEffect {
    private final Effect resource;

    public GetSomethingThrow(final Effect resource) {
        this.resource = resource;
    }

    @Override
    public boolean performEffect(final Player player, final Effect choice) {
        if (choice != this.resource) {
            return false;
        }
        CurrentThrow currentThrow = new CurrentThrow(player, choice, 2);
        currentThrow.finishUsingTools();
        return true;
    }
}
