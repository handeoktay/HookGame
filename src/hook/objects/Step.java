package hook.objects;

import hook.Game;
import hook.models.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import hook.Config;

import java.util.ArrayList;

import static hook.Config.CIRCLE_RADIUS;
import static hook.models.Direction.DOWN;
import static hook.models.Direction.UP;

/**
 * Created by IntelliJ IDEA.
 * User: Hande OKTAY
 * Date: 16/05/18
 * Time: 21:32
 */

/**
 * Step is simply every single puzzle to solve.
 * It contains {@link #gameObjects} array that contains subclasses of {@link GameObject}.
 * ALL GAME OBJCETS
 * {@link CutterObject}
 * {@link HookObject}
 * {@link LineObject}
 * {@link TriggerObject}
 */
public class Step {
    public long stepId;
    public boolean isFadeOuted = false;
    // Game objects array to keep objects.
    private ArrayList<GameObject> gameObjects;
    public ArrayList<JunctionObject> junctionObjects;
    public Orientation validOrientation = Orientation.VERTICAL;
    // A Variable that allows to decide cut operation is done or not.
    boolean isCutted = false;
    // A reference to cutter object.
    CutterObject cutterObject;
    // A variable to keep last added game objects id.
    long lastObjectsId = -1;
    // A variable to handle fade out animation.
    boolean isFadeOutStarted = false;
    // A variable to keep step's own opacity.
    double opacity = 1;
    // A reference to keep direction of step after it is cutted.
    public Direction directionAfterCut = Direction.UNSETTED;
    // Variables to keep last added objects x,y coordinates.
    private float lastX = 0, lastY = 0;

    private Direction lastLineDirection = Direction.UNSETTED;


    boolean isRunning = true;
    boolean firstCollision = false;
    float firstCollisionDistanceLength = 0;
    private float speed = 5f;
    private Game game;

    // Default constructor.
    public Step(long stepId, Game game) {
        this.stepId = stepId;
        gameObjects = new ArrayList<>();
        junctionObjects = new ArrayList<>();
        this.game = game;
    }


    public void stop() {
        if (firstCollisionDistanceLength == 0) {
            firstCollision = true;
            setGameObjectsSpeed(-speed);
        } else {
            isRunning = false;
            game.fadeOutAll();
        }
    }

    /**
     * This method creates an {@link TriggerObject} to start position.
     * <p>
     * IMPORTANT!
     * Every step initialization should start with this method.
     *
     * @param startX is start x-position of this step.
     * @param startY is start y-position of this step.
     * @return this to generate chain methods
     */
    public Step startAt(float startX, float startY) {
        lastX = startX + CIRCLE_RADIUS;
        lastY = startY + CIRCLE_RADIUS;
        gameObjects.add(new TriggerObject(Step.this, startX + CIRCLE_RADIUS, startY + CIRCLE_RADIUS));
        return this;
    }


