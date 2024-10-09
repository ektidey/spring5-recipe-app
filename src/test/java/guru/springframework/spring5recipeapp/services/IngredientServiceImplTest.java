package guru.springframework.spring5recipeapp.services;

import guru.springframework.spring5recipeapp.commands.IngredientCommand;
import guru.springframework.spring5recipeapp.converters.IngredientCommandToIngredient;
import guru.springframework.spring5recipeapp.converters.IngredientToIngredientCommand;
import guru.springframework.spring5recipeapp.converters.UnitOfMeasureCommandToUnitOfMeasure;
import guru.springframework.spring5recipeapp.converters.UnitOfMeasureToUnitOfMeasureCommand;
import guru.springframework.spring5recipeapp.domain.Ingredient;
import guru.springframework.spring5recipeapp.domain.Recipe;
import guru.springframework.spring5recipeapp.repositories.RecipeRepository;
import guru.springframework.spring5recipeapp.repositories.UnitOfMeasureRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class IngredientServiceImplTest {

    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private UnitOfMeasureRepository unitOfMeasureRepository;
    private IngredientToIngredientCommand ingredientToIngredientCommand = new IngredientToIngredientCommand(
        new UnitOfMeasureToUnitOfMeasureCommand()
    );
    private IngredientCommandToIngredient ingredientCommandToIngredient = new IngredientCommandToIngredient(
        new UnitOfMeasureCommandToUnitOfMeasure()
    );
    private IngredientServiceImpl ingredientService;
    private AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        ingredientService = new IngredientServiceImpl(
            ingredientToIngredientCommand, ingredientCommandToIngredient, recipeRepository, unitOfMeasureRepository
        );
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void findByRecipeIdAndIngredientId() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setId(1L);
        Ingredient ingredient2 = new Ingredient();
        ingredient2.setId(2L);
        Ingredient ingredient3 = new Ingredient();
        ingredient3.setId(3L);

        recipe.addIngredient(ingredient1);
        recipe.addIngredient(ingredient2);
        recipe.addIngredient(ingredient3);

        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));

        IngredientCommand ingredientCommand = ingredientService.findByRecipeIdAndIngredientId(1L, 3L);

        assertNotNull(ingredientCommand);
        assertEquals(3L, ingredientCommand.getId());
        assertEquals(1L, ingredientCommand.getRecipeId());
        verify(recipeRepository, times(1)).findById(anyLong());
    }

    @Test
    void deleteByRecipeIdAndIngredientId() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setId(1L);
        Ingredient ingredient2 = new Ingredient();
        ingredient2.setId(2L);
        Ingredient ingredient3 = new Ingredient();
        ingredient3.setId(3L);

        recipe.addIngredient(ingredient1);
        recipe.addIngredient(ingredient2);
        recipe.addIngredient(ingredient3);

        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));

        ingredientService.deleteByRecipeIdAndIngredientId(1L, 3L);

        verify(recipeRepository, times(1)).findById(any());
        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }

    @Test
    void saveIngredientCommand() {
        IngredientCommand command = new IngredientCommand();
        command.setId(3L);
        command.setRecipeId(2L);

        Recipe savedRecipe = new Recipe();
        savedRecipe.addIngredient(ingredientCommandToIngredient.convert(command));

        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(new Recipe()));
        when(recipeRepository.save(any())).thenReturn(savedRecipe);

        IngredientCommand savedCommand = ingredientService.saveIngredientCommand(command);

        assertNotNull(savedCommand);
        assertEquals(3L, savedCommand.getId());
        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }
}