package YuriReznik.Client.views;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
 
public class CameraRotate extends Group {
 
    public Translate t  = new Translate();
    public Rotate rx = new Rotate();
    { rx.setAxis(Rotate.X_AXIS); }
    public Rotate ry = new Rotate();
    { ry.setAxis(Rotate.Y_AXIS); }
    public Rotate rz = new Rotate();
    { rz.setAxis(Rotate.Z_AXIS); }

    public CameraRotate() {
        super();
        getTransforms().addAll(t, rz, ry, rx);
    }
    public void setRotateX(double x) { rx.setAngle(x); }
    public void setRotateY(double y) { ry.setAngle(y); }
    public void setRotateZ(double z) { rz.setAngle(z); }
}