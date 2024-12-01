package sk.uniba.fmph.dcs.game_phase_controller;

import org.junit.Test;
import sk.uniba.fmph.dcs.stone_age.*;

import java.util.*;

import static org.junit.Assert.*;

class MockClass implements InterfaceFigureLocation, InterfaceFeedTribe, InterfaceToolUse, InterfaceTakeReward {

    LinkedList<HasAction> hasActionExpected;
    LinkedList<ActionResult> actionResultExpected;
    LinkedList<Boolean> booleanExpected;

    MockClass(String hasAction, String actionResult, String bool) {
        hasActionExpected = new LinkedList<>();
        for (char c : hasAction.toCharArray()) {
            switch (c) {
                case 'w':
                    hasActionExpected.addLast(HasAction.WAITING_FOR_PLAYER_ACTION);
                    break;
                case 'n':
                    hasActionExpected.addLast(HasAction.NO_ACTION_POSSIBLE);
                    break;
                case 'a':
                    hasActionExpected.addLast(HasAction.AUTOMATIC_ACTION_DONE);
            }
        }
        actionResultExpected = new LinkedList<>();
        for (char c : actionResult.toCharArray()) {
            switch (c) {
                case 'F':
                    actionResultExpected.addLast(ActionResult.FAILURE);
                    break;
                case 'D':
                    actionResultExpected.addLast(ActionResult.ACTION_DONE);
                    break;
                case 'T':
                    actionResultExpected.addLast(ActionResult.ACTION_DONE_WAIT_FOR_TOOL_USE);
                    break;
                case 'R':
                    actionResultExpected.addLast(ActionResult.ACTION_DONE_ALL_PLAYERS_TAKE_A_REWARD);
            }
        }
        booleanExpected = new LinkedList<>();
        for (char c : bool.toCharArray()) {
            switch (c) {
                case 'y':
                    booleanExpected.addLast(true);
                    break;
                case 'n':
                    booleanExpected.addLast(false);
            }
        }
    }

    private boolean returnBoolean() {
        if (booleanExpected.isEmpty()) {
            System.out.println("empty boolean");
            return false;
        }
        return booleanExpected.removeFirst();
    }

    private HasAction returnHasAction() {
        if (hasActionExpected.isEmpty()) {
            System.out.println("empty hasAction");
            return HasAction.NO_ACTION_POSSIBLE;
        }
        return hasActionExpected.removeFirst();
    }

    private ActionResult returnActionResult() {
        if (actionResultExpected.isEmpty()) {
            System.out.println("empty action result");
            return ActionResult.FAILURE;
        }
        return actionResultExpected.removeFirst();
    }

    @Override
    public boolean feedTribeIfEnoughFood() {
        return returnBoolean();
    }

    @Override
    public boolean feedTribe(Collection<Effect> resources) {
        return returnBoolean();
    }

    @Override
    public boolean doNotFeedThisTurn() {
        return returnBoolean();
    }

    @Override
    public boolean isTribeFed() {
        return returnBoolean();
    }

    @Override
    public boolean placeFigures(PlayerOrder player, int figureCount) {
        return returnBoolean();
    }

    @Override
    public HasAction tryToPlaceFigures(PlayerOrder player, int count) {
        return returnHasAction();
    }

    @Override
    public ActionResult makeAction(PlayerOrder player, Collection<Effect> inputResources, Collection<Effect> outputResources) {
        return returnActionResult();
    }

    @Override
    public boolean skipAction(PlayerOrder player) {
        return returnBoolean();
    }

    @Override
    public HasAction tryToMakeAction(PlayerOrder player) {
        return returnHasAction();
    }

    @Override
    public boolean newTurn() {
        return returnBoolean();
    }

    @Override
    public boolean takeReward(PlayerOrder player, Effect reward) {
        return returnBoolean();
    }

    @Override
    public HasAction tryMakeAction(PlayerOrder player) {
        return returnHasAction();
    }

    @Override
    public boolean useTool(int idx) {
        return returnBoolean();
    }

    @Override
    public boolean canUseTools() {
        return returnBoolean();
    }

    @Override
    public boolean finishUsingTools() {
        return returnBoolean();
    }
}

class MockNewTurn implements InterfaceNewTurn {

    @Override
    public void newTurn() {
        // not relevant, void function in mock class
    }
}

public class IntegrationTest {

