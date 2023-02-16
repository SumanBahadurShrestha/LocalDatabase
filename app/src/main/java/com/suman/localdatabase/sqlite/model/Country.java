package com.suman.localdatabase.sqlite.model;

import java.util.Comparator;

public class Country {
    public int id, fav;
    public String cName, cCode, cDate;
    public double cSalary;
    public byte[] image;
    public String image_url;

    public Country(int id, String cName, String cCode, String cDate, double cSalary, byte[] image, int fav, String image_url) {
        this.id = id;
        this.cName = cName;
        this.cCode = cCode;
        this.cDate = cDate;
        this.cSalary = cSalary;
        this.image = image;
        this.fav = fav;
        this.image_url = image_url;
    }

    public static Comparator<Country> Name = new Comparator<Country>() {
        @Override
        public int compare(Country c1, Country c2){
            return c1.getcName().compareTo(c2.getcName());
        }
    };
    public static Comparator<Country> iddec = new Comparator<Country>() {
        @Override
        public int compare(Country c1, Country c2){
            return c2.getId() - c1.getId();
        }
    };
    public static Comparator<Country> idasc = new Comparator<Country>() {
        @Override
        public int compare(Country c1, Country c2){
            return c1.getId() - c2.getId();
        }
    };
    public static Comparator<Country> role = new Comparator<Country>() {
        @Override
        public int compare(Country c1, Country c2){
            return c1.getcCode().compareTo(c2.getcCode());
        }
    };

    public int getId() {
        return id;
    }

    public int getFav() {
        return fav;
    }

    public String getcName() {
        return cName;
    }

    public String getcCode() {
        return cCode;
    }

    public String getcDate() {
        return cDate;
    }

    public double getcSalary() {
        return cSalary;
    }

    public byte[] getImage() {
        return image;
    }

    public String getImage_url() {
        return image_url;
    }
}
