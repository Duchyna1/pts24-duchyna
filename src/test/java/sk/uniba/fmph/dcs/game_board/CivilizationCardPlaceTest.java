package sk.uniba.fmph.dcs.game_board;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import sk.uniba.fmph.dcs.player_board.PlayerBoard;
import sk.uniba.fmph.dcs.player_board.PlayerBoardGameBoardFacade;
import sk.uniba.fmph.dcs.stone_age.ActionResult;
import sk.uniba.fmph.dcs.stone_age.CivilizationCard;
import sk.uniba.fmph.dcs.stone_age.Effect;
import sk.uniba.fmph.dcs.stone_age.EndOfGameEffect;
import sk.uniba.fmph.dcs.stone_age.HasAction;
import sk.uniba.fmph.dcs.stone_age.ImmediateEffect;
import sk.uniba.fmph.dcs.stone_age.PlayerOrder;

public class CivilizationCardPlaceTest {
    private CivilizationCardDeck deck;
    private CivilizationCardPlace place;
    private Player playerWithBoard;
    private Player playerWithoutBoard;
    private PlayerBoardGameBoardFacade board;

    @Before
    public void setUp() {
        CivilizationCard[] cards = new CivilizationCard[] {
                new CivilizationCard(new ImmediateEffect[] {}, new EndOfGameEffect[] { EndOfGameEffect.BUILDER }) };
        deck = new CivilizationCardDeck(cards);
        place = new CivilizationCardPlace(null, deck, 2);
        board = new PlayerBoardGameBoardFacade(new PlayerBoard());
        playerWithBoard = new Player(new PlayerOrder(1, 2), board);
        playerWithoutBoard = new Player(new PlayerOrder(2, 2), null);
    }

    @Test
    public void testPlaceFigures() {
        assertTrue(place.placeFigures(playerWithBoard, 1));
        assertFalse(place.placeFigures(playerWithBoard, 1));
        assertFalse(place.placeFigures(playerWithBoard, 2));
        assertFalse(place.placeFigures(playerWithoutBoard, 1));
    }

    @Test
    public void testTryToPlaceFigures() {
        assertEquals(HasAction.AUTOMATIC_ACTION_DONE, place.tryToPlaceFigures(playerWithBoard, 1));
        assertEquals(HasAction.NO_ACTION_POSSIBLE, place.tryToPlaceFigures(playerWithBoard, 1));
        assertEquals(HasAction.NO_ACTION_POSSIBLE, place.tryToPlaceFigures(playerWithBoard, 2));
        assertEquals(HasAction.NO_ACTION_POSSIBLE, place.tryToPlaceFigures(playerWithoutBoard, 1));
    }

    @Test
    public void testMakeAction() {
        place.placeFigures(playerWithBoard, 1);

        assertEquals(ActionResult.FAILURE, place.makeAction(playerWithoutBoard, new Effect[] {}, new Effect[] {}));

        assertEquals(ActionResult.FAILURE,
                place.makeAction(playerWithBoard, new Effect[] { Effect.WOOD }, new Effect[] {}));

        assertEquals(ActionResult.FAILURE, place.makeAction(playerWithBoard, new Effect[] { Effect.WOOD, Effect.CLAY },
                new Effect[] { Effect.FOOD }));

        playerWithBoard.playerBoard().giveEffect(new Effect[] { Effect.WOOD, Effect.CLAY });
        assertEquals(ActionResult.ACTION_DONE,
                place.makeAction(playerWithBoard, new Effect[] { Effect.WOOD, Effect.CLAY }, new Effect[] {}));
    }

    @Test
    public void testTryToMakeAction() {
        assertEquals(HasAction.NO_ACTION_POSSIBLE, place.tryToMakeAction(playerWithBoard));

        place.placeFigures(playerWithBoard, 1);
        assertEquals(HasAction.WAITING_FOR_PLAYER_ACTION, place.tryToMakeAction(playerWithBoard));
        assertEquals(HasAction.NO_ACTION_POSSIBLE, place.tryToMakeAction(playerWithoutBoard));
    }

    @Test
    public void testSkipAction() {
        assertFalse(place.skipAction(playerWithBoard));

        place.placeFigures(playerWithBoard, 1);
        assertFalse(place.skipAction(playerWithoutBoard));
        assertTrue(place.skipAction(playerWithBoard));
        assertFalse(place.skipAction(playerWithBoard));
    }

    @Test
    public void testNewTurn() {
        assertTrue(place.newTurn());

        place.placeFigures(playerWithBoard, 1);
        assertFalse(place.newTurn());

        place.skipAction(playerWithBoard);
        assertTrue(place.newTurn());
    }
}