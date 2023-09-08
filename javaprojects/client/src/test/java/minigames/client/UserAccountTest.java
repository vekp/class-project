package minigames.client.useraccount;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Map;
import java.util.HashMap;
import io.vertx.core.json.JsonObject;



/**
 * This class contains unit tests for the UserAccountSchema class.
 * It tests the isValid method with various inputs, including different forms of valid and invalid usernames, passwords, and emails.
 */

public class UserAccountTest {
    private static final Logger logger = LogManager.getLogger(UserAccountTest.class);



    /**
     * Tests the isValid method with valid usernames.
     */

    @Test
    @DisplayName("UserAccountSchema.isValid() passes when given a valid username")
    public void validUsername() {
        logger.info("Begin testing for valid usernames ...");
        UserAccountSchema schema = new UserAccountSchema();

        // Various valid usernames to test and asserting that all valid usernames pass
        logger.info("\t6 letter username, all lowercase, isValid() should return true");
        assertTrue(schema.isValid("username", "abacus"));
        logger.info("\t6 letter username, all uppercase, isValid() should return true");
        assertTrue(schema.isValid("username", "ABACUS"));
        logger.info("\t6 letter username, all mixed case, isValid() should return true");
        assertTrue(schema.isValid("username", "aBaCuS"));
        logger.info("\t26 letter username, mixed case alphabet, isValid() should return true");
        assertTrue(schema.isValid("username", "aBcDeFgHiJkLmNoPqRsTuVwXyZ"));
        logger.info("\t6 character username, contains numbers, isValid() should return true");
        assertTrue(schema.isValid("username", "abacu5"));
        logger.info("\t6 character username, contains underscores, isValid() should return true");
        assertTrue(schema.isValid("username", "a_a_u_"));
        logger.info("\tTest Complete");
    }


    /**
     * Tests the isValid method with invalid usernames.
     */

    @Test
    @DisplayName("UserAccountSchema.isValid() fails when given an invalid username")
    public void invalidUsername() {
        logger.info("Begin testing for invalid usernames ...");
        UserAccountSchema schema = new UserAccountSchema();

        // Various invalid usernames to test and asserting that all invalid usernames fail
        logger.info("\t4 letter username, all lowercase, isValid() should return false");
        assertFalse(schema.isValid("username", "fail"));
        logger.info("\t33 letter username, all lowercase, isValid() should return false");
        assertFalse(schema.isValid("username", "thistestisanabsolutefailurenowman"));
        logger.info("\t6 character username, contains special characters, isValid() should return false");
        assertFalse(schema.isValid("username", "@b@cus"));
        logger.info("\t6 character username, contains spaces, isValid() should return false");
        assertFalse(schema.isValid("username", "a c u "));
        
        // Testing with null and empty string
        logger.info("\tTesting with null username");
        assertFalse(schema.isValid("username", null));
        logger.info("\tTesting with empty username");
        assertFalse(schema.isValid("username", ""));

        logger.info("\tTest Complete");

    }

    /**
     * Tests the isValid method with valid passwords.
     */
    @Test
    @DisplayName("UserAccountSchema.isValid() passes when given a valid password")
    public void validPassword() {
        logger.info("Begin testing for valid passwords ...");
        UserAccountSchema schema = new UserAccountSchema();

        // Valid password to test and asserting that the valid password passes
        logger.info("\t15 character password, compliant, isValid() should return true");
        assertTrue(schema.isValid("password", "th1s1sAv@l1d0ne"));
        logger.info("\tTest Complete");
    }

    /**
     * Tests the isValid method with invalid passwords including null and empty strings.
     */
    @Test
    @DisplayName("UserAccountSchema.isValid() fails when given an invalid password")
    public void invalidPassword() {
        logger.info("Begin testing for invalid passwords ...");
        UserAccountSchema schema = new UserAccountSchema();

        // Various invalid passwords to test and asserting that all invalid passwords fail
        logger.info("\t6 character password, under length, isValid() should return false");
        assertFalse(schema.isValid("password", "oN3T!wo"));
        logger.info("\t26 character password, over length, isValid() should return false");
        assertFalse(schema.isValid("password", "tH1s1s@n1nv@l1dP@ssworDm@n"));
        logger.info("\t16 character password, missing lowercase, isValid() should return false");
        assertFalse(schema.isValid("password", "TH1S1SINV@L1D0NE"));
        logger.info("\t16 character password, missing uppercase, isValid() should return false");
        assertFalse(schema.isValid("password", "th1s1sinv@l1d0ne"));
        logger.info("\t16 character password, missing digits, isValid() should return false");
        assertFalse(schema.isValid("password", "thisisINv@lidone"));
        logger.info("\t16 character password, missing special characters, isValid() should return false");
        assertFalse(schema.isValid("password", "th1s1sINval1d0ne"));
        logger.info("\t16 character password, contains spaces, isValid() should return false");
        assertFalse(schema.isValid("password", "t 1s1sIN @l1 0ne"));
        logger.info("\tTest Complete");
    }

