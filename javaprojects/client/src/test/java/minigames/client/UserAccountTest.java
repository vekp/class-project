package minigames.client.useraccount;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserAccountTest {
    private static final Logger logger = LogManager.getLogger(UserAccountTest.class);

    @Test
    @DisplayName("UserAccountSchema.isValid() passes when given a valid username")
    public void validUsername() {
        logger.info("Begin testing for valid usernames ...");
        UserAccountSchema schema = new UserAccountSchema();
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

    @Test
    @DisplayName("UserAccountSchema.isValid() fails when given an invalid username")
    public void invalidUsername() {
        logger.info("Begin testing for invalid usernames ...");
        UserAccountSchema schema = new UserAccountSchema();
        logger.info("\t4 letter username, all lowercase, isValid() should return false");
        assertFalse(schema.isValid("username", "fail"));
        logger.info("\t33 letter username, all lowercase, isValid() should return false");
        assertFalse(schema.isValid("username", "thistestisanabsolutefailurenowman"));
        logger.info("\t6 character username, contains special characters, isValid() should return false");
        assertFalse(schema.isValid("username", "@b@cus"));
        logger.info("\t6 character username, contains spaces, isValid() should return false");
        assertFalse(schema.isValid("username", "a c u "));
        logger.info("\tTest Complete");
    }

    @Test
    @DisplayName("UserAccountSchema.isValid() passes when given a valid password")
    public void validPassword() {
        logger.info("Begin testing for valid passwords ...");
        UserAccountSchema schema = new UserAccountSchema();
        logger.info("\t15 character password, compliant, isValid() should return true");
        assertTrue(schema.isValid("password", "th1s1sAv@l1d0ne"));
        logger.info("\tTest Complete");
    }

    @Test
    @DisplayName("UserAccountSchema.isValid() fails when given an invalid password")
    public void invalidPassword() {
        logger.info("Begin testing for invalid passwords ...");
        UserAccountSchema schema = new UserAccountSchema();
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

    @Test
    @DisplayName("UserAccountSchema.isValid() passes when given a valid email")
    public void validEmail() {
        logger.info("Begin testing for valid emails ...");
        UserAccountSchema schema = new UserAccountSchema();
        logger.info("\tUNE student email, all lowercase letters, isValid() should return true");
        assertTrue(schema.isValid("email", "james.manning@myune.edu.au"));
        //TODO: Add more valid email test types
        logger.info("\tTest Complete");
    }

    @Test
    @DisplayName("UserAccountSchema.isValid() fails when given an invalid email")
    public void invalidEmail() {
        logger.info("Begin testing for invalid emails ...");
        UserAccountSchema schema = new UserAccountSchema();
        logger.info("\tUNE student email, missing .edu.au, isValid() should return false");
        assertFalse(schema.isValid("email", "james.manning@myune"));
        //TODO: Add more invalid email test types
        logger.info("\tTest Complete");
    }

    //TODO: Write tests for validating Map<String, String> and JsonObject with isValid() ...
}
