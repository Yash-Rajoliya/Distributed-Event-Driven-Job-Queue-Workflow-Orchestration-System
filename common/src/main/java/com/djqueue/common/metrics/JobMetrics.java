@Component
public class JobMetrics {

    private final Counter jobProcessed =
        Counter.builder("jobs.processed").register(Metrics.globalRegistry);

    public void incrementProcessed() {
        jobProcessed.increment();
    }
}