package minigames.server;

import minigames.rendering.NameGenerator;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.HashSet;
import java.util.Set;

public class NameGeneratorTests{

    
    @Test
    @DisplayName("NameGenerator class is generating a unique name")
    public void testGenerateName(){
        Set<String> existingNames = new HashSet<>();
        existingNames.add("adorable_einstein");
        existingNames.add("clever_newton");

        for(int i = 0; i < 10; i++){
            String newName = NameGenerator.generateName(existingNames);
            assertFalse(existingNames.contains(newName));
            existingNames.add(newName);
        }
        assertEquals(12, existingNames.size());
    }

    @Test
    @DisplayName("NameGenerator class works with empty set")
    public void testNoExistingName(){
        Set<String> existingNames = new HashSet<>();

        String newName = NameGenerator.generateName(existingNames);

        assertNotNull(newName);
    }
}