    @Test
    public void test() {
        // Initialization
        PlayerOrder p1 = new PlayerOrder(0, 4);
        PlayerOrder p2 = new PlayerOrder(1, 4);
        PlayerOrder p3 = new PlayerOrder(2, 4);
        PlayerOrder p4 = new PlayerOrder(3, 4);

        // expected answers from mock class, ' ' 
        String hasAction = "wwannnnnnnnwnnwwwwannnwnnwnnnnnnnnw aaannnnnnnnnwnnnnnnnnnn";
        String actionResult = "TRDD D";
        String bool = "yyyynyyyynyyyynnynnynnynnynyyyyynn nynnnynnynnynnyyyyyny";

        MockClass everything = new MockClass(hasAction, actionResult, bool);

        Map<PlayerOrder, InterfaceFeedTribe> feedTribeMap = new HashMap<>();
        feedTribeMap.put(p1, everything);
        feedTribeMap.put(p2, everything);
        feedTribeMap.put(p3, everything);
        feedTribeMap.put(p4, everything);

        Map<Location, InterfaceFigureLocation> places = new HashMap<>();
        places.put(Location.BUILDING_TILE1, everything);
        places.put(Location.TOOL_MAKER, everything);

        Map<PlayerOrder, InterfaceToolUse> interfaceToolUseCollection = new HashMap<>();
        interfaceToolUseCollection.put(p1, everything);
        interfaceToolUseCollection.put(p2, everything);
        interfaceToolUseCollection.put(p3, everything);
        interfaceToolUseCollection.put(p4, everything);

        Map<PlayerOrder, InterfaceNewTurn> interfaceNewTurnMap = new HashMap<>();
        InterfaceNewTurn interfaceNewTurn = new MockNewTurn();
        interfaceNewTurnMap.put(p1, interfaceNewTurn);
        interfaceNewTurnMap.put(p2, interfaceNewTurn);
        interfaceNewTurnMap.put(p3, interfaceNewTurn);
        interfaceNewTurnMap.put(p4, interfaceNewTurn);

        // test first round, initialize new round
        GamePhaseController gpc = GamePhaseControllerFactory.createGamePhaseController(everything, feedTribeMap, places, interfaceNewTurnMap, interfaceToolUseCollection, p1);
        assertTrue(gpc.placeFigures(p1, Location.BUILDING_TILE1, 1));
        assertTrue(gpc.placeFigures(p2, Location.TOOL_MAKER, 1));
        assertTrue(gpc.placeFigures(p3, Location.BUILDING_TILE1, 1));
        assertTrue(gpc.makeAction(p1, Location.BUILDING_TILE1, new HashSet<>(), new HashSet<>()));
        assertTrue(gpc.useTools(p1, 5));
        assertTrue(gpc.noMoreToolsThisThrow(p1));
        assertTrue(gpc.makeAction(p2, Location.BUILDING_TILE1, new HashSet<>(), new HashSet<>()));
        assertTrue(gpc.makeAllPlayersTakeARewardChoice(p2, Effect.WOOD));
        assertTrue(gpc.makeAllPlayersTakeARewardChoice(p3, Effect.WOOD));
        assertTrue(gpc.makeAllPlayersTakeARewardChoice(p4, Effect.WOOD));
        assertTrue(gpc.skipAction(p3, Location.BUILDING_TILE1));
        assertTrue(gpc.makeAction(p4, Location.BUILDING_TILE1, new HashSet<>(), new HashSet<>()));
        assertTrue(gpc.feedTribe(p1, new ArrayList<>()));
        assertTrue(gpc.feedTribe(p2, new ArrayList<>()));
        assertTrue(gpc.doNotFeedThisTurn(p3));
        assertTrue(gpc.doNotFeedThisTurn(p3));

        // test second round, game end
        assertFalse(gpc.placeFigures(p1, Location.BUILDING_TILE1, 1));
        assertFalse(gpc.placeFigures(p2, Location.BUILDING_TILE1, 2));
        assertTrue(gpc.placeFigures(p2, Location.BUILDING_TILE1, 0));
        assertFalse(gpc.skipAction(p3, Location.BUILDING_TILE1));
        assertFalse(gpc.skipAction(p2, Location.TOOL_MAKER));
        assertTrue(gpc.makeAction(p2, Location.BUILDING_TILE1, new ArrayList<>(), new ArrayList<>()));
        assertFalse(gpc.feedTribe(p2, new ArrayList<>()));
        assertTrue(gpc.doNotFeedThisTurn(p2));
        assertTrue(gpc.feedTribe(p3, new ArrayList<>()));
        assertTrue(gpc.feedTribe(p4, new ArrayList<>()));
        assertTrue(gpc.feedTribe(p1, new ArrayList<>()));
        assertTrue(gpc.state().contains("GAME_END"));
    }
}
