package de.ingrid.mdek.upload.storage.validate.config;

import java.util.HashMap;
import java.util.Map;

import com.tngtech.configbuilder.annotation.typetransformer.TypeTransformer;

import de.ingrid.mdek.upload.storage.validate.Validator;
import de.ingrid.mdek.upload.storage.validate.ValidatorFactory;

/**
 * TypeTransformer class used to transform the validators configuration value to a list of validator instances.
 */
public class StringToValidatorTransformer extends TypeTransformer<String, Map<String, Validator>> {

    @Override
    public Map<String, Validator> transform(final String argument) {
        final Map<String, Validator> result = new HashMap<>();
        final ValidatorFactory factory = new ValidatorFactory(argument);
        for (final String name : factory.getValidatorNames()) {
            result.put(name, factory.getValidator(name));
        }
        return result;
    }
}
