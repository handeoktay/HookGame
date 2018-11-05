//Hande Nur Oktay
package hook;

import hook.models.Direction;
import hook.models.HitBox;
import hook.models.Orientation;
import hook.objects.JunctionObject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import hook.objects.Step;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static hook.Config.HEIGHT;
import static hook.Config.WIDTH;
import static hook.models.Direction.*;

/**
 * Created by IntelliJ IDEA.
 * User: Hande OKTAY
 *
 * Date: 16/05/18
 * Time: 20:03
 */

public class Game extends Application implements Runnable {

    public static boolean DEBUGGING = false;

    private GraphicsContext graphicsContext;
    private int hoveredPointerCount = 0;

    private ArrayList<Step> stepArrayList;

    public ArrayList<HitBox> lineHitBoxes;
    public ArrayList<HitBox> hookHitBoxes;
    public ArrayList<JunctionObject> junctionObjects;
    private boolean isRunning = false;


    Group root;
    Scene scene;


    private Thread gameThread;

    private boolean restartLevel = false;
    private float restartLevelTimer = 0;

    private boolean nextLevel = false;
    private float nextLevelTimer = 0;

    private boolean levelInfo = false;
    private float levelInfoTimer = 0;
    private float levelInfoTime = 120;

    private int fadeOutedSteps = 0;


    public int currentLevel = 1;

