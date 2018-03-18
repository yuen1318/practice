package io.toro.pairprogramming.unit;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.repositories.UserRepository;
import io.toro.pairprogramming.services.AuthService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

@RunWith(SpringRunner.class)
public class AuthServiceTest {

    private AuthService authService;

    @MockBean
    private UserRepository userRepository;

    private Faker faker = new Faker();

    @Before
    public void setUp() throws Exception {
        this.authService = new AuthService(userRepository, faker.lorem().characters());
    }

    @Test
    public void shouldReturnTokenWhenValid() throws Exception {
        User user = createUser();

        String password = faker.lorem().word();

        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));

        when(userRepository.existsByEmail(user.getEmail()))
                .thenReturn(true);

        when(userRepository.findByEmailContainingIgnoreCase(user.getEmail()))
                .thenReturn(user);

        Optional<String> token = authService.login(user.getEmail(), password);

        assertTrue(token.isPresent());
    }

    @Test
    public void shouldNotReturnTokenWhenInvalid() throws Exception {
        User user = createUser();

        user.setPassword(BCrypt.hashpw(faker.lorem().word(), BCrypt.gensalt()));

        when(userRepository.existsByEmail(user.getEmail()))
                .thenReturn(true);

        when(userRepository.findByEmailContainingIgnoreCase(user.getEmail()))
                .thenReturn(user);

        user.setPassword(BCrypt.hashpw(faker.lorem().word(), BCrypt.gensalt()));

        Optional<String> token = authService.login(user.getEmail(), user.getPassword());

        assertFalse(token.isPresent());
    }

    @Test
    public void shouldRegisterIfEmailDoesNotExists() throws Exception {
        User user = createUser();

        when(userRepository.existsByEmail(user.getEmail()))
                .thenReturn(false);

        when(userRepository.save(user))
                .thenReturn(user);

        assertTrue(authService.register(user).isPresent());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void shouldNotRegisterIfEmailExists() throws Exception {
        User user = createUser();

        when(userRepository.existsByEmail(user.getEmail()))
                .thenReturn(true);

        assertFalse(authService.register(user).isPresent());

        verify(userRepository, never()).save(user);
    }

    @Test
    public void shouldEncryptPassword() throws Exception {
        User user = createUser();

        String password = user.getPassword();

        when(userRepository.existsByEmail(user.getEmail()))
                .thenReturn(false);

        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));

        when(userRepository.save(user))
                .thenReturn(user);

        user.setPassword(password);

        User registeredUser = authService.register(user).get();

        // Assert encrypted
        assertNotEquals(password, registeredUser.getPassword());

        // Assert can be decrypted
        assertTrue(BCrypt.checkpw(password, user.getPassword()));
    }

    @Test
    public void shouldReturnUserFromToken() throws Exception {
        User user = createUser();
        user.setId(1L);

        String secret = authService.getSecret();

        ObjectMapper objectMapper = new ObjectMapper();

        String token = Jwts.builder()
                            .signWith(SignatureAlgorithm.HS512, secret)
                            .setSubject(objectMapper.writeValueAsString(user))
                            .compact();

        assertThat(authService.getUserFromToken(token), instanceOf(User.class));
    }

    @Test
    public void shouldVerifyToken() throws Exception {
        String token = Jwts.builder()
                            .signWith(SignatureAlgorithm.HS512, authService.getSecret())
                            .setSubject("test")
                            .compact();

        assertTrue(authService.verifyToken(token));

        String invalid = token + "qweasdzxcasdqwe";

        assertFalse(authService.verifyToken(invalid));
    }

    @Test
    public void shouldExtractTokenFromHeader() throws Exception {
        String token = Jwts.builder()
                            .signWith(SignatureAlgorithm.HS512, authService.getSecret())
                            .setSubject("test")
                            .compact();

        String header = "Bearer " + token;

        assertTrue(authService.getTokenFromHeader(header).equals(token));
    }

    private User createUser() {
        User user = new User();

        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setEmail(faker.internet().emailAddress());
        user.setPassword(faker.lorem().word());

        return user;
    }
}
