package org.togglz.core.repository.galileo;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;

import org.togglz.core.Feature;
import org.togglz.core.logging.Log;
import org.togglz.core.logging.LogFactory;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;

import com.careem.galileo.sdk.Galileo;

import java.util.HashMap;
import org.togglz.core.util.Strings;

public class GalileoStateRepository implements StateRepository {

    protected final Galileo galileo;

    protected final Log log = LogFactory.getLog(GalileoStateRepository.class);

    public GalileoStateRepository(Galileo galileoObj) {
        this.galileo = galileoObj;
    }

    @Override
    public FeatureState getFeatureState(Feature feature) {

        String inputVariable = "togglz/" + feature.name().toLowerCase();

        HashMap<String, Object> context = new HashMap<String, Object>(){{put("name", inputVariable);}};
        String value = this.galileo.getString(inputVariable, context, "failed to fetch");

        if (value == "failed to fetch") {
            log.warn("Could not fetch the value of " + inputVariable);
            return null;
        }

        try {
            JSONParser parser = new JSONParser();
            Map<String, Object> jsonMap = (Map<String, Object>) parser.parse(value);


            if(jsonMap.containsKey("FEATURE_ENABLED")) {
                boolean enabled = (long) jsonMap.get("FEATURE_ENABLED") > 0;

                FeatureState state = new FeatureState(feature, enabled);

                if(jsonMap.containsKey("STRATEGY_ID")) {
                    String strategyId = (String) jsonMap.get("STRATEGY_ID");
                    if (Strings.isNotBlank(strategyId)) {
                        state.setStrategyId(strategyId.trim());
                    }
                }

                if(jsonMap.containsKey("STRATEGY_PARAMS")) {
                    String paramData = (String) jsonMap.get("STRATEGY_PARAMS");
                    if (Strings.isNotBlank(paramData)) {
                        try {
                            Map<String, Object> strategy_params = (Map<String, Object>) parser.parse(paramData);

                            for (Map.Entry<String, Object> entry : strategy_params.entrySet()) {
                                state.setParameter(entry.getKey(), (String) entry.getValue());
                            }
                        }
                        catch(ParseException e) {
                            log.warn("Cannot parse strategy params for variable: " + inputVariable);
                            e.printStackTrace();
                        }
                    }
                }

                return state;
            }

        }
        catch (ParseException e) {
            log.warn("Cannot parse strategy params for variable: " + inputVariable);
            e.printStackTrace();
        }

        log.warn("Cannot parse the return value of variable: " + inputVariable);
        return null;
    }

    @Override
    public void setFeatureState(FeatureState featureState) {}
}
