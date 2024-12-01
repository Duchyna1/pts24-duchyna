package sk.uniba.fmph.dcs.game_board;

import org.junit.Test;
import sk.uniba.fmph.dcs.player_board.PlayerBoard;
import sk.uniba.fmph.dcs.player_board.PlayerBoardGameBoardFacade;
import sk.uniba.fmph.dcs.stone_age.*;

import java.util.Optional;

import static org.junit.Assert.*;

public class ResourceSourceTest {
    @Test
    public void testInitialization() {
        ResourceSource foodSource = new ResourceSource(Effect.FOOD, 4);
        assertEquals("Hunting grounds", foodSource.state().split("\n")[0]);

        ResourceSource woodSource = new ResourceSource(Effect.WOOD, 4);
        assertEquals("Forest", woodSource.state().split("\n")[0]);

        ResourceSource claySource = new ResourceSource(Effect.CLAY, 4);
        assertEquals("Clay mound", claySource.state().split("\n")[0]);

        ResourceSource stoneSource = new ResourceSource(Effect.STONE, 4);
        assertEquals("Quarry", stoneSource.state().split("\n")[0]);

        ResourceSource goldSource = new ResourceSource(Effect.GOLD, 4);
        assertEquals("River", goldSource.state().split("\n")[0]);
    }

    @Test
    public void testPlaceFigures() {
        ResourceSource source = new ResourceSource(Effect.WOOD, 4);
        PlayerBoardGameBoardFacade mockBoard = createMockPlayerBoard();
        Player player = new Player(new PlayerOrder(1, 1), mockBoard);

        assertTrue(source.placeFigures(player, 2));
        assertEquals(HasAction.NO_ACTION_POSSIBLE, source.tryToPlaceFigures(player, 2));
    }

    @Test
    public void testMakeAction() {
        ResourceSource source = new ResourceSource(Effect.WOOD, 4);
        PlayerBoardGameBoardFacade mockBoard = createMockPlayerBoard();
        Player player = new Player(new PlayerOrder(1, 1), mockBoard);

        source.placeFigures(player, 2);

        Effect[] input = new Effect[0];
        Effect[] output = new Effect[0];

        assertEquals(ActionResult.ACTION_DONE_WAIT_FOR_TOOL_USE, source.makeAction(player, input, output));

        input = new Effect[] {};
        assertEquals(ActionResult.ACTION_DONE, source.makeAction(player, input, output));
    }

    private PlayerBoardGameBoardFacade createMockPlayerBoard() {
        PlayerBoard pb = new PlayerBoard();
        return new PlayerBoardGameBoardFacade(pb) {
            private boolean hasFiguresAvailable = true;

            @Override
            public boolean hasFigures(int count) {
                return hasFiguresAvailable && count > 0;
            }

            @Override
            public boolean takeFigures(int count) {
                if (hasFiguresAvailable && count > 0) {
                    hasFiguresAvailable = false;
                    return true;
                }
                return false;
            }

            @Override
            public void giveEffect(Effect[] stuff) {
            }

            @Override
            public void giveEndOfGameEffect(EndOfGameEffect[] stuff) {
            }

            @Override
            public boolean takeResources(Effect[] stuff) {
                return true;
            }

            @Override
            public void giveFigure() {
                hasFiguresAvailable = true;
            }

            @Override
            public void addPoints(int points) {
            }

            @Override
            public boolean hasSufficientTools(int goal) {
                return true;
            }

            @Override
            public Optional<Integer> useTool(int idx) {
                return Optional.of(1);
            }

            @Override
            public void newTurn() {
                hasFiguresAvailable = true;
            }
        };
    }
}