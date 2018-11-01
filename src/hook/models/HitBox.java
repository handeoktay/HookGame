package hook.models;

import hook.Config;
import hook.objects.HookObject;
import hook.objects.LineObject;
import hook.objects.Step;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class HitBox extends GameObject {

    private GameObject gameObject;
    private Rectangle2D rectangle;
    private float size = 10;

    public HitBox setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
        return this;
    }

    public HitBox(Step step, float x, float y, Orientation orientation) {
        super(step, x, y, orientation);
        rectangle = new Rectangle2D.Float(getX() - size / 2, getY() - size / 2, size, size);
    }

    @Override
    public void render(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.rgb(255, 0, 255));
        graphicsContext.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        graphicsContext.setFill(Color.rgb(0, 0, 0));
        graphicsContext.fillText(getId() + "", rectangle.getCenterX(), rectangle.getCenterY());
    }

    @Override
    public void update(double delta) {
        if (gameObject instanceof LineObject) {
            setX(((LineObject) gameObject).endX);
            setY(((LineObject) gameObject).endY);
            rectangle.setRect(getX() - size / 2, getY() - size / 2, size, size);
        }

        if (gameObject instanceof HookObject) {
            switch (getStep().directionAfterCut) {
                case UP:
                    setX(gameObject.getX());
                    setY(gameObject.getY() - Config.CIRCLE_RADIUS * 2);
                    break;
                case DOWN:
                    setX(gameObject.getX());
                    setY(gameObject.getY() + Config.CIRCLE_RADIUS * 2);
                    break;
                case LEFT:
                    setX(gameObject.getX() - Config.CIRCLE_RADIUS * 2);
                    setY(gameObject.getY());
                    break;
                case RIGHT:
                    setX(gameObject.getX() + Config.CIRCLE_RADIUS * 2);
                    setY(gameObject.getY());
                    break;
            }
            rectangle.setRect(getX() - size / 2, getY() - size / 2, size, size);
        }
    }

    public boolean contact(HitBox b) {
        if (getStep() != b.getStep()) {
            if (rectangle.intersects(b.rectangle)) {
                return true;
            }
        }
        return false;
    }
}
