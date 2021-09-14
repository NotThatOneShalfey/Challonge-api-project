package Challonge.Rest;

import Challonge.Core.FilesCore;
import Challonge.Core.MatchesCore;
import Challonge.Core.ParticipantsCore;
import Challonge.Entities.Match;
import Challonge.Entities.Participant;
import Challonge.Exceptions.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/custom")
public class CustomTournamentRestController {
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

    @RequestMapping(value = "set_complete", method = RequestMethod.POST)
    public RequestResponse setMatchComplete(@RequestParam(name = "matchId") int matchId, @RequestParam(name = "complete", required = false) Boolean complete) throws RequestResponseNotFoundException, RequestResponseInternalServerErrorException {
        log.debug("Call setMatchActive with match ID = {}", matchId);
        if (complete == null) {
            complete = true;
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
        MatchesCore.getInstance().setMatchComplete(matchId, complete);
        return new RequestResponse(HttpStatus.OK, match);
    }

    @RequestMapping(value = "set_match_order", method = RequestMethod.POST)
    public RequestResponse setMatchOrder(@RequestParam(name = "matchId") int matchId, @RequestParam(name = "order") int order) throws RequestResponseNotFoundException, RequestResponseInternalServerErrorException {
        log.debug("Call setMatchOrder with match ID = {}", matchId);
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
        MatchesCore.getInstance().setMatchOrder(matchId, order);
        return new RequestResponse(HttpStatus.OK, match);
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

    @RequestMapping(value = "add_participant", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResponse addParticipant(@RequestParam(name = "name") String participantName) {
        log.debug("Call addParticipant");
        ParticipantsCore.getInstance().addParticipant(participantName);
        return new RequestResponse(HttpStatus.OK, "Participant : " + participantName + " added successfully");
    }

    @RequestMapping(value = "remove_participant", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResponse removeParticipant(@RequestParam(name = "id") int participantId) throws RequestResponseNotFoundException, RequestResponseInternalServerErrorException {
        log.debug("Call addParticipant");
        Participant participant;
        String participantAsString;
        try {
            participant = ParticipantsCore.getInstance().getParticipant(participantId);
            participantAsString = ParticipantsCore.getInstance().getParticipantByIdAsString(participantId);
        } catch (ParticipantNotFoundException | JsonProcessingException e) {
            if (e.getClass().getSimpleName().equals("ParticipantNotFoundException")) {
                throw new RequestResponseNotFoundException();
            }
            log.error(e.getMessage());
            throw new RequestResponseInternalServerErrorException();
        }
        ParticipantsCore.getInstance().removeParticipant(participant);
        return new RequestResponse(HttpStatus.OK, participantAsString);
    }

    @RequestMapping(value = "participant_by_name", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResponse getParticipantByName(@RequestParam(name = "name") String participantName) throws RequestResponseNotFoundException, RequestResponseInternalServerErrorException {
        log.debug("Call getParticipantByName");
        String participant;
        try {
            participant = ParticipantsCore.getInstance().getParticipantByNameAsString(participantName);
        } catch (JsonProcessingException | ParticipantNotFoundException e) {
            if (e.getClass().getSimpleName().equals("ParticipantNotFoundException")) {
                throw new RequestResponseNotFoundException();
            }
            log.error(e.getMessage());
            throw new RequestResponseInternalServerErrorException();
        }
        return new RequestResponse(HttpStatus.OK, participant);
    }

    @RequestMapping(value = "add_match", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResponse addMatch(@RequestParam(name = "participant_1_id") int participant1Id, @RequestParam(name = "participant_2_id") int participant2Id) throws RequestResponseNotFoundException, RequestResponseInternalServerErrorException {
        log.debug("Call addMatch");
        Participant participant1;
        Participant participant2;
        String match;
        int createdMatchId;
        try {
            participant1 = ParticipantsCore.getInstance().getParticipant(participant1Id);
            participant2 = ParticipantsCore.getInstance().getParticipant(participant2Id);
            createdMatchId = MatchesCore.getInstance().addMatch(participant1, participant2);
            match = MatchesCore.getInstance().getMatchAsString(createdMatchId);
        } catch (ParticipantNotFoundException | CloneNotSupportedException | MatchesNotFoundException | JsonProcessingException e) {
            if (e.getClass().getSimpleName().equals("ParticipantNotFoundException") || e.getClass().getSimpleName().equals("MatchesNotFoundException")) {
                throw new RequestResponseNotFoundException();
            }
            log.error(e.getMessage());
            throw new RequestResponseInternalServerErrorException();
        }
        return new RequestResponse(HttpStatus.OK, match);
    }

    @RequestMapping(value = "remove_match", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResponse removeMatch(@RequestParam(name = "id") int matchId) throws RequestResponseNotFoundException, RequestResponseInternalServerErrorException {
        log.debug("Call removeMatch");
        Match match;
        String matchAsString;
        try {
            match = MatchesCore.getInstance().getMatch(matchId);
            matchAsString = MatchesCore.getInstance().getMatchAsString(matchId);
        } catch (MatchesNotFoundException | JsonProcessingException e) {
            if (e.getClass().getSimpleName().equals("MatchesNotFoundException")) {
                throw new RequestResponseNotFoundException();
            }
            log.error(e.getMessage());
            throw new RequestResponseInternalServerErrorException();
        }
        MatchesCore.getInstance().removeMatch(match);
        return new RequestResponse(HttpStatus.OK, matchAsString);
    }

    @RequestMapping(value = "set_score", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResponse setScore(@RequestParam(name = "id") int matchId, @RequestParam(name = "p_1_score", required = false) Integer p1Score, @RequestParam(name = "p_2_score", required = false) Integer p2Score) throws RequestResponseNotFoundException, RequestResponseInternalServerErrorException {
        log.debug("Call setScore");
        String match;
        if (p1Score == null) {
            p1Score = 0;
        }
        if (p2Score == null) {
            p2Score = 0;
        }
        try {
            MatchesCore.getInstance().setScore(matchId, p1Score, p2Score);
            match = MatchesCore.getInstance().getMatchAsString(matchId);
        } catch (MatchesNotFoundException | JsonProcessingException e) {
            if (e.getClass().getSimpleName().equals("MatchesNotFoundException")) {
                throw new RequestResponseNotFoundException();
            }
            log.error(e.getMessage());
            throw new RequestResponseInternalServerErrorException();
        }
        return new RequestResponse(HttpStatus.OK, match);
    }
    @RequestMapping(value = "save_tournament", method = RequestMethod.POST)
    public void saveTournamentToFile() throws MatchesNotFoundException, IOException {
        FilesCore.getInstance().saveTournamentToFile();
    }

}
