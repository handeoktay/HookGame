package hook.models;

import javafx.scene.canvas.GraphicsContext;
import hook.objects.Step;

/**
 * Created by IntelliJ IDEA.
 * User: Hande OKTAY
 * Date: 17/05/18
 * Time: 18:56
 */

public abstract class GameObject {
    private static long GLOBAL_ID = 0;

    private Step step;
    private float x;
    private float y;
    private float speed = 0;
    private long id;
    private Orientation orientation;
    protected boolean isDestroyed = false;

    public GameObject(Step step, float x, float y, Orientation orientation) {
        this.x = x;
        this.y = y;
        this.id = ++GLOBAL_ID;
        this.orientation = orientation;
        this.step = step;
    }

    public abstract void render(GraphicsContext graphicsContext);

    public abstract void update(double delta);

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }
}
