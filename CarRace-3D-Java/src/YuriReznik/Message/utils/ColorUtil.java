package YuriReznik.Message.utils;


/**
 * Useful utilities for color conversion
 */
public class ColorUtil {

    public static java.awt.Color fxToAwt(javafx.scene.paint.Color color){
        return new java.awt.Color((float)color.getRed(), (float)color.getGreen(), (float)color.getBlue(), (float)color.getOpacity());
    }

    public static javafx.scene.paint.Color awtToFx(java.awt.Color color){
        return new javafx.scene.paint.Color(color.getRed()/255.0, color.getGreen()/255.0, color.getBlue()/255.0, color.getAlpha()/255.0);
    }
}