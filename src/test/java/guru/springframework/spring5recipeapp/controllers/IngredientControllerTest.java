package guru.springframework.spring5recipeapp.controllers;

import guru.springframework.spring5recipeapp.commands.IngredientCommand;
import guru.springframework.spring5recipeapp.commands.RecipeCommand;
import guru.springframework.spring5recipeapp.services.IngredientService;
import guru.springframework.spring5recipeapp.services.RecipeService;
import guru.springframework.spring5recipeapp.services.UnitOfMeasureService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashSet;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class IngredientControllerTest {

    @Mock
    private RecipeService recipeService;
    @Mock
    private IngredientService ingredientService;
    @Mock
    private UnitOfMeasureService unitOfMeasureService;
    private AutoCloseable openMocks;
    private IngredientController controller;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        controller = new IngredientController(recipeService, ingredientService, unitOfMeasureService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    public void testListIngredients() throws Exception {
        RecipeCommand recipeCommand = new RecipeCommand();
        when(recipeService.findCommandById(anyLong())).thenReturn(recipeCommand);

        mockMvc.perform(get("/recipe/1/ingredients"))
            .andExpect(status().isOk())
            .andExpect(view().name("recipe/ingredient/list"))
            .andExpect(model().attributeExists("recipe"));

        verify(recipeService, times(1)).findCommandById(anyLong());
    }

    @Test
    public void testShowIngredient() throws Exception {
        IngredientCommand ingredientCommand = new IngredientCommand();

        when(ingredientService.findByRecipeIdAndIngredientId(anyLong(), anyLong())).thenReturn(ingredientCommand);

        mockMvc.perform(get("/recipe/1/ingredient/2/show"))
            .andExpect(status().isOk())
            .andExpect(view().name("recipe/ingredient/show"))
            .andExpect(model().attributeExists("ingredient"));
    }

    @Test
    public void testNewIngredient() throws Exception {
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId(1L);

        when(recipeService.findCommandById(anyLong())).thenReturn(recipeCommand);
        when(unitOfMeasureService.listAllUoms()).thenReturn(new HashSet<>());

        mockMvc.perform(get("/recipe/1/ingredient/new"))
            .andExpect(status().isOk())
            .andExpect(view().name("recipe/ingredient/ingredientform"))
            .andExpect(model().attributeExists("ingredient"))
            .andExpect(model().attributeExists("uomList"));

        verify(recipeService, times(1)).findCommandById(anyLong());
    }

    @Test
    public void testUpdateIngredient() throws Exception {
        IngredientCommand ingredientCommand = new IngredientCommand();

        when(ingredientService.findByRecipeIdAndIngredientId(anyLong(), anyLong())).thenReturn(ingredientCommand);
        when(unitOfMeasureService.listAllUoms()).thenReturn(new HashSet<>());

        mockMvc.perform(get("/recipe/1/ingredient/2/update"))
            .andExpect(status().isOk())
            .andExpect(view().name("recipe/ingredient/ingredientform"))
            .andExpect(model().attributeExists("ingredient"))
            .andExpect(model().attributeExists("uomList"));
    }

    @Test
    public void testDeleteIngredient() throws Exception {
        mockMvc.perform(get("/recipe/1/ingredient/2/delete"))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/recipe/1/ingredients"));

        verify(ingredientService, times(1))
            .deleteByRecipeIdAndIngredientId(anyLong(), anyLong());
    }

    @Test
    public void testSaveOrUpdateIngredient() throws Exception {
        IngredientCommand savedCommand = new IngredientCommand();
        savedCommand.setId(3L);
        savedCommand.setRecipeId(2L);

        when(ingredientService.saveIngredientCommand(any())).thenReturn(savedCommand);

        mockMvc.perform(post("/recipe/2/ingredient")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("id", "")
            .param("description", "some string"))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/recipe/2/ingredient/3/show"));
    }
}