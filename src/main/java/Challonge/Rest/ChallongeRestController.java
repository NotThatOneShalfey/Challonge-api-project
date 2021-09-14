package Challonge.Rest;

import Challonge.Core.ProcessCore;
import Challonge.Exceptions.*;
import Challonge.Core.MatchesCore;
import Challonge.Core.ParticipantsCore;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/challonge")
@Slf4j
public class ChallongeRestController {
    @RequestMapping(value = "download", method = RequestMethod.POST)
    public RequestResponse downloadTournament(@RequestParam(name = "tournament") String tournament) throws RequestResponseNotFoundException, RequestResponseInternalServerErrorException {
        log.debug("Call downloadTournament");
        try {
            ProcessCore.getInstance().downloadTournament(tournament);
        } catch (TournamentNotFoundException | CloneNotSupportedException e) {
            if (e.getClass().getSimpleName().equals("TournamentNotFoundException")) {
                throw new RequestResponseNotFoundException();
            }
            log.error(e.getMessage());
            throw new RequestResponseInternalServerErrorException();
        }
        return new RequestResponse(HttpStatus.OK, "Tournament with id = " + tournament + " successfully downloaded");
    }

    @RequestMapping(value = "set_active", method = RequestMethod.POST)
    public RequestResponse setMatchActive(@RequestParam(name = "matchId") int matchId, @RequestParam(name = "activate", required = false) Boolean activate) throws RequestResponseNotFoundException, RequestResponseInternalServerErrorException {
        log.debug("Call setMatchActive with match ID = {}", matchId);
        if (activate == null) {
            activate = true;
        }
        String match;
        try {
            match = MatchesCore.getInstance().getMatchAsString(matchId);
        } catch (JsonProcessingException | MatchesNotFoundException e) {
            if (e.getClass().getSimpleName().equals("MatchesNotFoundException")) {
                throw new RequestResponseNotFoundException();
            }
            log.error(e.getMessage());
            throw new RequestResponseInternalServerErrorException();
        }
        MatchesCore.getInstance().setMatchActive(matchId, activate);
        return new RequestResponse(HttpStatus.OK, match);
    }

    @RequestMapping(value = "participants_list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResponse getParticipantsList() throws RequestResponseInternalServerErrorException {
        log.debug("Call getParticipantsList");
        String participants;
        try {
            participants = ParticipantsCore.getInstance().getParticipantsListAsString();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RequestResponseInternalServerErrorException();
        }
        return new RequestResponse(HttpStatus.OK, participants);
    }

    @RequestMapping(value = "matches_list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResponse getMatchesList() throws RequestResponseInternalServerErrorException, RequestResponseNotFoundException {
        log.debug("Call getMatchesList");
        String matches;
        try {
           matches = MatchesCore.getInstance().getMatchesListAsString();
        } catch (JsonProcessingException | MatchesNotFoundException e) {
            if (e.getClass().getSimpleName().equals("MatchesNotFoundException")) {
                throw new RequestResponseNotFoundException();
            }
            log.error(e.getMessage());
            throw new RequestResponseInternalServerErrorException();
        }
        return new RequestResponse(HttpStatus.OK, matches);
    }

    @RequestMapping(value = "complete_matches_list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResponse getCompleteMatchesList() throws RequestResponseInternalServerErrorException, RequestResponseNotFoundException {
        log.debug("Call getCompleteMatchesList");
        String matches;
        try {
            matches = MatchesCore.getInstance().getCompleteMatchesListAsString();
        } catch (JsonProcessingException | CompleteMatchesNotFoundException e) {
            if (e.getClass().getSimpleName().equals("CompleteMatchesNotFoundException")) {
                throw new RequestResponseNotFoundException();
            }
            log.error(e.getMessage());
            throw new RequestResponseInternalServerErrorException();
        }
        return new RequestResponse(HttpStatus.OK, matches);
    }

    @RequestMapping(value = "incomplete_matches_list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResponse getIncompleteMatchesList() throws RequestResponseInternalServerErrorException, RequestResponseNotFoundException {
        log.debug("Call getIncompleteMatchesList");
        String matches;
        try {
            matches = MatchesCore.getInstance().getIncompleteMatchesListAsString();
        } catch (JsonProcessingException | IncompleteMatchesNotFoundException e) {
            if (e.getClass().getSimpleName().equals("IncompleteMatchesNotFoundException")) {
                throw new RequestResponseNotFoundException();
            }
            log.error(e.getMessage());
            throw new RequestResponseInternalServerErrorException();
        }
        return new RequestResponse(HttpStatus.OK, matches);
    }

