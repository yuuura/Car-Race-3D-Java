package YuriReznik.Client.views;


import java.util.ArrayList;
import java.util.Collection;

import javafx.scene.Group;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

/**
 * Background 3D
 * @author yuuura87
 *
 */
public class Background extends Group{

	public final Rotate rx = new Rotate(0, Rotate.X_AXIS);
    public final Rotate ry = new Rotate(0, Rotate.Y_AXIS);
    public final Rotate rz = new Rotate(0, Rotate.Z_AXIS);
    private PhongMaterial greyMaterial;
    private PhongMaterial whiteMaterial;
    private PhongMaterial greenMaterial;
    private PhongMaterial brownMaterial;
    
    private Group backgroundGroup;
    public static final int ROAD_LENGTH = 2185;
	
	/**
	 * 
	 */
	public Background() {
		backgroundGroup = new Group();
		getTransforms().addAll(rz, ry, rx);
		createMaterial();
		buildBackground();
	}
	
	/**
	 * Build background objects
	 */
	public void buildBackground() {
		Box road = new Box(275, ROAD_LENGTH, 1);
		road.setTranslateY(-800);
		road.setMaterial(greyMaterial);
		
		Box offRoad1 = new Box(375, ROAD_LENGTH, 1);
		offRoad1.setTranslateX(331);
		offRoad1.setTranslateY(-800);
		offRoad1.setMaterial(greenMaterial);
		
		Box offRoad2 = new Box(375, ROAD_LENGTH, 1);
		offRoad2.setTranslateX(-331);
		offRoad2.setTranslateY(-800);
		offRoad2.setMaterial(greenMaterial);
		
		Collection<Cylinder> tree_1 = buildATree(200, -40);
		Collection<Cylinder> tree_2 = buildATree(350, -800);
		Collection<Cylinder> tree_3 = buildATree(-190, -350);
		Collection<Cylinder> tree_4 = buildATree(-380, -900);
		Collection<Cylinder> tree_5 = buildATree(-190, 20);
		Collection<Rectangle> lines = buildLines();
		
		backgroundGroup.getChildren().addAll(road, offRoad1, offRoad2);
		backgroundGroup.getChildren().addAll(3, tree_1);
		backgroundGroup.getChildren().addAll(4, tree_2);
		backgroundGroup.getChildren().addAll(5, tree_3);
		backgroundGroup.getChildren().addAll(6, tree_4);
		backgroundGroup.getChildren().addAll(7, tree_5);
		backgroundGroup.getChildren().addAll(8, lines);
		
		getChildren().add(backgroundGroup);
	}
	
	/**
	 * Create background material
	 */
	public void createMaterial() {
		greyMaterial = new PhongMaterial();
		greyMaterial.setDiffuseColor(Color.DARKGREY);
        greyMaterial.setSpecularColor(Color.GREY);
        
        whiteMaterial = new PhongMaterial();
        whiteMaterial.setDiffuseColor(Color.WHITESMOKE);
        
        greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.LIGHTGREEN);
        
        brownMaterial = new PhongMaterial();
        brownMaterial.setDiffuseColor(Color.BROWN);
        brownMaterial.setSpecularColor(Color.SADDLEBROWN);
	}
	
	/**
	 * Build lines on road objects
	 * @return
	 */
	public Collection<Rectangle> buildLines() {
		
		Collection<Rectangle> lines = new ArrayList<Rectangle>();
		int index = -1900;
		for(int i = 0 ; i < 73; i ++) {
			
			Rectangle l1 = new Rectangle(7, 15, Color.WHITESMOKE);
			l1.setTranslateX(137);
			l1.setTranslateY(index);
			lines.add(l1);
			Rectangle l2 = new Rectangle(7, 15, Color.WHITESMOKE);
			l2.setTranslateX(-144);
			l2.setTranslateY(index);
			lines.add(l2);
			index += 15;
			Rectangle l3 = new Rectangle(7, 15, Color.RED);
			l3.setTranslateX(137);
			l3.setTranslateY(index);
			lines.add(l3);
			Rectangle l4 = new Rectangle(7, 15, Color.RED);
			l4.setTranslateX(-144);
			l4.setTranslateY(index);
			lines.add(l4);
			index += 15;
		}
		index = -1900;
		for(int i = 0 ; i < 73; i ++) {
			Rectangle l1 = new Rectangle(3, 10, Color.WHITESMOKE);
			l1.setTranslateX(83);
			l1.setTranslateY(index);
			l1.setTranslateZ(-1);
			lines.add(l1);
			Rectangle l2 = new Rectangle(3, 10, Color.WHITESMOKE);
			l2.setTranslateX(28);
			l2.setTranslateY(index);
			l2.setTranslateZ(-1);
			lines.add(l2);
			Rectangle l3 = new Rectangle(3, 10, Color.WHITESMOKE);
			l3.setTranslateX(-27);
			l3.setTranslateY(index);
			l3.setTranslateZ(-1);
			lines.add(l3);
			Rectangle l4 = new Rectangle(3, 10, Color.WHITESMOKE);
			l4.setTranslateX(-82);
			l4.setTranslateY(index);
			l4.setTranslateZ(-1);
			lines.add(l4);
			index += 30;
		}
		return lines;
	}
	
	/**
	 * Build trees objects
	 * @param x
	 * @param y
	 * @return
	 */
	public Collection<Cylinder> buildATree(int x, int y) {
		int treeHeight = 29;
		final Cylinder tree1 = new Cylinder(2, treeHeight);
		tree1.setTranslateX(x);
		tree1.setTranslateY(y);
		tree1.setTranslateZ(-(treeHeight/2));
		tree1.setRotationAxis(Rotate.X_AXIS);
		tree1.setRotate(-90);
		tree1.setMaterial(brownMaterial);
		
		Collection<Cylinder> listCylinder = new ArrayList<Cylinder>();
		listCylinder.add(tree1);
		int zIndex = -30, radius = 20;
		for(int i = 0 ; i < 20; i ++) {
			Cylinder tree2 = new Cylinder(radius--, 1);
			tree2.setTranslateX(x);
			tree2.setTranslateY(y);
			tree2.setTranslateZ(zIndex);
			zIndex -= 2;
			tree2.setRotationAxis(Rotate.X_AXIS);
			tree2.setRotate(-90);
			tree2.setMaterial(greenMaterial);
			listCylinder.add(tree2);
		}
		return listCylinder;
	}
}
