package org.togglz.core.repository.galileo;

import com.careem.galileo.sdk.Galileo;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.togglz.core.Feature;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.util.NamedFeature;
import org.togglz.core.util.Strings;

import static org.junit.Assert.*;

public class GalileoStateRepositoryTest {
    private Galileo galileoMock;
    private StateRepository repository;


    @Before
    public void setup() {
        this.galileoMock = Mockito.mock(Galileo.class);
        this.repository = new GalileoStateRepository(galileoMock);


     }

    @Test
    public void testGalileoFeatureRetrieval() {



        // Check features
        Mockito.when(galileoMock.getString(Mockito.eq("togglz/sol_bo_no_of_passenger_field"), Mockito.anyObject(), Mockito.anyString()))
                .thenReturn("{\"FEATURE_NAME\": \"SOL_BO_NO_OF_PASSENGER_FIELD\", \"FEATURE_ENABLED\": 1, \"STRATEGY_ID\": null, \"STRATEGY_PARAMS\": \"rate 100\"}");
        Feature solBoNoOfPassengerField = new NamedFeature("SOL_BO_NO_OF_PASSENGER_FIELD");
        assertTrue(repository.getFeatureState(solBoNoOfPassengerField).isEnabled()); //Checks FEATURE_ENABLED in json
        assertNull(repository.getFeatureState(solBoNoOfPassengerField).getStrategyId());
        assertTrue(Strings.isEmpty(repository.getFeatureState(solBoNoOfPassengerField).getStrategyId()));
        assertEquals("Expected rate 100", "100",repository.getFeatureState(solBoNoOfPassengerField).getParameter("rate"));

        // Feature with params
        Mockito.when(galileoMock.getString(Mockito.eq("togglz/cash_in_card_top_up_currency_limits"), Mockito.anyObject(), Mockito.anyString()))
                .thenReturn("{\"FEATURE_NAME\":\"CASH_IN_CARD_TOP_UP_CURRENCY_LIMITS\",\"FEATURE_ENABLED\":1,\"STRATEGY_ID\":\"12\",\"STRATEGY_PARAMS\":\"PARAMS {\\\"AED\\\": {\\\"max\\\":5000,\\\"min\\\":1}}\"}");
        Feature cashInCardTopUpCurrencyLimits = new NamedFeature("CASH_IN_CARD_TOP_UP_CURRENCY_LIMITS");
        assertTrue(repository.getFeatureState(cashInCardTopUpCurrencyLimits).isEnabled());
        assertEquals("12", repository.getFeatureState(cashInCardTopUpCurrencyLimits).getStrategyId());
        assertEquals("expected strategy params equal", "{\"AED\": {\"max\":5000,\"min\":1}}", repository.getFeatureState(new NamedFeature("CASH_IN_CARD_TOP_UP_CURRENCY_LIMITS")).getParameter("PARAMS"));
        String jsonStr = repository.getFeatureState(new NamedFeature("CASH_IN_CARD_TOP_UP_CURRENCY_LIMITS")).getParameter("PARAMS");
        JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();
        assertEquals("json value is not as per expectation. Max should be 5000", 5000, jsonObject.get("AED").getAsJsonObject().get("max").getAsInt());

        // Malfunctioned JSON
        Mockito.when(galileoMock.getString(Mockito.eq("togglz/cash_in_card_top_up_currency_limits_error"), Mockito.anyObject(), Mockito.anyString()))
                .thenReturn("{ \"FEATURE_NAME\": \"CASH_IN_CARD_TOP_UP_CURRENCY_LIMITS_ERROR\", \"FEATURE_ENABLED\": 0, \"STRATEGY_ID\": \"NA\", \"STRATEGY_PARAMS\": \"Params: { \"AED\": { \"max\": 100, \"min\": 2}}\"}");

        Feature cashInCardTopUpCurrencyLimitsError = new NamedFeature("CASH_IN_CARD_TOP_UP_CURRENCY_LIMITS_ERROR");
        assertFalse(repository.getFeatureState(cashInCardTopUpCurrencyLimitsError).isEnabled()); //Disabled due to malfunctioned json
        assertTrue(Strings.isEmpty(repository.getFeatureState(cashInCardTopUpCurrencyLimitsError).getStrategyId()));

    }

    @Test
    public void testGalileoFeatureRetrievalWithException() {
        Mockito.when(galileoMock.getString(Mockito.eq("togglz/invalid_json"), Mockito.anyObject(), Mockito.anyString()))
                .thenReturn("{\"FEATURE_NAME\": \"INVALID_JSON\",}");

        Feature invalidJson = new NamedFeature("INVALID_JSON");
        assertFalse(repository.getFeatureState(invalidJson).isEnabled());


        Mockito.when(galileoMock.getString(Mockito.eq("togglz/invalid_feature_data"), Mockito.anyObject(), Mockito.anyString()))
                .thenReturn("{\"FEATURE_NAME\": \"INVALID_FEATURE_DATA\"}");

        Feature invalidFeatureData = new NamedFeature("INVALID_FEATURE_DATA");
        assertFalse(repository.getFeatureState(invalidFeatureData).isEnabled());

    }

}