

public class CentroidDNA implements Serializable {

    private ArrayList<ArrayList<int>> strand;
    private ArrayList<ArrayList<int>> newStrand;
    
    public double distance(String other) {
        int base;
        double dist;
        for (int i = 0; i < other.length; i++) {
            base = "ACGT".indexOf(other.charAt(i));
            dist += 1 - (this.strand[i][base] / (double) (sum(this.strand[i])));
        }
        return dist;
    }
    
    public 
    
    private int sum(ArrayList<int> list) {
        int total = 0;
        for (int val : list) {
            total += val;
        }
        return total;
    }
}