package minigames.client.useraccount;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.*;
import io.vertx.core.json.JsonObject;

//WIP; USAGE:
//  1) Initialise new schema (UserAccountSchema schema = new UserAccountSchema();)
//  2) Validate own user account against schema (e.g. if (schema.isValid(useraccount) {...}))
//      - Can supply either JsonObject, Map<String, String>, or String String to isValid()
public class UserAccountSchema {
    private Map<String, Set<String>> accountSchema = new HashMap<>();

    //Constructor
    public UserAccountSchema() {
        //Username - Regex Source: GeeksforGeeks
        //Web: https://www.geeksforgeeks.org/how-to-validate-a-username-using-regular-expressions-in-java/
        setSchema("username", "^[A-Za-z]\\w{4,29}$");
        //Password - Regex Source: GeeksforGeeks
        //Web: https://www.geeksforgeeks.org/how-to-validate-a-password-using-regular-expressions-in-java/
        setSchema("password", "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\S+$).{8,20}$");
        //Email - Regex Source: Jason Buberel @ StackOverflow
        //Web: https://stackoverflow.com/a/8204716
        setSchema("email", "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,12}$");
        //Alternate Password for where user wishes to use a PIN
        setSchema("password", "^[0-9].{1,6}$");
    }

    //Setters
    //Allows for key-value pairing as (String, Set<String>)
    private void setSchema(String key, Set<String> value) {
        String keyLower = key.toLowerCase();
        if (!getSchema().containsKey(keyLower)) {
            this.accountSchema.put(keyLower, value);
        } else {
            Set<String> newValue = getSchema(keyLower);
            newValue.addAll(value);
            this.accountSchema.put(keyLower, newValue);
        }
    }
    //Allows for key-value pairing as (String, String)
    private void setSchema(String key, String value) {
        Set<String> tmpValue = new HashSet<>();
        tmpValue.add(value);
        setSchema(key, tmpValue);
    }

    //Getters
    //Returns a Set of all keys (fields) contained in the schema Map
    public Set<String> getFields() { return this.accountSchema.keySet(); }
    //Returns entire schema Map
    public Map<String, Set<String>> getSchema() {
        return new HashMap<>(this.accountSchema);
    }
    //Returns specific schema Set
    public Set<String> getSchema(String field) {
        //Initialise a temporary HashMap containing a copy of the schema Map
        Map<String, Set<String>> tmpSchema = getSchema();
        //Convert field to lower case
        String fieldLower = field.toLowerCase();
        //Return an empty HashSet where schema does not contain field as a key
        if (!tmpSchema.containsKey(fieldLower)) { return new HashSet<>(); }
        //Return a copy of the HashSet containing the schema specified by field
        return new HashSet<>(tmpSchema.get(fieldLower));
    }


    //Public Functions
    //Allows for validation against schema of account as JsonObject
    public boolean isValid(JsonObject account) {
        //Iterate over entries in account and validate each in turn against their
        //respective schema
        for (String field : account.fieldNames()) {
            if (!isValid(field, account.getString(field))) { return false; }
        }
        //Return true where all entries in account pass validation
        return true;
    }
    //Allows for validation against schema of account as Map<String, String>
    public boolean isValid(Map<String, String> account) {
        //Iterate over entries in account and validate each in turn against their
        //respective schema
        for (String field : account.keySet()) {
            if (!isValid(field, account.get(field))) { return false; }
        }
        //Return true where all entries in account pass validation
        return true;
    }
    //Allows for validation of specific field given a key-value pairing
    public boolean isValid(String field, String value) {
        //Convert field to lower case
        String fieldLower = field.toLowerCase();
        //Print an error message to the console where field is not an existing schema
        if (!getSchema().containsKey(fieldLower)) {
            System.out.println("Error: field not a valid UserAccountSchema key");
            System.out.println(
                    "\tCallback: UserAccountSchema.isValid(field = "
                    +field
                    +", value = "
                    +value
                    +")"
            );
            return false;
        }

        //Return false where value is null
        if (value == null) { return false; }
        //Iterate over schema, testing for whether value matches the schema pattern
        for (String expression : getSchema(fieldLower)) {
            Pattern pattern = Pattern.compile(expression);
            Matcher matcher = pattern.matcher(value);
            if (matcher.matches()) { return true; }
        }
        //Return true where value has not failed any previous test (i.e. is valid)
        return false;
    }

    //Private Functions
}