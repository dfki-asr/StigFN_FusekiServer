package de.dfki.asr.stigFN;

import java.util.HashMap;
import lombok.SneakyThrows;
import org.apache.jena.sparql.expr.NodeValue;
import java.util.Map;
import org.apache.jena.sparql.function.FunctionBase5;

public class diffusion_distance extends FunctionBase5 {

    private final Map<String, Concentration> concentrations = new HashMap<>();

    @SneakyThrows
    @Override
    public NodeValue exec(NodeValue sourceId, NodeValue distance, NodeValue duration, NodeValue concentration, NodeValue rate) {

	if (!concentrations.containsKey(sourceId.asString())) {
	    concentrations.put(sourceId.asString(), new Concentration());
	}
	Concentration conc = concentrations.get(sourceId.asString());

	double output = conc.getAtDistance(distance, duration, concentration, rate);
	return NodeValue.makeDouble(output);
    }

    private synchronized Concentration getAndAddIfNotExist(String sourceId) {
	if (!concentrations.containsKey(sourceId)) {
	    System.out.println("[diffusion_distance] Source Stigma with ID " + sourceId + " not yet present. Adding.");
	    concentrations.put(sourceId, new Concentration());
	}

	System.out.println("[diffusion_distance] Returning Source Stigma with ID " + sourceId);
	return concentrations.get(sourceId);
    }
}
