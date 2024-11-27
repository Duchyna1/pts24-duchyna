package sk.uniba.fmph.dcs.game_board;

import sk.uniba.fmph.dcs.stone_age.CivilizationCard;
import sk.uniba.fmph.dcs.stone_age.PlayerOrder;
import sk.uniba.fmph.dcs.stone_age.Effect;
import sk.uniba.fmph.dcs.stone_age.ActionResult;
import sk.uniba.fmph.dcs.stone_age.HasAction;

import java.util.ArrayList;
import java.util.Optional;

public class CivilizationCardPlace implements InterfaceFigureLocationInternal {
    private final int requiredResources;
    private Optional<CivilizationCard> card;
    private final ArrayList<PlayerOrder> figures;
    private final CivilizationCardDeck deck;
    private final CivilizationCardPlace nextPlace;

    public CivilizationCardPlace(final CivilizationCardPlace nextPlace, final CivilizationCardDeck deck,
            final int requiredResources) {
        this.requiredResources = requiredResources;
        this.nextPlace = nextPlace;
        this.deck = deck;
        this.card = this.next();
        this.figures = new ArrayList<>();
    }

    /**
     * Put next card to this card place.
     *
     * @return new civilization card in this place
     */
    public Optional<CivilizationCard> next() {
        if (this.card.isPresent()) {
            return this.card;
        }

        if (this.nextPlace == null) {
            this.card = this.deck.getTop();
        } else {
            this.card = this.nextPlace.next();
        }
        return this.card;
    }

    /**
     * Places one player figure on civilisation card place.
     *
     * @param player
     *            which player
     * @param figureCount
     *            must be one
     *
     * @return {@code true} if successfully placed
     */
    @Override
    public boolean placeFigures(final Player player, final int figureCount) {
        if (figureCount != 1) {
            return false;
        }

        if (!player.playerBoard().hasFigures(figureCount)) {
            return false;
        }

        if (this.figures.size() > 1) {
            return false;
        }

        player.playerBoard().takeFigures(1);
        this.figures.add(player.playerOrder());
        return true;
    }

    /**
     * Tries to place figures on civilisation card place.
     *
     * @param player
     *            which player
     * @param count
     *            must be 1
     *
     * @return {@code HasAction.AUTOMATIC_ACTION_DONE} if placed successfully, otherwise
     *         {@code HasAction.NO_ACTION_POSSIBLE}
     */
    @Override
    public HasAction tryToPlaceFigures(final Player player, final int count) {
        if (this.placeFigures(player, count)) {
            return HasAction.AUTOMATIC_ACTION_DONE;
        }

        return HasAction.NO_ACTION_POSSIBLE;
    }

    /**
     * Resolve action for given player.
     *
     * @param player
     *            which player
     * @param inputResources
     *            resources used to pay for the civilisation card
     * @param outputResources
     *            should be empty
     *
     * @return {@code ActionResult.ACTION_DONE} is action was resolved successfully. Otherwise
     *         {@code ActionResult.FAILURE}.
     */
    @Override
    public ActionResult makeAction(final Player player, final Effect[] inputResources, final Effect[] outputResources) {
        if (!this.figures.contains(player.playerOrder())) {
            return ActionResult.FAILURE;
        }

        if (inputResources.length != this.requiredResources) {
            return ActionResult.FAILURE;
        }

        if (outputResources.length != 0) {
            return ActionResult.FAILURE;
        }

        int resourceCount = 0;
        for (Effect effect : inputResources) {
            if (effect.isResource()) {
                resourceCount++;
            }
        }
        if (resourceCount != this.requiredResources) {
            return ActionResult.FAILURE;
        }

        player.playerBoard().takeResources(inputResources);
        this.figures.remove(player.playerOrder());
        return ActionResult.ACTION_DONE;
    }

    /**
     * Tries to skip action.
     *
     * @param player
     *            which player
     *
     * @return {@code true} if successfully skipped.
     */
    @Override
    public boolean skipAction(final Player player) {
        if (!this.figures.contains(player.playerOrder())) {
            return false;
        }

        this.figures.remove(player.playerOrder());
        return true;
    }

    /**
     * @param player
     *            which player
     *
     * @return {@code HasAction.NO_ACTION_POSSIBLE} if player does not have figure on this civilization card place.
     *         Otherwise {@code HasAction.WAITING_FOR_PLAYER_ACTION}. Player must decide if he wants to pay or skip.
     */
    @Override
    public HasAction tryToMakeAction(final Player player) {
        if (!this.figures.contains(player.playerOrder())) {
            return HasAction.NO_ACTION_POSSIBLE;
        }
        return HasAction.WAITING_FOR_PLAYER_ACTION;
    }

    /**
     * Tries to make new turn and place new civilization card on this civilization card place.
     *
     * @return {@code true} if new turn was successfully.
     */
    @Override
    public boolean newTurn() {
        if (!this.figures.isEmpty()) {
            return false;
        }

        this.card = this.next();
        return true;
    }

    /**
     * @return state
     */
    @Override
    public String state() {
        return "Card: " + ((this.card.isEmpty()) ? "None" : "Present") + " Cost: " + this.requiredResources
                + " Figures: " + this.figures.size();
    }
}
