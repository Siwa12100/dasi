package td1.jeanico.patiment.modeles.mediums;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import td1.jeanico.patiment.modeles.utilisateurs.Genre;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Medium implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long ID;

    protected String denomination;

    @Enumerated(EnumType.STRING)
    protected Genre genre;

    protected String presentation;

    public Medium() {
    }

    public Medium(String denomination, Genre genre, String presentation) {
        this.denomination = denomination;
        this.genre = genre;
        this.presentation = presentation;
    }

    public Long getId() {
        return ID;
    }

    public void setId(Long ID) {
        this.ID = ID;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ID);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Medium other = (Medium) obj;
        return Objects.equals(this.ID, other.ID);
    }
}
