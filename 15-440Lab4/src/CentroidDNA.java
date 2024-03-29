import java.io.Serializable;
import java.util.Arrays;

public class CentroidDNA implements Serializable {

	private static final long serialVersionUID = -6603084775267021628L;
	
    private double[][] strand;
    private int[][] newStrand;

	public CentroidDNA(String DNA) {
        int base;
        
        this.strand = new double[DNA.length()][4];
        this.newStrand = new int[DNA.length()][4];
        
        for (int i = 0; i < DNA.length(); i++) {
            this.newStrand[i] = new int[] {0,0,0,0};
            base = "ACGT".indexOf(DNA.charAt(i));
            for (int j = 0; j < 4; j++) {
                if (j == base) {
                    this.strand[i][j] = 1;
                } else {
                    this.strand[i][j] = 0;
                }
            }
        }
    }
    
    public double distance(String other) {
        int base;
        double dist = 0;
        for (int i = 0; i < other.length(); i++) {
            base = "ACGT".indexOf(other.charAt(i));
            dist += 1 - this.strand[i][base];
        }
        return dist;
    }
    
    public void addPoint(String dna) {
        for (int i = 0; i < dna.length(); i++) {
            this.newStrand[i]["ACGT".indexOf(dna.charAt(i))]++;
        }
    }
    
    public boolean remean() {
        int dist = 0;
        int[][] temp;
        for (int i = 0; i < this.strand.length; i++) {
            for (int j = 0; j < 4; j++) {
                dist += Math.abs(this.strand[i][j] - (this.newStrand[i][j]/(double) sum(this.newStrand[i])));
            }
        }
        temp = this.newStrand.clone();
        this.newStrand = new int[this.newStrand.length][4];
        double total;
        for (int i = 0; i < this.strand.length; i++) {
            this.newStrand[i] = new int[] {0,0,0,0};
            total = (double) sum(temp[i]);
            for (int j = 0; j < 4; j++) {
                this.strand[i][j] = temp[i][j] / total;
            }
        }
        return (dist == 0);
    }    
    
    public String output() {
        String DNA = "";
        for (int i = 0; i < this.strand.length; i++) {
            DNA += "ACGT".charAt(max(this.strand[i]));
        }
        return DNA;
    }
    
    public void combine(CentroidDNA other) {
        int[][] otherNewStrand = other.getNewStrand();
        for (int i = 0; i < this.newStrand.length; i++) {
            for (int j = 0; j < 4; j++) {
                this.newStrand[i][j] += otherNewStrand[i][j];
            }
        }
    }
    
    public int[][] getNewStrand() {
        return this.newStrand;
    }
    
    private int max(double[] list) {
        int maxIndex = 0;
        for (int i = 1; i < list.length; i++) {
            if (list[i] > list[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }
    
    private int sum(int[] list) {
        int total = 0;
        for (int val : list) {
            total += val;
        }
        return total;
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CentroidDNA other = (CentroidDNA) obj;
		for (int i = 0; i < this.strand.length; i++) {
			if (!Arrays.equals(strand[i], other.strand[i]))
				return false;
		}
		return true;
	}
    
    
}