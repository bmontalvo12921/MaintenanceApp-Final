//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

public class Customer {
    private String phoneNumber;
    private String name;
    private String address;
    private String email;

    public Customer(String phoneNumber, String name, String address, String email) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.address = address;
        this.email = email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }

    public String getEmail() {
        return this.email;
    }

    public void setPhoneNumber(String v) {
        this.phoneNumber = v;
    }

    public void setName(String v) {
        this.name = v;
    }

    public void setAddress(String v) {
        this.address = v;
    }

    public void setEmail(String v) {
        this.email = v;
    }
}
