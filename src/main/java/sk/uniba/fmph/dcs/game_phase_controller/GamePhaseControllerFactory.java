package sk.uniba.fmph.dcs.game_phase_controller;

import sk.uniba.fmph.dcs.stone_age.InterfaceFeedTribe;
import sk.uniba.fmph.dcs.stone_age.InterfaceFigureLocation;
import sk.uniba.fmph.dcs.stone_age.InterfaceNewTurn;
import sk.uniba.fmph.dcs.stone_age.InterfaceTakeReward;
import sk.uniba.fmph.dcs.stone_age.InterfaceToolUse;
import sk.uniba.fmph.dcs.stone_age.Location;
import sk.uniba.fmph.dcs.stone_age.PlayerOrder;

import java.util.HashMap;
import java.util.Map;

public final class GamePhaseControllerFactory {
    private GamePhaseControllerFactory() {
        throw new UnsupportedOperationException();
    }

    public static GamePhaseController createGamePhaseController(final InterfaceTakeReward interfaceTakeReward,
                                                                final Map<PlayerOrder, InterfaceFeedTribe> interfaceFeedTribeCollection,
                                                                final Map<Location, InterfaceFigureLocation> places, final Map<PlayerOrder, InterfaceNewTurn> playerPlayerBordMap,
                                                                final Map<PlayerOrder, InterfaceToolUse> interfaceToolUseCollection, final PlayerOrder startingPlayer) {
        InterfaceGamePhaseState allPlayersTakeARewardState = new AllPlayersTakeARewardState(interfaceTakeReward);
        InterfaceGamePhaseState feedTribeState = new FeedTribeState(interfaceFeedTribeCollection);
        InterfaceGamePhaseState gameEndState = new GameEndState();
        InterfaceGamePhaseState makeActionState = new MakeActionState(places);
        InterfaceGamePhaseState newRoundState = new NewRoundState(
                places.values().toArray(new InterfaceFigureLocation[0]), playerPlayerBordMap);
        InterfaceGamePhaseState placeFigureState = new PlaceFigureState(places);
        InterfaceGamePhaseState waitingForToolUse = new WaitingForToolUse(interfaceToolUseCollection);
        Map<GamePhase, InterfaceGamePhaseState> dispatchers = new HashMap<>();
        dispatchers.put(GamePhase.GAME_END, gameEndState);
        dispatchers.put(GamePhase.FEED_TRIBE, feedTribeState);
        dispatchers.put(GamePhase.PLACE_FIGURES, placeFigureState);
        dispatchers.put(GamePhase.NEW_ROUND, newRoundState);
        dispatchers.put(GamePhase.MAKE_ACTION, makeActionState);
        dispatchers.put(GamePhase.ALL_PLAYERS_TAKE_A_REWARD, allPlayersTakeARewardState);
        dispatchers.put(GamePhase.WAITING_FOR_TOOL_USE, waitingForToolUse);
        return new GamePhaseController(dispatchers, startingPlayer);
    }
}
