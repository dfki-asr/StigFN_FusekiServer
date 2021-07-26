package de.dfki.asr.stigFN;

import lombok.SneakyThrows;
import org.apache.jena.sparql.expr.NodeValue;
import java.util.Map;
import org.apache.jena.sparql.function.FunctionBase5;

public class diffusion_distance extends FunctionBase5 {

    private Map<String, Concentration> concentrations;

    @SneakyThrows
    @Override
    public NodeValue exec(NodeValue sourceId, NodeValue distance, NodeValue duration, NodeValue concentration, NodeValue rate) {

	Concentration conc = concentrations.getOrDefault(sourceId.asString(), new Concentration());

	double output = conc.getAtDistance(distance, duration, concentration, rate);
	return NodeValue.makeDouble(output);
    }
}
