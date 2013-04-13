import java.io.Serializable;


public class MemberRecord implements Serializable {

	private static final long serialVersionUID = 14285278597278330L;
	
	public String self;
	public int birthyear;
	
	public boolean equals(MemberRecord other) {
		return (this.self.equals(other.self) && (this.birthyear == other.birthyear));
	}
}
