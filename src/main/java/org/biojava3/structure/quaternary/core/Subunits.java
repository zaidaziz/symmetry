
package org.biojava3.structure.quaternary.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.biojava3.structure.quaternary.geometry.MomentsOfInertia;
import org.biojava3.structure.quaternary.geometry.SuperPosition;


/**
 *
 * @author Peter
 */
public class Subunits {
    private List<Point3d[]> caCoords = Collections.emptyList();
//    private List<Point3d[]> cbCoords = new ArrayList<Point3d[]>();
    private List<Integer> sequenceClusterIds = Collections.emptyList();
    private List<String> chainIds = Collections.emptyList();
    private List<Integer> modelNumbers =  Collections.emptyList();
    private List<Integer> folds = Collections.emptyList();
    private List<Point3d> originalCenters = new ArrayList<Point3d>();
    private List<Point3d> centers = new ArrayList<Point3d>();
    private List<Vector3d> unitVectors = new ArrayList<Vector3d>();

    private Point3d centroid;
    private MomentsOfInertia momentsOfInertia = new MomentsOfInertia();

//    public Subunits(List<Point3d[]> caCoords, List<Point3d[]> cbCoords, List<Integer> sequenceClusterIds, List<Integer> folds) {
      public Subunits(List<Point3d[]> caCoords, List<Integer> sequenceClusterIds, List<Integer> folds, List<String> chainIds, List<Integer> modelNumbers) {
        this.caCoords = caCoords;
//        this.cbCoords = cbCoords;
        this.sequenceClusterIds = sequenceClusterIds;
        this.folds = folds;
        this.chainIds = chainIds;
        this.modelNumbers = modelNumbers;
    }

    public List<Point3d[]> getTraces() {
        return caCoords;
    }
    
//    public List<Point3d[]> getCBCoords() {
//        return cbCoords;
//    }

    public int getSubunitCount() {
        run();
        return centers.size();
    }
    
    public List<Integer> getSequenceClusterIds() {
    	return sequenceClusterIds;
    }
    
    public List<String> getChainIds() {
    	return chainIds;
    }
    
    public List<Integer> getModelNumbers() {
    	return modelNumbers;
    }
    
    public List<Integer>getFolds() {
    	return folds;
    }
    
    public int getCalphaCount() {
    	int count = 0;
    	for (Point3d[] trace: caCoords) {
    		count += trace.length;
    	}
    	return count;
    }
    
    public int getLargestSubunit() {
    	int index = -1;
    	int maxLength = 0;
    	for (int i = 0; i < caCoords.size(); i++) {
    		int length = caCoords.get(i).length;
    		if (length > maxLength) {
    			index = i;
    		}
    	}
    	return index;
    }

    public List<Point3d> getCenters() {
        run();
        return centers;
    }
    
    public List<Vector3d> getUnitVectors() {
        run();
        return unitVectors;
    }

    public List<Point3d> getOriginalCenters() {
        run();
        return originalCenters;
    }

    public Point3d getCentroid() {
        run();
        return centroid;
    }

    public MomentsOfInertia getMomentsOfInertia() {
    	run();
    	return momentsOfInertia;
    }
    
    private void run() {
        if (centers.size() > 0) {
            return;
        }
        calcOriginalCenters();
        calcCentroid();
        calcCenters();
        calcMomentsOfIntertia();
    }

    private void calcOriginalCenters() {
        for (Point3d[] trace: caCoords) {
            Point3d com = SuperPosition.centroid(trace);
            originalCenters.add(com);
        }
    }

    private void calcCentroid() {
        Point3d[] orig = originalCenters.toArray(new Point3d[originalCenters.size()]);
        centroid = SuperPosition.centroid(orig);
    }

    private void calcCenters() {
        for (Point3d p: originalCenters) {
            Point3d c = new Point3d(p);
            c.sub(centroid);
            centers.add(c);
            Vector3d v = new Vector3d(c);
            v.normalize();
            unitVectors.add(v);
        }
    }
    
    public Point3d getLowerBound() {
    	Point3d lower = new Point3d();
    	for (Point3d p: centers) {
    		if (p.x < lower.x) {
    			lower.x = p.x;
    		}
    		if (p.y < lower.y) {
    			lower.y = p.y;
    		}
    		if (p.z < lower.z) {
    			lower.z = p.z;
    		}
    	}
    	return lower;
    }
    
    public Point3d getUpperBound() {
    	Point3d upper = new Point3d();
    	for (Point3d p: centers) {
    		if (p.x > upper.x) {
    			upper.x = p.x;
    		}
    		if (p.y > upper.y) {
    			upper.y = p.y;
    		}
    		if (p.z > upper.z) {
    			upper.z = p.z;
    		}
    	}
    	return upper;
    }
    
    private void calcMomentsOfIntertia() {
    	for (Point3d[] trace: caCoords) {
    		for (Point3d p: trace) {
    			momentsOfInertia.addPoint(p, 1.0f);
    		}
    	}
    }
}
