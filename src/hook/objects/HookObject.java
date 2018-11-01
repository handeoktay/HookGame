package hook.objects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.ArcType;
import hook.Config;
import hook.models.GameObject;
import hook.models.Direction;

public class HookObject extends GameObject {
    private Direction toWhere;
    private float startAngle, tempX, tempY;
    private double arcExtend;
    private float w, angle;


    public HookObject(Step parentStep, float x, float y, Direction toWhere) {
        super(parentStep, x, y, parentStep.getLastObjectOrientation());
        this.toWhere = toWhere;
        arcExtend = Config.ARC_EXTEND;
    }

    @Override
    public void render(GraphicsContext graphicsContext) {
        if (!isDestroyed) {
            graphicsContext.setStroke(Config.PRIMARY_COLOR);
            graphicsContext.setLineWidth(Config.THICK_LINE_STROKE);
            graphicsContext.strokeArc(tempX, tempY, 2 * Config.CIRCLE_RADIUS, 2 * Config.CIRCLE_RADIUS, startAngle, arcExtend, ArcType.OPEN);
        }
    }

    @Override
    public void update(double delta) {
        if (!isDestroyed) {
            if (getStep().isRunning) {
                switch (getStep().directionAfterCut) {
                    case UP:
                        setY(getY() + getSpeed());
                        tempY = (getY() - 2 * Config.CIRCLE_RADIUS);
                        tempX = getX() - Config.CIRCLE_RADIUS;

                        if (tempY + 2 * Config.CIRCLE_RADIUS >= getStep().cutterObject.getY()) {
                            w = getStep().cutterObject.getY() - (tempY + Config.CIRCLE_RADIUS);
                            angle = (float) Math.toDegrees(Math.acos(w / Config.CIRCLE_RADIUS));
                            if (!Float.isNaN(angle)) {
                                if (toWhere == Direction.LEFT) {
                                    startAngle = 90;
                                    arcExtend = 180 - angle;
                                } else {
                                    startAngle = (270 + angle) % 360;
                                    arcExtend = 180 - angle;
                                }
                                if (arcExtend <= 0) {
                                    arcExtend = 0;
                                    isDestroyed = true;
                                }
                            }
                        } else {
                            startAngle = (toWhere == Direction.RIGHT) ? 270 : 90;
                        }

                        break;
                    case DOWN:
                        setY(getY() - getSpeed());
                        tempY = getY();
                        tempX = getX() - Config.CIRCLE_RADIUS;

                        if (tempY <= getStep().cutterObject.getY()) {
                            w = tempY + Config.CIRCLE_RADIUS - getStep().cutterObject.getY();
                            angle = (float) Math.toDegrees(Math.acos(w / Config.CIRCLE_RADIUS));

                            if (!Float.isNaN(angle)) {
                                if (toWhere == Direction.LEFT) {
                                    startAngle = 90 + angle;
                                    arcExtend = 180 - angle;
                                } else {
                                    startAngle = 270;
                                    arcExtend = 180 - angle;
                                }
                                if (arcExtend <= 0) {
                                    arcExtend = 0;
                                    isDestroyed = true;
                                }
                            }
                        } else {
                            startAngle = (toWhere == Direction.RIGHT) ? 270 : 90;
                        }


                        break;
                    case LEFT:
                        setX(getX() + getSpeed());
                        tempX = (getX() - 2 * Config.CIRCLE_RADIUS);
                        tempY = getY() - Config.CIRCLE_RADIUS;
                        startAngle = (toWhere == Direction.UP) ? 0 : 180;

                        if (tempX + Config.CIRCLE_RADIUS * 2 > getStep().cutterObject.getX()) {
                            w = getStep().cutterObject.getX() - (tempX + Config.CIRCLE_RADIUS);
                            angle = (float) Math.toDegrees(Math.acos(w / Config.CIRCLE_RADIUS));

                            if (!Float.isNaN(angle)) {
                                if (toWhere == Direction.UP) {
                                    startAngle = angle;
                                    arcExtend = 180 - angle;
                                } else {
                                    startAngle = 180;
                                    arcExtend = 180 - angle;
                                }
                                if (arcExtend <= 0) {
                                    arcExtend = 0;
                                    isDestroyed = true;
                                }
                            }
                        } else {
                            startAngle = (toWhere == Direction.UP) ? 0 : 180;
                        }


                        break;
                    case RIGHT:
                        setX(getX() - getSpeed());
                        tempX = (getX());
                        tempY = getY() - Config.CIRCLE_RADIUS;
                        if (tempX < getStep().cutterObject.getX() + 3) {
                            w = (tempX + Config.CIRCLE_RADIUS) - getStep().cutterObject.getX();
                            angle = (float) Math.toDegrees(Math.acos(w / Config.CIRCLE_RADIUS));
                            if (!Float.isNaN(angle)) {
                                if (toWhere == Direction.UP) {
                                    arcExtend = 180 - angle;
                                } else {
                                    startAngle = 180 + angle;
                                    if (startAngle >= 360) startAngle = 360;
                                    arcExtend = 180 - angle;
                                }
                                if (arcExtend <= 0) {
                                    arcExtend = 0;
                                    isDestroyed = true;
                                }
                            }

                        } else {
                            startAngle = (toWhere == Direction.UP) ? 0 : 180;
                        }

                        if (tempX + 2 * Config.CIRCLE_RADIUS < getStep().cutterObject.getX()) {
                            startAngle = 0;
                            arcExtend = 0;
                        }
                        break;
                }
            }
        }
    }
}
