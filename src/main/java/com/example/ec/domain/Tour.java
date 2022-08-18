package com.example.ec.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Objects;

/**
 * The Tour contains all attributes of an Explore California Tour.
 *
 * Created by Mary Ellen Bowman
 */
//@Entity
@Document
public class Tour {
    @Id
    private String id;
    @Indexed
    private String title;
    private String tourPackageCode;
    private String tourPackageName;
    private Map<String, String> details;

    public Tour(String title, TourPackage tourPackage, Map<String, String> details) {
        this.title = title;
        this.tourPackageCode = tourPackage.getCode();
        this.tourPackageName = tourPackage.getName();
        this.details = details;
    }

    protected Tour() {
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTourPackageCode() {
        return tourPackageCode;
    }

    public void setTourPackageCode(String tourPackageCode) {
        this.tourPackageCode = tourPackageCode;
    }

    public String getTourPackageName() {
        return tourPackageName;
    }

    public void setTourPackageName(String tourPackageName) {
        this.tourPackageName = tourPackageName;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "Tour{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", tourPackageCode='" + tourPackageCode + '\'' +
                ", tourPackageName='" + tourPackageName + '\'' +
                ", details=" + details +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tour tour = (Tour) o;
        return Objects.equals(id, tour.id) && Objects.equals(title, tour.title) && Objects.equals(tourPackageCode, tour.tourPackageCode) && Objects.equals(tourPackageName, tour.tourPackageName) && Objects.equals(details, tour.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, tourPackageCode, tourPackageName, details);
    }
}
