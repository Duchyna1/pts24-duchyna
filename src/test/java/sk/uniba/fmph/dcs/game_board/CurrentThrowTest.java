package sk.uniba.fmph.dcs.game_board;

import org.junit.Test;
import sk.uniba.fmph.dcs.stone_age.Effect;
import sk.uniba.fmph.dcs.stone_age.PlayerOrder;
import sk.uniba.fmph.dcs.player_board.*;
import static org.junit.Assert.*;

public class CurrentThrowTest {

    private PlayerBoardGameBoardFacade createMockPlayerBoard() {
        PlayerCivilizationCards pcc = new PlayerCivilizationCards();
        PlayerFigures pf = new PlayerFigures();
        PlayerResourcesAndFood prf = new PlayerResourcesAndFood(0);
        PlayerTools pt = new PlayerTools();
        pt.addTool();
        TribeFedStatus tfs = new TribeFedStatus(prf, pf);
        PlayerBoard pb = new PlayerBoard(pcc, pf, prf, pt, tfs);
        PlayerBoardGameBoardFacade pbgb = new PlayerBoardGameBoardFacade(pb);
        return pbgb;
    }

    @Test
    public void test_woodResourceCalculation() {
        PlayerBoardGameBoardFacade board = createMockPlayerBoard();
        Player player = new Player(new PlayerOrder(1, 1), board);

        String initialState = board.getPlayerResourcesAndFood().state();
        int initialWood = countResource(initialState, Effect.WOOD);

        CurrentThrow ct = new CurrentThrow(player, Effect.WOOD, 2);
        ct.useTool(0);
        ct.finishUsingTools();

        String finalState = board.getPlayerResourcesAndFood().state();
        int finalWood = countResource(finalState, Effect.WOOD);

        assertEquals(ct.getResult(), finalWood - initialWood);
    }

    @Test
    public void test_clayResourceCalculation() {
        PlayerBoardGameBoardFacade board = createMockPlayerBoard();
        Player player = new Player(new PlayerOrder(1, 1), board);

        String initialState = board.getPlayerResourcesAndFood().state();
        int initialClay = countResource(initialState, Effect.CLAY);

        CurrentThrow ct = new CurrentThrow(player, Effect.CLAY, 2);
        ct.useTool(0);
        ct.finishUsingTools();

        String finalState = board.getPlayerResourcesAndFood().state();
        int finalClay = countResource(finalState, Effect.CLAY);

        assertEquals(ct.getResult(), finalClay - initialClay);
    }

    private int countResource(String state, Effect resource) {
        for (String line : state.split("\n")) {
            if (line.startsWith(resource.toString())) {
                return Integer.parseInt(line.split(": ")[1]);
            }
        }
        return 0;
    }

    @Test
    public void test_stoneResourceCalculation() {
        PlayerBoardGameBoardFacade board = createMockPlayerBoard();
        Player player = new Player(new PlayerOrder(1, 1), board);

        String initialState = board.getPlayerResourcesAndFood().state();
        int initialStone = countResource(initialState, Effect.STONE);

        CurrentThrow ct = new CurrentThrow(player, Effect.STONE, 2);
        ct.useTool(0);
        ct.finishUsingTools();

        String finalState = board.getPlayerResourcesAndFood().state();
        int finalStone = countResource(finalState, Effect.STONE);

        assertEquals(ct.getResult(), finalStone - initialStone);
    }

    @Test
    public void test_goldResourceCalculation() {
        PlayerBoardGameBoardFacade board = createMockPlayerBoard();
        Player player = new Player(new PlayerOrder(1, 1), board);

        String initialState = board.getPlayerResourcesAndFood().state();
        int initialGold = countResource(initialState, Effect.GOLD);

        CurrentThrow ct = new CurrentThrow(player, Effect.GOLD, 2);
        ct.useTool(0);
        ct.finishUsingTools();

        String finalState = board.getPlayerResourcesAndFood().state();
        int finalGold = countResource(finalState, Effect.GOLD);

        assertEquals(ct.getResult(), finalGold - initialGold);
    }
}