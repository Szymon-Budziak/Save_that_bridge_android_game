package unina.game.development.savethatbridge.physicsapp.general;

import android.graphics.BitmapFactory;

import java.util.ArrayList;

import unina.game.development.savethatbridge.R;
import unina.game.development.savethatbridge.physicsapp.activities.StartingActivity;
import unina.game.development.savethatbridge.physicsapp.gameobjects.Anchor;
import unina.game.development.savethatbridge.physicsapp.gameobjects.Bomb;
import unina.game.development.savethatbridge.physicsapp.gameobjects.Bridge;
import unina.game.development.savethatbridge.physicsapp.gameobjects.GameObject;
import unina.game.development.savethatbridge.physicsapp.gameobjects.Road;
import unina.game.development.savethatbridge.physicsapp.gameobjects.Terrorist;

public class Level {
    private final float bridgeLength;
    private final float deckHeight;

    private static Box physicalSize;

    enum Levels {
        LEVEL_1,
        LEVEL_2,
        END
    }

    public Level(GameWorld world) {
        this.bridgeLength = GameWorld.getBridgeLength();
        this.deckHeight = GameWorld.getDeckHeight();
        physicalSize = world.getPhysicalSize();
    }

    public void level1(GameWorld world) {
        // prevents scaling and sets level 1 background to a picture
        initialSetup(world, Levels.LEVEL_1);

        // adding anchors for bridges
        addBridgeAnchors(world, 2);

        // adding anchors on roads
        addRoadAnchors(world, 2);

        // adding bridge decks and creating them
        addBridgeDecks(world, 6);

        // creating joints between roads and bridge decks
        createJoints(world, 2, 6);

        // creating bomb and terrorist
        createTerroristAndBomb(world, 6, 2);

        GameWorld.setPlanksToPlace(1);
        GameWorld.bridgeConstructions = new ArrayList<>(GameWorld.getPlanksToPlace());
    }

    public void level2(GameWorld world) {
        // prevents scaling and sets level 2 background to a picture
        initialSetup(world, Levels.LEVEL_2);

        // adding anchors for bridges
        addBridgeAnchors(world, 3);

        // adding anchors on roads
        addRoadAnchors(world, 3);

        // adding bridge decks and creating them
        addBridgeDecks(world, 15);

        // creating joints between roads and bridge decks
        createJoints(world, 3, 15);

        // creating bomb and terrorist
        createTerroristAndBomb(world, 15, 6);

        GameWorld.setPlanksToPlace(3);
        GameWorld.bridgeConstructions = new ArrayList<>(GameWorld.getPlanksToPlace());
    }

    public void endLevel(GameWorld world) {
        // prevents scaling and sets end level background to a picture
        initialSetup(world, Levels.END);

        // change music
        StartingActivity activity = world.getActivity();
        activity.setupSound(true);
    }

