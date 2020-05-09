package com.example.first;

public class Matches {
    private String id;
    private String name;
    private String seen;

    public void setId(String id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setSeen(String seen) { this.seen = seen; }

    public Matches() {
    }

    public Matches(String id, String name, String seen) {
        this.id = id;
        this.name = name;
        this.seen = seen;
    }

    public String getId() { return id; }

    public String getName() { return name; }

    public String getSeen() { return seen; }
}
