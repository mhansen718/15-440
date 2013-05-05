import java.io.Serializable;

public class CentroidDNA implements Serializable {

	private static final long serialVersionUID = -6603084775267021628L;
	
    private double[][] strand;
    private int[][] newStrand;

	public CentroidDNA(String DNA) {
        int base;
        
        this.strand = new double[DNA.length()][4];
        this.newStrand = new int[DNA.length()][4];
        
        for (int i = 0; i < DNA.length(); i++) {
            this.newStrand[i] = {0,0,0,0};
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
        double dist;
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
                dist += Math.abs(this.strand[i][j] - this.newStrand[i][j]);
            }
        }
        temp = this.newStrand.clone();
        this.newStrand = new int[this.newStrand.length][4];
        double total;
        for (i = 0; i < this.strand.length; i++) {
            this.newStrand[i] = {0,0,0,0};
            total = (double) sum(temp[i]);
            for (j = 0; j < 4; j++) {
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
}