package cn.edu.xmu.address.model.po;

import java.time.LocalDateTime;

public class AddressPo {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column address.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column address.customer_id
     *
     * @mbg.generated
     */
    private Long customerId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column address.region_id
     *
     * @mbg.generated
     */
    private Long regionId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column address.detail
     *
     * @mbg.generated
     */
    private String detail;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column address.consignee
     *
     * @mbg.generated
     */
    private String consignee;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column address.mobile
     *
     * @mbg.generated
     */
    private String mobile;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column address.be_default
     *
     * @mbg.generated
     */
    private Byte beDefault;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column address.gmt_create
     *
     * @mbg.generated
     */
    private LocalDateTime gmtCreate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column address.gmt_modified
     *
     * @mbg.generated
     */
    private LocalDateTime gmtModified;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column address.id
     *
     * @return the value of address.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column address.id
     *
     * @param id the value for address.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column address.customer_id
     *
     * @return the value of address.customer_id
     *
     * @mbg.generated
     */
    public Long getCustomerId() {
        return customerId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column address.customer_id
     *
     * @param customerId the value for address.customer_id
     *
     * @mbg.generated
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column address.region_id
     *
     * @return the value of address.region_id
     *
     * @mbg.generated
     */
    public Long getRegionId() {
        return regionId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column address.region_id
     *
     * @param regionId the value for address.region_id
     *
     * @mbg.generated
     */
    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column address.detail
     *
     * @return the value of address.detail
     *
     * @mbg.generated
     */
    public String getDetail() {
        return detail;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column address.detail
     *
     * @param detail the value for address.detail
     *
     * @mbg.generated
     */
    public void setDetail(String detail) {
        this.detail = detail == null ? null : detail.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column address.consignee
     *
     * @return the value of address.consignee
     *
     * @mbg.generated
     */
    public String getConsignee() {
        return consignee;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column address.consignee
     *
     * @param consignee the value for address.consignee
     *
     * @mbg.generated
     */
    public void setConsignee(String consignee) {
        this.consignee = consignee == null ? null : consignee.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column address.mobile
     *
     * @return the value of address.mobile
     *
     * @mbg.generated
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column address.mobile
     *
     * @param mobile the value for address.mobile
     *
     * @mbg.generated
     */
    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column address.be_default
     *
     * @return the value of address.be_default
     *
     * @mbg.generated
     */
    public Byte getBeDefault() {
        return beDefault;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column address.be_default
     *
     * @param beDefault the value for address.be_default
     *
     * @mbg.generated
     */
    public void setBeDefault(Byte beDefault) {
        this.beDefault = beDefault;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column address.gmt_create
     *
     * @return the value of address.gmt_create
     *
     * @mbg.generated
     */
    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column address.gmt_create
     *
     * @param gmtCreate the value for address.gmt_create
     *
     * @mbg.generated
     */
    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column address.gmt_modified
     *
     * @return the value of address.gmt_modified
     *
     * @mbg.generated
     */
    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column address.gmt_modified
     *
     * @param gmtModified the value for address.gmt_modified
     *
     * @mbg.generated
     */
    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }
}