package com.example.inclass06;

public class News {

    public String title;
    public String desc;
    public String date;
    public String urlToImage;

    @Override
    public String toString() {
        return "News{" +
                "title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", date='" + date + '\'' +
                ", urlToImage='" + urlToImage + '\'' +
                '}';
    }
}

