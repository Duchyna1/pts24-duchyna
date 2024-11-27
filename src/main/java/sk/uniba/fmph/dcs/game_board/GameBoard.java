package sk.uniba.fmph.dcs.game_board;

import org.json.JSONObject;
import sk.uniba.fmph.dcs.stone_age.CivilizationCard;
import sk.uniba.fmph.dcs.stone_age.Effect;
import sk.uniba.fmph.dcs.stone_age.InterfaceGetState;
import sk.uniba.fmph.dcs.stone_age.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GameBoard implements InterfaceGetState {
    private final Map<Location, InterfaceFigureLocationInternal> locations;
    private static final int CLAY_IN_CLAY_MOUND = 18;
    private static final int STONE_IN_QUARRY = 12;
    private static final int GOLD_IN_RIVER = 10;
    private static final int WOOD_IN_FOREST = 28;
    private static final int CIVILIZATION_CARDS_PLACES = 4;

    private static final int BUILDING_PILES = 4;
    private CivilizationCard[] civilizationCards;

    public GameBoard(final Collection<Player> players, final Building[] buildings) {
        ToolMakerHutsFields fields = new ToolMakerHutsFields(players.size());
        locations = new HashMap<>();
        locations.put(Location.HUT, new PlaceOnHutAdaptor(fields));
        locations.put(Location.FIELD, new PlaceOnFieldsAdaptor(fields));
        locations.put(Location.TOOL_MAKER, new PlaceOnToolMakerAdaptor(fields));
        locations.put(Location.CLAY_MOUND, new ResourceSource(Effect.CLAY, CLAY_IN_CLAY_MOUND));
        locations.put(Location.FOREST, new ResourceSource(Effect.WOOD, WOOD_IN_FOREST));
        locations.put(Location.QUARRY, new ResourceSource(Effect.STONE, STONE_IN_QUARRY));
        locations.put(Location.RIVER, new ResourceSource(Effect.GOLD, GOLD_IN_RIVER));

        ArrayList<Location> buildingTiles = new ArrayList<>();
        buildingTiles.add(Location.BUILDING_TILE1);
        buildingTiles.add(Location.BUILDING_TILE2);
        buildingTiles.add(Location.BUILDING_TILE3);
        buildingTiles.add(Location.BUILDING_TILE4);
        for (int i = 0; i < BUILDING_PILES; i++) {
            locations.put(buildingTiles.get(i), new BuildingTile(buildings[i]));
        }

        this.generateCards();
        CivilizationCardDeck deck = new CivilizationCardDeck(this.civilizationCards);
        CivilizationCardPlace place4 = new CivilizationCardPlace(null, deck, CIVILIZATION_CARDS_PLACES);
        CivilizationCardPlace place3 = new CivilizationCardPlace(place4, deck, CIVILIZATION_CARDS_PLACES - 1);
        CivilizationCardPlace place2 = new CivilizationCardPlace(place3, deck, CIVILIZATION_CARDS_PLACES - 2);
        CivilizationCardPlace place1 = new CivilizationCardPlace(place2, deck, 1);
        locations.put(Location.CIVILISATION_CARD1, place1);
        locations.put(Location.CIVILISATION_CARD2, place2);
        locations.put(Location.CIVILISATION_CARD3, place3);
        locations.put(Location.CIVILISATION_CARD4, place4);
    }

    private void generateCards() {
        ArrayList<CivilizationCard> cards = new ArrayList<>();
        // TODO add cards
        Collections.shuffle(cards);
        this.civilizationCards = cards.toArray(new CivilizationCard[0]);
    }

    /**
     * @return state combined from everything on the game board
     */
    @Override
    public String state() {
        Map<Location, String> states = new HashMap<>();

        for (var x : locations.keySet()) {
            states.put(x, locations.get(x).state());
        }

        var ret = new JSONObject(states);
        return ret.toString();
    }
}
