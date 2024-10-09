package guru.springframework.spring5recipeapp.services;

import guru.springframework.spring5recipeapp.commands.IngredientCommand;
import guru.springframework.spring5recipeapp.converters.IngredientCommandToIngredient;
import guru.springframework.spring5recipeapp.converters.IngredientToIngredientCommand;
import guru.springframework.spring5recipeapp.domain.Ingredient;
import guru.springframework.spring5recipeapp.domain.Recipe;
import guru.springframework.spring5recipeapp.repositories.RecipeRepository;
import guru.springframework.spring5recipeapp.repositories.UnitOfMeasureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final RecipeRepository recipeRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;

    public IngredientServiceImpl(IngredientToIngredientCommand ingredientToIngredientCommand,
                                 IngredientCommandToIngredient ingredientCommandToIngredient,
                                 RecipeRepository recipeRepository,
                                 UnitOfMeasureRepository unitOfMeasureRepository) {
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
        this.ingredientCommandToIngredient = ingredientCommandToIngredient;
        this.recipeRepository = recipeRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    @Override
    public IngredientCommand findByRecipeIdAndIngredientId(Long recipeId, Long ingredientId) {
        Optional<Recipe> optionalRecipe = recipeRepository.findById(recipeId);
        if (optionalRecipe.isEmpty()) {
            log.error("Recipe not found, id: {}", recipeId);
            return new IngredientCommand();
        }

        Recipe recipe = optionalRecipe.get();

        Optional<IngredientCommand> ingredientCommandOptional = recipe.getIngredients().stream()
            .filter(ingredient -> ingredient.getId().equals(ingredientId))
            .map(ingredientToIngredientCommand::convert)
            .filter(Objects::nonNull)
            .findFirst();

        if (ingredientCommandOptional.isEmpty()) {
            log.error("Ingredient not found, id: {}", ingredientId);
            return new IngredientCommand();
        }

        return ingredientCommandOptional.get();
    }

    @Override
    public void deleteByRecipeIdAndIngredientId(Long recipeId, Long ingredientId) {
        Optional<Recipe> optionalRecipe = recipeRepository.findById(recipeId);
        if (optionalRecipe.isEmpty()) {
            log.error("Recipe not found, id: {}", recipeId);
            return;
        }

        Recipe recipe = optionalRecipe.get();
        Optional<Ingredient> optionalIngredient = recipe.getIngredients()
            .stream()
            .filter(ingredient -> ingredient.getId().equals(ingredientId))
            .findFirst();

        if (optionalIngredient.isEmpty()) {
            log.error("Ingredient with id {} not found in recipe {}", ingredientId, recipeId);
            return;
        }

        optionalIngredient.get().setRecipe(null);
        recipe.getIngredients().remove(optionalIngredient.get());
        recipeRepository.save(recipe);
    }

    @Override
    public IngredientCommand saveIngredientCommand(IngredientCommand command) {
        Optional<Recipe> optionalRecipe = recipeRepository.findById(command.getRecipeId());
        if (optionalRecipe.isEmpty()) {
            log.error("Recipe not found, id: {}", command.getRecipeId());
            return new IngredientCommand();
        }

        Recipe recipe = optionalRecipe.get();
        Optional<Ingredient> optionalIngredient = recipe
            .getIngredients()
            .stream()
            .filter(ingredient -> ingredient.getId().equals(command.getId()))
            .findFirst();

        if (optionalIngredient.isPresent()) {
            Ingredient ingredient = optionalIngredient.get();
            ingredient.setDescription(command.getDescription());
            ingredient.setAmount(command.getAmount());
            ingredient.setUom(unitOfMeasureRepository.findById(command.getUom().getId())
                .orElseThrow(() -> new RuntimeException("Uom not found")));
        } else {
            Ingredient ingredient = ingredientCommandToIngredient.convert(command);
            if (ingredient != null) {
                recipe.addIngredient(ingredient);
            }
        }

        Recipe savedRecipe = recipeRepository.save(recipe);
        Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients().stream()
            .filter(ingredient -> ingredient.getId().equals(command.getId()))
            .findFirst();

        if (savedIngredientOptional.isEmpty()) {
            savedIngredientOptional = savedRecipe.getIngredients().stream()
                .filter(ingredient -> ingredient.getDescription().equals(command.getDescription()))
                .filter(ingredient -> ingredient.getAmount().equals(command.getAmount()))
                .filter(ingredient -> ingredient.getUom().getId().equals(command.getUom().getId()))
                .findFirst();
        }

        if (savedIngredientOptional.isEmpty()) {
            log.error("Ingredient not found, description: {}", command.getDescription());
            return new IngredientCommand();
        }

        return ingredientToIngredientCommand.convert(savedIngredientOptional.get());
    }

}
