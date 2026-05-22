@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobEventV2 {
    private String jobId;
    private String payload;
    private int retryCount;
    private String priority;
    private long createdAt;
}