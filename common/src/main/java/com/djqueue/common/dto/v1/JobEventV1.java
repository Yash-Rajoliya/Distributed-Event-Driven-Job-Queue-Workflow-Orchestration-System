@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobEventV1 {
    private String jobId;
    private String payload;
    private int retryCount;
    private long createdAt;
}