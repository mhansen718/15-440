
public class BasicTestsImpl implements BasicTests {

	private String phrase;
	private int number;
	
	/* Here are a bunch of straight forward functions */
	public BasicTestsImpl(String phrase, int number) {
		this.phrase = phrase;
		this.number = number;
	}
	
	@Override
	public String getPhrase() {
		return this.phrase;
	}

	@Override
	public int getNumber() {
		return this.number;
	}

	@Override
	public void addToNumber(int value) {
		this.number = this.number + value;
		return;
	}
	
	@Override
	public void div(int value) {
		this.number = this.number / value;
		return;
	}
	
	@Override
	public String toString() {
		return "   BasicTest Object: phrase = '" + phrase + "' number = " + number;
	}

}
