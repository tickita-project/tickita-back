package back.tickita.domain.crews.entity;

import back.tickita.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Crews extends BaseEntity {
    private String crewName;

    private String labelColor;

    @OneToMany(mappedBy = "crews", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CrewList> crewLists;

    public Crews(String crewName, String labelColor) {
        this.crewName = crewName;
        this.labelColor = labelColor;
    }

    public void setCrews(String newCrewName, String newLabelColor) {
        this.crewName = newCrewName;
        this.labelColor = newLabelColor;
    }
}
