import java.awt.geom.Point2D;


public class CentroidPoint extends Point2D.Double {

	private static final long serialVersionUID = 4241766081924964970L;
	private double dx;
	private double dy;
	private int numB;
	
	public CentroidPoint() {
		super();
		this.dx = 0;
		this.dy = 0;
		this.numB = 0;
	}
	
	public CentroidPoint(double x, double y) {
		super(x, y);
		this.dx = 0;
		this.dy = 0;
		this.numB = 0;
	}
	
	/* Adds a point to the cluster */
	public void addPoint(Point2D.Double p) {
		this.dx += p.getX();
		this.dy += p.getY();
		this.numB++;
		return;
	}
	
	/* Normalize to the cluster and determine if the change was minimal */
	public boolean remean() {
		this.dx /= this.numB;
		this.dy /= this.numB;
		
		/* Translate the centroid */
		Point2D.Double old = new Point2D.Double(this.getX(), this.getY());
		this.setLocation(this.dx, this.dy);
		
		/* Reset the cluster */
		this.dx = 0;
		this.dy = 0;
		this.numB = 0;
		
		return (this.distance(old) == 0);
	}

}