    private void initialSetup(GameWorld world, Levels level) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        switch (level) {
            case LEVEL_1:
                world.setBitmap(BitmapFactory.decodeResource(world.getActivity().getResources(), R.drawable.background_l1, options));
                break;
            case LEVEL_2:
                world.setBitmap(BitmapFactory.decodeResource(world.getActivity().getResources(), R.drawable.background_l2, options));
                break;
            case END:
                world.setBitmap(BitmapFactory.decodeResource(world.getActivity().getResources(), R.drawable.end_background, options));
                break;
            default:
                throw new IllegalArgumentException("Invalid level: " + level);
        }
        GameWorld.setCanPlace(true);
        GameWorld.setWorldBorder(world.addGameObject(new EnclosureGO(world, physicalSize.getxMin(), physicalSize.getxMax(), physicalSize.getyMin(), physicalSize.getyMax())));
    }

    private void addBridgeAnchors(GameWorld gameWorld, int numberOfRoadAnchors) {
        ArrayList<GameObject> gameBridgeAnchors = new ArrayList<>(numberOfRoadAnchors);
        gameBridgeAnchors.add(gameWorld.addGameObject(new Anchor(gameWorld, -this.bridgeLength / 2 + 3, physicalSize.getyMax() - 7)));
        gameBridgeAnchors.add(gameWorld.addGameObject(new Anchor(gameWorld, this.bridgeLength / 2 - 3, physicalSize.getyMax() - 7)));
        if (numberOfRoadAnchors == 3) {
            gameBridgeAnchors.add(gameWorld.addGameObject(new Anchor(gameWorld, this.bridgeLength / 2 - 14, physicalSize.getyMax() - 3)));
        }
        GameWorld.gameBridgeAnchors = gameBridgeAnchors;
    }

    private void addRoadAnchors(GameWorld gameWorld, int numberOfRoadAnchors) {
        ArrayList<GameObject> roadAnchors = new ArrayList<>(numberOfRoadAnchors);
        roadAnchors.add(gameWorld.addGameObject(new Road(gameWorld, physicalSize.getxMin(), -this.bridgeLength / 2, 0, physicalSize.getyMax())));
        roadAnchors.add(gameWorld.addGameObject(new Road(gameWorld, this.bridgeLength / 2, physicalSize.getxMax(), 0, physicalSize.getyMax())));
        GameWorld.road = roadAnchors;
    }

    private void addBridgeDecks(GameWorld world, int numberOfBridgeDecks) {
        float deckWidth = bridgeLength / numberOfBridgeDecks;
        GameWorld.bridge = new ArrayList<>(numberOfBridgeDecks);
        for (int i = 0; i < numberOfBridgeDecks; i++) {
            float x = -bridgeLength / 2 + i * deckWidth + deckWidth / 2;
            GameObject bridge = new Bridge(world, x, 0, deckWidth, deckHeight);
            GameWorld.bridge.add(world.addGameObject(bridge));
        }
    }

    private void createJoints(GameWorld world, int numberOfRoadAnchors, int numberOfBridgeDecks) {
        float deckWidth = bridgeLength / numberOfBridgeDecks;

        GameWorld.gameJoints = new ArrayList<>(numberOfRoadAnchors + numberOfBridgeDecks);
        MyRevoluteJoint joint;
        joint = new MyRevoluteJoint(world, GameWorld.road.get(0).body, GameWorld.bridge.get(0).body, -deckWidth / 2, -deckHeight / 2, ((Road) GameWorld.road.get(0)).getWidth() / 2, -((Road) GameWorld.road.get(0)).getHeight() / 2);
        GameWorld.gameJoints.add(joint);
        for (int i = 0; i < numberOfBridgeDecks - 1; i++) {
            joint = new MyRevoluteJoint(world, GameWorld.bridge.get(i).body, GameWorld.bridge.get(i + 1).body, -deckWidth / 2, -deckHeight / 2, deckWidth / 2, -deckHeight / 2);
            GameWorld.gameJoints.add(joint);
        }
        joint = new MyRevoluteJoint(world, GameWorld.bridge.get(numberOfBridgeDecks - 1).body, GameWorld.road.get(1).body, -((Road) GameWorld.road.get(0)).getWidth() / 2, -((Road) GameWorld.road.get(0)).getHeight() / 2, deckWidth / 2, -deckHeight / 2);
        GameWorld.gameJoints.add(joint);
    }

    private void createTerroristAndBomb(GameWorld world, int numberOfBridgeDecks, int index) {
        float deckWidth = bridgeLength / numberOfBridgeDecks;

        Terrorist terrorist = new Terrorist(world, physicalSize.getxMin() + 2, -1);
        world.addGameObject(terrorist);
        GameWorld.setTerrorist(terrorist);

        MyRevoluteJoint joint = GameWorld.gameJoints.get(index);
        Bomb bomb = new Bomb(world, joint.getJoint().getBodyB().getPositionX() - deckWidth / 2, joint.getJoint().getBodyB().getPositionY() + deckHeight / 2, joint, world.getActivity().getResources());
        GameWorld.setBomb(bomb);
    }
}