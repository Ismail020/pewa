package com.example.demo.move;
import com.example.demo.match.Match;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "move")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Move {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(nullable = false)
    private String playerId;

    @Column(nullable = false)
    private int xCoordinate;

    @Column(nullable = false)
    private int yCoordinate;

    @Column(nullable = false)
    private boolean hit;

    @Column(nullable = false, updatable = false)
    private LocalDateTime moveTime = LocalDateTime.now();
}
