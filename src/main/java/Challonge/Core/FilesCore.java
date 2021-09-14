package Challonge.Core;

import Challonge.Configuration.AppConfiguration;
import Challonge.Entities.Match;
import Challonge.Entities.Participant;
import Challonge.Exceptions.MatchesNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Scanner;

@Service
@Data
@Slf4j
public class FilesCore {
    @Getter
    private static FilesCore instance;

    public void generateMatchesToFiles() throws IOException {
        if (AppConfiguration.getInstance().isGenerateFiles()) {
            log.debug("<----- next match generated to file ----->");
            getNextMatchToFile();
            log.debug("<----- active match generated to file ----->");
            getActiveMatchToFile();
        }
    }

    private void getNextMatchToFile() throws IOException {
        if (AppConfiguration.getInstance().isOrdered()) {
            for (Match match : MatchesCore.getInstance().getOrderedMatchList()) {
                if (!match.getIsComplete() && !match.getIsActive()) {
                    StringBuilder sb = new StringBuilder();
                    if (match.getParticipant_1() == null) {
                        sb.append("Player yet unknown");
                    }
                    else {
                        sb.append(match.getParticipant_1().getName());
                        sb.append(match.getParticipant_1().getSpecification());
                    }
                    sb.append(" vs ");
                    if (match.getParticipant_2() == null) {
                        sb.append("Player yet unknown");
                    }
                    else {
                        sb.append(match.getParticipant_2().getName());
                        sb.append(match.getParticipant_2().getSpecification());
                    }
                    File file = new File(AppConfiguration.NEXT_MATCH_PATH);
                    generateFile(sb.toString(), file);
                    break;
                }
            }
        }
        else {
            for (Match match : MatchesCore.getInstance().getMatchesList()) {
                if (!match.getIsComplete() && !match.getIsActive()) {
                    StringBuilder sb = new StringBuilder();
                    if (match.getParticipant_1() == null) {
                        sb.append("Player yet unknown");
                    }
                    else {
                        sb.append(match.getParticipant_1().getName());
                        sb.append(match.getParticipant_1().getSpecification());
                    }
                    sb.append(" vs ");
                    if (match.getParticipant_2() == null) {
                        sb.append("Player yet unknown");
                    }
                    else {
                        sb.append(match.getParticipant_2().getName());
                        sb.append(match.getParticipant_2().getSpecification());
                    }
                    File file = new File(AppConfiguration.NEXT_MATCH_PATH);
                    generateFile(sb.toString(), file);
                    break;
                }
            }
        }
    }

    private void getActiveMatchToFile() throws IOException {
        for (Match match : MatchesCore.getInstance().getMatchesList()) {
            if (match.getIsActive()) {
                StringBuilder sb = new StringBuilder();
                if (match.getParticipant_1() == null) {
                    sb.append("Player yet unknown");
                }
                else {
                    sb.append(match.getParticipant_1().getName());
                    sb.append(match.getParticipant_1().getSpecification());
                }
                sb.append(" vs ");
                if (match.getParticipant_2() == null) {
                    sb.append("Player yet unknown");
                }
                else {
                    sb.append(match.getParticipant_2().getName());
                    sb.append(match.getParticipant_2().getSpecification());
                }
                File file = new File(AppConfiguration.ACTIVE_MATCH_PATH);
                generateFile(sb.toString(), file);
                break;
            }
        }
    }

    public void downloadTournamentFromFiles() throws IOException {
        String tournament = readFile(AppConfiguration.TOURNAMENT_PATH);
        if (!tournament.equals("")) {
            JSONArray jsonArray = new JSONArray(tournament);
            for (Object obj : jsonArray) {
                JSONObject jsonObject = new JSONObject(obj.toString());
                Match match = new Match(jsonObject.getInt("id"));

                Participant participant1 = new Participant(jsonObject.getJSONObject("participant_1").getInt("id"));
                participant1.setName(jsonObject.getJSONObject("participant_1").getString("name"));
                participant1.setSpecification(jsonObject.getJSONObject("participant_1").getString("specification"));

                Participant participant2 = new Participant(jsonObject.getJSONObject("participant_2").getInt("id"));
                participant2.setName(jsonObject.getJSONObject("participant_2").getString("name"));
                participant2.setSpecification(jsonObject.getJSONObject("participant_2").getString("specification"));

                match.setParticipant_1(participant1);
                match.setParticipant_2(participant2);
                match.setIsActive(jsonObject.getBoolean("isActive"));
                match.setIsComplete(jsonObject.getBoolean("isComplete"));
                match.setOrder(jsonObject.getInt("order"));
                if (jsonObject.get("resultScore").equals(JSONObject.NULL)) {
                    match.setResultScore("");
                } else {
                    match.setResultScore(jsonObject.getString("resultScore"));
                }
                if (AppConfiguration.getInstance().isOrdered()) {
                    MatchesCore.getInstance().getOrderedMatchList().add(match);
                } else {
                    MatchesCore.getInstance().getMatchesList().add(match);
                }
            }
        }

        String participants = readFile(AppConfiguration.PARTICIPANTS_PATH);
        if (!participants.equals("")) {
            JSONArray jsonArray = new JSONArray(participants);
            for (Object obj : jsonArray) {
                JSONObject jsonObject = new JSONObject(obj.toString());
                Participant participant = new Participant(jsonObject.getInt("id"));
                participant.setName(jsonObject.getString("name"));
                participant.setSpecification(jsonObject.getString("specification"));
                ParticipantsCore.getInstance().getParticipantsList().add(participant);
            }
        }
    }

    private String readFile(String path) throws IOException {
        Scanner in = new Scanner(new FileReader(path));
        StringBuilder sb = new StringBuilder();
        while(in.hasNext()) {
            sb.append(in.next());
        }
        in.close();
        return sb.toString();
    }

    public void saveTournamentToFile() throws MatchesNotFoundException, IOException {
        File file = new File(AppConfiguration.TOURNAMENT_PATH);
        if (AppConfiguration.getInstance().isOrdered()) {
            generateFile(MatchesCore.getInstance().getOrderedMatchListAsString(), file);
        }
        else {
            generateFile(MatchesCore.getInstance().getMatchesListAsString(), file);
        }
    }

    public void saveParticipantsToFile() throws IOException {
        File file = new File(AppConfiguration.PARTICIPANTS_PATH);
        generateFile(ParticipantsCore.getInstance().getParticipantsListAsString(), file);
    }

    public void generateFile(String str, File file) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] strToBytes = str.getBytes();
        outputStream.write(strToBytes);
        outputStream.flush();
        outputStream.close();
    }

    @PostConstruct
    public void init() {
        instance = this;
    }
}
