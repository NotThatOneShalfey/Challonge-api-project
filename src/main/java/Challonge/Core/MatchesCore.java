package Challonge.Core;

import Challonge.Configuration.AppConfiguration;
import Challonge.Entities.Match;
import Challonge.Entities.Participant;
import Challonge.Exceptions.CompleteMatchesNotFoundException;
import Challonge.Exceptions.IncompleteMatchesNotFoundException;
import Challonge.Exceptions.MatchesNotFoundException;
import Challonge.Exceptions.TournamentNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;

@Slf4j
@Service
public class MatchesCore {
    @Getter
    private static MatchesCore instance;

    @Getter
    private ArrayList<Match> matchesList = new ArrayList<>();

    @Getter
    private ArrayList<Match> completeMatchesList = new ArrayList<>();

    @Getter
    private ArrayList<Match> incompleteMatchesList = new ArrayList<>();

    @Getter
    private ArrayList<Match> orderedMatchList = new ArrayList<>();

    private JsonMapper jsonMapper = new JsonMapper();

    private RestTemplate restTemplate = new RestTemplate();

    private int customMatchesId = 0;

    public void loadMatches(String tournament) throws TournamentNotFoundException, CloneNotSupportedException {
        matchesList.clear();
        incompleteMatchesList.clear();
        completeMatchesList.clear();

        String apiKey = AppConfiguration.getInstance().getApiKey();
        String account = AppConfiguration.getInstance().getAccount();
        String method = "/tournaments/{tournament}/matches.json";
        String completeUrl = AppConfiguration.HTTPS_PREFIX + account + ":" + apiKey + "@" + AppConfiguration.CHALLONGE_URL + method;

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.exchange(completeUrl, HttpMethod.GET, AppConfiguration.getInstance().getEntity(), String.class, tournament);
        } catch (HttpClientErrorException e) {
            log.error("Error in call to loadMatches\nError - {}", e.getMessage());
            throw new TournamentNotFoundException("Error!\nNo tournament found with id = " + tournament);
        }
        AppConfiguration.getInstance().setTournament(tournament);
        JSONArray jsonArray = new JSONArray(responseEntity.getBody());
        int order = 0;
        for (Object obj : jsonArray) {
            JSONObject matchObject = new JSONObject(obj.toString()).getJSONObject("match");
            Match match = new Match(matchObject.getInt("id"));

            if (matchObject.getString("state").equals("complete")) {
                match.setIsComplete(true);
            } else if (!matchObject.get("scores_csv").equals("")) {
                match.setIsActive(true);
            }

            int player1_id = 0;
            int player2_id = 0;
            if (matchObject.get("player1_id") != JSONObject.NULL) {
                player1_id = matchObject.getInt("player1_id");
            }
            if (matchObject.get("player2_id") != JSONObject.NULL) {
                player2_id = matchObject.getInt("player2_id");
            }
            if (player1_id != player2_id) {
                for (Participant participant : ParticipantsCore.getInstance().getParticipantsList()) {
                    if (participant.getId() == player1_id) {
                        match.setParticipant_1(participant);
                    } else if (participant.getId() == player2_id) {
                        match.setParticipant_2(participant);
                    }
                }
                if (match.getParticipant_1() == null) {
                    match.setParticipant_1(new Participant());
                }
                if (match.getParticipant_2() == null) {
                    match.setParticipant_2(new Participant());
                }

                if (matchObject.get("suggested_play_order").equals(JSONObject.NULL)) {
                    match.setOrder(order + 1);
                }
                else {
                    order = matchObject.getInt("suggested_play_order");
                    match.setOrder(order);
                }

                if (match.getIsComplete()) {
                    completeMatchesList.add(match);
                } else {
                    incompleteMatchesList.add(match);
                }

                matchesList.add(match);

                updateOrderedMatchList((Match) match.clone());
            }
        }
        Iterator<Match> matchIterator = orderedMatchList.iterator();
        while (matchIterator.hasNext()) {
            Match match = matchIterator.next();
            if (!matchesList.contains(match)) {
                matchIterator.remove();
            }
        }
    }

    public void setMatchActive(int matchId, boolean activate) {
        if (AppConfiguration.getInstance().isOrdered()) {
            for (Match match : orderedMatchList) {
                if (activate) {
                    if (match.getIsActive() && match.getId() != matchId) {
                        setMatchComplete(matchId, true);
                    }
                    if (match.getId() == matchId && !match.getIsComplete()) {
                        match.setIsActive(true);
                    }
                }
                else {
                    if (match.getId() == matchId) {
                        match.setIsActive(false);
                    }
                }
            }
        }
        else {
            for (Match match : matchesList) {
                if (activate) {
                    if (match.getIsActive() && match.getId() != matchId) {
                        setMatchComplete(matchId, true);
                    }
                    if (match.getId() == matchId && !match.getIsComplete()) {
                        match.setIsActive(true);
                    }
                }
                else {
                    if (match.getId() == matchId) {
                        match.setIsActive(false);
                    }
                }
            }
        }
    }

    public void setMatchComplete(int matchId, boolean complete) {
        if (AppConfiguration.getInstance().isOrdered()) {
            for (Match match : orderedMatchList) {
                if (match.getId() == matchId) {
                    if (complete) {
                        match.setIsComplete(true);
                        if (match.getIsActive()) {
                            match.setIsActive(false);
                        }
                        completeMatchesList.add(match);
                        incompleteMatchesList.remove(match);
                    } else {
                        match.setIsComplete(false);
                        completeMatchesList.remove(match);
                        incompleteMatchesList.add(match);
                    }
                }
            }
        }
        else {
            for (Match match : matchesList) {
                if (match.getId() == matchId) {
                    if (complete) {
                        match.setIsComplete(true);
                        if (match.getIsActive()) {
                            match.setIsActive(false);
                        }
                        completeMatchesList.add(match);
                        incompleteMatchesList.remove(match);
                    } else {
                        match.setIsComplete(false);
                        completeMatchesList.remove(match);
                        incompleteMatchesList.add(match);
                    }
                }
            }
        }
    }

    public String getMatchAsString(int matchId) throws JsonProcessingException, MatchesNotFoundException {
        if (AppConfiguration.getInstance().isOrdered()) {
            for (Match match : orderedMatchList) {
                if (match.getId() == matchId) {
                    return jsonMapper.writeValueAsString(match);
                }
            }
        }
        else {
            for (Match match : matchesList) {
                if (match.getId() == matchId) {
                    return jsonMapper.writeValueAsString(match);
                }
            }
        }
        log.debug("Match not found");
        throw new MatchesNotFoundException("Match not found");
    }

    public String getActiveMatch() throws IOException, MatchesNotFoundException {
        if (AppConfiguration.getInstance().isOrdered()) {
            for (Match match : orderedMatchList) {
                if (match.getIsActive()) {
                    return jsonMapper.writeValueAsString(match);
                }
            }
        }
        else {
            for (Match match : matchesList) {
                if (match.getIsActive()) {
                    return jsonMapper.writeValueAsString(match);
                }
            }
        }
        log.debug("Match not found");
        throw new MatchesNotFoundException("Match not found");
    }

    public String getMatchByOrder(int order) throws JsonProcessingException, MatchesNotFoundException {
        if (AppConfiguration.getInstance().isOrdered()) {
            for (Match match : orderedMatchList) {
                if (match.getOrder() == order) {
                    return jsonMapper.writeValueAsString(match);
                }
            }
        }
        else {
            for (Match match : matchesList) {
                if (match.getOrder() == order) {
                    return jsonMapper.writeValueAsString(match);
                }
            }
        }
        log.debug("Match not found");
        throw new MatchesNotFoundException("Match not found");
    }

    public String getMatchesByParticipant(String participantName, boolean onlyIncomplete) throws JsonProcessingException, IncompleteMatchesNotFoundException, MatchesNotFoundException {
        ArrayList<Match> matchesByParticipant = new ArrayList<>();
        ArrayList<Match> incompleteMatchesByParticipant = new ArrayList<>();
        if (AppConfiguration.getInstance().isOrdered()) {
            for (Match match : orderedMatchList) {
                String participant1Name;
                String participant2Name;
                if (match.getParticipant_1().getName().isEmpty()) {
                    participant2Name = match.getParticipant_2().getName().toLowerCase(Locale.ROOT);
                    if (participant2Name.contains(participantName.toLowerCase(Locale.ROOT))) {
                        matchesByParticipant.add(match);
                        if (match.getIsComplete()) {
                                incompleteMatchesByParticipant.add(match);
                        }
                    }
                } else if (match.getParticipant_2().getName().isEmpty()) {
                    participant1Name = match.getParticipant_1().getName().toLowerCase(Locale.ROOT);
                    if (participant1Name.contains(participantName.toLowerCase(Locale.ROOT))) {
                        matchesByParticipant.add(match);
                        if (match.getIsComplete()) {
                            incompleteMatchesByParticipant.add(match);
                        }
                    }
                } else {
                    participant1Name = match.getParticipant_1().getName().toLowerCase(Locale.ROOT);
                    participant2Name = match.getParticipant_2().getName().toLowerCase(Locale.ROOT);
                    if (participant1Name.contains(participantName.toLowerCase(Locale.ROOT)) || participant2Name.contains(participantName.toLowerCase(Locale.ROOT))) {
                        matchesByParticipant.add(match);
                        if (match.getIsComplete()) {
                            incompleteMatchesByParticipant.add(match);
                        }
                    }
                }
            }
        }
        else {
            for (Match match : matchesList) {
                if (match.getParticipant_1().getName().isEmpty()) {
                    if (match.getParticipant_2().getName().toLowerCase(Locale.ROOT).contains(participantName.toLowerCase(Locale.ROOT))) {
                        matchesByParticipant.add(match);
                        if (match.getIsComplete()) {
                            incompleteMatchesByParticipant.add(match);
                        }
                    }
                } else if (match.getParticipant_2().getName().isEmpty()) {
                    if (match.getParticipant_1().getName().toLowerCase(Locale.ROOT).contains(participantName.toLowerCase(Locale.ROOT))) {
                        matchesByParticipant.add(match);
                        if (match.getIsComplete()) {
                            incompleteMatchesByParticipant.add(match);
                        }
                    }
                } else {
                    if (match.getParticipant_1().getName().toLowerCase(Locale.ROOT).contains(participantName.toLowerCase(Locale.ROOT)) || match.getParticipant_2().getName().toLowerCase(Locale.ROOT).equals(participantName.toLowerCase(Locale.ROOT))) {
                        matchesByParticipant.add(match);
                        if (match.getIsComplete()) {
                            incompleteMatchesByParticipant.add(match);
                        }
                    }
                }
            }
        }
        if (onlyIncomplete) {
            if (incompleteMatchesByParticipant.isEmpty()) {
                log.error("Error in call to getMatchesByParticipant");
                throw new IncompleteMatchesNotFoundException("Error!\nNo incomplete matches found");
            }
            return jsonMapper.writeValueAsString(incompleteMatchesByParticipant);
        }
        if (matchesByParticipant.isEmpty()) {
            log.error("Error in call to getMatchesByParticipant");
            throw new MatchesNotFoundException("Error!\nNo matches found");
        }
        return jsonMapper.writeValueAsString(matchesByParticipant);
    }

    public String getMatchesListAsString() throws JsonProcessingException, MatchesNotFoundException {
        if (matchesList.isEmpty()) {
            log.error("Error in call to getMatchesListAsString");
            throw new MatchesNotFoundException("Error!\nNo matches found");
        }
        return jsonMapper.writeValueAsString(matchesList);
    }

    public String getCompleteMatchesListAsString() throws JsonProcessingException, CompleteMatchesNotFoundException {
        if (completeMatchesList.isEmpty()) {
            log.error("Error in call to getCompleteMatchesListAsString");
            throw new CompleteMatchesNotFoundException("Error!\nNo complete matches found");
        }
        return jsonMapper.writeValueAsString(completeMatchesList);
    }

    public String getIncompleteMatchesListAsString() throws JsonProcessingException, IncompleteMatchesNotFoundException {
        if (incompleteMatchesList.isEmpty()) {
            log.error("Error in call to getIncompleteMatchesListAsString");
            throw new IncompleteMatchesNotFoundException("Error!\nNo incomplete matches found");
        }
        return jsonMapper.writeValueAsString(incompleteMatchesList);
    }

    public String getOrderedMatchListAsString() throws JsonProcessingException, MatchesNotFoundException {
        if (orderedMatchList.isEmpty()) {
            log.error("Error in call to getOrderedMatchListAsString");
            throw new MatchesNotFoundException("Error!\nNo matches found");
        }
        return jsonMapper.writeValueAsString(orderedMatchList);
    }

    public void setMatchOrder(int matchId, int order) {
        for (Match match : orderedMatchList) {
            if (match.getId().equals(matchId) && match.getOrder() != order) {
                match.setOrder(order);
            }
        }
        Collections.sort(orderedMatchList);
    }

    public String getNextMatch() throws IOException, MatchesNotFoundException {
        if (AppConfiguration.getInstance().isOrdered()) {
            for (Match match : orderedMatchList) {
                if (!match.getIsComplete() && !match.getIsActive()) {
                    return jsonMapper.writeValueAsString(match);
                }
            }
        }
        else {
            for (Match match : matchesList) {
                if (!match.getIsComplete() && !match.getIsActive()) {
                    return jsonMapper.writeValueAsString(match);
                }
            }
        }
        log.debug("Match not found");
        throw new MatchesNotFoundException("Match not found");
    }

    private void updateOrderedMatchList(Match match) {
        for (Match orderedMatch : orderedMatchList) {
            if (match.getId().equals(orderedMatch.getId()) && !match.getHash().equals(orderedMatch.getHash())) {
                orderedMatch.setParticipant_1(match.getParticipant_1());
                orderedMatch.setParticipant_2(match.getParticipant_2());
                orderedMatch.setIsActive(match.getIsActive());
                orderedMatch.setIsComplete(match.getIsComplete());
            }
        }
        if (!orderedMatchList.contains(match)) {
            orderedMatchList.add(match);
        }
        Collections.sort(orderedMatchList);
    }

    public int addMatch(Participant participant1, Participant participant2) throws CloneNotSupportedException {
        customMatchesId++;
        Match match = new Match(customMatchesId, participant1, participant2);
        matchesList.add(match);
        incompleteMatchesList.add(match);
        orderedMatchList.add((Match) match.clone());
        return match.getId();
    }

    public void removeMatch(Match match) {
        if (AppConfiguration.getInstance().isOrdered()) {
            orderedMatchList.remove(match);
        }
        else {
            matchesList.remove(match);
        }
        if (incompleteMatchesList.contains(match)) {
            incompleteMatchesList.remove(match);
        }
        if (completeMatchesList.contains(match)) {
            completeMatchesList.remove(match);
        }
    }

    public Match getMatch(int matchId) throws MatchesNotFoundException {
        if (AppConfiguration.getInstance().isOrdered()) {
            for (Match match : orderedMatchList) {
                if (match.getId() == matchId) {
                    return match;
                }
            }
        }
        else {
            for (Match match : matchesList) {
                if (match.getId() == matchId) {
                    return match;
                }
            }
        }
        log.debug("Match not found");
        throw new MatchesNotFoundException("Match not found");
    }

    public void setScore(int matchId, int p1Score, int p2Score) throws MatchesNotFoundException {
        Match match = getMatch(matchId);
        StringBuilder sb = new StringBuilder();
        sb.append(match.getParticipant_1().getScore());
        sb.append(":");
        sb.append(match.getParticipant_2().getScore());
        if (p1Score != 0) {
            match.getParticipant_1().setScore(p1Score);
            sb.deleteCharAt(0);
            sb.insert(0, p1Score);
        }
        if (p2Score != 0) {
            match.getParticipant_2().setScore(p2Score);
            sb.deleteCharAt(2);
            sb.append(p2Score);
        }
        match.setResultScore(sb.toString());
    }

    @PostConstruct
    public void init() {
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        instance = this;
    }
}
