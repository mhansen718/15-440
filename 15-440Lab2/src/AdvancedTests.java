
public interface AdvancedTests extends Remote440 {

	public BasicTests makeNewTests(String phrase, int number);
	public void changeNumber(BasicTests tests, int value);
	public Shipper getShipper();
	public void modifyShipper(String[] newCargo);
	public Shipped getGoods();
	public void giveGoods(Shipped goods);
}
