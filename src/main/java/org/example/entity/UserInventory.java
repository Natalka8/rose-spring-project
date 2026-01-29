package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @Column(name = "equipped", nullable = false)
    @Builder.Default
    private Boolean equipped = false;

    @Column(name = "slot")
    private String slot; // HEAD, CHEST, WEAPON, etc.

    @Column(name = "durability")
    private Integer durability;

    @Column(name = "enchantment_level")
    @Builder.Default
    private Integer enchantmentLevel = 0;

    @Column(name = "custom_name")
    private String customName;

    @CreationTimestamp
    @Column(name = "acquired_at", nullable = false)
    private LocalDateTime acquiredAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}