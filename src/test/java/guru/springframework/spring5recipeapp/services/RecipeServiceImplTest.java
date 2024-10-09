package guru.springframework.spring5recipeapp.services;

import guru.springframework.spring5recipeapp.commands.RecipeCommand;
import guru.springframework.spring5recipeapp.converters.RecipeCommandToRecipe;
import guru.springframework.spring5recipeapp.converters.RecipeToRecipeCommand;
import guru.springframework.spring5recipeapp.domain.Recipe;
import guru.springframework.spring5recipeapp.repositories.RecipeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class RecipeServiceImplTest {

    RecipeServiceImpl recipeService;
    @Mock
    RecipeRepository recipeRepository;
    @Mock
    RecipeCommandToRecipe recipeCommandToRecipe;
    @Mock
    RecipeToRecipeCommand recipeToRecipeCommand;
    private AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        recipeService = new RecipeServiceImpl(recipeRepository, recipeCommandToRecipe, recipeToRecipeCommand);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    public void getRecipes() {
        Recipe recipe = new Recipe();
        HashSet<Recipe> recipeData = new HashSet<>();
        recipeData.add(recipe);

        when(recipeRepository.findAll()).thenReturn(recipeData);

        Set<Recipe> recipeSet = recipeService.getRecipes();

        assertEquals(recipeSet.size(), 1);
        verify(recipeRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void getRecipeById() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        when(recipeRepository.findById(anyLong()))
            .thenReturn(Optional.of(recipe));

        Recipe recipeReturned = recipeService.findById(1L);

        assertNotNull(recipeReturned, "Null recipe returned");
        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, never()).findAll();
    }

    @Test
    public void deleteRecipeById() {
        Long idToDelete = 2L;
        recipeService.deleteById(idToDelete);

        verify(recipeRepository, times(1))
            .deleteById(anyLong());
    }

    @Test
    public void getRecipeCommandById() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        when(recipeRepository.findById(anyLong()))
            .thenReturn(Optional.of(recipe));

        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId(1L);

        when(recipeToRecipeCommand.convert(any())).thenReturn(recipeCommand);

        RecipeCommand commandById = recipeService.findCommandById(1L);

        assertNotNull(commandById, "Null recipe command returned");
        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, never()).findAll();
    }
}