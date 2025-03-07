package dto;

public class Phone {
    private int phoneId;
    private String brand;
    private String model;
    private int price;
    private int stock;
    
    public Phone() {}
    public Phone(int phoneId, String brand, String model, int price, int stock) {
        super();
        this.phoneId = phoneId;
        this.brand = brand;
        this.model = model;
        this.price = price;
        this.stock = stock;
    }
    
    public int getPhoneId() {
		return phoneId;
	}
	public void setPhoneId(int phoneId) {
		this.phoneId = phoneId;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getStock() {
		return stock;
	}
	public void setStock(int stock) {
		this.stock = stock;
	}
	
	@Override
	public String toString() {
		return "Phone [phoneId=" + phoneId + ", brand=" + brand + ", model=" + model + ", price=" + price
				+ ", stock=" + stock + "]";
	}
}