package sk.uniba.fmph.dcs.game_board;

import sk.uniba.fmph.dcs.stone_age.CivilizationCard;

import java.util.Optional;
import java.util.Stack;

public final class CivilizationCardDeck {
    private final Stack<CivilizationCard> deck;

    public CivilizationCardDeck(final CivilizationCard[] cards) {
        this.deck = new Stack<>();
        for (CivilizationCard card : cards) {
            this.deck.push(card);
        }
    }

    public Optional<CivilizationCard> getTop() {
        if (this.deck.empty()) {
            return Optional.empty();
        }

        return Optional.of(this.deck.pop());
    }

    public String state() {
        return "Deck size: " + this.deck.size();
    }
}
