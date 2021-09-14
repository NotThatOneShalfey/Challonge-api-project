package Challonge.Configuration;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Collections;

@Data
@Configuration
@ConfigurationProperties(prefix = "challonge-api")
@Slf4j
public class AppConfiguration {
    @Getter
    private static AppConfiguration instance;

    private String account = "ShalfeyTheFlower";
    private String apiKey = "MYV1GdafUzsTLbNlc4uIp1QwB2Ec8GSmlXyOjagl";
    private boolean scheduling = false;
    private String tournament;
    private HttpHeaders headers = new HttpHeaders();
    private HttpEntity<String> entity = new HttpEntity<>("", headers);
    private String nameDelimiter = "\\|";
    private String specDelimiter = "\\(([^)]+)\\)";
    private boolean generateFiles = false;
    private boolean saveCustomTournament = true;
    private boolean downloadCustomTournament = true;
    private boolean ordered = false;

    public static final String NEXT_MATCH_PATH = System.getProperty("user.dir") + File.separator + "next_match.txt";
    public static final String ACTIVE_MATCH_PATH = System.getProperty("user.dir") + File.separator + "active_match.txt";
    public static final String TOURNAMENT_PATH = System.getProperty("user.dir") + File.separator + "tournament.txt";
    public static final String PARTICIPANTS_PATH = System.getProperty("user.dir") + File.separator + "participants.txt";
    public static final String HTTPS_PREFIX = "https://";
    public static final String CHALLONGE_URL = "api.challonge.com/v1";

    @PostConstruct
    public void init() {
        headers.setBasicAuth(account, apiKey);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        log.debug("<----- startup parameters ----->");
        log.debug("generateFiles is {}", generateFiles);
        log.debug("saveCustomTournament is {}", saveCustomTournament);
        log.debug("downloadCustomTournament is {}", downloadCustomTournament);
        log.debug("ordered is {}", ordered);
        log.debug("tournamentID is {}", tournament);
        log.debug("scheduling is {}", scheduling);
        log.debug("<------------------------------>");
        instance = this;
    }

}
