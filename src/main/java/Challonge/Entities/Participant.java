package Challonge.Entities;

import Challonge.Configuration.AppConfiguration;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
public class Participant {
    private int id;
    private String name = "";
    private String specification = "";

    @JsonIgnore
    private int score = 0;

    private final static Pattern p = Pattern.compile(AppConfiguration.getInstance().getSpecDelimiter());

    public Participant(int id, String name) {
        this.id = id;
        List<String> genericArray = Arrays.asList(name.split(AppConfiguration.getInstance().getNameDelimiter()));
        List<String> names = new ArrayList<>();
        List<String> specs = new ArrayList<>();
        for (String element : genericArray) {
            Matcher m = p.matcher(element);
            if (m.find()) {
                specs.add(m.group());
                element = element.replace(m.group(), "");
            }
            names.add(element.trim());
        }
        this.name = String.join(":", names);
        this.specification = String.join(":", specs);
    }

    public Participant(int id) {
        this.id = id;
    }
}
