package org.togglz.core.repository.galileo;


import com.careem.galileo.sdk.Galileo;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.togglz.core.Feature;
import org.togglz.core.logging.Log;
import org.togglz.core.logging.LogFactory;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.util.DefaultMapSerializer;
import org.togglz.core.repository.util.MapSerializer;
import org.togglz.core.util.Strings;

import java.util.HashMap;
import java.util.Map;

public class GalileoStateRepository implements StateRepository {

    protected final Galileo galileo;
    protected final MapSerializer serializer;

    protected final Log log = LogFactory.getLog(GalileoStateRepository.class);

    protected final String disabledFeature = "{\"FEATURE_ENABLED\": 0}";

    public GalileoStateRepository(Galileo galileo) {
        this.galileo = galileo;
        this.serializer = DefaultMapSerializer.multiline();
    }

    @Override
    public FeatureState getFeatureState(Feature feature) {

        FeatureState disabledDefaultState = new FeatureState(feature, false);

        String inputVariable = "togglz/" + feature.name().toLowerCase();

        HashMap<String, Object> context = new HashMap<String, Object>() {
        };
        String value = this.galileo.getString(inputVariable, context, disabledFeature);

        if (value == null || value.isEmpty()) {

            log.warn("Could not fetch the value of " + inputVariable);
            return disabledDefaultState;
        }

        try {
            JsonObject jsonObject = JsonParser.parseString(value).getAsJsonObject();

            if (jsonObject.has("FEATURE_ENABLED")) {
                boolean enabled = jsonObject.get("FEATURE_ENABLED").getAsInt() > 0;

                FeatureState state = new FeatureState(feature, enabled);

                if (jsonObject.has("STRATEGY_ID") && !jsonObject.get("STRATEGY_ID").isJsonNull()) {
                    String strategyId = jsonObject.get("STRATEGY_ID").getAsString();
                    if (Strings.isNotBlank(strategyId)) {
                        state.setStrategyId(strategyId.trim());
                    }
                }

                if (jsonObject.has("STRATEGY_PARAMS") && !jsonObject.get("STRATEGY_PARAMS").isJsonNull()) {
                    String paramData = jsonObject.get("STRATEGY_PARAMS").getAsString();
                    if (Strings.isNotBlank(paramData)) {
                        Map<String, String> params = serializer.deserialize(paramData);
                        for (Map.Entry<String, String> param : params.entrySet()) {
                            state.setParameter(param.getKey(), param.getValue());
                        }
                    }

                    return state;
                }
            }
        } catch (JsonSyntaxException | UnsupportedOperationException e) {
            log.error("Cannot parse json data for feature: " + inputVariable, e);
            return disabledDefaultState;
        }

        log.warn("Cannot parse the response from variable: " + inputVariable);
        return disabledDefaultState;

    }

    @Override
    public void setFeatureState(FeatureState featureState) {
        log.warn("Set feature is not supported");
    }
}
