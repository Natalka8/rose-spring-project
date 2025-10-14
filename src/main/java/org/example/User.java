package org.example;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "players") // или "users" в зависимости от вашей таблицы
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String email;
    private String password;
    private int level;
    private int health;
    private int gold;
    private int experience;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // Связь с достижениями
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Achievement> achievements = new ArrayList<>();

    // Конструкторы
    public User() {
        this.level = 1;
        this.health = 100;
        this.gold = 0;
        this.experience = 0;
        this.createdAt = LocalDateTime.now();
    }

    // Конструктор для регистрации
    public User(String username, String email, String password) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Конструктор только с username (для обратной совместимости)
    public User(String username) {
        this();
        this.username = username;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }

    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public List<Achievement> getAchievements() { return achievements; }
    public void setAchievements(List<Achievement> achievements) { this.achievements = achievements; }

    public void addAchievement(Achievement achievement) {
        achievements.add(achievement);
        achievement.setUser(this);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", level=" + level +
                ", health=" + health +
                ", gold=" + gold +
                ", experience=" + experience +
                ", createdAt=" + createdAt +
                '}';
    }
}