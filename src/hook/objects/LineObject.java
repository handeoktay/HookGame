package hook.objects;

import javafx.scene.canvas.GraphicsContext;
import hook.Config;
import hook.models.Orientation;
import hook.models.GameObject;
import hook.models.LineType;

/**
 * Created by IntelliJ IDEA.
 * User: Hande OKTAY
 * Date: 17/05/18
 * Time: 19:50
 */

public class LineObject extends GameObject {

    private LineType lineType;
    public float endX, endY;

    public LineObject(Step parentStep, float startX, float startY, float endX, float endY, Orientation orientation) {
        super(parentStep, startX, startY, orientation);
        this.endX = endX;
        this.endY = endY;
        this.lineType = (getStep().isCutted) ? LineType.THICK : LineType.THIN;
    }

    @Override
    public void render(GraphicsContext graphicsContext) {

        if (!isDestroyed) {

            graphicsContext.setStroke(Config.PRIMARY_COLOR);
            graphicsContext.setLineWidth((lineType == LineType.THICK) ? Config.THICK_LINE_STROKE : Config.THIN_LINE_STROKE);

            if (lineType == LineType.THICK) {
                graphicsContext.strokeLine(getX(), getY(), endX, endY);
            } else {
                graphicsContext.strokeLine(getX(), getY(), endX, endY);
            }
        }
    }

    @Override
    public void update(double delta) {

        if (!isDestroyed) {
            if (lineType == LineType.THICK) {
                switch (getStep().directionAfterCut) {
                    case UP:
                        if (endY >= getStep().cutterObject.getY()) {
                            if (getStep().lastObjectsId == getId()) {
                                getStep().fadeOut();
                            }
                            isDestroyed = true;
                        }

                        if (getStep().isRunning) {
                            if (getY() < getStep().cutterObject.getY())
                                setY(getY() + getSpeed());
                            if (endY < getStep().cutterObject.getY())
                                endY += getSpeed();
                        }
                        break;
                    case DOWN:
                        if (endY <= getStep().cutterObject.getY()) {
                            if (getStep().lastObjectsId == getId()) {
                                getStep().fadeOut();
                            }
                            isDestroyed = true;
                        }
                        if (getStep().isRunning) {
                            if (getY() > getStep().cutterObject.getY())
                                setY(getY() - getSpeed());
                            if (endY > getStep().cutterObject.getY())
                                endY -= getSpeed();
                        }
                        break;
                    case LEFT:
                        if (endX >= getStep().cutterObject.getX()) {
                            if (getStep().lastObjectsId == getId()) {
                                getStep().fadeOut();
                            }
                            isDestroyed = true;
                        }
                        if (getStep().isRunning) {
                            if (getX() < getStep().cutterObject.getX())
                                setX(getX() + getSpeed());

                            if (endX < getStep().cutterObject.getX())
                                endX += getSpeed();
                        }
                        break;
                    case RIGHT:
                        if (endX <= getStep().cutterObject.getX()) {
                            if (getStep().lastObjectsId == getId()) {
                                getStep().fadeOut();
                            }
                            isDestroyed = true;
                        }
                        if (getStep().isRunning) {
                            if (getStep().cutterObject.getX() < getX())
                                setX(getX() - getSpeed());

                            if (getStep().cutterObject.getX() < endX)
                                endX -= getSpeed();
                        }
                        break;
                }
            }
        }
    }
}
