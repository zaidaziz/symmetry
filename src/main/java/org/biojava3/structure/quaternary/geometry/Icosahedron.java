package org.biojava3.structure.quaternary.geometry;

import java.util.Arrays;
import java.util.List;

import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;


public class Icosahedron implements Polyhedron {
	private static int[] lineLoop1 = {4,0,1,2,3,4,5,1};
	private static int[] lineLoop2 = {3,0,2};
	private static int[] lineLoop3 = {0,5};
	private static int[] lineLoop4 = {11,3,7,4,8,6,7,8,9,10,11,7};
	private static int[] lineLoop5 = {6,11,2,10,1,9,6,10};
	private static int[] lineLoop6 = {8,5,9};

	private double circumscribedRadius = 1.0;

	/**
	 * Returns the radius of a circumscribed sphere, that goes
	 * through all vertices
	 * @return the cirumscribedRadius
	 */
	public double getCirumscribedRadius() {
		return circumscribedRadius;
	}

	/**
	 * Set the radius of a circumscribed sphere, that goes
	 * through all vertices
	 * @param cirumscribedRadius the cirumscribedRadius to set
	 */
	public void setCirumscribedRadius(double cirumscribedRadius) {
		this.circumscribedRadius = cirumscribedRadius;
	}
	/**
	 * Returns the radius of an inscribed sphere, that is tangent to each 
	 * of the icosahedron's faces
	 * @return the inscribedRadius
	 */
	public double getInscribedRadius() {
		double side = getSideLengthFromCircumscribedRadius(circumscribedRadius);
		return getInscribedRadiusFromSideLength(side);
	}

	/**
	 * Sets the radius of an inscribed sphere, that is tangent to each 
	 * of the icosahedron's faces
	 * @param inscribedRadius the inscribedRadius to set
	 */
	public void setInscribedRadius(double radius) {
		double side = getSideLengthFromInscribedRadius(radius);
		this.circumscribedRadius = getCircumscribedRadiusFromSideLength(side);
	}

	/**
	 * Returns the radius of a sphere, that is tangent to each 
	 * of the icosahedron's edges
	 *
	 * @return the midRadius
	 */
	public double getMidRadius() {
		double side = getSideLengthFromCircumscribedRadius(circumscribedRadius);
		return getMiddleRadiusFromSideLength(side);
	}

	/**
	 * Sets the radius of radius of a sphere, that is tangent to each 
	 * of the icosahedron's edges
	 * @param midRadius the midRadius to set
	 */
	public void setMidRadius(double radius) {
		double side = getSideLengthFromMiddleRadius(radius);
		this.circumscribedRadius = getCircumscribedRadiusFromSideLength(side);
	}

	@Override
    public Point3d[] getVertices() {
		Point3d[] icosahedron = new Point3d[12];
        // see http://answers.yahoo.com/question/index?qid=20080108041441AAJCjEu
        double c = circumscribedRadius * 1 / Math.sqrt(5);
        double s = 2 * c; // golden ratio
        double c1 = Math.sqrt((3-Math.sqrt(5))/8); // cos(2Pi/5)
        double s1 = Math.sqrt((5+Math.sqrt(5))/8); // sin(2Pi/5)
        double c2 = Math.sqrt((3+Math.sqrt(5))/8); // cos(Pi/5)
        double s2 = Math.sqrt((5-Math.sqrt(5))/8); // sin(Pi/5)

        icosahedron[0] = new Point3d(0, 0, circumscribedRadius);
		icosahedron[1] = new Point3d(s, 0, c);
		icosahedron[2] = new Point3d(s*c1, s*s1, c);
		icosahedron[3] = new Point3d(-s*c2, s*s2, c);
		icosahedron[4] = new Point3d(-s*c2, -s*s2, c);
		icosahedron[5] = new Point3d(s*c1, -s*s1, c);
		for (int i = 0; i < 6; i++) {
			icosahedron[i+6] = new Point3d(icosahedron[i]);
			icosahedron[i+6].negate();
		}

		Matrix3d m = new Matrix3d();
		m.rotZ(Math.PI/10);
		for (Point3d p: icosahedron) {
			m.transform(p);
		}
		
		return icosahedron;
	};
	
	@Override
	public List<int[]> getLineLoops() {
		return Arrays.asList(lineLoop1, lineLoop2, lineLoop3, lineLoop4, lineLoop5, lineLoop6);
	}
	
	public int getViewCount() {
		return 3;
	}
	
	public String getViewName(int index) {
		String name;
		switch (index) {
		case 0:  name = "C5 axis vertex-centered";
		break;
		case 1:  name = "C3 axis face-centered";
		break;
		case 2:  name = "C2 axis edge-centered";
		break;
		default: throw new IllegalArgumentException("getViewMatrix: index out of range:" + index);
		}
        return name;
	}
	
	public Matrix3d getViewMatrix(int index) {
		Matrix3d m = new Matrix3d();
		switch (index) {
		case 0:  m.setIdentity(); // front vertex-centered
		break;
		case 1:  m.rotX(-0.6523581397843639); // back face-centered -0.5535743588970415 m.rotX(Math.toRadians(-26));
		break;
		case 2:  m.rotZ(Math.PI/2);
	          	 Matrix3d m1 = new Matrix3d();
		         m1.rotX(-1.0172219678978445);
		         m.mul(m1);
		break;
		default: throw new IllegalArgumentException("getViewMatrix: index out of range:" + index);
		}
		return m;
	}
	
	private static double getSideLengthFromInscribedRadius(double radius) {
		return radius / (Math.sqrt(3)/12 * (3 + Math.sqrt(5)));
	}
	
	private static double getInscribedRadiusFromSideLength(double sideLength) {
		return sideLength * (Math.sqrt(3)/12 * (3 + Math.sqrt(5)));
	}
	
	private static double getSideLengthFromMiddleRadius(double radius) {
		return radius * 4 /(1 + Math.sqrt(5));
	}
	
	private static double getMiddleRadiusFromSideLength(double sideLength) {
		return sideLength / 4 * (1 + Math.sqrt(5));
	}
	
	private static double getSideLengthFromCircumscribedRadius(double radius) {
		return radius * 4 / Math.sqrt(10 + 2 * Math.sqrt(5));
	}
	
	private static double getCircumscribedRadiusFromSideLength(double sideLength) {
		return sideLength / 4 * Math.sqrt(10 + 2 * Math.sqrt(5));
	}

}
