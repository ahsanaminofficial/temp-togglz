package org.togglz.core.repository.galileo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import com.careem.galileo.sdk.Galileo;
import org.togglz.core.Feature;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.cache.CachingStateRepositoryTest;
import org.togglz.core.util.NamedFeature;

import java.util.HashMap;

import static org.junit.Assert.*;

public class GalileoStateRepositoryTest {
    private Galileo galileoMock;
    private StateRepository repository;

    @Before
    public void setup() {
        galileoMock = Mockito.mock(Galileo.class);
        HashMap<String, Object> context1 = new HashMap<String, Object>(){{put("name", "togglz/sol_bo_no_of_passenger_field");}};
        Mockito.when(galileoMock.getString("togglz/sol_bo_no_of_passenger_field",context1, "failed to fetch"))
                .thenReturn("{\"FEATURE_NAME\": \"SOL_BO_NO_OF_PASSENGER_FIELD\", \"FEATURE_ENABLED\": 1, \"STRATEGY_ID\": null, \"STRATEGY_PARAMS\": \"rate 100\"}");

        HashMap<String, Object> context2 = new HashMap<String, Object>(){{put("name", "togglz/cash_in_card_top_up_currency_limits");}};
        Mockito.when(galileoMock.getString("togglz/cash_in_card_top_up_currency_limits",context2, "failed to fetch"))
                .thenReturn("{\n" +
                        "    \"FEATURE_NAME\": \"CASH_IN_CARD_TOP_UP_CURRENCY_LIMITS\",\n" +
                        "    \"FEATURE_ENABLED\": 0,\n" +
                        "    \"STRATEGY_ID\": \"NA\",\n" +
                        "    \"STRATEGY_PARAMS\": \"{Params: { \\\"AED\\\": { \\\"max\\\": 100, \\\"min\\\": 2}}}\"\n" +
                        "}");
    }

    @Test
    public void testGalileoFeatureEnabled() throws InterruptedException { //Check if feature is enabled
        repository = new GalileoStateRepository(galileoMock);
        assertTrue(repository.getFeatureState(new NamedFeature("sol_bo_no_of_passenger_field")).isEnabled()); //Checks FEATURE_ENABLED in json
        Mockito.verify(this.galileoMock).getString("togglz/sol_bo_no_of_passenger_field",new HashMap<String, Object>(){{put("name", "togglz/sol_bo_no_of_passenger_field");}}, "failed to fetch");
        Mockito.verifyNoMoreInteractions(galileoMock);
    }

    @Test
    public void testGalileoFeatureDisabled() throws InterruptedException { //Check if feature is disabled
        repository = new GalileoStateRepository(this.galileoMock);
        assertFalse(repository.getFeatureState(new NamedFeature("cash_in_card_top_up_currency_limits")).isEnabled());
        Mockito.verify(this.galileoMock).getString("togglz/cash_in_card_top_up_currency_limits",new HashMap<String, Object>(){{put("name", "togglz/cash_in_card_top_up_currency_limits");}}, "failed to fetch");
        Mockito.verifyNoMoreInteractions(galileoMock);
    }

    @Test
    public void testStrategyIdNull() throws InterruptedException { //Check if strategy ID is null
        repository = new GalileoStateRepository(this.galileoMock);
        assertNull(repository.getFeatureState(new NamedFeature("sol_bo_no_of_passenger_field")).getStrategyId());
        Mockito.verify(this.galileoMock).getString("togglz/sol_bo_no_of_passenger_field",new HashMap<String, Object>(){{put("name", "togglz/sol_bo_no_of_passenger_field");}}, "failed to fetch");
        Mockito.verifyNoMoreInteractions(galileoMock);
    }

    @Test
    public void testStrategyIdNotNull() throws InterruptedException { //Strategy ID may be NA or some other value (not null)
        repository = new GalileoStateRepository(this.galileoMock);
        assertNotNull(repository.getFeatureState(new NamedFeature("cash_in_card_top_up_currency_limits")).getStrategyId());
        Mockito.verify(this.galileoMock).getString("togglz/cash_in_card_top_up_currency_limits",new HashMap<String, Object>(){{put("name", "togglz/cash_in_card_top_up_currency_limits");}}, "failed to fetch");
        Mockito.verifyNoMoreInteractions(galileoMock);
    }

}