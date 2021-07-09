package talkdesk.mafalda.calls.model;

import java.util.Map;

public class CallStatistics {

    /**
     * total Inbound call duration
     */
    private Map<String, String> totalInboundCallDuration;

    /**
     * total Outbound call duration
     */
    private Map<String, String> totalOutboundCallDuration;

    /**
     * total number of calls
     */
    private int totalNumberOfCalls;

    /**
     * total calls made by caller number
     */

    private Map<String, Map<String, Long>> totalCallsByCallerNumber;

    /**
     * total calls made by callee number
     */
    private Map<String, Map<String, Long>> totalCallsByCalleeNumber;

    /**
     * total cost by outbound calls
     */
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
