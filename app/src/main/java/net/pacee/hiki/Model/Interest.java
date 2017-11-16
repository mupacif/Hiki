package net.pacee.hiki.Model;

import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by mupac_000 on 12-11-17.
 */

@Entity
public class Interest {

    @Id
    long id;

    String placeId;
    String name;
    String adress;
    Double lat;
    Double lng;
    String comment;
    Date date;
    Boolean done;

    public Interest()
    {

    }
    public Interest(long id, String placeId, String name, String adress, Double lat, Double lng, String comment, Date date, Boolean done) {
        this.id = id;
        this.placeId = placeId;
        this.name = name;
        this.adress = adress;
        this.lat = lat;
        this.lng = lng;
        this.comment = comment;
        this.date = date;
        this.done = done;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    @Override
    public String toString() {
        return "Interest{" +
                "id=" + id +
                ", placeId='" + placeId + '\'' +
                ", name='" + name + '\'' +
                ", adress='" + adress + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", comment='" + comment + '\'' +
                ", date=" + date +
                ", done=" + done +
                '}';
    }
}
