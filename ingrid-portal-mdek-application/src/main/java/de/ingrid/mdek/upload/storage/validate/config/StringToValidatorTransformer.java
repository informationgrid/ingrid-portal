package de.ingrid.mdek.upload.storage.validate.config;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.tngtech.configbuilder.annotation.typetransformer.TypeTransformer;

import de.ingrid.mdek.upload.storage.validate.Validator;

/**
 * TypeTransformer class used to transform the validators configuration value to a list of validator instances.
 */
public class StringToValidatorTransformer extends TypeTransformer<String, Map<String, Validator>> {

    static class ValidatorDef {
        public String impl;
        public Map<String, String> properties;
    }

    @Override
    public Map<String, Validator> transform(final String argument) {
        final Map<String, Validator> result = new HashMap<>();
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final TypeFactory typeFactory = mapper.getTypeFactory();
            final MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, ValidatorDef.class);
            final HashMap<String, ValidatorDef> config = mapper.readValue(argument, mapType);
            for (final Map.Entry<String, ValidatorDef> validatorsEntry : config.entrySet()) {
                final String validatorName = validatorsEntry.getKey();
                final ValidatorDef validatorDef = validatorsEntry.getValue();
                final Validator instance = (Validator)Class.forName(validatorDef.impl).newInstance();
                instance.initialize(validatorDef.properties);
                result.put(validatorName, instance);
            }
        }
        catch (final Exception e) {
            throw new IllegalArgumentException("Could not parse configuration value '"+argument+"' into validator list.", e);
        }
        return result;
    }

}
