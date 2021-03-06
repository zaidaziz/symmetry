package org.biojava3.structure.quaternary.core;

import java.util.ArrayList;
import java.util.List;

import org.biojava.bio.structure.Atom;
import org.biojava.bio.structure.Group;

public class UniqueSequenceList {
	private String sequenceString = "";
	private String seqResSequence = "";
    private List<Integer> alignment1 = null;
    private List<Integer> alignment2 = null;
    private List<Atom[]> caAtoms = new ArrayList<Atom[]>();
    private List<String> chainIds = new ArrayList<String>();
    private List<Integer> modelNumbers = new ArrayList<Integer>();
    private List<Integer> structureIds = new ArrayList<Integer>();
    
    public UniqueSequenceList(Atom[] cAlphaAtoms, String chainId, int modelNumber, int structureId, String seqResSequence) {
    	this.caAtoms.add(cAlphaAtoms);
    	this.chainIds.add(chainId);
    	this.modelNumbers.add(modelNumber);
    	this.structureIds.add(structureId);
    	this.seqResSequence = seqResSequence;
    	this.sequenceString =  getSequenceString(cAlphaAtoms);
    	this.alignment1 = new ArrayList<Integer>(cAlphaAtoms.length);
    	for (int i = 0; i < cAlphaAtoms.length; i++) {
    		this.alignment1.add(i);
    	}
    	this.alignment2 = alignment1;
    }
    
    /**
     * Return true is the sequence and residues numbers of the passed in array of
     * atoms matches those of this unique sequence list
     * 
     * @param caAlphaAtoms
     * @return
     */
    public boolean isMatch(Atom[] caAlphaAtoms) {
    	return sequenceString.equals(getSequenceString(caAlphaAtoms));
    }
    
    public void addChain(Atom[] cAlphaAtoms, String chainId, int modelNumber, int structureId) {
    	this.caAtoms.add(cAlphaAtoms);
    	this.chainIds.add(chainId); 
    	this.modelNumbers.add(modelNumber);
    	this.structureIds.add(structureId);
    }
    
    public int getChainCount() {
    	return caAtoms.size();
    }
    
    public Atom[] getChain(int index) {
    	return caAtoms.get(index);
    }
    
    public String getChainId(int index) {
    	return chainIds.get(index);
    }
    
    public int getModelNumber(int index) {
    	return modelNumbers.get(index);
    }
     
    public int getStructureId(int index) {
    	return structureIds.get(index);
    }
    
    public Atom[] getReferenceChain() {
    	return caAtoms.get(0);
    }
    
     /**
	 * @return the sequenceString
	 */
	public String getSequenceString() {
		return sequenceString;
	}
	
	public String getSeqResSequence() {
		return seqResSequence;
	}
	
	/**
	 * @param sequenceString the sequenceString to set
	 */
	public void setSequenceString(String sequenceString) {
		this.sequenceString = sequenceString;
	}
	/**
	 * @return the alignment1
	 */
	public List<Integer> getAlignment1() {
		return alignment1;
	}
	/**
	 * @param alignment1 the alignment1 to set
	 */
	public void setAlignment1(List<Integer> alignment1) {
		this.alignment1 = alignment1;
	}
	/**
	 * @return the alignment2
	 */
	public List<Integer> getAlignment2() {
		return alignment2;
	}
	/**
	 * @param alignment2 the alignment2 to set
	 */
	public void setAlignment2(List<Integer> alignment2) {
		this.alignment2 = alignment2;
	}
	
	public static String getSequenceString(Atom[] caAlphaAtoms) {
		StringBuilder builder = new StringBuilder();

		for (Atom a:  caAlphaAtoms) {
			Group g = a.getGroup();
			if (! g.getPDBName().equals("UNK")) {
				builder.append(g.getResidueNumber());
				builder.append(g.getPDBName());
			}
		}
		
//		System.out.println("getSequenceString: " + builder.toString());
		return builder.toString();
	}
     
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("#: ");
		builder.append(caAtoms.size());
		builder.append(" seq: ");
		builder.append(sequenceString);
		builder.append("\n");
		builder.append(alignment1);
		builder.append("\n");
		builder.append(alignment2);
		return builder.toString();
	}
	
}
