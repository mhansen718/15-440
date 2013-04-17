import java.io.Serializable;


public class MemberRecord implements Serializable {

	private static final long serialVersionUID = 14285278597278330L;
	
	public String firstName;
	public String lastName;
	public int birthyear;
	
	@Override
	public boolean equals(Object obj) {
		return (this.firstName.equals(((MemberRecord) obj).firstName) &&
				this.lastName.equals(((MemberRecord) obj).lastName) &&
				this.birthyear == ((MemberRecord) obj).birthyear);
	}
	
	@Override
	public int hashCode() {
		return this.birthyear + ((this.firstName).length() ^ (this.lastName.length()));
	}
	
	
}
