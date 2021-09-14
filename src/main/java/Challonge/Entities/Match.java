package Challonge.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class Match implements Comparable<Match>, Cloneable{
    private Integer id;
    private Participant participant_1;
    private Participant participant_2;
    private Boolean isActive = false;
    private Boolean isComplete = false;
    private Integer order = 0;
    private String resultScore;

    public Match(int id, Participant participant_1, Participant participant_2) {
        this.id = id;
        this.participant_1 = participant_1;
        this.participant_2 = participant_2;
    }

    public Match(int id) {
        this.id = id;
    }

    @JsonIgnore
    public String getHash() {
        if (participant_1 != null && participant_2 != null) {
            return id + ":" + participant_1.getId() + ":" + participant_1.getName() + ":" + participant_2.getId() + ":" + participant_2.getName() + ":" + isActive + ":" + isComplete + ":" + order;
        }
        else if (participant_1 != null) {
            return id + ":" + participant_1.getId() + ":" + participant_1.getName() + ":" + isActive + ":" + isComplete + ":" + order;
        }
        return id + ":" + participant_2.getId() + ":" + participant_2.getName() + ":" + isActive + ":" + isComplete + ":" + order;
    }

    @Override
    public int compareTo(Match matchToCompare) {
        if (order.compareTo(matchToCompare.getOrder()) == 0) {
            return id.compareTo(matchToCompare.getId());
        }
        return order.compareTo(matchToCompare.getOrder());
    }

    @Override
    public boolean equals(Object o) {
        if (this == (o)) {
            return true;
        }
        Match match = (Match) o;
        if (match.getId().equals(id)) {
            return true;
        }
        return false;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
