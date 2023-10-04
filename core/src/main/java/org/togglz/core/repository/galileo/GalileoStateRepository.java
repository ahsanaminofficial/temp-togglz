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

import org.togglz.core.repository.util.DefaultMapSerializer;
import org.togglz.core.repository.util.MapSerializer;
import java.util.Map.Entry;
import java.util.Objects;

public class GalileoStateRepository implements StateRepository {

    protected final Galileo galileo;

    protected final Log log = LogFactory.getLog(GalileoStateRepository.class);

    protected final MapSerializer serializer;

    public GalileoStateRepository(Galileo galileoObj) {
        this.galileo = galileoObj;
        this.serializer = DefaultMapSerializer.multiline();
    }

    @Override
    public FeatureState getFeatureState(Feature feature) {

        String inputVariable = "togglz/" + feature.name().toLowerCase();

        HashMap<String, Object> context = new HashMap<String, Object>(){{put("name", inputVariable);}};
        String value = this.galileo.getString(inputVariable, context, "failed to fetch");

        if (Objects.equals(value, "failed to fetch")) {
            log.warn("Could not fetch the value of " + inputVariable);
            return null;
        }

        try {
            JSONParser parser = new JSONParser();
            Map<String, Object> jsonMap = (Map<String, Object>) parser.parse(value);

            if(jsonMap.containsKey("FEATURE_ENABLED")) {

                boolean enabled = (long)jsonMap.get("FEATURE_ENABLED") > 0;

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

                            Map<String, String> params = serializer.deserialize(paramData);

                            for (Entry<String, String> param : params.entrySet()) {
                                state.setParameter(param.getKey(), param.getValue());
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                        }

//                        try {
//                            System.out.println("------CHECKING PARAM DATA---------");
//
//                            System.out.println(paramData);
//                            System.out.println(paramData.getClass().getName());
//
//                            Map<String, String> strategy_params = (Map<String, String>) parser.parse(paramData);
//                            System.out.println("HERE ARE PARAMS");
//                            System.out.println(strategy_params);
//                            for (Map.Entry<String, String> entry : strategy_params.entrySet()) {
//                                state.setParameter(entry.getKey(), (String) entry.getValue());
//                            }
//                        }
//                        catch(ParseException e) {
//                            log.warn("Cannot parse strategy params for variable: " + inputVariable);
//                            e.printStackTrace();
//                        }
                    }
                }

                return state;
            }

        }
        catch (ParseException e) {
            log.warn("Cannot parse the return value of variable: " + inputVariable);
        }

        return null;
    }

    @Override
    public void setFeatureState(FeatureState featureState) {}
}
