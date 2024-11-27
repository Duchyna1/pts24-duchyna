package sk.uniba.fmph.dcs.game_board;

import sk.uniba.fmph.dcs.stone_age.Effect;
import sk.uniba.fmph.dcs.stone_age.InterfaceToolUse;
import sk.uniba.fmph.dcs.stone_age.PlayerOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class CurrentThrow implements InterfaceToolUse {
    private Effect throwsFor;
    private int throwResult;
    private Player player;
    private boolean finished;

    private final int WOOD_CONSTANT = 3;
    private final int CLAY_CONSTANT = 4;
    private final int STONE_CONSTANT = 5;
    private final int GOLD_CONSTANT = 6;

    /**
     * @param player
     *            player
     * @param effect
     *            must be resource
     * @param dices
     *            number of dices
     */
    public CurrentThrow(final Player player, final Effect effect, final int dices) {
        this.player = player;
        this.throwsFor = effect;
        this.throwResult = Arrays.stream(Throw.hod(dices)).sum();
        this.finished = false;
    }

    /**
     * Tries to use player's tool.
     *
     * @param idx
     *            index of player's tool
     *
     * @return {@code true} if successful
     */
    @Override
    public boolean useTool(final int idx) {
        if (this.finished) {
            return false;
        }

        Optional<Integer> res = this.player.playerBoard().useTool(idx);
        if (res.isEmpty()) {
            return false;
        }

        this.throwResult += res.get();
        return true;
    }

    /**
     * @return {@code true} if players has any tools
     */
    @Override
    public boolean canUseTools() {
        return this.player.playerBoard().hasSufficientTools(1);
    }

    /**
     * Adds resources to player.
     *
     * @return {@code false} if already called. Otherwise {@code true}.
     */
    @Override
    public boolean finishUsingTools() {
        if (this.finished) {
            return false;
        }

        List<Effect> toAdd = new ArrayList<>();
        switch (this.throwsFor) {
            case Effect.WOOD:
                for (int i = 0; i < this.throwResult/WOOD_CONSTANT; i++) {
                    toAdd.add(Effect.WOOD);
                }
                break;
            case Effect.CLAY:
                for (int i = 0; i < this.throwResult/CLAY_CONSTANT; i++) {
                    toAdd.add(Effect.CLAY);
                }
                break;
            case Effect.STONE:
                for (int i = 0; i < this.throwResult/STONE_CONSTANT; i++) {
                    toAdd.add(Effect.STONE);
                }
                break;
            case Effect.GOLD:
                for (int i = 0; i < this.throwResult/GOLD_CONSTANT; i++) {
                    toAdd.add(Effect.GOLD);
                }
                break;
            default:
                return false;
        }

        this.player.playerBoard().giveEffect(toAdd.toArray(new Effect[0]));

        this.finished = true;
        return true;
    }
}
