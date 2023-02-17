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

    public Level(GameWorld world) {
        this.bridgeLength = GameWorld.getBridgeLength();
        this.deckHeight = GameWorld.getDeckHeight();
        physicalSize = world.getPhysicalSize();
    }

    public void level1(GameWorld world) {
        // prevents scaling and sets level 1 background to a picture
        initialSetup(world, 1);

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
        initialSetup(world, 2);

        // adding anchors for bridges
        addBridgeAnchors(world, 3);

        // adding anchors on roads
        addRoadAnchors(world, 3);

        // adding bridge decks and creating them
        addBridgeDecks(world, 12);

        // creating joints between roads and bridge decks
        createJoints(world, 3, 12);

        // creating bomb and terrorist
        createTerroristAndBomb(world, 12, 4);

        GameWorld.setPlanksToPlace(3);
        GameWorld.bridgeConstructions = new ArrayList<>(GameWorld.getPlanksToPlace());
    }

    public void endLevel(GameWorld world) {
        // prevents scaling and sets end level background to a picture
        initialSetup(world, 3);

        // change music
        StartingActivity activity = world.getActivity();
        activity.setupSound(true);
    }

    private void initialSetup(GameWorld world, int level) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        if (level == 1)
            world.setBitmap(BitmapFactory.decodeResource(world.getActivity().getResources(), R.drawable.background_l1, options));
        else if (level == 2)
            world.setBitmap(BitmapFactory.decodeResource(world.getActivity().getResources(), R.drawable.background_l2, options));
        else
            world.setBitmap(BitmapFactory.decodeResource(world.getActivity().getResources(), R.drawable.end_background, options));
        GameWorld.setCanPlace(true);
        GameWorld.setWorldBorder(world.addGameObject(new EnclosureGO(world, physicalSize.getxMin(), physicalSize.getxMax(), physicalSize.getyMin(), physicalSize.getyMax())));
    }

    private void addBridgeAnchors(GameWorld world, int numberOfRoadAnchors) {
        GameWorld.gameBridgeAnchors = new ArrayList<>(numberOfRoadAnchors);
        GameObject firstAnchor = new Anchor(world, -this.bridgeLength / 2 + 3, physicalSize.getyMax() - 7);
        GameObject secondAnchor = new Anchor(world, this.bridgeLength / 2 - 3, physicalSize.getyMax() - 7);

        GameWorld.gameBridgeAnchors.add(world.addGameObject(firstAnchor));
        GameWorld.gameBridgeAnchors.add(world.addGameObject(secondAnchor));
        if (numberOfRoadAnchors == 3) {
            GameObject thirdAnchor = new Anchor(world, physicalSize.getxMax() - 10, physicalSize.getyMax() - 5);
            GameWorld.gameBridgeAnchors.add(world.addGameObject(thirdAnchor));
        }
    }

    private void addRoadAnchors(GameWorld world, int numberOfRoadAnchors) {
        GameWorld.road = new ArrayList<>(numberOfRoadAnchors);
        GameObject firstAnchor = new Road(world, physicalSize.getxMin(), -this.bridgeLength / 2, 0, physicalSize.getyMax());
        GameObject secondAnchor = new Road(world, this.bridgeLength / 2, physicalSize.getxMax(), 0, physicalSize.getyMax());

        GameWorld.road.add(world.addGameObject(firstAnchor));
        GameWorld.road.add(world.addGameObject(secondAnchor));
    }

    private void addBridgeDecks(GameWorld world, int numberOfBridgeDecks) {
        float deckWidth = bridgeLength / numberOfBridgeDecks;

        GameWorld.bridge = new ArrayList<>(numberOfBridgeDecks);
        GameObject bridge;
        for (int i = 0; i < numberOfBridgeDecks; i++) {
            bridge = new Bridge(world, (-bridgeLength / 2) + (i * deckWidth), 0, deckWidth, deckHeight);
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
        GameWorld.setTerrorist(terrorist);
        world.addGameObject(terrorist);

        MyRevoluteJoint joint = GameWorld.gameJoints.get(index);
        Bomb bomb = new Bomb(world, joint.getJoint().getBodyB().getPositionX() - deckWidth / 2, joint.getJoint().getBodyB().getPositionY() + deckHeight / 2, joint, world.getActivity().getResources());
        GameWorld.setBomb(bomb);
    }
}