    @RequestMapping(value = "ordered_match_list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResponse getOrderedMatchList() throws RequestResponseInternalServerErrorException, RequestResponseNotFoundException {
        log.debug("Call getOrderedMatchList");
        String matches;
        try {
            matches = MatchesCore.getInstance().getOrderedMatchListAsString();
        } catch (JsonProcessingException | MatchesNotFoundException e) {
            if (e.getClass().getSimpleName().equals("MatchesNotFoundException")) {
                throw new RequestResponseNotFoundException();
            }
            log.error(e.getMessage());
            throw new RequestResponseInternalServerErrorException();
        }
        return new RequestResponse(HttpStatus.OK, matches);
    }

    @RequestMapping(value = "match", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResponse getMatch(@RequestParam(name = "matchId") int matchId) throws RequestResponseNotFoundException, RequestResponseInternalServerErrorException {
        log.debug("Call getMatch");
        String match;
        try {
            match = MatchesCore.getInstance().getMatchAsString(matchId);
        } catch (JsonProcessingException | MatchesNotFoundException e) {
            if (e.getClass().getSimpleName().equals("MatchesNotFoundException")) {
                throw new RequestResponseNotFoundException();
            }
            log.error(e.getMessage());
            throw new RequestResponseInternalServerErrorException();
        }
        return new RequestResponse(HttpStatus.OK, match);
    }

    @RequestMapping(value = "active_match", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResponse getActiveMatch() throws RequestResponseNotFoundException, RequestResponseInternalServerErrorException {
        log.debug("Call getActiveMatch");
        String match;
        try {
            match = MatchesCore.getInstance().getActiveMatch();
        } catch (MatchesNotFoundException | IOException e) {
            if (e.getClass().getSimpleName().equals("MatchesNotFoundException")) {
                throw new RequestResponseNotFoundException();
            }
            log.error(e.getMessage());
            throw new RequestResponseInternalServerErrorException();
        }
        return new RequestResponse(HttpStatus.OK, match);
    }

    @RequestMapping(value = "next_match", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResponse getNextMatch() throws RequestResponseNotFoundException, RequestResponseInternalServerErrorException {
        log.debug("Call getNextMatch");
        String match;
        try {
            match = MatchesCore.getInstance().getNextMatch();
        } catch (IOException | MatchesNotFoundException e) {
            if (e.getClass().getSimpleName().equals("MatchesNotFoundException")) {
                throw new RequestResponseNotFoundException();
            }
            log.error(e.getMessage());
            throw new RequestResponseInternalServerErrorException();
        }
        return new RequestResponse(HttpStatus.OK, match);
    }

    @RequestMapping(value = "matches_by_participant", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResponse getMatchesByParticipant(@RequestParam(name = "participant_name") String participantName, @RequestParam(name = "incomplete", required = false) Boolean onlyIncomplete) throws RequestResponseNotFoundException, RequestResponseInternalServerErrorException {
        log.debug("Call getMatchesByParticipant");
        if (onlyIncomplete == null) {
            onlyIncomplete = false;
        }
        String matches;
        try {
            matches = MatchesCore.getInstance().getMatchesByParticipant(participantName, onlyIncomplete);
        } catch (JsonProcessingException | MatchesNotFoundException | IncompleteMatchesNotFoundException e) {
            if (e.getClass().getSimpleName().equals("MatchesNotFoundException") || e.getClass().getSimpleName().equals("IncompleteMatchesNotFoundException")) {
                throw new RequestResponseNotFoundException();
            }
            log.error(e.getMessage());
            throw new RequestResponseInternalServerErrorException();
        }
        return new RequestResponse(HttpStatus.OK, matches);
    }

    @RequestMapping(value = "match_by_order", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResponse getMatchByOrder(@RequestParam(name = "order") int order) throws RequestResponseNotFoundException, RequestResponseInternalServerErrorException {
        log.debug("Call getMatchByOrder");
        String match;
        try {
            match = MatchesCore.getInstance().getMatchByOrder(order);
        } catch (JsonProcessingException | MatchesNotFoundException e) {
            if (e.getClass().getSimpleName().equals("MatchesNotFoundException")) {
                throw new RequestResponseNotFoundException();
            }
            log.error(e.getMessage());
            throw new RequestResponseInternalServerErrorException();
        }
        return new RequestResponse(HttpStatus.OK, match);
    }


}
