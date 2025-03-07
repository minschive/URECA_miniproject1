package dto;

public class Sale {
	private int saleId;
	private int custId;
	private int phoneId;
	private int quantity;
	private int total_price;
	
	public Sale() {}
	// 기존 생성자 (saleId 없이)
    public Sale(int custId, int phoneId, int quantity, int total_price) {
        this.custId = custId;
        this.phoneId = phoneId;
        this.quantity = quantity;
        this.total_price = total_price;
    }

    // 새로운 생성자 (saleId 포함)
    public Sale(int saleId, int custId, int phoneId, int quantity, int total_price) {
        this.saleId = saleId;
        this.custId = custId;
        this.phoneId = phoneId;
        this.quantity = quantity;
        this.total_price = total_price;
    }
	public int getSaleId() {
		return saleId;
	}
	public void setSaleId(int saleId) {
		this.saleId = saleId;
	}
	public int getCustId() {
		return custId;
	}
	public void setCustId(int custId) {
		this.custId = custId;
	}
	public int getPhoneId() {
		return phoneId;
	}
	public void setPhoneId(int phoneId) {
		this.phoneId = phoneId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getTotal_price() {
		return total_price;
	}
	public void setTotal_price(int total_price) {
		this.total_price = total_price;
	}
	@Override
	public String toString() {
		return "Sale [saleId=" + saleId + ", custId=" + custId + ", phoneId=" + phoneId + ", quantity=" + quantity
				+ ", total_price=" + total_price + "]";
	}
}
