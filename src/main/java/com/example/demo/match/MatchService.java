package com.example.demo.match;
import com.example.demo.move.Move;
import com.example.demo.move.MoveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MatchService {
    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MoveRepository moveRepository;

    public Match createMatch(String player1Id, String player2Id) {
        Match match = new Match();
        match.setPlayer1Id(player1Id);
        match.setPlayer2Id(player2Id);
        match.setStatus("IN_PROGRESS");
        return matchRepository.save(match);
    }

    public Move saveMove(Long matchId, String playerId, int x, int y, boolean hit) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found"));

        Move move = new Move();
        move.setMatch(match);
        move.setPlayerId(playerId);
        move.setXCoordinate(x);
        move.setYCoordinate(y);
        move.setHit(hit);

        return moveRepository.save(move);
    }
}
