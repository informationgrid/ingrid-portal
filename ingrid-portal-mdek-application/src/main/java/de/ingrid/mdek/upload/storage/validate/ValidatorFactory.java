package de.ingrid.mdek.upload.storage.validate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * ValidatorFactory is used to construct and provide Validator implementations
 */
public class ValidatorFactory {

    private final Map<String, Validator> validatorMap = new HashMap<>();

    static class ValidatorDef {
        public String impl;
        public Map<String, String> properties;
    }

    /**
     * Constructor.
     *
     * Initialize the factory with a JSON formatted configuration string of the form:
     *
     * {
     *   "validatorA":{
     *     "impl":"de.ingrid.mdek.upload.validate.ValidatorA",
     *     "properties":{
     *       "propA": "configuration property A"
     *     }
     *   },
     *   ...
     * }
     *
     * @param configuration
     */
    public ValidatorFactory(final String configuration) {
        validatorMap.clear();
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final TypeFactory typeFactory = mapper.getTypeFactory();
            final MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, ValidatorDef.class);
            final HashMap<String, ValidatorDef> encodedConfiguration = mapper.readValue(configuration, mapType);
            for (final Map.Entry<String, ValidatorDef> validatorsEntry : encodedConfiguration.entrySet()) {
                final String validatorName = validatorsEntry.getKey();
                final ValidatorDef validatorDef = validatorsEntry.getValue();
                final Validator instance = (Validator)Class.forName(validatorDef.impl).newInstance();
                instance.initialize(validatorDef.properties);
                validatorMap.put(validatorName, instance);
            }
        }
        catch (final Exception e) {
            throw new IllegalArgumentException("Could not parse configuration value '"+configuration+"' into validator list.", e);
        }
    }

    /**
     * Get all validator names
     * @return Set<String>
     */
    public Set<String> getValidatorNames() {
        return validatorMap.keySet();
    }

    /**
     * Get the validator with the given name
     * @param name
     * @return Validator
     */
    public Validator getValidator(final String name) {
        if (validatorMap.containsKey(name)) {
            return validatorMap.get(name);
        }
        throw new IllegalArgumentException("A validator with name '"+name+"' does not exist.");
    }
}
