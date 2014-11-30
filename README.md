eTokens
=======

Have you ever wanted to reward you players when they do something such as kill a mob, a player or for earning a hard-to-get achievement?

If so then this is the plugin for you!

eTokens is based a a fairly advanced system that will allow you to o just that!

With this plugin comes a default, easy to configure, XML file that allows you, the server owner, to automatically reward players.

The XML is really easy to grasp. All you need to do is add a "token" tab and tell it what you want to do.

Below are some example and what attribute means what.

```XML
<!-- This token is rewarded when the player breaks 10 diamond_ore blocks, they are given 10 tokens and sent the message -->
<token block-amount="10" block-type="DIAMOND_ORE" objective="block-break" token-amount="10">Well done {PLAYER}, have received {TOKEN-AMOUNT} token(s) for breaking {BLOCK-AMOUNT} {BLOCK-TYPE} block(s)!</token>

<!-- This token is rewarded when the player earns the achievement "BUILD_PICK". They are given 10 tokens -->
<token achievement="BUILD_PICKAXE" objective="achievement" token-amount="10">You have received {TOKEN-AMOUNT} tokens for getting the achievement {ACHIEVEMENT}!</token>

<!-- This token is rewarded when the player kills 10 zombies. This rewards them with 10 tokens. 
This also has the "repeatable" attribute which means that the user can complete this for as many times as they want -->
<token entity-type="ZOMBIE" kills="10" objective="kill" repeatable="true" token-amount="10">You have received {TOKEN-AMOUNT} tokens for killing {KILLS} {ENTITY-TYPE}(s)!</token>
```

| XML Attribute | What it does |
|:-------------:|:------------:|
| | |
| | |
| | |


Commands:
====
The commands that come with this plugin allow you to control how many tokens each player has.

| Command | Description | 
|:-------------:|:------:| 
|/token add <Player> <Amount> | Add a specified amount of tokens to the players' account | 
|/token remove <Player> <Amount> | Remove specified tokens to this players' account | 
|/token set <Player> <Amount> | Set this players token count |
|/tokens give <Player> <Amount> | Give this player some of your tokens |
|/shop create <Name> | Create a shop with the specified name (Case sensitive)|
|/shop add <ShopName> <Price> | Add the currently held item to the specified shop |
|/shop <ShopName> | Open a specified shop (Typing in a wrong shop will show available shops)|


All commands (Even the /shop) need a permission node before the user can execute it.
You can find these permissions and their corresponding commands below

| Permission | Command | 
|:-------------:|:------:| 
| etokens.admin | /token add | 
| etokens.admin | /token remove | 
| etokens.admin | /token set |
| etokens.give | /token give |
| etokens.shop.create | /shop create|
| etokens.shop.create | /shop add|
| etokens.shop.<ShopName> | This will give the player permission to open the specified shop|