    /**
     * This method keeps last objects id at {@link #lastObjectsId}
     * <p>
     * IMPORTANT!
     * Every step initialization should end with this method.
     *
     * @return this to generate chain methods
     */
    public Step end() {
        LineObject lineObject = (LineObject) gameObjects.get(gameObjects.size() - 1);
        lastObjectsId = lineObject.getId();
        game.lineHitBoxes.add(new HitBox(Step.this, lineObject.endX, lineObject.endY, Orientation.UNDIRECTED).setGameObject(lineObject));

        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof HookObject) {
                HookObject ho = (HookObject) gameObject;
                switch (directionAfterCut) {
                    case UP:
                        game.hookHitBoxes.add(new HitBox(Step.this, gameObject.getX(), gameObject.getY()  - 2 * CIRCLE_RADIUS, Orientation.UNDIRECTED).setGameObject(gameObject));
                        break;
                    case DOWN:
                        game.hookHitBoxes.add(new HitBox(Step.this, gameObject.getX(), gameObject.getY()  + 2 * CIRCLE_RADIUS, Orientation.UNDIRECTED).setGameObject(gameObject));
                        break;
                    case LEFT:
                        game.hookHitBoxes.add(new HitBox(Step.this, gameObject.getX() - 2 * CIRCLE_RADIUS, gameObject.getY(), Orientation.UNDIRECTED).setGameObject(gameObject));
                        break;
                    case RIGHT:
                        game.hookHitBoxes.add(new HitBox(Step.this, gameObject.getX() + 2 * CIRCLE_RADIUS, gameObject.getY(), Orientation.UNDIRECTED).setGameObject(gameObject));
                        break;
                }
            }
        }

        return this;
    }


    /**
     * This method creates {@link LineObject} according to delta x and y.
     * It calculates the orientation of this line according to params.
     * <p>
     * After {@link #cut()} is called, the first initialized line indicates the direction of this step.
     * After all, it updates the {@link #lastX} and {@link #lastY} to use at next operations.
     *
     * @param dX how long do you want to go in x-axis
     * @param dY how long do you want to go in y-axis
     * @return this to generate chain methods
     */
    public Step lineTo(float dX, float dY) {
        if (isCutted && directionAfterCut == Direction.UNSETTED) {
            if (dY == 0) {
                directionAfterCut = (dX > 0) ? Direction.RIGHT : Direction.LEFT;
            } else {
                directionAfterCut = (dY > 0) ? DOWN : UP;
            }
        }

        if (dY == 0) {
            lastLineDirection = (dX > 0) ? Direction.RIGHT : Direction.LEFT;
        } else {
            lastLineDirection = (dY > 0) ? DOWN : UP;
        }


        Orientation orientation = (Math.abs(dX) > Math.abs(dY)) ? Orientation.HORIZONTAL : Orientation.VERTICAL;
        gameObjects.add(new LineObject(Step.this, lastX, lastY, lastX + dX, lastY + dY, orientation));
        lastX += dX;
        lastY += dY;
        return this;
    }


    /**
     * This method creates {@link CutterObject}.
     * It also makes {@link #isCutted} true to notify cut operation is done and
     * gets reference of {@link CutterObject}.
     * <p>
     * IMPORTANT!
     * This method should be called once on every step.
     *
     * @return this to generate chain methods
     */
    public Step cut() {
        isCutted = true;
        cutterObject = new CutterObject(Step.this, lastX, lastY);
        gameObjects.add(cutterObject);
        return this;
    }


    public Step junction() {
        moveLastCoordinates(lastLineDirection);
        if (lastLineDirection == UP || lastLineDirection == DOWN) {
            validOrientation = Orientation.VERTICAL;
        } else {
            validOrientation = Orientation.HORIZONTAL;
        }
        return this;
    }


    /**
     * This method creates {@link HookObject} according to {@link Direction} and {@link Orientation}.
     * <p>
     * The orientation calculating programmatically according to last added line.
     * if orientation is VERTICAL you should give params like RIGHT or LEFT.
     * if orientation is HORIZONTAL you should give params like UP or DOWN.
     *
     * @param direction is where the hook looking at.
     * @return this to generate chain methods
     */
    public Step hook(Direction direction) {
        gameObjects.add(new HookObject(Step.this, lastX, lastY, direction));
        moveLastCoordinates(directionAfterCut);
        return this;
    }

    private void moveLastCoordinates(Direction direction) {
        switch (direction) {
            case UP:
                lastY -= 2 * CIRCLE_RADIUS;
                break;
            case DOWN:
                lastY += 2 * CIRCLE_RADIUS;
                break;
            case LEFT:
                lastX -= 2 * CIRCLE_RADIUS;
                break;
            case RIGHT:
                lastX += 2 * CIRCLE_RADIUS;
                break;
        }
    }


    /**
     * This method triggers the step if {@link TriggerObject#checkIfClicked(float, float)} method returns true.
     * It simply changes speeds of all {@link HookObject}'s and thick {@link LineObject}'s.
     * This speed changes these objects x and y coordinates according to {@link #directionAfterCut} variable.
     * Also makes {@link CutterObject#shouldExtend} true to trigger extend animation of cutter.
     */
    public void trigger() {
        for (JunctionObject junctionObject : junctionObjects) {
            if (validOrientation != junctionObject.getOrientation()) {
                return;
            }
        }
        cutterObject.shouldExtend = true;
        setGameObjectsSpeed(speed);
    }


    /**
     * This method is for {@link CutterObject}
     * It calculates the last objects orientation and returns the opposite
     * orientation to create {@link CutterObject}
     *
     * @return {@link Orientation} of last object at gameObjects array.
     */
    Orientation getLastObjectOrientation() {
        return (gameObjects.get(gameObjects.size() - 1).getOrientation() == Orientation.VERTICAL) ? Orientation.HORIZONTAL : Orientation.VERTICAL;
    }


    /**
     * This method contains all game objects update method.
     *
     * @param delta is time between every game cycle.
     */
    public void update(double delta) {

        if (firstCollision) {
            firstCollisionDistanceLength += speed;
            if (firstCollisionDistanceLength >= 30) {
                firstCollision = false;
                setGameObjectsSpeed(speed);
            }
        }

        for (GameObject gameObject : gameObjects) {
            gameObject.update(delta);
        }


    }


    /**
     * This method contains all game objects render method.
     * <p>
     * All objects are rendered according to {@link Config#PRIMARY_COLOR}
     * after {@link #fadeOut()} is called, we change the PRIMARY_COLOR variable
     * to animate fadeOut effect.
     *
     * @param graphicsContext is main paint object of Canvas.
     */
    public void render(GraphicsContext graphicsContext) {
        Config.PRIMARY_COLOR = Color.rgb(89, 86, 83, opacity);
        if (isFadeOutStarted) {
            opacity -= 0.02;
            if (opacity <= 0) {
                opacity = 0;
                isFadeOutStarted = false;
            }
        }
        for (GameObject gameObject : gameObjects) {
            gameObject.render(graphicsContext);
        }

    }


    /**
     * This method runs on every mouse move.
     * This method checks the mouse position if it inside the {@link TriggerObject} or not.
     *
     * @param mouseX is x-coordinate of mouse move.
     * @param mouseY is y-coordinate of mouse move.
     * @return 1 if it is hovered, 0 if it is not
     */
    public int checkMouseHovered(float mouseX, float mouseY) {
        TriggerObject triggerObject = (TriggerObject) gameObjects.get(0);
        if (triggerObject.checkIfHovered(mouseX, mouseY)) {
            return 1;
        }
        return 0;
    }


    /**
     * This method runs on every mouse click.
     * This method checks the mouse position if it inside the {@link TriggerObject} or not.
     * <p>
     * If it is inside of TriggerObject when the click performed, calls {@link #trigger()}
     *
     * @param mouseX is x-coordinate of mouse click.
     * @param mouseY is y-coordinate of mouse click.
     */
    public void checkMousePosition(float mouseX, float mouseY) {
        TriggerObject triggerObject = (TriggerObject) gameObjects.get(0);
        if (triggerObject.checkIfClicked(mouseX, mouseY)) {
            trigger();
        }
    }


    /**
     * This method starts this steps fade out animation.
     */
    public void fadeOut() {
        isFadeOuted = true;
        cutterObject.shouldFadeOut = true;
        if (!isFadeOutStarted) {
            isFadeOutStarted = true;
        }


    }


    private void setGameObjectsSpeed(float speed) {
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof HookObject || gameObject instanceof LineObject) {
                gameObject.setSpeed(speed);
            }
        }
    }
}
