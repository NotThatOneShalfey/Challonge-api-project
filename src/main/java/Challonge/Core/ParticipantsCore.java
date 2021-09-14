package Challonge.Core;

import Challonge.Configuration.AppConfiguration;
import Challonge.Entities.Participant;
import Challonge.Exceptions.ParticipantNotFoundException;
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
import java.util.ArrayList;
import java.util.Locale;

@Slf4j
@Service
public class ParticipantsCore {
    @Getter
    private static ParticipantsCore instance;

    @Getter
    private ArrayList<Participant> participantsList = new ArrayList<>();

    JsonMapper jsonMapper = new JsonMapper();

    private RestTemplate restTemplate = new RestTemplate();

    private int customParticipantsId = 0;

    public void loadParticipants(String tournament) throws TournamentNotFoundException {
        participantsList.clear();

        String apiKey = AppConfiguration.getInstance().getApiKey();
        String account = AppConfiguration.getInstance().getAccount();
        String method = "/tournaments/{tournament}/participants.json";
        String completeUrl = AppConfiguration.HTTPS_PREFIX + account + ":" + apiKey + "@" + AppConfiguration.CHALLONGE_URL + method;

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.exchange(completeUrl, HttpMethod.GET, AppConfiguration.getInstance().getEntity(), String.class, tournament);
        } catch (HttpClientErrorException e) {
            log.error("Error in call to loadParticipants\nError - {}", e.getMessage());
            throw new TournamentNotFoundException("Error!\nNo tournament found with id = " + tournament);
        }
        JSONArray jsonArray = new JSONArray(responseEntity.getBody());
        for (Object obj : jsonArray) {
            participantsList.add(new Participant(new JSONObject(obj.toString()).getJSONObject("participant").getInt("id"), new JSONObject(obj.toString()).getJSONObject("participant").getString("name")));
        }
    }

    public void addParticipant(String participantName) {
        customParticipantsId++;
        Participant participant = new Participant(customParticipantsId, participantName);
        participantsList.add(participant);
    }

    public void removeParticipant(Participant participant) {
        participantsList.remove(participant);
    }

    public String getParticipantByNameAsString(String participantName) throws JsonProcessingException, ParticipantNotFoundException {
        for (Participant participant : participantsList) {
            if (participant.getName().toLowerCase(Locale.ROOT).equals(participantName.toLowerCase(Locale.ROOT))) {
                return jsonMapper.writeValueAsString(participant);
            }
        }
        log.debug("Participant not found");
        throw new ParticipantNotFoundException("Participant not found");
    }

    public String getParticipantByIdAsString(int participantId) throws JsonProcessingException, ParticipantNotFoundException {
        for (Participant participant : participantsList) {
            if (participant.getId() == participantId) {
                return jsonMapper.writeValueAsString(participant);
            }
        }
        log.debug("Participant not found");
        throw new ParticipantNotFoundException("Participant not found");
    }

    public Participant getParticipant(int participantId) throws ParticipantNotFoundException {
        for (Participant participant : participantsList) {
            if (participant.getId() == participantId) {
                return participant;
            }
        }
        log.debug("Participant not found");
        throw new ParticipantNotFoundException("Participant not found");
    }

    public String getParticipantsListAsString() throws JsonProcessingException {
        return jsonMapper.writeValueAsString(participantsList);
    }

    @PostConstruct
    public void init() {
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        instance = this;
    }
}
