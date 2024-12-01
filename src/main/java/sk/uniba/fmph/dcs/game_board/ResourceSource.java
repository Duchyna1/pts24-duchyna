package sk.uniba.fmph.dcs.game_board;

import sk.uniba.fmph.dcs.stone_age.ActionResult;
import sk.uniba.fmph.dcs.stone_age.Effect;
import sk.uniba.fmph.dcs.stone_age.HasAction;
import sk.uniba.fmph.dcs.stone_age.PlayerOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceSource implements InterfaceFigureLocationInternal {
    private String name;
    private final Effect resource;
    private int maxFigures;
    private int maxFigureColors;
    private ArrayList<PlayerOrder> figures;

    private CurrentThrow currentThrow;
    private Player currentPlayer;

    private static final int MAX_PLAYERS = 4;
    private static final int NUMBER_OF_PLACES = 7;
    private static final int NUMBER_OF_TOOLS = 6;

    public ResourceSource(final Effect resource, final int count) {
        this.figures = new ArrayList<>();

        this.currentThrow = null;
        this.currentPlayer = null;

        this.resource = resource;
        switch (resource) {
        case Effect.FOOD:
            this.name = "Hunting grounds";
            this.maxFigures = Integer.MAX_VALUE;
            this.maxFigureColors = MAX_PLAYERS;
            break;
        case Effect.WOOD:
            this.name = "Forest";
            this.maxFigures = NUMBER_OF_PLACES;
            if (count == MAX_PLAYERS) {
                this.maxFigureColors = count;
            } else {
                this.maxFigureColors = count - 1;
            }
            break;
        case Effect.CLAY:
            this.name = "Clay mound";
            this.maxFigures = NUMBER_OF_PLACES;
            if (count == MAX_PLAYERS) {
                this.maxFigureColors = count;
            } else {
                this.maxFigureColors = count - 1;
            }
            break;
        case Effect.STONE:
            this.name = "Quarry";
            this.maxFigures = NUMBER_OF_PLACES;
            if (count == MAX_PLAYERS) {
                this.maxFigureColors = count;
            } else {
                this.maxFigureColors = count - 1;
            }
            break;
        case Effect.GOLD:
            this.name = "River";
            this.maxFigures = NUMBER_OF_PLACES;
            if (count == MAX_PLAYERS) {
                this.maxFigureColors = count;
            } else {
                this.maxFigureColors = count - 1;
            }
            break;
        default:
            break;
        }
    }

    /**
     * Place player's figures on resource source.
     *
     * @param player
     *            which player
     * @param figureCount
     *            number of figures
     *
     * @return {@code true} if successful
     */
    @Override
    public boolean placeFigures(final Player player, final int figureCount) {
        if (this.figures.contains(player.playerOrder())) {
            return false;
        }

        if (this.maxFigures - this.figures.size() < figureCount) {
            return false;
        }

        List<PlayerOrder> colors = new ArrayList<>();
        for (PlayerOrder order : this.figures) {
            if (!colors.contains(order)) {
                colors.add(order);
            }
        }
        if (colors.size() >= this.maxFigureColors) {
            return false;
        }

        if (!player.playerBoard().hasFigures(figureCount)) {
            return false;
        }

        for (int i = 0; i < figureCount; i++) {
            this.figures.add(player.playerOrder());
        }
        player.playerBoard().takeFigures(figureCount);

        return true;
    }

    /**
     * Tries to place player's figures.
     *
     * @param player
     *            which player
     * @param count
     *            number of figures
     *
     * @return {@code HasAction.AUTOMATIC_ACTION_DONE} if successful. Otherwise, returns
     *         {@code HasAction.NO_ACTION_POSSIBLE}.
     */
    @Override
    public HasAction tryToPlaceFigures(final Player player, final int count) {
        if (this.placeFigures(player, count)) {
            return HasAction.AUTOMATIC_ACTION_DONE;
        }
        return HasAction.NO_ACTION_POSSIBLE;
    }

    /**
     * If waiting for tool use, call this function with empty {@code Effect[] inputResources} to end using tools.
     *
     * @param player
     *            which player
     * @param inputResources
     *            put as many tools as you want to use
     * @param outputResources
     *            must be always empty
     *
     * @return TODO
     */
    @Override
    public ActionResult makeAction(final Player player, final Effect[] inputResources, final Effect[] outputResources) {
        if (outputResources.length != 0) {
            return ActionResult.FAILURE;
        }
        if (this.currentPlayer != null && player != this.currentPlayer) {
            return ActionResult.FAILURE;
        }

        if (this.currentThrow == null && this.currentPlayer == null) {
            int figureCount = 0;
            for (PlayerOrder order : this.figures) {
                if (order == player.playerOrder()) {
                    figureCount++;
                }
            }
            while (this.figures.contains(player.playerOrder())) {
                this.figures.remove(player.playerOrder());
            }
            this.currentThrow = new CurrentThrow(player, this.resource, figureCount);
            this.currentPlayer = player;
            return ActionResult.ACTION_DONE_WAIT_FOR_TOOL_USE;
        }

        if (inputResources.length == 0) {
            this.currentThrow.finishUsingTools();
            this.currentThrow = null;
            this.currentPlayer = null;
            return ActionResult.ACTION_DONE;
        }

        for (Effect effect : inputResources) {
            if (effect != Effect.TOOL) {
                return ActionResult.FAILURE;
            }
        }

        for (Effect effect : inputResources) {
            for (int i = 0; i < NUMBER_OF_TOOLS; i++) {
                if (this.currentThrow.useTool(i)) {
                    break;
                }
            }
        }

        return ActionResult.ACTION_DONE;
    }

    /**
     * This action can't be skipped.
     *
     * @param player
     *            does not matter
     *
     * @return {@code false}
     */
    @Override
    public boolean skipAction(final Player player) {
        return false;
    }

    /**
     * @param player
     *            which player
     *
     * @return {@code HasAction.AUTOMATIC_ACTION_DONE} if player did not yet collect his figures.
     *         {@code HasAction.WAITING_FOR_PLAYER_ACTION} if waiting for tool use. Otherwise
     *         {@code HasAction.NO_ACTION_POSSIBLE}.
     */
    @Override
    public HasAction tryToMakeAction(final Player player) {
        if (this.figures.contains(player.playerOrder()) && this.currentPlayer == null) {
            this.makeAction(player, new Effect[0], new Effect[0]);
            return HasAction.AUTOMATIC_ACTION_DONE;
        }
        if (this.currentPlayer == player && this.currentThrow.canUseTools()) {
            return HasAction.WAITING_FOR_PLAYER_ACTION;
        }

        return HasAction.NO_ACTION_POSSIBLE;
    }

    /**
     * Tries to make new turn.
     *
     * @return {@code false} if new turn not possible. Otherwise {@code true} and makes new turn.
     */
    @Override
    public boolean newTurn() {
        if (!this.figures.isEmpty()) {
            return false;
        }

        this.currentThrow = null;
        return true;
    }

    /**
     * @return state
     */
    @Override
    public String state() {
        Map<PlayerOrder, Integer> map = new HashMap<>();

        for (PlayerOrder order : this.figures) {
            if (map.containsKey(order)) {
                map.put(order, map.get(order) + 1);
            } else {
                map.put(order, 1);
            }
        }

        String state = this.name;
        for (PlayerOrder order : map.keySet()) {
            state += " " + order.toString() + " " + map.get(order);
        }

        return state;
    }
}
