# DwarfScrollerGDX

A first test of a simple game using [libGDX](https://libgdx.badlogicgames.com/). 

The game can be played using a keyboard and a mouse or a game pad (it's optimized for game pad use).

The project uses a data-driven approach, to make it configurable and reusable for other projects. How the parts of the game can be (re-)used is described in the following sections.

## Overview

- [Structure of the game and packages](#structure-of-the-game-and-packages)
- [Globally used classes](#globally-used-classes)
- [Inputs](#inputs)
- [Enemies](#enemies)
- [Attacks](#attacks)
- [Events](#events)
- [Items](#items)
- [Game Objects](#game-objects)
- [Maps](#maps)
- [Others](#others)

## Structure of the game and packages

The structure of the libGDX project is documented by [libGDX](https://libgdx.badlogicgames.com/documentation/gettingstarted/Creating%20Projects.html). The mayority of the classes is to be found in the `core` project.

The main class of the project is the class [DwarfScrollerGame](core/src/net/jfabricationgames/gdx/DwarfScrollerGame.java), which is instantiated by the desctop project (see libGDX documentation for details). This class contains some config paths, initializes [global classes](#Globally-used-classes) and sets the screen, so the execution is directed to the active screen (the first one will be the main menu).

The screens, that handle the whole game (when the screen is active), are placed in the `screens` package. Here the [GameScreen](core/src/net/jfabricationgames/gdx/screens/GameScreen.java) class is the central class, that is called from the libGDX engine, on every render step.

The playable character and it's movement is to be found in the `character` package. The [Dwarf](core/src/net/jfabricationgames/gdx/character/Dwarf.java) is the character, that is controlled by the player. The inputs, the player maked are handled in the [CharacterInputMovementHandler](core/src/net/jfabricationgames/gdx/character/CharacterInputMovementHandler.java), which uses the configurable input (explained in the [inputs section](#inputs)).

**Note:** All configuration json files might contain incorrect json syntax, because libGDX allows changes to the usuall json syntax, like adding comments to the files (see the [libGDX wiki documentation](https://github.com/libgdx/libgdx/wiki/Reading-and-writing-JSON) for more information)

## Globally used classes

#### AssetGroupManager

The class [AssetGroupManager](core/src/net/jfabricationgames/gdx/assets/AssetGroupManager.java) is a singleton that can be configured and is a central point to keep all assets of a game. The assets are divided into groups, because not all assets will be used in every level. An example of the configuration of the asset groups is the [asset_groups.json](core/assets/config/assets/asset_groups.json) file. The configuration file can be used to add different types of assets, with their path and optional parameters.

#### SoundManager

The class [SoundManager](core/src/net/jfabricationgames/gdx/sound/SoundManager.java) is a singleton that can be configured and used as a central point for all sound effects. The SoundManager keeps a map of sound sets, that contain sounds for different types (to get a more structured configuration of sounds). The configuration of sounds is to be found in the [sound_sets.json](core/assets/config/sound/sound_sets.json) file. The sounds can be named and parameterized with a volume (where 1 is the unchanged default volume of the file) and a delay, which causes the sound effect to be delayed when started (this is usefull e.g. for attack sound effects on attacks, where the animation first charges and then hits, so the animation and the sound can be started at the same time). An example is the `hit2` sound effect of the dwarf (in the [sound_sets.json](core/assets/config/sound/sound_sets.json) config file).

**Note:** for the configuration (volume and delay) of the sound effects to take effect the sound has to be started using the `playSound(String name)` method of the [SoundSet](core/src/net/jfabricationgames/gdx/sound/SoundSet.java). Do not use the `getSound(String name)` method unless you want to configure the sound yourself.

#### Font

The class [FontManager](core/src/net/jfabricationgames/gdx/text/FontManager.java) is a singleton that can be configured and used as a central point for all fonts, that are used in the game, to write texts to the screen. The configuration can be found in the [fonts.json](core/assets/config/font/fonts.json) file. For more information on shaders in libGDX see the [libGDX wiki](https://github.com/libgdx/libgdx/wiki/Shaders).

## Inputs

Input handling in libGDX (as described in the [libGDX wiki](https://github.com/libgdx/libgdx/wiki/Input-handling) can be used quite easy, but can not be configured as a data-driven approach. Therefore the [input package](core/src/net/jfabricationgames/gdx/input) has the possibilities to configure the inputs in an xml file, and to query the inputs by configured names. The input configuration, that is used in the game is to be found in the [profile.xml](core/assets/config/input/profile.xml) file. The use of the configuration in code can be found in the [CharacterInputMovementHandler](core/src/net/jfabricationgames/gdx/character/CharacterInputMovementHandler.java) class. Because not all possibilities of the input configuration are used in the game, there is a second configuration file, that includes a detailed documentation on the configuration possibilities: [demo_config.xml](core/assets/config/input/demo_profile.xml)

## Enemies

The code for the enemies is divided into several packages. The base class for every enemy in the game is the abstract class [Enemy](core/src/net/jfabricationgames/gdx/enemy/Enemy.java). This class keeps track of all information of the enemeis, like their AI, the states, physics bodies and configurations. An Enemy instance is added to the map by the [TiledMapLoader](core/src/net/jfabricationgames/gdx/map/TiledMapLoader.java), which is explained in the [Maps](#maps) section. This loader uses the [EnemyFactory](core/src/net/jfabricationgames/gdx/enemy/EnemyFactory.java) class to create all enemies. Therefore the enemies that are mentioned in the map properties have to be added in the EnemyFactory, for the enemies to be created.

**Note:** The EnemyFactory class has to be expanded for every new enemy type that is created. The names used for the types of enemies have to be the same as referenced in the map properties.

Because an enemy type is a difficult object, the Enemy class has some abstract methods, that have to be implemented in an enemy implementation. These methods are used to create the physics body of the enemy and create the AI. These parts have to be done in code. All other parts (like graphics, animations, sound) can be configured using json configuration files. Examples of enemy classes can be found in the [enemy/implementation](core/src/net/jfabricationgames/gdx/enemy/implementation) package

Although parts of the enemy implementations must be designed inside the classes, some parts of the enemies are configured in a json configuration file. There is one file called [types.json](core/assets/config/enemy/types.json) for all enemy types, that keeps track of some configurations, and the configuration files, that are used to further configure the enemy's AI and states.

### Animations

The animations that an enemy can show are configured in json configuration files. These files are referenced from the [types.json](core/assets/config/enemy/types.json) configuration file. They include the animation's name (that is used to identify the animation within the texture atlas, and to reference it e.g. from the states), the atlas that is used to store the images, the frame duration for each of the images in the animation and the play mode. An example for such a file is the [gladiator.json](core/assets/config/animation/enemy/gladiator.json) file.

### AI

Most enemies act slightly different, but have many things in common. But not all enemies will make use of all types of AIs. There might be AI's that follow the player, while others just stand still and throw things to the player. Therefore the AI of the enemies is designed, using a [decorator pattern](https://en.wikipedia.org/wiki/Decorator_pattern) and a [chain of responsibility pattern](https://en.wikipedia.org/wiki/Chain-of-responsibility_pattern).

An example of this approach can be found in the [Gladiator](core/src/net/jfabricationgames/gdx/enemy/implementation/Gladiator.java) class, which is an implementation of an enemy. The AI is build of a [BaseAI](core/src/net/jfabricationgames/gdx/enemy/ai/BaseAI.java), that is surrounded by a [PreDefinedMovementAI](core/src/net/jfabricationgames/gdx/enemy/ai/implementation/PreDefinedMovementAI.java), surrounded by a [FollowAI](core/src/net/jfabricationgames/gdx/enemy/ai/implementation/FollowAI.java), surrounded by a [FightAI](core/src/net/jfabricationgames/gdx/enemy/ai/implementation/FightAI.java). 

These AI's will cause the following behaviour for the Gladiator:
- The [BaseAI](core/src/net/jfabricationgames/gdx/enemy/ai/BaseAI.java) is (obviously) the base for all AIs, because the chain has to end somewhere.
- The [PreDefinedMovementAI](core/src/net/jfabricationgames/gdx/enemy/ai/implementation/PreDefinedMovementAI.java) will make the gladiator move along a pre-defined way (defined in the map properties), as long as there is no higher AI, that says something else
- The [FollowAI](core/src/net/jfabricationgames/gdx/enemy/ai/implementation/FollowAI.java) will make the gladiator follow the player if he is in range (and detected by the enemies box2d sensor). This AI is positioned higher as the PreDefinedMovementAI in the chain of responsibility. Therefore the gladiator will priorize following the player higher than moving along his pre-defined way.
- The [FightAI](core/src/net/jfabricationgames/gdx/enemy/ai/implementation/FightAI.java) will make the gladiator attack the player if he is near enough.

The following image shows the call hierarchy of the AI classes:

![AI call hierarchie](core/data/documentation/ai_call_hierarchie.png)

### States

An enemy can be in different states, which can be interrupted by other states, or changed to other states when the state ends. Therefore the enemy states are designed as a (kind of) [state pattern](https://en.wikipedia.org/wiki/State_pattern). These states of the enemy and the transitions between the states can be configured in json configuration files. An example of a state configuration file for the gladiator is the [gladiator.json](https://github.com/tfassbender/DwarfScrollerGDX/blob/master/core/assets/config/enemy/states/gladiator.json) file. It defines a list of states, that are identified by their id. The states may define the following attributes:

- **id:** A string to identify the state (within this enemy the id has to be unique)
- **animation:** The animation that is to be played in the state. The animation references the animation name, that is defined in the animation config file, for this enemy. For the gladiator this configuration file is the [gladiator.json](core/assets/config/animation/enemy/gladiator.json) file. The animation will be started when the state is entered.
- **attack:** An attack, that is executed when entering the state. The name references an attack from the attack configuration file. See the [Attacks](#attacks) section for more details on attacks.
- **endsWithAnimation:** Defines whether the state ends when the animation of this state is over. If set to true a **followingState** has to be defined.
- **followingState:** Defines the state that will follow after this state ends (only used if **endsWithAnimation** is set to true).
- **interruptingStates:** A list of states, that can interrupt this state. If a state that is not defined in this list tries to interrupt this state, the state will not be changed. Examples for this list would be an `idle` state, which usually can be interrupted by every other state, or a `die` state, which probably can't be interrupted by any other state (see the [gladiator.json](core/assets/config/animation/enemy/gladiator.json) config file).
- **flipAnimationToMovingDirection:** Indicates whether the animation images of this state should be flipped to follow the moving direction (e.g. for the `move` state, so the enemy will not run backwards). The default value of this property is `true`.
- **flipAnimationOnEnteringOnly:** Indicates whether the animation should be flipped to the current direction, only once when entering the state. This can be usefull e.g. for the `die` state, because the direction of this animation usually doesn't change.

## Attacks

Attacks are used to add a hit fixture to the enemy body. Hit fixtures are Box2D sensors, that apply damage to the player, enemies or objects, when colliding. The attacks are usually referenced from the player's or enemy's states and are executed as soon as a state is entered. An example file, that defines the attacks of an enemy is the [spider.json](core/assets/config/enemy/attack/spider.json) file. The possible attributes are:

- **id:** A string to identify the attack (and reference it form the states configuration).
- **type:** An [AttackType](core/src/net/jfabricationgames/gdx/attack/AttackType.java), that is used to differ between different attack (like melee attacks and projectile attacks)
- **delay:** A delay (in seconds) before the attack starts. This can be usefull, because the animation may show the enemy charging before actually attacking, so the attack appears in the correct moment.
- **duration:** Defines how long the hit fixture will stay active before being removed. The default value is `0`, which leads to the hit fixture being added for only one render step.
- **damage:** The damage that the attack will deal to the player.
- **distFromCenter:** The distance, that the hit fixture will have from the center of the enemies body (in the direction of the attack).
- **hitFixtureRadius:** The radius of the hit fixture.
- **pushForce:** The force whith which the player will be pushed back when hit by the attack.
- **pushForceAffectedByBlock:** Defines whether the push force can be lowered by the players shield.
- **explosionDamage:** The damage that is dealt by a projectile attack, that creates an explosion (like bombs).
- **explosionPushForce:** The push force of an explosion, that is created by a projectile attack.
- **explosionPushForceAffectedByBlock:** Defines whether the push force of an explosion, that is created by a projectile attack, can be lowered by the players shield.

There are several types of attacks, but they can be divided into two classes: **Melee Attacks** and **Projectile Attacks**

### Melee Attacks

Melee Attacks are quite simple attacks. They create a hit fixture (a Box2D sensor, with a mask to hit the targeted objects / enemies) that is created near the executing character (a player, an enemy, ...) and deals a defined damage to the target if a collision is found by Box2D. Melee attacks are executed in the [MeleeAttack](core/src/net/jfabricationgames/gdx/attack/MeleeAttack.java) class. For the configuration of attacks see [Attacks](#attacks).

### Projectile Attacks

Projectile Attacks are attacks that don't just create a hit fixture, that deals damage, but create a projectile that moves in the map like a new map object. A projectile attack is created using the [ProjectileAttack](core/src/net/jfabricationgames/gdx/attack/ProjectileAttack.java) class, that creates a [Projectile](core/src/net/jfabricationgames/gdx/projectile/Projectile.java). This projectile behaves like a Game Object but also like an attack. Because all projectiles can show very different behavior there are multiple projectile subclasses like [Arrow](core/src/net/jfabricationgames/gdx/projectile/Arrow.java) (a quite simple projectile that moves in a straight line and deals damage), [Bomb](core/src/net/jfabricationgames/gdx/projectile/Bomb.java) (a projectile that waits some time before exploding, which creates an explosion. The explosion is also a projectile) or [Web](core/src/net/jfabricationgames/gdx/projectile/Web.java) (a projectile that deals damage to the player and slows him down if he's in it's range).

### Creating Attack Objects

Attacks can be created using an [AttackCreator](core/src/net/jfabricationgames/gdx/attack/AttackCreator.java) instance. Every object that is able to attack others (usually an enemy or a player) has an instance of an AttackCreator, that keeps track of all the attacks of the object. It can be used to start attacks by it's startAttack methods that take the name of the attack, the direction and optionally the Box2D [PhysicsCollisionType](core/src/net/jfabricationgames/gdx/physics/PhysicsCollisionType.java) to create an attack using an [AttackFactory](core/src/net/jfabricationgames/gdx/attack/AttackFactory.java). After creating the Attack, the AttackCreator keeps the attacks, handles them and removes them after they are over.

The attacks that can be used are defined in configuration files, that are given to the AttackCreator as constructor parameters. These configuration files define all attacks of the attacking object as a list of [AttackConfig](core/src/net/jfabricationgames/gdx/attack/AttackConfig.java) objects, that define the behaviour of the attack. An example for an attack configuration file is [minotaur.json](core/assets/config/enemy/attack/minotaur.json). The attacks, that are configured in these files can be created in the AttackCreator and are referenced by their **id**.

The call hierarchy, that is used to create an attack (from an enemy object) is shown in the diagram below:

![Attack creation hierarchy](core/data/documentation/create_attack_call_hierarchy.png)

## Events

The event handling system of the game uses a singleton class [EventHandler](core/src/net/jfabricationgames/gdx/event/EventHandler.java) that is used for firing events and substribing [EventListener](core/src/net/jfabricationgames/gdx/event/EventListener.java)s. The events that are fired are [EventConfig](core/src/net/jfabricationgames/gdx/event/EventConfig.java) objects, that define the type of the fired event as a value of the [EventType](core/src/net/jfabricationgames/gdx/event/EventType.java) enum and other parameters.

### Configured Events

Some events are configured in the configuration file [events.json](core/assets/config/events/events.json). These events are fired from the code on specific events (e.g. the game is started). And can be received from game objects, that can be configured in the map (see [Spawn Points](#spawn-points)).

### Global Events

Events that are not created in code, but in configuration (e.g. when touching an event object on the map) can be handled by the [GlobalEventListener](core/src/net/jfabricationgames/gdx/event/global/GlobalEventListener.java). This EventListener listens to all fired events and compares them to the event config of the events, that are configured in the JSON file [globalListenedEvents](core/assets/config/events/globalListenedEvents.json). If they match, the event is executed (e.g. to show a text on the screen when the player reaches a position or picks up a special item. See [Event Items](#event-items) and [Event Objects](#event-objects)). The *executionType*, that is configured in the global events is a value of the [GlobalEventExecutionType](core/src/net/jfabricationgames/gdx/event/global/GlobalEventExecutionType.java) enum, that defines how the event is to be executed.

## Items

### Adding Items

Items are usually added to the game from the map properties, where they can be defined and configured using the map object properties. To add an item to the map, an object has to be added to the objects layer of the map and named `item.item_name` where *item_name* is the name of an item, that is configured in the item configuration json file: [types.json](core/assets/config/items/types.json). The fields that can be configured in the json configuration file are:

- **texture:** The texture that will be used for the item.
- **animation:** The animation that will be used for the item.
- **physicsObjectRadius:** The radius of the item's physics body (default is 0.1).
- **pickUpSoundName:** The sound that will be played when the item is picked up by the player. The name references a sound, that is configured in the sound set config file [sound_sets.json](core/assets/config/sound/sound_sets.json) under the *item* sound set.

The properties that an item has, can be configured in the map, using the custom properties of an object.

### Dropping Items

Game objects and enemies can drop items at any time. What items they do drop can be defined either in the type config files [config/objects/types.json](core/assets/config/objects/types.json) and [config/enemy/types.json](core/assets/config/enemy/types.json), or in the map properties of every object that is added to the map, by using the map property key *drops*. The drop types, that are configured in the type config files define the default, that is used if no *drops* are configured in the map properties. Both configurations use a key-value map in JSON format, that defines the names of the items, that can be dropped, mapped to the probability to drop this item.  
The following example from the object type config file [types.json](core/assets/config/objects/types.json) shows a drop config, that has a 50% probability of dropping a *coin* item, a 20% probability of dropping a *big_coin* item and a 10% probability of dropping an *arrow* item:


```javascript
{
  barrel: {
    // ...
    drops: {
      coin: 0.5,
      big_coin: 0.2,
      arrow: 0.1,
    }
    
    //...
  },
  //...
}
```

The items are dropped onto the map using the [ItemDropUtil](core/src/net/jfabricationgames/gdx/item/ItemDropUtil.java) class, that defines the static method `dropItems(ObjectMap<String, Float>, GameMap, float, float, boolean)` to randomly choose an item from the map and drop it at a position on the map.

To drop a special item (like a special key to open a door), a different configuration must be used, because the normal drop items can't have special map properties. To drop special items the dropping object or enemy has to define the map property keys *specialDropType* (which defines the item type that is dropped by it's name) and *specialDropMapProperties* (which defines the map properties of the dropped object in JSON representation).

### Event Items

Event items are special types of items, that fire an event when picked up. The event type is an 'EVENT_ITEM_PICKED_UP' object from the [EventType](core/src/net/jfabricationgames/gdx/event/EventType.java) enum. The string parameter can be configured in the map properties of the event item. The parameter object of the event is the picked up item itself. Usually these events are handled by the [GlobalEventListener](core/src/net/jfabricationgames/gdx/event/global/GlobalEventListener.java), whichs events are configured in the [globalListenedEvents.json](core/assets/config/events/globalListenedEvents.json) config file. See [Events](#events) for more details.

## Game Objects

Game objects are usually added to the game from the map properties, just like items. Unlike items, game objects don't have custom properties, but define the customizable properties for every object type in the configuration json file: [types.json](core/assets/config/objects/types.json). In this file all types of objects can be configured using the following properties:

- **Config for all Game Objects**
  - **type:** The [GameObjectType](core/src/net/jfabricationgames/gdx/object/GameObjectType.java) of this object.
  - **texture:** The name of the texture that is shown for the game object.
  - **animationHit:** The animation that is shown when the player hits a game object.
  - **hitSound:** The sound that is played when the player hits the object. The name references a sound from the sound config file [sound_sets.json](core/assets/config/sound/sound_sets.json), under the *object* sound set.

  - **physicsBodySizeFactorX:** The size factor of the physics body in x direction (where *1* would be the whole size of the texture).
  - **physicsBodySizeFactorY:** The size factor of the physics body in y direction (where *1* would be the whole size of the texture).
  - **physicsBodyOffsetFactorX:** The offset factor of the physics body in x direction (where *0* would be the left edge of the texture).
  - **physicsBodyOffsetFactorY:** The size factor of the physics body in x direction (where *0* would be the bottom edge of the texture).
  - **isSensor:** Defines whether the whole body of the object should be a sensor.
  - **addSensor:** Defines whether a sensor is to be added to the body.
  - **sensorRadius:** Defines the radius of the sensor that might be added to the body.
  
  - **initAction:** A value from the [GameObjectAction](core/src/net/jfabricationgames/gdx/object/GameObjectAction.java) enum, that is executed after the game object was added to the map

  - **drops:** The items that the object may drop. See [Dropping Items](#dropping-items) for more details.
  - **dropPositionOffsetX:** The offset to change the position where items will be dropped.
  - **dropPositionOffsetY:** The offset to change the position where items will be dropped.
  - **renderDropsAboveObject:** Defines whether dropped items will be rendered above the object or underneath it.

- **Config for Destroyable Objects**
  - **animationBreak:** The animation that is shown when the player breaks a game object (like a barrel or a wooden box).
  - **destroySound:** The sound that is played when the player destroys the object. The name references a sound from the sound config file [sound_sets.json](core/assets/config/sound/sound_sets.json), under the *object* sound set.
  - **health:** The initial health points of the object.
  - **requiredAttackType:** An [AttackType](core/src/net/jfabricationgames/gdx/attack/AttackType.java) that is needed to damage this object. Only this attack type or a subtype of this type will be able to deal damage to the object. The default is *AttackType.ATTACK*, which means that all attacks will damage the object.

- **Config for InteractiveObjects**
  - **animationAction:** The animation that is shown when the object's action is executed (e.g. opening a chest).
  - **textureAfterAction:** The texture that will be used after the execution of the action.
  - **multipleActionExecutionsPossible:** Defines whether the action can be executed multiple times or only once.
  - **hitAnimationAfterAction:** Defines whether the hit animation is still shown after the execution of the action (which could be false if a different texture is used after the execution).
  - **changeBodyToSensorAfterAction:** Defines whether the body of the object is to be changed to a sensor after the execution of the action (after the animation).
  
- **Config for LockedObjects**
  - **defaultLocked:** Defines whether a locked object's default is locked or unlocked. Changes from the default have to be defined in the map properties.

Some other properties define the Box2D physics properties (like density or friction) of the object, which usually stay to the default values.

The Game Objects are split up into multiple subclasses, for the different types of actions they can be used for. The following image shows the class structure of these classes:

![Game Object class structure](core/data/documentation/game_object_classes.png)

### Destroyable Objects

Destroyable objects are quite simple objects, that can be destroyed by the player and usually drop some items (see [Dropping Items](#dropping-items)). They define animations for taking damage and being destroyed.

### Interactive Objects

Interactive Objects are used to let the player interact with the map. They use a sensor to find a player that is near and show an iteraction icon (a bubble with a question mark) to inform the player that an interaction is possible. They are used to display text on the screen (for dialogs with NPCs or other information) or for chests, that can be opened and drop items (see [Dropping Items](#dropping-items)).

### Locked Objects

A locked object is a special kind of [Interactive Object](#iteractive-objects), that is used to lock doors or chests, so the player can't simply reach something. A *Chest* is not locked by default, but a *Key Wall* is locked by default (see the configuration possibilities of [Game Objects](#game-objects)). The default behaviour can be changed by a map property with the key *locked*, which needs a `boolean` value. To unlock a locked object the player has to find a key item and afterwards interact with the locked object. For normal locked objects, a normal key can be used. To unlock a special locked object a special key with the same map properties is needed. To define a special locked object (or a special key) a map property key that starts with `key_` is to be used. To match a key to a locked object the name and the value of the map property have to be equal. The properties are loaded and matched using the [KeyItem](core/src/net/jfabricationgames/gdx/character/container/data/KeyItem.java) class.

### Spawn Points

Spawn points are special Game Objects, that can be used to spawn new Items, Game Objects or Enemies. They can be added to the map, just like all other Game Objects. These objects use an Event Handling Service, that informs them on every fired event. If the event triggers a spawn, a new object is spawned on the map. The events and spawns can be configured in the json files [events.json](core/assets/config/events/events.json) and [spawns.json](core/assets/config/spawn/spawns.json). The Spawn Points, that are added in the tiled map, have to use a map property 'spawn', that references a spawn config from the [spawns.json](core/assets/config/spawn/spawns.json) file by name. This spawn config defines, what is to be spawned and when it is spawned.

The following image shows the connections between these config files and objects:

![Spawns and Events](core/data/documentation/event_and_spawn_classes.png)

### Event Objects

Event Objects are game objects that can be placed on the tiled map and are added to the game map in the same size and position. These objects are not visible, but react to the player touching them. When the touch of a player is registered, the event object fires an event, that can be handled by all registered event listeners. The Type of the event is an 'EVENT_OBJECT_TOUCHED' object of the [EventType](core/src/net/jfabricationgames/gdx/event/EventType.java) enum. The string parameter of the fired event can be configured in the map properties. Usually these events are handled by the [GlobalEventListener](core/src/net/jfabricationgames/gdx/event/global/GlobalEventListener.java), whichs events are configured in the [globalListenedEvents.json](core/assets/config/events/globalListenedEvents.json) config file. See [Events](#events) for more details.

## Maps

Tiled maps are used to create a map with textures, physics and objects. Enemies, Items and game objects can be defined within the map's *objects* layer, like explained in the sections [Enemies](#enemies), [Items](#items) and [Game objects](#game-objects). Physics objects (like walls) can be defined in the physics layer of the map. **Note:** The map's physics objects have to be created by polygons with at most *8* points. The material of the physics objects must be set in the custom properties of every map object, where the key is called *material* and the name references a material name that is defined in the materials json configuration file: [materials.json](core/assets/config/map/materials.json). Within the materials configuratino file the name of the material can be defined, along with the usual box2d physics properties: *density*, *restitution* and *friction*

### Global Map Properties

There are several properties that can be configured for objects on the map. The map itself has only one property that needs to be configured: **mini_map_config_path**. The value of this property is the path to the main config file of this map. This file is a JSON file that can be deserialized to a [MapConfig](core/src/net/jfabricationgames/gdx/screens/menu/config/MapConfig.java) object. An example for such a file is [tutorial.json](core/assets/config/menu/maps/tutorial.json).

### Fast Travel

Fast travel positions can be created by adding [Game objects](#game-objects) to the map (and configuring them) and configuring their use in map configuration files. The objects that need to be added to the map need to be named *object.fastTravelPoint*. The configurable map properties of these objects are:

- **fastTravelPointId:** The Id of the fast travel point which needs to be unique over all maps, to identify the fast travel point.
- **fastTravelPointName:** The name of the fast travel point that is shown to the user.
- **activeOnStartup:** A boolean flag that indicates whether the fast travel point is active when the game is started, or it needs to be activated by touching it (the default value is *false*)

The second part of configuration is done in a separate config file, that defines the UI buttons, that are used to select the fast travel points on the mini-map in the menu. This configuration is a JSON object, that maps the **fastTravelPointId**s (that reference the ones in the map object config by name) to [MenuState](core/src/net/jfabricationgames/gdx/screens/menu/control/MenuState.java) objects, that define the selection and iteraction of the fast travel points in the UI. This file needs to be referenced from the map config file. An example for such a file is [tutorial_fast_travel_states.json](core/assets/config/menu/maps/tutorial_fast_travel_states.json).

## Others

### Texture packing tool

The texture packing tool can be used to easily pack multiple texture files from multiple directories into one texture atlas. The tool is placed in the [TexturePackingTool](desktop/src/net/jfabricationgames/gdx/desktop/util/TexturePackingTool.java) class inside the `desktop` project. The tool will pack all textures, that are configured into a texture atlas. It also has the possibility to configure multiple levels so for every level there will be one atlas generated. The configuration json file is [texture_settings.json](core/assets/config/texture_packing/texture_settings.json). Inside this config file all directories can be listed (relative to the [assets](core/assets) directory) to be packed into an atlas. The ouput directory, the name of the generated atlas and some libGDX packing settings (like edge padding or texture filters) can also be configured. The texture packing tool can be started using the gradle task `gradle :desktop:packTextures`.

### Credits

All of the graphics used for this game are available on [itch.io](https://itch.io/). Most of them came from [Elthen's Pixel Art Shop](https://elthen.itch.io/) and [Pixel Frog](https://pixelfrog-store.itch.io/).
