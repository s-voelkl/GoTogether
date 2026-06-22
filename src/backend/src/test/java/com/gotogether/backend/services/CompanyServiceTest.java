package com.gotogether.backend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gotogether.backend.dto.CompanyDTO;
import com.gotogether.backend.dto.CompanyCreateDTO;
import com.gotogether.backend.dto.CompanyLoginDTO;
import com.gotogether.backend.mapper.CompanyMapper;
import com.gotogether.backend.model.Address;
import com.gotogether.backend.model.Company;
import com.gotogether.backend.model.Location;
import com.gotogether.backend.repository.CompanyRepository;
import com.gotogether.backend.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private CompanyMapper companyMapper;

    @InjectMocks
    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        companyMapper = new CompanyMapper();
    }

    private Company buildCompany() {
        Company company = new Company();
        company.setId(UUID.randomUUID());
        company.setName("Test Company");
        company.setPassword("secret");
        company.setEmail("company@example.com");
        company.setCurrency(100);
        company.setAddress(new Address("Main Street", "1", "12345", "Berlin"));
        company.setLocation(new Location(52.52, 13.405));
        return company;
    }

    private CompanyCreateDTO buildCreateDTO() {
        return new CompanyCreateDTO(
                "Test Company",
                "secret",
                "company@example.com",
                "Main Street",
                "1",
                "12345",
                "Berlin",
                52.52,
                13.405);
    }

    @Test
    void getCompanyById_CompanyExists_ReturnsCompany() {
        // Arrange
        Company mockCompany = buildCompany();
        when(companyRepository.findById(mockCompany.getId())).thenReturn(Optional.of(mockCompany));

        // Act
        CompanyDTO result = companyService.getCompanyById(mockCompany.getId());

        // Assert
        assertNotNull(result);
        assertEquals(mockCompany.getName(), result.getName());
        assertEquals(mockCompany.getEmail(), result.getEmail());
        verify(companyRepository, times(1)).findById(mockCompany.getId());
    }

    @Test
    void getCompanyById_CompanyDoesNotExist_ThrowsRuntimeException() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> companyService.getCompanyById(id));
        verify(companyRepository, times(1)).findById(id);
    }

    @Test
    void getAllCompanies_CompaniesExist_ReturnsList() {
        // Arrange
        Company mockCompany1 = buildCompany();
        Company mockCompany2 = buildCompany();
        mockCompany2.setEmail("two@example.com");
        when(companyRepository.findAll()).thenReturn(List.of(mockCompany1, mockCompany2));

        // Act
        List<CompanyDTO> result = companyService.getAllCompanies();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(companyRepository, times(1)).findAll();
    }

    @Test
    void createCompany_ValidInput_ReturnsId() {
        // Arrange
        CompanyCreateDTO dto = buildCreateDTO();
        UUID generatedId = UUID.randomUUID();
        when(companyRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> {
            Company c = invocation.getArgument(0);
            c.setId(generatedId);
            return c;
        });

        // Act
        UUID resultId = companyService.createCompany(dto.getName(), dto.getPassword(), dto.getEmail(),
                dto.getStreet(), dto.getHouseNumber(), dto.getZipCode(), dto.getCity(),
                dto.getLatitude(), dto.getLongitude());

        // Assert
        assertEquals(generatedId, resultId);
        verify(companyRepository, times(1)).save(any(Company.class));
    }

    @Test
    void createCompany_EmailAlreadyExistsInCompanies_ThrowsRuntimeException() {
        // Arrange
        CompanyCreateDTO dto = buildCreateDTO();
        when(companyRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.createCompany(dto.getName(), dto.getPassword(), dto.getEmail(),
                        dto.getStreet(), dto.getHouseNumber(), dto.getZipCode(), dto.getCity(),
                        dto.getLatitude(), dto.getLongitude()));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void createCompany_EmailAlreadyExistsInUsers_ThrowsRuntimeException() {
        // Arrange
        CompanyCreateDTO dto = buildCreateDTO();
        when(companyRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.createCompany(dto.getName(), dto.getPassword(), dto.getEmail(),
                        dto.getStreet(), dto.getHouseNumber(), dto.getZipCode(), dto.getCity(),
                        dto.getLatitude(), dto.getLongitude()));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void createCompany_EmptyEmail_ThrowsRuntimeException() {
        // Arrange
        CompanyCreateDTO dto = buildCreateDTO();
        dto.setEmail("");

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.createCompany(dto.getName(), dto.getPassword(), dto.getEmail(),
                        dto.getStreet(), dto.getHouseNumber(), dto.getZipCode(), dto.getCity(),
                        dto.getLatitude(), dto.getLongitude()));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void createCompany_InvalidEmailFormat_ThrowsRuntimeException() {
        // Arrange
        CompanyCreateDTO dto = buildCreateDTO();
        dto.setEmail("not_an_email");

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.createCompany(dto.getName(), dto.getPassword(), dto.getEmail(),
                        dto.getStreet(), dto.getHouseNumber(), dto.getZipCode(), dto.getCity(),
                        dto.getLatitude(), dto.getLongitude()));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void createCompany_StrictInvalidEmailFormat_ThrowsRuntimeException() {
        // Arrange: valid for loose regex, invalid for EmailValidator
        CompanyCreateDTO dto = buildCreateDTO();
        dto.setEmail("a@b");

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.createCompany(dto.getName(), dto.getPassword(), dto.getEmail(),
                        dto.getStreet(), dto.getHouseNumber(), dto.getZipCode(), dto.getCity(),
                        dto.getLatitude(), dto.getLongitude()));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void createCompany_EmptyName_ThrowsRuntimeException() {
        // Arrange
        CompanyCreateDTO dto = buildCreateDTO();
        dto.setName("");

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.createCompany(dto.getName(), dto.getPassword(), dto.getEmail(),
                        dto.getStreet(), dto.getHouseNumber(), dto.getZipCode(), dto.getCity(),
                        dto.getLatitude(), dto.getLongitude()));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void createCompany_EmptyPassword_ThrowsRuntimeException() {
        // Arrange
        CompanyCreateDTO dto = buildCreateDTO();
        dto.setPassword("");

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.createCompany(dto.getName(), dto.getPassword(), dto.getEmail(),
                        dto.getStreet(), dto.getHouseNumber(), dto.getZipCode(), dto.getCity(),
                        dto.getLatitude(), dto.getLongitude()));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void createCompany_EmptyStreet_ThrowsRuntimeException() {
        // Arrange
        CompanyCreateDTO dto = buildCreateDTO();
        dto.setStreet("");

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.createCompany(dto.getName(), dto.getPassword(), dto.getEmail(),
                        dto.getStreet(), dto.getHouseNumber(), dto.getZipCode(), dto.getCity(),
                        dto.getLatitude(), dto.getLongitude()));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void createCompany_EmptyHouseNumber_ThrowsRuntimeException() {
        // Arrange
        CompanyCreateDTO dto = buildCreateDTO();
        dto.setHouseNumber("");

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.createCompany(dto.getName(), dto.getPassword(), dto.getEmail(),
                        dto.getStreet(), dto.getHouseNumber(), dto.getZipCode(), dto.getCity(),
                        dto.getLatitude(), dto.getLongitude()));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void createCompany_EmptyZipCode_ThrowsRuntimeException() {
        // Arrange
        CompanyCreateDTO dto = buildCreateDTO();
        dto.setZipCode("");

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.createCompany(dto.getName(), dto.getPassword(), dto.getEmail(),
                        dto.getStreet(), dto.getHouseNumber(), dto.getZipCode(), dto.getCity(),
                        dto.getLatitude(), dto.getLongitude()));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void createCompany_EmptyCity_ThrowsRuntimeException() {
        // Arrange
        CompanyCreateDTO dto = buildCreateDTO();
        dto.setCity("");

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.createCompany(dto.getName(), dto.getPassword(), dto.getEmail(),
                        dto.getStreet(), dto.getHouseNumber(), dto.getZipCode(), dto.getCity(),
                        dto.getLatitude(), dto.getLongitude()));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void createCompany_LatitudeTooLow_ThrowsRuntimeException() {
        // Arrange
        CompanyCreateDTO dto = buildCreateDTO();
        dto.setLatitude(-90.1);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.createCompany(dto.getName(), dto.getPassword(), dto.getEmail(),
                        dto.getStreet(), dto.getHouseNumber(), dto.getZipCode(), dto.getCity(),
                        dto.getLatitude(), dto.getLongitude()));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void createCompany_LatitudeTooHigh_ThrowsRuntimeException() {
        // Arrange
        CompanyCreateDTO dto = buildCreateDTO();
        dto.setLatitude(90.1);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.createCompany(dto.getName(), dto.getPassword(), dto.getEmail(),
                        dto.getStreet(), dto.getHouseNumber(), dto.getZipCode(), dto.getCity(),
                        dto.getLatitude(), dto.getLongitude()));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void createCompany_LongitudeTooLow_ThrowsRuntimeException() {
        // Arrange
        CompanyCreateDTO dto = buildCreateDTO();
        dto.setLongitude(-180.1);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.createCompany(dto.getName(), dto.getPassword(), dto.getEmail(),
                        dto.getStreet(), dto.getHouseNumber(), dto.getZipCode(), dto.getCity(),
                        dto.getLatitude(), dto.getLongitude()));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void createCompany_LongitudeTooHigh_ThrowsRuntimeException() {
        // Arrange
        CompanyCreateDTO dto = buildCreateDTO();
        dto.setLongitude(180.1);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.createCompany(dto.getName(), dto.getPassword(), dto.getEmail(),
                        dto.getStreet(), dto.getHouseNumber(), dto.getZipCode(), dto.getCity(),
                        dto.getLatitude(), dto.getLongitude()));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void loginCompany_ValidCredentials_ReturnsCompanyId() {
        // Arrange
        Company mockCompany = buildCompany();
        CompanyLoginDTO dto = new CompanyLoginDTO("secret", "company@example.com");
        when(companyRepository.findByEmail(dto.getEmail())).thenReturn(mockCompany);

        // Act
        UUID resultId = companyService.loginCompany(dto.getEmail(), dto.getPassword());

        // Assert
        assertEquals(mockCompany.getId(), resultId);
        verify(companyRepository, times(1)).findByEmail(dto.getEmail());
    }

    @Test
    void loginCompany_InvalidEmail_ThrowsRuntimeException() {
        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.loginCompany("not_an_email", "secret"));
        verify(companyRepository, never()).findByEmail(anyString());
    }

    @Test
    void loginCompany_StrictInvalidEmail_ThrowsRuntimeException() {
        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.loginCompany("a@b", "secret"));
        verify(companyRepository, never()).findByEmail(anyString());
    }

    @Test
    void loginCompany_EmptyEmail_ThrowsRuntimeException() {
        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.loginCompany("", "secret"));
        verify(companyRepository, never()).findByEmail(anyString());
    }

    @Test
    void loginCompany_EmptyPassword_ThrowsRuntimeException() {
        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.loginCompany("company@example.com", ""));
        verify(companyRepository, never()).findByEmail(anyString());
    }

    @Test
    void loginCompany_CompanyNotFound_ThrowsRuntimeException() {
        // Arrange
        when(companyRepository.findByEmail("company@example.com")).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.loginCompany("company@example.com", "secret"));
        verify(companyRepository, times(1)).findByEmail("company@example.com");
    }

    @Test
    void loginCompany_InvalidPassword_ThrowsRuntimeException() {
        // Arrange
        Company mockCompany = buildCompany();
        when(companyRepository.findByEmail("company@example.com")).thenReturn(mockCompany);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.loginCompany("company@example.com", "wrong"));
        verify(companyRepository, times(1)).findByEmail("company@example.com");
    }

    @Test
    void addCompanyCurrency_ValidInput_UpdatesAndReturnsNewBalance() {
        // Arrange
        Company mockCompany = buildCompany();
        mockCompany.setCurrency(100);
        when(companyRepository.findById(mockCompany.getId())).thenReturn(Optional.of(mockCompany));

        // Act
        int result = companyService.addCompanyCurrency(mockCompany.getId(), 50);

        // Assert
        assertEquals(150, result);
        assertEquals(150, mockCompany.getCurrency());
        verify(companyRepository, times(1)).save(mockCompany);
    }

    @Test
    void addCompanyCurrency_NegativeAmount_ThrowsRuntimeException() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.addCompanyCurrency(id, -1));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void addCompanyCurrency_CompanyNotFound_ThrowsRuntimeException() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> companyService.addCompanyCurrency(id, 50));
        verify(companyRepository, never()).save(any(Company.class));
    }

}
