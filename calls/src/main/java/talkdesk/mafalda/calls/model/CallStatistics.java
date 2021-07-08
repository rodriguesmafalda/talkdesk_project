package talkdesk.mafalda.calls.model;

import java.util.Map;

public class CallStatistics {

    private Map<String, String> totalInboundCallDuration;
    private Map<String, String> totalOutboundCallDuration;
    private int totalNumberOfCalls;
    private Map<String, Map<String, Long>> totalCallsByCallerNumber;
    private Map<String, Map<String, Long>> totalCallsByCalleeNumber;
    private Map<String, Double> totalCostByOutbound;

    public Map<String, String> getTotalInboundCallDuration() {
        return totalInboundCallDuration;
    }

    public void setTotalInboundCallDuration(Map<String, String> totalInboundCallDuration) {
        this.totalInboundCallDuration = totalInboundCallDuration;
    }

    public Map<String, String> getTotalOutboundCallDuration() {
        return totalOutboundCallDuration;
    }

    public void setTotalOutboundCallDuration(Map<String, String> totalOutboundCallDuration) {
        this.totalOutboundCallDuration = totalOutboundCallDuration;
    }

    public long getTotalNumberOfCalls() {
        return totalNumberOfCalls;
    }

    public void setTotalNumberOfCalls(int totalNumberOfCalls) {
        this.totalNumberOfCalls = totalNumberOfCalls;
    }

    public Map<String, Map<String, Long>> getTotalCallsByCallerNumber() {
        return totalCallsByCallerNumber;
    }

    public void setTotalCallsByCallerNumber(Map<String, Map<String, Long>> totalCallsByCallerNumber) {
        this.totalCallsByCallerNumber = totalCallsByCallerNumber;
    }

    public Map<String, Map<String, Long>> getTotalCallsByCalleeNumber() {
        return totalCallsByCalleeNumber;
    }

    public void setTotalCallsByCalleeNumber(Map<String, Map<String, Long>> totalCallsByCalleeNumber) {
        this.totalCallsByCalleeNumber = totalCallsByCalleeNumber;
    }

    public Map<String, Double> getTotalCostByOutbound() {
        return totalCostByOutbound;
    }

    public void setTotalCostByOutbound(Map<String, Double> totalCostByOutbound) {
        this.totalCostByOutbound = totalCostByOutbound;
    }
}