    @Override
    public void start(Stage stage) {

        // Prepares scene and stage.
        root = new Group();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        scene = new Scene(root, WIDTH, HEIGHT);
        stage.setTitle("The Hook");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        junctionObjects = new ArrayList<>();
        stepArrayList = new ArrayList<>();
        lineHitBoxes = new ArrayList<>();
        hookHitBoxes = new ArrayList<>();

        // Gets the paint object of canvas.
        graphicsContext = canvas.getGraphicsContext2D();

        // Adds MOUSE_CLICKED callback to root.
        root.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (isRunning) {
                for (Step step : stepArrayList) {
                    step.checkMousePosition((float) e.getSceneX(), (float) e.getSceneY());
                }

                for (JunctionObject junctionObject : junctionObjects) {
                    junctionObject.checkIfClicked((float) e.getSceneX(), (float) e.getSceneY());
                }
            }

            System.out.println("MOUSE X: " + e.getSceneX() + " MOUSE Y: " + e.getSceneY());
        });

        // Adds MOUSE_MOVED callback to root.
        root.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            if (isRunning) {

                // Get the return value of every steps checkMouseHovered method.
                for (Step step : stepArrayList) {
                    hoveredPointerCount += step.checkMouseHovered((float) e.getSceneX(), (float) e.getSceneY());
                }
                // If it is bigger than zero, that means we are hovering some Trigger Object.
                if (hoveredPointerCount > 0) {
                    // Make cursor HAND
                    scene.setCursor(Cursor.HAND);
                } else {
                    // Make cursor DEFAULT
                    scene.setCursor(Cursor.DEFAULT);
                }
                // Clear the hoveredPointerCount
                hoveredPointerCount = 0;
            }
        });


        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case D:
                    DEBUGGING = !DEBUGGING;
                    break;
            }
        });

        loadLevel(currentLevel);

        // LET THE GAME BEGIN!
        gameThread = new Thread(this);
        gameThread.start();
        isRunning = true;
    }

    private void loadLevel(int levelNumber) {
        isRunning = false;
        nextLevel = false;
        if (!restartLevel) {
            levelInfo = true;
        }
        restartLevel = false;
        clearLevel();
        try (BufferedReader br = new BufferedReader(new FileReader(new File("levels/" + levelNumber + ".level")))) {
            String line;
            Step step = null;
            while ((line = br.readLine()) != null) {

                if (line.startsWith("add")) {

                    String[] splitted = line.split(" ");
                    Step jStep = Objects.requireNonNull(getStepById(Long.parseLong(splitted[2])));
                    JunctionObject junctionObject = Objects.requireNonNull(getJunctionById(splitted[1]));
                    jStep.junctionObjects.add(junctionObject);
                    junctionObject.connectedSteps.add(jStep);

                } else if (line.startsWith("step")) {

                    String[] splitted = line.split(" ");
                    step = new Step(Long.parseLong(splitted[1]), Game.this);

                } else if (line.startsWith("junction")) {

                    String[] splitted = line.split(" ");
                    Orientation orientation = Orientation.UNDIRECTED;
                    if (splitted[4].equals("VERTICAL")) {
                        orientation = Orientation.VERTICAL;
                    } else if (splitted[4].equals("HORIZONTAL")) {
                        orientation = Orientation.HORIZONTAL;
                    }
                    junctionObjects.add(new JunctionObject(splitted[1], Float.parseFloat(splitted[2]), Float.parseFloat(splitted[3]), orientation));

                }
                if (step != null) {
                    if (line.startsWith("start")) {
                        String[] splitted = line.split(" ");
                        step.startAt(Float.parseFloat(splitted[1]), Float.parseFloat(splitted[2]));
                    } else if (line.startsWith("line")) {
                        String[] splitted = line.split(" ");
                        if (splitted[1].equals("x")) {
                            step.lineTo(Float.parseFloat(splitted[2]), 0);
                        } else if (splitted[1].equals("y")) {
                            step.lineTo(0, Float.parseFloat(splitted[2]));
                        }
                    } else if (line.startsWith("cut")) {
                        step.cut();
                    } else if (line.startsWith("gap")) {
                        step.junction();
                    } else if (line.startsWith("hook")) {
                        String[] splitted = line.split(" ");
                        Direction direction = Direction.UNSETTED;
                        switch (splitted[1]) {
                            case "UP":
                                direction = UP;
                                break;
                            case "DOWN":
                                direction = DOWN;
                                break;
                            case "RIGHT":
                                direction = RIGHT;
                                break;
                            case "LEFT":
                                direction = LEFT;
                                break;
                        }

                        step.hook(direction);
                    } else if (line.startsWith("end")) {
                        step.end();
                        stepArrayList.add(step);
                        step = null;
                    }
                }

            }
        } catch (IOException e) {
            Platform.exit();
            e.printStackTrace();
        }
        isRunning = true;
    }

    // Game method
    public static void main(String[] args) {
        launch(args);
    }

    // Game Thread's run method.
    @Override
    public void run() {
        long lastLoopTime = System.nanoTime();
        // This is targeted FPS
        final int TARGET_FPS = 60;
        // This is targeted time between every frame.
        final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
        // This is the FPS of last rendered frame
        long lastFpsTime = 0;
        // This references the time at the beginning of frame.
        long now;
        long updateLength;
        // This is time between every game cycle.
        double delta;
        // This is time that thread needs to sleep.
        long gameTime;

        while (isRunning) {
            now = System.nanoTime();
            updateLength = now - lastLoopTime;
            lastLoopTime = now;
            delta = updateLength / ((double) OPTIMAL_TIME);

            lastFpsTime += updateLength;
            if (lastFpsTime >= 1000000000) {
                lastFpsTime = 0;
            }

            fadeOutedSteps = 0;
            // Call every steps update method.
            for (Step step : stepArrayList) {
                step.update(delta);
                if (step.isFadeOuted) fadeOutedSteps++;
            }

            for (HitBox hitBox : lineHitBoxes) {
                hitBox.update(delta);
            }
            for (HitBox hitBox : hookHitBoxes) {
                hitBox.update(delta);
            }

            for (HitBox lineHitBox : lineHitBoxes) {
                for (HitBox hookHitBox : hookHitBoxes) {
                    boolean intersects = lineHitBox.contact(hookHitBox);
                    if (intersects) {
                        hookHitBox.getStep().stop();
                        if (DEBUGGING)
                            System.out.println("COLLISION BETWEEN: " + lineHitBox.getId() + " - " + hookHitBox.getId());
                    }
                }
            }

            double finalDelta = delta;
            Platform.runLater(() -> {
                // Clears the screen
                clearScreen();

                if (levelInfo) {
                    System.out.println("LEVEL INFO");
                    levelInfoTimer += finalDelta;
                    if (levelInfoTimer >= levelInfoTime) {
                        levelInfoTimer = 0;
                        levelInfo = false;
                    }
                    graphicsContext.setFill(Color.rgb(89, 86, 83));
                    graphicsContext.setFont(Font.font(150));
                    graphicsContext.fillText(currentLevel + " ", WIDTH / 2f - 75, HEIGHT / 2f + 50);

                } else {
                    // Call every steps render method.
                    for (Step step : stepArrayList) {
                        step.render(graphicsContext);
                    }

                    if (DEBUGGING) {
                        for (HitBox hitBox : lineHitBoxes) {
                            graphicsContext.setFont(Font.font(10));
                            hitBox.render(graphicsContext);
                        }
                        for (HitBox hitBox : hookHitBoxes) {
                            hitBox.render(graphicsContext);
                        }
                    }

                    for (JunctionObject junctionObject : junctionObjects) {
                        junctionObject.render(graphicsContext);
                    }
                }
            });


            // Freeze Game Thread according to our calculations to keep FPS on 60.
            try {
                gameTime = (lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000;
                if (gameTime > 0)
                    Thread.sleep(gameTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            if (fadeOutedSteps >= stepArrayList.size()) {
                if (!restartLevel) {
                    nextLevel = true;
                }
            }

            if (nextLevel) {
                nextLevelTimer += delta;
                if (nextLevelTimer >= 80) {
                    nextLevelTimer = 0;
                    System.out.println("NEXT LEVEL");
                    loadLevel(++currentLevel);
                }
            }

            if (restartLevel) {
                restartLevelTimer += delta;
                if (restartLevelTimer >= 80) {
                    restartLevelTimer = 0;
                    System.out.println("RESTART");
                    loadLevel(currentLevel);
                }
            }


        }
    }

    /**
     * This method clears the canvas.
     */
    private void clearScreen() {
        graphicsContext.setFill(Config.BACKGROUND_COLOR);
        graphicsContext.fillRect(0, 0, WIDTH, HEIGHT);
    }

    private void clearLevel() {
        stepArrayList.clear();
        junctionObjects.clear();
        lineHitBoxes.clear();
        hookHitBoxes.clear();
    }

    public void fadeOutAll() {
        for (Step step : stepArrayList) {
            step.fadeOut();
        }

        restartLevel = true;
    }


    private Step getStepById(long id) {
        for (Step step : stepArrayList) {
            if (step.stepId == id) {
                return step;
            }
        }
        return null;
    }

    private JunctionObject getJunctionById(String id) {
        for (JunctionObject junctionObject : junctionObjects) {
            if (junctionObject.junctionId.equals(id)) {
                return junctionObject;
            }
        }
        return null;
    }
}
