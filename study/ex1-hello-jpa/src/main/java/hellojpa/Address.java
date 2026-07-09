package hellojpa;

import jakarta.persistence.*;

@Embeddable
public class Address {
    private String city;
    private String street;
    @Column(name = "ZIPCODE")
    private String zipcode;

    @Transient
    private Member member;

    public Address() {
    }

    public Address(String city, String street, String zip) {
        this.city = city;
        this.street = street;
        this.zipcode = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public String getZipcode() {
        return zipcode;
    }

    // private 으로 만드는건 선택 사항이다.
    private void setStreet(String street) {
        this.street = street;
    }

    private void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    private void setMember(Member member) {
        this.member = member;
    }
}
