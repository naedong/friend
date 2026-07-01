package com.naedong.friend.booking.domain;

import com.naedong.friend.common.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "safe_meeting_spot")
public class SafeMeetingSpot extends AuditableEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SafeMeetingSpotType type;

    @Column(nullable = false)
    private boolean staffPresent;

    @Column(nullable = false)
    private boolean wellLit;

    @Column(nullable = false)
    private boolean publicEntrance;

    @Column(nullable = false)
    private boolean easyExit;

    @Column(nullable = false)
    private boolean alcoholCentered;

    @Column(nullable = false)
    private boolean privateSpace;

    @Column(nullable = false)
    private boolean active;

    public boolean passesSafetyRules() {
        return active
                && staffPresent
                && wellLit
                && publicEntrance
                && easyExit
                && !alcoholCentered
                && !privateSpace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public SafeMeetingSpotType getType() {
        return type;
    }

    public void setType(SafeMeetingSpotType type) {
        this.type = type;
    }

    public boolean isStaffPresent() {
        return staffPresent;
    }

    public void setStaffPresent(boolean staffPresent) {
        this.staffPresent = staffPresent;
    }

    public boolean isWellLit() {
        return wellLit;
    }

    public void setWellLit(boolean wellLit) {
        this.wellLit = wellLit;
    }

    public boolean isPublicEntrance() {
        return publicEntrance;
    }

    public void setPublicEntrance(boolean publicEntrance) {
        this.publicEntrance = publicEntrance;
    }

    public boolean isEasyExit() {
        return easyExit;
    }

    public void setEasyExit(boolean easyExit) {
        this.easyExit = easyExit;
    }

    public boolean isAlcoholCentered() {
        return alcoholCentered;
    }

    public void setAlcoholCentered(boolean alcoholCentered) {
        this.alcoholCentered = alcoholCentered;
    }

    public boolean isPrivateSpace() {
        return privateSpace;
    }

    public void setPrivateSpace(boolean privateSpace) {
        this.privateSpace = privateSpace;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