     /**
     * Tests the isValid method with valid email addresses.
     */
    @Test
    @DisplayName("UserAccountSchema.isValid() passes when given a valid email")
    public void validEmail() {
        logger.info("Begin testing for valid emails ...");
        UserAccountSchema schema = new UserAccountSchema();

        // Various valid email addresses to test and  asserting that all valid email addresses pass
        logger.info("\tUNE student email, all lowercase letters, isValid() should return true");
        assertTrue(schema.isValid("email", "james.manning@myune.edu.au"));

        logger.info("\tCommon email format, isValid() should return true");
        assertTrue(schema.isValid("email", "test.user@gmail.com"));
    
        logger.info("\tEmail with subdomain, isValid() should return true");
        assertTrue(schema.isValid("email", "user@sub.example.com"));
    
        logger.info("\tEmail with digits, isValid() should return true");
        assertTrue(schema.isValid("email", "user123@example.co.in"));
    
        logger.info("\tEmail with plus sign, isValid() should return true");
        assertTrue(schema.isValid("email", "user+alias@example.com"));

        logger.info("\tTest Complete");
    }

    /**
     * Tests the isValid method with invalid email addresses including edge cases like null and empty strings.
     */

    @Test
    @DisplayName("UserAccountSchema.isValid() fails when given an invalid email")
    public void invalidEmail() {
        logger.info("Begin testing for invalid emails ...");
        UserAccountSchema schema = new UserAccountSchema();

        // Various invalid email addresses to test and asserting that all invalid email addresses fail
        logger.info("\tUNE student email, missing .edu.au, isValid() should return false");
        assertFalse(schema.isValid("email", "james.manning@myune"));
        logger.info("\tEmail missing '@', isValid() should return false");
        assertFalse(schema.isValid("email", "test.user.com"));
    
        logger.info("\tEmail with space, isValid() should return false");
        assertFalse(schema.isValid("email", "test user@example.com"));
    
        logger.info("\tEmail with multiple '@', isValid() should return false");
        assertFalse(schema.isValid("email", "test@@example.com"));
    
        logger.info("\tEmail with invalid domain, isValid() should return false");
        assertFalse(schema.isValid("email", "test@.com"));
    
        logger.info("\tTest Complete");
    }


    /**
     * Tests the isValid method with valid account details represented as a JsonObject.
     */
    @Test
    @DisplayName("UserAccountSchema.isValid() passes when given a valid account as JsonObject")
    public void validAccountJsonObject() {


        logger.info("Begin testing for valid account as JsonObject ...");
        UserAccountSchema schema = new UserAccountSchema();
        
        JsonObject validAccount = new JsonObject();
        validAccount.put("username", "ValidUser1");
        validAccount.put("password", "V@lidPass1");
        validAccount.put("email", "valid.user@myune.edu.au");
        
        assertTrue(schema.isValid(validAccount));

        logger.info("\tTest Complete");
    }


     /**
     * Tests the isValid method with invalid account details represented as a JsonObject.
     */

    @Test
    @DisplayName("UserAccountSchema.isValid() fails when given an invalid account as JsonObject")
    public void invalidAccountJsonObject() {
        logger.info("Begin testing for invalid account as JsonObject ...");
        UserAccountSchema schema = new UserAccountSchema();
        
        JsonObject invalidAccount = new JsonObject();
        invalidAccount.put("username", "inv");
        invalidAccount.put("password", "inv");
        invalidAccount.put("email", "invalid@myune");
        
        assertFalse(schema.isValid(invalidAccount));

        logger.info("\tTest Complete");
    }

    /**
     * Tests the isValid method with valid account details represented as a Map.
     */

    @Test
    @DisplayName("UserAccountSchema.isValid() passes when given a valid account as Map")
    public void validAccountMap() {
        logger.info("Begin testing for valid account as Map ...");
        UserAccountSchema schema = new UserAccountSchema();
        
        Map<String, String> validAccount = new HashMap<>();
        validAccount.put("username", "ValidUser1");
        validAccount.put("password", "V@lidPass1");
        validAccount.put("email", "valid.user@myune.edu.au");
        
        assertTrue(schema.isValid(validAccount));

        logger.info("\tTest Complete");
    }


    /**
     * Tests the isValid method with invalid account details represented as a Map.
     */
    @Test
    @DisplayName("UserAccountSchema.isValid() fails when given an invalid account as Map")
    public void invalidAccountMap() {
        logger.info("Begin testing for invalid account as Map ...");
        UserAccountSchema schema = new UserAccountSchema();
        
        Map<String, String> invalidAccount = new HashMap<>();
        invalidAccount.put("username", "inv");
        invalidAccount.put("password", "inv");
        invalidAccount.put("email", "invalid@myune");
        
        assertFalse(schema.isValid(invalidAccount));

        logger.info("\tTest Complete");
    }

}
