package Challonge.Core;

import Challonge.Configuration.AppConfiguration;
import Challonge.Exceptions.MatchesNotFoundException;
import Challonge.Exceptions.TournamentNotFoundException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;


@Slf4j
@Service
@EnableScheduling
public class ProcessCore {

    @Getter
    private static ProcessCore instance;

    public void downloadTournament(String tournament) throws TournamentNotFoundException, CloneNotSupportedException {
        ParticipantsCore.getInstance().loadParticipants(tournament);
        MatchesCore.getInstance().getOrderedMatchList().clear();
        MatchesCore.getInstance().loadMatches(tournament);
    }

    @Scheduled(fixedDelay = 30000, initialDelay = 10000)
    private void updateMatchesInfo() throws TournamentNotFoundException, IOException, CloneNotSupportedException {
        if (AppConfiguration.getInstance().isScheduling()) {
            log.debug("<----- updating matches info ----->");
            if (AppConfiguration.getInstance().getTournament() != null) {
                if (ParticipantsCore.getInstance().getParticipantsList().isEmpty()) {
                    log.debug("<----- reloading participants ----->");
                    ParticipantsCore.getInstance().loadParticipants(AppConfiguration.getInstance().getTournament());
                }
                log.debug("<----- reloading matches ----->");
                MatchesCore.getInstance().loadMatches(AppConfiguration.getInstance().getTournament());
            }
            FilesCore.getInstance().generateMatchesToFiles();
        }
    }

    @PostConstruct
    public void init() throws IOException {
        instance = this;
        if (AppConfiguration.getInstance().isDownloadCustomTournament() && AppConfiguration.getInstance().getTournament() == null) {
            log.info("<----- custom tournament downloaded from file ----->");
            FilesCore.getInstance().downloadTournamentFromFiles();
        }
    }

    @PreDestroy
    public void destroy() throws MatchesNotFoundException, IOException {
        if (AppConfiguration.getInstance().isSaveCustomTournament()) {
            FilesCore.getInstance().saveTournamentToFile();
            FilesCore.getInstance().saveParticipantsToFile();
        }
    }

}
