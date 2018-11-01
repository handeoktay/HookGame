package hook.objects;

import javafx.scene.canvas.GraphicsContext;
import hook.Config;
import hook.models.Orientation;
import hook.models.GameObject;

/**
 * Created by IntelliJ IDEA.
 * User: Hande OKTAY
 * Date: 16/05/18
 * Time: 20:34
 */

public class TriggerObject extends GameObject {

    private boolean isGrowing = false;
    private boolean isShrinking = false;
    private float circleRadius;
    private float growthSpeed = 1.5f;

    public TriggerObject(Step parentStep, float x, float y) {
        super(parentStep, x, y, Orientation.UNDIRECTED);
        circleRadius = Config.CIRCLE_RADIUS;
    }

    @Override
    public void render(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Config.PRIMARY_COLOR);
        graphicsContext.fillOval(getX() - circleRadius, getY() - circleRadius, 2 * circleRadius, 2 * circleRadius);
    }

    @Override
    public void update(double delta) {
        if (isGrowing) {
            circleRadius += growthSpeed;
            if (circleRadius > Config.CIRCLE_RADIUS * 1.2f) {
                isGrowing = false;
                isShrinking = true;
            }
        }

        if (isShrinking) {
            circleRadius -= growthSpeed;
            if (circleRadius <= Config.CIRCLE_RADIUS) {
                isShrinking = false;
            }
        }
    }

    boolean checkIfClicked(float mouseX, float mouseY) {
        boolean clicked = Math.hypot(getX() - circleRadius + Config.CIRCLE_RADIUS - mouseX, getY() - circleRadius + Config.CIRCLE_RADIUS - mouseY) < Config.CIRCLE_RADIUS;
        if (clicked) {
            isGrowing = true;
        }
        return clicked;
    }

    public boolean checkIfHovered(float mouseX, float mouseY) {
        return Math.hypot(getX() - circleRadius + Config.CIRCLE_RADIUS - mouseX, getY() - circleRadius + Config.CIRCLE_RADIUS - mouseY) < Config.CIRCLE_RADIUS;
    }
}
