package cz.gyarab3e.rocnikovaprace3.controller;

import cz.gyarab3e.rocnikovaprace3.jpa.CellStatus;
import cz.gyarab3e.rocnikovaprace3.jpa.Game;
import cz.gyarab3e.rocnikovaprace3.services.*;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin
@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;
    public GameController(GameService gameService){

        this.gameService = gameService;
    }
    @PostMapping("/start")
    public ResponseEntity<GameHolder> startGame(){
        Game game = gameService.startGameWithCode();
        return ResponseEntity.ok(new GameHolder(game.getId(),game.getPlayingCode(),game.getStatus(),null));
    }
    @PostMapping("/join")
    public ResponseEntity<GameHolder> joinGame(@RequestBody String code){
        Game game;
        try {
            game = gameService.joinGame(code);
        } catch (NoGameException e) {
            e.printStackTrace();
            return new ResponseEntity<>( HttpStatus.NOT_FOUND );
        }
        return ResponseEntity.ok(new GameHolder(game.getId(),game.getPlayingCode(),game.getStatus(),null));
    }
    @GetMapping(value="/{id}")
    public ResponseEntity<GameHolder> getGame(@PathVariable Long id){
        return ResponseEntity.ok(new GameHolder(gameService.getGame(id)));
    }

    @GetMapping(value="/getUsersBoard")
    public ResponseEntity<BoardHolder> getUsersBoard(@Param("id") Long id,@Param("username") String username){
        return ResponseEntity.ok(new BoardHolder(id,gameService.returnUsersBoard(id,username)));
    }

    @GetMapping(value="/getOpponentsBoard")
    public ResponseEntity<BoardHolder> getOpponentsBoard(@Param("id") Long id,@Param("username") String username){
        return ResponseEntity.ok(new BoardHolder(id,gameService.returnOpponentsBoard(id,username)));
    }


    @PostMapping("/saveBoard")
    public ResponseEntity<Void> saveBoard(@RequestBody BoardHolder boardholder){
        try {
            gameService.saveBoard(boardholder.id, boardholder.getBoard());
        } catch (ValidationException e) {
            e.printStackTrace();
            return new ResponseEntity<>( HttpStatus.BAD_REQUEST );
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>( HttpStatus.BAD_REQUEST );
        }
        return new ResponseEntity<>( HttpStatus.OK );
    }
//make them private
    @PostMapping("/getMove")
    public ResponseEntity<MoveStatus> move(@RequestBody MoveHolder moveHolder){
        try{
            return ResponseEntity.ok(gameService.move(moveHolder.id, moveHolder.x, moveHolder.y));
        }catch(IllegalArgumentException e){
            return new ResponseEntity<>( HttpStatus.BAD_REQUEST );
        } catch (MoveExceptions e) {
            e.printStackTrace();
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }

    }


}
