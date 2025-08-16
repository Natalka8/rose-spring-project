package org.example;

public class User {
    private String username;
    private int gamesPlayed;

    public User(String username) {
        this.username = username;
        this.gamesPlayed = 0;
    }

    public String getUsername() {
        return username;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void incrementGamesPlayed() {
        gamesPlayed++;
    }
}
