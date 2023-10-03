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
        HashMap<String, Object> context = new HashMap<String, Object>(){{put("name", "togglz/sol_bo_no_of_passenger_field");}};
        Mockito.when(galileoMock.getString("togglz/sol_bo_no_of_passenger_field",context, "failed to fetch"))
                .thenReturn("{\"FEATURE_NAME\": \"SOL_BO_NO_OF_PASSENGER_FIELD\", \"FEATURE_ENABLED\": 1, \"STRATEGY_ID\": null, \"STRATEGY_PARAMS\": \"rate 100\"}");

//        Mockito.when(galileoMock.getString("togglz/cash_in_card_top_up_currency_limits",new HashMap<>(), ""))
//                .thenReturn("{ \"FEATURE_NAME\": \"CASH_IN_CARD_TOP_UP_CURRENCY_LIMITS\", \"FEATURE_ENABLED\": 0, \"STRATEGY_ID\": \"NA\", \"STRATEGY_PARAMS\": \"Params: { \"AED\": { \"max\": 100, \"min\": 2}}\"}");
    }

    @Test
    public void testGalileoFeatureEnabled() throws InterruptedException { //Check if feature is enabled
        repository = new GalileoStateRepository(galileoMock);
        assertTrue(repository.getFeatureState(new NamedFeature("sol_bo_no_of_passenger_field")).isEnabled()); //Checks FEATURE_ENABLED in json
        Mockito.verify(this.galileoMock).getString("togglz/sol_bo_no_of_passenger_field",new HashMap<String, Object>(){{put("name", "togglz/sol_bo_no_of_passenger_field");}}, "failed to fetch");
        Mockito.verifyNoMoreInteractions(galileoMock);
    }

//    @Test
//    public void testGalileoFeatureDisabled() throws InterruptedException { //Check if feature is disabled
//        repository = new GalileoStateRepository(this.galileoMock);
//        assertFalse(repository.getFeatureState(GalileoStateRepositoryTest.DummyFeature.CASH_IN_CARD_TOP_UP_CURRENCY_LIMITS).isEnabled());
//        Mockito.verify(this.galileoMock).getString("togglz/cash_in_card_top_up_currency_limits",new HashMap<>(), "");
//        Mockito.verifyNoMoreInteractions(galileoMock);
//    }
//
//    @Test
//    public void testStrategyIdNull() throws InterruptedException { //Check if strategy ID is null
//        repository = new GalileoStateRepository(this.galileoMock);
//        assertNull(repository.getFeatureState(GalileoStateRepositoryTest.DummyFeature.SOL_BO_NO_OF_PASSENGER_FIELD).getStrategyId());
//        Mockito.verify(this.galileoMock).getString("togglz/sol_bo_no_of_passenger_field",new HashMap<>(), "");
//        Mockito.verifyNoMoreInteractions(galileoMock);
//    }
//
//    @Test
//    public void testStrategyIdNotNull() throws InterruptedException { //Strategy ID may be NA or some other value (not null)
//        repository = new GalileoStateRepository(this.galileoMock);
//        assertNotNull(repository.getFeatureState(DummyFeature.CASH_IN_CARD_TOP_UP_CURRENCY_LIMITS).getStrategyId());
//        Mockito.verify(this.galileoMock).getString("togglz/cash_in_card_top_up_currency_limits",new HashMap<>(), "");
//        Mockito.verifyNoMoreInteractions(galileoMock);
//    }


    private enum DummyFeature implements Feature {
        CASH_IN_CARD_TOP_UP_CURRENCY_LIMITS,
        SOL_BO_NO_OF_PASSENGER_FIELD
    }

}