package model;

import com.example.kirana.Model.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ModelTest {
    private Validator validator;
    @BeforeEach
    public void setUp() {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.afterPropertiesSet();
        validator = factory.getValidator();
    }
    @Test
    public void testValidUser() {
        UserModel user = new UserModel();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");

        Set<ConstraintViolation<UserModel>> violations = validator.validate(user);
        assertEquals(0, violations.size());
    }

    @Test
    public void testBlankName() {
        UserModel user = new UserModel();
        user.setName("");  // Blank name
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");

        Set<ConstraintViolation<UserModel>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("Name is required", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidEmail() {
        UserModel user = new UserModel();
        user.setName("John Doe");
        user.setEmail("invalid-email");  // Invalid email
        user.setPassword("password123");

        Set<ConstraintViolation<UserModel>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("Email should be valid", violations.iterator().next().getMessage());
    }
    @Test
    public void testBlankPassword() {
        UserModel user = new UserModel();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("");  // Blank password

        Set<ConstraintViolation<UserModel>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("Password is required", violations.iterator().next().getMessage());
    }
}
