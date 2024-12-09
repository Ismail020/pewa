package com.example.demo.match;
import com.example.demo.move.Move;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/match")
public class MatchController {
    @Autowired
    private MatchService matchService;

    @PostMapping("/create")
    public ResponseEntity<Match> createMatch(@RequestParam String player1Id, @RequestParam String player2Id) {
        Match match = matchService.createMatch(player1Id, player2Id);
        return ResponseEntity.ok(match);
    }

    @PostMapping("/{matchId}/move")
    public ResponseEntity<Move> saveMove(
            @PathVariable Long matchId,
            @RequestParam String playerId,
            @RequestParam int x,
            @RequestParam int y,
            @RequestParam boolean hit
    ) {
        Move move = matchService.saveMove(matchId, playerId, x, y, hit);
        return ResponseEntity.ok(move);
    }
}
