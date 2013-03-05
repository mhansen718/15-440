
public class AdvancedTestsImpl implements AdvancedTests {

	private Shipper ship;
	private Shipped goods;
	
	public AdvancedTestsImpl() {
		super();
		this.ship = new Shipper();
		this.goods = new Shipped();
	}
	
	/* A bunch cool object moving and such methods, in RMI, they will do lots of stuff exercising remote refs */
	@Override
	public BasicTests makeNewTests(String phrase, int number) {
		BasicTestsImpl basic = new BasicTestsImpl(phrase, number);
		return basic;
	}

	@Override
	public void changeNumber(BasicTests tests, int value) {
		int currentValue = tests.getNumber();
		tests.addToNumber(value - currentValue);
		return;
	}

	@Override
	public BasicTests giveBack(BasicTests tests) {
		return tests;
	}
	
	@Override
	public Shipper getShipper() {
		return this.ship;
	}

	@Override
	public void modifyShipper(String[] newCargo) {
		this.ship.cargo = newCargo;
		return;
	}

	@Override
	public Shipped getGoods() {
		return this.goods;
	}

	@Override
	public void giveGoods(Shipped goods) {
		this.goods = goods;
	}
	
}
