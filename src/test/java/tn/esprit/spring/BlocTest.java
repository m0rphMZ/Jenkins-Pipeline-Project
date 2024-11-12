package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.Foyer;
import tn.esprit.spring.dao.Repositories.BlocRepository;
import tn.esprit.spring.dao.Repositories.ChambreRepository;
import tn.esprit.spring.dao.Repositories.FoyerRepository;
import tn.esprit.spring.services.bloc.BlocService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class BlocServiceTest {

    // Injects mock dependencies into the class being tested
    @InjectMocks
    private BlocService blocService;

    // Mocks the dependencies of BlocService
    @Mock
    private BlocRepository blocRepository;

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private FoyerRepository foyerRepository;

    // Sets up mocks before each test method
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize the mocks
    }

    // Test to add or update a Bloc entity
    @Test
    void testAddOrUpdate() {
        Bloc bloc = new Bloc(1L, "Bloc A", 100L, null, new ArrayList<>());
        when(blocRepository.save(any(Bloc.class))).thenReturn(bloc); // Mocking the save method

        Bloc result = blocService.addOrUpdate(bloc); // Calling the method under test

        assertNotNull(result); // Assert that the result is not null
        assertEquals("Bloc A", result.getNomBloc()); // Assert that the result has the correct name
        verify(blocRepository, times(1)).save(bloc); // Verify that the save method was called once
    }

    // Test for handling null input in add or update method
    @Test
    void testAddOrUpdate_WithNullBloc() {
        assertThrows(NullPointerException.class, () -> {
            blocService.addOrUpdate(null); // Should throw NullPointerException when null is passed
        });
    }

    // Test to find all blocs
    @Test
    void testFindAll() {
        List<Bloc> blocs = Arrays.asList(new Bloc(1L, "Bloc A", 100L, null, new ArrayList<>()));
        when(blocRepository.findAll()).thenReturn(blocs); // Mocking the findAll method

        List<Bloc> result = blocService.findAll(); // Calling the method under test

        assertEquals(1, result.size()); // Assert that there is one bloc in the result
        assertEquals("Bloc A", result.get(0).getNomBloc()); // Assert that the first bloc's name is "Bloc A"
        verify(blocRepository, times(1)).findAll(); // Verify that findAll was called once
    }

    // Regression Test for add or update method
    @Test
    void testRegression_AddOrUpdate() {
        Bloc bloc = new Bloc(1L, "Bloc B", 120L, null, new ArrayList<>());
        when(blocRepository.save(any(Bloc.class))).thenReturn(bloc);

        Bloc result = blocService.addOrUpdate(bloc);

        assertNotNull(result);
        assertEquals("Bloc B", result.getNomBloc());
        verify(blocRepository, times(1)).save(bloc);
    }

    // Test when the list of blocs is empty
    @Test
    void testFindAll_EmptyList() {
        when(blocRepository.findAll()).thenReturn(new ArrayList<>()); // Mocking an empty list

        List<Bloc> result = blocService.findAll();

        assertTrue(result.isEmpty()); // Assert that the result is empty
        verify(blocRepository, times(1)).findAll(); // Verify that findAll was called once
    }

    // Test to find a bloc by its ID
    @Test
    void testFindById() {
        Bloc bloc = new Bloc(1L, "Bloc A", 100L, null, new ArrayList<>());
        when(blocRepository.findById(anyLong())).thenReturn(Optional.of(bloc));

        Bloc result = blocService.findById(1L);

        assertNotNull(result); // Assert that the result is not null
        assertEquals("Bloc A", result.getNomBloc()); // Assert that the result has the correct name
        verify(blocRepository, times(1)).findById(1L); // Verify that findById was called once
    }

    // Test for when the bloc is not found by ID
    @Test
    void testFindById_NotFound() {
        when(blocRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            blocService.findById(999L); // Should throw NoSuchElementException if the bloc is not found
        });

        assertEquals("No value present", exception.getMessage()); // Assert the exception message
        verify(blocRepository, times(1)).findById(999L); // Verify that findById was called once
    }

    // Test to delete a bloc by its ID
    @Test
    void testDeleteById() {
        blocService.deleteById(1L); // Calling the delete method

        verify(blocRepository, times(1)).deleteById(1L); // Verify that deleteById was called once
    }

    // Test for deleting a bloc and its associated chambres
    @Test
    void testDelete() {
        Chambre chambre1 = new Chambre();
        Chambre chambre2 = new Chambre();
        List<Chambre> chambres = Arrays.asList(chambre1, chambre2);
        Bloc bloc = new Bloc(1L, "Bloc A", 100L, null, chambres);

        blocService.delete(bloc); // Calling the delete method

        verify(chambreRepository, times(2)).delete(any(Chambre.class)); // Verifying each chambre is deleted
        verify(blocRepository, times(1)).delete(bloc); // Verifying the bloc is deleted
    }

    // Test for assigning rooms to a bloc
    @Test
    void testAffecterChambresABloc() {
        Bloc bloc = new Bloc(1L, "Bloc A", 100L, null, new ArrayList<>());
        when(blocRepository.findByNomBloc(anyString())).thenReturn(bloc);
        when(chambreRepository.findByNumeroChambre(anyLong())).thenReturn(new Chambre());

        Bloc result = blocService.affecterChambresABloc(Arrays.asList(1L, 2L), "Bloc A");

        assertNotNull(result); // Assert that the result is not null
        verify(chambreRepository, times(2)).findByNumeroChambre(anyLong()); // Verify that the chambre repository was called for each room
        verify(chambreRepository, times(2)).save(any(Chambre.class)); // Verify that the save method was called
    }

    // Test for assigning rooms to a bloc when the bloc is not found
    @Test
    void testAffecterChambresABloc_BlocNotFound() {
        String nomBloc = "NonExistentBloc";
        List<Long> numChambres = List.of(1L, 2L);

        Mockito.when(blocRepository.findByNomBloc(nomBloc)).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            blocService.affecterChambresABloc(numChambres, nomBloc); // Should throw IllegalArgumentException if bloc is not found
        });

        assertTrue(exception.getMessage().contains("Bloc not found"));
    }

    // Test for assigning a bloc to a foyer
    @Test
    void testAffecterBlocAFoyer() {
        Bloc bloc = new Bloc(1L, "Bloc A", 100L, null, new ArrayList<>());
        Foyer foyer = new Foyer();
        when(blocRepository.findByNomBloc(anyString())).thenReturn(bloc);
        when(foyerRepository.findByNomFoyer(anyString())).thenReturn(foyer);
        when(blocRepository.save(any(Bloc.class))).thenReturn(bloc);

        Bloc result = blocService.affecterBlocAFoyer("Bloc A", "Foyer A");

        assertNotNull(result); // Assert that the result is not null
        verify(blocRepository, times(1)).save(bloc); // Verify that the save method was called
    }

    // Load/Stress test to check performance of findAll method
    @Test
    void testLoad_FindAllPerformance() {
        List<Bloc> blocs = new ArrayList<>();
        for (long i = 0; i < 10000; i++) {
            blocs.add(new Bloc(i, "Bloc " + i, 100L, null, new ArrayList<>())); // Create a large number of blocs
        }
        when(blocRepository.findAll()).thenReturn(blocs);

        long startTime = System.currentTimeMillis();
        blocService.findAll(); // Calling the method under test
        long endTime = System.currentTimeMillis();

        assertTrue(endTime - startTime < 500); // Assert that the method completes in less than 500ms
    }
}
