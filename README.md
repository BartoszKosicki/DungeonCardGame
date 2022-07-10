
The task was to create a game like Roguelikes.

GENERAL
1. The game is a classic deckbuilder. During the gameplay, the player expands his deck with cards with which he defeats enemies.
2. The player has a choice of two character classes mage and warrior. Depending on the choice, the player has a different starting deck and different statistics. 
3. The goal of the game is to build a strong deck to defeat the final boss, get the key from him and open the door of the secret room.

BOARD
1. The game board is generated automatically. It consists of several rooms containing enemies, or randomly placed items. 
2. The layout of the rooms is random and is generated with each new game.
3. The minimap appears when the "M" key is pressed.
4. Direct combat occurs when a player or opponent enters the same field. 

![ScreenShot](https://raw.github.com/BartoszKosicki/DungeonCardGame/main/game_board.jpg)


FIGHT
1. Combat is turn-based. The player moves first, followed by the opponent. 
2. Each round consists of a card selection, a dice roll and the playing of selected cards.
3. The total of the dice rolls is the points for which cards can be played,
4. There are three types of cards, Common, Rare, Mythic. They differ in powerlevel and cost to play. 
5. Cards have many effects including poison, stun, block spells, deal magic or physical damage, lower resistance, affect the number of cards drawn or heal. 
6. After all cards in the deck have been played, all cards are shuffled and cards can be drawn from the deck again.
7. After winning a battle, the player can choose one card from the three generated cards to add to the deck. 

![ScreenShot](https://raw.github.com/BartoszKosicki/DungeonCardGame/main/game_fight.jpg)

Developers:
* Jakub Bogacki, 
* Bartosz Kosicki, 
* Krzysztof Kowalczyk


Technological stack:
JavaFX
