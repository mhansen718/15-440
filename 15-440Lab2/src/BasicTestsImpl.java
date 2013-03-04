
public class BasicTestsImpl implements BasicTests {

	private String phrase;
	private int number;
	
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

}
