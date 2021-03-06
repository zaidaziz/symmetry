/**
 * 
 */
package org.biojava3.structure.quaternary.jmolScript;

import org.biojava3.structure.quaternary.core.AxisTransformation;
import org.biojava3.structure.quaternary.geometry.Tetrahedron;


/**
 * @author Peter
 *
 */
public class JmolSymmetryScriptGeneratorT extends JmolSymmetryScriptGenerator {

	public JmolSymmetryScriptGeneratorT(AxisTransformation axisTransformation) {
		super(axisTransformation);
		double radius = Math.max(axisTransformation.getDimension().z, axisTransformation.getXYRadius());
		Tetrahedron t = new Tetrahedron();
		t.setMidRadius(radius);
		setPolyhedron(t);
	}
	
	public int getZoom() {
		// find maximum extension of structure
		double maxExtension = getMaxExtension();
		// find maximum extension of polyhedron
		double polyhedronExtension = getPolyhedron().getCirumscribedRadius();
		
		int zoom = Math.round((float)(maxExtension/polyhedronExtension * 110));
		if (zoom > 100) {
			zoom = 100;
		}
		return zoom;
	}
	
}
