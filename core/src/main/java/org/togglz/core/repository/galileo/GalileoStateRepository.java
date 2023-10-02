package org.togglz.core.repository.galileo;

import org.togglz.core.Feature;
import org.togglz.core.logging.Log;
import org.togglz.core.logging.LogFactory;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;

import com.careem.galileo.sdk.Galileo;
//import org.togglz.core.repository.jdbc.JDBCStateRepository;

import java.util.HashMap;

public class GalileoStateRepository implements StateRepository {

    protected final Galileo galileo;

    protected final String serviceName;

    protected final Log log = LogFactory.getLog(GalileoStateRepository.class);

    public GalileoStateRepository(String serviceName) {
        this.serviceName = serviceName.toLowerCase().replace("-", "_");
        System.out.println("Name of service before:");
        System.out.println(serviceName);
        System.out.println("Name of service after:");
        System.out.println(this.serviceName);


        this.galileo = Galileo.builder()
                .withService(this.serviceName)
                .build();

        System.out.println("-----------------created galielo state repo object----------\n\n\n\n");
    }

    @Override
    public FeatureState getFeatureState(Feature feature) {

        String inputVariable = "togglz/" + feature.name().toLowerCase();
        String value = this.galileo.getString(inputVariable, new HashMap<>(), "");

        System.out.println("\n\n---------------------get state called---------------\n\n");
        System.out.println(value);
        System.out.println("\n\n");


        return new FeatureState(new Feature() {
            @Override
            public String name() {
                return null;
            }
        });
//        return null;
    }

    @Override
    public void setFeatureState(FeatureState featureState) {}
}
