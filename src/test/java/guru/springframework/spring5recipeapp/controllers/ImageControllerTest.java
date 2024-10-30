package guru.springframework.spring5recipeapp.controllers;

import guru.springframework.spring5recipeapp.commands.RecipeCommand;
import guru.springframework.spring5recipeapp.services.ImageService;
import guru.springframework.spring5recipeapp.services.RecipeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.ControllerAdvice;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImageControllerTest {

    @Mock
    private ImageService imageService;

    @Mock
    private RecipeService recipeService;

    private MockMvc mockMvc;

    private ImageController controller;

    private AutoCloseable openMocks;

    @BeforeEach
    public void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        controller = new ImageController(imageService, recipeService);
        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(new ControllerExceptionHandler())
            .build();
    }

    @AfterEach
    public void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    public void showUploadForm() throws Exception {
        RecipeCommand command = new RecipeCommand();
        command.setId(1L);

        Mockito.when(recipeService.findCommandById(Mockito.anyLong()))
                .thenReturn(command);

        mockMvc.perform(MockMvcRequestBuilders.get("/recipe/1/image"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("recipe"));

        Mockito.verify(recipeService, Mockito.times(1))
                .findCommandById(Mockito.anyLong());
    }

    @Test
    public void handleImagePost() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("imagefile", "testing.txt", "text/plain", "Spring Framework Guru".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/recipe/1/image").file(multipartFile))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.header().string("Location", "/recipe/1/show"));

        Mockito.verify(imageService, Mockito.times(1))
                .saveImageFile(Mockito.anyLong(), Mockito.any());
    }

    @Test
    public void renderImageFromDB() throws Exception {

        String s = "fake image text";
        Byte[] bytesBoxed = new Byte[s.getBytes().length];

        int i = 0;
        for (byte primByte : s.getBytes()) {
            bytesBoxed[i++] = primByte;
        }

        RecipeCommand command = new RecipeCommand();
        command.setId(1L);
        command.setImage(bytesBoxed);

        Mockito.when(recipeService.findCommandById(Mockito.anyLong()))
            .thenReturn(command);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/recipe/1/recipeimage"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse();

        byte[] responseBytes = response.getContentAsByteArray();
        assertEquals(s.getBytes().length, responseBytes.length);

    }

    @Test
    public void getImageNumberFormatException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/recipe/asdf/recipeimage"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.view().name("400error"));
    }

}