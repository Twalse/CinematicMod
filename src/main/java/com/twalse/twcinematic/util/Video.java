package com.twalse.twcinematic.util;

import java.net.URI;

public class Video {
    private final String url;
    private final String name;

    public Video(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public Video(String url) {
        this.url = url;
        this.name = url;
    }

    public String getUrl() {
        return this.url;
    }

    public String getName() {
        return this.name;
    }

    public URI getMediaUri() {
        try {
            if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("file:/")) {
                return new URI(url);
            }
            return new java.io.File(url).toURI();
        } catch (Exception e) {
            return new java.io.File(url).toURI();
        }
    }
}
