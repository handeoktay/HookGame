package hook.objects;

import javafx.scene.canvas.GraphicsContext;
import hook.Config;
import hook.models.GameObject;

/**
 * Created by IntelliJ IDEA.
 * User: Hande OKTAY
 * Date: 17/05/18
 * Time: 19:13
 */

public class CutterObject extends GameObject {

    public boolean shouldFadeOut = false;
    boolean shouldExtend = false;
    float cutterLength;

    public CutterObject(Step parentStep, float x, float y) {
        super(parentStep, x, y, parentStep.getLastObjectOrientation());
        cutterLength = Config.CUTTER_LENGTH;
    }

    @Override
    public void render(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Config.PRIMARY_COLOR);
        switch (getOrientation()) {
            case HORIZONTAL:
                graphicsContext.fillRoundRect(
                        getX() - cutterLength / 2f,
                        getY() - (Config.THICK_LINE_STROKE + 2) / 2f,
                        cutterLength,
                        Config.THICK_LINE_STROKE + 2,
                        Config.CUTTER_CORNER_RADIUS,
                        Config.CUTTER_CORNER_RADIUS);
                break;
            case VERTICAL:
                graphicsContext.fillRoundRect(
                        getX() - (Config.THICK_LINE_STROKE + 2) / 2f,
                        getY() - cutterLength / 2f,
                        Config.THICK_LINE_STROKE + 2,
                        cutterLength,
                        Config.CUTTER_CORNER_RADIUS,
                        Config.CUTTER_CORNER_RADIUS);
                break;
        }
    }

    @Override
    public void update(double delta) {
        if (shouldExtend) {
            cutterLength += 5f;
            if (cutterLength >= Config.CUTTER_LENGTH * 3f) {
                shouldExtend = false;
            }
        }

        if (shouldFadeOut) {
            cutterLength = (float) (cutterLength * getStep().opacity);
        }
    }
}
