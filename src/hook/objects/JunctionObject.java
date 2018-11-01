package hook.objects;

import hook.Config;
import hook.models.GameObject;
import hook.models.Orientation;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

import static hook.Config.CIRCLE_RADIUS;

public class JunctionObject extends GameObject {
    public Color color = Config.PRIMARY_COLOR;
    public String junctionId;
    public ArrayList<Step> connectedSteps;
    double opacity = 1;

    public JunctionObject(String junctionId, float x, float y, Orientation orientation) {
        super(null, x, y, orientation);
        this.junctionId = junctionId;
        connectedSteps = new ArrayList<>();
    }

    @Override
    public void render(GraphicsContext graphicsContext) {
        int fadeOutedCount = 0;
        for (Step s : connectedSteps) {
            if (s.isFadeOuted) fadeOutedCount++;
        }

        if (fadeOutedCount == connectedSteps.size()) {
            opacity -= 0.02;
            if (opacity <= 0) {
                opacity = 0;
            }
        }

        color = Color.rgb(89, 86, 83, opacity);

        graphicsContext.setStroke(color);
        graphicsContext.setLineWidth(Config.THICK_LINE_STROKE);
        graphicsContext.strokeOval(getX() - CIRCLE_RADIUS, getY() - CIRCLE_RADIUS, 2 * CIRCLE_RADIUS, 2 * CIRCLE_RADIUS);
        graphicsContext.setLineWidth(Config.THIN_LINE_STROKE);
        switch (getOrientation()) {
            case VERTICAL:
                graphicsContext.strokeLine(getX(), getY() - CIRCLE_RADIUS, getX(), getY() + CIRCLE_RADIUS);
                break;
            case HORIZONTAL:
                graphicsContext.strokeLine(getX() - CIRCLE_RADIUS, getY(), getX() + CIRCLE_RADIUS, getY());
                break;
        }
    }

    @Override
    public void update(double delta) {

    }

    public boolean checkIfClicked(float mouseX, float mouseY) {
        boolean clicked = Math.hypot(getX() - CIRCLE_RADIUS + Config.CIRCLE_RADIUS - mouseX, getY() - CIRCLE_RADIUS + Config.CIRCLE_RADIUS - mouseY) < Config.CIRCLE_RADIUS;
        if (clicked) {
            setOrientation((getOrientation() == Orientation.VERTICAL) ? Orientation.HORIZONTAL : Orientation.VERTICAL);
        }
        return clicked;
    }

    public boolean checkIfHovered(float mouseX, float mouseY) {
        return Math.hypot(getX() - CIRCLE_RADIUS + Config.CIRCLE_RADIUS - mouseX, getY() - CIRCLE_RADIUS + Config.CIRCLE_RADIUS - mouseY) < Config.CIRCLE_RADIUS;
    }
}
