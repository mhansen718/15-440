import java.io.Serializable;
import java.util.ArrayList;



public class CentroidDNA implements Serializable {

	private static final long serialVersionUID = -6603084775267021628L;
	
    private ArrayList<ArrayList<Double>> strand;
    private ArrayList<ArrayList<Integer>> newStrand;

	public CentroidDNA(String DNA) {
        int base;
        
        this.strand = new ArrayList<ArrayList<Double>>();
        
        for (int i = 0; i < DNA.length(); i++) {
            this.strand.add(new ArrayList<Double>());
            base = "ACGT".indexOf(DNA.charAt(i));
            for (int j = 0; j < 4; j++) {
                if (j == base) {
                    this.strand[i].add(1);
                } else {
                    this.strand[i].add(0);
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
        ArrayList<ArrayList<int>> temp;
        for (int i = 0; i < this.strand.length; i++) {
            for (int j = 0; j < 4; j++) {
                dist += Math.abs(this.strand[i][j] - this.newStrand[i][j]);
            }
        }
        temp = this.newStrand.clone();
        this.newStrand = new ArrayList<ArrayList<int>>();
        double total;
        for (i = 0; i < this.strand.length; i++) {
            this.newStrand.add(new ArrayList<int>());
            total = (double) sum(temp[i]);
            for (j = 0; j < 4; j++) {
                this.newStrand[i].add(0);
                this.strand[i][j] = temp[i][j] / total;
            }
        }
        return (dist == 0);
    }    
    
    public String output() {
        String DNA = "";
        for (int i = 0; i < this.strand.length; i++) {
            DNA += "ACGT".charAt(maxIndex(this.strand[i]));
        }
        return DNA;
    }
    
    private int max(ArrayList<double> list) {
        int maxIndex = 0;
        for (int i = 1; i < list.length; i++) {
            if (list[i] > list[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }
    
    private int sum(ArrayList<int> list) {
        int total = 0;
        for (int val : list) {
            total += val;
        }
        return total;
    }
}