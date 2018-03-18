package io.toro.pairprogramming.integration;

import io.toro.pairprogramming.models.User;
import org.junit.Before;

import static io.toro.pairprogramming.integration.utils.AuthUtils.createToken;
import static io.toro.pairprogramming.integration.utils.AuthUtils.createUser;

public abstract class WithAuthenticationIntegrationTest extends BaseIntegrationTest {

    protected User user;

    protected String token;

    @Before
    public void setUp() throws Exception {
        user = entityManager.persistAndFlush(createUser());

        String subject = objectMapper.writeValueAsString(user);

        token = createToken(subject, secret);
    }
}
