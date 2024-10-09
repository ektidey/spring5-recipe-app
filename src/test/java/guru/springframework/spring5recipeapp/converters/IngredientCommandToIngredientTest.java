package guru.springframework.spring5recipeapp.converters;

import guru.springframework.spring5recipeapp.commands.IngredientCommand;
import guru.springframework.spring5recipeapp.commands.UnitOfMeasureCommand;
import guru.springframework.spring5recipeapp.domain.Ingredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class IngredientCommandToIngredientTest {

    public static final BigDecimal AMOUNT = new BigDecimal("1");
    public static final String DESCRIPTION = "Cheeseburger";
    public static final Long ID_VALUE = 1L;
    public static final Long UOM_ID = 2L;

    IngredientCommandToIngredient converter;

    @BeforeEach
    public void setUp() {
        converter = new IngredientCommandToIngredient(new UnitOfMeasureCommandToUnitOfMeasure());
    }

    @Test
    public void testNullObject() {
        assertNull(converter.convert(null));
    }

    @Test
    public void testEmptyObject() {
        assertNotNull(converter.convert(new IngredientCommand()));
    }

    @Test
    public void convert() {
        IngredientCommand command = new IngredientCommand();
        command.setId(ID_VALUE);
        command.setDescription(DESCRIPTION);
        command.setAmount(AMOUNT);

        UnitOfMeasureCommand uomCommand = new UnitOfMeasureCommand();
        uomCommand.setId(UOM_ID);
        command.setUom(uomCommand);

        Ingredient ingredient = converter.convert(command);

        assertNotNull(ingredient);
        assertNotNull(ingredient.getUom());
        assertEquals(ID_VALUE, ingredient.getId());
        assertEquals(DESCRIPTION, ingredient.getDescription());
        assertEquals(AMOUNT, ingredient.getAmount());
        assertEquals(UOM_ID, ingredient.getUom().getId());
    }

    @Test
    public void convertWithNullUOM() {
        IngredientCommand command = new IngredientCommand();
        command.setId(ID_VALUE);
        command.setDescription(DESCRIPTION);
        command.setAmount(AMOUNT);

        Ingredient ingredient = converter.convert(command);

        assertNotNull(ingredient);
        assertEquals(ID_VALUE, ingredient.getId());
        assertEquals(DESCRIPTION, ingredient.getDescription());
        assertEquals(AMOUNT, ingredient.getAmount());
        assertNull(ingredient.getUom());
    }

}