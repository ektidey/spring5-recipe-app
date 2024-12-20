package guru.springframework.spring5recipeapp.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(exclude = {"recipe"})
@Entity
public class Notes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Recipe recipe;

    @Column(columnDefinition = "text")
    private String recipeNotes;

    @Override
    public String toString() {
        return "Recipe notes " + id;
    }

}
